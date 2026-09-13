package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySignRenderer extends TileEntitySpecialRenderer {
   private static final ResourceLocation field_147513_b = new ResourceLocation("textures/entity/sign.png");
   private final ModelSign field_147514_c = new ModelSign();

   public TileEntitySignRenderer() {
      super();
   }

   public void func_147500_a(TileEntitySign var1, double var2, double var4, double var6, float var8) {
      Block var9 = var1.func_145838_q();
      GL11.glPushMatrix();
      float var10 = 0.6666667F;
      if (var9 == Blocks.field_150472_an) {
         GL11.glTranslatef((float)var2 + 0.5F, (float)var4 + 0.75F * var10, (float)var6 + 0.5F);
         float var11 = (float)(var1.func_145832_p() * 360) / 16.0F;
         GL11.glRotatef(-var11, 0.0F, 1.0F, 0.0F);
         this.field_147514_c.field_78165_b.field_78806_j = true;
      } else {
         int var16 = var1.func_145832_p();
         float var12 = 0.0F;
         if (var16 == 2) {
            var12 = 180.0F;
         }

         if (var16 == 4) {
            var12 = 90.0F;
         }

         if (var16 == 5) {
            var12 = -90.0F;
         }

         GL11.glTranslatef((float)var2 + 0.5F, (float)var4 + 0.75F * var10, (float)var6 + 0.5F);
         GL11.glRotatef(-var12, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
         this.field_147514_c.field_78165_b.field_78806_j = false;
      }

      this.func_147499_a(field_147513_b);
      GL11.glPushMatrix();
      GL11.glScalef(var10, -var10, -var10);
      this.field_147514_c.func_78164_a();
      GL11.glPopMatrix();
      FontRenderer var17 = this.func_147498_b();
      float var18 = 0.016666668F * var10;
      GL11.glTranslatef(0.0F, 0.5F * var10, 0.07F * var10);
      GL11.glScalef(var18, -var18, var18);
      GL11.glNormal3f(0.0F, 0.0F, -1.0F * var18);
      GL11.glDepthMask(false);
      byte var13 = 0;

      for(int var14 = 0; var14 < var1.field_145915_a.length; ++var14) {
         String var15 = var1.field_145915_a[var14];
         if (var14 == var1.field_145918_i) {
            var15 = "> " + var15 + " <";
            var17.func_78276_b(var15, -var17.func_78256_a(var15) / 2, var14 * 10 - var1.field_145915_a.length * 5, var13);
         } else {
            var17.func_78276_b(var15, -var17.func_78256_a(var15) / 2, var14 * 10 - var1.field_145915_a.length * 5, var13);
         }
      }

      GL11.glDepthMask(true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }
}
