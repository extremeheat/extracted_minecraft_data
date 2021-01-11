package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
   private ItemStack[] field_94113_a = new ItemStack[36];
   private boolean field_94112_b = true;

   public EntityMinecartContainer(World var1) {
      super(var1);
   }

   public EntityMinecartContainer(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         InventoryHelper.func_180176_a(this.field_70170_p, this, this);
      }

   }

   public ItemStack func_70301_a(int var1) {
      return this.field_94113_a[var1];
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_94113_a[var1] != null) {
         ItemStack var3;
         if (this.field_94113_a[var1].field_77994_a <= var2) {
            var3 = this.field_94113_a[var1];
            this.field_94113_a[var1] = null;
            return var3;
         } else {
            var3 = this.field_94113_a[var1].func_77979_a(var2);
            if (this.field_94113_a[var1].field_77994_a == 0) {
               this.field_94113_a[var1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_94113_a[var1] != null) {
         ItemStack var2 = this.field_94113_a[var1];
         this.field_94113_a[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_94113_a[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_70128_L) {
         return false;
      } else {
         return var1.func_70068_e(this) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.func_95999_t() : "container.minecart";
   }

   public int func_70297_j_() {
      return 64;
   }

   public void func_71027_c(int var1) {
      this.field_94112_b = false;
      super.func_71027_c(var1);
   }

   public void func_70106_y() {
      if (this.field_94112_b) {
         InventoryHelper.func_180176_a(this.field_70170_p, this, this);
      }

      super.func_70106_y();
   }

   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_94113_a.length; ++var3) {
         if (this.field_94113_a[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var3);
            this.field_94113_a[var3].func_77955_b(var4);
            var2.func_74742_a(var4);
         }
      }

      var1.func_74782_a("Items", var2);
   }

   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      NBTTagList var2 = var1.func_150295_c("Items", 10);
      this.field_94113_a = new ItemStack[this.func_70302_i_()];

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         int var5 = var4.func_74771_c("Slot") & 255;
         if (var5 >= 0 && var5 < this.field_94113_a.length) {
            this.field_94113_a[var5] = ItemStack.func_77949_a(var4);
         }
      }

   }

   public boolean func_130002_c(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K) {
         var1.func_71007_a(this);
      }

      return true;
   }

   protected void func_94101_h() {
      int var1 = 15 - Container.func_94526_b(this);
      float var2 = 0.98F + (float)var1 * 0.001F;
      this.field_70159_w *= (double)var2;
      this.field_70181_x *= 0.0D;
      this.field_70179_y *= (double)var2;
   }

   public int func_174887_a_(int var1) {
      return 0;
   }

   public void func_174885_b(int var1, int var2) {
   }

   public int func_174890_g() {
      return 0;
   }

   public boolean func_174893_q_() {
      return false;
   }

   public void func_174892_a(LockCode var1) {
   }

   public LockCode func_174891_i() {
      return LockCode.field_180162_a;
   }

   public void func_174888_l() {
      for(int var1 = 0; var1 < this.field_94113_a.length; ++var1) {
         this.field_94113_a[var1] = null;
      }

   }
}
