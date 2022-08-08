package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootItemEntityPropertyCondition implements LootItemCondition {
   final EntityPredicate predicate;
   final LootContext.EntityTarget entityTarget;

   LootItemEntityPropertyCondition(EntityPredicate var1, LootContext.EntityTarget var2) {
      super();
      this.predicate = var1;
      this.entityTarget = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ENTITY_PROPERTIES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN, this.entityTarget.getParam());
   }

   public boolean test(LootContext var1) {
      Entity var2 = (Entity)var1.getParamOrNull(this.entityTarget.getParam());
      Vec3 var3 = (Vec3)var1.getParamOrNull(LootContextParams.ORIGIN);
      return this.predicate.matches(var1.getLevel(), var3, var2);
   }

   public static LootItemCondition.Builder entityPresent(LootContext.EntityTarget var0) {
      return hasProperties(var0, EntityPredicate.Builder.entity());
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget var0, EntityPredicate.Builder var1) {
      return () -> {
         return new LootItemEntityPropertyCondition(var1.build(), var0);
      };
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget var0, EntityPredicate var1) {
      return () -> {
         return new LootItemEntityPropertyCondition(var1, var0);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemEntityPropertyCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LootItemEntityPropertyCondition var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
         var1.add("entity", var3.serialize(var2.entityTarget));
      }

      public LootItemEntityPropertyCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         EntityPredicate var3 = EntityPredicate.fromJson(var1.get("predicate"));
         return new LootItemEntityPropertyCondition(var3, (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", var2, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
