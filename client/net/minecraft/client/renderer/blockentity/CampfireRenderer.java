package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

public class CampfireRenderer extends BlockEntityRenderer<CampfireBlockEntity> {
   public CampfireRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(CampfireBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Direction var7 = (Direction)var1.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList var8 = var1.getItems();

      for(int var9 = 0; var9 < var8.size(); ++var9) {
         ItemStack var10 = (ItemStack)var8.get(var9);
         if (var10 != ItemStack.EMPTY) {
            var3.pushPose();
            var3.translate(0.5D, 0.44921875D, 0.5D);
            Direction var11 = Direction.from2DDataValue((var9 + var7.get2DDataValue()) % 4);
            float var12 = -var11.toYRot();
            var3.mulPose(Vector3f.YP.rotationDegrees(var12));
            var3.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            var3.translate(-0.3125D, -0.3125D, 0.0D);
            var3.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(var10, ItemTransforms.TransformType.FIXED, var5, var6, var3, var4);
            var3.popPose();
         }
      }

   }
}
