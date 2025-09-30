package net.minecraft.data.loot.packs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public record VanillaChargedCreeperExplosionLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
   private static final List<Entry> ENTRIES;

   public VanillaChargedCreeperExplosionLoot(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.ENTITY_TYPE);
      ArrayList var3 = new ArrayList(ENTRIES.size());

      for(Entry var5 : ENTRIES) {
         var1.accept(var5.lootTable, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var5.item))));
         LootItemCondition.Builder var6 = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(var2, var5.entityType)));
         var3.add(NestedLootTable.lootTableReference(var5.lootTable).when(var6));
      }

      var1.accept(BuiltInLootTables.CHARGED_CREEPER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(AlternativesEntry.alternatives((LootPoolEntryContainer.Builder[])var3.toArray((var0) -> new LootPoolEntryContainer.Builder[var0])))));
   }

   static {
      ENTRIES = List.of(new Entry(BuiltInLootTables.CHARGED_CREEPER_PIGLIN, EntityType.PIGLIN, Items.PIGLIN_HEAD), new Entry(BuiltInLootTables.CHARGED_CREEPER_CREEPER, EntityType.CREEPER, Items.CREEPER_HEAD), new Entry(BuiltInLootTables.CHARGED_CREEPER_SKELETON, EntityType.SKELETON, Items.SKELETON_SKULL), new Entry(BuiltInLootTables.CHARGED_CREEPER_WITHER_SKELETON, EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL), new Entry(BuiltInLootTables.CHARGED_CREEPER_ZOMBIE, EntityType.ZOMBIE, Items.ZOMBIE_HEAD));
   }

   static record Entry(ResourceKey<LootTable> lootTable, EntityType<?> entityType, Item item) {
      final ResourceKey<LootTable> lootTable;
      final EntityType<?> entityType;
      final Item item;

      Entry(ResourceKey<LootTable> var1, EntityType<?> var2, Item var3) {
         super();
         this.lootTable = var1;
         this.entityType = var2;
         this.item = var3;
      }
   }
}
