package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ExplosionCondition implements LootItemCondition {
   static final ExplosionCondition INSTANCE = new ExplosionCondition();

   private ExplosionCondition() {
      super();
   }

   public LootItemConditionType getType() {
      return LootItemConditions.SURVIVES_EXPLOSION;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext var1) {
      Float var2 = (Float)var1.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if (var2 != null) {
         RandomSource var3 = var1.getRandom();
         float var4 = 1.0F / var2;
         return var3.nextFloat() <= var4;
      } else {
         return true;
      }
   }

   public static LootItemCondition.Builder survivesExplosion() {
      return () -> {
         return INSTANCE;
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ExplosionCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ExplosionCondition var2, JsonSerializationContext var3) {
      }

      public ExplosionCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return ExplosionCondition.INSTANCE;
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
