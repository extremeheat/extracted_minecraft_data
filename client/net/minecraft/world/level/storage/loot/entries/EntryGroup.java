package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   public static final MapCodec<EntryGroup> CODEC = createCodec(EntryGroup::new);

   EntryGroup(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2) {
      super(var1, var2);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.GROUP;
   }

   protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1) {
      ComposableEntryContainer var10000;
      switch (var1.size()) {
         case 0:
            var10000 = ALWAYS_TRUE;
            break;
         case 1:
            var10000 = (ComposableEntryContainer)var1.get(0);
            break;
         case 2:
            ComposableEntryContainer var2 = (ComposableEntryContainer)var1.get(0);
            ComposableEntryContainer var3 = (ComposableEntryContainer)var1.get(1);
            var10000 = (var2x, var3x) -> {
               var2.expand(var2x, var3x);
               var3.expand(var2x, var3x);
               return true;
            };
            break;
         default:
            var10000 = (var1x, var2x) -> {
               for(ComposableEntryContainer var4 : var1) {
                  var4.expand(var1x, var2x);
               }

               return true;
            };
      }

      return var10000;
   }

   public static Builder list(LootPoolEntryContainer.Builder<?>... var0) {
      return new Builder(var0);
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

      public Builder append(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new EntryGroup(this.entries.build(), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }
}
