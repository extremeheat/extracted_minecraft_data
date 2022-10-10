package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.ModelGuardian;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class RenderGuardian extends RenderLiving<EntityGuardian> {
   private static final ResourceLocation field_177114_e = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation field_177117_k = new ResourceLocation("textures/entity/guardian_beam.png");

   public RenderGuardian(RenderManager var1) {
      super(var1, new ModelGuardian(), 0.5F);
   }

   public boolean func_177071_a(EntityGuardian var1, ICamera var2, double var3, double var5, double var7) {
      if (super.func_177071_a((EntityLiving)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.func_175474_cn()) {
            EntityLivingBase var9 = var1.func_175466_co();
            if (var9 != null) {
               Vec3d var10 = this.func_177110_a(var9, (double)var9.field_70131_O * 0.5D, 1.0F);
               Vec3d var11 = this.func_177110_a(var1, (double)var1.func_70047_e(), 1.0F);
               if (var2.func_78546_a(new AxisAlignedBB(var11.field_72450_a, var11.field_72448_b, var11.field_72449_c, var10.field_72450_a, var10.field_72448_b, var10.field_72449_c))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private Vec3d func_177110_a(EntityLivingBase var1, double var2, float var4) {
      double var5 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var4;
      double var7 = var2 + var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var4;
      double var9 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var4;
      return new Vec3d(var5, var7, var9);
   }

   public void func_76986_a(EntityGuardian var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
      EntityLivingBase var10 = var1.func_175466_co();
      if (var10 != null) {
         float var11 = var1.func_175477_p(var9);
         Tessellator var12 = Tessellator.func_178181_a();
         BufferBuilder var13 = var12.func_178180_c();
         this.func_110776_a(field_177117_k);
         GlStateManager.func_187421_b(3553, 10242, 10497);
         GlStateManager.func_187421_b(3553, 10243, 10497);
         GlStateManager.func_179140_f();
         GlStateManager.func_179129_p();
         GlStateManager.func_179084_k();
         GlStateManager.func_179132_a(true);
         float var14 = 240.0F;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         float var15 = (float)var1.field_70170_p.func_82737_E() + var9;
         float var16 = var15 * 0.5F % 1.0F;
         float var17 = var1.func_70047_e();
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)var2, (float)var4 + var17, (float)var6);
         Vec3d var18 = this.func_177110_a(var10, (double)var10.field_70131_O * 0.5D, var9);
         Vec3d var19 = this.func_177110_a(var1, (double)var17, var9);
         Vec3d var20 = var18.func_178788_d(var19);
         double var21 = var20.func_72433_c() + 1.0D;
         var20 = var20.func_72432_b();
         float var23 = (float)Math.acos(var20.field_72448_b);
         float var24 = (float)Math.atan2(var20.field_72449_c, var20.field_72450_a);
         GlStateManager.func_179114_b((1.5707964F - var24) * 57.295776F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(var23 * 57.295776F, 1.0F, 0.0F, 0.0F);
         boolean var25 = true;
         double var26 = (double)var15 * 0.05D * -1.5D;
         var13.func_181668_a(7, DefaultVertexFormats.field_181709_i);
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
         var13.func_181662_b(var52, var21, var54).func_187315_a(0.4999D, var76).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var52, 0.0D, var54).func_187315_a(0.4999D, var74).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var56, 0.0D, var58).func_187315_a(0.0D, var74).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var56, var21, var58).func_187315_a(0.0D, var76).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var60, var21, var62).func_187315_a(0.4999D, var76).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var60, 0.0D, var62).func_187315_a(0.4999D, var74).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var64, 0.0D, var66).func_187315_a(0.0D, var74).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var64, var21, var66).func_187315_a(0.0D, var76).func_181669_b(var29, var30, var31, 255).func_181675_d();
         double var78 = 0.0D;
         if (var1.field_70173_aa % 2 == 0) {
            var78 = 0.5D;
         }

         var13.func_181662_b(var36, var21, var38).func_187315_a(0.5D, var78 + 0.5D).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var40, var21, var42).func_187315_a(1.0D, var78 + 0.5D).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var48, var21, var50).func_187315_a(1.0D, var78).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var13.func_181662_b(var44, var21, var46).func_187315_a(0.5D, var78).func_181669_b(var29, var30, var31, 255).func_181675_d();
         var12.func_78381_a();
         GlStateManager.func_179121_F();
      }

   }

   protected ResourceLocation func_110775_a(EntityGuardian var1) {
      return field_177114_e;
   }
}
