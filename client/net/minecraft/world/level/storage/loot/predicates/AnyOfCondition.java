package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.Util;

public class AnyOfCondition extends CompositeLootItemCondition {
   public static final MapCodec<AnyOfCondition> CODEC = createCodec(AnyOfCondition::new);

   AnyOfCondition(List<LootItemCondition> var1) {
      super(var1, Util.anyOf(var1));
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ANY_OF;
   }

   public static Builder anyOf(LootItemCondition.Builder... var0) {
      return new Builder(var0);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(LootItemCondition.Builder... var1) {
         super(var1);
      }

      public Builder or(LootItemCondition.Builder var1) {
         this.addTerm(var1);
         return this;
      }

      protected LootItemCondition create(List<LootItemCondition> var1) {
         return new AnyOfCondition(var1);
      }
   }
}
