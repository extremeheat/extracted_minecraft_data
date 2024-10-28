package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class StructuresBecomeConfiguredFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<String, Conversion> CONVERSION_MAP = ImmutableMap.builder().put("mineshaft", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft")).put("shipwreck", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck")).put("ocean_ruin", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"), "minecraft:ocean_ruin_cold")).put("village", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:desert"), "minecraft:village_desert", List.of("minecraft:savanna"), "minecraft:village_savanna", List.of("minecraft:snowy_plains"), "minecraft:village_snowy", List.of("minecraft:taiga"), "minecraft:village_taiga"), "minecraft:village_plains")).put("ruined_portal", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:desert"), "minecraft:ruined_portal_desert", List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:windswept_hills", "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:stony_shore", "minecraft:meadow", "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes"), "minecraft:ruined_portal_mountain", List.of("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle"), "minecraft:ruined_portal_jungle", List.of("minecraft:deep_frozen_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:frozen_ocean", "minecraft:ocean", "minecraft:cold_ocean", "minecraft:lukewarm_ocean", "minecraft:warm_ocean"), "minecraft:ruined_portal_ocean"), "minecraft:ruined_portal")).put("pillager_outpost", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:pillager_outpost")).put("mansion", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:mansion")).put("jungle_pyramid", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:jungle_pyramid")).put("desert_pyramid", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:desert_pyramid")).put("igloo", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:igloo")).put("swamp_hut", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:swamp_hut")).put("stronghold", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:stronghold")).put("monument", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:monument")).put("fortress", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:fortress")).put("endcity", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:end_city")).put("buried_treasure", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:buried_treasure")).put("nether_fossil", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:nether_fossil")).put("bastion_remnant", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:bastion_remnant")).build();

   public StructuresBecomeConfiguredFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = this.getInputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("StucturesToConfiguredStructures", var1, var2, this::fix);
   }

   private Dynamic<?> fix(Dynamic<?> var1) {
      return var1.update("structures", (var2) -> {
         return var2.update("starts", (var2x) -> {
            return this.updateStarts(var2x, var1);
         }).update("References", (var2x) -> {
            return this.updateReferences(var2x, var1);
         });
      });
   }

   private Dynamic<?> updateStarts(Dynamic<?> var1, Dynamic<?> var2) {
      Map var3 = (Map)var1.getMapValues().result().orElse(Map.of());
      HashMap var4 = Maps.newHashMap();
      var3.forEach((var3x, var4x) -> {
         if (!var4x.get("id").asString("INVALID").equals("INVALID")) {
            Dynamic var5 = this.findUpdatedStructureType(var3x, var2);
            if (var5 == null) {
               LOGGER.warn("Encountered unknown structure in datafixer: " + var3x.asString("<missing key>"));
            } else {
               var4.computeIfAbsent(var5, (var2x) -> {
                  return var4x.set("id", var5);
               });
            }
         }
      });
      return var2.createMap(var4);
   }

   private Dynamic<?> updateReferences(Dynamic<?> var1, Dynamic<?> var2) {
      Map var3 = (Map)var1.getMapValues().result().orElse(Map.of());
      HashMap var4 = Maps.newHashMap();
      var3.forEach((var3x, var4x) -> {
         if (var4x.asLongStream().count() != 0L) {
            Dynamic var5 = this.findUpdatedStructureType(var3x, var2);
            if (var5 == null) {
               LOGGER.warn("Encountered unknown structure in datafixer: " + var3x.asString("<missing key>"));
            } else {
               var4.compute(var5, (var1, var2x) -> {
                  return var2x == null ? var4x : var4x.createLongList(LongStream.concat(var2x.asLongStream(), var4x.asLongStream()));
               });
            }
         }
      });
      return var2.createMap(var4);
   }

   @Nullable
   private Dynamic<?> findUpdatedStructureType(Dynamic<?> var1, Dynamic<?> var2) {
      String var3 = var1.asString("UNKNOWN").toLowerCase(Locale.ROOT);
      Conversion var4 = (Conversion)CONVERSION_MAP.get(var3);
      if (var4 == null) {
         return null;
      } else {
         String var5 = var4.fallback;
         if (!var4.biomeMapping().isEmpty()) {
            Optional var6 = this.guessConfiguration(var2, var4);
            if (var6.isPresent()) {
               var5 = (String)var6.get();
            }
         }

         return var2.createString(var5);
      }
   }

   private Optional<String> guessConfiguration(Dynamic<?> var1, Conversion var2) {
      Object2IntArrayMap var3 = new Object2IntArrayMap();
      var1.get("sections").asList(Function.identity()).forEach((var2x) -> {
         var2x.get("biomes").get("palette").asList(Function.identity()).forEach((var2xx) -> {
            String var3x = (String)var2.biomeMapping().get(var2xx.asString(""));
            if (var3x != null) {
               var3.mergeInt(var3x, 1, Integer::sum);
            }

         });
      });
      return var3.object2IntEntrySet().stream().max(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).map(Map.Entry::getKey);
   }

   static record Conversion(Map<String, String> biomeMapping, String fallback) {
      final String fallback;

      private Conversion(Map<String, String> var1, String var2) {
         super();
         this.biomeMapping = var1;
         this.fallback = var2;
      }

      public static Conversion trivial(String var0) {
         return new Conversion(Map.of(), var0);
      }

      public static Conversion biomeMapped(Map<List<String>, String> var0, String var1) {
         return new Conversion(unpack(var0), var1);
      }

      private static Map<String, String> unpack(Map<List<String>, String> var0) {
         ImmutableMap.Builder var1 = ImmutableMap.builder();
         Iterator var2 = var0.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            ((List)var3.getKey()).forEach((var2x) -> {
               var1.put(var2x, (String)var3.getValue());
            });
         }

         return var1.build();
      }

      public Map<String, String> biomeMapping() {
         return this.biomeMapping;
      }

      public String fallback() {
         return this.fallback;
      }
   }
}
