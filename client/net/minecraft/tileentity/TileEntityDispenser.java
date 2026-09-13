package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntity implements IInventory {
   private ItemStack[] field_146022_i = new ItemStack[9];
   private Random field_146021_j = new Random();
   protected String field_146020_a;

   public TileEntityDispenser() {
      super();
   }

   @Override
   public int func_70302_i_() {
      return 9;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return this.field_146022_i[var1];
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_146022_i[var1] != null) {
         if (this.field_146022_i[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_146022_i[var1];
            this.field_146022_i[var1] = null;
            this.func_70296_d();
            return var4;
         } else {
            ItemStack var3 = this.field_146022_i[var1].func_77979_a(var2);
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

   @Override
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
         if (this.field_146022_i[var3] != null && this.field_146021_j.nextInt(var2++) == 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   @Override
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

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.field_146020_a : "container.dispenser";
   }

   public void func_146018_a(String var1) {
      this.field_146020_a = var1;
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_146020_a != null;
   }

   @Override
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

   @Override
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

   @Override
   public int func_70297_j_() {
      return 64;
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this) {
         return false;
      } else {
         return !(var1.func_70092_e((double)this.field_145851_c + 0.5, (double)this.field_145848_d + 0.5, (double)this.field_145849_e + 0.5) > 64.0);
      }
   }

   @Override
   public void func_70295_k_() {
   }

   @Override
   public void func_70305_f() {
   }

   @Override
   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }
}
