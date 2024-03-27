package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   public static final MapCodec<EntryGroup> CODEC = createCodec(EntryGroup::new);

   EntryGroup(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2) {
      super(var1, var2);
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.GROUP;
   }

   @Override
   protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1) {
      return switch(var1.size()) {
         case 0 -> ALWAYS_TRUE;
         case 1 -> (ComposableEntryContainer)var1.get(0);
         case 2 -> {
            ComposableEntryContainer var2 = (ComposableEntryContainer)var1.get(0);
            ComposableEntryContainer var3 = (ComposableEntryContainer)var1.get(1);
            yield (var2x, var3x) -> {
               var2.expand(var2x, var3x);
               var3.expand(var2x, var3x);
               return true;
            };
         }
         default -> (var1x, var2x) -> {
         for(ComposableEntryContainer var4 : var1) {
            var4.expand(var1x, var2x);
         }

         return true;
      };
      };
   }

   public static EntryGroup.Builder list(LootPoolEntryContainer.Builder<?>... var0) {
      return new EntryGroup.Builder(var0);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<EntryGroup.Builder> {
      private final com.google.common.collect.ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();

         for(LootPoolEntryContainer.Builder var5 : var1) {
            this.entries.add(var5.build());
         }
      }

      protected EntryGroup.Builder getThis() {
         return this;
      }

      @Override
      public EntryGroup.Builder append(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      @Override
      public LootPoolEntryContainer build() {
         return new EntryGroup(this.entries.build(), this.getConditions());
      }
   }
}
