package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class UniformContainerBase extends LootPoolEntryContainer {
   public static final int DEFAULT_WEIGHT = 1;
   public static final int DEFAULT_QUALITY = 0;
   protected final int weight;
   protected final int quality;

   protected UniformContainerBase(final int weight, final int quality, final Optional<Holder<LootItemCondition>> condition, final Optional<Holder<LootItemFunction>> modifier) {
      super(condition, modifier);
      this.weight = weight;
      this.quality = quality;
   }

   public abstract MapCodec<? extends UniformContainerBase> codec();

   protected static <T extends UniformContainerBase> Products.P4<RecordCodecBuilder.Mu<T>, Integer, Integer, Optional<Holder<LootItemCondition>>, Optional<Holder<LootItemFunction>>> uniformFields(final RecordCodecBuilder.Instance<T> i) {
      return i.group(Codec.INT.optionalFieldOf("weight", 1).forGetter((e) -> e.weight), Codec.INT.optionalFieldOf("quality", 0).forGetter((e) -> e.quality)).and(commonFields(i).t1()).and(LootItemFunctions.CODEC.optionalFieldOf("modifier").forGetter((e) -> e.modifier));
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "modifier", this.modifier);
   }

   public static Builder<?> simpleBuilder(final EntryConstructor constructor) {
      return new DummyBuilder(constructor);
   }

   public abstract static class Builder<T extends Builder<T>> extends LootPoolEntryContainer.Builder<T> implements FunctionUserBuilder<T> {
      protected int weight = 1;
      protected int quality = 0;

      public Builder() {
         super();
      }

      public T setWeight(final int weight) {
         this.weight = weight;
         return (T)(this.getThis());
      }

      public T setQuality(final int quality) {
         this.quality = quality;
         return (T)(this.getThis());
      }
   }

   protected abstract class EntryBase implements LootPoolEntry {
      protected EntryBase() {
         Objects.requireNonNull(UniformContainerBase.this);
         super();
      }

      public int getWeight(final float luck) {
         return Math.max(Mth.floor((float)UniformContainerBase.this.weight + (float)UniformContainerBase.this.quality * luck), 0);
      }
   }

   private static class DummyBuilder extends Builder<DummyBuilder> {
      private final EntryConstructor constructor;

      public DummyBuilder(final EntryConstructor constructor) {
         super();
         this.constructor = constructor;
      }

      protected DummyBuilder getThis() {
         return this;
      }

      public LootPoolEntryContainer build() {
         return this.constructor.build(this.weight, this.quality, this.getCondition(), this.getModifier());
      }
   }

   @FunctionalInterface
   public interface EntryConstructor {
      UniformContainerBase build(int weight, int quality, Optional<Holder<LootItemCondition>> condition, Optional<Holder<LootItemFunction>> modifier);
   }
}
