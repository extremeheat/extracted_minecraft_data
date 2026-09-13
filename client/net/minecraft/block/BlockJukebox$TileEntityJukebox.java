package net.minecraft.block;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class BlockJukebox$TileEntityJukebox extends TileEntity {
   private ItemStack field_145858_a;

   public BlockJukebox$TileEntityJukebox() {
      super();
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_150297_b("RecordItem", 10)) {
         this.func_145857_a(ItemStack.func_77949_a(var1.func_74775_l("RecordItem")));
      } else if (var1.func_74762_e("Record") > 0) {
         this.func_145857_a(new ItemStack(Item.func_150899_d(var1.func_74762_e("Record")), 1, 0));
      }
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      if (this.func_145856_a() != null) {
         var1.func_74782_a("RecordItem", this.func_145856_a().func_77955_b(new NBTTagCompound()));
         var1.func_74768_a("Record", Item.func_150891_b(this.func_145856_a().func_77973_b()));
      }
   }

   public ItemStack func_145856_a() {
      return this.field_145858_a;
   }

   public void func_145857_a(ItemStack var1) {
      this.field_145858_a = var1;
      this.func_70296_d();
   }
}
