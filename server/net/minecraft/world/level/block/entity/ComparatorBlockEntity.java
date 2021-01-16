package net.minecraft.world.level.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ComparatorBlockEntity extends BlockEntity {
   private int output;

   public ComparatorBlockEntity() {
      super(BlockEntityType.COMPARATOR);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putInt("OutputSignal", this.output);
      return var1;
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      this.output = var2.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.output;
   }

   public void setOutputSignal(int var1) {
      this.output = var1;
   }
}
