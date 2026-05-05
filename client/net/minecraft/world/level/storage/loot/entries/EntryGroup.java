package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   public static final MapCodec<EntryGroup> MAP_CODEC = createCodec(EntryGroup::new);

   public EntryGroup(final List<LootPoolEntryContainer> children, final List<LootItemCondition> conditions) {
      super(children, conditions);
   }

   public MapCodec<EntryGroup> codec() {
      return MAP_CODEC;
   }

   protected ComposableEntryContainer compose(final List<? extends ComposableEntryContainer> entries) {
      ComposableEntryContainer var10000;
      switch (entries.size()) {
         case 0:
            var10000 = ALWAYS_TRUE;
            break;
         case 1:
            var10000 = (ComposableEntryContainer)entries.get(0);
            break;
         case 2:
            ComposableEntryContainer first = (ComposableEntryContainer)entries.get(0);
            ComposableEntryContainer second = (ComposableEntryContainer)entries.get(1);
            var10000 = (context, output) -> {
               first.expand(context, output);
               second.expand(context, output);
               return true;
            };
            break;
         default:
            var10000 = (context, output) -> {
               for(ComposableEntryContainer entry : entries) {
                  entry.expand(context, output);
               }

               return true;
            };
      }

      return var10000;
   }

   public static Builder list(final LootPoolEntryContainer.Builder<?>... entries) {
      return new Builder(entries);
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

      public Builder append(final LootPoolEntryContainer.Builder<?> other) {
         this.entries.add(other.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new EntryGroup(this.entries.build(), this.getConditions());
      }
   }
}
