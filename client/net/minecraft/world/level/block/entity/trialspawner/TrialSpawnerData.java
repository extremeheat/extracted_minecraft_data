package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;

public class TrialSpawnerData {
   public static final String TAG_SPAWN_DATA = "spawn_data";
   private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
   public static MapCodec<TrialSpawnerData> MAP_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               UUIDUtil.CODEC_SET.optionalFieldOf("registered_players", Sets.newHashSet()).forGetter(var0x -> var0x.detectedPlayers),
               UUIDUtil.CODEC_SET.optionalFieldOf("current_mobs", Sets.newHashSet()).forGetter(var0x -> var0x.currentMobs),
               Codec.LONG.optionalFieldOf("cooldown_ends_at", 0L).forGetter(var0x -> var0x.cooldownEndsAt),
               Codec.LONG.optionalFieldOf("next_mob_spawns_at", 0L).forGetter(var0x -> var0x.nextMobSpawnsAt),
               Codec.intRange(0, 2147483647).optionalFieldOf("total_mobs_spawned", 0).forGetter(var0x -> var0x.totalMobsSpawned),
               SpawnData.CODEC.optionalFieldOf("spawn_data").forGetter(var0x -> var0x.nextSpawnData),
               ResourceLocation.CODEC.optionalFieldOf("ejecting_loot_table").forGetter(var0x -> var0x.ejectingLootTable)
            )
            .apply(var0, TrialSpawnerData::new)
   );
   protected final Set<UUID> detectedPlayers = new HashSet<>();
   protected final Set<UUID> currentMobs = new HashSet<>();
   protected long cooldownEndsAt;
   protected long nextMobSpawnsAt;
   protected int totalMobsSpawned;
   protected Optional<SpawnData> nextSpawnData;
   protected Optional<ResourceLocation> ejectingLootTable;
   protected SimpleWeightedRandomList<SpawnData> spawnPotentials;
   @Nullable
   protected Entity displayEntity;
   protected double spin;
   protected double oSpin;

   public TrialSpawnerData() {
      this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
   }

   public TrialSpawnerData(Set<UUID> var1, Set<UUID> var2, long var3, long var5, int var7, Optional<SpawnData> var8, Optional<ResourceLocation> var9) {
      super();
      this.detectedPlayers.addAll(var1);
      this.currentMobs.addAll(var2);
      this.cooldownEndsAt = var3;
      this.nextMobSpawnsAt = var5;
      this.totalMobsSpawned = var7;
      this.nextSpawnData = var8;
      this.ejectingLootTable = var9;
   }

   public void setSpawnPotentialsFromConfig(TrialSpawnerConfig var1) {
      SimpleWeightedRandomList var2 = var1.spawnPotentialsDefinition();
      if (var2.isEmpty()) {
         this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData.orElseGet(SpawnData::new));
      } else {
         this.spawnPotentials = var2;
      }
   }

   public void reset() {
      this.detectedPlayers.clear();
      this.totalMobsSpawned = 0;
      this.nextMobSpawnsAt = 0L;
      this.cooldownEndsAt = 0L;
      this.currentMobs.clear();
   }

   public boolean hasMobToSpawn() {
      boolean var1 = this.nextSpawnData.isPresent() && this.nextSpawnData.get().getEntityToSpawn().contains("id", 8);
      return var1 || !this.spawnPotentials.isEmpty();
   }

   public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig var1, int var2) {
      return this.totalMobsSpawned >= var1.calculateTargetTotalMobs(var2);
   }

   public boolean haveAllCurrentMobsDied() {
      return this.currentMobs.isEmpty();
   }

   public boolean isReadyToSpawnNextMob(ServerLevel var1, TrialSpawnerConfig var2, int var3) {
      return var1.getGameTime() >= this.nextMobSpawnsAt && this.currentMobs.size() < var2.calculateTargetSimultaneousMobs(var3);
   }

   public int countAdditionalPlayers(BlockPos var1) {
      if (this.detectedPlayers.isEmpty()) {
         Util.logAndPauseIfInIde("Trial Spawner at " + var1 + " has no detected players");
      }

      return Math.max(0, this.detectedPlayers.size() - 1);
   }

   public void tryDetectPlayers(ServerLevel var1, BlockPos var2, PlayerDetector var3, int var4) {
      List var5 = var3.detect(var1, var2, var4);
      boolean var6 = this.detectedPlayers.addAll(var5);
      if (var6) {
         this.nextMobSpawnsAt = Math.max(var1.getGameTime() + 40L, this.nextMobSpawnsAt);
         var1.levelEvent(3013, var2, this.detectedPlayers.size());
      }
   }

   public boolean isReadyToOpenShutter(ServerLevel var1, TrialSpawnerConfig var2, float var3) {
      long var4 = this.cooldownEndsAt - (long)var2.targetCooldownLength();
      return (float)var1.getGameTime() >= (float)var4 + var3;
   }

   public boolean isReadyToEjectItems(ServerLevel var1, TrialSpawnerConfig var2, float var3) {
      long var4 = this.cooldownEndsAt - (long)var2.targetCooldownLength();
      return (float)(var1.getGameTime() - var4) % var3 == 0.0F;
   }

   public boolean isCooldownFinished(ServerLevel var1) {
      return var1.getGameTime() >= this.cooldownEndsAt;
   }

   public void setEntityId(TrialSpawner var1, RandomSource var2, EntityType<?> var3) {
      this.getOrCreateNextSpawnData(var1, var2).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(var3).toString());
   }

   protected SpawnData getOrCreateNextSpawnData(TrialSpawner var1, RandomSource var2) {
      if (this.nextSpawnData.isPresent()) {
         return this.nextSpawnData.get();
      } else {
         this.nextSpawnData = Optional.of(this.spawnPotentials.getRandom(var2).map(WeightedEntry.Wrapper::getData).orElseGet(SpawnData::new));
         var1.markUpdated();
         return this.nextSpawnData.get();
      }
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(TrialSpawner var1, Level var2, TrialSpawnerState var3) {
      if (var1.canSpawnInLevel(var2) && var3.hasSpinningMob()) {
         if (this.displayEntity == null) {
            CompoundTag var4 = this.getOrCreateNextSpawnData(var1, var2.getRandom()).getEntityToSpawn();
            if (var4.contains("id", 8)) {
               this.displayEntity = EntityType.loadEntityRecursive(var4, var2, Function.identity());
            }
         }

         return this.displayEntity;
      } else {
         return null;
      }
   }

   public CompoundTag getUpdateTag(TrialSpawnerState var1) {
      CompoundTag var2 = new CompoundTag();
      if (var1 == TrialSpawnerState.ACTIVE) {
         var2.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
      }

      this.nextSpawnData
         .ifPresent(
            var1x -> var2.put(
                  "spawn_data",
                  (Tag)SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, var1x).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
               )
         );
      return var2;
   }

   public double getSpin() {
      return this.spin;
   }

   public double getOSpin() {
      return this.oSpin;
   }
}
