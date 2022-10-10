package net.minecraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityJukebox extends TileEntity {
   private ItemStack field_195538_a;

   public TileEntityJukebox() {
      super(TileEntityType.field_200975_f);
      this.field_195538_a = ItemStack.field_190927_a;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_150297_b("RecordItem", 10)) {
         this.func_195535_a(ItemStack.func_199557_a(var1.func_74775_l("RecordItem")));
      }

   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (!this.func_195537_c().func_190926_b()) {
         var1.func_74782_a("RecordItem", this.func_195537_c().func_77955_b(new NBTTagCompound()));
      }

      return var1;
   }

   public ItemStack func_195537_c() {
      return this.field_195538_a;
   }

   public void func_195535_a(ItemStack var1) {
      this.field_195538_a = var1;
      this.func_70296_d();
   }
}
