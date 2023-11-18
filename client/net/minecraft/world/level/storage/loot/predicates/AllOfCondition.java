package net.minecraft.world.level.storage.loot.predicates;

public class AllOfCondition extends CompositeLootItemCondition {
   AllOfCondition(LootItemCondition[] var1) {
      super(var1, LootItemConditions.andConditions(var1));
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.ALL_OF;
   }

   public static AllOfCondition.Builder allOf(LootItemCondition.Builder... var0) {
      return new AllOfCondition.Builder(var0);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(LootItemCondition.Builder... var1) {
         super(var1);
      }

      @Override
      public AllOfCondition.Builder and(LootItemCondition.Builder var1) {
         this.addTerm(var1);
         return this;
      }

      @Override
      protected LootItemCondition create(LootItemCondition[] var1) {
         return new AllOfCondition(var1);
      }
   }

   public static class Serializer extends CompositeLootItemCondition.Serializer<AllOfCondition> {
      public Serializer() {
         super();
      }

      protected AllOfCondition create(LootItemCondition[] var1) {
         return new AllOfCondition(var1);
      }
   }
}
