package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import org.jspecify.annotations.Nullable;

public class LevelDataGeneratorOptionsFix extends DataFix {
   private static final Map<String, String> MAP = Map.ofEntries(Map.entry("0", "minecraft:ocean"), Map.entry("1", "minecraft:plains"), Map.entry("2", "minecraft:desert"), Map.entry("3", "minecraft:mountains"), Map.entry("4", "minecraft:forest"), Map.entry("5", "minecraft:taiga"), Map.entry("6", "minecraft:swamp"), Map.entry("7", "minecraft:river"), Map.entry("8", "minecraft:nether"), Map.entry("9", "minecraft:the_end"), Map.entry("10", "minecraft:frozen_ocean"), Map.entry("11", "minecraft:frozen_river"), Map.entry("12", "minecraft:snowy_tundra"), Map.entry("13", "minecraft:snowy_mountains"), Map.entry("14", "minecraft:mushroom_fields"), Map.entry("15", "minecraft:mushroom_field_shore"), Map.entry("16", "minecraft:beach"), Map.entry("17", "minecraft:desert_hills"), Map.entry("18", "minecraft:wooded_hills"), Map.entry("19", "minecraft:taiga_hills"), Map.entry("20", "minecraft:mountain_edge"), Map.entry("21", "minecraft:jungle"), Map.entry("22", "minecraft:jungle_hills"), Map.entry("23", "minecraft:jungle_edge"), Map.entry("24", "minecraft:deep_ocean"), Map.entry("25", "minecraft:stone_shore"), Map.entry("26", "minecraft:snowy_beach"), Map.entry("27", "minecraft:birch_forest"), Map.entry("28", "minecraft:birch_forest_hills"), Map.entry("29", "minecraft:dark_forest"), Map.entry("30", "minecraft:snowy_taiga"), Map.entry("31", "minecraft:snowy_taiga_hills"), Map.entry("32", "minecraft:giant_tree_taiga"), Map.entry("33", "minecraft:giant_tree_taiga_hills"), Map.entry("34", "minecraft:wooded_mountains"), Map.entry("35", "minecraft:savanna"), Map.entry("36", "minecraft:savanna_plateau"), Map.entry("37", "minecraft:badlands"), Map.entry("38", "minecraft:wooded_badlands_plateau"), Map.entry("39", "minecraft:badlands_plateau"), Map.entry("40", "minecraft:small_end_islands"), Map.entry("41", "minecraft:end_midlands"), Map.entry("42", "minecraft:end_highlands"), Map.entry("43", "minecraft:end_barrens"), Map.entry("44", "minecraft:warm_ocean"), Map.entry("45", "minecraft:lukewarm_ocean"), Map.entry("46", "minecraft:cold_ocean"), Map.entry("47", "minecraft:deep_warm_ocean"), Map.entry("48", "minecraft:deep_lukewarm_ocean"), Map.entry("49", "minecraft:deep_cold_ocean"), Map.entry("50", "minecraft:deep_frozen_ocean"), Map.entry("127", "minecraft:the_void"), Map.entry("129", "minecraft:sunflower_plains"), Map.entry("130", "minecraft:desert_lakes"), Map.entry("131", "minecraft:gravelly_mountains"), Map.entry("132", "minecraft:flower_forest"), Map.entry("133", "minecraft:taiga_mountains"), Map.entry("134", "minecraft:swamp_hills"), Map.entry("140", "minecraft:ice_spikes"), Map.entry("149", "minecraft:modified_jungle"), Map.entry("151", "minecraft:modified_jungle_edge"), Map.entry("155", "minecraft:tall_birch_forest"), Map.entry("156", "minecraft:tall_birch_hills"), Map.entry("157", "minecraft:dark_forest_hills"), Map.entry("158", "minecraft:snowy_taiga_mountains"), Map.entry("160", "minecraft:giant_spruce_taiga"), Map.entry("161", "minecraft:giant_spruce_taiga_hills"), Map.entry("162", "minecraft:modified_gravelly_mountains"), Map.entry("163", "minecraft:shattered_savanna"), Map.entry("164", "minecraft:shattered_savanna_plateau"), Map.entry("165", "minecraft:eroded_badlands"), Map.entry("166", "minecraft:modified_wooded_badlands_plateau"), Map.entry("167", "minecraft:modified_badlands_plateau"));
   public static final String GENERATOR_OPTIONS = "generatorOptions";

   public LevelDataGeneratorOptionsFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> resultType = this.getOutputSchema().getType(References.LEVEL);
      return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(References.LEVEL), resultType, (input) -> ExtraDataFixUtils.writeAndReadTypedOrThrow(input, resultType, (tag) -> {
            Optional<String> generatorOptions = tag.get("generatorOptions").asString().result();
            if ("flat".equalsIgnoreCase(tag.get("generatorName").asString(""))) {
               String flatOptionString = (String)generatorOptions.orElse("");
               return tag.set("generatorOptions", convert(flatOptionString, tag.getOps()));
            } else if ("buffet".equalsIgnoreCase(tag.get("generatorName").asString("")) && generatorOptions.isPresent()) {
               JsonElement legacyOptions = LenientJsonParser.parse((String)generatorOptions.get());
               return tag.set("generatorOptions", (new Dynamic(JsonOps.INSTANCE, legacyOptions)).convert(tag.getOps()));
            } else {
               return tag;
            }
         }));
   }

   private static <T> Dynamic<T> convert(final String flatOptionString, final DynamicOps<T> ops) {
      Iterator<String> parts = Splitter.on(';').split(flatOptionString).iterator();
      String biome = "minecraft:plains";
      Map<String, Map<String, String>> structuresOptions = new HashMap();
      List<Pair<Integer, String>> layerList;
      if (!flatOptionString.isEmpty() && parts.hasNext()) {
         layerList = getLayersInfoFromString((String)parts.next());
         if (!layerList.isEmpty()) {
            if (parts.hasNext()) {
               biome = (String)MAP.getOrDefault(parts.next(), "minecraft:plains");
            }

            if (parts.hasNext()) {
               String[] structures1 = ((String)parts.next()).toLowerCase(Locale.ROOT).split(",");

               for(String structure : structures1) {
                  String[] separated = structure.split("\\(", 2);
                  if (!separated[0].isEmpty()) {
                     structuresOptions.put(separated[0], new HashMap());
                     if (separated.length > 1 && separated[1].endsWith(")") && separated[1].length() > 1) {
                        String[] options = separated[1].substring(0, separated[1].length() - 1).split(" ");

                        for(String part : options) {
                           String[] split = part.split("=", 2);
                           if (split.length == 2) {
                              ((Map)structuresOptions.get(separated[0])).put(split[0], split[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               structuresOptions.put("village", Map.of());
            }
         }
      } else {
         layerList = new ArrayList();
         layerList.add(Pair.of(1, "minecraft:bedrock"));
         layerList.add(Pair.of(2, "minecraft:dirt"));
         layerList.add(Pair.of(1, "minecraft:grass_block"));
         structuresOptions.put("village", Map.of());
      }

      T layers = (T)ops.createList(layerList.stream().map((layer) -> ops.createMap(ImmutableMap.of(ops.createString("height"), ops.createInt((Integer)layer.getFirst()), ops.createString("block"), ops.createString((String)layer.getSecond())))));
      T structures = (T)ops.createMap((Map)structuresOptions.entrySet().stream().map((entry) -> Pair.of(ops.createString(((String)entry.getKey()).toLowerCase(Locale.ROOT)), ops.createMap((Map)((Map)entry.getValue()).entrySet().stream().map((option) -> Pair.of(ops.createString((String)option.getKey()), ops.createString((String)option.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(ops, ops.createMap(ImmutableMap.of(ops.createString("layers"), layers, ops.createString("biome"), ops.createString(biome), ops.createString("structures"), structures)));
   }

   private static @Nullable Pair<Integer, String> getLayerInfoFromString(final String input) {
      String[] parts = input.split("\\*", 2);
      int height;
      if (parts.length == 2) {
         try {
            height = Integer.parseInt(parts[0]);
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         height = 1;
      }

      String block = parts[parts.length - 1];
      return Pair.of(height, block);
   }

   private static List<Pair<Integer, String>> getLayersInfoFromString(final String input) {
      List<Pair<Integer, String>> result = new ArrayList();
      String[] depths = input.split(",");

      for(String depth : depths) {
         Pair<Integer, String> layer = getLayerInfoFromString(depth);
         if (layer == null) {
            return Collections.emptyList();
         }

         result.add(layer);
      }

      return result;
   }
}
