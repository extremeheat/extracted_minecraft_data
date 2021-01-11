package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
   private final IMerchant field_70476_a;
   private ItemStack[] field_70474_b = new ItemStack[3];
   private final EntityPlayer field_70475_c;
   private MerchantRecipe field_70472_d;
   private int field_70473_e;

   public InventoryMerchant(EntityPlayer var1, IMerchant var2) {
      super();
      this.field_70475_c = var1;
      this.field_70476_a = var2;
   }

   public int func_70302_i_() {
      return this.field_70474_b.length;
   }

   public ItemStack func_70301_a(int var1) {
      return this.field_70474_b[var1];
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70474_b[var1] != null) {
         ItemStack var3;
         if (var1 == 2) {
            var3 = this.field_70474_b[var1];
            this.field_70474_b[var1] = null;
            return var3;
         } else if (this.field_70474_b[var1].field_77994_a <= var2) {
            var3 = this.field_70474_b[var1];
            this.field_70474_b[var1] = null;
            if (this.func_70469_d(var1)) {
               this.func_70470_g();
            }

            return var3;
         } else {
            var3 = this.field_70474_b[var1].func_77979_a(var2);
            if (this.field_70474_b[var1].field_77994_a == 0) {
               this.field_70474_b[var1] = null;
            }

            if (this.func_70469_d(var1)) {
               this.func_70470_g();
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   private boolean func_70469_d(int var1) {
      return var1 == 0 || var1 == 1;
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_70474_b[var1] != null) {
         ItemStack var2 = this.field_70474_b[var1];
         this.field_70474_b[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70474_b[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      if (this.func_70469_d(var1)) {
         this.func_70470_g();
      }

   }

   public String func_70005_c_() {
      return "mob.villager";
   }

   public boolean func_145818_k_() {
      return false;
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      return this.field_70476_a.func_70931_l_() == var1;
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public void func_70296_d() {
      this.func_70470_g();
   }

   public void func_70470_g() {
      this.field_70472_d = null;
      ItemStack var1 = this.field_70474_b[0];
      ItemStack var2 = this.field_70474_b[1];
      if (var1 == null) {
         var1 = var2;
         var2 = null;
      }

      if (var1 == null) {
         this.func_70299_a(2, (ItemStack)null);
      } else {
         MerchantRecipeList var3 = this.field_70476_a.func_70934_b(this.field_70475_c);
         if (var3 != null) {
            MerchantRecipe var4 = var3.func_77203_a(var1, var2, this.field_70473_e);
            if (var4 != null && !var4.func_82784_g()) {
               this.field_70472_d = var4;
               this.func_70299_a(2, var4.func_77397_d().func_77946_l());
            } else if (var2 != null) {
               var4 = var3.func_77203_a(var2, var1, this.field_70473_e);
               if (var4 != null && !var4.func_82784_g()) {
                  this.field_70472_d = var4;
                  this.func_70299_a(2, var4.func_77397_d().func_77946_l());
               } else {
                  this.func_70299_a(2, (ItemStack)null);
               }
            } else {
               this.func_70299_a(2, (ItemStack)null);
            }
         }
      }

      this.field_70476_a.func_110297_a_(this.func_70301_a(2));
   }

   public MerchantRecipe func_70468_h() {
      return this.field_70472_d;
   }

   public void func_70471_c(int var1) {
      this.field_70473_e = var1;
      this.func_70470_g();
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
      for(int var1 = 0; var1 < this.field_70474_b.length; ++var1) {
         this.field_70474_b[var1] = null;
      }

   }
}
