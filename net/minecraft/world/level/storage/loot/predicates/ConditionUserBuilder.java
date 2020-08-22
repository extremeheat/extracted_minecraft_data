package net.minecraft.world.level.storage.loot.predicates;

public interface ConditionUserBuilder {
   Object when(LootItemCondition.Builder var1);

   Object unwrap();
}
