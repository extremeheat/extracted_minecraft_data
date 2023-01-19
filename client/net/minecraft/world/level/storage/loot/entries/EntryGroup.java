package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   EntryGroup(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var1, var2);
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.GROUP;
   }

   @Override
   protected ComposableEntryContainer compose(ComposableEntryContainer[] var1) {
      switch(var1.length) {
         case 0:
            return ALWAYS_TRUE;
         case 1:
            return var1[0];
         case 2:
            ComposableEntryContainer var2 = var1[0];
            ComposableEntryContainer var3 = var1[1];
            return (var2x, var3x) -> {
               var2.expand(var2x, var3x);
               var3.expand(var2x, var3x);
               return true;
            };
         default:
            return (var1x, var2x) -> {
               for(ComposableEntryContainer var6 : var1) {
                  var6.expand(var1x, var2x);
               }

               return true;
            };
      }
   }

   public static EntryGroup.Builder list(LootPoolEntryContainer.Builder<?>... var0) {
      return new EntryGroup.Builder(var0);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<EntryGroup.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

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
         return new EntryGroup(this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }
   }
}
