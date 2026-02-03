package net.minecraft.world.level.block.entity.trialspawner;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

public class TrialSpawnerConfigs {
   private static final Keys TRIAL_CHAMBER_BREEZE = TrialSpawnerConfigs.Keys.of("trial_chamber/breeze");
   private static final Keys TRIAL_CHAMBER_MELEE_HUSK = TrialSpawnerConfigs.Keys.of("trial_chamber/melee/husk");
   private static final Keys TRIAL_CHAMBER_MELEE_SPIDER = TrialSpawnerConfigs.Keys.of("trial_chamber/melee/spider");
   private static final Keys TRIAL_CHAMBER_MELEE_ZOMBIE = TrialSpawnerConfigs.Keys.of("trial_chamber/melee/zombie");
   private static final Keys TRIAL_CHAMBER_RANGED_POISON_SKELETON = TrialSpawnerConfigs.Keys.of("trial_chamber/ranged/poison_skeleton");
   private static final Keys TRIAL_CHAMBER_RANGED_SKELETON = TrialSpawnerConfigs.Keys.of("trial_chamber/ranged/skeleton");
   private static final Keys TRIAL_CHAMBER_RANGED_STRAY = TrialSpawnerConfigs.Keys.of("trial_chamber/ranged/stray");
   private static final Keys TRIAL_CHAMBER_SLOW_RANGED_POISON_SKELETON = TrialSpawnerConfigs.Keys.of("trial_chamber/slow_ranged/poison_skeleton");
   private static final Keys TRIAL_CHAMBER_SLOW_RANGED_SKELETON = TrialSpawnerConfigs.Keys.of("trial_chamber/slow_ranged/skeleton");
   private static final Keys TRIAL_CHAMBER_SLOW_RANGED_STRAY = TrialSpawnerConfigs.Keys.of("trial_chamber/slow_ranged/stray");
   private static final Keys TRIAL_CHAMBER_SMALL_MELEE_BABY_ZOMBIE = TrialSpawnerConfigs.Keys.of("trial_chamber/small_melee/baby_zombie");
   private static final Keys TRIAL_CHAMBER_SMALL_MELEE_CAVE_SPIDER = TrialSpawnerConfigs.Keys.of("trial_chamber/small_melee/cave_spider");
   private static final Keys TRIAL_CHAMBER_SMALL_MELEE_SILVERFISH = TrialSpawnerConfigs.Keys.of("trial_chamber/small_melee/silverfish");
   private static final Keys TRIAL_CHAMBER_SMALL_MELEE_SLIME = TrialSpawnerConfigs.Keys.of("trial_chamber/small_melee/slime");

   public TrialSpawnerConfigs() {
      super();
   }

   public static void bootstrap(final BootstrapContext<TrialSpawnerConfig> context) {
      register(context, TRIAL_CHAMBER_BREEZE, TrialSpawnerConfig.builder().simultaneousMobs(1.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(2.0F).totalMobsAddedPerPlayer(1.0F).spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.BREEZE))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(4.0F).totalMobsAddedPerPlayer(1.0F).spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.BREEZE))).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).build());
      register(context, TRIAL_CHAMBER_MELEE_HUSK, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.HUSK))).build(), trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.HUSK, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE))).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).build());
      register(context, TRIAL_CHAMBER_MELEE_SPIDER, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.SPIDER))).build(), trialChamberMeleeOminous().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.SPIDER))).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).build());
      register(context, TRIAL_CHAMBER_MELEE_ZOMBIE, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.ZOMBIE))).build(), trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.ZOMBIE, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE))).build());
      register(context, TRIAL_CHAMBER_RANGED_POISON_SKELETON, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.BOGGED))).build(), trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.BOGGED, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
      register(context, TRIAL_CHAMBER_RANGED_SKELETON, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.SKELETON))).build(), trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.SKELETON, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
      register(context, TRIAL_CHAMBER_RANGED_STRAY, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.STRAY))).build(), trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.STRAY, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
      register(context, TRIAL_CHAMBER_SLOW_RANGED_POISON_SKELETON, trialChamberSlowRanged().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.BOGGED))).build(), trialChamberSlowRanged().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.BOGGED, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
      register(context, TRIAL_CHAMBER_SLOW_RANGED_SKELETON, trialChamberSlowRanged().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.SKELETON))).build(), trialChamberSlowRanged().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.SKELETON, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
      register(context, TRIAL_CHAMBER_SLOW_RANGED_STRAY, trialChamberSlowRanged().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.STRAY))).build(), trialChamberSlowRanged().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnDataWithEquipment(EntityType.STRAY, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
      register(context, TRIAL_CHAMBER_SMALL_MELEE_BABY_ZOMBIE, TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).spawnPotentialsDefinition(WeightedList.of(customSpawnDataWithEquipment(EntityType.ZOMBIE, (tag) -> tag.putBoolean("IsBaby", true), (ResourceKey)null))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(customSpawnDataWithEquipment(EntityType.ZOMBIE, (tag) -> tag.putBoolean("IsBaby", true), BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE))).build());
      register(context, TRIAL_CHAMBER_SMALL_MELEE_CAVE_SPIDER, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.CAVE_SPIDER))).build(), trialChamberMeleeOminous().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.CAVE_SPIDER))).build());
      register(context, TRIAL_CHAMBER_SMALL_MELEE_SILVERFISH, trialChamberBase().spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.SILVERFISH))).build(), trialChamberMeleeOminous().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(spawnData(EntityType.SILVERFISH))).build());
      register(context, TRIAL_CHAMBER_SMALL_MELEE_SLIME, trialChamberBase().spawnPotentialsDefinition(WeightedList.builder().add(customSpawnData(EntityType.SLIME, (tag) -> tag.putByte("Size", (byte)1)), 3).add(customSpawnData(EntityType.SLIME, (tag) -> tag.putByte("Size", (byte)2)), 1).build()).build(), trialChamberMeleeOminous().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.builder().add(customSpawnData(EntityType.SLIME, (tag) -> tag.putByte("Size", (byte)1)), 3).add(customSpawnData(EntityType.SLIME, (tag) -> tag.putByte("Size", (byte)2)), 1).build()).build());
   }

   private static <T extends Entity> SpawnData spawnData(final EntityType<T> type) {
      return customSpawnDataWithEquipment(type, (tag) -> {
      }, (ResourceKey)null);
   }

   private static <T extends Entity> SpawnData customSpawnData(final EntityType<T> type, final Consumer<CompoundTag> tagModifier) {
      return customSpawnDataWithEquipment(type, tagModifier, (ResourceKey)null);
   }

   private static <T extends Entity> SpawnData spawnDataWithEquipment(final EntityType<T> type, final ResourceKey<LootTable> equipmentLootTable) {
      return customSpawnDataWithEquipment(type, (tag) -> {
      }, equipmentLootTable);
   }

   private static <T extends Entity> SpawnData customSpawnDataWithEquipment(final EntityType<T> type, final Consumer<CompoundTag> tagModifier, final @Nullable ResourceKey<LootTable> equipmentLootTable) {
      CompoundTag tag = new CompoundTag();
      tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
      tagModifier.accept(tag);
      Optional<EquipmentTable> table = Optional.ofNullable(equipmentLootTable).map((lootTable) -> new EquipmentTable(lootTable, 0.0F));
      return new SpawnData(tag, Optional.empty(), table);
   }

   private static void register(final BootstrapContext<TrialSpawnerConfig> context, final Keys keys, final TrialSpawnerConfig normalConfig, final TrialSpawnerConfig ominousConfig) {
      context.register(keys.normal, normalConfig);
      context.register(keys.ominous, ominousConfig);
   }

   private static ResourceKey<TrialSpawnerConfig> registryKey(final String id) {
      return ResourceKey.create(Registries.TRIAL_SPAWNER_CONFIG, Identifier.withDefaultNamespace(id));
   }

   private static TrialSpawnerConfig.Builder trialChamberMeleeOminous() {
      return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(12.0F);
   }

   private static TrialSpawnerConfig.Builder trialChamberSlowRanged() {
      return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(2.0F).ticksBetweenSpawn(160);
   }

   private static TrialSpawnerConfig.Builder trialChamberBase() {
      return TrialSpawnerConfig.builder().simultaneousMobs(3.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20);
   }

   private static record Keys(ResourceKey<TrialSpawnerConfig> normal, ResourceKey<TrialSpawnerConfig> ominous) {
      private Keys {
         super();
      }

      public static Keys of(final String id) {
         return new Keys(TrialSpawnerConfigs.registryKey(id + "/normal"), TrialSpawnerConfigs.registryKey(id + "/ominous"));
      }
   }
}
