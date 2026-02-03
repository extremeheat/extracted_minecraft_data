package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class LootItemConditions {
   public LootItemConditions() {
      super();
   }

   public static MapCodec<? extends LootItemCondition> bootstrap(final Registry<MapCodec<? extends LootItemCondition>> registry) {
      Registry.register(registry, (String)"inverted", InvertedLootItemCondition.MAP_CODEC);
      Registry.register(registry, (String)"any_of", AnyOfCondition.MAP_CODEC);
      Registry.register(registry, (String)"all_of", AllOfCondition.MAP_CODEC);
      Registry.register(registry, (String)"random_chance", LootItemRandomChanceCondition.MAP_CODEC);
      Registry.register(registry, (String)"random_chance_with_enchanted_bonus", LootItemRandomChanceWithEnchantedBonusCondition.MAP_CODEC);
      Registry.register(registry, (String)"entity_properties", LootItemEntityPropertyCondition.MAP_CODEC);
      Registry.register(registry, (String)"killed_by_player", LootItemKilledByPlayerCondition.MAP_CODEC);
      Registry.register(registry, (String)"entity_scores", EntityHasScoreCondition.MAP_CODEC);
      Registry.register(registry, (String)"block_state_property", LootItemBlockStatePropertyCondition.MAP_CODEC);
      Registry.register(registry, (String)"match_tool", MatchTool.MAP_CODEC);
      Registry.register(registry, (String)"table_bonus", BonusLevelTableCondition.MAP_CODEC);
      Registry.register(registry, (String)"survives_explosion", ExplosionCondition.MAP_CODEC);
      Registry.register(registry, (String)"damage_source_properties", DamageSourceCondition.MAP_CODEC);
      Registry.register(registry, (String)"location_check", LocationCheck.MAP_CODEC);
      Registry.register(registry, (String)"weather_check", WeatherCheck.MAP_CODEC);
      Registry.register(registry, (String)"reference", ConditionReference.MAP_CODEC);
      Registry.register(registry, (String)"time_check", TimeCheck.MAP_CODEC);
      Registry.register(registry, (String)"value_check", ValueCheckCondition.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"enchantment_active_check", EnchantmentActiveCheck.MAP_CODEC);
   }
}
