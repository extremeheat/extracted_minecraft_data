package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TrialSpawnerData {
   public static final String TAG_SPAWN_DATA = "spawn_data";
   private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
   private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;
   private static final int TRIAL_OMEN_PER_BAD_OMEN_LEVEL = 18000;
   public static MapCodec<TrialSpawnerData> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(UUIDUtil.CODEC_SET.lenientOptionalFieldOf("registered_players", Sets.newHashSet()).forGetter((var0x) -> {
         return var0x.detectedPlayers;
      }), UUIDUtil.CODEC_SET.lenientOptionalFieldOf("current_mobs", Sets.newHashSet()).forGetter((var0x) -> {
         return var0x.currentMobs;
      }), Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", 0L).forGetter((var0x) -> {
         return var0x.cooldownEndsAt;
      }), Codec.LONG.lenientOptionalFieldOf("next_mob_spawns_at", 0L).forGetter((var0x) -> {
         return var0x.nextMobSpawnsAt;
      }), Codec.intRange(0, 2147483647).lenientOptionalFieldOf("total_mobs_spawned", 0).forGetter((var0x) -> {
         return var0x.totalMobsSpawned;
      }), SpawnData.CODEC.lenientOptionalFieldOf("spawn_data").forGetter((var0x) -> {
         return var0x.nextSpawnData;
      }), ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("ejecting_loot_table").forGetter((var0x) -> {
         return var0x.ejectingLootTable;
      })).apply(var0, TrialSpawnerData::new);
   });
   protected final Set<UUID> detectedPlayers;
   protected final Set<UUID> currentMobs;
   protected long cooldownEndsAt;
   protected long nextMobSpawnsAt;
   protected int totalMobsSpawned;
   protected Optional<SpawnData> nextSpawnData;
   protected Optional<ResourceKey<LootTable>> ejectingLootTable;
   @Nullable
   protected Entity displayEntity;
   @Nullable
   private SimpleWeightedRandomList<ItemStack> dispensing;
   protected double spin;
   protected double oSpin;

   public TrialSpawnerData() {
      this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
   }

   public TrialSpawnerData(Set<UUID> var1, Set<UUID> var2, long var3, long var5, int var7, Optional<SpawnData> var8, Optional<ResourceKey<LootTable>> var9) {
      super();
      this.detectedPlayers = new HashSet();
      this.currentMobs = new HashSet();
      this.detectedPlayers.addAll(var1);
      this.currentMobs.addAll(var2);
      this.cooldownEndsAt = var3;
      this.nextMobSpawnsAt = var5;
      this.totalMobsSpawned = var7;
      this.nextSpawnData = var8;
      this.ejectingLootTable = var9;
   }

   public void reset() {
      this.detectedPlayers.clear();
      this.totalMobsSpawned = 0;
      this.nextMobSpawnsAt = 0L;
      this.cooldownEndsAt = 0L;
      this.currentMobs.clear();
   }

   public boolean hasMobToSpawn(TrialSpawner var1, RandomSource var2) {
      boolean var3 = this.getOrCreateNextSpawnData(var1, var2).getEntityToSpawn().contains("id", 8);
      return var3 || !var1.getConfig().spawnPotentialsDefinition().isEmpty();
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
         Util.logAndPauseIfInIde("Trial Spawner at " + String.valueOf(var1) + " has no detected players");
      }

      return Math.max(0, this.detectedPlayers.size() - 1);
   }

   public void tryDetectPlayers(ServerLevel var1, BlockPos var2, TrialSpawner var3) {
      boolean var4 = (var2.asLong() + var1.getGameTime()) % 20L != 0L;
      if (!var4) {
         if (!var3.getState().equals(TrialSpawnerState.COOLDOWN) || !var3.isOminous()) {
            List var5 = var3.getPlayerDetector().detect(var1, var3.getEntitySelector(), var2, (double)var3.getRequiredPlayerRange(), true);
            Player var6 = null;
            Iterator var7 = var5.iterator();

            while(var7.hasNext()) {
               UUID var8 = (UUID)var7.next();
               Player var9 = var1.getPlayerByUUID(var8);
               if (var9 != null) {
                  if (var9.hasEffect(MobEffects.BAD_OMEN)) {
                     this.transformBadOmenIntoTrialOmen(var9, var9.getEffect(MobEffects.BAD_OMEN));
                     var6 = var9;
                  } else if (var9.hasEffect(MobEffects.TRIAL_OMEN)) {
                     var6 = var9;
                  }
               }
            }

            boolean var11 = !var3.isOminous() && var6 != null;
            if (!var3.getState().equals(TrialSpawnerState.COOLDOWN) || var11) {
               if (var11) {
                  var1.levelEvent(3020, BlockPos.containing(var6.getEyePosition()), 0);
                  var3.applyOminous(var1, var2);
               }

               boolean var12 = var3.getData().detectedPlayers.isEmpty();
               List var13 = var12 ? var5 : var3.getPlayerDetector().detect(var1, var3.getEntitySelector(), var2, (double)var3.getRequiredPlayerRange(), false);
               if (this.detectedPlayers.addAll(var13)) {
                  this.nextMobSpawnsAt = Math.max(var1.getGameTime() + 40L, this.nextMobSpawnsAt);
                  if (!var11) {
                     int var10 = var3.isOminous() ? 3019 : 3013;
                     var1.levelEvent(var10, var2, this.detectedPlayers.size());
                  }
               }

            }
         }
      }
   }

   public void resetAfterBecomingOminous(TrialSpawner var1, ServerLevel var2) {
      Stream var10000 = this.currentMobs.stream();
      Objects.requireNonNull(var2);
      var10000.map(var2::getEntity).forEach((var1x) -> {
         if (var1x != null) {
            var2.levelEvent(3012, var1x.blockPosition(), TrialSpawner.FlameParticle.NORMAL.encode());
            var1x.remove(Entity.RemovalReason.DISCARDED);
         }
      });
      if (!var1.getOminousConfig().spawnPotentialsDefinition().isEmpty()) {
         this.nextSpawnData = Optional.empty();
      }

      this.totalMobsSpawned = 0;
      this.currentMobs.clear();
      this.nextMobSpawnsAt = var2.getGameTime() + (long)var1.getOminousConfig().ticksBetweenSpawn();
      var1.markUpdated();
      this.cooldownEndsAt = var2.getGameTime() + var1.getOminousConfig().ticksBetweenItemSpawners();
   }

   private void transformBadOmenIntoTrialOmen(Player var1, MobEffectInstance var2) {
      int var3 = var2.getAmplifier() + 1;
      int var4 = 18000 * var3;
      var1.removeEffect(MobEffects.BAD_OMEN);
      var1.addEffect(new MobEffectInstance(MobEffects.TRIAL_OMEN, var4, 0));
   }

   public boolean isReadyToOpenShutter(ServerLevel var1, float var2, int var3) {
      long var4 = this.cooldownEndsAt - (long)var3;
      return (float)var1.getGameTime() >= (float)var4 + var2;
   }

   public boolean isReadyToEjectItems(ServerLevel var1, float var2, int var3) {
      long var4 = this.cooldownEndsAt - (long)var3;
      return (float)(var1.getGameTime() - var4) % var2 == 0.0F;
   }

   public boolean isCooldownFinished(ServerLevel var1) {
      return var1.getGameTime() >= this.cooldownEndsAt;
   }

   public void setEntityId(TrialSpawner var1, RandomSource var2, EntityType<?> var3) {
      this.getOrCreateNextSpawnData(var1, var2).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(var3).toString());
   }

   protected SpawnData getOrCreateNextSpawnData(TrialSpawner var1, RandomSource var2) {
      if (this.nextSpawnData.isPresent()) {
         return (SpawnData)this.nextSpawnData.get();
      } else {
         SimpleWeightedRandomList var3 = var1.getConfig().spawnPotentialsDefinition();
         Optional var4 = var3.isEmpty() ? this.nextSpawnData : var3.getRandom(var2).map(WeightedEntry.Wrapper::data);
         this.nextSpawnData = Optional.of((SpawnData)var4.orElseGet(SpawnData::new));
         var1.markUpdated();
         return (SpawnData)this.nextSpawnData.get();
      }
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(TrialSpawner var1, Level var2, TrialSpawnerState var3) {
      if (!var3.hasSpinningMob()) {
         return null;
      } else {
         if (this.displayEntity == null) {
            CompoundTag var4 = this.getOrCreateNextSpawnData(var1, var2.getRandom()).getEntityToSpawn();
            if (var4.contains("id", 8)) {
               this.displayEntity = EntityType.loadEntityRecursive(var4, var2, Function.identity());
            }
         }

         return this.displayEntity;
      }
   }

   public CompoundTag getUpdateTag(TrialSpawnerState var1) {
      CompoundTag var2 = new CompoundTag();
      if (var1 == TrialSpawnerState.ACTIVE) {
         var2.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
      }

      this.nextSpawnData.ifPresent((var1x) -> {
         var2.put("spawn_data", (Tag)SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, var1x).result().orElseThrow(() -> {
            return new IllegalStateException("Invalid SpawnData");
         }));
      });
      return var2;
   }

   public double getSpin() {
      return this.spin;
   }

   public double getOSpin() {
      return this.oSpin;
   }

   SimpleWeightedRandomList<ItemStack> getDispensingItems(ServerLevel var1, TrialSpawnerConfig var2, BlockPos var3) {
      if (this.dispensing != null) {
         return this.dispensing;
      } else {
         LootTable var4 = var1.getServer().reloadableRegistries().getLootTable(var2.itemsToDropWhenOminous());
         LootParams var5 = (new LootParams.Builder(var1)).create(LootContextParamSets.EMPTY);
         long var6 = lowResolutionPosition(var1, var3);
         ObjectArrayList var8 = var4.getRandomItems(var5, var6);
         if (var8.isEmpty()) {
            return SimpleWeightedRandomList.empty();
         } else {
            SimpleWeightedRandomList.Builder var9 = new SimpleWeightedRandomList.Builder();
            ObjectListIterator var10 = var8.iterator();

            while(var10.hasNext()) {
               ItemStack var11 = (ItemStack)var10.next();
               var9.add(var11.copyWithCount(1), var11.getCount());
            }

            this.dispensing = var9.build();
            return this.dispensing;
         }
      }
   }

   private static long lowResolutionPosition(ServerLevel var0, BlockPos var1) {
      BlockPos var2 = new BlockPos(Mth.floor((float)var1.getX() / 30.0F), Mth.floor((float)var1.getY() / 20.0F), Mth.floor((float)var1.getZ() / 30.0F));
      return var0.getSeed() + var2.asLong();
   }
}
