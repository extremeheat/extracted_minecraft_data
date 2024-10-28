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
      Direction var7 = (Direction)var1.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList var8 = var1.getItems();
      int var9 = (int)var1.getBlockPos().asLong();

      for(int var10 = 0; var10 < var8.size(); ++var10) {
         ItemStack var11 = (ItemStack)var8.get(var10);
         if (var11 != ItemStack.EMPTY) {
            var3.pushPose();
            var3.translate(0.5F, 0.44921875F, 0.5F);
            Direction var12 = Direction.from2DDataValue((var10 + var7.get2DDataValue()) % 4);
            float var13 = -var12.toYRot();
            var3.mulPose(Axis.YP.rotationDegrees(var13));
            var3.mulPose(Axis.XP.rotationDegrees(90.0F));
            var3.translate(-0.3125F, -0.3125F, 0.0F);
            var3.scale(0.375F, 0.375F, 0.375F);
            this.itemRenderer.renderStatic(var11, ItemDisplayContext.FIXED, var5, var6, var3, var4, var1.getLevel(), var9 + var10);
            var3.popPose();
         }
      }

   }
}
