package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderFish extends Render {
   private static final ResourceLocation field_110792_a = new ResourceLocation("textures/particle/particles.png");

   public RenderFish() {
      super();
   }

   public void func_76986_a(EntityFishHook var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      GL11.glEnable(32826);
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      this.func_110777_b(var1);
      Tessellator var10 = Tessellator.field_78398_a;
      byte var11 = 1;
      byte var12 = 2;
      float var13 = (float)(var11 * 8 + 0) / 128.0F;
      float var14 = (float)(var11 * 8 + 8) / 128.0F;
      float var15 = (float)(var12 * 8 + 0) / 128.0F;
      float var16 = (float)(var12 * 8 + 8) / 128.0F;
      float var17 = 1.0F;
      float var18 = 0.5F;
      float var19 = 0.5F;
      GL11.glRotatef(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      var10.func_78382_b();
      var10.func_78375_b(0.0F, 1.0F, 0.0F);
      var10.func_78374_a((double)(0.0F - var18), (double)(0.0F - var19), 0.0, (double)var13, (double)var16);
      var10.func_78374_a((double)(var17 - var18), (double)(0.0F - var19), 0.0, (double)var14, (double)var16);
      var10.func_78374_a((double)(var17 - var18), (double)(1.0F - var19), 0.0, (double)var14, (double)var15);
      var10.func_78374_a((double)(0.0F - var18), (double)(1.0F - var19), 0.0, (double)var13, (double)var15);
      var10.func_78381_a();
      GL11.glDisable(32826);
      GL11.glPopMatrix();
      if (var1.field_146042_b != null) {
         float var20 = var1.field_146042_b.func_70678_g(var9);
         float var21 = MathHelper.func_76126_a(MathHelper.func_76129_c(var20) * 3.1415927F);
         Vec3 var22 = Vec3.func_72443_a(-0.5, 0.03, 0.8);
         var22.func_72440_a(
            -(var1.field_146042_b.field_70127_C + (var1.field_146042_b.field_70125_A - var1.field_146042_b.field_70127_C) * var9) * 3.1415927F / 180.0F
         );
         var22.func_72442_b(
            -(var1.field_146042_b.field_70126_B + (var1.field_146042_b.field_70177_z - var1.field_146042_b.field_70126_B) * var9) * 3.1415927F / 180.0F
         );
         var22.func_72442_b(var21 * 0.5F);
         var22.func_72440_a(-var21 * 0.7F);
         double var23 = var1.field_146042_b.field_70169_q
            + (var1.field_146042_b.field_70165_t - var1.field_146042_b.field_70169_q) * (double)var9
            + var22.field_72450_a;
         double var25 = var1.field_146042_b.field_70167_r
            + (var1.field_146042_b.field_70163_u - var1.field_146042_b.field_70167_r) * (double)var9
            + var22.field_72448_b;
         double var27 = var1.field_146042_b.field_70166_s
            + (var1.field_146042_b.field_70161_v - var1.field_146042_b.field_70166_s) * (double)var9
            + var22.field_72449_c;
         double var29 = var1.field_146042_b == Minecraft.func_71410_x().field_71439_g ? 0.0 : (double)var1.field_146042_b.func_70047_e();
         if (this.field_76990_c.field_78733_k.field_74320_O > 0 || var1.field_146042_b != Minecraft.func_71410_x().field_71439_g) {
            float var31 = (var1.field_146042_b.field_70760_ar + (var1.field_146042_b.field_70761_aq - var1.field_146042_b.field_70760_ar) * var9)
               * 3.1415927F
               / 180.0F;
            double var32 = (double)MathHelper.func_76126_a(var31);
            double var34 = (double)MathHelper.func_76134_b(var31);
            var23 = var1.field_146042_b.field_70169_q
               + (var1.field_146042_b.field_70165_t - var1.field_146042_b.field_70169_q) * (double)var9
               - var34 * 0.35
               - var32 * 0.85;
            var25 = var1.field_146042_b.field_70167_r + var29 + (var1.field_146042_b.field_70163_u - var1.field_146042_b.field_70167_r) * (double)var9 - 0.45;
            var27 = var1.field_146042_b.field_70166_s
               + (var1.field_146042_b.field_70161_v - var1.field_146042_b.field_70166_s) * (double)var9
               - var32 * 0.35
               + var34 * 0.85;
         }

         double var46 = var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var9;
         double var33 = var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var9 + 0.25;
         double var35 = var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var9;
         double var37 = (double)((float)(var23 - var46));
         double var39 = (double)((float)(var25 - var33));
         double var41 = (double)((float)(var27 - var35));
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         var10.func_78371_b(3);
         var10.func_78378_d(0);
         byte var43 = 16;

         for(int var44 = 0; var44 <= var43; ++var44) {
            float var45 = (float)var44 / (float)var43;
            var10.func_78377_a(var2 + var37 * (double)var45, var4 + var39 * (double)(var45 * var45 + var45) * 0.5 + 0.25, var6 + var41 * (double)var45);
         }

         var10.func_78381_a();
         GL11.glEnable(2896);
         GL11.glEnable(3553);
      }
   }

   protected ResourceLocation func_110775_a(EntityFishHook var1) {
      return field_110792_a;
   }
}
