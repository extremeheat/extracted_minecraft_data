package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

public class CopperBulbBlock extends Block {
   public static final MapCodec<CopperBulbBlock> CODEC = simpleCodec(CopperBulbBlock::new);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty LIT = BlockStateProperties.LIT;

   @Override
   protected MapCodec<? extends CopperBulbBlock> codec() {
      return CODEC;
   }

   public CopperBulbBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var4.getBlock() != var1.getBlock() && var2 instanceof ServerLevel var6) {
         this.checkAndFlip(var1, var6, var3);
      }
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (var2 instanceof ServerLevel var7) {
         this.checkAndFlip(var1, var7, var3);
      }
   }

   public void checkAndFlip(BlockState var1, ServerLevel var2, BlockPos var3) {
      boolean var4 = var2.hasNeighborSignal(var3);
      if (var4 != var1.getValue(POWERED)) {
         BlockState var5 = var1;
         if (!var1.getValue(POWERED)) {
            var5 = var1.cycle(LIT);
            var2.playSound(null, var3, var5.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
         }

         var2.setBlock(var3, var5.setValue(POWERED, Boolean.valueOf(var4)), 3);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT, POWERED);
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return var2.getBlockState(var3).getValue(LIT) ? 15 : 0;
   }
}
