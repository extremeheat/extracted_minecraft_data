package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
   public static final MapCodec<SequentialEntry> CODEC = createCodec(SequentialEntry::new);

   SequentialEntry(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2) {
      super(var1, var2);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.SEQUENCE;
   }

   protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1) {
      ComposableEntryContainer var10000;
      switch (var1.size()) {
         case 0 -> var10000 = ALWAYS_TRUE;
         case 1 -> var10000 = (ComposableEntryContainer)var1.get(0);
         case 2 -> var10000 = ((ComposableEntryContainer)var1.get(0)).and((ComposableEntryContainer)var1.get(1));
         default -> var10000 = (var1x, var2) -> {
   Iterator var3 = var1.iterator();

   ComposableEntryContainer var4;
   do {
      if (!var3.hasNext()) {
         return true;
      }

      var4 = (ComposableEntryContainer)var3.next();
   } while(var4.expand(var1x, var2));

   return false;
};
      }

      return var10000;
   }

   public static Builder sequential(LootPoolEntryContainer.Builder<?>... var0) {
      return new Builder(var0);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<Builder> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();
         LootPoolEntryContainer.Builder[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootPoolEntryContainer.Builder var5 = var2[var4];
            this.entries.add(var5.build());
         }

      }

      protected Builder getThis() {
         return this;
      }

      public Builder then(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new SequentialEntry(this.entries.build(), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }
}
