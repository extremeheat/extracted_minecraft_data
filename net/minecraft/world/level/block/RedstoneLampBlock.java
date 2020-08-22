package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneLampBlock extends Block {
   public static final BooleanProperty LIT;

   public RedstoneLampBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
   }

   public int getLightEmission(BlockState var1) {
      return (Boolean)var1.getValue(LIT) ? super.getLightEmission(var1) : 0;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      super.onPlace(var1, var2, var3, var4, var5);
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

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(LIT);
   }

   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType var4) {
      return true;
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
