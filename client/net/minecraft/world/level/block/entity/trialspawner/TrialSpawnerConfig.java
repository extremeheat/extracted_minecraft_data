package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, WeightedList<SpawnData> spawnPotentialsDefinition, WeightedList<ResourceKey<LootTable>> lootTablesToEject, ResourceKey<LootTable> itemsToDropWhenOminous) {
   public static final TrialSpawnerConfig DEFAULT = builder().build();
   public static final Codec<TrialSpawnerConfig> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(TrialSpawnerConfig::simultaneousMobs), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer), Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer), Codec.intRange(0, 2147483647).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn), SpawnData.LIST_CODEC.optionalFieldOf("spawn_potentials", WeightedList.of()).forGetter(TrialSpawnerConfig::spawnPotentialsDefinition), WeightedList.codec(LootTable.KEY_CODEC).optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject), LootTable.KEY_CODEC.optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous).forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)).apply(i, TrialSpawnerConfig::new));
   public static final Codec<Holder<TrialSpawnerConfig>> CODEC;

   public TrialSpawnerConfig {
      super();
   }

   public int calculateTargetTotalMobs(final int additionalPlayers) {
      return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers));
   }

   public int calculateTargetSimultaneousMobs(final int additionalPlayers) {
      return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers));
   }

   public long ticksBetweenItemSpawners() {
      return 160L;
   }

   public static Builder builder() {
      return new Builder();
   }

   public TrialSpawnerConfig withSpawning(final EntityType<?> type) {
      CompoundTag tag = new CompoundTag();
      tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
      SpawnData spawnData = new SpawnData(tag, Optional.empty(), Optional.empty());
      return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, WeightedList.of(spawnData), this.lootTablesToEject, this.itemsToDropWhenOminous);
   }

   public TrialSpawnerConfig withSpawning(final TypedEntityData<EntityType<?>> entityData) {
      CompoundTag tag = new CompoundTag();
      SpawnData spawnData = new SpawnData(tag, Optional.empty(), Optional.empty());
      entityData.loadInto(spawnData, BuiltInRegistries.ENTITY_TYPE);
      return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, WeightedList.of(spawnData), this.lootTablesToEject, this.itemsToDropWhenOminous);
   }

   static {
      CODEC = RegistryFileCodec.<Holder<TrialSpawnerConfig>>create(Registries.TRIAL_SPAWNER_CONFIG, DIRECT_CODEC);
   }

   public static class Builder {
      private int spawnRange = 4;
      private float totalMobs = 6.0F;
      private float simultaneousMobs = 2.0F;
      private float totalMobsAddedPerPlayer = 2.0F;
      private float simultaneousMobsAddedPerPlayer = 1.0F;
      private int ticksBetweenSpawn = 40;
      private WeightedList<SpawnData> spawnPotentialsDefinition = WeightedList.<SpawnData>of();
      private WeightedList<ResourceKey<LootTable>> lootTablesToEject;
      private ResourceKey<LootTable> itemsToDropWhenOminous;

      public Builder() {
         super();
         this.lootTablesToEject = WeightedList.<ResourceKey<LootTable>>builder().add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES).add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY).build();
         this.itemsToDropWhenOminous = BuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS;
      }

      public Builder spawnRange(final int spawnRange) {
         this.spawnRange = spawnRange;
         return this;
      }

      public Builder totalMobs(final float totalMobs) {
         this.totalMobs = totalMobs;
         return this;
      }

      public Builder simultaneousMobs(final float simultaneousMobs) {
         this.simultaneousMobs = simultaneousMobs;
         return this;
      }

      public Builder totalMobsAddedPerPlayer(final float totalMobsAddedPerPlayer) {
         this.totalMobsAddedPerPlayer = totalMobsAddedPerPlayer;
         return this;
      }

      public Builder simultaneousMobsAddedPerPlayer(final float simultaneousMobsAddedPerPlayer) {
         this.simultaneousMobsAddedPerPlayer = simultaneousMobsAddedPerPlayer;
         return this;
      }

      public Builder ticksBetweenSpawn(final int ticksBetweenSpawn) {
         this.ticksBetweenSpawn = ticksBetweenSpawn;
         return this;
      }

      public Builder spawnPotentialsDefinition(final WeightedList<SpawnData> spawnPotentialsDefinition) {
         this.spawnPotentialsDefinition = spawnPotentialsDefinition;
         return this;
      }

      public Builder lootTablesToEject(final WeightedList<ResourceKey<LootTable>> lootTablesToEject) {
         this.lootTablesToEject = lootTablesToEject;
         return this;
      }

      public Builder itemsToDropWhenOminous(final ResourceKey<LootTable> itemsToDropWhenOminous) {
         this.itemsToDropWhenOminous = itemsToDropWhenOminous;
         return this;
      }

      public TrialSpawnerConfig build() {
         return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, this.spawnPotentialsDefinition, this.lootTablesToEject, this.itemsToDropWhenOminous);
      }
   }
}
