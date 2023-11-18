package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolSingletonContainer extends LootPoolEntryContainer {
   public static final int DEFAULT_WEIGHT = 1;
   public static final int DEFAULT_QUALITY = 0;
   protected final int weight;
   protected final int quality;
   protected final List<LootItemFunction> functions;
   final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
   private final LootPoolEntry entry = new LootPoolSingletonContainer.EntryBase() {
      @Override
      public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
         LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, var1, var2), var2);
      }
   };

   protected LootPoolSingletonContainer(int var1, int var2, List<LootItemCondition> var3, List<LootItemFunction> var4) {
      super(var3);
      this.weight = var1;
      this.quality = var2;
      this.functions = var4;
      this.compositeFunction = LootItemFunctions.compose(var4);
   }

   protected static <T extends LootPoolSingletonContainer> P4<Mu<T>, Integer, Integer, List<LootItemCondition>, List<LootItemFunction>> singletonFields(
      Instance<T> var0
   ) {
      return var0.group(
            ExtraCodecs.strictOptionalField(Codec.INT, "weight", 1).forGetter(var0x -> var0x.weight),
            ExtraCodecs.strictOptionalField(Codec.INT, "quality", 0).forGetter(var0x -> var0x.quality)
         )
         .and(commonFields(var0).t1())
         .and(ExtraCodecs.strictOptionalField(LootItemFunctions.CODEC.listOf(), "functions", List.of()).forGetter(var0x -> var0x.functions));
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.functions.size(); ++var2) {
         this.functions.get(var2).validate(var1.forChild(".functions[" + var2 + "]"));
      }
   }

   protected abstract void createItemStack(Consumer<ItemStack> var1, LootContext var2);

   @Override
   public boolean expand(LootContext var1, Consumer<LootPoolEntry> var2) {
      if (this.canRun(var1)) {
         var2.accept(this.entry);
         return true;
      } else {
         return false;
      }
   }

   public static LootPoolSingletonContainer.Builder<?> simpleBuilder(LootPoolSingletonContainer.EntryConstructor var0) {
      return new LootPoolSingletonContainer.DummyBuilder(var0);
   }

   public abstract static class Builder<T extends LootPoolSingletonContainer.Builder<T>>
      extends LootPoolEntryContainer.Builder<T>
      implements FunctionUserBuilder<T> {
      protected int weight = 1;
      protected int quality = 0;
      private final com.google.common.collect.ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();

      public Builder() {
         super();
      }

      public T apply(LootItemFunction.Builder var1) {
         this.functions.add(var1.build());
         return this.getThis();
      }

      protected List<LootItemFunction> getFunctions() {
         return this.functions.build();
      }

      public T setWeight(int var1) {
         this.weight = var1;
         return this.getThis();
      }

      public T setQuality(int var1) {
         this.quality = var1;
         return this.getThis();
      }
   }

   static class DummyBuilder extends LootPoolSingletonContainer.Builder<LootPoolSingletonContainer.DummyBuilder> {
      private final LootPoolSingletonContainer.EntryConstructor constructor;

      public DummyBuilder(LootPoolSingletonContainer.EntryConstructor var1) {
         super();
         this.constructor = var1;
      }

      protected LootPoolSingletonContainer.DummyBuilder getThis() {
         return this;
      }

      @Override
      public LootPoolEntryContainer build() {
         return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
      }
   }

   protected abstract class EntryBase implements LootPoolEntry {
      protected EntryBase() {
         super();
      }

      @Override
      public int getWeight(float var1) {
         return Math.max(Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * var1), 0);
      }
   }

   @FunctionalInterface
   protected interface EntryConstructor {
      LootPoolSingletonContainer build(int var1, int var2, List<LootItemCondition> var3, List<LootItemFunction> var4);
   }
}
