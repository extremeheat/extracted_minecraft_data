package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.Iterator;
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
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         ItemStack var6 = (ItemStack)var5.next();
         if (!var6.isEmpty()) {
            ++var4;
            this.stackedContents.accountStack(var6, 1);
         }
      }

      this.ingredientCount = var4;
   }

   public static CraftingInput of(int var0, int var1, List<ItemStack> var2) {
      return ofPositioned(var0, var1, var2).input();
   }

   public static Positioned ofPositioned(int var0, int var1, List<ItemStack> var2) {
      if (var0 != 0 && var1 != 0) {
         int var3 = var0 - 1;
         int var4 = 0;
         int var5 = var1 - 1;
         int var6 = 0;

         int var7;
         for(var7 = 0; var7 < var1; ++var7) {
            boolean var8 = true;

            for(int var9 = 0; var9 < var0; ++var9) {
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

         var7 = var4 - var3 + 1;
         int var13 = var6 - var5 + 1;
         if (var7 > 0 && var13 > 0) {
            if (var7 == var0 && var13 == var1) {
               return new Positioned(new CraftingInput(var0, var1, var2), var3, var5);
            } else {
               ArrayList var14 = new ArrayList(var7 * var13);

               for(int var15 = 0; var15 < var13; ++var15) {
                  for(int var11 = 0; var11 < var7; ++var11) {
                     int var12 = var11 + var3 + (var15 + var5) * var0;
                     var14.add((ItemStack)var2.get(var12));
                  }
               }

               return new Positioned(new CraftingInput(var7, var13, var14), var3, var5);
            }
         } else {
            return CraftingInput.Positioned.EMPTY;
         }
      } else {
         return CraftingInput.Positioned.EMPTY;
      }
   }

   public ItemStack getItem(int var1) {
      return (ItemStack)this.items.get(var1);
   }

   public ItemStack getItem(int var1, int var2) {
      return (ItemStack)this.items.get(var1 + var2 * this.width);
   }

   public int size() {
      return this.items.size();
   }

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

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CraftingInput)) {
         return false;
      } else {
         CraftingInput var2 = (CraftingInput)var1;
         return this.width == var2.width && this.height == var2.height && this.ingredientCount == var2.ingredientCount && ItemStack.listMatches(this.items, var2.items);
      }
   }

   public int hashCode() {
      int var1 = ItemStack.hashStackList(this.items);
      var1 = 31 * var1 + this.width;
      var1 = 31 * var1 + this.height;
      return var1;
   }

   public static record Positioned(CraftingInput input, int left, int top) {
      public static final Positioned EMPTY;

      public Positioned(CraftingInput var1, int var2, int var3) {
         super();
         this.input = var1;
         this.left = var2;
         this.top = var3;
      }

      public CraftingInput input() {
         return this.input;
      }

      public int left() {
         return this.left;
      }

      public int top() {
         return this.top;
      }

      static {
         EMPTY = new Positioned(CraftingInput.EMPTY, 0, 0);
      }
   }
}
