package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeRepairItem extends IRecipeHidden {
   public RecipeRepairItem(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         ArrayList var3 = Lists.newArrayList();

         for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
            ItemStack var5 = var1.func_70301_a(var4);
            if (!var5.func_190926_b()) {
               var3.add(var5);
               if (var3.size() > 1) {
                  ItemStack var6 = (ItemStack)var3.get(0);
                  if (var5.func_77973_b() != var6.func_77973_b() || var6.func_190916_E() != 1 || var5.func_190916_E() != 1 || !var6.func_77973_b().func_77645_m()) {
                     return false;
                  }
               }
            }
         }

         return var3.size() == 2;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ArrayList var2 = Lists.newArrayList();

      ItemStack var4;
      for(int var3 = 0; var3 < var1.func_70302_i_(); ++var3) {
         var4 = var1.func_70301_a(var3);
         if (!var4.func_190926_b()) {
            var2.add(var4);
            if (var2.size() > 1) {
               ItemStack var5 = (ItemStack)var2.get(0);
               if (var4.func_77973_b() != var5.func_77973_b() || var5.func_190916_E() != 1 || var4.func_190916_E() != 1 || !var5.func_77973_b().func_77645_m()) {
                  return ItemStack.field_190927_a;
               }
            }
         }
      }

      if (var2.size() == 2) {
         ItemStack var11 = (ItemStack)var2.get(0);
         var4 = (ItemStack)var2.get(1);
         if (var11.func_77973_b() == var4.func_77973_b() && var11.func_190916_E() == 1 && var4.func_190916_E() == 1 && var11.func_77973_b().func_77645_m()) {
            Item var12 = var11.func_77973_b();
            int var6 = var12.func_77612_l() - var11.func_77952_i();
            int var7 = var12.func_77612_l() - var4.func_77952_i();
            int var8 = var6 + var7 + var12.func_77612_l() * 5 / 100;
            int var9 = var12.func_77612_l() - var8;
            if (var9 < 0) {
               var9 = 0;
            }

            ItemStack var10 = new ItemStack(var11.func_77973_b());
            var10.func_196085_b(var9);
            return var10;
         }
      }

      return ItemStack.field_190927_a;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199584_j;
   }
}
