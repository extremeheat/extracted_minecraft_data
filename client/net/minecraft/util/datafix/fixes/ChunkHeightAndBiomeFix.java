package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class ChunkHeightAndBiomeFix extends DataFix {
   public static final String DATAFIXER_CONTEXT_TAG = "__context";
   private static final String NAME = "ChunkHeightAndBiomeFix";
   private static final int OLD_SECTION_COUNT = 16;
   private static final int NEW_SECTION_COUNT = 24;
   private static final int NEW_MIN_SECTION_Y = -4;
   public static final int BLOCKS_PER_SECTION = 4096;
   private static final int LONGS_PER_SECTION = 64;
   private static final int HEIGHTMAP_BITS = 9;
   private static final long HEIGHTMAP_MASK = 511L;
   private static final int HEIGHTMAP_OFFSET = 64;
   private static final String[] HEIGHTMAP_TYPES = new String[]{"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"};
   private static final Set<String> STATUS_IS_OR_AFTER_SURFACE = Set.of("surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
   private static final Set<String> STATUS_IS_OR_AFTER_NOISE = Set.of("noise", "surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
   private static final Set<String> BLOCKS_BEFORE_FEATURE_STATUS = Set.of("minecraft:air", "minecraft:basalt", "minecraft:bedrock", "minecraft:blackstone", "minecraft:calcite", "minecraft:cave_air", "minecraft:coarse_dirt", "minecraft:crimson_nylium", "minecraft:dirt", "minecraft:end_stone", "minecraft:grass_block", "minecraft:gravel", "minecraft:ice", "minecraft:lava", "minecraft:mycelium", "minecraft:nether_wart_block", "minecraft:netherrack", "minecraft:orange_terracotta", "minecraft:packed_ice", "minecraft:podzol", "minecraft:powder_snow", "minecraft:red_sand", "minecraft:red_sandstone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow_block", "minecraft:soul_sand", "minecraft:soul_soil", "minecraft:stone", "minecraft:terracotta", "minecraft:warped_nylium", "minecraft:warped_wart_block", "minecraft:water", "minecraft:white_terracotta");
   private static final int BIOME_CONTAINER_LAYER_SIZE = 16;
   private static final int BIOME_CONTAINER_SIZE = 64;
   private static final int BIOME_CONTAINER_TOP_LAYER_OFFSET = 1008;
   public static final String DEFAULT_BIOME = "minecraft:plains";
   private static final Int2ObjectMap<String> BIOMES_BY_ID = new Int2ObjectOpenHashMap();

   public ChunkHeightAndBiomeFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("Level");
      OpticFinder var3 = var2.type().findField("Sections");
      Schema var4 = this.getOutputSchema();
      Type var5 = var4.getType(References.CHUNK);
      Type var6 = var5.findField("Level").type();
      Type var7 = var6.findField("Sections").type();
      return this.fixTypeEverywhereTyped("ChunkHeightAndBiomeFix", var1, var5, (var5x) -> {
         return var5x.updateTyped(var2, var6, (var4) -> {
            Dynamic var5 = (Dynamic)var4.get(DSL.remainderFinder());
            OptionalDynamic var6 = ((Dynamic)var5x.get(DSL.remainderFinder())).get("__context");
            String var7x = (String)var6.get("dimension").asString().result().orElse("");
            String var8 = (String)var6.get("generator").asString().result().orElse("");
            boolean var9 = "minecraft:overworld".equals(var7x);
            MutableBoolean var10 = new MutableBoolean();
            int var11 = var9 ? -4 : 0;
            Dynamic[] var12 = getBiomeContainers(var5, var9, var11, var10);
            Dynamic var13 = makePalettedContainer(var5.createList(Stream.of(var5.createMap(ImmutableMap.of(var5.createString("Name"), var5.createString("minecraft:air"))))));
            HashSet var14 = Sets.newHashSet();
            MutableObject var15 = new MutableObject(() -> {
               return null;
            });
            var4 = var4.updateTyped(var3, var7, (var7xx) -> {
               IntOpenHashSet var8 = new IntOpenHashSet();
               Dynamic var9 = (Dynamic)var7xx.write().result().orElseThrow(() -> {
                  return new IllegalStateException("Malformed Chunk.Level.Sections");
               });
               List var10 = (List)var9.asStream().map((var6) -> {
                  int var7 = var6.get("Y").asInt(0);
                  Dynamic var8x = (Dynamic)DataFixUtils.orElse(var6.get("Palette").result().flatMap((var2) -> {
                     Stream var10000 = var2.asStream().map((var0) -> {
                        return var0.get("Name").asString("minecraft:air");
                     });
                     Objects.requireNonNull(var14);
                     var10000.forEach(var14::add);
                     return var6.get("BlockStates").result().map((var1) -> {
                        return makeOptimizedPalettedContainer(var2, var1);
                     });
                  }), var13);
                  Dynamic var9 = var6;
                  int var10 = var7 - var11;
                  if (var10 >= 0 && var10 < var12.length) {
                     var9 = var6.set("biomes", var12[var10]);
                  }

                  var8.add(var7);
                  if (var6.get("Y").asInt(2147483647) == 0) {
                     var15.setValue(() -> {
                        List var1 = var8x.get("palette").asList(Function.identity());
                        long[] var2 = var8x.get("data").asLongStream().toArray();
                        return new ChunkProtoTickListFix.PoorMansPalettedContainer(var1, var2);
                     });
                  }

                  return var9.set("block_states", var8x).remove("Palette").remove("BlockStates");
               }).collect(Collectors.toCollection(ArrayList::new));

               for(int var11x = 0; var11x < var12.length; ++var11x) {
                  int var12x = var11x + var11;
                  if (var8.add(var12x)) {
                     Dynamic var13x = var5.createMap(Map.of(var5.createString("Y"), var5.createInt(var12x)));
                     var13x = var13x.set("block_states", var13);
                     var13x = var13x.set("biomes", var12[var11x]);
                     var10.add(var13x);
                  }
               }

               return (Typed)((Pair)var7.readTyped(var5.createList(var10.stream())).result().orElseThrow(() -> {
                  return new IllegalStateException("ChunkHeightAndBiomeFix failed.");
               })).getFirst();
            });
            return var4.update(DSL.remainderFinder(), (var6x) -> {
               if (var9) {
                  var6x = this.predictChunkStatusBeforeSurface(var6x, var14);
               }

               return updateChunkTag(var6x, var9, var10.booleanValue(), "minecraft:noise".equals(var8), (Supplier)var15.getValue());
            });
         });
      });
   }

   private Dynamic<?> predictChunkStatusBeforeSurface(Dynamic<?> var1, Set<String> var2) {
      return var1.update("Status", (var1x) -> {
         String var2x = var1x.asString("empty");
         if (STATUS_IS_OR_AFTER_SURFACE.contains(var2x)) {
            return var1x;
         } else {
            var2.remove("minecraft:air");
            boolean var3 = !var2.isEmpty();
            var2.removeAll(BLOCKS_BEFORE_FEATURE_STATUS);
            boolean var4 = !var2.isEmpty();
            if (var4) {
               return var1x.createString("liquid_carvers");
            } else if (!"noise".equals(var2x) && !var3) {
               return "biomes".equals(var2x) ? var1x.createString("structure_references") : var1x;
            } else {
               return var1x.createString("noise");
            }
         }
      });
   }

   private static Dynamic<?>[] getBiomeContainers(Dynamic<?> var0, boolean var1, int var2, MutableBoolean var3) {
      Dynamic[] var4 = new Dynamic[var1 ? 24 : 16];
      int[] var5 = (int[])var0.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse((Object)null);
      int var6;
      if (var5 != null && var5.length == 1536) {
         var3.setValue(true);

         for(var6 = 0; var6 < 24; ++var6) {
            var4[var6] = makeBiomeContainer(var0, (var2x) -> {
               return getOldBiome(var5, var6 * 64 + var2x);
            });
         }
      } else if (var5 != null && var5.length == 1024) {
         for(var6 = 0; var6 < 16; ++var6) {
            int var7 = var6 - var2;
            var4[var7] = makeBiomeContainer(var0, (var2x) -> {
               return getOldBiome(var5, var6 * 64 + var2x);
            });
         }

         if (var1) {
            Dynamic var9 = makeBiomeContainer(var0, (var1x) -> {
               return getOldBiome(var5, var1x % 16);
            });
            Dynamic var10 = makeBiomeContainer(var0, (var1x) -> {
               return getOldBiome(var5, var1x % 16 + 1008);
            });

            int var8;
            for(var8 = 0; var8 < 4; ++var8) {
               var4[var8] = var9;
            }

            for(var8 = 20; var8 < 24; ++var8) {
               var4[var8] = var10;
            }
         }
      } else {
         Arrays.fill(var4, makePalettedContainer(var0.createList(Stream.of(var0.createString("minecraft:plains")))));
      }

      return var4;
   }

   private static int getOldBiome(int[] var0, int var1) {
      return var0[var1] & 255;
   }

   private static Dynamic<?> updateChunkTag(Dynamic<?> var0, boolean var1, boolean var2, boolean var3, Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer> var4) {
      var0 = var0.remove("Biomes");
      if (!var1) {
         return updateCarvingMasks(var0, 16, 0);
      } else if (var2) {
         return updateCarvingMasks(var0, 24, 0);
      } else {
         var0 = updateHeightmaps(var0);
         var0 = addPaddingEntries(var0, "Lights");
         var0 = addPaddingEntries(var0, "LiquidsToBeTicked");
         var0 = addPaddingEntries(var0, "PostProcessing");
         var0 = addPaddingEntries(var0, "ToBeTicked");
         var0 = updateCarvingMasks(var0, 24, 4);
         var0 = var0.update("UpgradeData", ChunkHeightAndBiomeFix::shiftUpgradeData);
         if (!var3) {
            return var0;
         } else {
            Optional var5 = var0.get("Status").result();
            if (var5.isPresent()) {
               Dynamic var6 = (Dynamic)var5.get();
               String var7 = var6.asString("");
               if (!"empty".equals(var7)) {
                  var0 = var0.set("blending_data", var0.createMap(ImmutableMap.of(var0.createString("old_noise"), var0.createBoolean(STATUS_IS_OR_AFTER_NOISE.contains(var7)))));
                  ChunkProtoTickListFix.PoorMansPalettedContainer var8 = (ChunkProtoTickListFix.PoorMansPalettedContainer)var4.get();
                  if (var8 != null) {
                     BitSet var9 = new BitSet(256);
                     boolean var10 = var7.equals("noise");

                     for(int var11 = 0; var11 < 16; ++var11) {
                        for(int var12 = 0; var12 < 16; ++var12) {
                           Dynamic var13 = var8.get(var12, 0, var11);
                           boolean var14 = var13 != null && "minecraft:bedrock".equals(var13.get("Name").asString(""));
                           boolean var15 = var13 != null && "minecraft:air".equals(var13.get("Name").asString(""));
                           if (var15) {
                              var9.set(var11 * 16 + var12);
                           }

                           var10 |= var14;
                        }
                     }

                     if (var10 && var9.cardinality() != var9.size()) {
                        Dynamic var16 = "full".equals(var7) ? var0.createString("heightmaps") : var6;
                        var0 = var0.set("below_zero_retrogen", var0.createMap(ImmutableMap.of(var0.createString("target_status"), var16, var0.createString("missing_bedrock"), var0.createLongList(LongStream.of(var9.toLongArray())))));
                        var0 = var0.set("Status", var0.createString("empty"));
                     }

                     var0 = var0.set("isLightOn", var0.createBoolean(false));
                  }
               }
            }

            return var0;
         }
      }
   }

   private static <T> Dynamic<T> shiftUpgradeData(Dynamic<T> var0) {
      return var0.update("Indices", (var0x) -> {
         HashMap var1 = new HashMap();
         var0x.getMapValues().result().ifPresent((var1x) -> {
            var1x.forEach((var1xx, var2) -> {
               try {
                  var1xx.asString().result().map(Integer::parseInt).ifPresent((var3) -> {
                     int var4 = var3 - -4;
                     var1.put(var1xx.createString(Integer.toString(var4)), var2);
                  });
               } catch (NumberFormatException var4) {
               }

            });
         });
         return var0x.createMap(var1);
      });
   }

   private static Dynamic<?> updateCarvingMasks(Dynamic<?> var0, int var1, int var2) {
      Dynamic var3 = var0.get("CarvingMasks").orElseEmptyMap();
      var3 = var3.updateMapValues((var3x) -> {
         long[] var4 = BitSet.valueOf(((Dynamic)var3x.getSecond()).asByteBuffer().array()).toLongArray();
         long[] var5 = new long[64 * var1];
         System.arraycopy(var4, 0, var5, 64 * var2, var4.length);
         return Pair.of((Dynamic)var3x.getFirst(), var0.createLongList(LongStream.of(var5)));
      });
      return var0.set("CarvingMasks", var3);
   }

   private static Dynamic<?> addPaddingEntries(Dynamic<?> var0, String var1) {
      List var2 = (List)var0.get(var1).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
      if (var2.size() == 24) {
         return var0;
      } else {
         Dynamic var3 = var0.emptyList();

         for(int var4 = 0; var4 < 4; ++var4) {
            var2.add(0, var3);
            var2.add(var3);
         }

         return var0.set(var1, var0.createList(var2.stream()));
      }
   }

   private static Dynamic<?> updateHeightmaps(Dynamic<?> var0) {
      return var0.update("Heightmaps", (var0x) -> {
         String[] var1 = HEIGHTMAP_TYPES;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            var0x = var0x.update(var4, ChunkHeightAndBiomeFix::getFixedHeightmap);
         }

         return var0x;
      });
   }

   private static Dynamic<?> getFixedHeightmap(Dynamic<?> var0) {
      return var0.createLongList(var0.asLongStream().map((var0x) -> {
         long var2 = 0L;

         for(int var4 = 0; var4 + 9 <= 64; var4 += 9) {
            long var5 = var0x >> var4 & 511L;
            long var7;
            if (var5 == 0L) {
               var7 = 0L;
            } else {
               var7 = Math.min(var5 + 64L, 511L);
            }

            var2 |= var7 << var4;
         }

         return var2;
      }));
   }

   private static Dynamic<?> makeBiomeContainer(Dynamic<?> var0, Int2IntFunction var1) {
      Int2IntLinkedOpenHashMap var2 = new Int2IntLinkedOpenHashMap();

      int var4;
      for(int var3 = 0; var3 < 64; ++var3) {
         var4 = var1.applyAsInt(var3);
         if (!var2.containsKey(var4)) {
            var2.put(var4, var2.size());
         }
      }

      Dynamic var12 = var0.createList(var2.keySet().stream().map((var1x) -> {
         return var0.createString((String)BIOMES_BY_ID.getOrDefault(var1x, "minecraft:plains"));
      }));
      var4 = ceillog2(var2.size());
      if (var4 == 0) {
         return makePalettedContainer(var12);
      } else {
         int var5 = 64 / var4;
         int var6 = (64 + var5 - 1) / var5;
         long[] var7 = new long[var6];
         int var8 = 0;
         int var9 = 0;

         for(int var10 = 0; var10 < 64; ++var10) {
            int var11 = var1.applyAsInt(var10);
            var7[var8] |= (long)var2.get(var11) << var9;
            var9 += var4;
            if (var9 + var4 > 64) {
               ++var8;
               var9 = 0;
            }
         }

         Dynamic var13 = var0.createLongList(Arrays.stream(var7));
         return makePalettedContainer(var12, var13);
      }
   }

   private static Dynamic<?> makePalettedContainer(Dynamic<?> var0) {
      return var0.createMap(ImmutableMap.of(var0.createString("palette"), var0));
   }

   private static Dynamic<?> makePalettedContainer(Dynamic<?> var0, Dynamic<?> var1) {
      return var0.createMap(ImmutableMap.of(var0.createString("palette"), var0, var0.createString("data"), var1));
   }

   private static Dynamic<?> makeOptimizedPalettedContainer(Dynamic<?> var0, Dynamic<?> var1) {
      List var2 = (List)var0.asStream().collect(Collectors.toCollection(ArrayList::new));
      if (var2.size() == 1) {
         return makePalettedContainer(var0);
      } else {
         var0 = padPaletteEntries(var0, var1, var2);
         return makePalettedContainer(var0, var1);
      }
   }

   private static Dynamic<?> padPaletteEntries(Dynamic<?> var0, Dynamic<?> var1, List<Dynamic<?>> var2) {
      long var3 = var1.asLongStream().count() * 64L;
      long var5 = var3 / 4096L;
      int var7 = var2.size();
      int var8 = ceillog2(var7);
      if (var5 <= (long)var8) {
         return var0;
      } else {
         Dynamic var9 = var0.createMap(ImmutableMap.of(var0.createString("Name"), var0.createString("minecraft:air")));
         int var10 = (1 << (int)(var5 - 1L)) + 1;
         int var11 = var10 - var7;

         for(int var12 = 0; var12 < var11; ++var12) {
            var2.add(var9);
         }

         return var0.createList(var2.stream());
      }
   }

   public static int ceillog2(int var0) {
      return var0 == 0 ? 0 : (int)Math.ceil(Math.log((double)var0) / Math.log(2.0));
   }

   static {
      BIOMES_BY_ID.put(0, "minecraft:ocean");
      BIOMES_BY_ID.put(1, "minecraft:plains");
      BIOMES_BY_ID.put(2, "minecraft:desert");
      BIOMES_BY_ID.put(3, "minecraft:mountains");
      BIOMES_BY_ID.put(4, "minecraft:forest");
      BIOMES_BY_ID.put(5, "minecraft:taiga");
      BIOMES_BY_ID.put(6, "minecraft:swamp");
      BIOMES_BY_ID.put(7, "minecraft:river");
      BIOMES_BY_ID.put(8, "minecraft:nether_wastes");
      BIOMES_BY_ID.put(9, "minecraft:the_end");
      BIOMES_BY_ID.put(10, "minecraft:frozen_ocean");
      BIOMES_BY_ID.put(11, "minecraft:frozen_river");
      BIOMES_BY_ID.put(12, "minecraft:snowy_tundra");
      BIOMES_BY_ID.put(13, "minecraft:snowy_mountains");
      BIOMES_BY_ID.put(14, "minecraft:mushroom_fields");
      BIOMES_BY_ID.put(15, "minecraft:mushroom_field_shore");
      BIOMES_BY_ID.put(16, "minecraft:beach");
      BIOMES_BY_ID.put(17, "minecraft:desert_hills");
      BIOMES_BY_ID.put(18, "minecraft:wooded_hills");
      BIOMES_BY_ID.put(19, "minecraft:taiga_hills");
      BIOMES_BY_ID.put(20, "minecraft:mountain_edge");
      BIOMES_BY_ID.put(21, "minecraft:jungle");
      BIOMES_BY_ID.put(22, "minecraft:jungle_hills");
      BIOMES_BY_ID.put(23, "minecraft:jungle_edge");
      BIOMES_BY_ID.put(24, "minecraft:deep_ocean");
      BIOMES_BY_ID.put(25, "minecraft:stone_shore");
      BIOMES_BY_ID.put(26, "minecraft:snowy_beach");
      BIOMES_BY_ID.put(27, "minecraft:birch_forest");
      BIOMES_BY_ID.put(28, "minecraft:birch_forest_hills");
      BIOMES_BY_ID.put(29, "minecraft:dark_forest");
      BIOMES_BY_ID.put(30, "minecraft:snowy_taiga");
      BIOMES_BY_ID.put(31, "minecraft:snowy_taiga_hills");
      BIOMES_BY_ID.put(32, "minecraft:giant_tree_taiga");
      BIOMES_BY_ID.put(33, "minecraft:giant_tree_taiga_hills");
      BIOMES_BY_ID.put(34, "minecraft:wooded_mountains");
      BIOMES_BY_ID.put(35, "minecraft:savanna");
      BIOMES_BY_ID.put(36, "minecraft:savanna_plateau");
      BIOMES_BY_ID.put(37, "minecraft:badlands");
      BIOMES_BY_ID.put(38, "minecraft:wooded_badlands_plateau");
      BIOMES_BY_ID.put(39, "minecraft:badlands_plateau");
      BIOMES_BY_ID.put(40, "minecraft:small_end_islands");
      BIOMES_BY_ID.put(41, "minecraft:end_midlands");
      BIOMES_BY_ID.put(42, "minecraft:end_highlands");
      BIOMES_BY_ID.put(43, "minecraft:end_barrens");
      BIOMES_BY_ID.put(44, "minecraft:warm_ocean");
      BIOMES_BY_ID.put(45, "minecraft:lukewarm_ocean");
      BIOMES_BY_ID.put(46, "minecraft:cold_ocean");
      BIOMES_BY_ID.put(47, "minecraft:deep_warm_ocean");
      BIOMES_BY_ID.put(48, "minecraft:deep_lukewarm_ocean");
      BIOMES_BY_ID.put(49, "minecraft:deep_cold_ocean");
      BIOMES_BY_ID.put(50, "minecraft:deep_frozen_ocean");
      BIOMES_BY_ID.put(127, "minecraft:the_void");
      BIOMES_BY_ID.put(129, "minecraft:sunflower_plains");
      BIOMES_BY_ID.put(130, "minecraft:desert_lakes");
      BIOMES_BY_ID.put(131, "minecraft:gravelly_mountains");
      BIOMES_BY_ID.put(132, "minecraft:flower_forest");
      BIOMES_BY_ID.put(133, "minecraft:taiga_mountains");
      BIOMES_BY_ID.put(134, "minecraft:swamp_hills");
      BIOMES_BY_ID.put(140, "minecraft:ice_spikes");
      BIOMES_BY_ID.put(149, "minecraft:modified_jungle");
      BIOMES_BY_ID.put(151, "minecraft:modified_jungle_edge");
      BIOMES_BY_ID.put(155, "minecraft:tall_birch_forest");
      BIOMES_BY_ID.put(156, "minecraft:tall_birch_hills");
      BIOMES_BY_ID.put(157, "minecraft:dark_forest_hills");
      BIOMES_BY_ID.put(158, "minecraft:snowy_taiga_mountains");
      BIOMES_BY_ID.put(160, "minecraft:giant_spruce_taiga");
      BIOMES_BY_ID.put(161, "minecraft:giant_spruce_taiga_hills");
      BIOMES_BY_ID.put(162, "minecraft:modified_gravelly_mountains");
      BIOMES_BY_ID.put(163, "minecraft:shattered_savanna");
      BIOMES_BY_ID.put(164, "minecraft:shattered_savanna_plateau");
      BIOMES_BY_ID.put(165, "minecraft:eroded_badlands");
      BIOMES_BY_ID.put(166, "minecraft:modified_wooded_badlands_plateau");
      BIOMES_BY_ID.put(167, "minecraft:modified_badlands_plateau");
      BIOMES_BY_ID.put(168, "minecraft:bamboo_jungle");
      BIOMES_BY_ID.put(169, "minecraft:bamboo_jungle_hills");
      BIOMES_BY_ID.put(170, "minecraft:soul_sand_valley");
      BIOMES_BY_ID.put(171, "minecraft:crimson_forest");
      BIOMES_BY_ID.put(172, "minecraft:warped_forest");
      BIOMES_BY_ID.put(173, "minecraft:basalt_deltas");
      BIOMES_BY_ID.put(174, "minecraft:dripstone_caves");
      BIOMES_BY_ID.put(175, "minecraft:lush_caves");
      BIOMES_BY_ID.put(177, "minecraft:meadow");
      BIOMES_BY_ID.put(178, "minecraft:grove");
      BIOMES_BY_ID.put(179, "minecraft:snowy_slopes");
      BIOMES_BY_ID.put(180, "minecraft:snowcapped_peaks");
      BIOMES_BY_ID.put(181, "minecraft:lofty_peaks");
      BIOMES_BY_ID.put(182, "minecraft:stony_peaks");
   }
}
