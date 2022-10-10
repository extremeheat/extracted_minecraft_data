package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;

public class RecipeItemHelper {
   public final Int2IntMap field_194124_a = new Int2IntOpenHashMap();

   public RecipeItemHelper() {
      super();
   }

   public void func_195932_a(ItemStack var1) {
      if (!var1.func_77951_h() && !var1.func_77948_v() && !var1.func_82837_s()) {
         this.func_194112_a(var1);
      }

   }

   public void func_194112_a(ItemStack var1) {
      if (!var1.func_190926_b()) {
         int var2 = func_194113_b(var1);
         int var3 = var1.func_190916_E();
         this.func_194117_b(var2, var3);
      }

   }

   public static int func_194113_b(ItemStack var0) {
      return IRegistry.field_212630_s.func_148757_b(var0.func_77973_b());
   }

   public boolean func_194120_a(int var1) {
      return this.field_194124_a.get(var1) > 0;
   }

   public int func_194122_a(int var1, int var2) {
      int var3 = this.field_194124_a.get(var1);
      if (var3 >= var2) {
         this.field_194124_a.put(var1, var3 - var2);
         return var1;
      } else {
         return 0;
      }
   }

   private void func_194117_b(int var1, int var2) {
      this.field_194124_a.put(var1, this.field_194124_a.get(var1) + var2);
   }

   public boolean func_194116_a(IRecipe var1, @Nullable IntList var2) {
      return this.func_194118_a(var1, var2, 1);
   }

   public boolean func_194118_a(IRecipe var1, @Nullable IntList var2, int var3) {
      return (new RecipeItemHelper.RecipePicker(var1)).func_194092_a(var3, var2);
   }

   public int func_194114_b(IRecipe var1, @Nullable IntList var2) {
      return this.func_194121_a(var1, 2147483647, var2);
   }

   public int func_194121_a(IRecipe var1, int var2, @Nullable IntList var3) {
      return (new RecipeItemHelper.RecipePicker(var1)).func_194102_b(var2, var3);
   }

   public static ItemStack func_194115_b(int var0) {
      return var0 == 0 ? ItemStack.field_190927_a : new ItemStack(Item.func_150899_d(var0));
   }

   public void func_194119_a() {
      this.field_194124_a.clear();
   }

   class RecipePicker {
      private final IRecipe field_194105_b;
      private final List<Ingredient> field_194106_c = Lists.newArrayList();
      private final int field_194107_d;
      private final int[] field_194108_e;
      private final int field_194109_f;
      private final BitSet field_194110_g;
      private final IntList field_194111_h = new IntArrayList();

      public RecipePicker(IRecipe var2) {
         super();
         this.field_194105_b = var2;
         this.field_194106_c.addAll(var2.func_192400_c());
         this.field_194106_c.removeIf(Ingredient::func_203189_d);
         this.field_194107_d = this.field_194106_c.size();
         this.field_194108_e = this.func_194097_a();
         this.field_194109_f = this.field_194108_e.length;
         this.field_194110_g = new BitSet(this.field_194107_d + this.field_194109_f + this.field_194107_d + this.field_194107_d * this.field_194109_f);

         for(int var3 = 0; var3 < this.field_194106_c.size(); ++var3) {
            IntList var4 = ((Ingredient)this.field_194106_c.get(var3)).func_194139_b();

            for(int var5 = 0; var5 < this.field_194109_f; ++var5) {
               if (var4.contains(this.field_194108_e[var5])) {
                  this.field_194110_g.set(this.func_194095_d(true, var5, var3));
               }
            }
         }

      }

      public boolean func_194092_a(int var1, @Nullable IntList var2) {
         if (var1 <= 0) {
            return true;
         } else {
            int var3;
            for(var3 = 0; this.func_194098_a(var1); ++var3) {
               RecipeItemHelper.this.func_194122_a(this.field_194108_e[this.field_194111_h.getInt(0)], var1);
               int var4 = this.field_194111_h.size() - 1;
               this.func_194096_c(this.field_194111_h.getInt(var4));

               for(int var5 = 0; var5 < var4; ++var5) {
                  this.func_194089_c((var5 & 1) == 0, this.field_194111_h.get(var5), this.field_194111_h.get(var5 + 1));
               }

               this.field_194111_h.clear();
               this.field_194110_g.clear(0, this.field_194107_d + this.field_194109_f);
            }

            boolean var10 = var3 == this.field_194107_d;
            boolean var11 = var10 && var2 != null;
            if (var11) {
               var2.clear();
            }

            this.field_194110_g.clear(0, this.field_194107_d + this.field_194109_f + this.field_194107_d);
            int var6 = 0;
            NonNullList var7 = this.field_194105_b.func_192400_c();

            for(int var8 = 0; var8 < var7.size(); ++var8) {
               if (var11 && ((Ingredient)var7.get(var8)).func_203189_d()) {
                  var2.add(0);
               } else {
                  for(int var9 = 0; var9 < this.field_194109_f; ++var9) {
                     if (this.func_194100_b(false, var6, var9)) {
                        this.func_194089_c(true, var9, var6);
                        RecipeItemHelper.this.func_194117_b(this.field_194108_e[var9], var1);
                        if (var11) {
                           var2.add(this.field_194108_e[var9]);
                        }
                     }
                  }

                  ++var6;
               }
            }

            return var10;
         }
      }

      private int[] func_194097_a() {
         IntAVLTreeSet var1 = new IntAVLTreeSet();
         Iterator var2 = this.field_194106_c.iterator();

         while(var2.hasNext()) {
            Ingredient var3 = (Ingredient)var2.next();
            var1.addAll(var3.func_194139_b());
         }

         IntIterator var4 = var1.iterator();

         while(var4.hasNext()) {
            if (!RecipeItemHelper.this.func_194120_a(var4.nextInt())) {
               var4.remove();
            }
         }

         return var1.toIntArray();
      }

      private boolean func_194098_a(int var1) {
         int var2 = this.field_194109_f;

         for(int var3 = 0; var3 < var2; ++var3) {
            if (RecipeItemHelper.this.field_194124_a.get(this.field_194108_e[var3]) >= var1) {
               this.func_194088_a(false, var3);

               while(!this.field_194111_h.isEmpty()) {
                  int var4 = this.field_194111_h.size();
                  boolean var5 = (var4 & 1) == 1;
                  int var6 = this.field_194111_h.getInt(var4 - 1);
                  if (!var5 && !this.func_194091_b(var6)) {
                     break;
                  }

                  int var7 = var5 ? this.field_194107_d : var2;

                  int var8;
                  for(var8 = 0; var8 < var7; ++var8) {
                     if (!this.func_194101_b(var5, var8) && this.func_194093_a(var5, var6, var8) && this.func_194100_b(var5, var6, var8)) {
                        this.func_194088_a(var5, var8);
                        break;
                     }
                  }

                  var8 = this.field_194111_h.size();
                  if (var8 == var4) {
                     this.field_194111_h.removeInt(var8 - 1);
                  }
               }

               if (!this.field_194111_h.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean func_194091_b(int var1) {
         return this.field_194110_g.get(this.func_194094_d(var1));
      }

      private void func_194096_c(int var1) {
         this.field_194110_g.set(this.func_194094_d(var1));
      }

      private int func_194094_d(int var1) {
         return this.field_194107_d + this.field_194109_f + var1;
      }

      private boolean func_194093_a(boolean var1, int var2, int var3) {
         return this.field_194110_g.get(this.func_194095_d(var1, var2, var3));
      }

      private boolean func_194100_b(boolean var1, int var2, int var3) {
         return var1 != this.field_194110_g.get(1 + this.func_194095_d(var1, var2, var3));
      }

      private void func_194089_c(boolean var1, int var2, int var3) {
         this.field_194110_g.flip(1 + this.func_194095_d(var1, var2, var3));
      }

      private int func_194095_d(boolean var1, int var2, int var3) {
         int var4 = var1 ? var2 * this.field_194107_d + var3 : var3 * this.field_194107_d + var2;
         return this.field_194107_d + this.field_194109_f + this.field_194107_d + 2 * var4;
      }

      private void func_194088_a(boolean var1, int var2) {
         this.field_194110_g.set(this.func_194099_c(var1, var2));
         this.field_194111_h.add(var2);
      }

      private boolean func_194101_b(boolean var1, int var2) {
         return this.field_194110_g.get(this.func_194099_c(var1, var2));
      }

      private int func_194099_c(boolean var1, int var2) {
         return (var1 ? 0 : this.field_194107_d) + var2;
      }

      public int func_194102_b(int var1, @Nullable IntList var2) {
         int var3 = 0;
         int var4 = Math.min(var1, this.func_194090_b()) + 1;

         while(true) {
            while(true) {
               int var5 = (var3 + var4) / 2;
               if (this.func_194092_a(var5, (IntList)null)) {
                  if (var4 - var3 <= 1) {
                     if (var5 > 0) {
                        this.func_194092_a(var5, var2);
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

      private int func_194090_b() {
         int var1 = 2147483647;
         Iterator var2 = this.field_194106_c.iterator();

         while(var2.hasNext()) {
            Ingredient var3 = (Ingredient)var2.next();
            int var4 = 0;

            int var6;
            for(IntListIterator var5 = var3.func_194139_b().iterator(); var5.hasNext(); var4 = Math.max(var4, RecipeItemHelper.this.field_194124_a.get(var6))) {
               var6 = (Integer)var5.next();
            }

            if (var1 > 0) {
               var1 = Math.min(var1, var4);
            }
         }

         return var1;
      }
   }
}
