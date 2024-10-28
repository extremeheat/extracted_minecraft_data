package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem extends LootPoolSingletonContainer {
   public static final MapCodec<EmptyLootItem> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return singletonFields(var0).apply(var0, EmptyLootItem::new);
   });

   private EmptyLootItem(int var1, int var2, List<LootItemCondition> var3, List<LootItemFunction> var4) {
      super(var1, var2, var3, var4);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.EMPTY;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
   }

   public static LootPoolSingletonContainer.Builder<?> emptyItem() {
      return simpleBuilder(EmptyLootItem::new);
   }
}
