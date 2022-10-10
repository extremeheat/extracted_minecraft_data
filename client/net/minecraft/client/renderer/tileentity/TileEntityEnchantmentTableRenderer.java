package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBook;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class TileEntityEnchantmentTableRenderer extends TileEntityRenderer<TileEntityEnchantmentTable> {
   private static final ResourceLocation field_147540_b = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final ModelBook field_147541_c = new ModelBook();

   public TileEntityEnchantmentTableRenderer() {
      super();
   }

   public void func_199341_a(TileEntityEnchantmentTable var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4 + 0.75F, (float)var6 + 0.5F);
      float var10 = (float)var1.field_195522_a + var8;
      GlStateManager.func_179109_b(0.0F, 0.1F + MathHelper.func_76126_a(var10 * 0.1F) * 0.01F, 0.0F);

      float var11;
      for(var11 = var1.field_195529_l - var1.field_195530_m; var11 >= 3.1415927F; var11 -= 6.2831855F) {
      }

      while(var11 < -3.1415927F) {
         var11 += 6.2831855F;
      }

      float var12 = var1.field_195530_m + var11 * var8;
      GlStateManager.func_179114_b(-var12 * 57.295776F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(80.0F, 0.0F, 0.0F, 1.0F);
      this.func_147499_a(field_147540_b);
      float var13 = var1.field_195524_g + (var1.field_195523_f - var1.field_195524_g) * var8 + 0.25F;
      float var14 = var1.field_195524_g + (var1.field_195523_f - var1.field_195524_g) * var8 + 0.75F;
      var13 = (var13 - (float)MathHelper.func_76140_b((double)var13)) * 1.6F - 0.3F;
      var14 = (var14 - (float)MathHelper.func_76140_b((double)var14)) * 1.6F - 0.3F;
      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      if (var14 > 1.0F) {
         var14 = 1.0F;
      }

      float var15 = var1.field_195528_k + (var1.field_195527_j - var1.field_195528_k) * var8;
      GlStateManager.func_179089_o();
      this.field_147541_c.func_78088_a((Entity)null, var10, var13, var14, var15, 0.0F, 0.0625F);
      GlStateManager.func_179121_F();
   }
}
