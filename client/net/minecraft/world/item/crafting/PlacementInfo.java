package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlacementInfo {
   public static final PlacementInfo NOT_PLACEABLE = new PlacementInfo(List.of(), List.of());
   private final List<StackedContents.IngredientInfo<Holder<Item>>> stackedIngredients;
   private final List<Optional<PlacementInfo.SlotInfo>> slotInfo;

   private PlacementInfo(List<StackedContents.IngredientInfo<Holder<Item>>> var1, List<Optional<PlacementInfo.SlotInfo>> var2) {
      super();
      this.stackedIngredients = var1;
      this.slotInfo = var2;
   }

   private static StackedContents.IngredientInfo<Holder<Item>> createStackedContents(List<ItemStack> var0) {
      return StackedItemContents.convertIngredientContents(var0.stream().map(ItemStack::getItemHolder));
   }

   private static List<ItemStack> createPossibleItems(Ingredient var0) {
      return var0.items().stream().map(ItemStack::new).toList();
   }

   public static PlacementInfo create(Ingredient var0) {
      List var1 = createPossibleItems(var0);
      if (var1.isEmpty()) {
         return NOT_PLACEABLE;
      } else {
         StackedContents.IngredientInfo var2 = createStackedContents(var1);
         PlacementInfo.SlotInfo var3 = new PlacementInfo.SlotInfo(var1, 0);
         return new PlacementInfo(List.of(var2), List.of(Optional.of(var3)));
      }
   }

   public static PlacementInfo createFromOptionals(List<Optional<Ingredient>> var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList(var1);
      ArrayList var3 = new ArrayList(var1);
      int var4 = 0;

      for (Optional var6 : var0) {
         if (var6.isPresent()) {
            List var7 = createPossibleItems((Ingredient)var6.get());
            if (var7.isEmpty()) {
               return NOT_PLACEABLE;
            }

            var2.add(createStackedContents(var7));
            var3.add(Optional.of(new PlacementInfo.SlotInfo(var7, var4++)));
         } else {
            var3.add(Optional.empty());
         }
      }

      return new PlacementInfo(var2, var3);
   }

   public static PlacementInfo create(List<Ingredient> var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList(var1);
      ArrayList var3 = new ArrayList(var1);

      for (int var4 = 0; var4 < var1; var4++) {
         Ingredient var5 = (Ingredient)var0.get(var4);
         List var6 = createPossibleItems(var5);
         if (var6.isEmpty()) {
            return NOT_PLACEABLE;
         }

         var2.add(createStackedContents(var6));
         var3.add(Optional.of(new PlacementInfo.SlotInfo(var6, var4)));
      }

      return new PlacementInfo(var2, var3);
   }

   public List<Optional<PlacementInfo.SlotInfo>> slotInfo() {
      return this.slotInfo;
   }

   public List<StackedContents.IngredientInfo<Holder<Item>>> stackedRecipeContents() {
      return this.stackedIngredients;
   }

   public boolean isImpossibleToPlace() {
      return this.slotInfo.isEmpty();
   }

   public static record SlotInfo(List<ItemStack> possibleItems, int placerOutputPosition) {
      public SlotInfo(List<ItemStack> possibleItems, int placerOutputPosition) {
         super();
         if (possibleItems.isEmpty()) {
            throw new IllegalArgumentException("Possible items list must be not empty");
         } else {
            this.possibleItems = possibleItems;
            this.placerOutputPosition = placerOutputPosition;
         }
      }
   }
}
