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
import net.minecraft.world.phys.Vec3;

public class CampfireRenderer implements BlockEntityRenderer<CampfireBlockEntity> {
   private static final float SIZE = 0.375F;
   private final ItemRenderer itemRenderer;

   public CampfireRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(CampfireBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Direction var8 = (Direction)var1.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList var9 = var1.getItems();
      int var10 = (int)var1.getBlockPos().asLong();

      for(int var11 = 0; var11 < var9.size(); ++var11) {
         ItemStack var12 = (ItemStack)var9.get(var11);
         if (var12 != ItemStack.EMPTY) {
            var3.pushPose();
            var3.translate(0.5F, 0.44921875F, 0.5F);
            Direction var13 = Direction.from2DDataValue((var11 + var8.get2DDataValue()) % 4);
            float var14 = -var13.toYRot();
            var3.mulPose(Axis.YP.rotationDegrees(var14));
            var3.mulPose(Axis.XP.rotationDegrees(90.0F));
            var3.translate(-0.3125F, -0.3125F, 0.0F);
            var3.scale(0.375F, 0.375F, 0.375F);
            this.itemRenderer.renderStatic(var12, ItemDisplayContext.FIXED, var5, var6, var3, var4, var1.getLevel(), var10 + var11);
            var3.popPose();
         }
      }

   }
}
