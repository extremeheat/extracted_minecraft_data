package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.LecternRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class LecternRenderer implements BlockEntityRenderer<LecternBlockEntity, LecternRenderState> {
   private final MaterialSet materials;
   private final BookModel bookModel;
   private final BookModel.State bookState = new BookModel.State(0.0F, 0.1F, 0.9F, 1.2F);

   public LecternRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.bookModel = new BookModel(var1.bakeLayer(ModelLayers.BOOK));
   }

   public LecternRenderState createRenderState() {
      return new LecternRenderState();
   }

   public void extractRenderState(LecternBlockEntity var1, LecternRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.hasBook = (Boolean)var1.getBlockState().getValue(LecternBlock.HAS_BOOK);
      var2.yRot = ((Direction)var1.getBlockState().getValue(LecternBlock.FACING)).getClockWise().toYRot();
   }

   public void submit(LecternRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.hasBook) {
         var2.pushPose();
         var2.translate(0.5F, 1.0625F, 0.5F);
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var1.yRot));
         var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(67.5F));
         var2.translate(0.0F, -0.125F, 0.0F);
         var3.submitModel(this.bookModel, this.bookState, var2, EnchantTableRenderer.BOOK_LOCATION.renderType(RenderType::entitySolid), var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, this.materials.get(EnchantTableRenderer.BOOK_LOCATION), 0, var1.breakProgress);
         var2.popPose();
      }
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
