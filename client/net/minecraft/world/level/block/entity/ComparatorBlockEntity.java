package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ComparatorBlockEntity extends BlockEntity {
   private int output;

   public ComparatorBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.COMPARATOR, var1, var2);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putInt("OutputSignal", this.output);
      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.output = var1.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.output;
   }

   public void setOutputSignal(int var1) {
      this.output = var1;
   }
}
