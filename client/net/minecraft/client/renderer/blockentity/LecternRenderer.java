package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BookModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LecternRenderer extends BlockEntityRenderer<LecternBlockEntity> {
   private static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final BookModel bookModel = new BookModel();

   public LecternRenderer() {
      super();
   }

   public void render(LecternBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      BlockState var10 = var1.getBlockState();
      if ((Boolean)var10.getValue(LecternBlock.HAS_BOOK)) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 1.0F + 0.0625F, (float)var6 + 0.5F);
         float var11 = ((Direction)var10.getValue(LecternBlock.FACING)).getClockWise().toYRot();
         GlStateManager.rotatef(-var11, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(67.5F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(0.0F, -0.125F, 0.0F);
         this.bindTexture(BOOK_LOCATION);
         GlStateManager.enableCull();
         this.bookModel.render(0.0F, 0.1F, 0.9F, 1.2F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      }
   }
}
