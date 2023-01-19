package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public abstract class BaseSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EVENT_SPAWN = 1;
   private int spawnDelay = 20;
   private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
   private SpawnData nextSpawnData = new SpawnData();
   private double spin;
   private double oSpin;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   @Nullable
   private Entity displayEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;

   public BaseSpawner() {
      super();
   }

   public void setEntityId(EntityType<?> var1) {
      this.nextSpawnData.getEntityToSpawn().putString("id", Registry.ENTITY_TYPE.getKey(var1).toString());
   }

   private boolean isNearPlayer(Level var1, BlockPos var2) {
      return var1.hasNearbyAlivePlayer((double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, (double)this.requiredPlayerRange);
   }

   public void clientTick(Level var1, BlockPos var2) {
      if (!this.isNearPlayer(var1, var2)) {
         this.oSpin = this.spin;
      } else {
         RandomSource var3 = var1.getRandom();
         double var4 = (double)var2.getX() + var3.nextDouble();
         double var6 = (double)var2.getY() + var3.nextDouble();
         double var8 = (double)var2.getZ() + var3.nextDouble();
         var1.addParticle(ParticleTypes.SMOKE, var4, var6, var8, 0.0, 0.0, 0.0);
         var1.addParticle(ParticleTypes.FLAME, var4, var6, var8, 0.0, 0.0, 0.0);
         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         }

         this.oSpin = this.spin;
         this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
      }
   }

   public void serverTick(ServerLevel var1, BlockPos var2) {
      if (this.isNearPlayer(var1, var2)) {
         if (this.spawnDelay == -1) {
            this.delay(var1, var2);
         }

         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         } else {
            boolean var3 = false;

            for(int var4 = 0; var4 < this.spawnCount; ++var4) {
               CompoundTag var5 = this.nextSpawnData.getEntityToSpawn();
               Optional var6 = EntityType.by(var5);
               if (var6.isEmpty()) {
                  this.delay(var1, var2);
                  return;
               }

               ListTag var7 = var5.getList("Pos", 6);
               int var8 = var7.size();
               RandomSource var9 = var1.getRandom();
               double var10 = var8 >= 1 ? var7.getDouble(0) : (double)var2.getX() + (var9.nextDouble() - var9.nextDouble()) * (double)this.spawnRange + 0.5;
               double var12 = var8 >= 2 ? var7.getDouble(1) : (double)(var2.getY() + var9.nextInt(3) - 1);
               double var14 = var8 >= 3 ? var7.getDouble(2) : (double)var2.getZ() + (var9.nextDouble() - var9.nextDouble()) * (double)this.spawnRange + 0.5;
               if (var1.noCollision(((EntityType)var6.get()).getAABB(var10, var12, var14))) {
                  BlockPos var16 = new BlockPos(var10, var12, var14);
                  if (this.nextSpawnData.getCustomSpawnRules().isPresent()) {
                     if (!((EntityType)var6.get()).getCategory().isFriendly() && var1.getDifficulty() == Difficulty.PEACEFUL) {
                        continue;
                     }

                     SpawnData.CustomSpawnRules var17 = this.nextSpawnData.getCustomSpawnRules().get();
                     if (!var17.blockLightLimit().isValueInRange(var1.getBrightness(LightLayer.BLOCK, var16))
                        || !var17.skyLightLimit().isValueInRange(var1.getBrightness(LightLayer.SKY, var16))) {
                        continue;
                     }
                  } else if (!SpawnPlacements.checkSpawnRules((EntityType)var6.get(), var1, MobSpawnType.SPAWNER, var16, var1.getRandom())) {
                     continue;
                  }

                  Entity var20 = EntityType.loadEntityRecursive(var5, var1, var6x -> {
                     var6x.moveTo(var10, var12, var14, var6x.getYRot(), var6x.getXRot());
                     return var6x;
                  });
                  if (var20 == null) {
                     this.delay(var1, var2);
                     return;
                  }

                  int var18 = var1.getEntitiesOfClass(
                        var20.getClass(),
                        new AABB(
                              (double)var2.getX(),
                              (double)var2.getY(),
                              (double)var2.getZ(),
                              (double)(var2.getX() + 1),
                              (double)(var2.getY() + 1),
                              (double)(var2.getZ() + 1)
                           )
                           .inflate((double)this.spawnRange)
                     )
                     .size();
                  if (var18 >= this.maxNearbyEntities) {
                     this.delay(var1, var2);
                     return;
                  }

                  var20.moveTo(var20.getX(), var20.getY(), var20.getZ(), var9.nextFloat() * 360.0F, 0.0F);
                  if (var20 instanceof Mob var19) {
                     if (this.nextSpawnData.getCustomSpawnRules().isEmpty() && !var19.checkSpawnRules(var1, MobSpawnType.SPAWNER)
                        || !var19.checkSpawnObstruction(var1)) {
                        continue;
                     }

                     if (this.nextSpawnData.getEntityToSpawn().size() == 1 && this.nextSpawnData.getEntityToSpawn().contains("id", 8)) {
                        ((Mob)var20).finalizeSpawn(var1, var1.getCurrentDifficultyAt(var20.blockPosition()), MobSpawnType.SPAWNER, null, null);
                     }
                  }

                  if (!var1.tryAddFreshEntityWithPassengers(var20)) {
                     this.delay(var1, var2);
                     return;
                  }

                  var1.levelEvent(2004, var2, 0);
                  var1.gameEvent(var20, GameEvent.ENTITY_PLACE, var16);
                  if (var20 instanceof Mob) {
                     ((Mob)var20).spawnAnim();
                  }

                  var3 = true;
               }
            }

            if (var3) {
               this.delay(var1, var2);
            }
         }
      }
   }

   private void delay(Level var1, BlockPos var2) {
      RandomSource var3 = var1.random;
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         this.spawnDelay = this.minSpawnDelay + var3.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
      }

      this.spawnPotentials.getRandom(var3).ifPresent(var3x -> this.setNextSpawnData(var1, var2, var3x.getData()));
      this.broadcastEvent(var1, var2, 1);
   }

   public void load(@Nullable Level var1, BlockPos var2, CompoundTag var3) {
      this.spawnDelay = var3.getShort("Delay");
      boolean var4 = var3.contains("SpawnPotentials", 9);
      boolean var5 = var3.contains("SpawnData", 10);
      if (!var4) {
         SpawnData var6;
         if (var5) {
            var6 = SpawnData.CODEC
               .parse(NbtOps.INSTANCE, var3.getCompound("SpawnData"))
               .resultOrPartial(var0 -> LOGGER.warn("Invalid SpawnData: {}", var0))
               .orElseGet(SpawnData::new);
         } else {
            var6 = new SpawnData();
         }

         this.spawnPotentials = SimpleWeightedRandomList.single(var6);
         this.setNextSpawnData(var1, var2, var6);
      } else {
         ListTag var8 = var3.getList("SpawnPotentials", 10);
         this.spawnPotentials = (SimpleWeightedRandomList)SpawnData.LIST_CODEC
            .parse(NbtOps.INSTANCE, var8)
            .resultOrPartial(var0 -> LOGGER.warn("Invalid SpawnPotentials list: {}", var0))
            .orElseGet(SimpleWeightedRandomList::empty);
         if (var5) {
            SpawnData var7 = SpawnData.CODEC
               .parse(NbtOps.INSTANCE, var3.getCompound("SpawnData"))
               .resultOrPartial(var0 -> LOGGER.warn("Invalid SpawnData: {}", var0))
               .orElseGet(SpawnData::new);
            this.setNextSpawnData(var1, var2, var7);
         } else {
            this.spawnPotentials.getRandom(var1.getRandom()).ifPresent(var3x -> this.setNextSpawnData(var1, var2, var3x.getData()));
         }
      }

      if (var3.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = var3.getShort("MinSpawnDelay");
         this.maxSpawnDelay = var3.getShort("MaxSpawnDelay");
         this.spawnCount = var3.getShort("SpawnCount");
      }

      if (var3.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = var3.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = var3.getShort("RequiredPlayerRange");
      }

      if (var3.contains("SpawnRange", 99)) {
         this.spawnRange = var3.getShort("SpawnRange");
      }

      this.displayEntity = null;
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putShort("Delay", (short)this.spawnDelay);
      var1.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
      var1.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
      var1.putShort("SpawnCount", (short)this.spawnCount);
      var1.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
      var1.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
      var1.putShort("SpawnRange", (short)this.spawnRange);
      var1.put(
         "SpawnData",
         (Tag)SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, this.nextSpawnData).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
      );
      var1.put("SpawnPotentials", (Tag)SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
      return var1;
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(Level var1) {
      if (this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getEntityToSpawn(), var1, Function.identity());
         if (this.nextSpawnData.getEntityToSpawn().size() == 1 && this.nextSpawnData.getEntityToSpawn().contains("id", 8) && this.displayEntity instanceof Mob
            )
          {
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(Level var1, int var2) {
      if (var2 == 1) {
         if (var1.isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
         }

         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(@Nullable Level var1, BlockPos var2, SpawnData var3) {
      this.nextSpawnData = var3;
   }

   public abstract void broadcastEvent(Level var1, BlockPos var2, int var3);

   public double getSpin() {
      return this.spin;
   }

   public double getoSpin() {
      return this.oSpin;
   }
}
