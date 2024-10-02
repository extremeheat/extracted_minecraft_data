package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface PlaceRecipeHelper {
   static <T> void placeRecipe(int var0, int var1, Recipe<?> var2, Iterable<T> var3, PlaceRecipeHelper.Output<T> var4) {
      if (var2 instanceof ShapedRecipe var5) {
         placeRecipe(var0, var1, var5.getWidth(), var5.getHeight(), var3, var4);
      } else {
         placeRecipe(var0, var1, var0, var1, var3, var4);
      }
   }

   static <T> void placeRecipe(int var0, int var1, int var2, int var3, Iterable<T> var4, PlaceRecipeHelper.Output<T> var5) {
      Iterator var6 = var4.iterator();
      int var7 = 0;

      for (int var8 = 0; var8 < var1; var8++) {
         boolean var9 = (float)var3 < (float)var1 / 2.0F;
         int var10 = Mth.floor((float)var1 / 2.0F - (float)var3 / 2.0F);
         if (var9 && var10 > var8) {
            var7 += var0;
            var8++;
         }

         for (int var11 = 0; var11 < var0; var11++) {
            if (!var6.hasNext()) {
               return;
            }

            var9 = (float)var2 < (float)var0 / 2.0F;
            var10 = Mth.floor((float)var0 / 2.0F - (float)var2 / 2.0F);
            int var12 = var2;
            boolean var13 = var11 < var2;
            if (var9) {
               var12 = var10 + var2;
               var13 = var10 <= var11 && var11 < var10 + var2;
            }

            if (var13) {
               var5.addItemToSlot(var6.next(), var7, var11, var8);
            } else if (var12 == var11) {
               var7 += var0 - var11;
               break;
            }

            var7++;
         }
      }
   }

   @FunctionalInterface
   public interface Output<T> {
      void addItemToSlot(T var1, int var2, int var3, int var4);
   }
}
