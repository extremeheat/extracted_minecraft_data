package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneLampBlock extends Block {
   public static final BooleanProperty LIT;

   public RedstoneLampBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(LIT, var1.getLevel().hasNeighborSignal(var1.getClickedPos()));
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = (Boolean)var1.getValue(LIT);
         if (var7 != var2.hasNeighborSignal(var3)) {
            if (var7) {
               var2.getBlockTicks().scheduleTick(var3, this, 4);
            } else {
               var2.setBlock(var3, (BlockState)var1.cycle(LIT), 2);
            }
         }

      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.getValue(LIT) && !var2.hasNeighborSignal(var3)) {
         var2.setBlock(var3, (BlockState)var1.cycle(LIT), 2);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT);
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
