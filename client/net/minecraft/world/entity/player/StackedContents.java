package net.minecraft.world.entity.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class StackedContents {
   private static final int EMPTY = 0;
   public final Int2IntMap contents = new Int2IntOpenHashMap();

   public StackedContents() {
      super();
   }

   public void accountSimpleStack(ItemStack var1) {
      if (!var1.isDamaged() && !var1.isEnchanted() && !var1.hasCustomHoverName()) {
         this.accountStack(var1);
      }
   }

   public void accountStack(ItemStack var1) {
      this.accountStack(var1, 64);
   }

   public void accountStack(ItemStack var1, int var2) {
      if (!var1.isEmpty()) {
         int var3 = getStackingIndex(var1);
         int var4 = Math.min(var2, var1.getCount());
         this.put(var3, var4);
      }
   }

   public static int getStackingIndex(ItemStack var0) {
      return Registry.ITEM.getId(var0.getItem());
   }

   boolean has(int var1) {
      return this.contents.get(var1) > 0;
   }

   int take(int var1, int var2) {
      int var3 = this.contents.get(var1);
      if (var3 >= var2) {
         this.contents.put(var1, var3 - var2);
         return var1;
      } else {
         return 0;
      }
   }

   void put(int var1, int var2) {
      this.contents.put(var1, this.contents.get(var1) + var2);
   }

   public boolean canCraft(Recipe<?> var1, @Nullable IntList var2) {
      return this.canCraft(var1, var2, 1);
   }

   public boolean canCraft(Recipe<?> var1, @Nullable IntList var2, int var3) {
      return new StackedContents.RecipePicker(var1).tryPick(var3, var2);
   }

   public int getBiggestCraftableStack(Recipe<?> var1, @Nullable IntList var2) {
      return this.getBiggestCraftableStack(var1, 2147483647, var2);
   }

   public int getBiggestCraftableStack(Recipe<?> var1, int var2, @Nullable IntList var3) {
      return new StackedContents.RecipePicker(var1).tryPickAll(var2, var3);
   }

   public static ItemStack fromStackingIndex(int var0) {
      return var0 == 0 ? ItemStack.EMPTY : new ItemStack(Item.byId(var0));
   }

   public void clear() {
      this.contents.clear();
   }

   class RecipePicker {
      private final Recipe<?> recipe;
      private final List<Ingredient> ingredients = Lists.newArrayList();
      private final int ingredientCount;
      private final int[] items;
      private final int itemCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(Recipe<?> var2) {
         super();
         this.recipe = var2;
         this.ingredients.addAll(var2.getIngredients());
         this.ingredients.removeIf(Ingredient::isEmpty);
         this.ingredientCount = this.ingredients.size();
         this.items = this.getUniqueAvailableIngredientItems();
         this.itemCount = this.items.length;
         this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);

         for(int var3 = 0; var3 < this.ingredients.size(); ++var3) {
            IntList var4 = this.ingredients.get(var3).getStackingIds();

            for(int var5 = 0; var5 < this.itemCount; ++var5) {
               if (var4.contains(this.items[var5])) {
                  this.data.set(this.getIndex(true, var5, var3));
               }
            }
         }
      }

      public boolean tryPick(int var1, @Nullable IntList var2) {
         if (var1 <= 0) {
            return true;
         } else {
            int var3;
            for(var3 = 0; this.dfs(var1); ++var3) {
               StackedContents.this.take(this.items[this.path.getInt(0)], var1);
               int var4 = this.path.size() - 1;
               this.setSatisfied(this.path.getInt(var4));

               for(int var5 = 0; var5 < var4; ++var5) {
                  this.toggleResidual((var5 & 1) == 0, this.path.get(var5), this.path.get(var5 + 1));
               }

               this.path.clear();
               this.data.clear(0, this.ingredientCount + this.itemCount);
            }

            boolean var10 = var3 == this.ingredientCount;
            boolean var11 = var10 && var2 != null;
            if (var11) {
               var2.clear();
            }

            this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
            int var6 = 0;
            NonNullList var7 = this.recipe.getIngredients();

            for(int var8 = 0; var8 < var7.size(); ++var8) {
               if (var11 && ((Ingredient)var7.get(var8)).isEmpty()) {
                  var2.add(0);
               } else {
                  for(int var9 = 0; var9 < this.itemCount; ++var9) {
                     if (this.hasResidual(false, var6, var9)) {
                        this.toggleResidual(true, var9, var6);
                        StackedContents.this.put(this.items[var9], var1);
                        if (var11) {
                           var2.add(this.items[var9]);
                        }
                     }
                  }

                  ++var6;
               }
            }

            return var10;
         }
      }

      private int[] getUniqueAvailableIngredientItems() {
         IntAVLTreeSet var1 = new IntAVLTreeSet();

         for(Ingredient var3 : this.ingredients) {
            var1.addAll(var3.getStackingIds());
         }

         IntIterator var4 = var1.iterator();

         while(var4.hasNext()) {
            if (!StackedContents.this.has(var4.nextInt())) {
               var4.remove();
            }
         }

         return var1.toIntArray();
      }

      private boolean dfs(int var1) {
         int var2 = this.itemCount;

         for(int var3 = 0; var3 < var2; ++var3) {
            if (StackedContents.this.contents.get(this.items[var3]) >= var1) {
               this.visit(false, var3);

               while(!this.path.isEmpty()) {
                  int var4 = this.path.size();
                  boolean var5 = (var4 & 1) == 1;
                  int var6 = this.path.getInt(var4 - 1);
                  if (!var5 && !this.isSatisfied(var6)) {
                     break;
                  }

                  int var7 = var5 ? this.ingredientCount : var2;
                  int var8 = 0;

                  while(true) {
                     if (var8 < var7) {
                        if (this.hasVisited(var5, var8) || !this.hasConnection(var5, var6, var8) || !this.hasResidual(var5, var6, var8)) {
                           ++var8;
                           continue;
                        }

                        this.visit(var5, var8);
                     }

                     var8 = this.path.size();
                     if (var8 == var4) {
                        this.path.removeInt(var8 - 1);
                     }
                     break;
                  }
               }

               if (!this.path.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isSatisfied(int var1) {
         return this.data.get(this.getSatisfiedIndex(var1));
      }

      private void setSatisfied(int var1) {
         this.data.set(this.getSatisfiedIndex(var1));
      }

      private int getSatisfiedIndex(int var1) {
         return this.ingredientCount + this.itemCount + var1;
      }

      private boolean hasConnection(boolean var1, int var2, int var3) {
         return this.data.get(this.getIndex(var1, var2, var3));
      }

      private boolean hasResidual(boolean var1, int var2, int var3) {
         return var1 != this.data.get(1 + this.getIndex(var1, var2, var3));
      }

      private void toggleResidual(boolean var1, int var2, int var3) {
         this.data.flip(1 + this.getIndex(var1, var2, var3));
      }

      private int getIndex(boolean var1, int var2, int var3) {
         int var4 = var1 ? var2 * this.ingredientCount + var3 : var3 * this.ingredientCount + var2;
         return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * var4;
      }

      private void visit(boolean var1, int var2) {
         this.data.set(this.getVisitedIndex(var1, var2));
         this.path.add(var2);
      }

      private boolean hasVisited(boolean var1, int var2) {
         return this.data.get(this.getVisitedIndex(var1, var2));
      }

      private int getVisitedIndex(boolean var1, int var2) {
         return (var1 ? 0 : this.ingredientCount) + var2;
      }

      public int tryPickAll(int var1, @Nullable IntList var2) {
         int var3 = 0;
         int var4 = Math.min(var1, this.getMinIngredientCount()) + 1;

         while(true) {
            int var5 = (var3 + var4) / 2;
            if (this.tryPick(var5, null)) {
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

      private int getMinIngredientCount() {
         int var1 = 2147483647;

         for(Ingredient var3 : this.ingredients) {
            int var4 = 0;

            int var6;
            for(IntListIterator var5 = var3.getStackingIds().iterator(); var5.hasNext(); var4 = Math.max(var4, StackedContents.this.contents.get(var6))) {
               var6 = var5.next();
            }

            if (var1 > 0) {
               var1 = Math.min(var1, var4);
            }
         }

         return var1;
      }
   }
}
