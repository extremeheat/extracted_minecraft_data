package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlacementInfo {
   public static final int EMPTY_SLOT = -1;
   public static final PlacementInfo NOT_PLACEABLE = new PlacementInfo(List.of(), IntList.of());
   private final List<Ingredient> ingredients;
   private final IntList slotsToIngredientIndex;

   private PlacementInfo(List<Ingredient> var1, IntList var2) {
      super();
      this.ingredients = var1;
      this.slotsToIngredientIndex = var2;
   }

   public static PlacementInfo create(Ingredient var0) {
      return var0.isEmpty() ? NOT_PLACEABLE : new PlacementInfo(List.of(var0), IntList.of(0));
   }

   public static PlacementInfo createFromOptionals(List<Optional<Ingredient>> var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList(var1);
      IntArrayList var3 = new IntArrayList(var1);
      int var4 = 0;

      for(Optional var6 : var0) {
         if (var6.isPresent()) {
            Ingredient var7 = (Ingredient)var6.get();
            if (var7.isEmpty()) {
               return NOT_PLACEABLE;
            }

            var2.add(var7);
            var3.add(var4++);
         } else {
            var3.add(-1);
         }
      }

      return new PlacementInfo(var2, var3);
   }

   public static PlacementInfo create(List<Ingredient> var0) {
      int var1 = var0.size();
      IntArrayList var2 = new IntArrayList(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         Ingredient var4 = (Ingredient)var0.get(var3);
         if (var4.isEmpty()) {
            return NOT_PLACEABLE;
         }

         var2.add(var3);
      }

      return new PlacementInfo(var0, var2);
   }

   public IntList slotsToIngredientIndex() {
      return this.slotsToIngredientIndex;
   }

   public List<Ingredient> ingredients() {
      return this.ingredients;
   }

   public boolean isImpossibleToPlace() {
      return this.slotsToIngredientIndex.isEmpty();
   }
}
