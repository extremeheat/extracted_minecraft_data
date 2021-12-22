package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
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
import javax.annotation.Nullable;
import net.minecraft.util.datafix.PackedBitStorage;

public class LeavesFix extends DataFix {
   private static final int NORTH_WEST_MASK = 128;
   private static final int WEST_MASK = 64;
   private static final int SOUTH_WEST_MASK = 32;
   private static final int SOUTH_MASK = 16;
   private static final int SOUTH_EAST_MASK = 8;
   private static final int EAST_MASK = 4;
   private static final int NORTH_EAST_MASK = 2;
   private static final int NORTH_MASK = 1;
   private static final int[][] DIRECTIONS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final int DECAY_DISTANCE = 7;
   private static final int SIZE_BITS = 12;
   private static final int SIZE = 4096;
   static final Object2IntMap<String> LEAVES = (Object2IntMap)DataFixUtils.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.put("minecraft:acacia_leaves", 0);
      var0.put("minecraft:birch_leaves", 1);
      var0.put("minecraft:dark_oak_leaves", 2);
      var0.put("minecraft:jungle_leaves", 3);
      var0.put("minecraft:oak_leaves", 4);
      var0.put("minecraft:spruce_leaves", 5);
   });
   static final Set<String> LOGS = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

   public LeavesFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
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
                  }).collect(Collectors.toMap(LeavesFix.Section::getIndex, (var0) -> {
                     return var0;
                  })));
                  if (var4x.values().stream().allMatch(LeavesFix.Section::isSkippable)) {
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
                                    var11 = this.getX(var10);
                                    int var12 = this.getY(var10);
                                    int var13 = this.getZ(var10);
                                    int[][] var14 = DIRECTIONS;
                                    int var15 = var14.length;

                                    for(int var16 = 0; var16 < var15; ++var16) {
                                       int[] var17 = var14[var16];
                                       int var18 = var11 + var17[0];
                                       int var19 = var12 + var17[1];
                                       int var20 = var13 + var17[2];
                                       if (var18 >= 0 && var18 <= 15 && var20 >= 0 && var20 <= 15 && var19 >= 0 && var19 <= 255) {
                                          LeavesFix.LeavesSection var21 = (LeavesFix.LeavesSection)var4x.get(var19 >> 4);
                                          if (var21 != null && !var21.isSkippable()) {
                                             int var22 = getIndex(var18, var19 & 15, var20);
                                             int var23 = var21.getBlock(var22);
                                             if (var21.isLeaf(var23)) {
                                                int var24 = var21.getDistance(var23);
                                                if (var24 > var6x) {
                                                   var21.setDistance(var22, var23, var6x);
                                                   var27.add(getIndex(var18, var19, var20));
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }

                              return var3xx.updateTyped(var6, (var1) -> {
                                 return ((LeavesFix.LeavesSection)var4x.get(((Dynamic)var1.get(DSL.remainderFinder())).get("Y").asInt(0))).write(var1);
                              });
                           }

                           var7 = (LeavesFix.LeavesSection)var25.next();
                        } while(var7.isSkippable());

                        for(int var8 = 0; var8 < 4096; ++var8) {
                           int var9 = var7.getBlock(var8);
                           if (var7.isLog(var9)) {
                              ((IntSet)var5.get(0)).add(var7.getIndex() << 12 | var8);
                           } else if (var7.isLeaf(var9)) {
                              var10 = this.getX(var8);
                              var11 = this.getZ(var8);
                              var4[0] |= getSideMask(var10 == 0, var10 == 15, var11 == 0, var11 == 15);
                           }
                        }
                     }
                  }
               });
               if (var4[0] != 0) {
                  var5 = var5.update(DSL.remainderFinder(), (var1) -> {
                     Dynamic var2 = (Dynamic)DataFixUtils.orElse(var1.get("UpgradeData").result(), var1.emptyMap());
                     return var1.set("UpgradeData", var2.set("Sides", var1.createByte((byte)(var2.get("Sides").asByte((byte)0) | var4[0]))));
                  });
               }

               return var5;
            });
         });
      }
   }

   public static int getIndex(int var0, int var1, int var2) {
      return var1 << 8 | var2 << 4 | var0;
   }

   private int getX(int var1) {
      return var1 & 15;
   }

   private int getY(int var1) {
      return var1 >> 8 & 255;
   }

   private int getZ(int var1) {
      return var1 >> 4 & 15;
   }

   public static int getSideMask(boolean var0, boolean var1, boolean var2, boolean var3) {
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
      private static final String PERSISTENT = "persistent";
      private static final String DECAYABLE = "decayable";
      private static final String DISTANCE = "distance";
      @Nullable
      private IntSet leaveIds;
      @Nullable
      private IntSet logIds;
      @Nullable
      private Int2IntMap stateToIdMap;

      public LeavesSection(Typed<?> var1, Schema var2) {
         super(var1, var2);
      }

      protected boolean skippable() {
         this.leaveIds = new IntOpenHashSet();
         this.logIds = new IntOpenHashSet();
         this.stateToIdMap = new Int2IntOpenHashMap();

         for(int var1 = 0; var1 < this.palette.size(); ++var1) {
            Dynamic var2 = (Dynamic)this.palette.get(var1);
            String var3 = var2.get("Name").asString("");
            if (LeavesFix.LEAVES.containsKey(var3)) {
               boolean var4 = Objects.equals(var2.get("Properties").get("decayable").asString(""), "false");
               this.leaveIds.add(var1);
               this.stateToIdMap.put(this.getStateId(var3, var4, 7), var1);
               this.palette.set(var1, this.makeLeafTag(var2, var3, var4, 7));
            }

            if (LeavesFix.LOGS.contains(var3)) {
               this.logIds.add(var1);
            }
         }

         return this.leaveIds.isEmpty() && this.logIds.isEmpty();
      }

      private Dynamic<?> makeLeafTag(Dynamic<?> var1, String var2, boolean var3, int var4) {
         Dynamic var5 = var1.emptyMap();
         var5 = var5.set("persistent", var5.createString(var3 ? "true" : "false"));
         var5 = var5.set("distance", var5.createString(Integer.toString(var4)));
         Dynamic var6 = var1.emptyMap();
         var6 = var6.set("Properties", var5);
         var6 = var6.set("Name", var6.createString(var2));
         return var6;
      }

      public boolean isLog(int var1) {
         return this.logIds.contains(var1);
      }

      public boolean isLeaf(int var1) {
         return this.leaveIds.contains(var1);
      }

      int getDistance(int var1) {
         return this.isLog(var1) ? 0 : Integer.parseInt(((Dynamic)this.palette.get(var1)).get("Properties").get("distance").asString(""));
      }

      void setDistance(int var1, int var2, int var3) {
         Dynamic var4 = (Dynamic)this.palette.get(var2);
         String var5 = var4.get("Name").asString("");
         boolean var6 = Objects.equals(var4.get("Properties").get("persistent").asString(""), "true");
         int var7 = this.getStateId(var5, var6, var3);
         int var8;
         if (!this.stateToIdMap.containsKey(var7)) {
            var8 = this.palette.size();
            this.leaveIds.add(var8);
            this.stateToIdMap.put(var7, var8);
            this.palette.add(this.makeLeafTag(var4, var5, var6, var3));
         }

         var8 = this.stateToIdMap.get(var7);
         if (1 << this.storage.getBits() <= var8) {
            PackedBitStorage var9 = new PackedBitStorage(this.storage.getBits() + 1, 4096);

            for(int var10 = 0; var10 < 4096; ++var10) {
               var9.set(var10, this.storage.get(var10));
            }

            this.storage = var9;
         }

         this.storage.set(var1, var8);
      }
   }

   public abstract static class Section {
      protected static final String BLOCK_STATES_TAG = "BlockStates";
      protected static final String NAME_TAG = "Name";
      protected static final String PROPERTIES_TAG = "Properties";
      private final Type<Pair<String, Dynamic<?>>> blockStateType;
      protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder;
      protected final List<Dynamic<?>> palette;
      protected final int index;
      @Nullable
      protected PackedBitStorage storage;

      public Section(Typed<?> var1, Schema var2) {
         super();
         this.blockStateType = DSL.named(References.BLOCK_STATE.typeName(), DSL.remainderType());
         this.paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
         if (!Objects.equals(var2.getType(References.BLOCK_STATE), this.blockStateType)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional var3 = var1.getOptional(this.paletteFinder);
            this.palette = (List)var3.map((var0) -> {
               return (List)var0.stream().map(Pair::getSecond).collect(Collectors.toList());
            }).orElse(ImmutableList.of());
            Dynamic var4 = (Dynamic)var1.get(DSL.remainderFinder());
            this.index = var4.get("Y").asInt(0);
            this.readStorage(var4);
         }
      }

      protected void readStorage(Dynamic<?> var1) {
         if (this.skippable()) {
            this.storage = null;
         } else {
            long[] var2 = var1.get("BlockStates").asLongStream().toArray();
            int var3 = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));
            this.storage = new PackedBitStorage(var3, 4096, var2);
         }

      }

      public Typed<?> write(Typed<?> var1) {
         return this.isSkippable() ? var1 : var1.update(DSL.remainderFinder(), (var1x) -> {
            return var1x.set("BlockStates", var1x.createLongList(Arrays.stream(this.storage.getRaw())));
         }).set(this.paletteFinder, (List)this.palette.stream().map((var0) -> {
            return Pair.of(References.BLOCK_STATE.typeName(), var0);
         }).collect(Collectors.toList()));
      }

      public boolean isSkippable() {
         return this.storage == null;
      }

      public int getBlock(int var1) {
         return this.storage.get(var1);
      }

      protected int getStateId(String var1, boolean var2, int var3) {
         return LeavesFix.LEAVES.get(var1) << 5 | (var2 ? 16 : 0) | var3;
      }

      int getIndex() {
         return this.index;
      }

      protected abstract boolean skippable();
   }
}
