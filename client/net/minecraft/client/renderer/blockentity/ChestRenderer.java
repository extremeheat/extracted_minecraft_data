package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.SpecialDates;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T, ChestRenderState> {
   public static final MultiblockChestResources<ModelLayerLocation> LAYERS;
   private static final Map<Direction, Transformation> TRANSFORMATIONS;
   private final SpriteGetter sprites;
   private final MultiblockChestResources<ChestModel> models;
   private final boolean xmasTextures;

   public ChestRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      this.sprites = context.sprites();
      this.xmasTextures = xmasTextures();
      this.models = LAYERS.<ChestModel>map((layer) -> new ChestModel(context.bakeLayer(layer)));
   }

   public static boolean xmasTextures() {
      return SpecialDates.isExtendedChristmas();
   }

   public ChestRenderState createRenderState() {
      return new ChestRenderState();
   }

   public void extractRenderState(final T blockEntity, final ChestRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combineResult;
      label30: {
         BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
         boolean hasLevel = blockEntity.getLevel() != null;
         BlockState blockState = hasLevel ? blockEntity.getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
         state.type = blockState.hasProperty(ChestBlock.TYPE) ? (ChestType)blockState.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
         state.facing = (Direction)blockState.getValue(ChestBlock.FACING);
         state.material = getChestMaterial(blockEntity, this.xmasTextures);
         if (hasLevel) {
            Block var10 = blockState.getBlock();
            if (var10 instanceof ChestBlock) {
               ChestBlock chestBlock = (ChestBlock)var10;
               combineResult = chestBlock.combine(blockState, blockEntity.getLevel(), blockEntity.getBlockPos(), true);
               break label30;
            }
         }

         combineResult = DoubleBlockCombiner.Combiner::acceptNone;
      }

      state.open = ((Float2FloatFunction)combineResult.apply(ChestBlock.opennessCombiner(blockEntity))).get(partialTicks);
      if (state.type != ChestType.SINGLE) {
         state.lightCoords = ((Int2IntFunction)combineResult.apply(new BrightnessCombiner())).applyAsInt(state.lightCoords);
      }

   }

   public void submit(final ChestRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      poseStack.mulPose(modelTransformation(state.facing));
      float open = state.open;
      open = 1.0F - open;
      open = 1.0F - open * open * open;
      SpriteId spriteId = Sheets.chooseSprite(state.material, state.type);
      ChestModel model = this.models.select(state.type);
      submitNodeCollector.submitModel(model, open, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, spriteId, this.sprites, 0, state.breakProgress);
      poseStack.popPose();
   }

   private static ChestRenderState.ChestMaterialType getChestMaterial(final BlockEntity entity, final boolean xmasTextures) {
      Block var3 = entity.getBlockState().getBlock();
      if (var3 instanceof CopperChestBlock) {
         CopperChestBlock copperChestBlock = (CopperChestBlock)var3;
         ChestRenderState.ChestMaterialType var10000;
         switch (copperChestBlock.getState()) {
            case UNAFFECTED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_UNAFFECTED;
            case EXPOSED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_EXPOSED;
            case WEATHERED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_WEATHERED;
            case OXIDIZED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_OXIDIZED;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      } else if (entity instanceof EnderChestBlockEntity) {
         return ChestRenderState.ChestMaterialType.ENDER_CHEST;
      } else if (xmasTextures) {
         return ChestRenderState.ChestMaterialType.CHRISTMAS;
      } else {
         return entity instanceof TrappedChestBlockEntity ? ChestRenderState.ChestMaterialType.TRAPPED : ChestRenderState.ChestMaterialType.REGULAR;
      }
   }

   public static Transformation modelTransformation(final Direction facing) {
      return (Transformation)TRANSFORMATIONS.get(facing);
   }

   private static Transformation createModelTransformation(final Direction facing) {
      return new Transformation((new Matrix4f()).rotationAround(Axis.YP.rotationDegrees(-facing.toYRot()), 0.5F, 0.0F, 0.5F));
   }

   static {
      LAYERS = new MultiblockChestResources<ModelLayerLocation>(ModelLayers.CHEST, ModelLayers.DOUBLE_CHEST_LEFT, ModelLayers.DOUBLE_CHEST_RIGHT);
      TRANSFORMATIONS = Util.<Direction, Transformation>makeEnumMap(Direction.class, ChestRenderer::createModelTransformation);
   }
}
