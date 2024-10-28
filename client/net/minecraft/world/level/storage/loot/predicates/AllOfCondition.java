package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.Util;

public class AllOfCondition extends CompositeLootItemCondition {
   public static final MapCodec<AllOfCondition> CODEC = createCodec(AllOfCondition::new);
   public static final Codec<AllOfCondition> INLINE_CODEC = createInlineCodec(AllOfCondition::new);

   AllOfCondition(List<LootItemCondition> var1) {
      super(var1, Util.allOf(var1));
   }

   public static AllOfCondition allOf(List<LootItemCondition> var0) {
      return new AllOfCondition(List.copyOf(var0));
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ALL_OF;
   }

   public static Builder allOf(LootItemCondition.Builder... var0) {
      return new Builder(var0);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(LootItemCondition.Builder... var1) {
         super(var1);
      }

      public Builder and(LootItemCondition.Builder var1) {
         this.addTerm(var1);
         return this;
      }

      protected LootItemCondition create(List<LootItemCondition> var1) {
         return new AllOfCondition(var1);
      }
   }
}
