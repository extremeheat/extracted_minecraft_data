package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LecternRenderer implements BlockEntityRenderer<LecternBlockEntity> {
   private final BookModel bookModel;

   public LecternRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.bookModel = new BookModel(var1.bakeLayer(ModelLayers.BOOK));
   }

   public void render(LecternBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      BlockState var8 = var1.getBlockState();
      if ((Boolean)var8.getValue(LecternBlock.HAS_BOOK)) {
         var3.pushPose();
         var3.translate(0.5F, 1.0625F, 0.5F);
         float var9 = ((Direction)var8.getValue(LecternBlock.FACING)).getClockWise().toYRot();
         var3.mulPose(Axis.YP.rotationDegrees(-var9));
         var3.mulPose(Axis.ZP.rotationDegrees(67.5F));
         var3.translate(0.0F, -0.125F, 0.0F);
         this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
         VertexConsumer var10 = EnchantTableRenderer.BOOK_LOCATION.buffer(var4, RenderType::entitySolid);
         this.bookModel.renderToBuffer(var3, var10, var5, var6);
         var3.popPose();
      }
   }
}
