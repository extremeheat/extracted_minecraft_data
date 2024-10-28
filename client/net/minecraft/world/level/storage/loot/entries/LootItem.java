package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem extends LootPoolSingletonContainer {
   public static final MapCodec<LootItem> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("name").forGetter((var0x) -> {
         return var0x.item;
      })).and(singletonFields(var0)).apply(var0, LootItem::new);
   });
   private final Holder<Item> item;

   private LootItem(Holder<Item> var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.item = var1;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.ITEM;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      var1.accept(new ItemStack(this.item));
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new LootItem(var0.asItem().builtInRegistryHolder(), var1, var2, var3, var4);
      });
   }
}
