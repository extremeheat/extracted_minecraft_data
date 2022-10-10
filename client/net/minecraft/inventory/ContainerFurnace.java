package net.minecraft.inventory;

import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

public class ContainerFurnace extends ContainerRecipeBook {
   private final IInventory field_75158_e;
   private final World field_201773_f;
   private int field_178152_f;
   private int field_178153_g;
   private int field_178154_h;
   private int field_178155_i;

   public ContainerFurnace(InventoryPlayer var1, IInventory var2) {
      super();
      this.field_75158_e = var2;
      this.field_201773_f = var1.field_70458_d.field_70170_p;
      this.func_75146_a(new Slot(var2, 0, 56, 17));
      this.func_75146_a(new SlotFurnaceFuel(var2, 1, 56, 53));
      this.func_75146_a(new SlotFurnaceOutput(var1.field_70458_d, var2, 2, 116, 35));

      int var3;
      for(var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.func_75146_a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 9; ++var3) {
         this.func_75146_a(new Slot(var1, var3, 8 + var3 * 18, 142));
      }

   }

   public void func_75132_a(IContainerListener var1) {
      super.func_75132_a(var1);
      var1.func_175173_a(this, this.field_75158_e);
   }

   public void func_201771_a(RecipeItemHelper var1) {
      if (this.field_75158_e instanceof IRecipeHelperPopulator) {
         ((IRecipeHelperPopulator)this.field_75158_e).func_194018_a(var1);
      }

   }

   public void func_201768_e() {
      this.field_75158_e.func_174888_l();
   }

   public boolean func_201769_a(IRecipe var1) {
      return var1.func_77569_a(this.field_75158_e, this.field_201773_f);
   }

   public int func_201767_f() {
      return 2;
   }

   public int func_201770_g() {
      return 1;
   }

   public int func_201772_h() {
      return 1;
   }

   public int func_203721_h() {
      return 3;
   }

   public void func_75142_b() {
      super.func_75142_b();
      Iterator var1 = this.field_75149_d.iterator();

      while(var1.hasNext()) {
         IContainerListener var2 = (IContainerListener)var1.next();
         if (this.field_178152_f != this.field_75158_e.func_174887_a_(2)) {
            var2.func_71112_a(this, 2, this.field_75158_e.func_174887_a_(2));
         }

         if (this.field_178154_h != this.field_75158_e.func_174887_a_(0)) {
            var2.func_71112_a(this, 0, this.field_75158_e.func_174887_a_(0));
         }

         if (this.field_178155_i != this.field_75158_e.func_174887_a_(1)) {
            var2.func_71112_a(this, 1, this.field_75158_e.func_174887_a_(1));
         }

         if (this.field_178153_g != this.field_75158_e.func_174887_a_(3)) {
            var2.func_71112_a(this, 3, this.field_75158_e.func_174887_a_(3));
         }
      }

      this.field_178152_f = this.field_75158_e.func_174887_a_(2);
      this.field_178154_h = this.field_75158_e.func_174887_a_(0);
      this.field_178155_i = this.field_75158_e.func_174887_a_(1);
      this.field_178153_g = this.field_75158_e.func_174887_a_(3);
   }

   public void func_75137_b(int var1, int var2) {
      this.field_75158_e.func_174885_b(var1, var2);
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75158_e.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 2) {
            if (!this.func_75135_a(var5, 3, 39, true)) {
               return ItemStack.field_190927_a;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 != 1 && var2 != 0) {
            if (this.func_206253_a(var5)) {
               if (!this.func_75135_a(var5, 0, 1, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (TileEntityFurnace.func_145954_b(var5)) {
               if (!this.func_75135_a(var5, 1, 2, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (var2 >= 3 && var2 < 30) {
               if (!this.func_75135_a(var5, 30, 39, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (var2 >= 30 && var2 < 39 && !this.func_75135_a(var5, 3, 30, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(var5, 3, 39, false)) {
            return ItemStack.field_190927_a;
         }

         if (var5.func_190926_b()) {
            var4.func_75215_d(ItemStack.field_190927_a);
         } else {
            var4.func_75218_e();
         }

         if (var5.func_190916_E() == var3.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         var4.func_190901_a(var1, var5);
      }

      return var3;
   }

   private boolean func_206253_a(ItemStack var1) {
      Iterator var2 = this.field_201773_f.func_199532_z().func_199510_b().iterator();

      IRecipe var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (IRecipe)var2.next();
      } while(!(var3 instanceof FurnaceRecipe) || !((Ingredient)var3.func_192400_c().get(0)).test(var1));

      return true;
   }
}
