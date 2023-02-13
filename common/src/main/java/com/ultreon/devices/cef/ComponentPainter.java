package com.ultreon.devices.cef;

import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

class ComponentPainter {

    public static BufferedImage paintComponent(Component c) {
        // Set it to it's preferred size. (optional)
        c.setSize(c.getPreferredSize());
        c.setVisible(true);
//        if (!c.isShowing()) {
//            throw new RuntimeException("Component to paint isn't visible, wtf happened?");
//        }
//        layoutComponent(c);


        BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);

//        CellRendererPane crp = new CellRendererPane();
//        crp.setVisible(true);
        Graphics2D graphics = img.createGraphics();
        c.paint(graphics);
//        graphics.dispose();
//        crp.add(c);
//        crp.paintComponent(img.createGraphics(), c, crp, c.getBounds());
        return img;
    }

    // from the example of user489041
    public static void layoutComponent(Component c) {
        synchronized (c.getTreeLock()) {
            c.doLayout();
            if (c instanceof Container)
                for (Component child : ((Container) c).getComponents())
                    layoutComponent(child);
        }
    }
}