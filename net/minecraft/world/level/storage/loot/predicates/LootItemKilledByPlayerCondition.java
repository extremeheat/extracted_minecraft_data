package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemKilledByPlayerCondition implements LootItemCondition {
   private static final LootItemKilledByPlayerCondition INSTANCE = new LootItemKilledByPlayerCondition();

   private LootItemKilledByPlayerCondition() {
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public boolean test(LootContext var1) {
      return var1.hasParam(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public static LootItemCondition.Builder killedByPlayer() {
      return () -> {
         return INSTANCE;
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("killed_by_player"), LootItemKilledByPlayerCondition.class);
      }

      public void serialize(JsonObject var1, LootItemKilledByPlayerCondition var2, JsonSerializationContext var3) {
      }

      public LootItemKilledByPlayerCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return LootItemKilledByPlayerCondition.INSTANCE;
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
