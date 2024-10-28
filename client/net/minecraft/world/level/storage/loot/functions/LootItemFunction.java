package net.minecraft.world.level.storage.loot.functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemFunction extends LootContextUser, BiFunction<ItemStack, LootContext, ItemStack> {
   LootItemFunctionType<? extends LootItemFunction> getType();

   static Consumer<ItemStack> decorate(BiFunction<ItemStack, LootContext, ItemStack> var0, Consumer<ItemStack> var1, LootContext var2) {
      return (var3) -> {
         var1.accept((ItemStack)var0.apply(var3, var2));
      };
   }

   public interface Builder {
      LootItemFunction build();
   }
}
