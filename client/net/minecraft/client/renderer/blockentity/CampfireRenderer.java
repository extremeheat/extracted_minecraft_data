package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class CampfireRenderer implements BlockEntityRenderer<CampfireBlockEntity> {
   private static final float SIZE = 0.375F;
   private final ItemModelResolver itemModelResolver;

   public CampfireRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public void submit(CampfireBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Direction var9 = (Direction)var1.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList var10 = var1.getItems();
      int var11 = (int)var1.getBlockPos().asLong();

      for(int var12 = 0; var12 < var10.size(); ++var12) {
         ItemStack var13 = (ItemStack)var10.get(var12);
         if (var13 != ItemStack.EMPTY) {
            var3.pushPose();
            var3.translate(0.5F, 0.44921875F, 0.5F);
            Direction var14 = Direction.from2DDataValue((var12 + var9.get2DDataValue()) % 4);
            float var15 = -var14.toYRot();
            var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var15));
            var3.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
            var3.translate(-0.3125F, -0.3125F, 0.0F);
            var3.scale(0.375F, 0.375F, 0.375F);
            ItemStackRenderState var16 = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(var16, var13, ItemDisplayContext.FIXED, var1.getLevel(), (ItemOwner)null, var11 + var12);
            var16.submit(var3, var8, var4, var5, 0);
            var3.popPose();
         }
      }

   }
}
