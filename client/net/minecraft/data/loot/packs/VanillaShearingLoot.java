package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public record VanillaShearingLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
   public VanillaShearingLoot(HolderLookup.Provider registries) {
      super();
      this.registries = registries;
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var1) {
      var1.accept(BuiltInLootTables.BOGGED_SHEAR, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2.0F)).add(LootItem.lootTableItem(Items.BROWN_MUSHROOM).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))).add(LootItem.lootTableItem(Items.RED_MUSHROOM).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))));
   }

   public HolderLookup.Provider registries() {
      return this.registries;
   }
}
