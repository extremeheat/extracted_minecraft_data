package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.statue.CopperGolemStatueModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class CopperGolemStatueBlockRenderer implements BlockEntityRenderer<CopperGolemStatueBlockEntity, CopperGolemStatueRenderState> {
   private static final Map<Direction, Transformation> TRANSFORMATIONS = Util.<Direction, Transformation>makeEnumMap(Direction.class, CopperGolemStatueBlockRenderer::createModelTransformation);
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
      BlockState blockState = blockEntity.getBlockState();
      state.direction = (Direction)blockState.getValue(CopperGolemStatueBlock.FACING);
      state.pose = (CopperGolemStatueBlock.Pose)blockState.getValue(CopperGolemStatueBlock.POSE);
      Block var8 = blockState.getBlock();
      WeatheringCopper.WeatherState var10001;
      if (var8 instanceof CopperGolemStatueBlock copperGolemStatueBlock) {
         var10001 = copperGolemStatueBlock.getWeatheringState();
      } else {
         var10001 = WeatheringCopper.WeatherState.UNAFFECTED;
      }

      state.oxidationState = var10001;
   }

   public void submit(final CopperGolemStatueRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      poseStack.mulPose(modelTransformation(state.direction));
      CopperGolemStatueModel model = (CopperGolemStatueModel)this.models.get(state.pose);
      submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, CopperGolemOxidationLevels.getOxidationLevel(state.oxidationState).texture(), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
      poseStack.popPose();
   }

   public static Transformation modelTransformation(final Direction facing) {
      return (Transformation)TRANSFORMATIONS.get(facing);
   }

   private static Transformation createModelTransformation(final Direction entityDirection) {
      return new Transformation((new Matrix4f()).translation(0.5F, 0.0F, 0.5F).rotate(Axis.YP.rotationDegrees(-entityDirection.getOpposite().toYRot())));
   }
}
