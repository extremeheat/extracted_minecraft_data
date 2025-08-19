package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
   private static final float ITEM_SIZE = 0.25F;
   private static final float ALIGN_ITEMS_TO_BOTTOM = -0.125F;
   private final ItemRenderer itemRenderer;

   public ShelfRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.itemRenderer();
   }

   public void submit(ShelfBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Direction var9 = (Direction)var1.getBlockState().getValue(ShelfBlock.FACING);
      NonNullList var10 = var1.getItems();
      int var11 = HashCommon.long2int(var1.getBlockPos().asLong());
      float var12 = var9.getAxis().isHorizontal() ? -var9.toYRot() : 180.0F;

      for(int var13 = 0; var13 < var10.size(); ++var13) {
         ItemStack var14 = (ItemStack)var10.get(var13);
         if (!var14.isEmpty()) {
            this.submitItem(var1, var3, var8, var4, var5, var13, var12, var14, var11);
         }
      }

   }

   private void submitItem(ShelfBlockEntity var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, int var6, float var7, ItemStack var8, int var9) {
      float var10 = (float)(var6 - 1) * 0.3125F;
      boolean var11 = var1.getAlignItemsToBottom();
      Vec3 var12 = new Vec3((double)var10, var11 ? -0.125 : 0.0, -0.25);
      var2.pushPose();
      var2.translate(0.5F, 0.5F, 0.5F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var7));
      var2.translate(var12);
      var2.scale(0.25F, 0.25F, 0.25F);
      if (!var11) {
         AABB var13 = this.itemRenderer.getBoundingBox(var8, ItemDisplayContext.ON_SHELF, var1.getLevel(), var1, var9 + var6);
         var2.translate(0.0, -(var13.maxY - var13.minY) / 2.0, 0.0);
      }

      this.itemRenderer.renderUpwardsFrom(ItemOwner.offsetFromOwner(var1, var12.yRot(var7 * 0.017453292F)), var8, ItemDisplayContext.ON_SHELF, var2, var3, var1.getLevel(), var4, var5, var9 + var6);
      var2.popPose();
   }
}
