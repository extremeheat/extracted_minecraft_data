package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.util.BitArray;
import net.minecraft.util.datafix.TypeReferences;

public class LeavesFix extends DataFix {
   private static final int[][] field_208425_a = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final Object2IntMap<String> field_208434_j = (Object2IntMap)DataFixUtils.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.put("minecraft:acacia_leaves", 0);
      var0.put("minecraft:birch_leaves", 1);
      var0.put("minecraft:dark_oak_leaves", 2);
      var0.put("minecraft:jungle_leaves", 3);
      var0.put("minecraft:oak_leaves", 4);
      var0.put("minecraft:spruce_leaves", 5);
   });
   private static final Set<String> field_208435_k = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

   public LeavesFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211287_c);
      OpticFinder var2 = var1.findField("Level");
      OpticFinder var3 = var2.type().findField("Sections");
      Type var4 = var3.type();
      if (!(var4 instanceof ListType)) {
         throw new IllegalStateException("Expecting sections to be a list.");
      } else {
         Type var5 = ((ListType)var4).getElement();
         OpticFinder var6 = DSL.typeFinder(var5);
         return this.fixTypeEverywhereTyped("Leaves fix", var1, (var4x) -> {
            return var4x.updateTyped(var2, (var3x) -> {
               int[] var4 = new int[]{0};
               Typed var5 = var3x.updateTyped(var3, (var3xx) -> {
                  Int2ObjectOpenHashMap var4x = new Int2ObjectOpenHashMap((Map)var3xx.getAllTyped(var6).stream().map((var1) -> {
                     return new LeavesFix.LeavesSection(var1, this.getInputSchema());
                  }).collect(Collectors.toMap(LeavesFix.Section::func_208456_b, (var0) -> {
                     return var0;
                  })));
                  if (var4x.values().stream().allMatch(LeavesFix.Section::func_208461_a)) {
                     return var3xx;
                  } else {
                     ArrayList var5 = Lists.newArrayList();

                     int var6x;
                     for(var6x = 0; var6x < 7; ++var6x) {
                        var5.add(new IntOpenHashSet());
                     }

                     ObjectIterator var25 = var4x.values().iterator();

                     while(true) {
                        LeavesFix.LeavesSection var7;
                        int var10;
                        int var11;
                        do {
                           if (!var25.hasNext()) {
                              for(var6x = 1; var6x < 7; ++var6x) {
                                 IntSet var26 = (IntSet)var5.get(var6x - 1);
                                 IntSet var27 = (IntSet)var5.get(var6x);
                                 IntIterator var28 = var26.iterator();

                                 while(var28.hasNext()) {
                                    var10 = var28.nextInt();
                                    var11 = this.func_208412_a(var10);
                                    int var12 = this.func_208421_b(var10);
                                    int var13 = this.func_208409_c(var10);
                                    int[][] var14 = field_208425_a;
                                    int var15 = var14.length;

                                    for(int var16 = 0; var16 < var15; ++var16) {
                                       int[] var17 = var14[var16];
                                       int var18 = var11 + var17[0];
                                       int var19 = var12 + var17[1];
                                       int var20 = var13 + var17[2];
                                       if (var18 >= 0 && var18 <= 15 && var20 >= 0 && var20 <= 15 && var19 >= 0 && var19 <= 255) {
                                          LeavesFix.LeavesSection var21 = (LeavesFix.LeavesSection)var4x.get(var19 >> 4);
                                          if (var21 != null && !var21.func_208461_a()) {
                                             int var22 = func_208411_a(var18, var19 & 15, var20);
                                             int var23 = var21.func_208453_a(var22);
                                             if (var21.func_208460_c(var23)) {
                                                int var24 = var21.func_208459_d(var23);
                                                if (var24 > var6x) {
                                                   var21.func_208454_a(var22, var23, var6x);
                                                   var27.add(func_208411_a(var18, var19, var20));
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }

                              return var3xx.updateTyped(var6, (var1) -> {
                                 return ((LeavesFix.LeavesSection)var4x.get(((Dynamic)var1.get(DSL.remainderFinder())).getInt("Y"))).func_208465_a(var1);
                              });
                           }

                           var7 = (LeavesFix.LeavesSection)var25.next();
                        } while(var7.func_208461_a());

                        for(int var8 = 0; var8 < 4096; ++var8) {
                           int var9 = var7.func_208453_a(var8);
                           if (var7.func_208457_b(var9)) {
                              ((IntSet)var5.get(0)).add(var7.func_208456_b() << 12 | var8);
                           } else if (var7.func_208460_c(var9)) {
                              var10 = this.func_208412_a(var8);
                              var11 = this.func_208409_c(var8);
                              var4[0] |= func_210537_a(var10 == 0, var10 == 15, var11 == 0, var11 == 15);
                           }
                        }
                     }
                  }
               });
               if (var4[0] != 0) {
                  var5 = var5.update(DSL.remainderFinder(), (var1) -> {
                     Dynamic var2 = (Dynamic)DataFixUtils.orElse(var1.get("UpgradeData"), var1.emptyMap());
                     return var1.set("UpgradeData", var2.set("Sides", var1.createByte((byte)(var2.getByte("Sides") | var4[0]))));
                  });
               }

               return var5;
            });
         });
      }
   }

   public static int func_208411_a(int var0, int var1, int var2) {
      return var1 << 8 | var2 << 4 | var0;
   }

   private int func_208412_a(int var1) {
      return var1 & 15;
   }

   private int func_208421_b(int var1) {
      return var1 >> 8 & 255;
   }

   private int func_208409_c(int var1) {
      return var1 >> 4 & 15;
   }

   public static int func_210537_a(boolean var0, boolean var1, boolean var2, boolean var3) {
      int var4 = 0;
      if (var2) {
         if (var1) {
            var4 |= 2;
         } else if (var0) {
            var4 |= 128;
         } else {
            var4 |= 1;
         }
      } else if (var3) {
         if (var0) {
            var4 |= 32;
         } else if (var1) {
            var4 |= 8;
         } else {
            var4 |= 16;
         }
      } else if (var1) {
         var4 |= 4;
      } else if (var0) {
         var4 |= 64;
      }

      return var4;
   }

   public static final class LeavesSection extends LeavesFix.Section {
      @Nullable
      private IntSet field_212523_f;
      @Nullable
      private IntSet field_212524_g;
      @Nullable
      private Int2IntMap field_212525_h;

      public LeavesSection(Typed<?> var1, Schema var2) {
         super(var1, var2);
      }

      protected boolean func_212508_a() {
         this.field_212523_f = new IntOpenHashSet();
         this.field_212524_g = new IntOpenHashSet();
         this.field_212525_h = new Int2IntOpenHashMap();

         for(int var1 = 0; var1 < this.field_208469_d.size(); ++var1) {
            Dynamic var2 = (Dynamic)this.field_208469_d.get(var1);
            String var3 = var2.getString("Name");
            if (LeavesFix.field_208434_j.containsKey(var3)) {
               boolean var4 = Objects.equals(var2.get("Properties").flatMap((var0) -> {
                  return var0.get("decayable");
               }).flatMap(Dynamic::getStringValue).orElse(""), "false");
               this.field_212523_f.add(var1);
               this.field_212525_h.put(this.func_208464_a(var3, var4, 7), var1);
               this.field_208469_d.set(var1, this.func_209770_a(var2, var3, var4, 7));
            }

            if (LeavesFix.field_208435_k.contains(var3)) {
               this.field_212524_g.add(var1);
            }
         }

         return this.field_212523_f.isEmpty() && this.field_212524_g.isEmpty();
      }

      private Dynamic<?> func_209770_a(Dynamic<?> var1, String var2, boolean var3, int var4) {
         Dynamic var5 = var1.emptyMap();
         var5 = var5.set("persistent", var5.createString(var3 ? "true" : "false"));
         var5 = var5.set("distance", var5.createString(Integer.toString(var4)));
         Dynamic var6 = var1.emptyMap();
         var6 = var6.set("Properties", var5);
         var6 = var6.set("Name", var6.createString(var2));
         return var6;
      }

      public boolean func_208457_b(int var1) {
         return this.field_212524_g.contains(var1);
      }

      public boolean func_208460_c(int var1) {
         return this.field_212523_f.contains(var1);
      }

      private int func_208459_d(int var1) {
         return this.func_208457_b(var1) ? 0 : Integer.parseInt((String)((Dynamic)this.field_208469_d.get(var1)).get("Properties").flatMap((var0) -> {
            return var0.get("distance");
         }).flatMap(Dynamic::getStringValue).orElse(""));
      }

      private void func_208454_a(int var1, int var2, int var3) {
         Dynamic var4 = (Dynamic)this.field_208469_d.get(var2);
         String var5 = var4.getString("Name");
         boolean var6 = Objects.equals(var4.get("Properties").flatMap((var0) -> {
            return var0.get("persistent");
         }).flatMap(Dynamic::getStringValue).orElse(""), "true");
         int var7 = this.func_208464_a(var5, var6, var3);
         int var8;
         if (!this.field_212525_h.containsKey(var7)) {
            var8 = this.field_208469_d.size();
            this.field_212523_f.add(var8);
            this.field_212525_h.put(var7, var8);
            this.field_208469_d.add(this.func_209770_a(var4, var5, var6, var3));
         }

         var8 = this.field_212525_h.get(var7);
         if (1 << this.field_208470_e.func_208535_c() <= var8) {
            BitArray var9 = new BitArray(this.field_208470_e.func_208535_c() + 1, 4096);

            for(int var10 = 0; var10 < 4096; ++var10) {
               var9.func_188141_a(var10, this.field_208470_e.func_188142_a(var10));
            }

            this.field_208470_e = var9;
         }

         this.field_208470_e.func_188141_a(var1, var8);
      }
   }

   public abstract static class Section {
      final Type<Pair<String, Dynamic<?>>> field_208466_a;
      protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_208468_c;
      protected final List<Dynamic<?>> field_208469_d;
      protected final int field_208474_i;
      @Nullable
      protected BitArray field_208470_e;

      public Section(Typed<?> var1, Schema var2) {
         super();
         this.field_208466_a = DSL.named(TypeReferences.field_211296_l.typeName(), DSL.remainderType());
         this.field_208468_c = DSL.fieldFinder("Palette", DSL.list(this.field_208466_a));
         if (!Objects.equals(var2.getType(TypeReferences.field_211296_l), this.field_208466_a)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional var3 = var1.getOptional(this.field_208468_c);
            this.field_208469_d = (List)var3.map((var0) -> {
               return (List)var0.stream().map(Pair::getSecond).collect(Collectors.toList());
            }).orElse(ImmutableList.of());
            Dynamic var4 = (Dynamic)var1.get(DSL.remainderFinder());
            this.field_208474_i = var4.getInt("Y");
            this.func_212507_a(var4);
         }
      }

      protected void func_212507_a(Dynamic<?> var1) {
         if (this.func_212508_a()) {
            this.field_208470_e = null;
         } else {
            long[] var2 = ((LongStream)var1.get("BlockStates").flatMap(Dynamic::getLongStream).get()).toArray();
            int var3 = Math.max(4, DataFixUtils.ceillog2(this.field_208469_d.size()));
            this.field_208470_e = new BitArray(var3, 4096, var2);
         }

      }

      public Typed<?> func_208465_a(Typed<?> var1) {
         return this.func_208461_a() ? var1 : var1.update(DSL.remainderFinder(), (var1x) -> {
            return var1x.set("BlockStates", var1x.createLongList(Arrays.stream(this.field_208470_e.func_188143_a())));
         }).set(this.field_208468_c, this.field_208469_d.stream().map((var0) -> {
            return Pair.of(TypeReferences.field_211296_l.typeName(), var0);
         }).collect(Collectors.toList()));
      }

      public boolean func_208461_a() {
         return this.field_208470_e == null;
      }

      public int func_208453_a(int var1) {
         return this.field_208470_e.func_188142_a(var1);
      }

      protected int func_208464_a(String var1, boolean var2, int var3) {
         return LeavesFix.field_208434_j.get(var1) << 5 | (var2 ? 16 : 0) | var3;
      }

      int func_208456_b() {
         return this.field_208474_i;
      }

      protected abstract boolean func_212508_a();
   }
}
