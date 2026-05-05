package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AlternativesEntry extends CompositeEntryBase {
   public static final MapCodec<AlternativesEntry> MAP_CODEC = createCodec(AlternativesEntry::new);
   public static final ProblemReporter.Problem UNREACHABLE_PROBLEM = new ProblemReporter.Problem() {
      public String description() {
         return "Unreachable entry!";
      }
   };

   public AlternativesEntry(final List<LootPoolEntryContainer> children, final List<LootItemCondition> conditions) {
      super(children, conditions);
   }

   public MapCodec<AlternativesEntry> codec() {
      return MAP_CODEC;
   }

   protected ComposableEntryContainer compose(final List<? extends ComposableEntryContainer> entries) {
      ComposableEntryContainer var10000;
      switch (entries.size()) {
         case 0 -> var10000 = ALWAYS_FALSE;
         case 1 -> var10000 = (ComposableEntryContainer)entries.get(0);
         case 2 -> var10000 = ((ComposableEntryContainer)entries.get(0)).or((ComposableEntryContainer)entries.get(1));
         default -> var10000 = (context, output) -> {
   for(ComposableEntryContainer entry : entries) {
      if (entry.expand(context, output)) {
         return true;
      }
   }

   return false;
};
      }

      return var10000;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);

      for(int i = 0; i < this.children.size() - 1; ++i) {
         if (((LootPoolEntryContainer)this.children.get(i)).conditions.isEmpty()) {
            context.reportProblem(UNREACHABLE_PROBLEM);
         }
      }

   }

   public static Builder alternatives(final LootPoolEntryContainer.Builder<?>... entries) {
      return new Builder(entries);
   }

   public static <E> Builder alternatives(final Collection<E> items, final Function<E, LootPoolEntryContainer.Builder<?>> provider) {
      Stream var10002 = items.stream();
      Objects.requireNonNull(provider);
      return new Builder((LootPoolEntryContainer.Builder[])var10002.map(provider::apply).toArray((x$0) -> new LootPoolEntryContainer.Builder[x$0]));
   }

   public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

      public Builder(final LootPoolEntryContainer.Builder<?>... entries) {
         super();

         for(LootPoolEntryContainer.Builder<?> entry : entries) {
            this.entries.add(entry.build());
         }

      }

      protected Builder getThis() {
         return this;
      }

      public Builder otherwise(final LootPoolEntryContainer.Builder<?> other) {
         this.entries.add(other.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new AlternativesEntry(this.entries.build(), this.getConditions());
      }
   }
}
