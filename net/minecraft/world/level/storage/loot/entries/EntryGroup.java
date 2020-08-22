package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   EntryGroup(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var1, var2);
   }

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
            ComposableEntryContainer[] var3 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ComposableEntryContainer var6 = var3[var5];
               var6.expand(var1x, var2x);
            }

            return true;
         };
      }
   }
}
