package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FrostedIceBlock extends IceBlock {
   public static final MapCodec<FrostedIceBlock> CODEC = simpleCodec(FrostedIceBlock::new);
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final int NEIGHBORS_TO_AGE = 4;
   private static final int NEIGHBORS_TO_MELT = 2;

   @Override
   public MapCodec<FrostedIceBlock> codec() {
      return CODEC;
   }

   public FrostedIceBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.tick(var1, var2, var3, var4);
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((var4.nextInt(3) == 0 || this.fewerNeigboursThan(var2, var3, 4))
         && var2.getMaxLocalRawBrightness(var3) > 11 - var1.getValue(AGE) - var1.getLightBlock(var2, var3)
         && this.slightlyMelt(var1, var2, var3)) {
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(Direction var9 : Direction.values()) {
            var5.setWithOffset(var3, var9);
            BlockState var10 = var2.getBlockState(var5);
            if (var10.is(this) && !this.slightlyMelt(var10, var2, var5)) {
               var2.scheduleTick(var5, this, Mth.nextInt(var4, 20, 40));
            }
         }
      } else {
         var2.scheduleTick(var3, this, Mth.nextInt(var4, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState var1, Level var2, BlockPos var3) {
      int var4 = var1.getValue(AGE);
      if (var4 < 3) {
         var2.setBlock(var3, var1.setValue(AGE, Integer.valueOf(var4 + 1)), 2);
         return false;
      } else {
         this.melt(var1, var2, var3);
         return true;
      }
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var4.defaultBlockState().is(this) && this.fewerNeigboursThan(var2, var3, 2)) {
         this.melt(var1, var2, var3);
      }

      super.neighborChanged(var1, var2, var3, var4, var5, var6);
   }

   private boolean fewerNeigboursThan(BlockGetter var1, BlockPos var2, int var3) {
      int var4 = 0;
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(Direction var9 : Direction.values()) {
         var5.setWithOffset(var2, var9);
         if (var1.getBlockState(var5).is(this)) {
            if (++var4 >= var3) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }
}
