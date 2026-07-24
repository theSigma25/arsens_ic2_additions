package com.ic2plus.ic2plus.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderBeamAttack extends Render<BeamAttack> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            new ResourceLocation("ic2plus", "textures/entity/proton_beam.png");

    public RenderBeamAttack(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(BeamAttack entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityLivingBase shooter = entity.getShooter();
        if (shooter == null) return;

        bindTexture(getEntityTexture(entity));

        Vec3d start = new Vec3d(
                shooter.prevPosX + (shooter.posX - shooter.prevPosX) * partialTicks,
                shooter.prevPosY + (shooter.posY - shooter.prevPosY) * partialTicks + shooter.getEyeHeight(),
                shooter.prevPosZ + (shooter.posZ - shooter.prevPosZ) * partialTicks
        );

        Vec3d target = entity.getTarget();

        renderLaser(
                start.subtract(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ),
                target.subtract(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ)
        );
    }

    private void renderLaser(Vec3d start, Vec3d end) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);

        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        Vec3d dir = end.subtract(start).normalize();
        Vec3d up = Math.abs(dir.y) > 0.99 ? new Vec3d(1,0,0) : new Vec3d(0,1,0);
        Vec3d right = dir.crossProduct(up).normalize();

        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        drawPlane(b, start, end, right, 0.08);
        drawPlane(b, start, end, dir.crossProduct(right).normalize(), 0.08);

        Tessellator.getInstance().draw();

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void drawPlane(BufferBuilder b, Vec3d s, Vec3d e, Vec3d axis, double w) {
        Vec3d o = axis.scale(w);

        b.pos(s.x+o.x,s.y+o.y,s.z+o.z).tex(0,0).color(255,255,255,255).endVertex();
        b.pos(e.x+o.x,e.y+o.y,e.z+o.z).tex(0,1).color(255,255,255,255).endVertex();
        b.pos(e.x-o.x,e.y-o.y,e.z-o.z).tex(1,1).color(255,255,255,255).endVertex();
        b.pos(s.x-o.x,s.y-o.y,s.z-o.z).tex(1,0).color(255,255,255,255).endVertex();
    }

    @Override
    protected ResourceLocation getEntityTexture(BeamAttack entity) {
        String path = entity.getTexturePath();
        if (path == null || !path.contains(":")) return DEFAULT_TEXTURE;

        String[] s = path.split(":", 2);
        return new ResourceLocation(s[0], s[1]);
    }
}