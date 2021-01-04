package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GuardianRenderer extends MobRenderer<Guardian, GuardianModel> {
   private static final ResourceLocation GUARDIAN_LOCATION = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");

   public GuardianRenderer(EntityRenderDispatcher var1) {
      this(var1, 0.5F);
   }

   protected GuardianRenderer(EntityRenderDispatcher var1, float var2) {
      super(var1, new GuardianModel(), var2);
   }

   public boolean shouldRender(Guardian var1, Culler var2, double var3, double var5, double var7) {
      if (super.shouldRender((Mob)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.hasActiveAttackTarget()) {
            LivingEntity var9 = var1.getActiveAttackTarget();
            if (var9 != null) {
               Vec3 var10 = this.getPosition(var9, (double)var9.getBbHeight() * 0.5D, 1.0F);
               Vec3 var11 = this.getPosition(var1, (double)var1.getEyeHeight(), 1.0F);
               if (var2.isVisible(new AABB(var11.x, var11.y, var11.z, var10.x, var10.y, var10.z))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private Vec3 getPosition(LivingEntity var1, double var2, float var4) {
      double var5 = Mth.lerp((double)var4, var1.xOld, var1.x);
      double var7 = Mth.lerp((double)var4, var1.yOld, var1.y) + var2;
      double var9 = Mth.lerp((double)var4, var1.zOld, var1.z);
      return new Vec3(var5, var7, var9);
   }

   public void render(Guardian var1, double var2, double var4, double var6, float var8, float var9) {
      super.render((Mob)var1, var2, var4, var6, var8, var9);
      LivingEntity var10 = var1.getActiveAttackTarget();
      if (var10 != null) {
         float var11 = var1.getAttackAnimationScale(var9);
         Tesselator var12 = Tesselator.getInstance();
         BufferBuilder var13 = var12.getBuilder();
         this.bindTexture(GUARDIAN_BEAM_LOCATION);
         GlStateManager.texParameter(3553, 10242, 10497);
         GlStateManager.texParameter(3553, 10243, 10497);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         float var14 = 240.0F;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         float var15 = (float)var1.level.getGameTime() + var9;
         float var16 = var15 * 0.5F % 1.0F;
         float var17 = var1.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2, (float)var4 + var17, (float)var6);
         Vec3 var18 = this.getPosition(var10, (double)var10.getBbHeight() * 0.5D, var9);
         Vec3 var19 = this.getPosition(var1, (double)var17, var9);
         Vec3 var20 = var18.subtract(var19);
         double var21 = var20.length() + 1.0D;
         var20 = var20.normalize();
         float var23 = (float)Math.acos(var20.y);
         float var24 = (float)Math.atan2(var20.z, var20.x);
         GlStateManager.rotatef((1.5707964F - var24) * 57.295776F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(var23 * 57.295776F, 1.0F, 0.0F, 0.0F);
         boolean var25 = true;
         double var26 = (double)var15 * 0.05D * -1.5D;
         var13.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         float var28 = var11 * var11;
         int var29 = 64 + (int)(var28 * 191.0F);
         int var30 = 32 + (int)(var28 * 191.0F);
         int var31 = 128 - (int)(var28 * 64.0F);
         double var32 = 0.2D;
         double var34 = 0.282D;
         double var36 = 0.0D + Math.cos(var26 + 2.356194490192345D) * 0.282D;
         double var38 = 0.0D + Math.sin(var26 + 2.356194490192345D) * 0.282D;
         double var40 = 0.0D + Math.cos(var26 + 0.7853981633974483D) * 0.282D;
         double var42 = 0.0D + Math.sin(var26 + 0.7853981633974483D) * 0.282D;
         double var44 = 0.0D + Math.cos(var26 + 3.9269908169872414D) * 0.282D;
         double var46 = 0.0D + Math.sin(var26 + 3.9269908169872414D) * 0.282D;
         double var48 = 0.0D + Math.cos(var26 + 5.497787143782138D) * 0.282D;
         double var50 = 0.0D + Math.sin(var26 + 5.497787143782138D) * 0.282D;
         double var52 = 0.0D + Math.cos(var26 + 3.141592653589793D) * 0.2D;
         double var54 = 0.0D + Math.sin(var26 + 3.141592653589793D) * 0.2D;
         double var56 = 0.0D + Math.cos(var26 + 0.0D) * 0.2D;
         double var58 = 0.0D + Math.sin(var26 + 0.0D) * 0.2D;
         double var60 = 0.0D + Math.cos(var26 + 1.5707963267948966D) * 0.2D;
         double var62 = 0.0D + Math.sin(var26 + 1.5707963267948966D) * 0.2D;
         double var64 = 0.0D + Math.cos(var26 + 4.71238898038469D) * 0.2D;
         double var66 = 0.0D + Math.sin(var26 + 4.71238898038469D) * 0.2D;
         double var70 = 0.0D;
         double var72 = 0.4999D;
         double var74 = (double)(-1.0F + var16);
         double var76 = var21 * 2.5D + var74;
         var13.vertex(var52, var21, var54).uv(0.4999D, var76).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var52, 0.0D, var54).uv(0.4999D, var74).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var56, 0.0D, var58).uv(0.0D, var74).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var56, var21, var58).uv(0.0D, var76).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var60, var21, var62).uv(0.4999D, var76).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var60, 0.0D, var62).uv(0.4999D, var74).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var64, 0.0D, var66).uv(0.0D, var74).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var64, var21, var66).uv(0.0D, var76).color(var29, var30, var31, 255).endVertex();
         double var78 = 0.0D;
         if (var1.tickCount % 2 == 0) {
            var78 = 0.5D;
         }

         var13.vertex(var36, var21, var38).uv(0.5D, var78 + 0.5D).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var40, var21, var42).uv(1.0D, var78 + 0.5D).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var48, var21, var50).uv(1.0D, var78).color(var29, var30, var31, 255).endVertex();
         var13.vertex(var44, var21, var46).uv(0.5D, var78).color(var29, var30, var31, 255).endVertex();
         var12.end();
         GlStateManager.popMatrix();
      }

   }

   protected ResourceLocation getTextureLocation(Guardian var1) {
      return GUARDIAN_LOCATION;
   }
}
