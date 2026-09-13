package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTNTPrimed extends Render {
   private RenderBlocks field_76993_a = new RenderBlocks();

   public RenderTNTPrimed() {
      super();
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityTNTPrimed var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      if ((float)var1.field_70516_a - var9 + 1.0F < 10.0F) {
         float var10 = 1.0F - ((float)var1.field_70516_a - var9 + 1.0F) / 10.0F;
         if (var10 < 0.0F) {
            var10 = 0.0F;
         }

         if (var10 > 1.0F) {
            var10 = 1.0F;
         }

         var10 *= var10;
         var10 *= var10;
         float var11 = 1.0F + var10 * 0.3F;
         GL11.glScalef(var11, var11, var11);
      }

      float var14 = (1.0F - ((float)var1.field_70516_a - var9 + 1.0F) / 100.0F) * 0.8F;
      this.func_110777_b(var1);
      this.field_76993_a.func_147800_a(Blocks.field_150335_W, 0, var1.func_70013_c(var9));
      if (var1.field_70516_a / 5 % 2 == 0) {
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 772);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, var14);
         this.field_76993_a.func_147800_a(Blocks.field_150335_W, 0, 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(3042);
         GL11.glEnable(2896);
         GL11.glEnable(3553);
      }

      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityTNTPrimed var1) {
      return TextureMap.field_110575_b;
   }
}
