package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
   SequentialEntry(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var1, var2);
   }

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
            ComposableEntryContainer[] var3 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ComposableEntryContainer var6 = var3[var5];
               if (!var6.expand(var1x, var2)) {
                  return false;
               }
            }

            return true;
         };
      }
   }
}
