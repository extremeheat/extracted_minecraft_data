package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryEnderChest extends InventoryBasic {
   private TileEntityEnderChest field_70488_a;

   public InventoryEnderChest() {
      super(new TextComponentTranslation("container.enderchest", new Object[0]), 27);
   }

   public void func_146031_a(TileEntityEnderChest var1) {
      this.field_70488_a = var1;
   }

   public void func_70486_a(NBTTagList var1) {
      int var2;
      for(var2 = 0; var2 < this.func_70302_i_(); ++var2) {
         this.func_70299_a(var2, ItemStack.field_190927_a);
      }

      for(var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         int var4 = var3.func_74771_c("Slot") & 255;
         if (var4 >= 0 && var4 < this.func_70302_i_()) {
            this.func_70299_a(var4, ItemStack.func_199557_a(var3));
         }
      }

   }

   public NBTTagList func_70487_g() {
      NBTTagList var1 = new NBTTagList();

      for(int var2 = 0; var2 < this.func_70302_i_(); ++var2) {
         ItemStack var3 = this.func_70301_a(var2);
         if (!var3.func_190926_b()) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var2);
            var3.func_77955_b(var4);
            var1.add((INBTBase)var4);
         }
      }

      return var1;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      return this.field_70488_a != null && !this.field_70488_a.func_145971_a(var1) ? false : super.func_70300_a(var1);
   }

   public void func_174889_b(EntityPlayer var1) {
      if (this.field_70488_a != null) {
         this.field_70488_a.func_145969_a();
      }

      super.func_174889_b(var1);
   }

   public void func_174886_c(EntityPlayer var1) {
      if (this.field_70488_a != null) {
         this.field_70488_a.func_145970_b();
      }

      super.func_174886_c(var1);
      this.field_70488_a = null;
   }
}
