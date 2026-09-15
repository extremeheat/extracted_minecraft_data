package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface DispatcherProvider<Value extends Validatable> extends Validatable {
   static <Value extends Validatable, Self extends DispatcherProvider<Value>> MapCodec<Self> mapCodec(final Codec<Holder<Value>> valueCodec, final Holder<Value> defaultCase, final Factory<Value, Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P2 var10000 = i.group(DispatcherProvider.Case.codec(valueCodec).listOf().fieldOf("cases").forGetter(DispatcherProvider::cases), valueCodec.optionalFieldOf("default", defaultCase).forGetter(DispatcherProvider::defaultValue));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   default Holder<Value> selectValue(final LootContext context) {
      for(Case<Value> aCase : this.cases()) {
         if (aCase.test(context)) {
            return aCase.value;
         }
      }

      return this.defaultValue();
   }

   List<Case<Value>> cases();

   Holder<Value> defaultValue();

   default void validate(final ValidationContext context) {
      Validatable.validate(context, "cases", this.cases());
      Validatable.validateHolder(context, "default", this.defaultValue());
   }

   public static record Case<Value extends Validatable>(Holder<LootItemCondition> condition, Holder<Value> value) implements Validatable {
      public Case {
         super();
      }

      public static <Value extends Validatable> Codec<Case<Value>> codec(final Codec<Holder<Value>> valueCodec) {
         return RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.fieldOf("condition").forGetter(Case::condition), valueCodec.fieldOf("value").forGetter(Case::value)).apply(i, Case::new));
      }

      public boolean test(final LootContext context) {
         return ((LootItemCondition)this.condition.value()).test(context);
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, "condition", this.condition);
         Validatable.validateHolder(context, "value", this.value);
      }
   }

   @FunctionalInterface
   public interface Factory<Value extends Validatable, Self extends DispatcherProvider<Value>> {
      Self create(List<Case<Value>> cases, Holder<Value> defaultValue);
   }
}
