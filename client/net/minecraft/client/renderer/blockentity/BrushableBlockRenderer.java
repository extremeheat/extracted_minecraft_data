package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
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
import org.jspecify.annotations.Nullable;

public class BrushableBlockRenderer implements BlockEntityRenderer<BrushableBlockEntity, BrushableBlockRenderState> {
   private final ItemModelResolver itemModelResolver;

   public BrushableBlockRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      this.itemModelResolver = context.itemModelResolver();
   }

   public BrushableBlockRenderState createRenderState() {
      return new BrushableBlockRenderState();
   }

   public void extractRenderState(final BrushableBlockEntity blockEntity, final BrushableBlockRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.hitDirection = blockEntity.getHitDirection();
      state.dustProgress = (Integer)blockEntity.getBlockState().getValue(BlockStateProperties.DUSTED);
      if (blockEntity.getLevel() != null && blockEntity.getHitDirection() != null) {
         state.lightCoords = LevelRenderer.getLightCoords(LevelRenderer.BrightnessGetter.DEFAULT, blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos().relative(blockEntity.getHitDirection()));
      }

      this.itemModelResolver.updateForTopItem(state.itemState, blockEntity.getItem(), ItemDisplayContext.FIXED, blockEntity.getLevel(), (ItemOwner)null, 0);
   }

   public void submit(final BrushableBlockRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      if (state.dustProgress > 0 && state.hitDirection != null && !state.itemState.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.0F, 0.5F, 0.0F);
         float[] translations = this.translations(state.hitDirection, state.dustProgress);
         poseStack.translate(translations[0], translations[1], translations[2]);
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(75.0F));
         boolean eastWest = state.hitDirection == Direction.EAST || state.hitDirection == Direction.WEST;
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)((eastWest ? 90 : 0) + 11)));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         state.itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
         poseStack.popPose();
      }
   }

   private float[] translations(final Direction direction, final int completionState) {
      float[] xyzTranslations = new float[]{0.5F, 0.0F, 0.5F};
      float completionOffset = (float)completionState / 10.0F * 0.75F;
      switch (direction) {
         case EAST -> xyzTranslations[0] = 0.73F + completionOffset;
         case WEST -> xyzTranslations[0] = 0.25F - completionOffset;
         case UP -> xyzTranslations[1] = 0.25F + completionOffset;
         case DOWN -> xyzTranslations[1] = -0.23F - completionOffset;
         case NORTH -> xyzTranslations[2] = 0.25F - completionOffset;
         case SOUTH -> xyzTranslations[2] = 0.73F + completionOffset;
      }

      return xyzTranslations;
   }
}
