package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ConditionalValue(Holder<LootItemCondition> condition, Holder<NumberProvider> onTrue, Holder<NumberProvider> onFalse) implements NumberProvider {
   public static final MapCodec<ConditionalValue> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LootItemCondition.CODEC.fieldOf("condition").forGetter((c) -> c.condition), NumberProviders.CODEC.fieldOf("on_true").forGetter((c) -> c.onTrue), NumberProviders.CODEC.optionalFieldOf("on_false", ConstantValue.exactly(0.0F)).forGetter((c) -> c.onFalse)).apply(i, ConditionalValue::new));
   public static final Codec<ConditionalValue> CODEC;

   public ConditionalValue {
      super();
   }

   public int getInt(final LootContext context) {
      return this.selectValue(context).getInt(context);
   }

   public float getFloat(final LootContext context) {
      return this.selectValue(context).getFloat(context);
   }

   private NumberProvider selectValue(final LootContext context) {
      return !((LootItemCondition)this.condition.value()).test(context) ? (NumberProvider)this.onFalse.value() : (NumberProvider)this.onTrue.value();
   }

   public MapCodec<ConditionalValue> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validateHolder(context, "condition", this.condition);
      Validatable.validateHolder(context, "on_true", this.onTrue);
      Validatable.validateHolder(context, "on_false", this.onFalse);
   }

   static {
      CODEC = MAP_CODEC.codec();
   }
}
