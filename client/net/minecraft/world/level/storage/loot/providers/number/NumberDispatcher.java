package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record NumberDispatcher(List<Case> cases, Holder<NumberProvider> defaultValue) implements NumberProvider {
   public static final MapCodec<NumberDispatcher> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(NumberDispatcher.Case.CODEC.listOf().fieldOf("cases").forGetter((n) -> n.cases), NumberProviders.CODEC.optionalFieldOf("default", ConstantValue.exactly(0.0F)).forGetter((n) -> n.defaultValue)).apply(i, NumberDispatcher::new));

   public NumberDispatcher {
      super();
   }

   private NumberProvider selectValue(final LootContext context) {
      for(Case aCase : this.cases) {
         if (aCase.test(context)) {
            return aCase.numberProvider.value();
         }
      }

      return this.defaultValue.value();
   }

   public float getFloat(final LootContext context) {
      return this.selectValue(context).getFloat(context);
   }

   public int getInt(final LootContext context) {
      return this.selectValue(context).getInt(context);
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validate(context, "cases", this.cases);
      Validatable.validateHolder(context, "default", this.defaultValue);
   }

   public MapCodec<NumberDispatcher> codec() {
      return MAP_CODEC;
   }

   public static record Case(Holder<LootItemCondition> condition, Holder<NumberProvider> numberProvider) implements Validatable {
      public static final Codec<Case> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.fieldOf("condition").forGetter((c) -> c.condition), NumberProviders.CODEC.fieldOf("number_provider").forGetter((c) -> c.numberProvider)).apply(i, Case::new));

      public Case {
         super();
      }

      public boolean test(final LootContext context) {
         return ((LootItemCondition)this.condition.value()).test(context);
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, "condition", this.condition);
         Validatable.validateHolder(context, "number_provider", this.numberProvider);
      }
   }
}
