package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.statue.CopperGolemStatueModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CopperGolemStatueBlockRenderer implements BlockEntityRenderer<CopperGolemStatueBlockEntity, CopperGolemStatueRenderState> {
   private final Map<CopperGolemStatueBlock.Pose, CopperGolemStatueModel> models = new HashMap();

   public CopperGolemStatueBlockRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      EntityModelSet modelSet = context.entityModelSet();
      this.models.put(CopperGolemStatueBlock.Pose.STANDING, new CopperGolemStatueModel(modelSet.bakeLayer(ModelLayers.COPPER_GOLEM)));
      this.models.put(CopperGolemStatueBlock.Pose.RUNNING, new CopperGolemStatueModel(modelSet.bakeLayer(ModelLayers.COPPER_GOLEM_RUNNING)));
      this.models.put(CopperGolemStatueBlock.Pose.SITTING, new CopperGolemStatueModel(modelSet.bakeLayer(ModelLayers.COPPER_GOLEM_SITTING)));
      this.models.put(CopperGolemStatueBlock.Pose.STAR, new CopperGolemStatueModel(modelSet.bakeLayer(ModelLayers.COPPER_GOLEM_STAR)));
   }

   public CopperGolemStatueRenderState createRenderState() {
      return new CopperGolemStatueRenderState();
   }

   public void extractRenderState(final CopperGolemStatueBlockEntity blockEntity, final CopperGolemStatueRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.direction = (Direction)blockEntity.getBlockState().getValue(CopperGolemStatueBlock.FACING);
      state.pose = (CopperGolemStatueBlock.Pose)blockEntity.getBlockState().getValue(BlockStateProperties.COPPER_GOLEM_POSE);
   }

   public void submit(final CopperGolemStatueRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      Block var6 = state.blockState.getBlock();
      if (var6 instanceof CopperGolemStatueBlock copperGolemStatueBlock) {
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.0F, 0.5F);
         CopperGolemStatueModel model = (CopperGolemStatueModel)this.models.get(state.pose);
         Direction direction = state.direction;
         RenderType renderType = RenderTypes.entityCutoutNoCull(CopperGolemOxidationLevels.getOxidationLevel(copperGolemStatueBlock.getWeatheringState()).texture());
         submitNodeCollector.submitModel(model, direction, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
         poseStack.popPose();
      }

   }
}
