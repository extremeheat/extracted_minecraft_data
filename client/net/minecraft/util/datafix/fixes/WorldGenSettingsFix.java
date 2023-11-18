package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class WorldGenSettingsFix extends DataFix {
   private static final String VILLAGE = "minecraft:village";
   private static final String DESERT_PYRAMID = "minecraft:desert_pyramid";
   private static final String IGLOO = "minecraft:igloo";
   private static final String JUNGLE_TEMPLE = "minecraft:jungle_pyramid";
   private static final String SWAMP_HUT = "minecraft:swamp_hut";
   private static final String PILLAGER_OUTPOST = "minecraft:pillager_outpost";
   private static final String END_CITY = "minecraft:endcity";
   private static final String WOODLAND_MANSION = "minecraft:mansion";
   private static final String OCEAN_MONUMENT = "minecraft:monument";
   private static final ImmutableMap<String, WorldGenSettingsFix.StructureFeatureConfiguration> DEFAULTS = ImmutableMap.builder()
      .put("minecraft:village", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 8, 10387312))
      .put("minecraft:desert_pyramid", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 8, 14357617))
      .put("minecraft:igloo", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 8, 14357618))
      .put("minecraft:jungle_pyramid", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 8, 14357619))
      .put("minecraft:swamp_hut", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 8, 14357620))
      .put("minecraft:pillager_outpost", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 8, 165745296))
      .put("minecraft:monument", new WorldGenSettingsFix.StructureFeatureConfiguration(32, 5, 10387313))
      .put("minecraft:endcity", new WorldGenSettingsFix.StructureFeatureConfiguration(20, 11, 10387313))
      .put("minecraft:mansion", new WorldGenSettingsFix.StructureFeatureConfiguration(80, 20, 10387319))
      .build();

   public WorldGenSettingsFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "WorldGenSettings building",
         this.getInputSchema().getType(References.WORLD_GEN_SETTINGS),
         var0 -> var0.update(DSL.remainderFinder(), WorldGenSettingsFix::fix)
      );
   }

   private static <T> Dynamic<T> noise(long var0, DynamicLike<T> var2, Dynamic<T> var3, Dynamic<T> var4) {
      return var2.createMap(
         ImmutableMap.of(
            var2.createString("type"),
            var2.createString("minecraft:noise"),
            var2.createString("biome_source"),
            var4,
            var2.createString("seed"),
            var2.createLong(var0),
            var2.createString("settings"),
            var3
         )
      );
   }

   private static <T> Dynamic<T> vanillaBiomeSource(Dynamic<T> var0, long var1, boolean var3, boolean var4) {
      Builder var5 = ImmutableMap.builder()
         .put(var0.createString("type"), var0.createString("minecraft:vanilla_layered"))
         .put(var0.createString("seed"), var0.createLong(var1))
         .put(var0.createString("large_biomes"), var0.createBoolean(var4));
      if (var3) {
         var5.put(var0.createString("legacy_biome_init_layer"), var0.createBoolean(var3));
      }

      return var0.createMap(var5.build());
   }

   private static <T> Dynamic<T> fix(Dynamic<T> var0) {
      DynamicOps var1 = var0.getOps();
      long var2 = var0.get("RandomSeed").asLong(0L);
      Optional var5 = var0.get("generatorName").asString().map(var0x -> var0x.toLowerCase(Locale.ROOT)).result();
      Optional var6 = var0.get("legacy_custom_options")
         .asString()
         .result()
         .map(Optional::of)
         .orElseGet(() -> var5.equals(Optional.of("customized")) ? var0.get("generatorOptions").asString().result() : Optional.empty());
      boolean var7 = false;
      Dynamic var4;
      if (var5.equals(Optional.of("customized"))) {
         var4 = defaultOverworld(var0, var2);
      } else if (var5.isEmpty()) {
         var4 = defaultOverworld(var0, var2);
      } else {
         String var8 = (String)var5.get();
         switch(var8) {
            case "flat":
               OptionalDynamic var10 = var0.get("generatorOptions");
               Map var11 = fixFlatStructures(var1, var10);
               var4 = var0.createMap(
                  ImmutableMap.of(
                     var0.createString("type"),
                     var0.createString("minecraft:flat"),
                     var0.createString("settings"),
                     var0.createMap(
                        ImmutableMap.of(
                           var0.createString("structures"),
                           var0.createMap(var11),
                           var0.createString("layers"),
                           (Dynamic)var10.get("layers")
                              .result()
                              .orElseGet(
                                 () -> (T)var0.createList(
                                       Stream.of(
                                          (T[])(var0.createMap(
                                             ImmutableMap.of(
                                                var0.createString("height"),
                                                var0.createInt(1),
                                                var0.createString("block"),
                                                var0.createString("minecraft:bedrock")
                                             )
                                          ),
                                          var0.createMap(
                                             ImmutableMap.of(
                                                var0.createString("height"),
                                                var0.createInt(2),
                                                var0.createString("block"),
                                                var0.createString("minecraft:dirt")
                                             )
                                          ),
                                          var0.createMap(
                                             ImmutableMap.of(
                                                var0.createString("height"),
                                                var0.createInt(1),
                                                var0.createString("block"),
                                                var0.createString("minecraft:grass_block")
                                             )
                                          ))
                                       )
                                    )
                              ),
                           var0.createString("biome"),
                           var0.createString(var10.get("biome").asString("minecraft:plains"))
                        )
                     )
                  )
               );
               break;
            case "debug_all_block_states":
               var4 = var0.createMap(ImmutableMap.of(var0.createString("type"), var0.createString("minecraft:debug")));
               break;
            case "buffet":
               OptionalDynamic var12 = var0.get("generatorOptions");
               OptionalDynamic var13 = var12.get("chunk_generator");
               Optional var14 = var13.get("type").asString().result();
               Dynamic var15;
               if (Objects.equals(var14, Optional.of("minecraft:caves"))) {
                  var15 = var0.createString("minecraft:caves");
                  var7 = true;
               } else if (Objects.equals(var14, Optional.of("minecraft:floating_islands"))) {
                  var15 = var0.createString("minecraft:floating_islands");
               } else {
                  var15 = var0.createString("minecraft:overworld");
               }

               Dynamic var16 = (Dynamic)var12.get("biome_source")
                  .result()
                  .orElseGet(() -> (T)var0.createMap(ImmutableMap.of(var0.createString("type"), var0.createString("minecraft:fixed"))));
               Dynamic var17;
               if (var16.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
                  String var18 = var16.get("options")
                     .get("biomes")
                     .asStream()
                     .findFirst()
                     .flatMap(var0x -> var0x.asString().result())
                     .orElse("minecraft:ocean");
                  var17 = var16.remove("options").set("biome", var0.createString(var18));
               } else {
                  var17 = var16;
               }

               var4 = noise(var2, var0, var15, var17);
               break;
            default:
               boolean var25 = ((String)var5.get()).equals("default");
               boolean var19 = ((String)var5.get()).equals("default_1_1") || var25 && var0.get("generatorVersion").asInt(0) == 0;
               boolean var20 = ((String)var5.get()).equals("amplified");
               boolean var21 = ((String)var5.get()).equals("largebiomes");
               var4 = noise(var2, var0, var0.createString(var20 ? "minecraft:amplified" : "minecraft:overworld"), vanillaBiomeSource(var0, var2, var19, var21));
         }
      }

      boolean var22 = var0.get("MapFeatures").asBoolean(true);
      boolean var23 = var0.get("BonusChest").asBoolean(false);
      Builder var24 = ImmutableMap.builder();
      var24.put(var1.createString("seed"), var1.createLong(var2));
      var24.put(var1.createString("generate_features"), var1.createBoolean(var22));
      var24.put(var1.createString("bonus_chest"), var1.createBoolean(var23));
      var24.put(var1.createString("dimensions"), vanillaLevels(var0, var2, var4, var7));
      var6.ifPresent(var2x -> var24.put(var1.createString("legacy_custom_options"), var1.createString(var2x)));
      return new Dynamic(var1, var1.createMap(var24.build()));
   }

   protected static <T> Dynamic<T> defaultOverworld(Dynamic<T> var0, long var1) {
      return noise(var1, var0, var0.createString("minecraft:overworld"), vanillaBiomeSource(var0, var1, false, false));
   }

   protected static <T> T vanillaLevels(Dynamic<T> var0, long var1, Dynamic<T> var3, boolean var4) {
      DynamicOps var5 = var0.getOps();
      return (T)var5.createMap(
         ImmutableMap.of(
            var5.createString("minecraft:overworld"),
            var5.createMap(
               ImmutableMap.of(
                  var5.createString("type"),
                  var5.createString("minecraft:overworld" + (var4 ? "_caves" : "")),
                  var5.createString("generator"),
                  var3.getValue()
               )
            ),
            var5.createString("minecraft:the_nether"),
            var5.createMap(
               ImmutableMap.of(
                  var5.createString("type"),
                  var5.createString("minecraft:the_nether"),
                  var5.createString("generator"),
                  noise(
                        var1,
                        var0,
                        var0.createString("minecraft:nether"),
                        var0.createMap(
                           ImmutableMap.of(
                              var0.createString("type"),
                              var0.createString("minecraft:multi_noise"),
                              var0.createString("seed"),
                              var0.createLong(var1),
                              var0.createString("preset"),
                              var0.createString("minecraft:nether")
                           )
                        )
                     )
                     .getValue()
               )
            ),
            var5.createString("minecraft:the_end"),
            var5.createMap(
               ImmutableMap.of(
                  var5.createString("type"),
                  var5.createString("minecraft:the_end"),
                  var5.createString("generator"),
                  noise(
                        var1,
                        var0,
                        var0.createString("minecraft:end"),
                        var0.createMap(
                           ImmutableMap.of(var0.createString("type"), var0.createString("minecraft:the_end"), var0.createString("seed"), var0.createLong(var1))
                        )
                     )
                     .getValue()
               )
            )
         )
      );
   }

   private static <T> Map<Dynamic<T>, Dynamic<T>> fixFlatStructures(DynamicOps<T> var0, OptionalDynamic<T> var1) {
      MutableInt var2 = new MutableInt(32);
      MutableInt var3 = new MutableInt(3);
      MutableInt var4 = new MutableInt(128);
      MutableBoolean var5 = new MutableBoolean(false);
      HashMap var6 = Maps.newHashMap();
      if (var1.result().isEmpty()) {
         var5.setTrue();
         var6.put("minecraft:village", (WorldGenSettingsFix.StructureFeatureConfiguration)DEFAULTS.get("minecraft:village"));
      }

      var1.get("structures")
         .flatMap(Dynamic::getMapValues)
         .result()
         .ifPresent(
            var5x -> var5x.forEach(
                  (var5xx, var6x) -> var6x.getMapValues()
                        .result()
                        .ifPresent(
                           var6xx -> var6xx.forEach(
                                 (var6xxx, var7x) -> {
                                    String var8 = var5xx.asString("");
                                    String var9 = var6xxx.asString("");
                                    String var10 = var7x.asString("");
                                    if ("stronghold".equals(var8)) {
                                       var5.setTrue();
                                       switch(var9) {
                                          case "distance":
                                             var2.setValue(getInt(var10, var2.getValue(), 1));
                                             return;
                                          case "spread":
                                             var3.setValue(getInt(var10, var3.getValue(), 1));
                                             return;
                                          case "count":
                                             var4.setValue(getInt(var10, var4.getValue(), 1));
                                             return;
                                       }
                                    } else {
                                       switch(var9) {
                                          case "distance":
                                             switch(var8) {
                                                case "village":
                                                   setSpacing(var6, "minecraft:village", var10, 9);
                                                   return;
                                                case "biome_1":
                                                   setSpacing(var6, "minecraft:desert_pyramid", var10, 9);
                                                   setSpacing(var6, "minecraft:igloo", var10, 9);
                                                   setSpacing(var6, "minecraft:jungle_pyramid", var10, 9);
                                                   setSpacing(var6, "minecraft:swamp_hut", var10, 9);
                                                   setSpacing(var6, "minecraft:pillager_outpost", var10, 9);
                                                   return;
                                                case "endcity":
                                                   setSpacing(var6, "minecraft:endcity", var10, 1);
                                                   return;
                                                case "mansion":
                                                   setSpacing(var6, "minecraft:mansion", var10, 1);
                                                   return;
                                                default:
                                                   return;
                                             }
                                          case "separation":
                                             if ("oceanmonument".equals(var8)) {
                                                WorldGenSettingsFix.StructureFeatureConfiguration var13 = (WorldGenSettingsFix.StructureFeatureConfiguration)var6.getOrDefault(
                                                   "minecraft:monument", (WorldGenSettingsFix.StructureFeatureConfiguration)DEFAULTS.get("minecraft:monument")
                                                );
                                                int var14 = getInt(var10, var13.separation, 1);
                                                var6.put(
                                                   "minecraft:monument",
                                                   new WorldGenSettingsFix.StructureFeatureConfiguration(var14, var13.separation, var13.salt)
                                                );
                                             }
                  
                                             return;
                                          case "spacing":
                                             if ("oceanmonument".equals(var8)) {
                                                setSpacing(var6, "minecraft:monument", var10, 1);
                                             }
                  
                                             return;
                                       }
                                    }
                                 }
                              )
                        )
               )
         );
      Builder var7 = ImmutableMap.builder();
      var7.put(
         var1.createString("structures"),
         var1.createMap(
            var6.entrySet()
               .stream()
               .collect(
                  Collectors.toMap(
                     var1x -> var1.createString((String)var1x.getKey()),
                     var1x -> ((WorldGenSettingsFix.StructureFeatureConfiguration)var1x.getValue()).serialize(var0)
                  )
               )
         )
      );
      if (var5.isTrue()) {
         var7.put(
            var1.createString("stronghold"),
            var1.createMap(
               ImmutableMap.of(
                  var1.createString("distance"),
                  var1.createInt(var2.getValue()),
                  var1.createString("spread"),
                  var1.createInt(var3.getValue()),
                  var1.createString("count"),
                  var1.createInt(var4.getValue())
               )
            )
         );
      }

      return var7.build();
   }

   private static int getInt(String var0, int var1) {
      return NumberUtils.toInt(var0, var1);
   }

   private static int getInt(String var0, int var1, int var2) {
      return Math.max(var2, getInt(var0, var1));
   }

   private static void setSpacing(Map<String, WorldGenSettingsFix.StructureFeatureConfiguration> var0, String var1, String var2, int var3) {
      WorldGenSettingsFix.StructureFeatureConfiguration var4 = (WorldGenSettingsFix.StructureFeatureConfiguration)var0.getOrDefault(
         var1, (WorldGenSettingsFix.StructureFeatureConfiguration)DEFAULTS.get(var1)
      );
      int var5 = getInt(var2, var4.spacing, var3);
      var0.put(var1, new WorldGenSettingsFix.StructureFeatureConfiguration(var5, var4.separation, var4.salt));
   }

   static final class StructureFeatureConfiguration {
      public static final Codec<WorldGenSettingsFix.StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.INT.fieldOf("spacing").forGetter(var0x -> var0x.spacing),
                  Codec.INT.fieldOf("separation").forGetter(var0x -> var0x.separation),
                  Codec.INT.fieldOf("salt").forGetter(var0x -> var0x.salt)
               )
               .apply(var0, WorldGenSettingsFix.StructureFeatureConfiguration::new)
      );
      final int spacing;
      final int separation;
      final int salt;

      public StructureFeatureConfiguration(int var1, int var2, int var3) {
         super();
         this.spacing = var1;
         this.separation = var2;
         this.salt = var3;
      }

      public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
         return new Dynamic(var1, CODEC.encodeStart(var1, this).result().orElse(var1.emptyMap()));
      }
   }
}
