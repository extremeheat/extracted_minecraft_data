package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BrushableBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity, BrushableBlockRenderState> {
   private final ItemModelResolver itemModelResolver;

   public BrushableBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public BrushableBlockRenderState createRenderState() {
      return new BrushableBlockRenderState();
   }

   public void extractRenderState(BrushableBlockEntity var1, BrushableBlockRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.hitDirection = var1.getHitDirection();
      var2.dustProgress = (Integer)var1.getBlockState().getValue(BlockStateProperties.DUSTED);
      if (var1.getLevel() != null && var1.getHitDirection() != null) {
         var2.lightCoords = LevelRenderer.getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, var1.getLevel(), var1.getBlockState(), var1.getBlockPos().relative(var1.getHitDirection()));
      }

      this.itemModelResolver.updateForTopItem(var2.itemState, var1.getItem(), ItemDisplayContext.FIXED, var1.getLevel(), (ItemOwner)null, 0);
   }

   public void submit(BrushableBlockRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.dustProgress > 0 && var1.hitDirection != null && !var1.itemState.isEmpty()) {
         var2.pushPose();
         var2.translate(0.0F, 0.5F, 0.0F);
         float[] var5 = this.translations(var1.hitDirection, var1.dustProgress);
         var2.translate(var5[0], var5[1], var5[2]);
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(75.0F));
         boolean var6 = var1.hitDirection == Direction.EAST || var1.hitDirection == Direction.WEST;
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)((var6 ? 90 : 0) + 11)));
         var2.scale(0.5F, 0.5F, 0.5F);
         var1.itemState.submit(var2, var3, var1.lightCoords, OverlayTexture.NO_OVERLAY, 0);
         var2.popPose();
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

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
