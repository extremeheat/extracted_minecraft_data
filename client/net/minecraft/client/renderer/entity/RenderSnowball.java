package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSnowball extends Render {
   private Item field_94151_a;
   private int field_94150_f;

   public RenderSnowball(Item var1, int var2) {
      super();
      this.field_94151_a = var1;
      this.field_94150_f = var2;
   }

   public RenderSnowball(Item var1) {
      this(var1, 0);
   }

   @Override
   public void func_76986_a(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      IIcon var10 = this.field_94151_a.func_77617_a(this.field_94150_f);
      if (var10 != null) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)var2, (float)var4, (float)var6);
         GL11.glEnable(32826);
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         this.func_110777_b(var1);
         Tessellator var11 = Tessellator.field_78398_a;
         if (var10 == ItemPotion.func_94589_d("bottle_splash")) {
            int var12 = PotionHelper.func_77915_a(((EntityPotion)var1).func_70196_i(), false);
            float var13 = (float)(var12 >> 16 & 0xFF) / 255.0F;
            float var14 = (float)(var12 >> 8 & 0xFF) / 255.0F;
            float var15 = (float)(var12 & 0xFF) / 255.0F;
            GL11.glColor3f(var13, var14, var15);
            GL11.glPushMatrix();
            this.func_77026_a(var11, ItemPotion.func_94589_d("overlay"));
            GL11.glPopMatrix();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
         }

         this.func_77026_a(var11, var10);
         GL11.glDisable(32826);
         GL11.glPopMatrix();
      }
   }

   @Override
   protected ResourceLocation func_110775_a(Entity var1) {
      return TextureMap.field_110576_c;
   }

   private void func_77026_a(Tessellator var1, IIcon var2) {
      float var3 = var2.func_94209_e();
      float var4 = var2.func_94212_f();
      float var5 = var2.func_94206_g();
      float var6 = var2.func_94210_h();
      float var7 = 1.0F;
      float var8 = 0.5F;
      float var9 = 0.25F;
      GL11.glRotatef(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      var1.func_78382_b();
      var1.func_78375_b(0.0F, 1.0F, 0.0F);
      var1.func_78374_a((double)(0.0F - var8), (double)(0.0F - var9), 0.0, (double)var3, (double)var6);
      var1.func_78374_a((double)(var7 - var8), (double)(0.0F - var9), 0.0, (double)var4, (double)var6);
      var1.func_78374_a((double)(var7 - var8), (double)(var7 - var9), 0.0, (double)var4, (double)var5);
      var1.func_78374_a((double)(0.0F - var8), (double)(var7 - var9), 0.0, (double)var3, (double)var5);
      var1.func_78381_a();
   }
}
