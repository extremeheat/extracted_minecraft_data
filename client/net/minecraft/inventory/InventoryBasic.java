package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory {
   private String field_70483_a;
   private int field_70481_b;
   private ItemStack[] field_70482_c;
   private List<IInvBasic> field_70480_d;
   private boolean field_94051_e;

   public InventoryBasic(String var1, boolean var2, int var3) {
      super();
      this.field_70483_a = var1;
      this.field_94051_e = var2;
      this.field_70481_b = var3;
      this.field_70482_c = new ItemStack[var3];
   }

   public InventoryBasic(IChatComponent var1, int var2) {
      this(var1.func_150260_c(), true, var2);
   }

   public void func_110134_a(IInvBasic var1) {
      if (this.field_70480_d == null) {
         this.field_70480_d = Lists.newArrayList();
      }

      this.field_70480_d.add(var1);
   }

   public void func_110132_b(IInvBasic var1) {
      this.field_70480_d.remove(var1);
   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= 0 && var1 < this.field_70482_c.length ? this.field_70482_c[var1] : null;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70482_c[var1] != null) {
         ItemStack var3;
         if (this.field_70482_c[var1].field_77994_a <= var2) {
            var3 = this.field_70482_c[var1];
            this.field_70482_c[var1] = null;
            this.func_70296_d();
            return var3;
         } else {
            var3 = this.field_70482_c[var1].func_77979_a(var2);
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

   public ItemStack func_174894_a(ItemStack var1) {
      ItemStack var2 = var1.func_77946_l();

      for(int var3 = 0; var3 < this.field_70481_b; ++var3) {
         ItemStack var4 = this.func_70301_a(var3);
         if (var4 == null) {
            this.func_70299_a(var3, var2);
            this.func_70296_d();
            return null;
         }

         if (ItemStack.func_179545_c(var4, var2)) {
            int var5 = Math.min(this.func_70297_j_(), var4.func_77976_d());
            int var6 = Math.min(var2.field_77994_a, var5 - var4.field_77994_a);
            if (var6 > 0) {
               var4.field_77994_a += var6;
               var2.field_77994_a -= var6;
               if (var2.field_77994_a <= 0) {
                  this.func_70296_d();
                  return null;
               }
            }
         }
      }

      if (var2.field_77994_a != var1.field_77994_a) {
         this.func_70296_d();
      }

      return var2;
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_70482_c[var1] != null) {
         ItemStack var2 = this.field_70482_c[var1];
         this.field_70482_c[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70482_c[var1] = var2;
      if (var2 != null && var2.field_77994_a > this.func_70297_j_()) {
         var2.field_77994_a = this.func_70297_j_();
      }

      this.func_70296_d();
   }

   public int func_70302_i_() {
      return this.field_70481_b;
   }

   public String func_70005_c_() {
      return this.field_70483_a;
   }

   public boolean func_145818_k_() {
      return this.field_94051_e;
   }

   public void func_110133_a(String var1) {
      this.field_94051_e = true;
      this.field_70483_a = var1;
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }

   public int func_70297_j_() {
      return 64;
   }

   public void func_70296_d() {
      if (this.field_70480_d != null) {
         for(int var1 = 0; var1 < this.field_70480_d.size(); ++var1) {
            ((IInvBasic)this.field_70480_d.get(var1)).func_76316_a(this);
         }
      }

   }

   public boolean func_70300_a(EntityPlayer var1) {
      return true;
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
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
      for(int var1 = 0; var1 < this.field_70482_c.length; ++var1) {
         this.field_70482_c[var1] = null;
      }

   }
}
