package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AlternativesEntry extends CompositeEntryBase {
   public static final MapCodec<AlternativesEntry> CODEC = createCodec(AlternativesEntry::new);

   AlternativesEntry(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2) {
      super(var1, var2);
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.ALTERNATIVES;
   }

   @Override
   protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1) {
      return switch (var1.size()) {
         case 0 -> ALWAYS_FALSE;
         case 1 -> (ComposableEntryContainer)var1.get(0);
         case 2 -> ((ComposableEntryContainer)var1.get(0)).or((ComposableEntryContainer)var1.get(1));
         default -> (var1x, var2) -> {
         for (ComposableEntryContainer var4 : var1) {
            if (var4.expand(var1x, var2)) {
               return true;
            }
         }

         return false;
      };
      };
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);

      for (int var2 = 0; var2 < this.children.size() - 1; var2++) {
         if (this.children.get(var2).conditions.isEmpty()) {
            var1.reportProblem("Unreachable entry!");
         }
      }
   }

   public static AlternativesEntry.Builder alternatives(LootPoolEntryContainer.Builder<?>... var0) {
      return new AlternativesEntry.Builder(var0);
   }

   public static <E> AlternativesEntry.Builder alternatives(Collection<E> var0, Function<E, LootPoolEntryContainer.Builder<?>> var1) {
      return new AlternativesEntry.Builder(var0.stream().map(var1::apply).toArray(LootPoolEntryContainer.Builder[]::new));
   }

   public static class Builder extends LootPoolEntryContainer.Builder<AlternativesEntry.Builder> {
      private final com.google.common.collect.ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();

         for (LootPoolEntryContainer.Builder var5 : var1) {
            this.entries.add(var5.build());
         }
      }

      protected AlternativesEntry.Builder getThis() {
         return this;
      }

      @Override
      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      @Override
      public LootPoolEntryContainer build() {
         return new AlternativesEntry(this.entries.build(), this.getConditions());
      }
   }
}
