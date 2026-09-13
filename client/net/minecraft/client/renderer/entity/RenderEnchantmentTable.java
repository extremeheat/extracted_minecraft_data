package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnchantmentTable extends TileEntitySpecialRenderer {
   private static final ResourceLocation field_147540_b = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private ModelBook field_147541_c = new ModelBook();

   public RenderEnchantmentTable() {
      super();
   }

   public void func_147500_a(TileEntityEnchantmentTable var1, double var2, double var4, double var6, float var8) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2 + 0.5F, (float)var4 + 0.75F, (float)var6 + 0.5F);
      float var9 = (float)var1.field_145926_a + var8;
      GL11.glTranslatef(0.0F, 0.1F + MathHelper.func_76126_a(var9 * 0.1F) * 0.01F, 0.0F);
      float var10 = var1.field_145928_o - var1.field_145925_p;

      while(var10 >= 3.1415927F) {
         var10 -= 6.2831855F;
      }

      while(var10 < -3.1415927F) {
         var10 += 6.2831855F;
      }

      float var11 = var1.field_145925_p + var10 * var8;
      GL11.glRotatef(-var11 * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.func_147499_a(field_147540_b);
      float var12 = var1.field_145931_j + (var1.field_145933_i - var1.field_145931_j) * var8 + 0.25F;
      float var13 = var1.field_145931_j + (var1.field_145933_i - var1.field_145931_j) * var8 + 0.75F;
      var12 = (var12 - (float)MathHelper.func_76140_b((double)var12)) * 1.6F - 0.3F;
      var13 = (var13 - (float)MathHelper.func_76140_b((double)var13)) * 1.6F - 0.3F;
      if (var12 < 0.0F) {
         var12 = 0.0F;
      }

      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var12 > 1.0F) {
         var12 = 1.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      float var14 = var1.field_145927_n + (var1.field_145930_m - var1.field_145927_n) * var8;
      GL11.glEnable(2884);
      this.field_147541_c.func_78088_a(null, var9, var12, var13, var14, 0.0F, 0.0625F);
      GL11.glPopMatrix();
   }
}
