package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
   private IInventory field_82866_e;
   private final ContainerBeacon.BeaconSlot field_82864_f;

   public ContainerBeacon(IInventory var1, IInventory var2) {
      super();
      this.field_82866_e = var2;
      this.func_75146_a(this.field_82864_f = new ContainerBeacon.BeaconSlot(var2, 0, 136, 110));
      byte var3 = 36;
      short var4 = 137;

      int var5;
      for(var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.func_75146_a(new Slot(var1, var6 + var5 * 9 + 9, var3 + var6 * 18, var4 + var5 * 18));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.func_75146_a(new Slot(var1, var5, var3 + var5 * 18, 58 + var4));
      }

   }

   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_175173_a(this, this.field_82866_e);
   }

   public void func_75137_b(int var1, int var2) {
      this.field_82866_e.func_174885_b(var1, var2);
   }

   public IInventory func_180611_e() {
      return this.field_82866_e;
   }

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      if (var1 != null && !var1.field_70170_p.field_72995_K) {
         ItemStack var2 = this.field_82864_f.func_75209_a(this.field_82864_f.func_75219_a());
         if (var2 != null) {
            var1.func_71019_a(var2, false);
         }

      }
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_82866_e.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 0) {
            if (!this.func_75135_a(var5, 1, 37, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         } else if (!this.field_82864_f.func_75216_d() && this.field_82864_f.func_75214_a(var5) && var5.field_77994_a == 1) {
            if (!this.func_75135_a(var5, 0, 1, false)) {
               return null;
            }
         } else if (var2 >= 1 && var2 < 28) {
            if (!this.func_75135_a(var5, 28, 37, false)) {
               return null;
            }
         } else if (var2 >= 28 && var2 < 37) {
            if (!this.func_75135_a(var5, 1, 28, false)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 1, 37, false)) {
            return null;
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d((ItemStack)null);
         } else {
            var4.func_75218_e();
         }

         if (var5.field_77994_a == var3.field_77994_a) {
            return null;
         }

         var4.func_82870_a(var1, var5);
      }

      return var3;
   }

   class BeaconSlot extends Slot {
      public BeaconSlot(IInventory var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      public boolean func_75214_a(ItemStack var1) {
         if (var1 == null) {
            return false;
         } else {
            return var1.func_77973_b() == Items.field_151166_bC || var1.func_77973_b() == Items.field_151045_i || var1.func_77973_b() == Items.field_151043_k || var1.func_77973_b() == Items.field_151042_j;
         }
      }

      public int func_75219_a() {
         return 1;
      }
   }
}
