package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShulkerBoxColoringRecipe extends IRecipeHidden {
   public ShulkerBoxColoringRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         int var3 = 0;
         int var4 = 0;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (Block.func_149634_a(var6.func_77973_b()) instanceof BlockShulkerBox) {
                  ++var3;
               } else {
                  if (!(var6.func_77973_b() instanceof ItemDye)) {
                     return false;
                  }

                  ++var4;
               }

               if (var4 > 1 || var3 > 1) {
                  return false;
               }
            }
         }

         return var3 == 1 && var4 == 1;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = ItemStack.field_190927_a;
      ItemDye var3 = (ItemDye)Items.field_196106_bc;

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         if (!var5.func_190926_b()) {
            Item var6 = var5.func_77973_b();
            if (Block.func_149634_a(var6) instanceof BlockShulkerBox) {
               var2 = var5;
            } else if (var6 instanceof ItemDye) {
               var3 = (ItemDye)var6;
            }
         }
      }

      ItemStack var7 = BlockShulkerBox.func_190953_b(var3.func_195962_g());
      if (var2.func_77942_o()) {
         var7.func_77982_d(var2.func_77978_p().func_74737_b());
      }

      return var7;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199589_o;
   }
}
