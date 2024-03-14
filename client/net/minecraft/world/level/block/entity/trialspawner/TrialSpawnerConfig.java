package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public record TrialSpawnerConfig(
   int c, int d, float e, float f, float g, float h, int i, int j, SimpleWeightedRandomList<SpawnData> k, SimpleWeightedRandomList<ResourceLocation> l
) {
   private final int requiredPlayerRange;
   private final int spawnRange;
   private final float totalMobs;
   private final float simultaneousMobs;
   private final float totalMobsAddedPerPlayer;
   private final float simultaneousMobsAddedPerPlayer;
   private final int ticksBetweenSpawn;
   private final int targetCooldownLength;
   private final SimpleWeightedRandomList<SpawnData> spawnPotentialsDefinition;
   private final SimpleWeightedRandomList<ResourceLocation> lootTablesToEject;
   public static TrialSpawnerConfig DEFAULT = new TrialSpawnerConfig(
      14,
      4,
      6.0F,
      2.0F,
      2.0F,
      1.0F,
      40,
      36000,
      SimpleWeightedRandomList.empty(),
      SimpleWeightedRandomList.<ResourceLocation>builder()
         .add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES)
         .add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY)
         .build()
   );
   public static MapCodec<TrialSpawnerConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.intRange(1, 128).optionalFieldOf("required_player_range", DEFAULT.requiredPlayerRange).forGetter(TrialSpawnerConfig::requiredPlayerRange),
               Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
               Codec.floatRange(0.0F, 3.4028235E38F).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
               Codec.floatRange(0.0F, 3.4028235E38F)
                  .optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs)
                  .forGetter(TrialSpawnerConfig::simultaneousMobs),
               Codec.floatRange(0.0F, 3.4028235E38F)
                  .optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer)
                  .forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
               Codec.floatRange(0.0F, 3.4028235E38F)
                  .optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer)
                  .forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
               Codec.intRange(0, 2147483647).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
               Codec.intRange(0, 2147483647)
                  .optionalFieldOf("target_cooldown_length", DEFAULT.targetCooldownLength)
                  .forGetter(TrialSpawnerConfig::targetCooldownLength),
               SpawnData.LIST_CODEC
                  .optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty())
                  .forGetter(TrialSpawnerConfig::spawnPotentialsDefinition),
               SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceLocation.CODEC)
                  .optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject)
                  .forGetter(TrialSpawnerConfig::lootTablesToEject)
            )
            .apply(var0, TrialSpawnerConfig::new)
   );

   public TrialSpawnerConfig(
      int var1,
      int var2,
      float var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      SimpleWeightedRandomList<SpawnData> var9,
      SimpleWeightedRandomList<ResourceLocation> var10
   ) {
      super();
      this.requiredPlayerRange = var1;
      this.spawnRange = var2;
      this.totalMobs = var3;
      this.simultaneousMobs = var4;
      this.totalMobsAddedPerPlayer = var5;
      this.simultaneousMobsAddedPerPlayer = var6;
      this.ticksBetweenSpawn = var7;
      this.targetCooldownLength = var8;
      this.spawnPotentialsDefinition = var9;
      this.lootTablesToEject = var10;
   }

   public int calculateTargetTotalMobs(int var1) {
      return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)var1));
   }

   public int calculateTargetSimultaneousMobs(int var1) {
      return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)var1));
   }
}
