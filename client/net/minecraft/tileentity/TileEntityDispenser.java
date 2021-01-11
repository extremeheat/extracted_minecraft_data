package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntityLockable implements IInventory {
   private static final Random field_174913_f = new Random();
   private ItemStack[] field_146022_i = new ItemStack[9];
   protected String field_146020_a;

   public TileEntityDispenser() {
      super();
   }

   public int func_70302_i_() {
      return 9;
   }

   public ItemStack func_70301_a(int var1) {
      return this.field_146022_i[var1];
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_146022_i[var1] != null) {
         ItemStack var3;
         if (this.field_146022_i[var1].field_77994_a <= var2) {
            var3 = this.field_146022_i[var1];
            this.field_146022_i[var1] = null;
            this.func_70296_d();
            return var3;
         } else {
            var3 = this.field_146022_i[var1].func_77979_a(var2);
            if (this.field_146022_i[var1].field_77994_a == 0) {
               this.field_146022_i[var1] = null;
            }

            this.func_70296_d();
            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_146022_i[var1] != null) {
         ItemStack var2 = this.field_146022_i[var1];
         this.field_146022_i[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public int func_146017_i() {
      int var1 = -1;
      int var2 = 1;

      for(int var3 = 0; var3 < this.field_146022_i.length; ++var3) {
         if (this.field_146022_i[var3] != null && field_174913_f.nextInt(var2++) == 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_146022_i[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      this.func_70296_d();
   }

   public int func_146019_a(ItemStack var1) {
      for(int var2 = 0; var2 < this.field_146022_i.length; ++var2) {
         if (this.field_146022_i[var2] == null || this.field_146022_i[var2].func_77973_b() == null) {
            this.func_70299_a(var2, var1);
            return var2;
         }
      }

      return -1;
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_146020_a : "container.dispenser";
   }

   public void func_146018_a(String var1) {
      this.field_146020_a = var1;
   }

   public boolean func_145818_k_() {
      return this.field_146020_a != null;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      NBTTagList var2 = var1.func_150295_c("Items", 10);
      this.field_146022_i = new ItemStack[this.func_70302_i_()];

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         int var5 = var4.func_74771_c("Slot") & 255;
         if (var5 >= 0 && var5 < this.field_146022_i.length) {
            this.field_146022_i[var5] = ItemStack.func_77949_a(var4);
         }
      }

      if (var1.func_150297_b("CustomName", 8)) {
         this.field_146020_a = var1.func_74779_i("CustomName");
      }

   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_146022_i.length; ++var3) {
         if (this.field_146022_i[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var3);
            this.field_146022_i[var3].func_77955_b(var4);
            var2.func_74742_a(var4);
         }
      }

      var1.func_74782_a("Items", var2);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", this.field_146020_a);
      }

   }

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public String func_174875_k() {
      return "minecraft:dispenser";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerDispenser(var1, this);
   }

   public int func_174887_a_(int var1) {
      return 0;
   }

   public void func_174885_b(int var1, int var2) {
   }

   public int func_174890_g() {
      return 0;
   }

   public void func_174888_l() {
      for(int var1 = 0; var1 < this.field_146022_i.length; ++var1) {
         this.field_146022_i[var1] = null;
      }

   }
}
