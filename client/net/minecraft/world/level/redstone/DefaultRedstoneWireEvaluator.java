package net.minecraft.world.level.redstone;

import com.google.common.collect.Sets;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DefaultRedstoneWireEvaluator extends RedstoneWireEvaluator {
   public DefaultRedstoneWireEvaluator(RedStoneWireBlock var1) {
      super(var1);
   }

   @Override
   public void updatePowerStrength(Level var1, BlockPos var2, BlockState var3, @Nullable Orientation var4) {
      int var5 = this.calculateTargetStrength(var1, var2);
      if (var3.getValue(RedStoneWireBlock.POWER) != var5) {
         if (var1.getBlockState(var2) == var3) {
            var1.setBlock(var2, var3.setValue(RedStoneWireBlock.POWER, Integer.valueOf(var5)), 2);
         }

         HashSet var6 = Sets.newHashSet();
         var6.add(var2);

         for (Direction var10 : Direction.values()) {
            var6.add(var2.relative(var10));
         }

         for (BlockPos var12 : var6) {
            var1.updateNeighborsAt(var12, this.wireBlock);
         }
      }
   }

   private int calculateTargetStrength(Level var1, BlockPos var2) {
      int var3 = this.getBlockSignal(var1, var2);
      return var3 == 15 ? var3 : Math.max(var3, this.getIncomingWireSignal(var1, var2));
   }
}
