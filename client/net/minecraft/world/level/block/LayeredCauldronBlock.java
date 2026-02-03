package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LayeredCauldronBlock extends AbstractCauldronBlock {
   public static final MapCodec<LayeredCauldronBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter((b) -> b.precipitationType), CauldronInteraction.CODEC.fieldOf("interactions").forGetter((b) -> b.interactions), propertiesCodec()).apply(i, LayeredCauldronBlock::new));
   public static final int MIN_FILL_LEVEL = 1;
   public static final int MAX_FILL_LEVEL = 3;
   public static final IntegerProperty LEVEL;
   private static final int BASE_CONTENT_HEIGHT = 6;
   private static final double HEIGHT_PER_LEVEL = 3.0;
   private static final VoxelShape[] FILLED_SHAPES;
   private final Biome.Precipitation precipitationType;

   public MapCodec<LayeredCauldronBlock> codec() {
      return CODEC;
   }

   public LayeredCauldronBlock(final Biome.Precipitation precipitationType, final CauldronInteraction.InteractionMap interactionMap, final BlockBehaviour.Properties properties) {
      super(properties, interactionMap);
      this.precipitationType = precipitationType;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 1));
   }

   public boolean isFull(final BlockState state) {
      return (Integer)state.getValue(LEVEL) == 3;
   }

   protected boolean canReceiveStalactiteDrip(final Fluid fluid) {
      return fluid == Fluids.WATER && this.precipitationType == Biome.Precipitation.RAIN;
   }

   protected double getContentHeight(final BlockState state) {
      return getPixelContentHeight((Integer)state.getValue(LEVEL)) / 16.0;
   }

   private static double getPixelContentHeight(final int level) {
      return 6.0 + (double)level * 3.0;
   }

   protected VoxelShape getEntityInsideCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final Entity entity) {
      return FILLED_SHAPES[(Integer)state.getValue(LEVEL) - 1];
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (level instanceof ServerLevel serverLevel) {
         BlockPos blockPos = pos.immutable();
         effectApplier.runBefore(InsideBlockEffectType.EXTINGUISH, (e) -> {
            if (e.isOnFire() && e.mayInteract(serverLevel, blockPos)) {
               this.handleEntityOnFireInside(state, level, blockPos);
            }

         });
      }

      effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
   }

   private void handleEntityOnFireInside(final BlockState state, final Level level, final BlockPos pos) {
      if (this.precipitationType == Biome.Precipitation.SNOW) {
         lowerFillLevel((BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, (Integer)state.getValue(LEVEL)), level, pos);
      } else {
         lowerFillLevel(state, level, pos);
      }

   }

   public static void lowerFillLevel(final BlockState state, final Level level, final BlockPos pos) {
      int newLevel = (Integer)state.getValue(LEVEL) - 1;
      BlockState newState = newLevel == 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState)state.setValue(LEVEL, newLevel);
      level.setBlockAndUpdate(pos, newState);
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
   }

   public void handlePrecipitation(final BlockState state, final Level level, final BlockPos pos, final Biome.Precipitation precipitation) {
      if (CauldronBlock.shouldHandlePrecipitation(level, precipitation) && (Integer)state.getValue(LEVEL) != 3 && precipitation == this.precipitationType) {
         BlockState newState = (BlockState)state.cycle(LEVEL);
         level.setBlockAndUpdate(pos, newState);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
      }
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return (Integer)state.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(LEVEL);
   }

   protected void receiveStalactiteDrip(final BlockState state, final Level level, final BlockPos pos, final Fluid fluid) {
      if (!this.isFull(state)) {
         BlockState newState = (BlockState)state.setValue(LEVEL, (Integer)state.getValue(LEVEL) + 1);
         level.setBlockAndUpdate(pos, newState);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
         level.levelEvent(1047, pos, 0);
      }
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_CAULDRON;
      FILLED_SHAPES = (VoxelShape[])Util.make(() -> Block.boxes(2, (level) -> Shapes.or(AbstractCauldronBlock.SHAPE, Block.column(12.0, 4.0, getPixelContentHeight(level + 1)))));
   }
}
