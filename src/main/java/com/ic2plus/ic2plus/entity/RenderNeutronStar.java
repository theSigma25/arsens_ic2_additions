package com.ic2plus.ic2plus.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class RenderNeutronStar extends Render<NeutronStar> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("ic2plus", "textures/entity/neutron_star.png");

    public RenderNeutronStar(RenderManager manager) {
        super(manager);
    }
    @Override
    public boolean shouldRender(NeutronStar entity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void doRender(NeutronStar entity, double x, double y, double z, float yaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        float renderTicks = entity.ticksExisted +   partialTicks;
        float interpolatedYaw = (renderTicks * 0.1F) % 360.0F;
        float interpolatedPitch = (renderTicks * 0.1F) % 360.0F;
        float radius = entity.getRadius();

        GlStateManager.rotate(interpolatedYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(interpolatedPitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.disableLighting();
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);

        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                240F,
                240F
        );

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        bindTexture(TEXTURE);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.color(10F, 10F, 10F, 1F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        drawSphere(buffer, 5.0F*entity.getRadius(), 32, 32);
        tess.draw();

        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();

        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        GlStateManager.color(0.45F, 0.8F, 1F, 0.5F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        drawSphereNoTex(buffer, 6.0F*entity.getRadius(), 32, 32);
        tess.draw();


        renderBeams(entity, partialTicks, 32, 6.0F*entity.getRadius(), 20.0F*entity.getRadius(), 12.0F, 1F,false);
        renderBeams(entity, partialTicks, 6, 12.0F*entity.getRadius(), 120.0F*entity.getRadius(), 2.0F, 0.5F,true);


        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        GlStateManager.enableLighting();

        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    private void renderBeams(NeutronStar entity, float partialTicks, int rayCount, float rayWidth, float rayLength, float offsetAngle, float rotationSpeed, boolean directional) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        float ticks = entity.ticksExisted + partialTicks;
        Random rand = new Random(432L);

        GlStateManager.pushMatrix();

        if (directional) {
            for (int dir : new int[]{1, -1}) {
                for (int i = 0; i < rayCount; ++i) {
                    float randomYaw = rand.nextFloat() * 360.0F;
                    float randomPitch = (rand.nextFloat() - 0.5F) * offsetAngle * 2.0F;
                    float randomRoll = (rand.nextFloat() - 0.5F) * offsetAngle * 2.0F;
                    float timeRotation = ticks * rotationSpeed;

                    GlStateManager.pushMatrix();

                    if (dir == -1) {
                        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                    }

                    GlStateManager.rotate(randomYaw + timeRotation, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(randomPitch, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(randomRoll, 0.0F, 0.0F, 1.0F);

                    drawSingleBeam(tessellator, buffer, rayWidth, rayLength, rand, rotationSpeed == 0.0F);

                    GlStateManager.popMatrix();
                }
            }
        } else {
            for (int i = 0; i < rayCount; ++i) {
                GlStateManager.pushMatrix();


                GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(rand.nextFloat() * 360.0F + ticks * rotationSpeed, 0.0F, 0.0F, 1.0F);

                drawSingleBeam(tessellator, buffer, rayWidth, rayLength, rand, rotationSpeed == 0.0F);

                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
    }

    private void drawSingleBeam(Tessellator tessellator, BufferBuilder buffer, float rayWidth, float rayLength, Random rand, boolean isStatic) {
        float currentLength = isStatic ? rayLength : rayLength * (0.8F + rand.nextFloat() * 0.4F);
        float currentWidth = isStatic ? rayWidth : rayWidth * (0.8F + rand.nextFloat() * 0.4F);

        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);


        buffer.pos(0.0D, 0.0D, 0.0D).color(220, 240, 255, 255).endVertex();


        buffer.pos(-0.866D * currentWidth, (double) currentLength, -0.5D * currentWidth).color(0, 150, 255, 0).endVertex();
        buffer.pos(0.866D * currentWidth, (double) currentLength, -0.5D * currentWidth).color(0, 150, 255, 0).endVertex();
        buffer.pos(0.0D, (double) currentLength, 1.0D * currentWidth).color(0, 150, 255, 0).endVertex();
        buffer.pos(-0.866D * currentWidth, (double) currentLength, -0.5D * currentWidth).color(0, 150, 255, 0).endVertex();

        tessellator.draw();
    }

    private void drawSphere(BufferBuilder b, float radius, int rings, int sectors) {
        float pi = (float) Math.PI;

        for (int r = 0; r < rings; r++) {
            float lat0 = pi * (-0.5f + (float) r / rings);
            float z0  = (float) Math.sin(lat0);
            float zr0 = (float) Math.cos(lat0);

            float lat1 = pi * (-0.5f + (float) (r + 1) / rings);
            float z1  = (float) Math.sin(lat1);
            float zr1 = (float) Math.cos(lat1);

            float v0 = (float) r / rings;
            float v1 = (float) (r + 1) / rings;

            for (int s = 0; s < sectors; s++) {
                float lng0 = 2 * pi * (float) s / sectors;
                float x0 = (float) Math.cos(lng0);
                float y0 = (float) Math.sin(lng0);

                float lng1 = 2 * pi * (float) (s + 1) / sectors;
                float x1 = (float) Math.cos(lng1);
                float y1 = (float) Math.sin(lng1);

                float u0 = (float) s / sectors;
                float u1 = (float) (s + 1) / sectors;

                b.pos(radius * x0 * zr0, radius * z0, radius * y0 * zr0).tex(u0, v0).endVertex();
                b.pos(radius * x0 * zr1, radius * z1, radius * y0 * zr1).tex(u0, v1).endVertex();
                b.pos(radius * x1 * zr1, radius * z1, radius * y1 * zr1).tex(u1, v1).endVertex();
                b.pos(radius * x1 * zr0, radius * z0, radius * y1 * zr0).tex(u1, v0).endVertex();
            }
        }
    }

    private void drawSphereNoTex(BufferBuilder b, float radius, int rings, int sectors) {
        float pi = (float)Math.PI;

        for (int r = 0; r < rings; r++) {
            float lat0 = pi * (-0.5f + (float)r / rings);
            float z0 = (float)Math.sin(lat0);
            float zr0 = (float)Math.cos(lat0);

            float lat1 = pi * (-0.5f + (float)(r + 1) / rings);
            float z1 = (float)Math.sin(lat1);
            float zr1 = (float)Math.cos(lat1);

            for (int s = 0; s < sectors; s++) {
                float lng0 = 2 * pi * (float)s / sectors;
                float x0 = (float)Math.cos(lng0);
                float y0 = (float)Math.sin(lng0);

                float lng1 = 2 * pi * (float)(s + 1) / sectors;
                float x1 = (float)Math.cos(lng1);
                float y1 = (float)Math.sin(lng1);

                b.pos(radius * x0 * zr0, radius * z0, radius * y0 * zr0).endVertex();
                b.pos(radius * x0 * zr1, radius * z1, radius * y0 * zr1).endVertex();
                b.pos(radius * x1 * zr1, radius * z1, radius * y1 * zr1).endVertex();
                b.pos(radius * x1 * zr0, radius * z0, radius * y1 * zr0).endVertex();
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(NeutronStar entity) {
        return TEXTURE;
    }
}