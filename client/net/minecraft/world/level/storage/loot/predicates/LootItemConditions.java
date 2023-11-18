package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

public class LootItemConditions {
   public static final LootItemConditionType INVERTED = register("inverted", new InvertedLootItemCondition.Serializer());
   public static final LootItemConditionType ANY_OF = register("any_of", new AnyOfCondition.Serializer());
   public static final LootItemConditionType ALL_OF = register("all_of", new AllOfCondition.Serializer());
   public static final LootItemConditionType RANDOM_CHANCE = register("random_chance", new LootItemRandomChanceCondition.Serializer());
   public static final LootItemConditionType RANDOM_CHANCE_WITH_LOOTING = register(
      "random_chance_with_looting", new LootItemRandomChanceWithLootingCondition.Serializer()
   );
   public static final LootItemConditionType ENTITY_PROPERTIES = register("entity_properties", new LootItemEntityPropertyCondition.Serializer());
   public static final LootItemConditionType KILLED_BY_PLAYER = register("killed_by_player", new LootItemKilledByPlayerCondition.Serializer());
   public static final LootItemConditionType ENTITY_SCORES = register("entity_scores", new EntityHasScoreCondition.Serializer());
   public static final LootItemConditionType BLOCK_STATE_PROPERTY = register("block_state_property", new LootItemBlockStatePropertyCondition.Serializer());
   public static final LootItemConditionType MATCH_TOOL = register("match_tool", new MatchTool.Serializer());
   public static final LootItemConditionType TABLE_BONUS = register("table_bonus", new BonusLevelTableCondition.Serializer());
   public static final LootItemConditionType SURVIVES_EXPLOSION = register("survives_explosion", new ExplosionCondition.Serializer());
   public static final LootItemConditionType DAMAGE_SOURCE_PROPERTIES = register("damage_source_properties", new DamageSourceCondition.Serializer());
   public static final LootItemConditionType LOCATION_CHECK = register("location_check", new LocationCheck.Serializer());
   public static final LootItemConditionType WEATHER_CHECK = register("weather_check", new WeatherCheck.Serializer());
   public static final LootItemConditionType REFERENCE = register("reference", new ConditionReference.Serializer());
   public static final LootItemConditionType TIME_CHECK = register("time_check", new TimeCheck.Serializer());
   public static final LootItemConditionType VALUE_CHECK = register("value_check", new ValueCheckCondition.Serializer());

   public LootItemConditions() {
      super();
   }

   private static LootItemConditionType register(String var0, Serializer<? extends LootItemCondition> var1) {
      return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(var0), new LootItemConditionType(var1));
   }

   public static Object createGsonAdapter() {
      return GsonAdapterFactory.builder(BuiltInRegistries.LOOT_CONDITION_TYPE, "condition", "condition", LootItemCondition::getType).build();
   }

   public static <T> Predicate<T> andConditions(Predicate<T>[] var0) {
      return switch(var0.length) {
         case 0 -> var0x -> true;
         case 1 -> var0[0];
         case 2 -> var0[0].and(var0[1]);
         default -> var1 -> {
         for(Predicate var5 : var0) {
            if (!var5.test(var1)) {
               return false;
            }
         }

         return true;
      };
      };
   }

   public static <T> Predicate<T> orConditions(Predicate<T>[] var0) {
      return switch(var0.length) {
         case 0 -> var0x -> false;
         case 1 -> var0[0];
         case 2 -> var0[0].or(var0[1]);
         default -> var1 -> {
         for(Predicate var5 : var0) {
            if (var5.test(var1)) {
               return true;
            }
         }

         return false;
      };
      };
   }
}
