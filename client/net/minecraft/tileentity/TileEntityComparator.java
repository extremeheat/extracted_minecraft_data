package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityComparator extends TileEntity {
   private int field_145997_a;

   public TileEntityComparator() {
      super();
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74768_a("OutputSignal", this.field_145997_a);
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145997_a = var1.func_74762_e("OutputSignal");
   }

   public int func_145996_a() {
      return this.field_145997_a;
   }

   public void func_145995_a(int var1) {
      this.field_145997_a = var1;
   }
}
