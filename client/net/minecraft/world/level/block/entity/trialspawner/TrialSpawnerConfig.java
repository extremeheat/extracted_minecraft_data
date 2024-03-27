package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record TrialSpawnerConfig(
   int c,
   float d,
   float e,
   float f,
   float g,
   int h,
   SimpleWeightedRandomList<SpawnData> i,
   SimpleWeightedRandomList<ResourceKey<LootTable>> j,
   ResourceKey<LootTable> k
) {
   private final int spawnRange;
   private final float totalMobs;
   private final float simultaneousMobs;
   private final float totalMobsAddedPerPlayer;
   private final float simultaneousMobsAddedPerPlayer;
   private final int ticksBetweenSpawn;
   private final SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition;
   private final SimpleWeightedRandomList<ResourceKey<LootTable>> lootTablesToEject;
   private final ResourceKey<LootTable> itemsToDropWhenOminous;
   public static final TrialSpawnerConfig DEFAULT = new TrialSpawnerConfig(
      4,
      6.0F,
      2.0F,
      2.0F,
      1.0F,
      40,
      SimpleWeightedRandomList.empty(),
      SimpleWeightedRandomList.<ResourceKey<LootTable>>builder()
         .add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES)
         .add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY)
         .build(),
      BuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS
   );
   public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.intRange(1, 128).lenientOptionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
               Codec.floatRange(0.0F, 3.4028235E38F).lenientOptionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
               Codec.floatRange(0.0F, 3.4028235E38F)
                  .lenientOptionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs)
                  .forGetter(TrialSpawnerConfig::simultaneousMobs),
               Codec.floatRange(0.0F, 3.4028235E38F)
                  .lenientOptionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
                  .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
               Codec.floatRange(0.0F, 3.4028235E38F)
                  .lenientOptionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
                  .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
               Codec.intRange(0, 2147483647)
                  .lenientOptionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn)
                  .forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
               SpawnData.LIST_CODEC
                  .lenientOptionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty())
                  .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
               SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceKey.codec(Registries.LOOT_TABLE))
                  .lenientOptionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject)
                  .forGetter(TrialSpawnerConfig::lootTablesToEject),
               ResourceKey.codec(Registries.LOOT_TABLE)
                  .lenientOptionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous)
                  .forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)
            )
            .apply(var0, TrialSpawnerConfig::new)
   );

   public TrialSpawnerConfig(
      int var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      SimpleWeightedRandomList<SpawnData> var7,
      SimpleWeightedRandomList<ResourceKey<LootTable>> var8,
      ResourceKey<LootTable> var9
   ) {
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
}
