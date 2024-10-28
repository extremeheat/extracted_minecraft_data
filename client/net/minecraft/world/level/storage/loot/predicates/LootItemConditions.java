package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class LootItemConditions {
   public static final LootItemConditionType INVERTED;
   public static final LootItemConditionType ANY_OF;
   public static final LootItemConditionType ALL_OF;
   public static final LootItemConditionType RANDOM_CHANCE;
   public static final LootItemConditionType RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
   public static final LootItemConditionType ENTITY_PROPERTIES;
   public static final LootItemConditionType KILLED_BY_PLAYER;
   public static final LootItemConditionType ENTITY_SCORES;
   public static final LootItemConditionType BLOCK_STATE_PROPERTY;
   public static final LootItemConditionType MATCH_TOOL;
   public static final LootItemConditionType TABLE_BONUS;
   public static final LootItemConditionType SURVIVES_EXPLOSION;
   public static final LootItemConditionType DAMAGE_SOURCE_PROPERTIES;
   public static final LootItemConditionType LOCATION_CHECK;
   public static final LootItemConditionType WEATHER_CHECK;
   public static final LootItemConditionType REFERENCE;
   public static final LootItemConditionType TIME_CHECK;
   public static final LootItemConditionType VALUE_CHECK;
   public static final LootItemConditionType ENCHANTMENT_ACTIVE_CHECK;

   public LootItemConditions() {
      super();
   }

   private static LootItemConditionType register(String var0, MapCodec<? extends LootItemCondition> var1) {
      return (LootItemConditionType)Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new LootItemConditionType(var1));
   }

   static {
      INVERTED = register("inverted", InvertedLootItemCondition.CODEC);
      ANY_OF = register("any_of", AnyOfCondition.CODEC);
      ALL_OF = register("all_of", AllOfCondition.CODEC);
      RANDOM_CHANCE = register("random_chance", LootItemRandomChanceCondition.CODEC);
      RANDOM_CHANCE_WITH_ENCHANTED_BONUS = register("random_chance_with_enchanted_bonus", LootItemRandomChanceWithEnchantedBonusCondition.CODEC);
      ENTITY_PROPERTIES = register("entity_properties", LootItemEntityPropertyCondition.CODEC);
      KILLED_BY_PLAYER = register("killed_by_player", LootItemKilledByPlayerCondition.CODEC);
      ENTITY_SCORES = register("entity_scores", EntityHasScoreCondition.CODEC);
      BLOCK_STATE_PROPERTY = register("block_state_property", LootItemBlockStatePropertyCondition.CODEC);
      MATCH_TOOL = register("match_tool", MatchTool.CODEC);
      TABLE_BONUS = register("table_bonus", BonusLevelTableCondition.CODEC);
      SURVIVES_EXPLOSION = register("survives_explosion", ExplosionCondition.CODEC);
      DAMAGE_SOURCE_PROPERTIES = register("damage_source_properties", DamageSourceCondition.CODEC);
      LOCATION_CHECK = register("location_check", LocationCheck.CODEC);
      WEATHER_CHECK = register("weather_check", WeatherCheck.CODEC);
      REFERENCE = register("reference", ConditionReference.CODEC);
      TIME_CHECK = register("time_check", TimeCheck.CODEC);
      VALUE_CHECK = register("value_check", ValueCheckCondition.CODEC);
      ENCHANTMENT_ACTIVE_CHECK = register("enchantment_active_check", EnchantmentActiveCheck.CODEC);
   }
}
