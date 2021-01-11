package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand extends TileEntityLockable implements ITickable, ISidedInventory {
   private static final int[] field_145941_a = new int[]{3};
   private static final int[] field_145947_i = new int[]{0, 1, 2};
   private ItemStack[] field_145945_j = new ItemStack[4];
   private int field_145946_k;
   private boolean[] field_145943_l;
   private Item field_145944_m;
   private String field_145942_n;

   public TileEntityBrewingStand() {
      super();
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_145942_n : "container.brewing";
   }

   public boolean func_145818_k_() {
      return this.field_145942_n != null && this.field_145942_n.length() > 0;
   }

   public void func_145937_a(String var1) {
      this.field_145942_n = var1;
   }

   public int func_70302_i_() {
      return this.field_145945_j.length;
   }

   public void func_73660_a() {
      if (this.field_145946_k > 0) {
         --this.field_145946_k;
         if (this.field_145946_k == 0) {
            this.func_145940_l();
            this.func_70296_d();
         } else if (!this.func_145934_k()) {
            this.field_145946_k = 0;
            this.func_70296_d();
         } else if (this.field_145944_m != this.field_145945_j[3].func_77973_b()) {
            this.field_145946_k = 0;
            this.func_70296_d();
         }
      } else if (this.func_145934_k()) {
         this.field_145946_k = 400;
         this.field_145944_m = this.field_145945_j[3].func_77973_b();
      }

      if (!this.field_145850_b.field_72995_K) {
         boolean[] var1 = this.func_174902_m();
         if (!Arrays.equals(var1, this.field_145943_l)) {
            this.field_145943_l = var1;
            IBlockState var2 = this.field_145850_b.func_180495_p(this.func_174877_v());
            if (!(var2.func_177230_c() instanceof BlockBrewingStand)) {
               return;
            }

            for(int var3 = 0; var3 < BlockBrewingStand.field_176451_a.length; ++var3) {
               var2 = var2.func_177226_a(BlockBrewingStand.field_176451_a[var3], var1[var3]);
            }

            this.field_145850_b.func_180501_a(this.field_174879_c, var2, 2);
         }
      }

   }

   private boolean func_145934_k() {
      if (this.field_145945_j[3] != null && this.field_145945_j[3].field_77994_a > 0) {
         ItemStack var1 = this.field_145945_j[3];
         if (!var1.func_77973_b().func_150892_m(var1)) {
            return false;
         } else {
            boolean var2 = false;

            for(int var3 = 0; var3 < 3; ++var3) {
               if (this.field_145945_j[var3] != null && this.field_145945_j[var3].func_77973_b() == Items.field_151068_bn) {
                  int var4 = this.field_145945_j[var3].func_77960_j();
                  int var5 = this.func_145936_c(var4, var1);
                  if (!ItemPotion.func_77831_g(var4) && ItemPotion.func_77831_g(var5)) {
                     var2 = true;
                     break;
                  }

                  List var6 = Items.field_151068_bn.func_77834_f(var4);
                  List var7 = Items.field_151068_bn.func_77834_f(var5);
                  if ((var4 <= 0 || var6 != var7) && (var6 == null || !var6.equals(var7) && var7 != null) && var4 != var5) {
                     var2 = true;
                     break;
                  }
               }
            }

            return var2;
         }
      } else {
         return false;
      }
   }

   private void func_145940_l() {
      if (this.func_145934_k()) {
         ItemStack var1 = this.field_145945_j[3];

         for(int var2 = 0; var2 < 3; ++var2) {
            if (this.field_145945_j[var2] != null && this.field_145945_j[var2].func_77973_b() == Items.field_151068_bn) {
               int var3 = this.field_145945_j[var2].func_77960_j();
               int var4 = this.func_145936_c(var3, var1);
               List var5 = Items.field_151068_bn.func_77834_f(var3);
               List var6 = Items.field_151068_bn.func_77834_f(var4);
               if ((var3 <= 0 || var5 != var6) && (var5 == null || !var5.equals(var6) && var6 != null)) {
                  if (var3 != var4) {
                     this.field_145945_j[var2].func_77964_b(var4);
                  }
               } else if (!ItemPotion.func_77831_g(var3) && ItemPotion.func_77831_g(var4)) {
                  this.field_145945_j[var2].func_77964_b(var4);
               }
            }
         }

         if (var1.func_77973_b().func_77634_r()) {
            this.field_145945_j[3] = new ItemStack(var1.func_77973_b().func_77668_q());
         } else {
            --this.field_145945_j[3].field_77994_a;
            if (this.field_145945_j[3].field_77994_a <= 0) {
               this.field_145945_j[3] = null;
            }
         }

      }
   }

   private int func_145936_c(int var1, ItemStack var2) {
      if (var2 == null) {
         return var1;
      } else {
         return var2.func_77973_b().func_150892_m(var2) ? PotionHelper.func_77913_a(var1, var2.func_77973_b().func_150896_i(var2)) : var1;
      }
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      NBTTagList var2 = var1.func_150295_c("Items", 10);
      this.field_145945_j = new ItemStack[this.func_70302_i_()];

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         byte var5 = var4.func_74771_c("Slot");
         if (var5 >= 0 && var5 < this.field_145945_j.length) {
            this.field_145945_j[var5] = ItemStack.func_77949_a(var4);
         }
      }

      this.field_145946_k = var1.func_74765_d("BrewTime");
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145942_n = var1.func_74779_i("CustomName");
      }

   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74777_a("BrewTime", (short)this.field_145946_k);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_145945_j.length; ++var3) {
         if (this.field_145945_j[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.func_74774_a("Slot", (byte)var3);
            this.field_145945_j[var3].func_77955_b(var4);
            var2.func_74742_a(var4);
         }
      }

      var1.func_74782_a("Items", var2);
      if (this.func_145818_k_()) {
         var1.func_74778_a("CustomName", this.field_145942_n);
      }

   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= 0 && var1 < this.field_145945_j.length ? this.field_145945_j[var1] : null;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (var1 >= 0 && var1 < this.field_145945_j.length) {
         ItemStack var3 = this.field_145945_j[var1];
         this.field_145945_j[var1] = null;
         return var3;
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (var1 >= 0 && var1 < this.field_145945_j.length) {
         ItemStack var2 = this.field_145945_j[var1];
         this.field_145945_j[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.field_145945_j.length) {
         this.field_145945_j[var1] = var2;
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
      if (var1 == 3) {
         return var2.func_77973_b().func_150892_m(var2);
      } else {
         return var2.func_77973_b() == Items.field_151068_bn || var2.func_77973_b() == Items.field_151069_bo;
      }
   }

   public boolean[] func_174902_m() {
      boolean[] var1 = new boolean[3];

      for(int var2 = 0; var2 < 3; ++var2) {
         if (this.field_145945_j[var2] != null) {
            var1[var2] = true;
         }
      }

      return var1;
   }

   public int[] func_180463_a(EnumFacing var1) {
      return var1 == EnumFacing.UP ? field_145941_a : field_145947_i;
   }

   public boolean func_180462_a(int var1, ItemStack var2, EnumFacing var3) {
      return this.func_94041_b(var1, var2);
   }

   public boolean func_180461_b(int var1, ItemStack var2, EnumFacing var3) {
      return true;
   }

   public String func_174875_k() {
      return "minecraft:brewing_stand";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerBrewingStand(var1, this);
   }

   public int func_174887_a_(int var1) {
      switch(var1) {
      case 0:
         return this.field_145946_k;
      default:
         return 0;
      }
   }

   public void func_174885_b(int var1, int var2) {
      switch(var1) {
      case 0:
         this.field_145946_k = var2;
      default:
      }
   }

   public int func_174890_g() {
      return 1;
   }

   public void func_174888_l() {
      for(int var1 = 0; var1 < this.field_145945_j.length; ++var1) {
         this.field_145945_j[var1] = null;
      }

   }
}
