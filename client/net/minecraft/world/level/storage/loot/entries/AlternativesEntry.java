package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AlternativesEntry extends CompositeEntryBase {
   public static final MapCodec<AlternativesEntry> CODEC = createCodec(AlternativesEntry::new);

   AlternativesEntry(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2) {
      super(var1, var2);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.ALTERNATIVES;
   }

   protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1) {
      ComposableEntryContainer var10000;
      switch (var1.size()) {
         case 0 -> var10000 = ALWAYS_FALSE;
         case 1 -> var10000 = (ComposableEntryContainer)var1.get(0);
         case 2 -> var10000 = ((ComposableEntryContainer)var1.get(0)).or((ComposableEntryContainer)var1.get(1));
         default -> var10000 = (var1x, var2) -> {
   for(ComposableEntryContainer var4 : var1) {
      if (var4.expand(var1x, var2)) {
         return true;
      }
   }

   return false;
};
      }

      return var10000;
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.children.size() - 1; ++var2) {
         if (((LootPoolEntryContainer)this.children.get(var2)).conditions.isEmpty()) {
            var1.reportProblem("Unreachable entry!");
         }
      }

   }

   public static Builder alternatives(LootPoolEntryContainer.Builder<?>... var0) {
      return new Builder(var0);
   }

   public static <E> Builder alternatives(Collection<E> var0, Function<E, LootPoolEntryContainer.Builder<?>> var1) {
      Stream var10002 = var0.stream();
      Objects.requireNonNull(var1);
      return new Builder((LootPoolEntryContainer.Builder[])var10002.map(var1::apply).toArray((var0x) -> new LootPoolEntryContainer.Builder[var0x]));
   }

   public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();

         for(LootPoolEntryContainer.Builder var5 : var1) {
            this.entries.add(var5.build());
         }

      }

      protected Builder getThis() {
         return this;
      }

      public Builder otherwise(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new AlternativesEntry(this.entries.build(), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }
}
