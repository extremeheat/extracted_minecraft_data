package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.skull.DragonHeadModel;
import net.minecraft.client.model.object.skull.PiglinHeadModel;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SkullBlockRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockRenderState> {
   private final Function<SkullBlock.Type, SkullModelBase> modelByType;
   private static final Map<SkullBlock.Type, Identifier> SKIN_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put(SkullBlock.Types.SKELETON, Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png"));
      map.put(SkullBlock.Types.WITHER_SKELETON, Identifier.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png"));
      map.put(SkullBlock.Types.ZOMBIE, Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png"));
      map.put(SkullBlock.Types.CREEPER, Identifier.withDefaultNamespace("textures/entity/creeper/creeper.png"));
      map.put(SkullBlock.Types.DRAGON, Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon.png"));
      map.put(SkullBlock.Types.PIGLIN, Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png"));
      map.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultTexture());
   });
   private final PlayerSkinRenderCache playerSkinRenderCache;

   public static @Nullable SkullModelBase createModel(final EntityModelSet modelSet, final SkullBlock.Type type) {
      if (type instanceof SkullBlock.Types) {
         SkullBlock.Types vanillaType = (SkullBlock.Types)type;
         Object var10000;
         switch (vanillaType) {
            case SKELETON -> var10000 = new SkullModel(modelSet.bakeLayer(ModelLayers.SKELETON_SKULL));
            case WITHER_SKELETON -> var10000 = new SkullModel(modelSet.bakeLayer(ModelLayers.WITHER_SKELETON_SKULL));
            case PLAYER -> var10000 = new SkullModel(modelSet.bakeLayer(ModelLayers.PLAYER_HEAD));
            case ZOMBIE -> var10000 = new SkullModel(modelSet.bakeLayer(ModelLayers.ZOMBIE_HEAD));
            case CREEPER -> var10000 = new SkullModel(modelSet.bakeLayer(ModelLayers.CREEPER_HEAD));
            case DRAGON -> var10000 = new DragonHeadModel(modelSet.bakeLayer(ModelLayers.DRAGON_SKULL));
            case PIGLIN -> var10000 = new PiglinHeadModel(modelSet.bakeLayer(ModelLayers.PIGLIN_HEAD));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return (SkullModelBase)var10000;
      } else {
         return null;
      }
   }

   public SkullBlockRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      EntityModelSet modelSet = context.entityModelSet();
      this.playerSkinRenderCache = context.playerSkinRenderCache();
      this.modelByType = Util.memoize((Function)((type) -> createModel(modelSet, type)));
   }

   public SkullBlockRenderState createRenderState() {
      return new SkullBlockRenderState();
   }

   public void extractRenderState(final SkullBlockEntity blockEntity, final SkullBlockRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.animationProgress = blockEntity.getAnimation(partialTicks);
      BlockState blockState = blockEntity.getBlockState();
      boolean isWallSkull = blockState.getBlock() instanceof WallSkullBlock;
      state.direction = isWallSkull ? (Direction)blockState.getValue(WallSkullBlock.FACING) : null;
      int rotationSegment = isWallSkull ? RotationSegment.convertToSegment(state.direction.getOpposite()) : (Integer)blockState.getValue(SkullBlock.ROTATION);
      state.rotationDegrees = RotationSegment.convertToDegrees(rotationSegment);
      state.skullType = ((AbstractSkullBlock)blockState.getBlock()).getType();
      state.renderType = this.resolveSkullRenderType(state.skullType, blockEntity);
   }

   public void submit(final SkullBlockRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      SkullModelBase model = (SkullModelBase)this.modelByType.apply(state.skullType);
      submitSkull(state.direction, state.rotationDegrees, state.animationProgress, poseStack, submitNodeCollector, state.lightCoords, model, state.renderType, 0, state.breakProgress);
   }

   public static void submitSkull(final @Nullable Direction direction, final float rot, final float animationValue, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final SkullModelBase model, final RenderType renderType, final int outlineColor, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      poseStack.pushPose();
      if (direction == null) {
         poseStack.translate(0.5F, 0.0F, 0.5F);
      } else {
         float offset = 0.25F;
         poseStack.translate(0.5F - (float)direction.getStepX() * 0.25F, 0.25F, 0.5F - (float)direction.getStepZ() * 0.25F);
      }

      poseStack.scale(-1.0F, -1.0F, 1.0F);
      SkullModelBase.State modelState = new SkullModelBase.State();
      modelState.animationPos = animationValue;
      modelState.yRot = rot;
      submitNodeCollector.submitModel(model, modelState, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, outlineColor, breakProgress);
      poseStack.popPose();
   }

   private RenderType resolveSkullRenderType(final SkullBlock.Type type, final SkullBlockEntity entity) {
      if (type == SkullBlock.Types.PLAYER) {
         ResolvableProfile ownerProfile = entity.getOwnerProfile();
         if (ownerProfile != null) {
            return this.playerSkinRenderCache.getOrDefault(ownerProfile).renderType();
         }
      }

      return getSkullRenderType(type, (Identifier)null);
   }

   public static RenderType getSkullRenderType(final SkullBlock.Type type, final @Nullable Identifier texture) {
      return RenderTypes.entityCutoutZOffset(texture != null ? texture : (Identifier)SKIN_BY_TYPE.get(type));
   }

   public static RenderType getPlayerSkinRenderType(final Identifier texture) {
      return RenderTypes.entityTranslucent(texture);
   }
}
