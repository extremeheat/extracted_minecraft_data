package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class LecternRenderer implements BlockEntityRenderer<LecternBlockEntity> {
   private final MaterialSet materials;
   private final BookModel bookModel;
   private final BookModel.State bookState = new BookModel.State();

   public LecternRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.bookModel = new BookModel(var1.bakeLayer(ModelLayers.BOOK));
      this.bookState.animationPos = 0.0F;
      this.bookState.pageFlip1 = 0.1F;
      this.bookState.pageFlip2 = 0.9F;
      this.bookState.open = 1.2F;
   }

   public void submit(LecternBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      BlockState var9 = var1.getBlockState();
      if ((Boolean)var9.getValue(LecternBlock.HAS_BOOK)) {
         var3.pushPose();
         var3.translate(0.5F, 1.0625F, 0.5F);
         float var10 = ((Direction)var9.getValue(LecternBlock.FACING)).getClockWise().toYRot();
         var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var10));
         var3.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(67.5F));
         var3.translate(0.0F, -0.125F, 0.0F);
         var8.submitModel(this.bookModel, this.bookState, var3, EnchantTableRenderer.BOOK_LOCATION.renderType(RenderType::entitySolid), var4, var5, -1, this.materials.get(EnchantTableRenderer.BOOK_LOCATION), 0, var7);
         var3.popPose();
      }
   }
}
