package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class EnchantTableRenderer implements BlockEntityRenderer<EnchantingTableBlockEntity> {
   public static final Material BOOK_LOCATION;
   private final MaterialSet materials;
   private final BookModel bookModel;
   private final BookModel.State bookState = new BookModel.State();

   public EnchantTableRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.bookModel = new BookModel(var1.bakeLayer(ModelLayers.BOOK));
   }

   public void submit(EnchantingTableBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      var3.pushPose();
      var3.translate(0.5F, 0.75F, 0.5F);
      float var9 = (float)var1.time + var2;
      var3.translate(0.0F, 0.1F + Mth.sin(var9 * 0.1F) * 0.01F, 0.0F);

      float var10;
      for(var10 = var1.rot - var1.oRot; var10 >= 3.1415927F; var10 -= 6.2831855F) {
      }

      while(var10 < -3.1415927F) {
         var10 += 6.2831855F;
      }

      float var11 = var1.oRot + var10 * var2;
      var3.mulPose((Quaternionfc)Axis.YP.rotation(-var11));
      var3.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(80.0F));
      float var12 = Mth.lerp(var2, var1.oFlip, var1.flip);
      float var13 = Mth.frac(var12 + 0.25F) * 1.6F - 0.3F;
      float var14 = Mth.frac(var12 + 0.75F) * 1.6F - 0.3F;
      float var15 = Mth.lerp(var2, var1.oOpen, var1.open);
      this.bookState.animationPos = var9;
      this.bookState.pageFlip1 = Mth.clamp(var13, 0.0F, 1.0F);
      this.bookState.pageFlip2 = Mth.clamp(var14, 0.0F, 1.0F);
      this.bookState.open = var15;
      var8.submitModel(this.bookModel, this.bookState, var3, BOOK_LOCATION.renderType(RenderType::entitySolid), var4, var5, -1, this.materials.get(BOOK_LOCATION), 0, var7);
      var3.popPose();
   }

   static {
      BOOK_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("enchanting_table_book");
   }
}
