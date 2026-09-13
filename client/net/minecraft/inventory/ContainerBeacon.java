package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBeacon;

public class ContainerBeacon extends Container {
   private TileEntityBeacon field_82866_e;
   private final ContainerBeacon$BeaconSlot field_82864_f;
   private int field_82865_g;
   private int field_82867_h;
   private int field_82868_i;

   public ContainerBeacon(InventoryPlayer var1, TileEntityBeacon var2) {
      super();
      this.field_82866_e = var2;
      this.func_75146_a(this.field_82864_f = new ContainerBeacon$BeaconSlot(this, var2, 0, 136, 110));
      byte var3 = 36;
      short var4 = 137;

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.func_75146_a(new Slot(var1, var6 + var5 * 9 + 9, var3 + var6 * 18, var4 + var5 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.func_75146_a(new Slot(var1, var7, var3 + var7 * 18, 58 + var4));
      }

      this.field_82865_g = var2.func_145998_l();
      this.field_82867_h = var2.func_146007_j();
      this.field_82868_i = var2.func_146006_k();
   }

   @Override
   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_71112_a(this, 0, this.field_82865_g);
      var1.func_71112_a(this, 1, this.field_82867_h);
      var1.func_71112_a(this, 2, this.field_82868_i);
   }

   @Override
   public void func_75137_b(int var1, int var2) {
      if (var1 == 0) {
         this.field_82866_e.func_146005_c(var2);
      }

      if (var1 == 1) {
         this.field_82866_e.func_146001_d(var2);
      }

      if (var1 == 2) {
         this.field_82866_e.func_146004_e(var2);
      }
   }

   public TileEntityBeacon func_148327_e() {
      return this.field_82866_e;
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_82866_e.func_70300_a(var1);
   }

   @Override
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
            var4.func_75215_d(null);
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
}
