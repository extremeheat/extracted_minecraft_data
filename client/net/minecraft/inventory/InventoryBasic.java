package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBasic implements IInventory {
   private String field_70483_a;
   private int field_70481_b;
   private ItemStack[] field_70482_c;
   private List field_70480_d;
   private boolean field_94051_e;

   public InventoryBasic(String var1, boolean var2, int var3) {
      super();
      this.field_70483_a = var1;
      this.field_94051_e = var2;
      this.field_70481_b = var3;
      this.field_70482_c = new ItemStack[var3];
   }

   public void func_110134_a(IInvBasic var1) {
      if (this.field_70480_d == null) {
         this.field_70480_d = new ArrayList();
      }

      this.field_70480_d.add(var1);
   }

   public void func_110132_b(IInvBasic var1) {
      this.field_70480_d.remove(var1);
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return var1 >= 0 && var1 < this.field_70482_c.length ? this.field_70482_c[var1] : null;
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70482_c[var1] != null) {
         if (this.field_70482_c[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_70482_c[var1];
            this.field_70482_c[var1] = null;
            this.func_70296_d();
            return var4;
         } else {
            ItemStack var3 = this.field_70482_c[var1].func_77979_a(var2);
            if (this.field_70482_c[var1].field_77994_a == 0) {
               this.field_70482_c[var1] = null;
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
      if (this.field_70482_c[var1] != null) {
         ItemStack var2 = this.field_70482_c[var1];
         this.field_70482_c[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70482_c[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      this.func_70296_d();
   }

   @Override
   public int func_70302_i_() {
      return this.field_70481_b;
   }

   @Override
   public String func_145825_b() {
      return this.field_70483_a;
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_94051_e;
   }

   public void func_110133_a(String var1) {
      this.field_94051_e = true;
      this.field_70483_a = var1;
   }

   @Override
   public int func_70297_j_() {
      return 64;
   }

   @Override
   public void func_70296_d() {
      if (this.field_70480_d != null) {
         for(int var1 = 0; var1 < this.field_70480_d.size(); ++var1) {
            ((IInvBasic)this.field_70480_d.get(var1)).func_76316_a(this);
         }
      }
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      return true;
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
