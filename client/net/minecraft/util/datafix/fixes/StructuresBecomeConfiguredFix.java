package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class StructuresBecomeConfiguredFix extends DataFix {
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
      Map var3 = (Map)var1.getMapValues().result().get();
      ArrayList var4 = new ArrayList();
      var3.forEach((var1x, var2x) -> {
         if (var2x.get("id").asString("INVALID").equals("INVALID")) {
            var4.add(var1x);
         }

      });

      Dynamic var6;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); var1 = var1.remove(var6.asString(""))) {
         var6 = (Dynamic)var5.next();
      }

      return var1.updateMapValues((var2x) -> {
         return this.updateStart(var2x, var2);
      });
   }

   private Pair<Dynamic<?>, Dynamic<?>> updateStart(Pair<Dynamic<?>, Dynamic<?>> var1, Dynamic<?> var2) {
      Dynamic var3 = this.findUpdatedStructureType(var1, var2);
      return new Pair(var3, ((Dynamic)var1.getSecond()).set("id", var3));
   }

   private Dynamic<?> updateReferences(Dynamic<?> var1, Dynamic<?> var2) {
      Map var3 = (Map)var1.getMapValues().result().get();
      ArrayList var4 = new ArrayList();
      var3.forEach((var1x, var2x) -> {
         if (var2x.asLongStream().count() == 0L) {
            var4.add(var1x);
         }

      });

      Dynamic var6;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); var1 = var1.remove(var6.asString(""))) {
         var6 = (Dynamic)var5.next();
      }

      return var1.updateMapValues((var2x) -> {
         return this.updateReference(var2x, var2);
      });
   }

   private Pair<Dynamic<?>, Dynamic<?>> updateReference(Pair<Dynamic<?>, Dynamic<?>> var1, Dynamic<?> var2) {
      return var1.mapFirst((var3) -> {
         return this.findUpdatedStructureType(var1, var2);
      });
   }

   private Dynamic<?> findUpdatedStructureType(Pair<Dynamic<?>, Dynamic<?>> var1, Dynamic<?> var2) {
      String var3 = ((Dynamic)var1.getFirst()).asString("UNKNOWN").toLowerCase(Locale.ROOT);
      Conversion var4 = (Conversion)CONVERSION_MAP.get(var3);
      if (var4 == null) {
         throw new IllegalStateException("Found unknown structure: " + var3);
      } else {
         Dynamic var5 = (Dynamic)var1.getSecond();
         String var6 = var4.fallback;
         if (!var4.biomeMapping().isEmpty()) {
            Optional var7 = this.guessConfiguration(var2, var4);
            if (var7.isPresent()) {
               var6 = (String)var7.get();
            }
         }

         Dynamic var8 = var5.createString(var6);
         return var8;
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

   static record Conversion(Map<String, String> a, String b) {
      private final Map<String, String> biomeMapping;
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
