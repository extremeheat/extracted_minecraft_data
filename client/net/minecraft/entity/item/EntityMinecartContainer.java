package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements IInventory {
   private ItemStack[] field_94113_a = new ItemStack[36];
   private boolean field_94112_b = true;

   public EntityMinecartContainer(World var1) {
      super(var1);
   }

   public EntityMinecartContainer(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);

      for(int var2 = 0; var2 < this.func_70302_i_(); ++var2) {
         ItemStack var3 = this.func_70301_a(var2);
         if (var3 != null) {
            float var4 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
            float var5 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
            float var6 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;

            while(var3.field_77994_a > 0) {
               int var7 = this.field_70146_Z.nextInt(21) + 10;
               if (var7 > var3.field_77994_a) {
                  var7 = var3.field_77994_a;
               }

               var3.field_77994_a -= var7;
               EntityItem var8 = new EntityItem(
                  this.field_70170_p,
                  this.field_70165_t + (double)var4,
                  this.field_70163_u + (double)var5,
                  this.field_70161_v + (double)var6,
                  new ItemStack(var3.func_77973_b(), var7, var3.func_77960_j())
               );
               float var9 = 0.05F;
               var8.field_70159_w = (double)((float)this.field_70146_Z.nextGaussian() * var9);
               var8.field_70181_x = (double)((float)this.field_70146_Z.nextGaussian() * var9 + 0.2F);
               var8.field_70179_y = (double)((float)this.field_70146_Z.nextGaussian() * var9);
               this.field_70170_p.func_72838_d(var8);
            }
         }
      }
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return this.field_94113_a[var1];
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_94113_a[var1] != null) {
         if (this.field_94113_a[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_94113_a[var1];
            this.field_94113_a[var1] = null;
            return var4;
         } else {
            ItemStack var3 = this.field_94113_a[var1].func_77979_a(var2);
            if (this.field_94113_a[var1].field_77994_a == 0) {
               this.field_94113_a[var1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (this.field_94113_a[var1] != null) {
         ItemStack var2 = this.field_94113_a[var1];
         this.field_94113_a[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_94113_a[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }
   }

   @Override
   public void func_70296_d() {
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_70128_L) {
         return false;
      } else {
         return !(var1.func_70068_e(this) > 64.0);
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

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.func_95999_t() : "container.minecart";
   }

   @Override
   public int func_70297_j_() {
      return 64;
   }

   @Override
   public void func_71027_c(int var1) {
      this.field_94112_b = false;
      super.func_71027_c(var1);
   }

   @Override
   public void func_70106_y() {
      if (this.field_94112_b) {
         for(int var1 = 0; var1 < this.func_70302_i_(); ++var1) {
            ItemStack var2 = this.func_70301_a(var1);
            if (var2 != null) {
               float var3 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
               float var4 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
               float var5 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;

               while(var2.field_77994_a > 0) {
                  int var6 = this.field_70146_Z.nextInt(21) + 10;
                  if (var6 > var2.field_77994_a) {
                     var6 = var2.field_77994_a;
                  }

                  var2.field_77994_a -= var6;
                  EntityItem var7 = new EntityItem(
                     this.field_70170_p,
                     this.field_70165_t + (double)var3,
                     this.field_70163_u + (double)var4,
                     this.field_70161_v + (double)var5,
                     new ItemStack(var2.func_77973_b(), var6, var2.func_77960_j())
                  );
                  if (var2.func_77942_o()) {
                     var7.func_92059_d().func_77982_d((NBTTagCompound)var2.func_77978_p().func_74737_b());
                  }

                  float var8 = 0.05F;
                  var7.field_70159_w = (double)((float)this.field_70146_Z.nextGaussian() * var8);
                  var7.field_70181_x = (double)((float)this.field_70146_Z.nextGaussian() * var8 + 0.2F);
                  var7.field_70179_y = (double)((float)this.field_70146_Z.nextGaussian() * var8);
                  this.field_70170_p.func_72838_d(var7);
               }
            }
         }
      }

      super.func_70106_y();
   }

   @Override
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

   @Override
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

   @Override
   public boolean func_130002_c(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K) {
         var1.func_71007_a(this);
      }

      return true;
   }

   @Override
   protected void func_94101_h() {
      int var1 = 15 - Container.func_94526_b(this);
      float var2 = 0.98F + (float)var1 * 0.001F;
      this.field_70159_w *= (double)var2;
      this.field_70181_x *= 0.0;
      this.field_70179_y *= (double)var2;
   }
}
