package net.minecraft.entity.player;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory {
   public ItemStack[] field_70462_a = new ItemStack[36];
   public ItemStack[] field_70460_b = new ItemStack[4];
   public int field_70461_c;
   private ItemStack field_70456_f;
   public EntityPlayer field_70458_d;
   private ItemStack field_70457_g;
   public boolean field_70459_e;

   public InventoryPlayer(EntityPlayer var1) {
      super();
      this.field_70458_d = var1;
   }

   public ItemStack func_70448_g() {
      return this.field_70461_c < 9 && this.field_70461_c >= 0 ? this.field_70462_a[this.field_70461_c] : null;
   }

   public static int func_70451_h() {
      return 9;
   }

   private int func_146029_c(Item var1) {
      for(int var2 = 0; var2 < this.field_70462_a.length; ++var2) {
         if (this.field_70462_a[var2] != null && this.field_70462_a[var2].func_77973_b() == var1) {
            return var2;
         }
      }

      return -1;
   }

   private int func_146024_c(Item var1, int var2) {
      for(int var3 = 0; var3 < this.field_70462_a.length; ++var3) {
         if (this.field_70462_a[var3] != null && this.field_70462_a[var3].func_77973_b() == var1 && this.field_70462_a[var3].func_77960_j() == var2) {
            return var3;
         }
      }

      return -1;
   }

   private int func_70432_d(ItemStack var1) {
      for(int var2 = 0; var2 < this.field_70462_a.length; ++var2) {
         if (this.field_70462_a[var2] != null
            && this.field_70462_a[var2].func_77973_b() == var1.func_77973_b()
            && this.field_70462_a[var2].func_77985_e()
            && this.field_70462_a[var2].field_77994_a < this.field_70462_a[var2].func_77976_d()
            && this.field_70462_a[var2].field_77994_a < this.func_70297_j_()
            && (!this.field_70462_a[var2].func_77981_g() || this.field_70462_a[var2].func_77960_j() == var1.func_77960_j())
            && ItemStack.func_77970_a(this.field_70462_a[var2], var1)) {
            return var2;
         }
      }

      return -1;
   }

   public int func_70447_i() {
      for(int var1 = 0; var1 < this.field_70462_a.length; ++var1) {
         if (this.field_70462_a[var1] == null) {
            return var1;
         }
      }

      return -1;
   }

   public void func_146030_a(Item var1, int var2, boolean var3, boolean var4) {
      int var5 = -1;
      this.field_70456_f = this.func_70448_g();
      if (var3) {
         var5 = this.func_146024_c(var1, var2);
      } else {
         var5 = this.func_146029_c(var1);
      }

      if (var5 >= 0 && var5 < 9) {
         this.field_70461_c = var5;
      } else {
         if (var4 && var1 != null) {
            int var6 = this.func_70447_i();
            if (var6 >= 0 && var6 < 9) {
               this.field_70461_c = var6;
            }

            this.func_70439_a(var1, var2);
         }
      }
   }

   public void func_70453_c(int var1) {
      if (var1 > 0) {
         var1 = 1;
      }

      if (var1 < 0) {
         var1 = -1;
      }

      this.field_70461_c -= var1;

      while(this.field_70461_c < 0) {
         this.field_70461_c += 9;
      }

      while(this.field_70461_c >= 9) {
         this.field_70461_c -= 9;
      }
   }

   public int func_146027_a(Item var1, int var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.field_70462_a.length; ++var4) {
         ItemStack var5 = this.field_70462_a[var4];
         if (var5 != null && (var1 == null || var5.func_77973_b() == var1) && (var2 <= -1 || var5.func_77960_j() == var2)) {
            var3 += var5.field_77994_a;
            this.field_70462_a[var4] = null;
         }
      }

      for(int var6 = 0; var6 < this.field_70460_b.length; ++var6) {
         ItemStack var7 = this.field_70460_b[var6];
         if (var7 != null && (var1 == null || var7.func_77973_b() == var1) && (var2 <= -1 || var7.func_77960_j() == var2)) {
            var3 += var7.field_77994_a;
            this.field_70460_b[var6] = null;
         }
      }

      if (this.field_70457_g != null) {
         if (var1 != null && this.field_70457_g.func_77973_b() != var1) {
            return var3;
         }

         if (var2 > -1 && this.field_70457_g.func_77960_j() != var2) {
            return var3;
         }

         var3 += this.field_70457_g.field_77994_a;
         this.func_70437_b(null);
      }

      return var3;
   }

   public void func_70439_a(Item var1, int var2) {
      if (var1 != null) {
         if (this.field_70456_f != null
            && this.field_70456_f.func_77956_u()
            && this.func_146024_c(this.field_70456_f.func_77973_b(), this.field_70456_f.func_77952_i()) == this.field_70461_c) {
            return;
         }

         int var3 = this.func_146024_c(var1, var2);
         if (var3 >= 0) {
            int var4 = this.field_70462_a[var3].field_77994_a;
            this.field_70462_a[var3] = this.field_70462_a[this.field_70461_c];
            this.field_70462_a[this.field_70461_c] = new ItemStack(var1, var4, var2);
         } else {
            this.field_70462_a[this.field_70461_c] = new ItemStack(var1, 1, var2);
         }
      }
   }

   private int func_70452_e(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      int var3 = var1.field_77994_a;
      if (var1.func_77976_d() == 1) {
         int var7 = this.func_70447_i();
         if (var7 < 0) {
            return var3;
         } else {
            if (this.field_70462_a[var7] == null) {
               this.field_70462_a[var7] = ItemStack.func_77944_b(var1);
            }

            return 0;
         }
      } else {
         int var4 = this.func_70432_d(var1);
         if (var4 < 0) {
            var4 = this.func_70447_i();
         }

         if (var4 < 0) {
            return var3;
         } else {
            if (this.field_70462_a[var4] == null) {
               this.field_70462_a[var4] = new ItemStack(var2, 0, var1.func_77960_j());
               if (var1.func_77942_o()) {
                  this.field_70462_a[var4].func_77982_d((NBTTagCompound)var1.func_77978_p().func_74737_b());
               }
            }

            int var5 = var3;
            if (var3 > this.field_70462_a[var4].func_77976_d() - this.field_70462_a[var4].field_77994_a) {
               var5 = this.field_70462_a[var4].func_77976_d() - this.field_70462_a[var4].field_77994_a;
            }

            if (var5 > this.func_70297_j_() - this.field_70462_a[var4].field_77994_a) {
               var5 = this.func_70297_j_() - this.field_70462_a[var4].field_77994_a;
            }

            if (var5 == 0) {
               return var3;
            } else {
               var3 -= var5;
               this.field_70462_a[var4].field_77994_a += var5;
               this.field_70462_a[var4].field_77992_b = 5;
               return var3;
            }
         }
      }
   }

   public void func_70429_k() {
      for(int var1 = 0; var1 < this.field_70462_a.length; ++var1) {
         if (this.field_70462_a[var1] != null) {
            this.field_70462_a[var1].func_77945_a(this.field_70458_d.field_70170_p, this.field_70458_d, var1, this.field_70461_c == var1);
         }
      }
   }

   public boolean func_146026_a(Item var1) {
      int var2 = this.func_146029_c(var1);
      if (var2 < 0) {
         return false;
      } else {
         if (--this.field_70462_a[var2].field_77994_a <= 0) {
            this.field_70462_a[var2] = null;
         }

         return true;
      }
   }

   public boolean func_146028_b(Item var1) {
      int var2 = this.func_146029_c(var1);
      return var2 >= 0;
   }

   public boolean func_70441_a(ItemStack var1) {
      if (var1 != null && var1.field_77994_a != 0 && var1.func_77973_b() != null) {
         try {
            if (var1.func_77951_h()) {
               int var6 = this.func_70447_i();
               if (var6 >= 0) {
                  this.field_70462_a[var6] = ItemStack.func_77944_b(var1);
                  this.field_70462_a[var6].field_77992_b = 5;
                  var1.field_77994_a = 0;
                  return true;
               } else if (this.field_70458_d.field_71075_bZ.field_75098_d) {
                  var1.field_77994_a = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               int var2;
               do {
                  var2 = var1.field_77994_a;
                  var1.field_77994_a = this.func_70452_e(var1);
               } while(var1.field_77994_a > 0 && var1.field_77994_a < var2);

               if (var1.field_77994_a == var2 && this.field_70458_d.field_71075_bZ.field_75098_d) {
                  var1.field_77994_a = 0;
                  return true;
               } else {
                  return var1.field_77994_a < var2;
               }
            }
         } catch (Throwable var5) {
            CrashReport var3 = CrashReport.func_85055_a(var5, "Adding item to inventory");
            CrashReportCategory var4 = var3.func_85058_a("Item being added");
            var4.func_71507_a("Item ID", Item.func_150891_b(var1.func_77973_b()));
            var4.func_71507_a("Item data", var1.func_77960_j());
            var4.func_71500_a("Item name", new InventoryPlayer$1(this, var1));
            throw new ReportedException(var3);
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      ItemStack[] var3 = this.field_70462_a;
      if (var1 >= this.field_70462_a.length) {
         var3 = this.field_70460_b;
         var1 -= this.field_70462_a.length;
      }

      if (var3[var1] != null) {
         if (var3[var1].field_77994_a <= var2) {
            ItemStack var5 = var3[var1];
            var3[var1] = null;
            return var5;
         } else {
            ItemStack var4 = var3[var1].func_77979_a(var2);
            if (var3[var1].field_77994_a == 0) {
               var3[var1] = null;
            }

            return var4;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      ItemStack[] var2 = this.field_70462_a;
      if (var1 >= this.field_70462_a.length) {
         var2 = this.field_70460_b;
         var1 -= this.field_70462_a.length;
      }

      if (var2[var1] != null) {
         ItemStack var3 = var2[var1];
         var2[var1] = null;
         return var3;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      ItemStack[] var3 = this.field_70462_a;
      if (var1 >= var3.length) {
         var1 -= var3.length;
         var3 = this.field_70460_b;
      }

      var3[var1] = var2;
   }

   public float func_146023_a(Block var1) {
      float var2 = 1.0F;
      if (this.field_70462_a[this.field_70461_c] != null) {
         var2 *= this.field_70462_a[this.field_70461_c].func_150997_a(var1);
      }

      return var2;
   }

   public NBTTagList func_70442_a(NBTTagList var1) {
      for(int var2 = 0; var2 < this.field_70462_a.length; ++var2) {
         if (this.field_70462_a[var2] != null) {
            NBTTagCompound var3 = new NBTTagCompound();
            var3.func_74774_a("Slot", (byte)var2);
            this.field_70462_a[var2].func_77955_b(var3);
            var1.func_74742_a(var3);
         }
      }

      for(int var4 = 0; var4 < this.field_70460_b.length; ++var4) {
         if (this.field_70460_b[var4] != null) {
            NBTTagCompound var5 = new NBTTagCompound();
            var5.func_74774_a("Slot", (byte)(var4 + 100));
            this.field_70460_b[var4].func_77955_b(var5);
            var1.func_74742_a(var5);
         }
      }

      return var1;
   }

   public void func_70443_b(NBTTagList var1) {
      this.field_70462_a = new ItemStack[36];
      this.field_70460_b = new ItemStack[4];

      for(int var2 = 0; var2 < var1.func_74745_c(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         int var4 = var3.func_74771_c("Slot") & 255;
         ItemStack var5 = ItemStack.func_77949_a(var3);
         if (var5 != null) {
            if (var4 >= 0 && var4 < this.field_70462_a.length) {
               this.field_70462_a[var4] = var5;
            }

            if (var4 >= 100 && var4 < this.field_70460_b.length + 100) {
               this.field_70460_b[var4 - 100] = var5;
            }
         }
      }
   }

   @Override
   public int func_70302_i_() {
      return this.field_70462_a.length + 4;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      ItemStack[] var2 = this.field_70462_a;
      if (var1 >= var2.length) {
         var1 -= var2.length;
         var2 = this.field_70460_b;
      }

      return var2[var1];
   }

   @Override
   public String func_145825_b() {
      return "container.inventory";
   }

   @Override
   public boolean func_145818_k_() {
      return false;
   }

   @Override
   public int func_70297_j_() {
      return 64;
   }

   public boolean func_146025_b(Block var1) {
      if (var1.func_149688_o().func_76229_l()) {
         return true;
      } else {
         ItemStack var2 = this.func_70301_a(this.field_70461_c);
         return var2 != null ? var2.func_150998_b(var1) : false;
      }
   }

   public ItemStack func_70440_f(int var1) {
      return this.field_70460_b[var1];
   }

   public int func_70430_l() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.field_70460_b.length; ++var2) {
         if (this.field_70460_b[var2] != null && this.field_70460_b[var2].func_77973_b() instanceof ItemArmor) {
            int var3 = ((ItemArmor)this.field_70460_b[var2].func_77973_b()).field_77879_b;
            var1 += var3;
         }
      }

      return var1;
   }

   public void func_70449_g(float var1) {
      var1 /= 4.0F;
      if (var1 < 1.0F) {
         var1 = 1.0F;
      }

      for(int var2 = 0; var2 < this.field_70460_b.length; ++var2) {
         if (this.field_70460_b[var2] != null && this.field_70460_b[var2].func_77973_b() instanceof ItemArmor) {
            this.field_70460_b[var2].func_77972_a((int)var1, this.field_70458_d);
            if (this.field_70460_b[var2].field_77994_a == 0) {
               this.field_70460_b[var2] = null;
            }
         }
      }
   }

   public void func_70436_m() {
      for(int var1 = 0; var1 < this.field_70462_a.length; ++var1) {
         if (this.field_70462_a[var1] != null) {
            this.field_70458_d.func_146097_a(this.field_70462_a[var1], true, false);
            this.field_70462_a[var1] = null;
         }
      }

      for(int var2 = 0; var2 < this.field_70460_b.length; ++var2) {
         if (this.field_70460_b[var2] != null) {
            this.field_70458_d.func_146097_a(this.field_70460_b[var2], true, false);
            this.field_70460_b[var2] = null;
         }
      }
   }

   @Override
   public void func_70296_d() {
      this.field_70459_e = true;
   }

   public void func_70437_b(ItemStack var1) {
      this.field_70457_g = var1;
   }

   public ItemStack func_70445_o() {
      return this.field_70457_g;
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_70458_d.field_70128_L) {
         return false;
      } else {
         return !(var1.func_70068_e(this.field_70458_d) > 64.0);
      }
   }

   public boolean func_70431_c(ItemStack var1) {
      for(int var2 = 0; var2 < this.field_70460_b.length; ++var2) {
         if (this.field_70460_b[var2] != null && this.field_70460_b[var2].func_77969_a(var1)) {
            return true;
         }
      }

      for(int var3 = 0; var3 < this.field_70462_a.length; ++var3) {
         if (this.field_70462_a[var3] != null && this.field_70462_a[var3].func_77969_a(var1)) {
            return true;
         }
      }

      return false;
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

   public void func_70455_b(InventoryPlayer var1) {
      for(int var2 = 0; var2 < this.field_70462_a.length; ++var2) {
         this.field_70462_a[var2] = ItemStack.func_77944_b(var1.field_70462_a[var2]);
      }

      for(int var3 = 0; var3 < this.field_70460_b.length; ++var3) {
         this.field_70460_b[var3] = ItemStack.func_77944_b(var1.field_70460_b[var3]);
      }

      this.field_70461_c = var1.field_70461_c;
   }
}
