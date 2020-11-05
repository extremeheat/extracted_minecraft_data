package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private int spawnDelay = 20;
   private final List<SpawnData> spawnPotentials = Lists.newArrayList();
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
   private final Random random = new Random();

   public BaseSpawner() {
      super();
   }

   @Nullable
   private ResourceLocation getEntityId(@Nullable Level var1, BlockPos var2) {
      String var3 = this.nextSpawnData.getTag().getString("id");

      try {
         return StringUtil.isNullOrEmpty(var3) ? null : new ResourceLocation(var3);
      } catch (ResourceLocationException var5) {
         LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", var3, var1 != null ? var1.dimension().location() : "<null>", var2.getX(), var2.getY(), var2.getZ());
         return null;
      }
   }

   public void setEntityId(EntityType<?> var1) {
      this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(var1).toString());
   }

   private boolean isNearPlayer(Level var1, BlockPos var2) {
      return var1.hasNearbyAlivePlayer((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, (double)this.requiredPlayerRange);
   }

   public void clientTick(Level var1, BlockPos var2) {
      if (!this.isNearPlayer(var1, var2)) {
         this.oSpin = this.spin;
      } else {
         double var3 = (double)var2.getX() + var1.random.nextDouble();
         double var5 = (double)var2.getY() + var1.random.nextDouble();
         double var7 = (double)var2.getZ() + var1.random.nextDouble();
         var1.addParticle(ParticleTypes.SMOKE, var3, var5, var7, 0.0D, 0.0D, 0.0D);
         var1.addParticle(ParticleTypes.FLAME, var3, var5, var7, 0.0D, 0.0D, 0.0D);
         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         }

         this.oSpin = this.spin;
         this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
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
               CompoundTag var5 = this.nextSpawnData.getTag();
               Optional var6 = EntityType.by(var5);
               if (!var6.isPresent()) {
                  this.delay(var1, var2);
                  return;
               }

               ListTag var7 = var5.getList("Pos", 6);
               int var8 = var7.size();
               double var9 = var8 >= 1 ? var7.getDouble(0) : (double)var2.getX() + (var1.random.nextDouble() - var1.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               double var11 = var8 >= 2 ? var7.getDouble(1) : (double)(var2.getY() + var1.random.nextInt(3) - 1);
               double var13 = var8 >= 3 ? var7.getDouble(2) : (double)var2.getZ() + (var1.random.nextDouble() - var1.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               if (var1.noCollision(((EntityType)var6.get()).getAABB(var9, var11, var13)) && SpawnPlacements.checkSpawnRules((EntityType)var6.get(), var1, MobSpawnType.SPAWNER, new BlockPos(var9, var11, var13), var1.getRandom())) {
                  Entity var15 = EntityType.loadEntityRecursive(var5, var1, (var6x) -> {
                     var6x.moveTo(var9, var11, var13, var6x.yRot, var6x.xRot);
                     return var6x;
                  });
                  if (var15 == null) {
                     this.delay(var1, var2);
                     return;
                  }

                  int var16 = var1.getEntitiesOfClass(var15.getClass(), (new AABB((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), (double)(var2.getX() + 1), (double)(var2.getY() + 1), (double)(var2.getZ() + 1))).inflate((double)this.spawnRange)).size();
                  if (var16 >= this.maxNearbyEntities) {
                     this.delay(var1, var2);
                     return;
                  }

                  var15.moveTo(var15.getX(), var15.getY(), var15.getZ(), var1.random.nextFloat() * 360.0F, 0.0F);
                  if (var15 instanceof Mob) {
                     Mob var17 = (Mob)var15;
                     if (!var17.checkSpawnRules(var1, MobSpawnType.SPAWNER) || !var17.checkSpawnObstruction(var1)) {
                        continue;
                     }

                     if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                        ((Mob)var15).finalizeSpawn(var1, var1.getCurrentDifficultyAt(var15.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
                     }
                  }

                  if (!var1.tryAddFreshEntityWithPassengers(var15)) {
                     this.delay(var1, var2);
                     return;
                  }

                  var1.levelEvent(2004, var2, 0);
                  if (var15 instanceof Mob) {
                     ((Mob)var15).spawnAnim();
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
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
      }

      if (!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData(var1, var2, (SpawnData)WeighedRandom.getRandomItem(this.random, this.spawnPotentials));
      }

      this.broadcastEvent(var1, var2, 1);
   }

   public void load(@Nullable Level var1, BlockPos var2, CompoundTag var3) {
      this.spawnDelay = var3.getShort("Delay");
      this.spawnPotentials.clear();
      if (var3.contains("SpawnPotentials", 9)) {
         ListTag var4 = var3.getList("SpawnPotentials", 10);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            this.spawnPotentials.add(new SpawnData(var4.getCompound(var5)));
         }
      }

      if (var3.contains("SpawnData", 10)) {
         this.setNextSpawnData(var1, var2, new SpawnData(1, var3.getCompound("SpawnData")));
      } else if (!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData(var1, var2, (SpawnData)WeighedRandom.getRandomItem(this.random, this.spawnPotentials));
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

   public CompoundTag save(@Nullable Level var1, BlockPos var2, CompoundTag var3) {
      ResourceLocation var4 = this.getEntityId(var1, var2);
      if (var4 == null) {
         return var3;
      } else {
         var3.putShort("Delay", (short)this.spawnDelay);
         var3.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
         var3.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         var3.putShort("SpawnCount", (short)this.spawnCount);
         var3.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         var3.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
         var3.putShort("SpawnRange", (short)this.spawnRange);
         var3.put("SpawnData", this.nextSpawnData.getTag().copy());
         ListTag var5 = new ListTag();
         if (this.spawnPotentials.isEmpty()) {
            var5.add(this.nextSpawnData.save());
         } else {
            Iterator var6 = this.spawnPotentials.iterator();

            while(var6.hasNext()) {
               SpawnData var7 = (SpawnData)var6.next();
               var5.add(var7.save());
            }
         }

         var3.put("SpawnPotentials", var5);
         return var3;
      }
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(Level var1) {
      if (this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), var1, Function.identity());
         if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8) && this.displayEntity instanceof Mob) {
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
