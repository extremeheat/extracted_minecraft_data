package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BannerDuplicateRecipe extends IRecipeHidden {
   public BannerDuplicateRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         EnumDyeColor var3 = null;
         ItemStack var4 = null;
         ItemStack var5 = null;

         for(int var6 = 0; var6 < var1.func_70302_i_(); ++var6) {
            ItemStack var7 = var1.func_70301_a(var6);
            Item var8 = var7.func_77973_b();
            if (var8 instanceof ItemBanner) {
               ItemBanner var9 = (ItemBanner)var8;
               if (var3 == null) {
                  var3 = var9.func_195948_b();
               } else if (var3 != var9.func_195948_b()) {
                  return false;
               }

               boolean var10 = TileEntityBanner.func_175113_c(var7) > 0;
               if (var10) {
                  if (var4 != null) {
                     return false;
                  }

                  var4 = var7;
               } else {
                  if (var5 != null) {
                     return false;
                  }

                  var5 = var7;
               }
            }
         }

         return var4 != null && var5 != null;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      for(int var2 = 0; var2 < var1.func_70302_i_(); ++var2) {
         ItemStack var3 = var1.func_70301_a(var2);
         if (!var3.func_190926_b() && TileEntityBanner.func_175113_c(var3) > 0) {
            ItemStack var4 = var3.func_77946_l();
            var4.func_190920_e(1);
            return var4;
         }
      }

      return ItemStack.field_190927_a;
   }

   public NonNullList<ItemStack> func_179532_b(IInventory var1) {
      NonNullList var2 = NonNullList.func_191197_a(var1.func_70302_i_(), ItemStack.field_190927_a);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (!var4.func_190926_b()) {
            if (var4.func_77973_b().func_77634_r()) {
               var2.set(var3, new ItemStack(var4.func_77973_b().func_77668_q()));
            } else if (var4.func_77942_o() && TileEntityBanner.func_175113_c(var4) > 0) {
               ItemStack var5 = var4.func_77946_l();
               var5.func_190920_e(1);
               var2.set(var3, var5);
            }
         }
      }

      return var2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199586_l;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }
}
