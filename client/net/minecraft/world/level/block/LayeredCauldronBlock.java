package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
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

public class LayeredCauldronBlock extends AbstractCauldronBlock {
   public static final int MIN_FILL_LEVEL = 1;
   public static final int MAX_FILL_LEVEL = 3;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
   private static final int BASE_CONTENT_HEIGHT = 6;
   private static final double HEIGHT_PER_LEVEL = 3.0;
   public static final Predicate<Biome.Precipitation> RAIN = var0 -> var0 == Biome.Precipitation.RAIN;
   public static final Predicate<Biome.Precipitation> SNOW = var0 -> var0 == Biome.Precipitation.SNOW;
   private final Predicate<Biome.Precipitation> fillPredicate;

   public LayeredCauldronBlock(BlockBehaviour.Properties var1, Predicate<Biome.Precipitation> var2, Map<Item, CauldronInteraction> var3) {
      super(var1, var3);
      this.fillPredicate = var2;
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(1)));
   }

   @Override
   public boolean isFull(BlockState var1) {
      return var1.getValue(LEVEL) == 3;
   }

   @Override
   protected boolean canReceiveStalactiteDrip(Fluid var1) {
      return var1 == Fluids.WATER && this.fillPredicate == RAIN;
   }

   @Override
   protected double getContentHeight(BlockState var1) {
      return (6.0 + (double)var1.getValue(LEVEL).intValue() * 3.0) / 16.0;
   }

   @Override
   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide && var4.isOnFire() && this.isEntityInsideContent(var1, var3, var4)) {
         var4.clearFire();
         if (var4.mayInteract(var2, var3)) {
            this.handleEntityOnFireInside(var1, var2, var3);
         }
      }
   }

   protected void handleEntityOnFireInside(BlockState var1, Level var2, BlockPos var3) {
      lowerFillLevel(var1, var2, var3);
   }

   public static void lowerFillLevel(BlockState var0, Level var1, BlockPos var2) {
      int var3 = var0.getValue(LEVEL) - 1;
      BlockState var4 = var3 == 0 ? Blocks.CAULDRON.defaultBlockState() : var0.setValue(LEVEL, Integer.valueOf(var3));
      var1.setBlockAndUpdate(var2, var4);
      var1.gameEvent(GameEvent.BLOCK_CHANGE, var2, GameEvent.Context.of(var4));
   }

   @Override
   public void handlePrecipitation(BlockState var1, Level var2, BlockPos var3, Biome.Precipitation var4) {
      if (CauldronBlock.shouldHandlePrecipitation(var2, var4) && var1.getValue(LEVEL) != 3 && this.fillPredicate.test(var4)) {
         BlockState var5 = var1.cycle(LEVEL);
         var2.setBlockAndUpdate(var3, var5);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var5));
      }
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return var1.getValue(LEVEL);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   @Override
   protected void receiveStalactiteDrip(BlockState var1, Level var2, BlockPos var3, Fluid var4) {
      if (!this.isFull(var1)) {
         BlockState var5 = var1.setValue(LEVEL, Integer.valueOf(var1.getValue(LEVEL) + 1));
         var2.setBlockAndUpdate(var3, var5);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var5));
         var2.levelEvent(1047, var3, 0);
      }
   }
}
