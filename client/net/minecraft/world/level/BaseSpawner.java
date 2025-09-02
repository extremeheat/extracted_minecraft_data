package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class BaseSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String SPAWN_DATA_TAG = "SpawnData";
   private static final int EVENT_SPAWN = 1;
   private static final int DEFAULT_SPAWN_DELAY = 20;
   private static final int DEFAULT_MIN_SPAWN_DELAY = 200;
   private static final int DEFAULT_MAX_SPAWN_DELAY = 800;
   private static final int DEFAULT_SPAWN_COUNT = 4;
   private static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
   private static final int DEFAULT_REQUIRED_PLAYER_RANGE = 16;
   private static final int DEFAULT_SPAWN_RANGE = 4;
   private int spawnDelay = 20;
   private WeightedList<SpawnData> spawnPotentials = WeightedList.<SpawnData>of();
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
               try (ProblemReporter.ScopedCollector var7 = new ProblemReporter.ScopedCollector(this::toString, LOGGER)) {
                  ValueInput var8 = TagValueInput.create(var7, var1.registryAccess(), var5.getEntityToSpawn());
                  Optional var9 = EntityType.by(var8);
                  if (var9.isEmpty()) {
                     this.delay(var1, var2);
                     return;
                  }

                  Vec3 var10 = (Vec3)var8.read("Pos", Vec3.CODEC).orElseGet(() -> new Vec3((double)var2.getX() + (var4.nextDouble() - var4.nextDouble()) * (double)this.spawnRange + 0.5, (double)(var2.getY() + var4.nextInt(3) - 1), (double)var2.getZ() + (var4.nextDouble() - var4.nextDouble()) * (double)this.spawnRange + 0.5));
                  if (var1.noCollision(((EntityType)var9.get()).getSpawnAABB(var10.x, var10.y, var10.z))) {
                     BlockPos var11 = BlockPos.containing(var10);
                     if (var5.getCustomSpawnRules().isPresent()) {
                        if (!((EntityType)var9.get()).getCategory().isFriendly() && var1.getDifficulty() == Difficulty.PEACEFUL) {
                           continue;
                        }

                        SpawnData.CustomSpawnRules var12 = (SpawnData.CustomSpawnRules)var5.getCustomSpawnRules().get();
                        if (!var12.isValidPosition(var11, var1)) {
                           continue;
                        }
                     } else if (!SpawnPlacements.checkSpawnRules((EntityType)var9.get(), var1, EntitySpawnReason.SPAWNER, var11, var1.getRandom())) {
                        continue;
                     }

                     Entity var18 = EntityType.loadEntityRecursive((ValueInput)var8, var1, EntitySpawnReason.SPAWNER, (var1x) -> {
                        var1x.snapTo(var10.x, var10.y, var10.z, var1x.getYRot(), var1x.getXRot());
                        return var1x;
                     });
                     if (var18 == null) {
                        this.delay(var1, var2);
                        return;
                     }

                     int var13 = var1.getEntities(EntityTypeTest.forExactClass(var18.getClass()), (new AABB((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), (double)(var2.getX() + 1), (double)(var2.getY() + 1), (double)(var2.getZ() + 1))).inflate((double)this.spawnRange), EntitySelector.NO_SPECTATORS).size();
                     if (var13 >= this.maxNearbyEntities) {
                        this.delay(var1, var2);
                        return;
                     }

                     var18.snapTo(var18.getX(), var18.getY(), var18.getZ(), var4.nextFloat() * 360.0F, 0.0F);
                     if (var18 instanceof Mob) {
                        Mob var14 = (Mob)var18;
                        if (var5.getCustomSpawnRules().isEmpty() && !var14.checkSpawnRules(var1, EntitySpawnReason.SPAWNER) || !var14.checkSpawnObstruction(var1)) {
                           continue;
                        }

                        boolean var15 = var5.getEntityToSpawn().size() == 1 && var5.getEntityToSpawn().getString("id").isPresent();
                        if (var15) {
                           ((Mob)var18).finalizeSpawn(var1, var1.getCurrentDifficultyAt(var18.blockPosition()), EntitySpawnReason.SPAWNER, (SpawnGroupData)null);
                        }

                        Optional var10000 = var5.getEquipment();
                        Objects.requireNonNull(var14);
                        var10000.ifPresent(var14::equip);
                     }

                     if (!var1.tryAddFreshEntityWithPassengers(var18)) {
                        this.delay(var1, var2);
                        return;
                     }

                     var1.levelEvent(2004, var2, 0);
                     var1.gameEvent(var18, GameEvent.ENTITY_PLACE, var11);
                     if (var18 instanceof Mob) {
                        ((Mob)var18).spawnAnim();
                     }

                     var3 = true;
                  }
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

      this.spawnPotentials.getRandom(var3).ifPresent((var3x) -> this.setNextSpawnData(var1, var2, var3x));
      this.broadcastEvent(var1, var2, 1);
   }

   public void load(@Nullable Level var1, BlockPos var2, ValueInput var3) {
      this.spawnDelay = var3.getShortOr("Delay", (short)20);
      var3.read("SpawnData", SpawnData.CODEC).ifPresent((var3x) -> this.setNextSpawnData(var1, var2, var3x));
      this.spawnPotentials = (WeightedList)var3.read("SpawnPotentials", SpawnData.LIST_CODEC).orElseGet(() -> WeightedList.of(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData()));
      this.minSpawnDelay = var3.getIntOr("MinSpawnDelay", 200);
      this.maxSpawnDelay = var3.getIntOr("MaxSpawnDelay", 800);
      this.spawnCount = var3.getIntOr("SpawnCount", 4);
      this.maxNearbyEntities = var3.getIntOr("MaxNearbyEntities", 6);
      this.requiredPlayerRange = var3.getIntOr("RequiredPlayerRange", 16);
      this.spawnRange = var3.getIntOr("SpawnRange", 4);
      this.displayEntity = null;
   }

   public void save(ValueOutput var1) {
      var1.putShort("Delay", (short)this.spawnDelay);
      var1.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
      var1.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
      var1.putShort("SpawnCount", (short)this.spawnCount);
      var1.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
      var1.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
      var1.putShort("SpawnRange", (short)this.spawnRange);
      var1.storeNullable("SpawnData", SpawnData.CODEC, this.nextSpawnData);
      var1.store("SpawnPotentials", SpawnData.LIST_CODEC, this.spawnPotentials);
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(Level var1, BlockPos var2) {
      if (this.displayEntity == null) {
         CompoundTag var3 = this.getOrCreateNextSpawnData(var1, var1.getRandom(), var2).getEntityToSpawn();
         if (var3.getString("id").isEmpty()) {
            return null;
         }

         this.displayEntity = EntityType.loadEntityRecursive(var3, var1, EntitySpawnReason.SPAWNER, Function.identity());
         if (var3.size() == 1 && this.displayEntity instanceof Mob) {
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(Level var1, int var2) {
      if (var2 == 1) {
         if (var1.isClientSide()) {
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
         this.setNextSpawnData(var1, var3, (SpawnData)this.spawnPotentials.getRandom(var2).orElseGet(SpawnData::new));
         return this.nextSpawnData;
      }
   }

   public abstract void broadcastEvent(Level var1, BlockPos var2, int var3);

   public double getSpin() {
      return this.spin;
   }

   public double getOSpin() {
      return this.oSpin;
   }
}
