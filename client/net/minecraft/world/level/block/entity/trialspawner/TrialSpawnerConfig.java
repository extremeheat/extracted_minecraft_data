package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition, SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject, ResourceKey<LootTable> itemsToDropWhenOminous) {
   public static final TrialSpawnerConfig DEFAULT;
   public static final Codec<TrialSpawnerConfig> CODEC;

   public TrialSpawnerConfig(int var1, float var2, float var3, float var4, float var5, int var6, SimpleWeightedRandomList<SpawnData> var7, SimpleWeightedRandomList<ResourceKey<LootTable>> var8, ResourceKey<LootTable> var9) {
      super();
      this.spawnRange = var1;
      this.totalMobs = var2;
      this.simultaneousMobs = var3;
      this.totalMobsAddedPerPlayer = var4;
      this.simultaneousMobsAddedPerPlayer = var5;
      this.ticksBetweenSpawn = var6;
      this.spawnPotentialsDefinition = var7;
      this.lootTablesToEject = var8;
      this.itemsToDropWhenOminous = var9;
   }

   public int calculateTargetTotalMobs(int var1) {
      return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)var1));
   }

   public int calculateTargetSimultaneousMobs(int var1) {
      return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)var1));
   }

   public long ticksBetweenItemSpawners() {
      return 160L;
   }

   public int spawnRange() {
      return this.spawnRange;
   }

   public float totalMobs() {
      return this.totalMobs;
   }

   public float simultaneousMobs() {
      return this.simultaneousMobs;
   }

   public float totalMobsAddedPerPlayer() {
      return this.totalMobsAddedPerPlayer;
   }

   public float simultaneousMobsAddedPerPlayer() {
      return this.simultaneousMobsAddedPerPlayer;
   }

   public int ticksBetweenSpawn() {
      return this.ticksBetweenSpawn;
   }

   public SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition() {
      return this.spawnPotentialsDefinition;
   }

   public SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject() {
      return this.lootTablesToEject;
   }

   public ResourceKey<LootTable> itemsToDropWhenOminous() {
      return this.itemsToDropWhenOminous;
   }

   static {
      DEFAULT = new TrialSpawnerConfig(4, 6.0F, 2.0F, 2.0F, 1.0F, 40, SimpleWeightedRandomList.empty(), SimpleWeightedRandomList.builder().add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES).add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY).build(), BuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.intRange(1, 128).lenientOptionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange), Codec.floatRange(0.0F, 3.4028235E38F).lenientOptionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs), Codec.floatRange(0.0F, 3.4028235E38F).lenientOptionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(TrialSpawnerConfig::simultaneousMobs), Codec.floatRange(0.0F, 3.4028235E38F).lenientOptionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer), Codec.floatRange(0.0F, 3.4028235E38F).lenientOptionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer), Codec.intRange(0, 2147483647).lenientOptionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn), SpawnData.LIST_CODEC.lenientOptionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty()).forGetter(TrialSpawnerConfig::spawnPotentialsDefinition), SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceKey.codec(Registries.LOOT_TABLE)).lenientOptionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject), ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous).forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)).apply(var0, TrialSpawnerConfig::new);
      });
   }
}
