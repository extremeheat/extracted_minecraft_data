package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrialSpawnerConfigInRegistryFix extends NamedEntityFix {
   public TrialSpawnerConfigInRegistryFix(final Schema outputSchema) {
      super(outputSchema, false, "TrialSpawnerConfigInRegistryFix", References.BLOCK_ENTITY, "minecraft:trial_spawner");
   }

   public <T> Dynamic<T> fixTag(final Dynamic<T> input) {
      Optional<Dynamic<Object>> normalConfig = input.get("normal_config").result().map((v) -> v.convert(JavaOps.INSTANCE));
      if (normalConfig.isEmpty()) {
         return input;
      } else {
         Optional<Dynamic<Object>> ominousConfig = input.get("ominous_config").result().map((v) -> v.convert(JavaOps.INSTANCE));
         if (ominousConfig.isEmpty()) {
            return input;
         } else {
            String registryLocation = (String)TrialSpawnerConfigInRegistryFix.VanillaTrialChambers.CONFIGS_TO_KEY.get(Pair.of((Dynamic)normalConfig.get(), (Dynamic)ominousConfig.get()));
            return registryLocation == null ? input : input.set("normal_config", input.createString(registryLocation + "/normal")).set("ominous_config", input.createString(registryLocation + "/ominous"));
         }
      }
   }

   protected Typed<?> fix(final Typed<?> entity) {
      return entity.update(DSL.remainderFinder(), this::fixTag);
   }

   private static Dynamic<Object> removeDefaultInt(final Dynamic<Object> config, final String field, final int defaultValue) {
      return config.get(field).asInt(0) == defaultValue ? config.remove(field) : config;
   }

   private static Dynamic<Object> removeDefaultFloat(final Dynamic<Object> config, final String field, final float defaultValue) {
      return config.get(field).asFloat(0.0F) == defaultValue ? config.remove(field) : config;
   }

   private static final class VanillaTrialChambers {
      private static final List<Object> OMINOUS_LOOT_TABLES_TO_EJECT = List.of(Map.of("data", "minecraft:spawners/ominous/trial_chamber/key", "weight", 3), Map.of("data", "minecraft:spawners/ominous/trial_chamber/consumables", "weight", 7));
      private static final Map<String, Object> MELEE_EQUIPMENT = Map.of("loot_table", "minecraft:equipment/trial_chamber_melee", "slot_drop_chances", 0.0F);
      private static final Map<String, Object> RANGED_EQUIPMENT = Map.of("loot_table", "minecraft:equipment/trial_chamber_ranged", "slot_drop_chances", 0.0F);
      public static final Map<Pair<Dynamic<Object>, Dynamic<Object>>, String> CONFIGS_TO_KEY = buildConfigsToKey();

      private VanillaTrialChambers() {
         super();
      }

      private static Map<Pair<Dynamic<Object>, Dynamic<Object>>, String> buildConfigsToKey() {
         Map<Pair<Dynamic<Object>, Dynamic<Object>>, String> configsToKey = new HashMap();
         register(configsToKey, "minecraft:trial_chamber/breeze", Map.of("simultaneous_mobs", 1.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:breeze")), "weight", 1)), "ticks_between_spawn", 20, "total_mobs", 2.0F, "total_mobs_added_per_player", 1.0F), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "simultaneous_mobs", 2.0F, "total_mobs", 4.0F));
         register(configsToKey, "minecraft:trial_chamber/melee/husk", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:husk")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:husk"), "equipment", MELEE_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/melee/spider", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:spider")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "simultaneous_mobs", 4.0F, "total_mobs", 12.0F));
         register(configsToKey, "minecraft:trial_chamber/melee/zombie", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:zombie")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:zombie"), "equipment", MELEE_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/ranged/poison_skeleton", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:bogged")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:bogged"), "equipment", RANGED_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/ranged/skeleton", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:skeleton")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:skeleton"), "equipment", RANGED_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/ranged/stray", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:stray")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:stray"), "equipment", RANGED_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/slow_ranged/poison_skeleton", Map.of("simultaneous_mobs", 4.0F, "simultaneous_mobs_added_per_player", 2.0F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:bogged")), "weight", 1)), "ticks_between_spawn", 160), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:bogged"), "equipment", RANGED_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/slow_ranged/skeleton", Map.of("simultaneous_mobs", 4.0F, "simultaneous_mobs_added_per_player", 2.0F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:skeleton")), "weight", 1)), "ticks_between_spawn", 160), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:skeleton"), "equipment", RANGED_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/slow_ranged/stray", Map.of("simultaneous_mobs", 4.0F, "simultaneous_mobs_added_per_player", 2.0F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:stray")), "weight", 1)), "ticks_between_spawn", 160), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:stray"), "equipment", RANGED_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/small_melee/baby_zombie", Map.of("simultaneous_mobs", 2.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("IsBaby", (byte)1, "id", "minecraft:zombie")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("IsBaby", (byte)1, "id", "minecraft:zombie"), "equipment", MELEE_EQUIPMENT), "weight", 1))));
         register(configsToKey, "minecraft:trial_chamber/small_melee/cave_spider", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:cave_spider")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "simultaneous_mobs", 4.0F, "total_mobs", 12.0F));
         register(configsToKey, "minecraft:trial_chamber/small_melee/silverfish", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("id", "minecraft:silverfish")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "simultaneous_mobs", 4.0F, "total_mobs", 12.0F));
         register(configsToKey, "minecraft:trial_chamber/small_melee/slime", Map.of("simultaneous_mobs", 3.0F, "simultaneous_mobs_added_per_player", 0.5F, "spawn_potentials", List.of(Map.of("data", Map.of("entity", Map.of("Size", 1, "id", "minecraft:slime")), "weight", 3), Map.of("data", Map.of("entity", Map.of("Size", 2, "id", "minecraft:slime")), "weight", 1)), "ticks_between_spawn", 20), Map.of("loot_tables_to_eject", OMINOUS_LOOT_TABLES_TO_EJECT, "simultaneous_mobs", 4.0F, "total_mobs", 12.0F));
         return Map.copyOf(configsToKey);
      }

      private static void register(final Map<Pair<Dynamic<Object>, Dynamic<Object>>, String> configsToKey, final String location, final Map<String, Object> normalConfig, final Map<String, Object> ominousConfig) {
         Dynamic<Object> normalDynamic = new Dynamic(JavaOps.INSTANCE, normalConfig);
         Dynamic<Object> ominousDynamic = new Dynamic(JavaOps.INSTANCE, ominousConfig);
         Map<String, Object> ominousMerged = mergeAndOverwrite(normalConfig, ominousConfig);
         Dynamic<Object> ominousMergedDynamic = new Dynamic(JavaOps.INSTANCE, ominousMerged);
         Dynamic<Object> ominousMergedDynamicDefaultsOmitted = removeDefaults(ominousMergedDynamic);
         configsToKey.put(Pair.of(normalDynamic, ominousDynamic), location);
         configsToKey.put(Pair.of(normalDynamic, ominousMergedDynamic), location);
         configsToKey.put(Pair.of(normalDynamic, ominousMergedDynamicDefaultsOmitted), location);
      }

      private static Map<String, Object> mergeAndOverwrite(final Map<String, Object> m1, final Map<String, Object> m2) {
         HashMap<String, Object> map = new HashMap(m1);
         map.putAll(m2);
         return Map.copyOf(map);
      }

      private static Dynamic<Object> removeDefaults(Dynamic<Object> config) {
         config = TrialSpawnerConfigInRegistryFix.removeDefaultInt(config, "spawn_range", 4);
         config = TrialSpawnerConfigInRegistryFix.removeDefaultFloat(config, "total_mobs", 6.0F);
         config = TrialSpawnerConfigInRegistryFix.removeDefaultFloat(config, "simultaneous_mobs", 2.0F);
         config = TrialSpawnerConfigInRegistryFix.removeDefaultFloat(config, "total_mobs_added_per_player", 2.0F);
         config = TrialSpawnerConfigInRegistryFix.removeDefaultFloat(config, "simultaneous_mobs_added_per_player", 1.0F);
         config = TrialSpawnerConfigInRegistryFix.removeDefaultInt(config, "ticks_between_spawn", 40);
         return config;
      }
   }
}
