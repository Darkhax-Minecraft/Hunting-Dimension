package net.darkhax.huntingdim.dimension;

import net.darkhax.bookshelf.util.RenderUtils;
import net.darkhax.huntingdim.events.EventLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHuntingSky extends IRenderHandler {

    public static final RenderHuntingSky INSTANCE = new RenderHuntingSky();
    
    private ResourceLocation moonTextures;

    public RenderHuntingSky() {
        
        if (EventLoader.month == 10 && EventLoader.day >= 20 && EventLoader.day <= 31) {
            
            moonTextures = new ResourceLocation("huntingdim", "textures/environment/pumpkin_moon.png");
        }
        
        else {
            moonTextures = new ResourceLocation("textures/environment/moon_phases.png");
        }
    }
    
    @Override
    public void render (float partialTicks, WorldClient world, Minecraft mc) {

        // This is a hacky way of getting the pass, since Forge doesn't give it to us.
        final int pass = EntityRenderer.anaglyphField;
        final RenderGlobal render = mc.renderGlobal;

        GlStateManager.disableTexture2D();
        final Vec3d skyColor = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
        float skyRed = (float) skyColor.x;
        float skyGreen = (float) skyColor.y;
        float skyBlue = (float) skyColor.z;

        if (pass != 2) {
            final float f3 = (skyRed * 30.0F + skyGreen * 59.0F + skyBlue * 11.0F) / 100.0F;
            final float f4 = (skyRed * 30.0F + skyGreen * 70.0F) / 100.0F;
            final float f5 = (skyRed * 30.0F + skyBlue * 70.0F) / 100.0F;
            skyRed = f3;
            skyGreen = f4;
            skyBlue = f5;
        }

        GlStateManager.color(skyRed, skyGreen, skyBlue);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(skyRed, skyGreen, skyBlue);

        if (OpenGlHelper.useVbo()) {
            final VertexBuffer skyVBO = RenderUtils.getSkyVBO(render);
            skyVBO.bindBuffer();
            GlStateManager.glEnableClientState(32884);
            GlStateManager.glVertexPointer(3, 5126, 12, 0);
            skyVBO.drawArrays(7);
            skyVBO.unbindBuffer();
            GlStateManager.glDisableClientState(32884);
        }
        else {
            GlStateManager.callList(RenderUtils.getSkyList(render));
        }

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        final float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);

        if (afloat != null) {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float f6 = afloat[0];
            float f7 = afloat[1];
            float f8 = afloat[2];

            if (pass != 2) {
                final float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                final float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                final float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                f6 = f9;
                f7 = f10;
                f8 = f11;
            }

            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();

            for (int j2 = 0; j2 <= 16; ++j2) {
                final float f21 = j2 * ((float) Math.PI * 2F) / 16.0F;
                final float f12 = MathHelper.sin(f21);
                final float f13 = MathHelper.cos(f21);
                bufferbuilder.pos(f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final float f16 = 1.0F - world.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        final float f17 = 20.0F;
        mc.getTextureManager().bindTexture(moonTextures);
        final int k1 = world.getMoonPhase();
        final int i2 = k1 % 4;
        final int k2 = k1 / 4 % 2;
        final float f22 = (i2 + 0) / 4.0F;
        final float f23 = (k2 + 0) / 2.0F;
        final float f24 = (i2 + 1) / 4.0F;
        final float f14 = (k2 + 1) / 2.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-f17, -100.0D, f17).tex(f24, f14).endVertex();
        bufferbuilder.pos(f17, -100.0D, f17).tex(f22, f14).endVertex();
        bufferbuilder.pos(f17, -100.0D, -f17).tex(f22, f23).endVertex();
        bufferbuilder.pos(-f17, -100.0D, -f17).tex(f24, f23).endVertex();
        tessellator.draw();
        GlStateManager.disableTexture2D();
        final float f15 = world.getStarBrightness(partialTicks) * f16;

        if (f15 > 0.0F) {
            GlStateManager.color(f15, f15, f15, f15);

            if (OpenGlHelper.useVbo()) {
                final VertexBuffer starVBO = RenderUtils.getStarVBO(render);
                starVBO.bindBuffer();
                GlStateManager.glEnableClientState(32884);
                GlStateManager.glVertexPointer(3, 5126, 12, 0);
                starVBO.drawArrays(7);
                starVBO.unbindBuffer();
                GlStateManager.glDisableClientState(32884);
            }
            else {
                GlStateManager.callList(RenderUtils.getStarGLCallList(render));
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        final double d3 = mc.player.getPositionEyes(partialTicks).y - world.getHorizon();

        if (d3 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);

            if (OpenGlHelper.useVbo()) {
                final VertexBuffer sky2VBO = RenderUtils.getSky2VBO(render);
                sky2VBO.bindBuffer();
                GlStateManager.glEnableClientState(32884);
                GlStateManager.glVertexPointer(3, 5126, 12, 0);
                sky2VBO.drawArrays(7);
                sky2VBO.unbindBuffer();
                GlStateManager.glDisableClientState(32884);
            }
            else {
                GlStateManager.callList(RenderUtils.getSkyList2(render));
            }

            GlStateManager.popMatrix();
            final float f19 = -((float) (d3 + 65.0D));
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(-1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, f19, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, f19, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
        }

        if (world.provider.isSkyColored()) {
            GlStateManager.color(skyRed * 0.2F + 0.04F, skyGreen * 0.2F + 0.04F, skyBlue * 0.6F + 0.1F);
        }
        else {
            GlStateManager.color(skyRed, skyGreen, skyBlue);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float) (d3 - 16.0D)), 0.0F);
        GlStateManager.callList(RenderUtils.getSkyList2(render));
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
}