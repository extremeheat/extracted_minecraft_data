package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConditionalProvider<Value extends Validatable> extends Validatable {
   static <Value extends Validatable, Self extends ConditionalProvider<Value>> MapCodec<Self> mapCodec(final Codec<Holder<Value>> valueCodec, final Holder<Value> defaultIfFalse, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P3 var10000 = i.group(LootItemCondition.CODEC.fieldOf("condition").forGetter(ConditionalProvider::condition), valueCodec.fieldOf("on_true").forGetter(ConditionalProvider::onTrue), valueCodec.optionalFieldOf("on_false", defaultIfFalse).forGetter(ConditionalProvider::onFalse));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   Holder<LootItemCondition> condition();

   Holder<Value> onTrue();

   Holder<Value> onFalse();

   default Holder<Value> selectValue(final LootContext context) {
      return !((LootItemCondition)this.condition().value()).test(context) ? this.onFalse() : this.onTrue();
   }

   default void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "condition", this.condition());
      Validatable.validateHolder(context, "on_true", this.onTrue());
      Validatable.validateHolder(context, "on_false", this.onFalse());
   }

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends ConditionalProvider<Value>> {
      Self create(Holder<LootItemCondition> condition, Holder<Value> onTrue, Holder<Value> onFalse);
   }
}
