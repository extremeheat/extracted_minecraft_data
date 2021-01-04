package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BookModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

public class EnchantTableRenderer extends BlockEntityRenderer<EnchantmentTableBlockEntity> {
   private static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final BookModel bookModel = new BookModel();

   public EnchantTableRenderer() {
      super();
   }

   public void render(EnchantmentTableBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.75F, (float)var6 + 0.5F);
      float var10 = (float)var1.time + var8;
      GlStateManager.translatef(0.0F, 0.1F + Mth.sin(var10 * 0.1F) * 0.01F, 0.0F);

      float var11;
      for(var11 = var1.rot - var1.oRot; var11 >= 3.1415927F; var11 -= 6.2831855F) {
      }

      while(var11 < -3.1415927F) {
         var11 += 6.2831855F;
      }

      float var12 = var1.oRot + var11 * var8;
      GlStateManager.rotatef(-var12 * 57.295776F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(BOOK_LOCATION);
      float var13 = Mth.lerp(var8, var1.oFlip, var1.flip) + 0.25F;
      float var14 = Mth.lerp(var8, var1.oFlip, var1.flip) + 0.75F;
      var13 = (var13 - (float)Mth.fastFloor((double)var13)) * 1.6F - 0.3F;
      var14 = (var14 - (float)Mth.fastFloor((double)var14)) * 1.6F - 0.3F;
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

      float var15 = Mth.lerp(var8, var1.oOpen, var1.open);
      GlStateManager.enableCull();
      this.bookModel.render(var10, var13, var14, var15, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
