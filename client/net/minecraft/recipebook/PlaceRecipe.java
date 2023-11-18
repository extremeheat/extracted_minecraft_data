package net.minecraft.recipebook;

import java.util.Iterator;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface PlaceRecipe<T> {
   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   default void placeRecipe(int var1, int var2, int var3, RecipeHolder<?> var4, Iterator<T> var5, int var6) {
      int var7 = var1;
      int var8 = var2;
      Recipe var9 = var4.value();
      if (var9 instanceof ShapedRecipe var10) {
         var7 = var10.getWidth();
         var8 = var10.getHeight();
      }

      int var17 = 0;

      for(int var11 = 0; var11 < var2; ++var11) {
         if (var17 == var3) {
            ++var17;
         }

         boolean var12 = (float)var8 < (float)var2 / 2.0F;
         int var13 = Mth.floor((float)var2 / 2.0F - (float)var8 / 2.0F);
         if (var12 && var13 > var11) {
            var17 += var1;
            ++var11;
         }

         for(int var14 = 0; var14 < var1; ++var14) {
            if (!var5.hasNext()) {
               return;
            }

            var12 = (float)var7 < (float)var1 / 2.0F;
            var13 = Mth.floor((float)var1 / 2.0F - (float)var7 / 2.0F);
            int var15 = var7;
            boolean var16 = var14 < var7;
            if (var12) {
               var15 = var13 + var7;
               var16 = var13 <= var14 && var14 < var13 + var7;
            }

            if (var16) {
               this.addItemToSlot(var5, var17, var6, var11, var14);
            } else if (var15 == var14) {
               var17 += var1 - var14;
               break;
            }

            ++var17;
         }
      }
   }

   void addItemToSlot(Iterator<T> var1, int var2, int var3, int var4, int var5);
}
