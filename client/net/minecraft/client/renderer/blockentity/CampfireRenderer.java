package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

public class CampfireRenderer implements BlockEntityRenderer<CampfireBlockEntity> {
   private static final float SIZE = 0.375F;
   private final ItemRenderer itemRenderer;

   public CampfireRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(CampfireBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      boolean var7 = var1.isFryingTable;
      Direction var8 = var1.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList var9 = var1.getItems();
      int var10 = (int)var1.getBlockPos().asLong();
      float var11 = var7 ? -2.5F : -5.0F;
      float var12 = var7 ? 11.0F : 7.0F;

      for(int var13 = 0; var13 < var9.size(); ++var13) {
         ItemStack var14 = (ItemStack)var9.get(var13);
         if (var14 != ItemStack.EMPTY) {
            var3.pushPose();
            var3.translate(0.5F, (var12 + 0.1875F) / 16.0F, 0.5F);
            Direction var15 = Direction.from2DDataValue((var13 + var8.get2DDataValue()) % 4);
            float var16 = -var15.toYRot();
            var3.mulPose(Axis.YP.rotationDegrees(var16));
            var3.mulPose(Axis.XP.rotationDegrees(90.0F));
            var3.translate(var11 / 16.0F, var11 / 16.0F, 0.0F);
            var3.scale(0.375F, 0.375F, 0.375F);
            this.itemRenderer.renderStatic(var14, ItemDisplayContext.FIXED, var5, var6, var3, var4, var1.getLevel(), var10 + var13);
            var3.popPose();
         }
      }
   }
}
