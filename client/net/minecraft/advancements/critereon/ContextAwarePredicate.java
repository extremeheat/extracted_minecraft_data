package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ContextAwarePredicate {
   public static final Codec<ContextAwarePredicate> CODEC;
   private final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositePredicates;

   ContextAwarePredicate(List<LootItemCondition> var1) {
      super();
      this.conditions = var1;
      this.compositePredicates = Util.allOf(var1);
   }

   public static ContextAwarePredicate create(LootItemCondition... var0) {
      return new ContextAwarePredicate(List.of(var0));
   }

   public boolean matches(LootContext var1) {
      return this.compositePredicates.test(var1);
   }

   public void validate(ValidationContext var1) {
      for(int var2 = 0; var2 < this.conditions.size(); ++var2) {
         LootItemCondition var3 = (LootItemCondition)this.conditions.get(var2);
         var3.validate(var1.forChild("[" + var2 + "]"));
      }

   }

   static {
      CODEC = LootItemCondition.DIRECT_CODEC.listOf().xmap(ContextAwarePredicate::new, (var0) -> {
         return var0.conditions;
      });
   }
}
