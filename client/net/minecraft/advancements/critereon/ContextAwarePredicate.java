package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ContextAwarePredicate {
   public static final ContextAwarePredicate ANY = new ContextAwarePredicate(new LootItemCondition[0]);
   private final LootItemCondition[] conditions;
   private final Predicate<LootContext> compositePredicates;

   ContextAwarePredicate(LootItemCondition[] var1) {
      super();
      this.conditions = var1;
      this.compositePredicates = LootItemConditions.andConditions(var1);
   }

   public static ContextAwarePredicate create(LootItemCondition... var0) {
      return new ContextAwarePredicate(var0);
   }

   @Nullable
   public static ContextAwarePredicate fromElement(String var0, DeserializationContext var1, @Nullable JsonElement var2, LootContextParamSet var3) {
      if (var2 != null && var2.isJsonArray()) {
         LootItemCondition[] var4 = var1.deserializeConditions(var2.getAsJsonArray(), var1.getAdvancementId() + "/" + var0, var3);
         return new ContextAwarePredicate(var4);
      } else {
         return null;
      }
   }

   public boolean matches(LootContext var1) {
      return this.compositePredicates.test(var1);
   }

   public JsonElement toJson(SerializationContext var1) {
      return (JsonElement)(this.conditions.length == 0 ? JsonNull.INSTANCE : var1.serializeConditions(this.conditions));
   }

   public static JsonElement toJson(ContextAwarePredicate[] var0, SerializationContext var1) {
      if (var0.length == 0) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray var2 = new JsonArray();

         for(ContextAwarePredicate var6 : var0) {
            var2.add(var6.toJson(var1));
         }

         return var2;
      }
   }
}
