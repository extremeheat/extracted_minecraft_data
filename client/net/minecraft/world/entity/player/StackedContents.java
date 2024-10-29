package net.minecraft.world.entity.player;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class StackedContents<T> {
   public final Reference2IntOpenHashMap<T> amounts = new Reference2IntOpenHashMap();

   public StackedContents() {
      super();
   }

   boolean hasAnyAmount(T var1) {
      return this.amounts.getInt(var1) > 0;
   }

   boolean hasAtLeast(T var1, int var2) {
      return this.amounts.getInt(var1) >= var2;
   }

   void take(T var1, int var2) {
      int var3 = this.amounts.addTo(var1, -var2);
      if (var3 < var2) {
         throw new IllegalStateException("Took " + var2 + " items, but only had " + var3);
      }
   }

   void put(T var1, int var2) {
      this.amounts.addTo(var1, var2);
   }

   public boolean tryPick(List<IngredientInfo<T>> var1, int var2, @Nullable Output<T> var3) {
      return (new RecipePicker(var1)).tryPick(var2, var3);
   }

   public int tryPickAll(List<IngredientInfo<T>> var1, int var2, @Nullable Output<T> var3) {
      return (new RecipePicker(var1)).tryPickAll(var2, var3);
   }

   public void clear() {
      this.amounts.clear();
   }

   public void account(T var1, int var2) {
      this.put(var1, var2);
   }

   private class RecipePicker {
      private final List<IngredientInfo<T>> ingredients;
      private final int ingredientCount;
      private final List<T> items;
      private final int itemCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(final List<IngredientInfo<T>> var2) {
         super();
         this.ingredients = var2;
         this.ingredientCount = this.ingredients.size();
         this.items = this.getUniqueAvailableIngredientItems();
         this.itemCount = this.items.size();
         this.data = new BitSet(this.visitedIngredientCount() + this.visitedItemCount() + this.satisfiedCount() + this.connectionCount() + this.residualCount());
         this.setInitialConnections();
      }

      private void setInitialConnections() {
         for(int var1 = 0; var1 < this.ingredientCount; ++var1) {
            List var2 = ((IngredientInfo)this.ingredients.get(var1)).allowedItems();

            for(int var3 = 0; var3 < this.itemCount; ++var3) {
               if (var2.contains(this.items.get(var3))) {
                  this.setConnection(var3, var1);
               }
            }
         }

      }

      public boolean tryPick(int var1, @Nullable Output<T> var2) {
         if (var1 <= 0) {
            return true;
         } else {
            int var3 = 0;

            while(true) {
               IntList var4 = this.tryAssigningNewItem(var1);
               int var6;
               int var7;
               if (var4 == null) {
                  boolean var10 = var3 == this.ingredientCount;
                  boolean var11 = var10 && var2 != null;
                  this.clearAllVisited();
                  this.clearSatisfied();

                  for(var6 = 0; var6 < this.ingredientCount; ++var6) {
                     for(var7 = 0; var7 < this.itemCount; ++var7) {
                        if (this.isAssigned(var7, var6)) {
                           this.unassign(var7, var6);
                           StackedContents.this.put(this.items.get(var7), var1);
                           if (var11) {
                              var2.accept(this.items.get(var7));
                           }
                           break;
                        }
                     }
                  }

                  assert this.data.get(this.residualOffset(), this.residualOffset() + this.residualCount()).isEmpty();

                  return var10;
               }

               int var5 = var4.getInt(0);
               StackedContents.this.take(this.items.get(var5), var1);
               var6 = var4.size() - 1;
               this.setSatisfied(var4.getInt(var6));
               ++var3;

               for(var7 = 0; var7 < var4.size() - 1; ++var7) {
                  int var8;
                  int var9;
                  if (isPathIndexItem(var7)) {
                     var8 = var4.getInt(var7);
                     var9 = var4.getInt(var7 + 1);
                     this.assign(var8, var9);
                  } else {
                     var8 = var4.getInt(var7 + 1);
                     var9 = var4.getInt(var7);
                     this.unassign(var8, var9);
                  }
               }
            }
         }
      }

      private static boolean isPathIndexItem(int var0) {
         return (var0 & 1) == 0;
      }

      private List<T> getUniqueAvailableIngredientItems() {
         ReferenceOpenHashSet var1 = new ReferenceOpenHashSet();
         Iterator var2 = this.ingredients.iterator();

         while(var2.hasNext()) {
            IngredientInfo var3 = (IngredientInfo)var2.next();
            var1.addAll(var3.allowedItems());
         }

         var1.removeIf((var1x) -> {
            return !StackedContents.this.hasAnyAmount(var1x);
         });
         return List.copyOf(var1);
      }

      @Nullable
      private IntList tryAssigningNewItem(int var1) {
         this.clearAllVisited();

         for(int var2 = 0; var2 < this.itemCount; ++var2) {
            if (StackedContents.this.hasAtLeast(this.items.get(var2), var1)) {
               IntList var3 = this.findNewItemAssignmentPath(var2);
               if (var3 != null) {
                  return var3;
               }
            }
         }

         return null;
      }

      @Nullable
      private IntList findNewItemAssignmentPath(int var1) {
         this.path.clear();
         this.visitItem(var1);
         this.path.add(var1);

         while(!this.path.isEmpty()) {
            int var2 = this.path.size();
            int var3;
            int var4;
            if (isPathIndexItem(var2 - 1)) {
               var3 = this.path.getInt(var2 - 1);

               for(var4 = 0; var4 < this.ingredientCount; ++var4) {
                  if (!this.hasVisitedIngredient(var4) && this.hasConnection(var3, var4) && !this.isAssigned(var3, var4)) {
                     this.visitIngredient(var4);
                     this.path.add(var4);
                     break;
                  }
               }
            } else {
               var3 = this.path.getInt(var2 - 1);
               if (!this.isSatisfied(var3)) {
                  return this.path;
               }

               for(var4 = 0; var4 < this.itemCount; ++var4) {
                  if (!this.hasVisitedItem(var4) && this.isAssigned(var4, var3)) {
                     assert this.hasConnection(var4, var3);

                     this.visitItem(var4);
                     this.path.add(var4);
                     break;
                  }
               }
            }

            var3 = this.path.size();
            if (var3 == var2) {
               this.path.removeInt(var3 - 1);
            }
         }

         return null;
      }

      private int visitedIngredientOffset() {
         return 0;
      }

      private int visitedIngredientCount() {
         return this.ingredientCount;
      }

      private int visitedItemOffset() {
         return this.visitedIngredientOffset() + this.visitedIngredientCount();
      }

      private int visitedItemCount() {
         return this.itemCount;
      }

      private int satisfiedOffset() {
         return this.visitedItemOffset() + this.visitedItemCount();
      }

      private int satisfiedCount() {
         return this.ingredientCount;
      }

      private int connectionOffset() {
         return this.satisfiedOffset() + this.satisfiedCount();
      }

      private int connectionCount() {
         return this.ingredientCount * this.itemCount;
      }

      private int residualOffset() {
         return this.connectionOffset() + this.connectionCount();
      }

      private int residualCount() {
         return this.ingredientCount * this.itemCount;
      }

      private boolean isSatisfied(int var1) {
         return this.data.get(this.getSatisfiedIndex(var1));
      }

      private void setSatisfied(int var1) {
         this.data.set(this.getSatisfiedIndex(var1));
      }

      private int getSatisfiedIndex(int var1) {
         assert var1 >= 0 && var1 < this.ingredientCount;

         return this.satisfiedOffset() + var1;
      }

      private void clearSatisfied() {
         this.clearRange(this.satisfiedOffset(), this.satisfiedCount());
      }

      private void setConnection(int var1, int var2) {
         this.data.set(this.getConnectionIndex(var1, var2));
      }

      private boolean hasConnection(int var1, int var2) {
         return this.data.get(this.getConnectionIndex(var1, var2));
      }

      private int getConnectionIndex(int var1, int var2) {
         assert var1 >= 0 && var1 < this.itemCount;

         assert var2 >= 0 && var2 < this.ingredientCount;

         return this.connectionOffset() + var1 * this.ingredientCount + var2;
      }

      private boolean isAssigned(int var1, int var2) {
         return this.data.get(this.getResidualIndex(var1, var2));
      }

      private void assign(int var1, int var2) {
         int var3 = this.getResidualIndex(var1, var2);

         assert !this.data.get(var3);

         this.data.set(var3);
      }

      private void unassign(int var1, int var2) {
         int var3 = this.getResidualIndex(var1, var2);

         assert this.data.get(var3);

         this.data.clear(var3);
      }

      private int getResidualIndex(int var1, int var2) {
         assert var1 >= 0 && var1 < this.itemCount;

         assert var2 >= 0 && var2 < this.ingredientCount;

         return this.residualOffset() + var1 * this.ingredientCount + var2;
      }

      private void visitIngredient(int var1) {
         this.data.set(this.getVisitedIngredientIndex(var1));
      }

      private boolean hasVisitedIngredient(int var1) {
         return this.data.get(this.getVisitedIngredientIndex(var1));
      }

      private int getVisitedIngredientIndex(int var1) {
         assert var1 >= 0 && var1 < this.ingredientCount;

         return this.visitedIngredientOffset() + var1;
      }

      private void visitItem(int var1) {
         this.data.set(this.getVisitiedItemIndex(var1));
      }

      private boolean hasVisitedItem(int var1) {
         return this.data.get(this.getVisitiedItemIndex(var1));
      }

      private int getVisitiedItemIndex(int var1) {
         assert var1 >= 0 && var1 < this.itemCount;

         return this.visitedItemOffset() + var1;
      }

      private void clearAllVisited() {
         this.clearRange(this.visitedIngredientOffset(), this.visitedIngredientCount());
         this.clearRange(this.visitedItemOffset(), this.visitedItemCount());
      }

      private void clearRange(int var1, int var2) {
         this.data.clear(var1, var1 + var2);
      }

      public int tryPickAll(int var1, @Nullable Output<T> var2) {
         int var3 = 0;
         int var4 = Math.min(var1, this.getMinIngredientCount()) + 1;

         while(true) {
            while(true) {
               int var5 = (var3 + var4) / 2;
               if (this.tryPick(var5, (Output)null)) {
                  if (var4 - var3 <= 1) {
                     if (var5 > 0) {
                        this.tryPick(var5, var2);
                     }

                     return var5;
                  }

                  var3 = var5;
               } else {
                  var4 = var5;
               }
            }
         }
      }

      private int getMinIngredientCount() {
         int var1 = 2147483647;
         Iterator var2 = this.ingredients.iterator();

         while(var2.hasNext()) {
            IngredientInfo var3 = (IngredientInfo)var2.next();
            int var4 = 0;

            Object var6;
            for(Iterator var5 = var3.allowedItems().iterator(); var5.hasNext(); var4 = Math.max(var4, StackedContents.this.amounts.getInt(var6))) {
               var6 = var5.next();
            }

            if (var1 > 0) {
               var1 = Math.min(var1, var4);
            }
         }

         return var1;
      }
   }

   @FunctionalInterface
   public interface Output<T> {
      void accept(T var1);
   }

   public static record IngredientInfo<T>(List<T> allowedItems) {
      public IngredientInfo(List<T> var1) {
         super();
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("Ingredients can't be empty");
         } else {
            this.allowedItems = var1;
         }
      }

      public List<T> allowedItems() {
         return this.allowedItems;
      }
   }
}
