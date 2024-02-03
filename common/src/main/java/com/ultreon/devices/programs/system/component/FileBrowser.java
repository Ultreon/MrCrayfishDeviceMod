package com.ultreon.devices.programs.system.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.*;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.app.listener.ItemClickListener;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.driver.DiskDriver;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.io.Folder;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.Window;
import com.ultreon.devices.core.Wrappable;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.task.TaskGetFiles;
import com.ultreon.devices.core.io.task.TaskGetStructure;
import com.ultreon.devices.core.io.task.TaskSetupFileBrowser;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.SystemApp;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.System;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Casey on 20-Jun-17.
 */
@SuppressWarnings("FieldCanBeLocal")
public class FileBrowser extends Component {
    private static final ResourceLocation ASSETS = new ResourceLocation("devices:textures/gui/file_browser.png");

    private static final Color HEADER_BACKGROUND = Color.decode("0x535861");
    private static final Color ITEM_BACKGROUND = Color.decode("0x9E9E9E");
    private static final Color ITEM_SELECTED = Color.decode("0x757575");
    private static final Color PROTECTED_FILE = new Color(155, 237, 242);

    private static final ListItemRenderer<File> ITEM_RENDERER = new ListItemRenderer<>(18) {
        @Override
        public void render(GuiGraphics graphics, File file, Minecraft mc, int x, int y, int width, int height, boolean selected) {
            Color bgColor = new Color(Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
            graphics.fill(x, y, x + width, y + height, selected ? bgColor.brighter().brighter().getRGB() : bgColor.brighter().getRGB());

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, ASSETS);
            if (file.isFolder()) {
                RenderUtil.drawRectWithTexture(ASSETS, graphics, x + 3, y + 2, 0, 0, 14, 14, 14, 14);
            } else {
                assert file.getOpeningApp() != null;
                AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(file.getOpeningApp()));
                RenderUtil.drawApplicationIcon(graphics, info, x + 3, y + 2);
            }
            graphics.drawString(Minecraft.getInstance().font, file.getName(), x + 22, y + 5, file.isProtected() ? PROTECTED_FILE.getRGB() : Laptop.getSystem().getSettings().getColorScheme().getTextColor());
        }
    };

    public static boolean refreshList = false;

    private final Wrappable wrappable;
    private final Mode mode;

    private Layout layoutMain;
    private ItemList<File> fileList;
    private Button btnPreviousFolder;
    private Button btnNewFolder;
    private Button btnRename;
    private Button btnCopy;
    private Button btnCut;
    private Button btnPaste;
    private Button btnDelete;

    private ComboBox.List<Drive> comboBoxDrive;
    private Label labelPath;

    private Layout layoutLoading;
    private Spinner spinnerLoading;

    private final Stack<Folder> predecessor = new Stack<>();
    private Drive currentDrive;
    private Folder currentFolder;

    private Drive clipboardDrive;
    private Folder clipboardDir;
    private File clipboardFile;

    private String initialFolder = FileSystem.DIR_ROOT;
    private boolean loadedStructure = false;

    private long lastClick = 0;

    private ItemClickListener<File> itemClickListener;

    private Predicate<File> filter;
    private final List<ContextMenuItem<File>> contextMenuItems;

    /**
     * The default constructor for a component. For your component to
     * be laid out correctly, make sure you use the x and y parameters
     * from {@link Wrappable#init(CompoundTag)} and pass them into the
     * x and y arguments of this constructor.
     * <p>
     * Laying out the components is a simple relative positioning. So for left (x position),
     * specific how many pixels from the left of the application window you want
     * it to be positioned at. The top is the same, but obviously from the top (y position).
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public FileBrowser(int left, int top, Wrappable wrappable, Mode mode) {
        super(left, top);
        this.wrappable = wrappable;
        this.mode = mode;
        contextMenuItems = List.of(
                new ContextMenuItem<>(
                        net.minecraft.network.chat.Component.translatable("app.devices.file_browser.open"),
                        (file) -> {
                            if (file instanceof Folder) {
                                handleFolderOpen((Folder) file);
                            } else if (wrappable instanceof SystemApp systemApp) {
                                handleFileOpen(file, systemApp);
                            }
                        }
                ),
                new ContextMenuItem<>(
                        net.minecraft.network.chat.Component.translatable("app.devices.file_browser.properties"),
                        (file) -> {
                            if (file instanceof Folder) {
                                handleFolderProperties((Folder) file);
                            } else {
                                handleFileProperties(file);
                            }
                        }
                )
        );
    }

    @Override
    public void init(Layout layout) {
        layoutMain = new Layout(mode.getWidth(), mode.getHeight());
        layoutMain.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
            graphics.fill(x, y, x + width, y + 20, color.getRGB());
            graphics.fill(x, y + 20, x + width, y + 21, color.darker().getRGB());
        });

        btnPreviousFolder = new Button(5, 2, Icons.ARROW_LEFT);
        btnPreviousFolder.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                goToPreviousFolder();
            }
        });
        btnPreviousFolder.setToolTip("Previous Folder", "Go back to the previous folder");
        btnPreviousFolder.setEnabled(false);
        layoutMain.addComponent(btnPreviousFolder);

        int btnIndex = 0;

        btnNewFolder = new Button(5, 25, Icons.NEW_FOLDER);
        btnNewFolder.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                createFolder();
            }
        });
        btnNewFolder.setToolTip("New Folder", "Creates a new folder in this directory");
        layoutMain.addComponent(btnNewFolder);

        btnIndex++;

        btnRename = new Button(5, 25 + btnIndex * 20, Icons.RENAME);
        btnRename.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                renameSelectedFile();
            }
        });
        btnRename.setToolTip("Rename", "Change the name of the selected file or folder");
        btnRename.setEnabled(false);
        layoutMain.addComponent(btnRename);

        if (mode == Mode.FULL) {
            btnIndex++;

            btnCopy = new Button(5, 25 + btnIndex * 20, Icons.COPY);
            btnCopy.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    setClipboardFileToSelected();
                }
            });
            btnCopy.setToolTip("Copy", "Copies the selected file or folder");
            btnCopy.setEnabled(false);
            layoutMain.addComponent(btnCopy);

            btnIndex++;

            btnCut = new Button(5, 25 + btnIndex * 20, Icons.CUT);
            btnCut.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    cutSelectedFile();
                }
            });
            btnCut.setToolTip("Cut", "Cuts the selected file or folder");
            btnCut.setEnabled(false);
            layoutMain.addComponent(btnCut);

            btnIndex++;

            btnPaste = new Button(5, 25 + btnIndex * 20, Icons.CLIPBOARD);
            btnPaste.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    pasteClipboardFile();
                }
            });
            btnPaste.setToolTip("Paste", "Pastes the copied file into this directory");
            btnPaste.setEnabled(false);
            layoutMain.addComponent(btnPaste);
        }

        btnIndex++;

        btnDelete = new Button(5, 25 + btnIndex * 20, Icons.TRASH);
        btnDelete.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                deleteSelectedFile();
            }
        });
        btnDelete.setToolTip("Delete", "Deletes the selected file or folder");
        btnDelete.setEnabled(false);
        layoutMain.addComponent(btnDelete);

        fileList = new ItemList<>(mode.getOffset(), 25, 180, mode.getVisibleItems());
        fileList.setListItemRenderer(ITEM_RENDERER);
        fileList.sortBy(File.SORT_BY_NAME);
        fileList.setItemClickListener((file, index, mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                this.handleFileListClick(file);
            } else if (mouseButton == 1) {
                Laptop.getSystem().openContext(createFileMenu(mouseX, mouseY, file), mouseX - 100, mouseY - 100);
            }
            if (itemClickListener != null) {
                itemClickListener.onClick(file, index, mouseX, mouseY, mouseButton);
            }
        });
        layoutMain.addComponent(fileList);

        comboBoxDrive = new ComboBox.List<>(26, 3, 44, 100, new Drive[]{});
        comboBoxDrive.setChangeListener((oldValue, newValue) -> openDrive(newValue));
        comboBoxDrive.setListItemRenderer(new ListItemRenderer<>(12) {
            @Override
            public void render(GuiGraphics graphics, Drive drive, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                Color bgColor = new Color(getColorScheme().getBackgroundColor());
                graphics.fill(x, y, x + width, y + height, selected ? bgColor.brighter().brighter().getRGB() : bgColor.brighter().getRGB());
                RenderSystem.setShaderTexture(0, ASSETS);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderUtil.drawRectWithTexture(ASSETS, graphics, x + 2, y + 2, drive.getType().ordinal() * 8, 30, 8, 8, 8, 8);

                String text = drive.getName();
                if (mc.font.width(text) > 87) {
                    text = mc.font.plainSubstrByWidth(drive.getName(), 78) + "...";
                }
                graphics.drawString(mc.font, text, x + 13, y + 2, Color.WHITE.getRGB());
            }
        });
        layoutMain.addComponent(comboBoxDrive);

        labelPath = new Label("/", 72, 6);
        layoutMain.addComponent(labelPath);
        layout.addComponent(layoutMain);

        layoutLoading = new Layout(mode.getOffset(), 25, fileList.getWidth(), fileList.getHeight());
        layoutLoading.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Window.Color_WINDOW_DARK));
        layoutLoading.setVisible(false);

        spinnerLoading = new Spinner((layoutLoading.width - 12) / 2, (layoutLoading.height - 12) / 2);
        layoutLoading.addComponent(spinnerLoading);
        layout.addComponent(layoutLoading);
    }

    private void handleFileListClick(File file) {
        btnRename.setEnabled(true);
        btnDelete.setEnabled(true);
        if (mode == Mode.FULL) {
            btnCopy.setEnabled(true);
            btnCut.setEnabled(true);
        }
        if (System.currentTimeMillis() - this.lastClick <= 200) {
            if (file.isFolder()) {
                handleFolderOpen((Folder) file);
            } else if (mode == Mode.FULL && wrappable instanceof SystemApp systemApp) {
                handleFileOpen(file, systemApp);
            }
        } else {
            this.lastClick = System.currentTimeMillis();
        }
    }

    private void handleFileOpen(File file, SystemApp systemApp) {
        Laptop laptop = systemApp.getLaptop();
        if (laptop != null) {
            //TODO change to check if application is installed
            Application targetApp = laptop.getApplication(file.getOpeningApp());
            if (targetApp != null) {
                startUpAppWithFile(file, systemApp, laptop, targetApp);
            } else {
                createErrorDialog("The application designed for this file does not exist.");
            }
        }
    }

    private void startUpAppWithFile(File file, SystemApp systemApp, Laptop laptop, Application targetApp) {
        if (laptop.isApplicationInstalled(targetApp.getInfo())) {
            if (!laptop.openApplication(targetApp.getInfo(), file).right()) {
                laptop.sendApplicationToFront(systemApp.getInfo());
                createErrorDialog(targetApp.getInfo().getName() + " was unable to open the file.");
            }
        } else {
            createErrorDialog("This file could not be open because the application '" + ChatFormatting.YELLOW + targetApp.getInfo().getName() + ChatFormatting.RESET + "' is not installed.");
        }
    }

    private void handleFolderOpen(Folder file) {
        fileList.setSelectedIndex(-1);
        openFolder(file, true, (folder, success) -> {
            if (mode == Mode.FULL) {
                btnRename.setEnabled(false);
                btnCopy.setEnabled(false);
                btnCut.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });
    }

    private void handleFolderProperties(Folder file) {
        Dialog dialog = new Dialog() {
            @Override
            public void init(@Nullable CompoundTag intent) {
                super.init(intent);

                Layout layout = new Layout(150, 150);
                layout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor())); // TODO Window Background
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Folder Properties", 5, 5));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label(file.getName(), 5, 20));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label(file.getPath(), 5, 35));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Creation Date: " + Laptop.getInstance().formatDateTime(file.getCreationTime()), 5, 50));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Last Modified: " + Laptop.getInstance().formatDateTime(file.getLastModified()), 5, 65));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Last Accessed: " + Laptop.getInstance().formatDateTime(file.getLastAccessed()), 5, 80));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Protected: " + (file.isProtected() ? "Yes" : "No"), 5, 95));

                Button close = new Button(45, 125, 50, 20, "Close");
                close.setClickListener((mouseX, mouseY, mouseButton) -> {
                    close();
                });

                layout.addComponent(close);

                this.setLayout(layout);
            }
        };

        wrappable.openDialog(dialog);
    }

    private void handleFileProperties(File file) {
        Dialog dialog = new Dialog() {
            @Override
            public void init(@Nullable CompoundTag intent) {
                super.init(intent);

                Layout layout = new Layout(150, 150);
                layout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor())); // TODO Window Background
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("File Properties", 5, 5));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label(file.getName(), 5, 20));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label(file.getPath(), 5, 35));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Creation Date: " + Laptop.getInstance().formatDateTime(file.getCreationTime()), 5, 50));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Last Modified: " + Laptop.getInstance().formatDateTime(file.getLastModified()), 5, 65));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Last Accessed: " + Laptop.getInstance().formatDateTime(file.getLastAccessed()), 5, 80));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Opening App: " + Objects.requireNonNullElse(file.getOpeningApp(), "N/A"), 5, 95));
                layout.addComponent(new com.ultreon.devices.api.app.component.Label("Protected: " + (file.isProtected() ? "Yes" : "No"), 5, 110));

                Button close = new Button(45, 125, 50, 20, "Close");
                close.setClickListener((mouseX, mouseY, mouseButton) -> {
                    close();
                });

                layout.addComponent(close);

                this.setLayout(layout);
            }
        };

        wrappable.openDialog(dialog);
    }

    private Layout createFileMenu(int x, int y, File file) {
        Layout layout = new Layout.Context(100, contextMenuItems.size() * 20 + 10);
        layout.xPosition = x;
        layout.yPosition = y;

        layout.setBackground((graphics, mc, bgX, bgY, width, height, mouseX, mouseY, windowActive) -> graphics.fill(bgX, bgY, bgX + width, bgY + height, Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor())); // TODO Window Background

        ItemList<ContextMenuItem<File>> list = new ItemList<>(0, 10, 100, contextMenuItems.size());
        contextMenuItems.forEach(list::addItem);
        list.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphics graphics, ContextMenuItem<File> contextMenuItem, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                contextMenuItem.render(graphics, contextMenuItem, mc, x, y, width, height, selected);
            }
        });
        list.setItemClickListener((contextMenuItem, index, mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Laptop.getSystem().closeContext();
                contextMenuItem.execute(file);
            }
        });
        layout.addComponent(list);

        return layout;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handleLoad() {
        if (!loadedStructure) {
            setLoading(true);
            Task task = new TaskSetupFileBrowser(Laptop.getPos(), Laptop.getMainDrive() == null);
            task.setCallback((tag, success) -> {
                DiskDriver diskDriver = Laptop.getInstance().getDriverManager().getBySubClass(DiskDriver.class).orElse(null);
                if (diskDriver == null) {
                    createErrorDialog("No available disk driver found.");
                    return;
                }

                if (success) {
                    if (Laptop.getMainDrive() == null) {
                        assert tag != null;
                        CompoundTag structureTag = tag.getCompound("structure");
                        Drive drive = new Drive(tag.getCompound("main_drive"));
                        drive.syncRoot(Folder.fromTag(FileSystem.LAPTOP_DRIVE_NAME, structureTag));
                        drive.getRoot().validate();
                        Laptop.setMainDrive(drive);
                    }

                    assert tag != null;
                    ListTag driveList = tag.getList("available_drives", Tag.TAG_COMPOUND);

                    Drive[] drives = new Drive[driveList.size() + 1];
                    drives[0] = currentDrive = Laptop.getMainDrive();
                    for (int i = 0; i < driveList.size(); i++) {
                        CompoundTag driveTag = driveList.getCompound(i);
                        drives[i + 1] = new Drive(driveTag);
                    }
                    comboBoxDrive.setItems(drives);

                    Folder folder = currentDrive.getFolder(initialFolder);
                    if (folder != null) {
                        pushPredecessors(folder);
                        openFolder(folder, false, (folder1, success1) -> {
                            if (!success1) {
                                createErrorDialog("A critical error occurred while initializing.");
                            }
                        });
                        return;
                    } else {
                        openFolder(currentDrive.getRoot(), false, (folder12, success12) -> {
                            if (success) {
                                createErrorDialog("Unable to open directory '" + initialFolder + "'");
                            } else {
                                createErrorDialog("A critical error occurred while initializing.");
                            }
                        });
                    }
                } else {
                    createErrorDialog("A critical error occurred while initializing.");
                    return;
                }
                setLoading(false);
            });
            TaskManager.sendTask(task);
            loadedStructure = true;
        }
    }

    @Override
    public void handleTick() {
        if (refreshList) {
            fileList.removeAll();
            fileList.setItems(currentFolder.getFiles());
        }
    }

    public void openFolder(String directory) {
        this.initialFolder = directory;
    }

    private void openDrive(Drive drive) {
        predecessor.clear();
        if (drive.isSynced()) {
            openFolder(drive.getRoot(), false, (folder, success) -> {
                if (!success) {
                    createErrorDialog("Unable to open drive '" + drive.getName() + "'");
                }
            });
        } else {
            setLoading(true);
            TaskGetStructure task = new TaskGetStructure(drive, Laptop.getPos());
            task.setCallback((tag, success) -> {
                setLoading(false);
                if (success) {
                    assert tag != null;
                    Folder folder = Folder.fromTag(tag.getString("file_name"), tag.getCompound("structure"));
                    drive.syncRoot(folder);
                    openFolder(drive.getRoot(), false, (folder1, success1) -> {
                        if (!success1) {
                            createErrorDialog("Unable to open drive '" + drive.getName() + "'");
                        }
                    });
                } else {
                    createErrorDialog("Unable to retrieve drive structure for '" + drive.getName() + "'");
                }
            });
            TaskManager.sendTask(task);
        }
    }

    private void openFolder(Folder folder, boolean push, Callback<Folder> callback) {
        DebugLog.log("Opening Folder");
        if (!folder.isSynced()) {
            BlockPos pos = Laptop.getPos();
            DebugLog.log("Open Folder: " + pos);
            if (pos == null) {
                if (callback != null) {
                    callback.execute(null, false);
                }
                return;
            }

            setLoading(true);
            Task task = new TaskGetFiles(folder, pos); //TODO convert to file system
            task.setCallback((tag, success) -> {
                if (success) {
                    assert tag != null;
                    if (tag.contains("files", Tag.TAG_LIST)) {
                        ListTag files = tag.getList("files", Tag.TAG_COMPOUND);
                        folder.syncFiles(files);
                        setCurrentFolder(folder, push);
                    }
                }
                if (callback != null) {
                    callback.execute(folder, success);
                }
                setLoading(false);
            });
            TaskManager.sendTask(task);
        } else {
            setCurrentFolder(folder, push);
            if (callback != null) {
                callback.execute(folder, true);
            }
            setLoading(false);
        }
    }

    private void setCurrentFolder(Folder folder, boolean push) {
        if (push) {
            predecessor.push(currentFolder);
            btnPreviousFolder.setEnabled(true);
        }
        currentDrive = folder.getDrive();
        currentFolder = folder;
        fileList.removeAll();

        List<File> files = folder.getFiles();
        if (filter != null) {
            files = files.stream().filter(filter).collect(Collectors.toList());
        }
        fileList.setItems(files);

        updatePath();
    }

    private void pushPredecessors(Folder folder) {
        List<Folder> predecessors = new ArrayList<>();
        Folder temp = folder.getParent();
        while (temp != null) {
            predecessors.add(temp);
            temp = temp.getParent();
        }
        Collections.reverse(predecessors);
        predecessors.forEach(predecessor::push);
        if (!predecessor.isEmpty()) {
            btnPreviousFolder.setEnabled(true);
        }
    }

    private void createFolder() {
        Dialog.Input dialog = new Dialog.Input("Enter a name");
        dialog.setResponseHandler((success, v) -> {
            if (success) {
                addFile(new Folder(v));
            }
            return true;
        });
        dialog.setTitle("Create a Folder");
        dialog.setPositiveText("Create");
        wrappable.openDialog(dialog);
    }

    private void goToPreviousFolder() {
        if (!predecessor.isEmpty()) {
            setLoading(true);
            Folder folder = predecessor.pop();
            openFolder(folder, false, (folder2, success) -> {
                if (success) {
                    if (isRootFolder()) {
                        btnPreviousFolder.setEnabled(false);
                    }
                    updatePath();
                } else {
                    createErrorDialog("Unable to open previous folder");
                }
                setLoading(false);
            });
        }
    }

    public File getSelectedFile() {
        return fileList.getSelectedItem();
    }

    public void addFile(File file) {
        addFile(file, null);
    }

    public void addFile(File file, Callback<FileSystem.Response> callback) {
        setLoading(true);
        currentFolder.add(file, (response, success) -> {
            assert response != null;
            if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                fileList.addItem(file);
                FileBrowser.refreshList = true;
            }
            if (callback != null) {
                callback.execute(response, success);
            }
            setLoading(false);
        });
    }

    public void addFile(File file, boolean override, Callback<FileSystem.Response> callback) {
        setLoading(true);
        currentFolder.add(file, override, (response, success) -> {
            assert response != null;
            if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                fileList.addItem(file);
                FileBrowser.refreshList = true;
            }
            if (callback != null) {
                callback.execute(response, success);
            }
            setLoading(false);
        });
    }

    private void deleteSelectedFile() {
        File file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

            Dialog.Confirmation dialog = new Dialog.Confirmation();
            StringBuilder builder = new StringBuilder();
            builder.append("Are you sure you want to delete this ");
            if (file.isFolder()) {
                builder.append("folder");
            } else {
                builder.append("file");
            }
            builder.append(" '").append(file.getName()).append("'?");
            dialog.setMessageText(builder.toString());
            dialog.setTitle("Delete");
            dialog.setPositiveText("Yes");
            dialog.setPositiveListener((mouseX, mouseY, mouseButton) -> {
                removeFile(fileList.getSelectedIndex());
                btnRename.setEnabled(false);
                btnDelete.setEnabled(false);
                if (mode == Mode.FULL) {
                    btnCopy.setEnabled(false);
                    btnCut.setEnabled(false);
                }
            });
            wrappable.openDialog(dialog);
        }
    }

    private void removeFile(int index) {
        File file = fileList.getItem(index);
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            setLoading(true);
            currentFolder.delete(file, (response, success) -> {
                if (success) {
                    fileList.removeItem(index);
                    FileBrowser.refreshList = true;
                }
                setLoading(false);
            });
        }
    }

    public void removeFile(String name) {
        File file = currentFolder.getFile(name);
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            setLoading(true);
            currentFolder.delete(file, (o, success) -> {
                if (success) {
                    int index = fileList.getItems().indexOf(file);
                    fileList.removeItem(index);
                    FileBrowser.refreshList = true;
                }
                setLoading(false);
            });
        }
    }

    private void setClipboardFileToSelected() {
        File file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be copied.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            clipboardDir = null;
            clipboardFile = file;
            btnPaste.setEnabled(true);
        } else {
            Dialog.Message dialog = new Dialog.Message("The file/folder you are trying to copy does not exist.");
            wrappable.openDialog(dialog);
        }
    }

    private void cutSelectedFile() {
        File file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be cut.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            clipboardDrive = comboBoxDrive.getValue();
            clipboardDir = currentFolder;
            clipboardFile = file;
            btnPaste.setEnabled(true);
        } else {
            Dialog.Message dialog = new Dialog.Message("The file/folder you are trying to cut does not exist.");
            wrappable.openDialog(dialog);
        }
    }

    private void pasteClipboardFile() {
        if (clipboardFile != null) {
            if (canPasteHere()) {
                handleCopyCut(false);
            } else {
                Dialog.Message dialog = new Dialog.Message("Destination folder can't be a subfolder");
                wrappable.openDialog(dialog);
            }
        }
    }

    private void handleCopyCut(boolean override) {
        final Callback<FileSystem.Response> CALLBACK = (response, success) -> {
            assert response != null;
            if (response.getStatus() == FileSystem.Status.FILE_EXISTS) {
                Dialog.Confirmation dialog = new Dialog.Confirmation("A file with the same name already exists in this directory. Do you want to override it?");
                dialog.setPositiveText("Override");
                dialog.setPositiveListener((mouseX, mouseY, mouseButton) -> {
                    if (mouseButton == 0) {
                        handleCopyCut(true);
                    }
                });
                wrappable.openDialog(dialog);
            } else if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                resetClipboard();
            } else {
                createErrorDialog(response.getMessage());
            }
            setLoading(false);
        };

        setLoading(true);
        if (clipboardDir != null) {
            clipboardFile.moveTo(currentFolder, override, CALLBACK);
        } else {
            clipboardFile.copyTo(currentFolder, override, CALLBACK);
        }
    }

    private void resetClipboard() {
        if (clipboardDir != null) {
            clipboardDir.refresh();
            clipboardDir = null;
            clipboardFile = null;
            btnPaste.setEnabled(false);
        }
        currentFolder.refresh();
        openFolder(currentFolder, false, (folder, success) -> {
            if (mode == Mode.FULL) {
                btnRename.setEnabled(false);
                btnCopy.setEnabled(false);
                btnCut.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });
    }

    private boolean canPasteHere() {
        if (clipboardFile != null) {
            if (clipboardFile instanceof Folder) {
                return !predecessor.contains(clipboardFile) && currentFolder != clipboardFile;
            }
        }
        return true;
    }

    private boolean isRootFolder() {
        return predecessor.isEmpty();
    }

    private void updatePath() {
        String path = currentFolder.getPath();
        path = path.replace("/", ChatFormatting.GOLD + "/" + ChatFormatting.RESET);
        int width = Minecraft.getInstance().font.width(path);
        if (width > 144) {
            path = "..." + Minecraft.getInstance().font.plainSubstrByWidth(path, 144, true);
        }
        labelPath.setText(path);
    }

    public void setLoading(boolean loading) {
        layoutLoading.setVisible(loading);
        if (loading) {
            disableAllButtons();
        } else {
            updateButtons();
        }
    }

    private void updateButtons() {
        boolean hasSelectedFile = fileList.getSelectedIndex() != -1;
        btnNewFolder.setEnabled(true);
        btnRename.setEnabled(hasSelectedFile);
        btnDelete.setEnabled(hasSelectedFile);
        if (mode == Mode.FULL) {
            btnCopy.setEnabled(hasSelectedFile);
            btnCut.setEnabled(hasSelectedFile);
            btnPaste.setEnabled(clipboardFile != null);
        }
        btnPreviousFolder.setEnabled(!isRootFolder());
    }

    private void disableAllButtons() {
        btnPreviousFolder.setEnabled(false);
        btnNewFolder.setEnabled(false);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(false);
        if (mode == Mode.FULL) {
            btnCopy.setEnabled(false);
            btnCut.setEnabled(false);
            btnPaste.setEnabled(false);
        }
    }

    private void renameSelectedFile() {
        File file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be renamed.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

            Dialog.Input dialog = new Dialog.Input("Enter a name");
            dialog.setResponseHandler((success, s) -> {
                if (success) {
                    setLoading(true);
                    file.rename(s, (response, success1) -> {
                        assert response != null;
                        if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                            dialog.close();
                        } else {
                            createErrorDialog(response.getMessage());
                        }
                        setLoading(false);
                    });
                }
                return false;
            });
            dialog.setTitle("Rename " + (file instanceof Folder ? "Folder" : "File"));
            dialog.setInputText(file.getName());
            wrappable.openDialog(dialog);
        }
    }

    private void createErrorDialog(String message) {
        Dialog.Message dialog = new Dialog.Message(message);
        dialog.setTitle("Error");
        wrappable.openDialog(dialog);
    }

    public void setFilter(Predicate<File> filter) {
        this.filter = filter;
    }

    public void setItemClickListener(ItemClickListener<File> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public enum Mode {
        FULL(225, 145, 26, 6), BASIC(211, 105, 26, 4);

        private final int width;
        private final int height;
        private final int offset;
        private final int visibleItems;

        Mode(int width, int height, int offset, int visibleItems) {
            this.width = width;
            this.height = height;
            this.offset = offset;
            this.visibleItems = visibleItems;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getOffset() {
            return offset;
        }

        public int getVisibleItems() {
            return visibleItems;
        }
    }
}
