package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class DamageSourceCondition implements LootItemCondition {
   final DamageSourcePredicate predicate;

   DamageSourceCondition(DamageSourcePredicate var1) {
      super();
      this.predicate = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
   }

   public boolean test(LootContext var1) {
      DamageSource var2 = (DamageSource)var1.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
      Vec3 var3 = (Vec3)var1.getParamOrNull(LootContextParams.ORIGIN);
      return var3 != null && var2 != null && this.predicate.matches(var1.getLevel(), var3, var2);
   }

   public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder var0) {
      return () -> {
         return new DamageSourceCondition(var0.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<DamageSourceCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, DamageSourceCondition var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
      }

      public DamageSourceCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         DamageSourcePredicate var3 = DamageSourcePredicate.fromJson(var1.get("predicate"));
         return new DamageSourceCondition(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
