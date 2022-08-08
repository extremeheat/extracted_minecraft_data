package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
   private ItemStack record;

   public JukeboxBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.JUKEBOX, var1, var2);
      this.record = ItemStack.EMPTY;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("RecordItem", 10)) {
         this.setRecord(ItemStack.of(var1.getCompound("RecordItem")));
      }

   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (!this.getRecord().isEmpty()) {
         var1.put("RecordItem", this.getRecord().save(new CompoundTag()));
      }

   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack var1) {
      this.record = var1;
      this.setChanged();
   }

   public void clearContent() {
      this.setRecord(ItemStack.EMPTY);
   }
}
