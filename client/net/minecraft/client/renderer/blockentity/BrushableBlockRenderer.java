package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity> {
   private final ItemModelResolver itemModelResolver;

   public BrushableBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public void submit(BrushableBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      if (var1.getLevel() != null) {
         int var9 = (Integer)var1.getBlockState().getValue(BlockStateProperties.DUSTED);
         if (var9 > 0) {
            Direction var10 = var1.getHitDirection();
            if (var10 != null) {
               ItemStack var11 = var1.getItem();
               if (!var11.isEmpty()) {
                  var3.pushPose();
                  var3.translate(0.0F, 0.5F, 0.0F);
                  float[] var12 = this.translations(var10, var9);
                  var3.translate(var12[0], var12[1], var12[2]);
                  var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(75.0F));
                  boolean var13 = var10 == Direction.EAST || var10 == Direction.WEST;
                  var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)((var13 ? 90 : 0) + 11)));
                  var3.scale(0.5F, 0.5F, 0.5F);
                  int var14 = LevelRenderer.getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, var1.getLevel(), var1.getBlockState(), var1.getBlockPos().relative(var10));
                  ItemStackRenderState var15 = new ItemStackRenderState();
                  this.itemModelResolver.updateForTopItem(var15, var11, ItemDisplayContext.FIXED, var1.getLevel(), (ItemOwner)null, 0);
                  var15.submit(var3, var8, var14, OverlayTexture.NO_OVERLAY, 0);
                  var3.popPose();
               }
            }
         }
      }
   }

   private float[] translations(Direction var1, int var2) {
      float[] var3 = new float[]{0.5F, 0.0F, 0.5F};
      float var4 = (float)var2 / 10.0F * 0.75F;
      switch (var1) {
         case EAST -> var3[0] = 0.73F + var4;
         case WEST -> var3[0] = 0.25F - var4;
         case UP -> var3[1] = 0.25F + var4;
         case DOWN -> var3[1] = -0.23F - var4;
         case NORTH -> var3[2] = 0.25F - var4;
         case SOUTH -> var3[2] = 0.73F + var4;
      }

      return var3;
   }
}
