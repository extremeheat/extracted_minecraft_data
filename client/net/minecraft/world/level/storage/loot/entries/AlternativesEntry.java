package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesEntry extends CompositeEntryBase {
   AlternativesEntry(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var1, var2);
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.ALTERNATIVES;
   }

   @Override
   protected ComposableEntryContainer compose(ComposableEntryContainer[] var1) {
      switch(var1.length) {
         case 0:
            return ALWAYS_FALSE;
         case 1:
            return var1[0];
         case 2:
            return var1[0].or(var1[1]);
         default:
            return (var1x, var2) -> {
               for(ComposableEntryContainer var6 : var1) {
                  if (var6.expand(var1x, var2)) {
                     return true;
                  }
               }

               return false;
            };
      }
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.children.length - 1; ++var2) {
         if (ArrayUtils.isEmpty(this.children[var2].conditions)) {
            var1.reportProblem("Unreachable entry!");
         }
      }
   }

   public static AlternativesEntry.Builder alternatives(LootPoolEntryContainer.Builder<?>... var0) {
      return new AlternativesEntry.Builder(var0);
   }

   public static <E> AlternativesEntry.Builder alternatives(Collection<E> var0, Function<E, LootPoolEntryContainer.Builder<?>> var1) {
      return new AlternativesEntry.Builder(var0.stream().map(var1::apply).toArray(var0x -> new LootPoolEntryContainer.Builder[var0x]));
   }

   public static class Builder extends LootPoolEntryContainer.Builder<AlternativesEntry.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();

         for(LootPoolEntryContainer.Builder var5 : var1) {
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
         return new AlternativesEntry(this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }
   }
}
