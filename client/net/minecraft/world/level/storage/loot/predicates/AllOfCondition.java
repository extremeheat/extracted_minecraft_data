package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import java.util.List;

public class AllOfCondition extends CompositeLootItemCondition {
   public static final Codec<AllOfCondition> CODEC = createCodec(AllOfCondition::new);
   public static final Codec<AllOfCondition> INLINE_CODEC = createInlineCodec(AllOfCondition::new);

   AllOfCondition(List<LootItemCondition> var1) {
      super(var1, LootItemConditions.andConditions(var1));
   }

   public static AllOfCondition allOf(List<LootItemCondition> var0) {
      return new AllOfCondition(List.copyOf(var0));
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
      protected LootItemCondition create(List<LootItemCondition> var1) {
         return new AllOfCondition(var1);
      }
   }
}
