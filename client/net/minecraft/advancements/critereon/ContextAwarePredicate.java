package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ContextAwarePredicate {
   private final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositePredicates;

   ContextAwarePredicate(List<LootItemCondition> var1) {
      super();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("ContextAwarePredicate must have at least one condition");
      } else {
         this.conditions = var1;
         this.compositePredicates = LootItemConditions.andConditions(var1);
      }
   }

   public static ContextAwarePredicate create(LootItemCondition... var0) {
      return new ContextAwarePredicate(List.of(var0));
   }

   public static Optional<Optional<ContextAwarePredicate>> fromElement(
      String var0, DeserializationContext var1, @Nullable JsonElement var2, LootContextParamSet var3
   ) {
      if (var2 != null && var2.isJsonArray()) {
         List var4 = var1.deserializeConditions(var2.getAsJsonArray(), var1.getAdvancementId() + "/" + var0, var3);
         return var4.isEmpty() ? Optional.of(Optional.empty()) : Optional.of(Optional.of(new ContextAwarePredicate(var4)));
      } else {
         return Optional.empty();
      }
   }

   public boolean matches(LootContext var1) {
      return this.compositePredicates.test(var1);
   }

   public JsonElement toJson() {
      return Util.getOrThrow(LootItemConditions.CODEC.listOf().encodeStart(JsonOps.INSTANCE, this.conditions), IllegalStateException::new);
   }

   public static JsonElement toJson(List<ContextAwarePredicate> var0) {
      if (var0.isEmpty()) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray var1 = new JsonArray();

         for(ContextAwarePredicate var3 : var0) {
            var1.add(var3.toJson());
         }

         return var1;
      }
   }
}
