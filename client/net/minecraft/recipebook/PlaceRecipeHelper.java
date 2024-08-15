package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface PlaceRecipeHelper {
   static <T> void placeRecipe(int var0, int var1, RecipeHolder<?> var2, Iterable<T> var3, PlaceRecipeHelper.Output<T> var4) {
      int var5 = var0;
      int var6 = var1;
      if (var2.value() instanceof ShapedRecipe var8) {
         var5 = var8.getWidth();
         var6 = var8.getHeight();
      }

      Iterator var16 = var3.iterator();
      int var9 = 0;

      for (int var10 = 0; var10 < var1; var10++) {
         boolean var11 = (float)var6 < (float)var1 / 2.0F;
         int var12 = Mth.floor((float)var1 / 2.0F - (float)var6 / 2.0F);
         if (var11 && var12 > var10) {
            var9 += var0;
            var10++;
         }

         for (int var13 = 0; var13 < var0; var13++) {
            if (!var16.hasNext()) {
               return;
            }

            var11 = (float)var5 < (float)var0 / 2.0F;
            var12 = Mth.floor((float)var0 / 2.0F - (float)var5 / 2.0F);
            int var14 = var5;
            boolean var15 = var13 < var5;
            if (var11) {
               var14 = var12 + var5;
               var15 = var12 <= var13 && var13 < var12 + var5;
            }

            if (var15) {
               var4.addItemToSlot(var16.next(), var9, var13, var10);
            } else if (var14 == var13) {
               var9 += var0 - var13;
               break;
            }

            var9++;
         }
      }
   }

   @FunctionalInterface
   public interface Output<T> {
      void addItemToSlot(T var1, int var2, int var3, int var4);
   }
}
