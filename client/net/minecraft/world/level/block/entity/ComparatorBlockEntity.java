package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ComparatorBlockEntity extends BlockEntity {
   private static final int DEFAULT_OUTPUT = 0;
   private int output = 0;

   public ComparatorBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.COMPARATOR, var1, var2);
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      var1.putInt("OutputSignal", this.output);
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.output = var1.getIntOr("OutputSignal", 0);
   }

   public int getOutputSignal() {
      return this.output;
   }

   public void setOutputSignal(int var1) {
      this.output = var1;
   }
}
