package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;

public class CraftingInput implements RecipeInput {
   public static final CraftingInput EMPTY = new CraftingInput(0, 0, List.of());
   private final int width;
   private final int height;
   private final List<ItemStack> items;
   private final StackedItemContents stackedContents = new StackedItemContents();
   private final int ingredientCount;

   private CraftingInput(int var1, int var2, List<ItemStack> var3) {
      super();
      this.width = var1;
      this.height = var2;
      this.items = var3;
      int var4 = 0;

      for (ItemStack var6 : var3) {
         if (!var6.isEmpty()) {
            var4++;
            this.stackedContents.accountStack(var6, 1);
         }
      }

      this.ingredientCount = var4;
   }

   public static CraftingInput of(int var0, int var1, List<ItemStack> var2) {
      return ofPositioned(var0, var1, var2).input();
   }

   public static CraftingInput.Positioned ofPositioned(int var0, int var1, List<ItemStack> var2) {
      if (var0 != 0 && var1 != 0) {
         int var3 = var0 - 1;
         int var4 = 0;
         int var5 = var1 - 1;
         int var6 = 0;

         for (int var7 = 0; var7 < var1; var7++) {
            boolean var8 = true;

            for (int var9 = 0; var9 < var0; var9++) {
               ItemStack var10 = (ItemStack)var2.get(var9 + var7 * var0);
               if (!var10.isEmpty()) {
                  var3 = Math.min(var3, var9);
                  var4 = Math.max(var4, var9);
                  var8 = false;
               }
            }

            if (!var8) {
               var5 = Math.min(var5, var7);
               var6 = Math.max(var6, var7);
            }
         }

         int var13 = var4 - var3 + 1;
         int var14 = var6 - var5 + 1;
         if (var13 <= 0 || var14 <= 0) {
            return CraftingInput.Positioned.EMPTY;
         } else if (var13 == var0 && var14 == var1) {
            return new CraftingInput.Positioned(new CraftingInput(var0, var1, var2), var3, var5);
         } else {
            ArrayList var15 = new ArrayList(var13 * var14);

            for (int var16 = 0; var16 < var14; var16++) {
               for (int var11 = 0; var11 < var13; var11++) {
                  int var12 = var11 + var3 + (var16 + var5) * var0;
                  var15.add((ItemStack)var2.get(var12));
               }
            }

            return new CraftingInput.Positioned(new CraftingInput(var13, var14, var15), var3, var5);
         }
      } else {
         return CraftingInput.Positioned.EMPTY;
      }
   }

   @Override
   public ItemStack getItem(int var1) {
      return this.items.get(var1);
   }

   public ItemStack getItem(int var1, int var2) {
      return this.items.get(var1 + var2 * this.width);
   }

   @Override
   public int size() {
      return this.items.size();
   }

   @Override
   public boolean isEmpty() {
      return this.ingredientCount == 0;
   }

   public StackedItemContents stackedContents() {
      return this.stackedContents;
   }

   public List<ItemStack> items() {
      return this.items;
   }

   public int ingredientCount() {
      return this.ingredientCount;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return !(var1 instanceof CraftingInput var2)
            ? false
            : this.width == var2.width
               && this.height == var2.height
               && this.ingredientCount == var2.ingredientCount
               && ItemStack.listMatches(this.items, var2.items);
      }
   }

   @Override
   public int hashCode() {
      int var1 = ItemStack.hashStackList(this.items);
      var1 = 31 * var1 + this.width;
      return 31 * var1 + this.height;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
