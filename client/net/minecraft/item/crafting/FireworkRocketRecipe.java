package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends IRecipeHidden {
   private static final Ingredient field_196209_a;
   private static final Ingredient field_196210_b;
   private static final Ingredient field_196211_c;

   public FireworkRocketRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean var3 = false;
         int var4 = 0;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (field_196209_a.test(var6)) {
                  if (var3) {
                     return false;
                  }

                  var3 = true;
               } else if (field_196210_b.test(var6)) {
                  ++var4;
                  if (var4 > 3) {
                     return false;
                  }
               } else if (!field_196211_c.test(var6)) {
                  return false;
               }
            }
         }

         return var3 && var4 >= 1;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = new ItemStack(Items.field_196152_dE, 3);
      NBTTagCompound var3 = var2.func_190925_c("Fireworks");
      NBTTagList var4 = new NBTTagList();
      int var5 = 0;

      for(int var6 = 0; var6 < var1.func_70302_i_(); ++var6) {
         ItemStack var7 = var1.func_70301_a(var6);
         if (!var7.func_190926_b()) {
            if (field_196210_b.test(var7)) {
               ++var5;
            } else if (field_196211_c.test(var7)) {
               NBTTagCompound var8 = var7.func_179543_a("Explosion");
               if (var8 != null) {
                  var4.add((INBTBase)var8);
               }
            }
         }
      }

      var3.func_74774_a("Flight", (byte)var5);
      if (!var4.isEmpty()) {
         var3.func_74782_a("Explosions", var4);
      }

      return var2;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public ItemStack func_77571_b() {
      return new ItemStack(Items.field_196152_dE);
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199581_g;
   }

   static {
      field_196209_a = Ingredient.func_199804_a(Items.field_151121_aF);
      field_196210_b = Ingredient.func_199804_a(Items.field_151016_H);
      field_196211_c = Ingredient.func_199804_a(Items.field_196153_dF);
   }
}
