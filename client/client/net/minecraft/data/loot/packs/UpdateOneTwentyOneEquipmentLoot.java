package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class UpdateOneTwentyOneEquipmentLoot implements LootTableSubProvider {
   public UpdateOneTwentyOneEquipmentLoot() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookup(Registries.TRIM_PATTERN).orElseThrow();
      HolderLookup.RegistryLookup var4 = var1.lookup(Registries.TRIM_MATERIAL).orElseThrow();
      ArmorTrim var5 = new ArmorTrim(var4.get(TrimMaterials.COPPER).orElseThrow(), var3.get(TrimPatterns.FLOW).orElseThrow());
      ArmorTrim var6 = new ArmorTrim(var4.get(TrimMaterials.COPPER).orElseThrow(), var3.get(TrimPatterns.BOLT).orElseThrow());
      var2.accept(
         BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, var6).build()).setWeight(4))
                  .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.IRON_HELMET, Items.IRON_CHESTPLATE, var5).build()).setWeight(2))
                  .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, var5).build()).setWeight(1))
            )
      );
   }

   public static LootTable.Builder trialChamberEquipment(Item var0, Item var1, ArmorTrim var2) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(0.5F))
               .add(LootItem.lootTableItem(var0).apply(SetComponentsFunction.setComponent(DataComponents.TRIM, var2)))
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(0.5F))
               .add(LootItem.lootTableItem(var1).apply(SetComponentsFunction.setComponent(DataComponents.TRIM, var2)))
         );
   }
}
