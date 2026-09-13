package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render {
   private static final ResourceLocation field_110780_a = new ResourceLocation("textures/entity/arrow.png");

   public RenderArrow() {
      super();
   }

   public void func_76986_a(EntityArrow var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_110777_b(var1);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      GL11.glRotatef(var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var9 - 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9, 0.0F, 0.0F, 1.0F);
      Tessellator var10 = Tessellator.field_78398_a;
      byte var11 = 0;
      float var12 = 0.0F;
      float var13 = 0.5F;
      float var14 = (float)(0 + var11 * 10) / 32.0F;
      float var15 = (float)(5 + var11 * 10) / 32.0F;
      float var16 = 0.0F;
      float var17 = 0.15625F;
      float var18 = (float)(5 + var11 * 10) / 32.0F;
      float var19 = (float)(10 + var11 * 10) / 32.0F;
      float var20 = 0.05625F;
      GL11.glEnable(32826);
      float var21 = (float)var1.field_70249_b - var9;
      if (var21 > 0.0F) {
         float var22 = -MathHelper.func_76126_a(var21 * 3.0F) * var21;
         GL11.glRotatef(var22, 0.0F, 0.0F, 1.0F);
      }

      GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(var20, var20, var20);
      GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(var20, 0.0F, 0.0F);
      var10.func_78382_b();
      var10.func_78374_a(-7.0, -2.0, -2.0, (double)var16, (double)var18);
      var10.func_78374_a(-7.0, -2.0, 2.0, (double)var17, (double)var18);
      var10.func_78374_a(-7.0, 2.0, 2.0, (double)var17, (double)var19);
      var10.func_78374_a(-7.0, 2.0, -2.0, (double)var16, (double)var19);
      var10.func_78381_a();
      GL11.glNormal3f(-var20, 0.0F, 0.0F);
      var10.func_78382_b();
      var10.func_78374_a(-7.0, 2.0, -2.0, (double)var16, (double)var18);
      var10.func_78374_a(-7.0, 2.0, 2.0, (double)var17, (double)var18);
      var10.func_78374_a(-7.0, -2.0, 2.0, (double)var17, (double)var19);
      var10.func_78374_a(-7.0, -2.0, -2.0, (double)var16, (double)var19);
      var10.func_78381_a();

      for(int var23 = 0; var23 < 4; ++var23) {
         GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, var20);
         var10.func_78382_b();
         var10.func_78374_a(-8.0, -2.0, 0.0, (double)var12, (double)var14);
         var10.func_78374_a(8.0, -2.0, 0.0, (double)var13, (double)var14);
         var10.func_78374_a(8.0, 2.0, 0.0, (double)var13, (double)var15);
         var10.func_78374_a(-8.0, 2.0, 0.0, (double)var12, (double)var15);
         var10.func_78381_a();
      }

      GL11.glDisable(32826);
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityArrow var1) {
      return field_110780_a;
   }
}
