package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

   public BaseSpawner() {
      super();
   }

   @Nullable
   private ResourceLocation getEntityId() {
      String var1 = this.nextSpawnData.getTag().getString("id");

      try {
         return StringUtil.isNullOrEmpty(var1) ? null : new ResourceLocation(var1);
      } catch (ResourceLocationException var4) {
         BlockPos var3 = this.getPos();
         LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", var1, this.getLevel().dimension().location(), var3.getX(), var3.getY(), var3.getZ());
         return null;
      }
   }

   public void setEntityId(EntityType<?> var1) {
      this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(var1).toString());
   }

   private boolean isNearPlayer() {
      BlockPos var1 = this.getPos();
      return this.getLevel().hasNearbyAlivePlayer((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, (double)this.requiredPlayerRange);
   }

   public void tick() {
      if (!this.isNearPlayer()) {
         this.oSpin = this.spin;
      } else {
         Level var1 = this.getLevel();
         BlockPos var2 = this.getPos();
         if (!(var1 instanceof ServerLevel)) {
            double var19 = (double)var2.getX() + var1.random.nextDouble();
            double var20 = (double)var2.getY() + var1.random.nextDouble();
            double var21 = (double)var2.getZ() + var1.random.nextDouble();
            var1.addParticle(ParticleTypes.SMOKE, var19, var20, var21, 0.0D, 0.0D, 0.0D);
            var1.addParticle(ParticleTypes.FLAME, var19, var20, var21, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
               --this.spawnDelay;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
         } else {
            if (this.spawnDelay == -1) {
               this.delay();
            }

            if (this.spawnDelay > 0) {
               --this.spawnDelay;
               return;
            }

            boolean var3 = false;
            int var4 = 0;

            while(true) {
               if (var4 >= this.spawnCount) {
                  if (var3) {
                     this.delay();
                  }
                  break;
               }

               CompoundTag var5 = this.nextSpawnData.getTag();
               Optional var6 = EntityType.by(var5);
               if (!var6.isPresent()) {
                  this.delay();
                  return;
               }

               ListTag var7 = var5.getList("Pos", 6);
               int var8 = var7.size();
               double var9 = var8 >= 1 ? var7.getDouble(0) : (double)var2.getX() + (var1.random.nextDouble() - var1.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               double var11 = var8 >= 2 ? var7.getDouble(1) : (double)(var2.getY() + var1.random.nextInt(3) - 1);
               double var13 = var8 >= 3 ? var7.getDouble(2) : (double)var2.getZ() + (var1.random.nextDouble() - var1.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               if (var1.noCollision(((EntityType)var6.get()).getAABB(var9, var11, var13))) {
                  ServerLevel var15 = (ServerLevel)var1;
                  if (SpawnPlacements.checkSpawnRules((EntityType)var6.get(), var15, MobSpawnType.SPAWNER, new BlockPos(var9, var11, var13), var1.getRandom())) {
                     label105: {
                        Entity var16 = EntityType.loadEntityRecursive(var5, var1, (var6x) -> {
                           var6x.moveTo(var9, var11, var13, var6x.yRot, var6x.xRot);
                           return var6x;
                        });
                        if (var16 == null) {
                           this.delay();
                           return;
                        }

                        int var17 = var1.getEntitiesOfClass(var16.getClass(), (new AABB((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), (double)(var2.getX() + 1), (double)(var2.getY() + 1), (double)(var2.getZ() + 1))).inflate((double)this.spawnRange)).size();
                        if (var17 >= this.maxNearbyEntities) {
                           this.delay();
                           return;
                        }

                        var16.moveTo(var16.getX(), var16.getY(), var16.getZ(), var1.random.nextFloat() * 360.0F, 0.0F);
                        if (var16 instanceof Mob) {
                           Mob var18 = (Mob)var16;
                           if (!var18.checkSpawnRules(var1, MobSpawnType.SPAWNER) || !var18.checkSpawnObstruction(var1)) {
                              break label105;
                           }

                           if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                              ((Mob)var16).finalizeSpawn(var15, var1.getCurrentDifficultyAt(var16.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
                           }
                        }

                        if (!var15.tryAddFreshEntityWithPassengers(var16)) {
                           this.delay();
                           return;
                        }

                        var1.levelEvent(2004, var2, 0);
                        if (var16 instanceof Mob) {
                           ((Mob)var16).spawnAnim();
                        }

                        var3 = true;
                     }
                  }
               }

               ++var4;
            }
         }

      }
   }

   private void delay() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int var10003 = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getLevel().random.nextInt(var10003);
      }

      if (!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData((SpawnData)WeighedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
      }

      this.broadcastEvent(1);
   }

   public void load(CompoundTag var1) {
      this.spawnDelay = var1.getShort("Delay");
      this.spawnPotentials.clear();
      if (var1.contains("SpawnPotentials", 9)) {
         ListTag var2 = var1.getList("SpawnPotentials", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.spawnPotentials.add(new SpawnData(var2.getCompound(var3)));
         }
      }

      if (var1.contains("SpawnData", 10)) {
         this.setNextSpawnData(new SpawnData(1, var1.getCompound("SpawnData")));
      } else if (!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData((SpawnData)WeighedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
      }

      if (var1.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = var1.getShort("MinSpawnDelay");
         this.maxSpawnDelay = var1.getShort("MaxSpawnDelay");
         this.spawnCount = var1.getShort("SpawnCount");
      }

      if (var1.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = var1.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = var1.getShort("RequiredPlayerRange");
      }

      if (var1.contains("SpawnRange", 99)) {
         this.spawnRange = var1.getShort("SpawnRange");
      }

      if (this.getLevel() != null) {
         this.displayEntity = null;
      }

   }

   public CompoundTag save(CompoundTag var1) {
      ResourceLocation var2 = this.getEntityId();
      if (var2 == null) {
         return var1;
      } else {
         var1.putShort("Delay", (short)this.spawnDelay);
         var1.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
         var1.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         var1.putShort("SpawnCount", (short)this.spawnCount);
         var1.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         var1.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
         var1.putShort("SpawnRange", (short)this.spawnRange);
         var1.put("SpawnData", this.nextSpawnData.getTag().copy());
         ListTag var3 = new ListTag();
         if (this.spawnPotentials.isEmpty()) {
            var3.add(this.nextSpawnData.save());
         } else {
            Iterator var4 = this.spawnPotentials.iterator();

            while(var4.hasNext()) {
               SpawnData var5 = (SpawnData)var4.next();
               var3.add(var5.save());
            }
         }

         var1.put("SpawnPotentials", var3);
         return var1;
      }
   }

   @Nullable
   public Entity getOrCreateDisplayEntity() {
      if (this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), this.getLevel(), Function.identity());
         if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8) && this.displayEntity instanceof Mob) {
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(int var1) {
      if (var1 == 1 && this.getLevel().isClientSide) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(SpawnData var1) {
      this.nextSpawnData = var1;
   }

   public abstract void broadcastEvent(int var1);

   public abstract Level getLevel();

   public abstract BlockPos getPos();

   public double getSpin() {
      return this.spin;
   }

   public double getoSpin() {
      return this.oSpin;
   }
}
