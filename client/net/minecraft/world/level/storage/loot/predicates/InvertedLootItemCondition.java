package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.TriBool;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.jspecify.annotations.Nullable;

public record InvertedLootItemCondition(Holder<LootItemCondition> term) implements LootItemCondition {
   public static final MapCodec<InvertedLootItemCondition> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LootItemCondition.CODEC.fieldOf("term").forGetter(InvertedLootItemCondition::term)).apply(i, InvertedLootItemCondition::new));

   public InvertedLootItemCondition {
      super();
   }

   public MapCodec<InvertedLootItemCondition> codec() {
      return MAP_CODEC;
   }

   public boolean test(final LootContext context) {
      return !((LootItemCondition)this.term.value()).test(context);
   }

   public void validate(final ValidationContext output) {
      Validatable.validateHolder(output, "term", this.term);
   }

   public @Nullable Boolean constantValue(final LootItemCondition.ConstantConditionEvaluationContext context) {
      LootItemCondition subCondition = this.term.value();
      if (!context.push(subCondition)) {
         return null;
      } else {
         Boolean result = TriBool.not(subCondition.constantValue(context));
         context.pop(subCondition);
         return result;
      }
   }

   public static LootItemCondition.Builder invert(final LootItemCondition.Builder term) {
      return invert(Holder.direct(term.build()));
   }

   public static LootItemCondition.Builder invert(final Holder<LootItemCondition> term) {
      InvertedLootItemCondition result = new InvertedLootItemCondition(term);
      return () -> result;
   }
}
