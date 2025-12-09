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
   public static final MapCodec<LayeredCauldronBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter((var0x) -> var0x.precipitationType), CauldronInteraction.CODEC.fieldOf("interactions").forGetter((var0x) -> var0x.interactions), propertiesCodec()).apply(var0, LayeredCauldronBlock::new));
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

   public LayeredCauldronBlock(Biome.Precipitation var1, CauldronInteraction.InteractionMap var2, BlockBehaviour.Properties var3) {
      super(var3, var2);
      this.precipitationType = var1;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 1));
   }

   public boolean isFull(BlockState var1) {
      return (Integer)var1.getValue(LEVEL) == 3;
   }

   protected boolean canReceiveStalactiteDrip(Fluid var1) {
      return var1 == Fluids.WATER && this.precipitationType == Biome.Precipitation.RAIN;
   }

   protected double getContentHeight(BlockState var1) {
      return getPixelContentHeight((Integer)var1.getValue(LEVEL)) / 16.0;
   }

   private static double getPixelContentHeight(int var0) {
      return 6.0 + (double)var0 * 3.0;
   }

   protected VoxelShape getEntityInsideCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, Entity var4) {
      return FILLED_SHAPES[(Integer)var1.getValue(LEVEL) - 1];
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4, InsideBlockEffectApplier var5, boolean var6) {
      if (var2 instanceof ServerLevel var7) {
         BlockPos var8 = var3.immutable();
         var5.runBefore(InsideBlockEffectType.EXTINGUISH, (var5x) -> {
            if (var5x.isOnFire() && var5x.mayInteract(var7, var8)) {
               this.handleEntityOnFireInside(var1, var2, var8);
            }

         });
      }

      var5.apply(InsideBlockEffectType.EXTINGUISH);
   }

   private void handleEntityOnFireInside(BlockState var1, Level var2, BlockPos var3) {
      if (this.precipitationType == Biome.Precipitation.SNOW) {
         lowerFillLevel((BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, (Integer)var1.getValue(LEVEL)), var2, var3);
      } else {
         lowerFillLevel(var1, var2, var3);
      }

   }

   public static void lowerFillLevel(BlockState var0, Level var1, BlockPos var2) {
      int var3 = (Integer)var0.getValue(LEVEL) - 1;
      BlockState var4 = var3 == 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState)var0.setValue(LEVEL, var3);
      var1.setBlockAndUpdate(var2, var4);
      var1.gameEvent(GameEvent.BLOCK_CHANGE, var2, GameEvent.Context.of(var4));
   }

   public void handlePrecipitation(BlockState var1, Level var2, BlockPos var3, Biome.Precipitation var4) {
      if (CauldronBlock.shouldHandlePrecipitation(var2, var4) && (Integer)var1.getValue(LEVEL) != 3 && var4 == this.precipitationType) {
         BlockState var5 = (BlockState)var1.cycle(LEVEL);
         var2.setBlockAndUpdate(var3, var5);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var5));
      }
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      return (Integer)var1.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   protected void receiveStalactiteDrip(BlockState var1, Level var2, BlockPos var3, Fluid var4) {
      if (!this.isFull(var1)) {
         BlockState var5 = (BlockState)var1.setValue(LEVEL, (Integer)var1.getValue(LEVEL) + 1);
         var2.setBlockAndUpdate(var3, var5);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var5));
         var2.levelEvent(1047, var3, 0);
      }
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_CAULDRON;
      FILLED_SHAPES = (VoxelShape[])Util.make(() -> Block.boxes(2, (var0) -> Shapes.or(AbstractCauldronBlock.SHAPE, Block.column(12.0, 4.0, getPixelContentHeight(var0 + 1)))));
   }
}
