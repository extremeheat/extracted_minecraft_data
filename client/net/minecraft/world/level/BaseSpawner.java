package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public abstract class BaseSpawner {
   public static final String SPAWN_DATA_TAG = "SpawnData";
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EVENT_SPAWN = 1;
   private int spawnDelay = 20;
   private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
   @Nullable
   private SpawnData nextSpawnData;
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

   public void setEntityId(EntityType<?> var1, @Nullable Level var2, RandomSource var3, BlockPos var4) {
      this.getOrCreateNextSpawnData(var2, var3, var4).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(var1).toString());
   }

   private boolean isNearPlayer(Level var1, BlockPos var2) {
      return var1.hasNearbyAlivePlayer((double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, (double)this.requiredPlayerRange);
   }

   public void clientTick(Level var1, BlockPos var2) {
      if (!this.isNearPlayer(var1, var2)) {
         this.oSpin = this.spin;
      } else if (this.displayEntity != null) {
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
            RandomSource var4 = var1.getRandom();
            SpawnData var5 = this.getOrCreateNextSpawnData(var1, var4, var2);

            for(int var6 = 0; var6 < this.spawnCount; ++var6) {
               CompoundTag var7 = var5.getEntityToSpawn();
               Optional var8 = EntityType.by(var7);
               if (var8.isEmpty()) {
                  this.delay(var1, var2);
                  return;
               }

               ListTag var9 = var7.getList("Pos", 6);
               int var10 = var9.size();
               double var11 = var10 >= 1 ? var9.getDouble(0) : (double)var2.getX() + (var4.nextDouble() - var4.nextDouble()) * (double)this.spawnRange + 0.5;
               double var13 = var10 >= 2 ? var9.getDouble(1) : (double)(var2.getY() + var4.nextInt(3) - 1);
               double var15 = var10 >= 3 ? var9.getDouble(2) : (double)var2.getZ() + (var4.nextDouble() - var4.nextDouble()) * (double)this.spawnRange + 0.5;
               if (var1.noCollision(((EntityType)var8.get()).getAABB(var11, var13, var15))) {
                  BlockPos var17 = BlockPos.containing(var11, var13, var15);
                  if (var5.getCustomSpawnRules().isPresent()) {
                     if (!((EntityType)var8.get()).getCategory().isFriendly() && var1.getDifficulty() == Difficulty.PEACEFUL) {
                        continue;
                     }

                     SpawnData.CustomSpawnRules var18 = var5.getCustomSpawnRules().get();
                     if (!var18.blockLightLimit().isValueInRange(var1.getBrightness(LightLayer.BLOCK, var17))
                        || !var18.skyLightLimit().isValueInRange(var1.getBrightness(LightLayer.SKY, var17))) {
                        continue;
                     }
                  } else if (!SpawnPlacements.checkSpawnRules((EntityType)var8.get(), var1, MobSpawnType.SPAWNER, var17, var1.getRandom())) {
                     continue;
                  }

                  Entity var21 = EntityType.loadEntityRecursive(var7, var1, var6x -> {
                     var6x.moveTo(var11, var13, var15, var6x.getYRot(), var6x.getXRot());
                     return var6x;
                  });
                  if (var21 == null) {
                     this.delay(var1, var2);
                     return;
                  }

                  int var19 = var1.getEntities(
                        EntityTypeTest.forExactClass(var21.getClass()),
                        new AABB(
                              (double)var2.getX(),
                              (double)var2.getY(),
                              (double)var2.getZ(),
                              (double)(var2.getX() + 1),
                              (double)(var2.getY() + 1),
                              (double)(var2.getZ() + 1)
                           )
                           .inflate((double)this.spawnRange),
                        EntitySelector.NO_SPECTATORS
                     )
                     .size();
                  if (var19 >= this.maxNearbyEntities) {
                     this.delay(var1, var2);
                     return;
                  }

                  var21.moveTo(var21.getX(), var21.getY(), var21.getZ(), var4.nextFloat() * 360.0F, 0.0F);
                  if (var21 instanceof Mob var20) {
                     if (var5.getCustomSpawnRules().isEmpty() && !var20.checkSpawnRules(var1, MobSpawnType.SPAWNER) || !var20.checkSpawnObstruction(var1)) {
                        continue;
                     }

                     if (var5.getEntityToSpawn().size() == 1 && var5.getEntityToSpawn().contains("id", 8)) {
                        ((Mob)var21).finalizeSpawn(var1, var1.getCurrentDifficultyAt(var21.blockPosition()), MobSpawnType.SPAWNER, null, null);
                     }
                  }

                  if (!var1.tryAddFreshEntityWithPassengers(var21)) {
                     this.delay(var1, var2);
                     return;
                  }

                  var1.levelEvent(2004, var2, 0);
                  var1.gameEvent(var21, GameEvent.ENTITY_PLACE, var17);
                  if (var21 instanceof Mob) {
                     ((Mob)var21).spawnAnim();
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
      boolean var4 = var3.contains("SpawnData", 10);
      if (var4) {
         SpawnData var5 = SpawnData.CODEC
            .parse(NbtOps.INSTANCE, var3.getCompound("SpawnData"))
            .resultOrPartial(var0 -> LOGGER.warn("Invalid SpawnData: {}", var0))
            .orElseGet(SpawnData::new);
         this.setNextSpawnData(var1, var2, var5);
      }

      boolean var7 = var3.contains("SpawnPotentials", 9);
      if (var7) {
         ListTag var6 = var3.getList("SpawnPotentials", 10);
         this.spawnPotentials = (SimpleWeightedRandomList)SpawnData.LIST_CODEC
            .parse(NbtOps.INSTANCE, var6)
            .resultOrPartial(var0 -> LOGGER.warn("Invalid SpawnPotentials list: {}", var0))
            .orElseGet(SimpleWeightedRandomList::empty);
      } else {
         this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData());
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
      if (this.nextSpawnData != null) {
         var1.put(
            "SpawnData",
            (Tag)SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, this.nextSpawnData).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
         );
      }

      var1.put("SpawnPotentials", (Tag)SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
      return var1;
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(Level var1, BlockPos var2) {
      if (this.displayEntity == null) {
         CompoundTag var3 = this.getOrCreateNextSpawnData(var1, var1.getRandom(), var2).getEntityToSpawn();
         if (!var3.contains("id", 8)) {
            return null;
         }

         this.displayEntity = EntityType.loadEntityRecursive(var3, var1, Function.identity());
         if (var3.size() == 1 && this.displayEntity instanceof Mob) {
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

   protected void setNextSpawnData(@Nullable Level var1, BlockPos var2, SpawnData var3) {
      this.nextSpawnData = var3;
   }

   private SpawnData getOrCreateNextSpawnData(@Nullable Level var1, RandomSource var2, BlockPos var3) {
      if (this.nextSpawnData != null) {
         return this.nextSpawnData;
      } else {
         this.setNextSpawnData(var1, var3, this.spawnPotentials.getRandom(var2).map(WeightedEntry.Wrapper::getData).orElseGet(SpawnData::new));
         return this.nextSpawnData;
      }
   }

   public abstract void broadcastEvent(Level var1, BlockPos var2, int var3);

   public double getSpin() {
      return this.spin;
   }

   public double getoSpin() {
      return this.oSpin;
   }
}
