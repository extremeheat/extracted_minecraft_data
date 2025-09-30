package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.EnchantTableRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class EnchantTableRenderer implements BlockEntityRenderer<EnchantingTableBlockEntity, EnchantTableRenderState> {
   public static final Material BOOK_LOCATION;
   private final MaterialSet materials;
   private final BookModel bookModel;

   public EnchantTableRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.bookModel = new BookModel(var1.bakeLayer(ModelLayers.BOOK));
   }

   public EnchantTableRenderState createRenderState() {
      return new EnchantTableRenderState();
   }

   public void extractRenderState(EnchantingTableBlockEntity var1, EnchantTableRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.flip = Mth.lerp(var3, var1.oFlip, var1.flip);
      var2.open = Mth.lerp(var3, var1.oOpen, var1.open);
      var2.time = (float)var1.time + var3;

      float var6;
      for(var6 = var1.rot - var1.oRot; var6 >= 3.1415927F; var6 -= 6.2831855F) {
      }

      while(var6 < -3.1415927F) {
         var6 += 6.2831855F;
      }

      var2.yRot = var1.oRot + var6 * var3;
   }

   public void submit(EnchantTableRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.translate(0.5F, 0.75F, 0.5F);
      var2.translate(0.0F, 0.1F + Mth.sin(var1.time * 0.1F) * 0.01F, 0.0F);
      float var5 = var1.yRot;
      var2.mulPose((Quaternionfc)Axis.YP.rotation(-var5));
      var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(80.0F));
      float var6 = Mth.frac(var1.flip + 0.25F) * 1.6F - 0.3F;
      float var7 = Mth.frac(var1.flip + 0.75F) * 1.6F - 0.3F;
      BookModel.State var8 = new BookModel.State(var1.time, Mth.clamp(var6, 0.0F, 1.0F), Mth.clamp(var7, 0.0F, 1.0F), var1.open);
      var3.submitModel(this.bookModel, var8, var2, BOOK_LOCATION.renderType(RenderType::entitySolid), var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, this.materials.get(BOOK_LOCATION), 0, var1.breakProgress);
      var2.popPose();
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static {
      BOOK_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("enchanting_table_book");
   }
}
