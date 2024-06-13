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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public record VanillaEquipmentLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
   public VanillaEquipmentLoot(HolderLookup.Provider registries) {
      super();
      this.registries = registries;
   }

   @Override
   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookup(Registries.TRIM_PATTERN).orElseThrow();
      HolderLookup.RegistryLookup var3 = this.registries.lookup(Registries.TRIM_MATERIAL).orElseThrow();
      HolderLookup.RegistryLookup var4 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      ArmorTrim var5 = new ArmorTrim(var3.get(TrimMaterials.COPPER).orElseThrow(), var2.get(TrimPatterns.FLOW).orElseThrow());
      ArmorTrim var6 = new ArmorTrim(var3.get(TrimMaterials.COPPER).orElseThrow(), var2.get(TrimPatterns.BOLT).orElseThrow());
      var1.accept(
         BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     NestedLootTable.inlineLootTable(trialChamberEquipment(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, var6, var4).build())
                        .setWeight(4)
                  )
                  .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.IRON_HELMET, Items.IRON_CHESTPLATE, var5, var4).build()).setWeight(2))
                  .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, var5, var4).build()).setWeight(1))
            )
      );
      var1.accept(
         BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.IRON_SWORD).setWeight(4))
                  .add(
                     LootItem.lootTableItem(Items.IRON_SWORD)
                        .apply(new SetEnchantmentsFunction.Builder().withEnchantment(var4.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(1.0F)))
                  )
                  .add(
                     LootItem.lootTableItem(Items.IRON_SWORD)
                        .apply(new SetEnchantmentsFunction.Builder().withEnchantment(var4.getOrThrow(Enchantments.KNOCKBACK), ConstantValue.exactly(1.0F)))
                  )
                  .add(LootItem.lootTableItem(Items.DIAMOND_SWORD))
            )
      );
      var1.accept(
         BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.BOW).setWeight(2))
                  .add(
                     LootItem.lootTableItem(Items.BOW)
                        .apply(new SetEnchantmentsFunction.Builder().withEnchantment(var4.getOrThrow(Enchantments.POWER), ConstantValue.exactly(1.0F)))
                  )
                  .add(
                     LootItem.lootTableItem(Items.BOW)
                        .apply(new SetEnchantmentsFunction.Builder().withEnchantment(var4.getOrThrow(Enchantments.PUNCH), ConstantValue.exactly(1.0F)))
                  )
            )
      );
   }

   public static LootTable.Builder trialChamberEquipment(Item var0, Item var1, ArmorTrim var2, HolderLookup.RegistryLookup<Enchantment> var3) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(0.5F))
               .add(
                  LootItem.lootTableItem(var0)
                     .apply(SetComponentsFunction.setComponent(DataComponents.TRIM, var2))
                     .apply(
                        new SetEnchantmentsFunction.Builder()
                           .withEnchantment(var3.getOrThrow(Enchantments.PROTECTION), ConstantValue.exactly(4.0F))
                           .withEnchantment(var3.getOrThrow(Enchantments.PROJECTILE_PROTECTION), ConstantValue.exactly(4.0F))
                           .withEnchantment(var3.getOrThrow(Enchantments.FIRE_PROTECTION), ConstantValue.exactly(4.0F))
                     )
               )
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(LootItemRandomChanceCondition.randomChance(0.5F))
               .add(
                  LootItem.lootTableItem(var1)
                     .apply(SetComponentsFunction.setComponent(DataComponents.TRIM, var2))
                     .apply(
                        new SetEnchantmentsFunction.Builder()
                           .withEnchantment(var3.getOrThrow(Enchantments.PROTECTION), ConstantValue.exactly(4.0F))
                           .withEnchantment(var3.getOrThrow(Enchantments.PROJECTILE_PROTECTION), ConstantValue.exactly(4.0F))
                           .withEnchantment(var3.getOrThrow(Enchantments.FIRE_PROTECTION), ConstantValue.exactly(4.0F))
                     )
               )
         );
   }
}
