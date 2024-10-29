package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition, SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject, ResourceKey<LootTable> itemsToDropWhenOminous) {
   public static final TrialSpawnerConfig DEFAULT = builder().build();
   public static final Codec<TrialSpawnerConfig> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(TrialSpawnerConfig::simultaneousMobs), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer), Codec.intRange(0, 2147483647).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn), SpawnData.LIST_CODEC.optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty()).forGetter(TrialSpawnerConfig::spawnPotentialsDefinition), SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceKey.codec(Registries.LOOT_TABLE)).optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject), ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous).forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)).apply(var0, TrialSpawnerConfig::new);
   });
   public static final Codec<Holder<TrialSpawnerConfig>> CODEC;

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

   public static Builder builder() {
      return new Builder();
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
      CODEC = RegistryFileCodec.create(Registries.TRIAL_SPAWNER_CONFIG, DIRECT_CODEC);
   }

   public static class Builder {
      private int spawnRange = 4;
      private float totalMobs = 6.0F;
      private float simultaneousMobs = 2.0F;
      private float totalMobsAddedPerPlayer = 2.0F;
      private float simultaneousMobsAddedPerPlayer = 1.0F;
      private int ticksBetweenSpawn = 40;
      private SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition = SimpleWeightedRandomList.empty();
      private SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject;
      private ResourceKey<LootTable> itemsToDropWhenOminous;

      public Builder() {
         super();
         this.lootTablesToEject = SimpleWeightedRandomList.builder().add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES).add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY).build();
         this.itemsToDropWhenOminous = BuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS;
      }

      public Builder spawnRange(int var1) {
         this.spawnRange = var1;
         return this;
      }

      public Builder totalMobs(float var1) {
         this.totalMobs = var1;
         return this;
      }

      public Builder simultaneousMobs(float var1) {
         this.simultaneousMobs = var1;
         return this;
      }

      public Builder totalMobsAddedPerPlayer(float var1) {
         this.totalMobsAddedPerPlayer = var1;
         return this;
      }

      public Builder simultaneousMobsAddedPerPlayer(float var1) {
         this.simultaneousMobsAddedPerPlayer = var1;
         return this;
      }

      public Builder ticksBetweenSpawn(int var1) {
         this.ticksBetweenSpawn = var1;
         return this;
      }

      public Builder spawnPotentialsDefinition(SimpleWeightedRandomList<SpawnData> var1) {
         this.spawnPotentialsDefinition = var1;
         return this;
      }

      public Builder lootTablesToEject(SimpleWeightedRandomList<ResourceKey<LootTable>> var1) {
         this.lootTablesToEject = var1;
         return this;
      }

      public Builder itemsToDropWhenOminous(ResourceKey<LootTable> var1) {
         this.itemsToDropWhenOminous = var1;
         return this;
      }

      public TrialSpawnerConfig build() {
         return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, this.spawnPotentialsDefinition, this.lootTablesToEject, this.itemsToDropWhenOminous);
      }
   }
}
