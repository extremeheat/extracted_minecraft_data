package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ShelfRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity, ShelfRenderState> {
   private static final float ITEM_SIZE = 0.25F;
   private static final float ALIGN_ITEMS_TO_BOTTOM = -0.25F;
   private final ItemModelResolver itemModelResolver;

   public ShelfRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public ShelfRenderState createRenderState() {
      return new ShelfRenderState();
   }

   public void extractRenderState(ShelfBlockEntity var1, ShelfRenderState var2, float var3, Vec3 var4, ModelFeatureRenderer.@Nullable CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.alignToBottom = var1.getAlignItemsToBottom();
      NonNullList var6 = var1.getItems();
      int var7 = HashCommon.long2int(var1.getBlockPos().asLong());

      for(int var8 = 0; var8 < var6.size(); ++var8) {
         ItemStack var9 = (ItemStack)var6.get(var8);
         if (!var9.isEmpty()) {
            ItemStackRenderState var10 = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(var10, var9, ItemDisplayContext.ON_SHELF, var1.level(), var1, var7 + var8);
            var2.items[var8] = var10;
         }
      }

   }

   public void submit(ShelfRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      Direction var5 = (Direction)var1.blockState.getValue(ShelfBlock.FACING);
      float var6 = var5.getAxis().isHorizontal() ? -var5.toYRot() : 180.0F;

      for(int var7 = 0; var7 < var1.items.length; ++var7) {
         ItemStackRenderState var8 = var1.items[var7];
         if (var8 != null) {
            this.submitItem(var1, var8, var2, var3, var7, var6);
         }
      }

   }

   private void submitItem(ShelfRenderState var1, ItemStackRenderState var2, PoseStack var3, SubmitNodeCollector var4, int var5, float var6) {
      float var7 = (float)(var5 - 1) * 0.3125F;
      Vec3 var8 = new Vec3((double)var7, var1.alignToBottom ? -0.25 : 0.0, -0.25);
      var3.pushPose();
      var3.translate(0.5F, 0.5F, 0.5F);
      var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var6));
      var3.translate(var8);
      var3.scale(0.25F, 0.25F, 0.25F);
      AABB var9 = var2.getModelBoundingBox();
      double var10 = -var9.minY;
      if (!var1.alignToBottom) {
         var10 += -(var9.maxY - var9.minY) / 2.0;
      }

      var3.translate(0.0, var10, 0.0);
      var2.submit(var3, var4, var1.lightCoords, OverlayTexture.NO_OVERLAY, 0);
      var3.popPose();
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
