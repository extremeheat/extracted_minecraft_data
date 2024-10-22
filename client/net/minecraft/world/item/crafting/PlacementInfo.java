package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;

public class PlacementInfo {
   public static final PlacementInfo NOT_PLACEABLE = new PlacementInfo(List.of(), List.of(), List.of());
   private final List<Ingredient> ingredients;
   private final List<StackedContents.IngredientInfo<Holder<Item>>> unpackedIngredients;
   private final List<Optional<PlacementInfo.SlotInfo>> slotInfo;

   private PlacementInfo(List<Ingredient> var1, List<StackedContents.IngredientInfo<Holder<Item>>> var2, List<Optional<PlacementInfo.SlotInfo>> var3) {
      super();
      this.ingredients = var1;
      this.unpackedIngredients = var2;
      this.slotInfo = var3;
   }

   public static StackedContents.IngredientInfo<Holder<Item>> ingredientToContents(Ingredient var0) {
      return StackedItemContents.convertIngredientContents(var0.items().stream());
   }

   public static PlacementInfo create(Ingredient var0) {
      if (var0.items().isEmpty()) {
         return NOT_PLACEABLE;
      } else {
         StackedContents.IngredientInfo var1 = ingredientToContents(var0);
         PlacementInfo.SlotInfo var2 = new PlacementInfo.SlotInfo(0);
         return new PlacementInfo(List.of(var0), List.of(var1), List.of(Optional.of(var2)));
      }
   }

   public static PlacementInfo createFromOptionals(List<Optional<Ingredient>> var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList(var1);
      ArrayList var3 = new ArrayList(var1);
      ArrayList var4 = new ArrayList(var1);
      int var5 = 0;

      for (Optional var7 : var0) {
         if (var7.isPresent()) {
            Ingredient var8 = (Ingredient)var7.get();
            if (var8.items().isEmpty()) {
               return NOT_PLACEABLE;
            }

            var2.add(var8);
            var3.add(ingredientToContents(var8));
            var4.add(Optional.of(new PlacementInfo.SlotInfo(var5++)));
         } else {
            var4.add(Optional.empty());
         }
      }

      return new PlacementInfo(var2, var3, var4);
   }

   public static PlacementInfo create(List<Ingredient> var0) {
      int var1 = var0.size();
      ArrayList var2 = new ArrayList(var1);
      ArrayList var3 = new ArrayList(var1);

      for (int var4 = 0; var4 < var1; var4++) {
         Ingredient var5 = (Ingredient)var0.get(var4);
         if (var5.items().isEmpty()) {
            return NOT_PLACEABLE;
         }

         var2.add(ingredientToContents(var5));
         var3.add(Optional.of(new PlacementInfo.SlotInfo(var4)));
      }

      return new PlacementInfo(var0, var2, var3);
   }

   public List<Optional<PlacementInfo.SlotInfo>> slotInfo() {
      return this.slotInfo;
   }

   public List<Ingredient> ingredients() {
      return this.ingredients;
   }

   public List<StackedContents.IngredientInfo<Holder<Item>>> unpackedIngredients() {
      return this.unpackedIngredients;
   }

   public boolean isImpossibleToPlace() {
      return this.slotInfo.isEmpty();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
