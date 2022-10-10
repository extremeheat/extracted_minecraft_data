package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipe {
   boolean func_77569_a(IInventory var1, World var2);

   ItemStack func_77572_b(IInventory var1);

   boolean func_194133_a(int var1, int var2);

   ItemStack func_77571_b();

   default NonNullList<ItemStack> func_179532_b(IInventory var1) {
      NonNullList var2 = NonNullList.func_191197_a(var1.func_70302_i_(), ItemStack.field_190927_a);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Item var4 = var1.func_70301_a(var3).func_77973_b();
         if (var4.func_77634_r()) {
            var2.set(var3, new ItemStack(var4.func_77668_q()));
         }
      }

      return var2;
   }

   default NonNullList<Ingredient> func_192400_c() {
      return NonNullList.func_191196_a();
   }

   default boolean func_192399_d() {
      return false;
   }

   default String func_193358_e() {
      return "";
   }

   ResourceLocation func_199560_c();

   IRecipeSerializer<?> func_199559_b();
}
