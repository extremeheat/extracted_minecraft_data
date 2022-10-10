package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe extends IRecipeHidden {
   private static final Ingredient field_196217_a;

   public FireworkStarFadeRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean var3 = false;
         boolean var4 = false;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (var6.func_77973_b() instanceof ItemDye) {
                  var3 = true;
               } else {
                  if (!field_196217_a.test(var6)) {
                     return false;
                  }

                  if (var4) {
                     return false;
                  }

                  var4 = true;
               }
            }
         }

         return var4 && var3;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ArrayList var2 = Lists.newArrayList();
      ItemStack var3 = null;

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         Item var6 = var5.func_77973_b();
         if (var6 instanceof ItemDye) {
            var2.add(((ItemDye)var6).func_195962_g().func_196060_f());
         } else if (field_196217_a.test(var5)) {
            var3 = var5.func_77946_l();
            var3.func_190920_e(1);
         }
      }

      if (var3 != null && !var2.isEmpty()) {
         var3.func_190925_c("Explosion").func_197646_b("FadeColors", var2);
         return var3;
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199583_i;
   }

   static {
      field_196217_a = Ingredient.func_199804_a(Items.field_196153_dF);
   }
}
