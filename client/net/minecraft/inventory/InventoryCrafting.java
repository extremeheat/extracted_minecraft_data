package net.minecraft.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCrafting implements IInventory, IRecipeHelperPopulator {
   private final NonNullList<ItemStack> field_70466_a;
   private final int field_70464_b;
   private final int field_174924_c;
   private final Container field_70465_c;

   public InventoryCrafting(Container var1, int var2, int var3) {
      super();
      this.field_70466_a = NonNullList.func_191197_a(var2 * var3, ItemStack.field_190927_a);
      this.field_70465_c = var1;
      this.field_70464_b = var2;
      this.field_174924_c = var3;
   }

   public int func_70302_i_() {
      return this.field_70466_a.size();
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_70466_a.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= this.func_70302_i_() ? ItemStack.field_190927_a : (ItemStack)this.field_70466_a.get(var1);
   }

   public ITextComponent func_200200_C_() {
      return new TextComponentTranslation("container.crafting", new Object[0]);
   }

   public boolean func_145818_k_() {
      return false;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return null;
   }

   public ItemStack func_70304_b(int var1) {
      return ItemStackHelper.func_188383_a(this.field_70466_a, var1);
   }

   public ItemStack func_70298_a(int var1, int var2) {
      ItemStack var3 = ItemStackHelper.func_188382_a(this.field_70466_a, var1, var2);
      if (!var3.func_190926_b()) {
         this.field_70465_c.func_75130_a(this);
      }

      return var3;
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70466_a.set(var1, var2);
      this.field_70465_c.func_75130_a(this);
   }

   public int func_70297_j_() {
      return 64;
   }

   public void func_70296_d() {
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
      this.field_70466_a.clear();
   }

   public int func_174923_h() {
      return this.field_174924_c;
   }

   public int func_174922_i() {
      return this.field_70464_b;
   }

   public void func_194018_a(RecipeItemHelper var1) {
      Iterator var2 = this.field_70466_a.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.func_195932_a(var3);
      }

   }
}
