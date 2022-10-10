package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShieldRecipes extends IRecipeHidden {
   public ShieldRecipes(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         ItemStack var3 = ItemStack.field_190927_a;
         ItemStack var4 = ItemStack.field_190927_a;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (var6.func_77973_b() instanceof ItemBanner) {
                  if (!var4.func_190926_b()) {
                     return false;
                  }

                  var4 = var6;
               } else {
                  if (var6.func_77973_b() != Items.field_185159_cQ) {
                     return false;
                  }

                  if (!var3.func_190926_b()) {
                     return false;
                  }

                  if (var6.func_179543_a("BlockEntityTag") != null) {
                     return false;
                  }

                  var3 = var6;
               }
            }
         }

         if (!var3.func_190926_b() && !var4.func_190926_b()) {
            return true;
         } else {
            return false;
         }
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = ItemStack.field_190927_a;
      ItemStack var3 = ItemStack.field_190927_a;

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         if (!var5.func_190926_b()) {
            if (var5.func_77973_b() instanceof ItemBanner) {
               var2 = var5;
            } else if (var5.func_77973_b() == Items.field_185159_cQ) {
               var3 = var5.func_77946_l();
            }
         }
      }

      if (var3.func_190926_b()) {
         return var3;
      } else {
         NBTTagCompound var6 = var2.func_179543_a("BlockEntityTag");
         NBTTagCompound var7 = var6 == null ? new NBTTagCompound() : var6.func_74737_b();
         var7.func_74768_a("Base", ((ItemBanner)var2.func_77973_b()).func_195948_b().func_196059_a());
         var3.func_77983_a("BlockEntityTag", var7);
         return var3;
      }
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199588_n;
   }
}
