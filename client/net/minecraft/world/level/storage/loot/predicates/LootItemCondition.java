package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemCondition extends LootContextUser, Predicate<LootContext> {
   LootItemConditionType getType();

   @FunctionalInterface
   public interface Builder {
      LootItemCondition build();

      default LootItemCondition.Builder invert() {
         return InvertedLootItemCondition.invert(this);
      }

      // $FF: renamed from: or (net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder) net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition$Builder
      default AlternativeLootItemCondition.Builder method_13(LootItemCondition.Builder var1) {
         return AlternativeLootItemCondition.alternative(this, var1);
      }
   }
}
