package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
   public static final ProblemReporter.Problem NO_CHILDREN_PROBLEM = new ProblemReporter.Problem() {
      public String description() {
         return "Empty children list";
      }
   };
   protected final List<LootPoolEntryContainer> children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(final List<LootPoolEntryContainer> children, final Optional<Holder<LootItemCondition>> condition, final Optional<Holder<LootItemFunction>> modifier) {
      super(condition, modifier);
      this.children = children;
      this.composedChildren = this.compose(children);
   }

   public abstract MapCodec<? extends CompositeEntryBase> codec();

   public void validate(final ValidationContext context) {
      super.validate(context);
      if (this.children.isEmpty()) {
         context.reportProblem(NO_CHILDREN_PROBLEM);
      }

      Validatable.validate(context, "children", this.children);
   }

   protected abstract ComposableEntryContainer compose(List<? extends ComposableEntryContainer> entries);

   public final boolean expandRaw(final LootContext context, final Consumer<LootPoolEntry> output) {
      return this.composedChildren.expand(context, output);
   }

   public static <T extends CompositeEntryBase> MapCodec<T> createCodec(final CompositeEntryConstructor<T> constructor) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P3 var10000 = i.group(LootPoolEntries.CODEC.listOf().optionalFieldOf("children", List.of()).forGetter((e) -> e.children)).and(commonFields(i));
         Objects.requireNonNull(constructor);
         return var10000.apply(i, constructor::create);
      });
   }

   public abstract static class Builder<T extends CompositeEntryBase, B extends Builder<T, B>> extends LootPoolEntryContainer.Builder<B> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

      public Builder(final LootPoolEntryContainer.Builder<?>... entries) {
         super();

         for(LootPoolEntryContainer.Builder<?> entry : entries) {
            this.entries.add(entry.build());
         }

      }

      protected ImmutableList.Builder<LootPoolEntryContainer> addEntry(final LootPoolEntryContainer.Builder<?> entry) {
         return this.entries.add(entry.build());
      }

      protected T build(final CompositeEntryConstructor<T> constructor) {
         return constructor.create(this.entries.build(), this.getCondition(), this.getModifier());
      }
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor<T extends CompositeEntryBase> {
      T create(List<LootPoolEntryContainer> children, Optional<Holder<LootItemCondition>> condition, Optional<Holder<LootItemFunction>> modifier);
   }
}
