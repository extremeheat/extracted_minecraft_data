package net.minecraft.world.entity.ai.village;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillageSiege implements CustomSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean hasSetupSiege;
   private VillageSiege.State siegeState;
   private int zombiesToSpawn;
   private int nextSpawnTime;
   private int spawnX;
   private int spawnY;
   private int spawnZ;

   public VillageSiege() {
      super();
      this.siegeState = VillageSiege.State.SIEGE_DONE;
   }

   public int tick(ServerLevel var1, boolean var2, boolean var3) {
      if (!var1.isDay() && var2) {
         float var4 = var1.getTimeOfDay(0.0F);
         if ((double)var4 == 0.5D) {
            this.siegeState = var1.random.nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
         }

         if (this.siegeState == VillageSiege.State.SIEGE_DONE) {
            return 0;
         } else {
            if (!this.hasSetupSiege) {
               if (!this.tryToSetupSiege(var1)) {
                  return 0;
               }

               this.hasSetupSiege = true;
            }

            if (this.nextSpawnTime > 0) {
               --this.nextSpawnTime;
               return 0;
            } else {
               this.nextSpawnTime = 2;
               if (this.zombiesToSpawn > 0) {
                  this.trySpawn(var1);
                  --this.zombiesToSpawn;
               } else {
                  this.siegeState = VillageSiege.State.SIEGE_DONE;
               }

               return 1;
            }
         }
      } else {
         this.siegeState = VillageSiege.State.SIEGE_DONE;
         this.hasSetupSiege = false;
         return 0;
      }
   }

   private boolean tryToSetupSiege(ServerLevel var1) {
      Iterator var2 = var1.players().iterator();

      while(var2.hasNext()) {
         Player var3 = (Player)var2.next();
         if (!var3.isSpectator()) {
            BlockPos var4 = var3.blockPosition();
            if (var1.isVillage(var4) && var1.getBiome(var4).getBiomeCategory() != Biome.BiomeCategory.MUSHROOM) {
               for(int var5 = 0; var5 < 10; ++var5) {
                  float var6 = var1.random.nextFloat() * 6.2831855F;
                  this.spawnX = var4.getX() + Mth.floor(Mth.cos(var6) * 32.0F);
                  this.spawnY = var4.getY();
                  this.spawnZ = var4.getZ() + Mth.floor(Mth.sin(var6) * 32.0F);
                  if (this.findRandomSpawnPos(var1, new BlockPos(this.spawnX, this.spawnY, this.spawnZ)) != null) {
                     this.nextSpawnTime = 0;
                     this.zombiesToSpawn = 20;
                     break;
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   private void trySpawn(ServerLevel var1) {
      Vec3 var2 = this.findRandomSpawnPos(var1, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
      if (var2 != null) {
         Zombie var3;
         try {
            var3 = new Zombie(var1);
            var3.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var3.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData)null, (CompoundTag)null);
         } catch (Exception var5) {
            LOGGER.warn("Failed to create zombie for village siege at {}", var2, var5);
            return;
         }

         var3.moveTo(var2.x, var2.y, var2.z, var1.random.nextFloat() * 360.0F, 0.0F);
         var1.addFreshEntityWithPassengers(var3);
      }
   }

   @Nullable
   private Vec3 findRandomSpawnPos(ServerLevel var1, BlockPos var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         int var4 = var2.getX() + var1.random.nextInt(16) - 8;
         int var5 = var2.getZ() + var1.random.nextInt(16) - 8;
         int var6 = var1.getHeight(Heightmap.Types.WORLD_SURFACE, var4, var5);
         BlockPos var7 = new BlockPos(var4, var6, var5);
         if (var1.isVillage(var7) && Monster.checkMonsterSpawnRules(EntityType.ZOMBIE, var1, MobSpawnType.EVENT, var7, var1.random)) {
            return Vec3.atBottomCenterOf(var7);
         }
      }

      return null;
   }

   static enum State {
      SIEGE_CAN_ACTIVATE,
      SIEGE_TONIGHT,
      SIEGE_DONE;

      private State() {
      }
   }
}
