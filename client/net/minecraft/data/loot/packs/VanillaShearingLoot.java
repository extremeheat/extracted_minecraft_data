package net.minecraft.data.loot.packs;

import java.util.Objects;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class VanillaShearingLoot implements LootTableSubProvider {
   private final LootTableSubProvider.Context output;
   private final HolderGetter<LootTable> lootTables;

   public VanillaShearingLoot(final LootTableSubProvider.Context output) {
      super();
      this.output = output;
      this.lootTables = output.lookup(Registries.LOOT_TABLE);
   }

   public void run() {
      this.output.accept(BuiltInLootTables.BOGGED_SHEAR, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(2.0F)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem(Items.BROWN_MUSHROOM).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem(Items.RED_MUSHROOM).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))));
      ColorCollection.zipApply(BuiltInLootTables.SHEAR_DYED_SHEEP, Blocks.WOOL, (dyedSheep, wool) -> this.output.accept(dyedSheep, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F)).add(LootItem.lootTableItem(wool)))));
      LootTableSubProvider.Context var10000 = this.output;
      ResourceKey var10001 = BuiltInLootTables.SHEAR_SHEEP;
      LootTable.Builder var10002 = LootTable.lootTable();
      ColorCollection var10003 = BuiltInLootTables.SHEAR_DYED_SHEEP;
      HolderGetter var10004 = this.lootTables;
      Objects.requireNonNull(var10004);
      var10000.accept(var10001, var10002.withPool(EntityLootSubProvider.createSheepDispatchPool(var10003.map(var10004::getOrThrow))));
      Holder.Reference<LootTable> shearRedMooshroom = this.output.accept(BuiltInLootTables.SHEAR_RED_MOOSHROOM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(5.0F)).add(LootItem.lootTableItem(Items.RED_MUSHROOM))));
      Holder.Reference<LootTable> sheadBrownMooshroom = this.output.accept(BuiltInLootTables.SHEAR_BROWN_MOOSHROOM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(5.0F)).add(LootItem.lootTableItem(Items.BROWN_MUSHROOM))));
      this.output.accept(BuiltInLootTables.SHEAR_MOOSHROOM, LootTable.lootTable().withPool(LootPool.lootPool().add(AlternativesEntry.alternatives((LootPoolEntryContainer.Builder)NestedLootTable.lootTableReference(shearRedMooshroom).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().components(DataComponentExactPredicate.expect(DataComponents.MOOSHROOM_VARIANT, MushroomCow.Variant.RED)))), (LootPoolEntryContainer.Builder)NestedLootTable.lootTableReference(sheadBrownMooshroom).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().components(DataComponentExactPredicate.expect(DataComponents.MOOSHROOM_VARIANT, MushroomCow.Variant.BROWN))))))));
      this.output.accept(BuiltInLootTables.SHEAR_SNOW_GOLEM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.CARVED_PUMPKIN))));
   }
}
