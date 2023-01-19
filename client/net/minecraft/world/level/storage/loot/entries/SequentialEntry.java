package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
   SequentialEntry(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var1, var2);
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.SEQUENCE;
   }

   @Override
   protected ComposableEntryContainer compose(ComposableEntryContainer[] var1) {
      switch(var1.length) {
         case 0:
            return ALWAYS_TRUE;
         case 1:
            return var1[0];
         case 2:
            return var1[0].and(var1[1]);
         default:
            return (var1x, var2) -> {
               for(ComposableEntryContainer var6 : var1) {
                  if (!var6.expand(var1x, var2)) {
                     return false;
                  }
               }

               return true;
            };
      }
   }

   public static SequentialEntry.Builder sequential(LootPoolEntryContainer.Builder<?>... var0) {
      return new SequentialEntry.Builder(var0);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<SequentialEntry.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();

         for(LootPoolEntryContainer.Builder var5 : var1) {
            this.entries.add(var5.build());
         }
      }

      protected SequentialEntry.Builder getThis() {
         return this;
      }

      @Override
      public SequentialEntry.Builder then(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      @Override
      public LootPoolEntryContainer build() {
         return new SequentialEntry(this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }
   }
}
