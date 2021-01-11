package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeChest implements ILockableContainer {
   private String field_70479_a;
   private ILockableContainer field_70477_b;
   private ILockableContainer field_70478_c;

   public InventoryLargeChest(String var1, ILockableContainer var2, ILockableContainer var3) {
      super();
      this.field_70479_a = var1;
      if (var2 == null) {
         var2 = var3;
      }

      if (var3 == null) {
         var3 = var2;
      }

      this.field_70477_b = var2;
      this.field_70478_c = var3;
      if (var2.func_174893_q_()) {
         var3.func_174892_a(var2.func_174891_i());
      } else if (var3.func_174893_q_()) {
         var2.func_174892_a(var3.func_174891_i());
      }

   }

   public int func_70302_i_() {
      return this.field_70477_b.func_70302_i_() + this.field_70478_c.func_70302_i_();
   }

   public boolean func_90010_a(IInventory var1) {
      return this.field_70477_b == var1 || this.field_70478_c == var1;
   }

   public String func_70005_c_() {
      if (this.field_70477_b.func_145818_k_()) {
         return this.field_70477_b.func_70005_c_();
      } else {
         return this.field_70478_c.func_145818_k_() ? this.field_70478_c.func_70005_c_() : this.field_70479_a;
      }
   }

   public boolean func_145818_k_() {
      return this.field_70477_b.func_145818_k_() || this.field_70478_c.func_145818_k_();
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= this.field_70477_b.func_70302_i_() ? this.field_70478_c.func_70301_a(var1 - this.field_70477_b.func_70302_i_()) : this.field_70477_b.func_70301_a(var1);
   }

   public ItemStack func_70298_a(int var1, int var2) {
      return var1 >= this.field_70477_b.func_70302_i_() ? this.field_70478_c.func_70298_a(var1 - this.field_70477_b.func_70302_i_(), var2) : this.field_70477_b.func_70298_a(var1, var2);
   }

   public ItemStack func_70304_b(int var1) {
      return var1 >= this.field_70477_b.func_70302_i_() ? this.field_70478_c.func_70304_b(var1 - this.field_70477_b.func_70302_i_()) : this.field_70477_b.func_70304_b(var1);
   }

   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 >= this.field_70477_b.func_70302_i_()) {
         this.field_70478_c.func_70299_a(var1 - this.field_70477_b.func_70302_i_(), var2);
      } else {
         this.field_70477_b.func_70299_a(var1, var2);
      }

   }

   public int func_70297_j_() {
      return this.field_70477_b.func_70297_j_();
   }

   public void func_70296_d() {
      this.field_70477_b.func_70296_d();
      this.field_70478_c.func_70296_d();
   }

   public boolean func_70300_a(EntityPlayer var1) {
      return this.field_70477_b.func_70300_a(var1) && this.field_70478_c.func_70300_a(var1);
   }

   public void func_174889_b(EntityPlayer var1) {
      this.field_70477_b.func_174889_b(var1);
      this.field_70478_c.func_174889_b(var1);
   }

   public void func_174886_c(EntityPlayer var1) {
      this.field_70477_b.func_174886_c(var1);
      this.field_70478_c.func_174886_c(var1);
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

   public boolean func_174893_q_() {
      return this.field_70477_b.func_174893_q_() || this.field_70478_c.func_174893_q_();
   }

   public void func_174892_a(LockCode var1) {
      this.field_70477_b.func_174892_a(var1);
      this.field_70478_c.func_174892_a(var1);
   }

   public LockCode func_174891_i() {
      return this.field_70477_b.func_174891_i();
   }

   public String func_174875_k() {
      return this.field_70477_b.func_174875_k();
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }

   public void func_174888_l() {
      this.field_70477_b.func_174888_l();
      this.field_70478_c.func_174888_l();
   }
}
