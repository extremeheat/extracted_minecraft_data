package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

public class EnchantTableRenderer extends BlockEntityRenderer<EnchantmentTableBlockEntity> {
   public static final Material BOOK_LOCATION;
   private final BookModel bookModel = new BookModel();

   public EnchantTableRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(EnchantmentTableBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      var3.pushPose();
      var3.translate(0.5D, 0.75D, 0.5D);
      float var7 = (float)var1.time + var2;
      var3.translate(0.0D, (double)(0.1F + Mth.sin(var7 * 0.1F) * 0.01F), 0.0D);

      float var8;
      for(var8 = var1.rot - var1.oRot; var8 >= 3.1415927F; var8 -= 6.2831855F) {
      }

      while(var8 < -3.1415927F) {
         var8 += 6.2831855F;
      }

      float var9 = var1.oRot + var8 * var2;
      var3.mulPose(Vector3f.YP.rotation(-var9));
      var3.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
      float var10 = Mth.lerp(var2, var1.oFlip, var1.flip);
      float var11 = Mth.frac(var10 + 0.25F) * 1.6F - 0.3F;
      float var12 = Mth.frac(var10 + 0.75F) * 1.6F - 0.3F;
      float var13 = Mth.lerp(var2, var1.oOpen, var1.open);
      this.bookModel.setupAnim(var7, Mth.clamp(var11, 0.0F, 1.0F), Mth.clamp(var12, 0.0F, 1.0F), var13);
      VertexConsumer var14 = BOOK_LOCATION.buffer(var4, RenderType::entitySolid);
      this.bookModel.render(var3, var14, var5, var6, 1.0F, 1.0F, 1.0F, 1.0F);
      var3.popPose();
   }

   static {
      BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
   }
}
