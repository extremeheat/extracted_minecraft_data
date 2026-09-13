package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFireball extends Render {
   private float field_77002_a;

   public RenderFireball(float var1) {
      super();
      this.field_77002_a = var1;
   }

   public void func_76986_a(EntityFireball var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      this.func_110777_b(var1);
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      GL11.glEnable(32826);
      float var10 = this.field_77002_a;
      GL11.glScalef(var10 / 1.0F, var10 / 1.0F, var10 / 1.0F);
      IIcon var11 = Items.field_151059_bz.func_77617_a(0);
      Tessellator var12 = Tessellator.field_78398_a;
      float var13 = var11.func_94209_e();
      float var14 = var11.func_94212_f();
      float var15 = var11.func_94206_g();
      float var16 = var11.func_94210_h();
      float var17 = 1.0F;
      float var18 = 0.5F;
      float var19 = 0.25F;
      GL11.glRotatef(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      var12.func_78382_b();
      var12.func_78375_b(0.0F, 1.0F, 0.0F);
      var12.func_78374_a((double)(0.0F - var18), (double)(0.0F - var19), 0.0, (double)var13, (double)var16);
      var12.func_78374_a((double)(var17 - var18), (double)(0.0F - var19), 0.0, (double)var14, (double)var16);
      var12.func_78374_a((double)(var17 - var18), (double)(1.0F - var19), 0.0, (double)var14, (double)var15);
      var12.func_78374_a((double)(0.0F - var18), (double)(1.0F - var19), 0.0, (double)var13, (double)var15);
      var12.func_78381_a();
      GL11.glDisable(32826);
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityFireball var1) {
      return TextureMap.field_110576_c;
   }
}
