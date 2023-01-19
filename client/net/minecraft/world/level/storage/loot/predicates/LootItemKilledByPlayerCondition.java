package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemKilledByPlayerCondition implements LootItemCondition {
   static final LootItemKilledByPlayerCondition INSTANCE = new LootItemKilledByPlayerCondition();

   private LootItemKilledByPlayerCondition() {
      super();
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.KILLED_BY_PLAYER;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public boolean test(LootContext var1) {
      return var1.hasParam(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public static LootItemCondition.Builder killedByPlayer() {
      return () -> INSTANCE;
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemKilledByPlayerCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LootItemKilledByPlayerCondition var2, JsonSerializationContext var3) {
      }

      public LootItemKilledByPlayerCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return LootItemKilledByPlayerCondition.INSTANCE;
      }
   }
}
