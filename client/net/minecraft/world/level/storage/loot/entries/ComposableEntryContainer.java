package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

@FunctionalInterface
interface ComposableEntryContainer {
   ComposableEntryContainer ALWAYS_FALSE = (var0, var1) -> {
      return false;
   };
   ComposableEntryContainer ALWAYS_TRUE = (var0, var1) -> {
      return true;
   };

   boolean expand(LootContext var1, Consumer<LootPoolEntry> var2);

   default ComposableEntryContainer and(ComposableEntryContainer var1) {
      Objects.requireNonNull(var1);
      return (var2, var3) -> {
         return this.expand(var2, var3) && var1.expand(var2, var3);
      };
   }

   default ComposableEntryContainer or(ComposableEntryContainer var1) {
      Objects.requireNonNull(var1);
      return (var2, var3) -> {
         return this.expand(var2, var3) || var1.expand(var2, var3);
      };
   }
}
