package com.ultreon.devices.object;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.tiles.Tile;
import com.ultreon.devices.util.KeyboardHelper;
import com.ultreon.devices.util.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class Player {
    private static final ResourceLocation boatTextures = new ResourceLocation("textures/entity/boat/oak.png");
    boolean canMove = false;
    private final Game game;
    private double posX, posY;
    private double posXPrev, posYPrev;
    private double speed;
    private int rotation, rotationPrev;
    private final Vec2d direction;
    private final Vec2d velocity;
    private final BoatRenderer boatModel;
    //private final ModelDummyPlayer playerModel;
    private BoatEntity boat;

    public Player(Game game) {
        this.game = game;
        this.direction = new Vec2d(0, 0);
        this.velocity = new Vec2d(0, 0);
        this.boatModel = new BoatRenderer(Minecraft.getInstance().getEntityRenderDispatcher());
		assert Minecraft.getInstance().player != null;
		boolean slim = Minecraft.getInstance().player.getModelName().equals("slim");
        if (Laptop.isWorldLess()) {
            boat = new BoatEntity(Objects.requireNonNull(Minecraft.getInstance().level), 0, 0, 0);
        }
//        this.playerModel = new ModelDummyPlayer(0f, slim);
//        this.playerModel.isRiding = true;
//        this.playerModel.isChild = false;
    }

    public void tick() {
        rotationPrev = rotation;
        posXPrev = posX;
        posYPrev = posY;

        if (KeyboardHelper.isKeyDown(GLFW.GLFW_KEY_UP)) {
            speed += 0.5;
            if (speed >= 3) {
                speed = 3;
            }
            if (KeyboardHelper.isKeyDown(42) || KeyboardHelper.isKeyDown(54)) {
                speed += 2;
            }
        } else {
            speed /= 1.1;
        }
        if (KeyboardHelper.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
            rotation -= 8;
        }
        if (KeyboardHelper.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
            rotation += 8;
        }

        Tile tile = game.getTile(Game.Layer.BACKGROUND, getPosX(), getPosY());
        if (tile != null && tile.isSlow()) {
            speed *= 0.1;
        }

        direction.x = Math.cos(Math.toRadians(rotation));
        direction.y = Math.sin(Math.toRadians(rotation));
        direction.normalise();

        velocity.x = direction.x * speed;
        velocity.y = direction.y * speed;

        canMove = canMove();
        if (canMove) {
            this.posX += velocity.x;
            this.posY += velocity.y;
        } else {
            speed = 0;
        }
    }

    public boolean canMove() {
        if (posX + velocity.x <= 0) return false;
        if (posY + velocity.y <= 0) return false;
        if (posX + velocity.x >= game.mapWidth * Tile.WIDTH) return false;
		return !(posY + velocity.y >= game.mapHeight * Tile.HEIGHT);
	}

    public int getPosX() {
        return (int) (posX / Tile.WIDTH);
    }

    public int getPosY() {
        return (int) (posY / Tile.HEIGHT);
    }

    public void render(MatrixStack pose, int x, int y, float partialTicks) {
        float scale = 0.5f;
        double px = x + posXPrev + (posX - posXPrev) * partialTicks;
        double py = y + posYPrev + (posY - posYPrev) * partialTicks;
        float rot = rotationPrev + (rotation - rotationPrev) * partialTicks;

        pose.pushPose();
        pose.translate((float) px, (float) py, 3f);
        pose.scale(-scale, -scale, -scale);
        pose.mulPose(new Quaternion(180f, 0f, 0f, 1f)); //Flips boat up
        pose.mulPose(new Quaternion(90, 1, 0, 0));
        pose.translate(0f, -3d, 0f);
        pose.mulPose(new Quaternion(-90, 1f, 0f, 0f));
        pose.mulPose(new Quaternion(rot, 0f, 1f, 0f));
        Minecraft.getInstance().textureManager.bind(boatTextures);
        Minecraft.getInstance().getEntityRenderDispatcher().render(this.boat, 0, 0, 0, 0f, partialTicks, pose, IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder()), 1);
        boatModel.render(boat, 0f, 0f, pose, Minecraft.getInstance().renderBuffers().bufferSource(), 1);
        pose.popPose();

        pose.pushPose();
        pose.translate((float) px, (float) py, 3f);
        pose.scale(-scale, scale, scale);
        // //Flips boat up
        pose.mulPose(new Quaternion(90, 1, 0, 0));
        pose.translate(0f, 5f, 0f);
        pose.mulPose(new Quaternion(90, 1f, 0f, 0f));
        pose.mulPose(new Quaternion(180f, 0f, 0f, 1f));
        pose.mulPose(new Quaternion(rot - 90, 0f, 1f, 0f));
        pose.translate(0f, -12f, 5f);
//        Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().player.getLocationSkin());
        //playerModel.render(null, 0f, 0f, 0f, 0f, 0f, 1f);
        pose.popPose();
    }

//    public static class ModelDummyPlayer extends PlayerModel<net.minecraft.entity.player.Player> {
//        public ModelPart bipedLeftArmwear;
//        public ModelPart bipedRightArmwear;
//        public ModelPart bipedLeftLegwear;
//        public ModelPart bipedRightLegwear;
//        public ModelPart bipedBodyWear;
//        private final ModelPart bipedCape;
//        private final ModelPart bipedDeadmau5Head;
//        private final boolean smallArms;
//
//        public ModelDummyPlayer(float scale, boolean slim) {
//            super(scale, slim);
//            this.smallArms = slim;
//            this.bipedDeadmau5Head = new ModelPart(this, 24, 0);
//            this.bipedDeadmau5Head.addBox(-3f, -6f, -1f, 6, 6, 1, scale);
//            this.bipedCape = new ModelPart(this, 0, 0);
//            this.bipedCape.setTextureSize(64, 32);
//            this.bipedCape.addBox(-5f, 0f, -1f, 10, 16, 1, scale);
//
//            if (slim) {
//                this.bipedLeftArm = new ModelPart(this, 32, 48);
//                this.bipedLeftArm.addBox(-1f, -2f, -2f, 3, 12, 4, scale);
//                this.bipedLeftArm.setRotationPoint(5f, 2.5f, 0f);
//                this.bipedRightArm = new ModelPart(this, 40, 16);
//                this.bipedRightArm.addBox(-2f, -2f, -2f, 3, 12, 4, scale);
//                this.bipedRightArm.setRotationPoint(-5f, 2.5f, 0f);
//                this.bipedLeftArmwear = new ModelPart(this, 48, 48);
//                this.bipedLeftArmwear.addBox(-1f, -2f, -2f, 3, 12, 4, scale + 0.25f);
//                this.bipedLeftArmwear.setRotationPoint(5f, 2.5f, 0f);
//                this.bipedRightArmwear = new ModelPart(this, 40, 32);
//                this.bipedRightArmwear.addBox(-2f, -2f, -2f, 3, 12, 4, scale + 0.25f);
//                this.bipedRightArmwear.setRotationPoint(-5f, 2.5f, 10f);
//            } else {
//                this.bipedLeftArm = new ModelPart(this, 32, 48);
//                this.bipedLeftArm.addBox(-1f, -2f, -2f, 4, 12, 4, scale);
//                this.bipedLeftArm.setRotationPoint(5f, 2f, 0f);
//                this.bipedLeftArmwear = new ModelPart(this, 48, 48);
//                this.bipedLeftArmwear.addBox(-1f, -2f, -2f, 4, 12, 4, scale + 0.25f);
//                this.bipedLeftArmwear.setRotationPoint(5f, 2f, 0f);
//                this.bipedRightArmwear = new ModelPart(this, 40, 32);
//                this.bipedRightArmwear.addBox(-3f, -2f, -2f, 4, 12, 4, scale + 0.25f);
//                this.bipedRightArmwear.setRotationPoint(-5f, 2f, 10f);
//            }
//
//            this.bipedLeftLeg = new ModelPart(this, 16, 48);
//            this.bipedLeftLeg.addBox(-2f, 0f, -2f, 4, 12, 4, scale);
//            this.bipedLeftLeg.setRotationPoint(1.9f, 12f, 0f);
//            this.bipedLeftLegwear = new ModelPart(this, 0, 48);
//            this.bipedLeftLegwear.addBox(-2f, 0f, -2f, 4, 12, 4, scale + 0.25f);
//            this.bipedLeftLegwear.setRotationPoint(1.9f, 12f, 0f);
//            this.bipedRightLegwear = new ModelPart(this, 0, 32);
//            this.bipedRightLegwear.addBox(-2f, 0f, -2f, 4, 12, 4, scale + 0.25f);
//            this.bipedRightLegwear.setRotationPoint(-1.9f, 12f, 0f);
//            this.bipedBodyWear = new ModelPart(this, 16, 32);
//            this.bipedBodyWear.addBox(-4f, 0f, -2f, 8, 12, 4, scale + 0.25f);
//            this.bipedBodyWear.setRotationPoint(0f, 0f, 0f);
//        }
//
//        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
//            this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
//            GlStateManager.pushMatrix();
//
//            this.bipedHead.render(scale);
//            this.bipedBody.render(scale);
//            this.bipedRightArm.render(scale);
//            this.bipedLeftArm.render(scale);
//            this.bipedRightLeg.render(scale);
//            this.bipedLeftLeg.render(scale);
//            this.bipedHeadwear.render(scale);
//            this.bipedLeftLegwear.render(scale);
//            this.bipedRightLegwear.render(scale);
//            this.bipedLeftArmwear.render(scale);
//            this.bipedRightArmwear.render(scale);
//            this.bipedBodyWear.render(scale);
//
//            GlStateManager.popMatrix();
//        }
//
//        public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
//            super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
//            copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
//            copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
//            copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
//            copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
//            copyModelAngles(this.bipedBody, this.bipedBodyWear);
//            this.bipedCape.rotationPointY = 0f;
//        }
//    }
}
