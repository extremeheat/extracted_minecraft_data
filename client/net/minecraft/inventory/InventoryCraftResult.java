package net.minecraft.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryCraftResult implements IInventory, IRecipeHolder {
   private final NonNullList<ItemStack> field_70467_a;
   private IRecipe field_193057_b;

   public InventoryCraftResult() {
      super();
      this.field_70467_a = NonNullList.func_191197_a(1, ItemStack.field_190927_a);
   }

   public int func_70302_i_() {
      return 1;
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_70467_a.iterator();

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
      return (ItemStack)this.field_70467_a.get(0);
   }

   public ITextComponent func_200200_C_() {
      return new TextComponentString("Result");
   }

   public boolean func_145818_k_() {
      return false;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return null;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      return ItemStackHelper.func_188383_a(this.field_70467_a, 0);
   }

   public ItemStack func_70304_b(int var1) {
      return ItemStackHelper.func_188383_a(this.field_70467_a, 0);
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70467_a.set(0, var2);
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
      this.field_70467_a.clear();
   }

   public void func_193056_a(@Nullable IRecipe var1) {
      this.field_193057_b = var1;
   }

   @Nullable
   public IRecipe func_193055_i() {
      return this.field_193057_b;
   }
}
