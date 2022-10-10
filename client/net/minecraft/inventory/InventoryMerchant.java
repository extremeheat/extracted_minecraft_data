package net.minecraft.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
   private final IMerchant field_70476_a;
   private final NonNullList<ItemStack> field_70474_b;
   private final EntityPlayer field_70475_c;
   private MerchantRecipe field_70472_d;
   private int field_70473_e;

   public InventoryMerchant(EntityPlayer var1, IMerchant var2) {
      super();
      this.field_70474_b = NonNullList.func_191197_a(3, ItemStack.field_190927_a);
      this.field_70475_c = var1;
      this.field_70476_a = var2;
   }

   public int func_70302_i_() {
      return this.field_70474_b.size();
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_70474_b.iterator();

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
      return (ItemStack)this.field_70474_b.get(var1);
   }

   public ItemStack func_70298_a(int var1, int var2) {
      ItemStack var3 = (ItemStack)this.field_70474_b.get(var1);
      if (var1 == 2 && !var3.func_190926_b()) {
         return ItemStackHelper.func_188382_a(this.field_70474_b, var1, var3.func_190916_E());
      } else {
         ItemStack var4 = ItemStackHelper.func_188382_a(this.field_70474_b, var1, var2);
         if (!var4.func_190926_b() && this.func_70469_d(var1)) {
            this.func_70470_g();
         }

         return var4;
      }
   }

   private boolean func_70469_d(int var1) {
      return var1 == 0 || var1 == 1;
   }

   public ItemStack func_70304_b(int var1) {
      return ItemStackHelper.func_188383_a(this.field_70474_b, var1);
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70474_b.set(var1, var2);
      if (!var2.func_190926_b() && var2.func_190916_E() > this.func_70297_j_()) {
         var2.func_190920_e(this.func_70297_j_());
      }

      if (this.func_70469_d(var1)) {
         this.func_70470_g();
      }

   }

   public ITextComponent func_200200_C_() {
      return new TextComponentTranslation("mob.villager", new Object[0]);
   }

   public boolean func_145818_k_() {
      return false;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return null;
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
      ItemStack var1 = (ItemStack)this.field_70474_b.get(0);
      ItemStack var2 = (ItemStack)this.field_70474_b.get(1);
      if (var1.func_190926_b()) {
         var1 = var2;
         var2 = ItemStack.field_190927_a;
      }

      if (var1.func_190926_b()) {
         this.func_70299_a(2, ItemStack.field_190927_a);
      } else {
         MerchantRecipeList var3 = this.field_70476_a.func_70934_b(this.field_70475_c);
         if (var3 != null) {
            MerchantRecipe var4 = var3.func_77203_a(var1, var2, this.field_70473_e);
            if (var4 != null && !var4.func_82784_g()) {
               this.field_70472_d = var4;
               this.func_70299_a(2, var4.func_77397_d().func_77946_l());
            } else if (!var2.func_190926_b()) {
               var4 = var3.func_77203_a(var2, var1, this.field_70473_e);
               if (var4 != null && !var4.func_82784_g()) {
                  this.field_70472_d = var4;
                  this.func_70299_a(2, var4.func_77397_d().func_77946_l());
               } else {
                  this.func_70299_a(2, ItemStack.field_190927_a);
               }
            } else {
               this.func_70299_a(2, ItemStack.field_190927_a);
            }
         }

         this.field_70476_a.func_110297_a_(this.func_70301_a(2));
      }

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
      this.field_70474_b.clear();
   }
}
