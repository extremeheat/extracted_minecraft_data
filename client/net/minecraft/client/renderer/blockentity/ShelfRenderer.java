package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
   private static final float ITEM_SIZE = 0.25F;
   private final ItemRenderer itemRenderer;

   public ShelfRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.itemRenderer();
   }

   public void render(ShelfBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Direction var8 = (Direction)var1.getBlockState().getValue(ShelfBlock.FACING);
      NonNullList var9 = var1.getItems();
      int var10 = HashCommon.long2int(var1.getBlockPos().asLong());
      float var11 = var8.getAxis().isHorizontal() ? 180.0F - var8.toYRot() : 180.0F;

      for(int var12 = 0; var12 < var9.size(); ++var12) {
         ItemStack var13 = (ItemStack)var9.get(var12);
         if (!var13.isEmpty()) {
            this.renderItem(var1, var3, var4, var5, var6, var12, var1.getBlockPos(), var8, var11, var13, var10);
         }
      }

   }

   private void renderItem(ShelfBlockEntity var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, int var6, BlockPos var7, Direction var8, float var9, ItemStack var10, int var11) {
      float var12 = (float)(1 - var6) * 0.3125F;
      Vec3 var13 = new Vec3((double)var12, -0.25, 0.25);
      var2.pushPose();
      var2.translate(0.5F, 0.5F, 0.5F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var9));
      var2.translate(var13);
      var2.scale(0.25F, 0.25F, 0.25F);
      Vec3 var14 = var7.getCenter().add(var13.yRot(var9 * 0.017453292F));
      ItemOwner var15 = ItemOwner.custom(var14, var8.getOpposite(), var1.getLevel());
      this.itemRenderer.renderUpwardsFrom(var15, var10, ItemDisplayContext.FIXED, var2, var3, var1.getLevel(), var4, var5, var11 + var6);
      var2.popPose();
   }
}
