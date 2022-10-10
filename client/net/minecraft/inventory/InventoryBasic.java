package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InventoryBasic implements IInventory, IRecipeHelperPopulator {
   private final ITextComponent field_70483_a;
   private final int field_70481_b;
   private final NonNullList<ItemStack> field_70482_c;
   private List<IInventoryChangedListener> field_70480_d;
   private ITextComponent field_94051_e;

   public InventoryBasic(ITextComponent var1, int var2) {
      super();
      this.field_70483_a = var1;
      this.field_70481_b = var2;
      this.field_70482_c = NonNullList.func_191197_a(var2, ItemStack.field_190927_a);
   }

   public void func_110134_a(IInventoryChangedListener var1) {
      if (this.field_70480_d == null) {
         this.field_70480_d = Lists.newArrayList();
      }

      this.field_70480_d.add(var1);
   }

   public void func_110132_b(IInventoryChangedListener var1) {
      this.field_70480_d.remove(var1);
   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= 0 && var1 < this.field_70482_c.size() ? (ItemStack)this.field_70482_c.get(var1) : ItemStack.field_190927_a;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      ItemStack var3 = ItemStackHelper.func_188382_a(this.field_70482_c, var1, var2);
      if (!var3.func_190926_b()) {
         this.func_70296_d();
      }

      return var3;
   }

   public ItemStack func_174894_a(ItemStack var1) {
      ItemStack var2 = var1.func_77946_l();

      for(int var3 = 0; var3 < this.field_70481_b; ++var3) {
         ItemStack var4 = this.func_70301_a(var3);
         if (var4.func_190926_b()) {
            this.func_70299_a(var3, var2);
            this.func_70296_d();
            return ItemStack.field_190927_a;
         }

         if (ItemStack.func_179545_c(var4, var2)) {
            int var5 = Math.min(this.func_70297_j_(), var4.func_77976_d());
            int var6 = Math.min(var2.func_190916_E(), var5 - var4.func_190916_E());
            if (var6 > 0) {
               var4.func_190917_f(var6);
               var2.func_190918_g(var6);
               if (var2.func_190926_b()) {
                  this.func_70296_d();
                  return ItemStack.field_190927_a;
               }
            }
         }
      }

      if (var2.func_190916_E() != var1.func_190916_E()) {
         this.func_70296_d();
      }

      return var2;
   }

   public ItemStack func_70304_b(int var1) {
      ItemStack var2 = (ItemStack)this.field_70482_c.get(var1);
      if (var2.func_190926_b()) {
         return ItemStack.field_190927_a;
      } else {
         this.field_70482_c.set(var1, ItemStack.field_190927_a);
         return var2;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70482_c.set(var1, var2);
      if (!var2.func_190926_b() && var2.func_190916_E() > this.func_70297_j_()) {
         var2.func_190920_e(this.func_70297_j_());
      }

      this.func_70296_d();
   }

   public int func_70302_i_() {
      return this.field_70481_b;
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_70482_c.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public ITextComponent func_200200_C_() {
      return this.field_94051_e != null ? this.field_94051_e : this.field_70483_a;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_94051_e;
   }

   public boolean func_145818_k_() {
      return this.field_94051_e != null;
   }

   public void func_200228_a(@Nullable ITextComponent var1) {
      this.field_94051_e = var1;
   }

   public int func_70297_j_() {
      return 64;
   }

   public void func_70296_d() {
      if (this.field_70480_d != null) {
         for(int var1 = 0; var1 < this.field_70480_d.size(); ++var1) {
            ((IInventoryChangedListener)this.field_70480_d.get(var1)).func_76316_a(this);
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
      this.field_70482_c.clear();
   }

   public void func_194018_a(RecipeItemHelper var1) {
      Iterator var2 = this.field_70482_c.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.func_194112_a(var3);
      }

   }
}
