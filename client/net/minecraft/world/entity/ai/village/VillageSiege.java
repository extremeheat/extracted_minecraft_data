package net.minecraft.world.entity.ai.village;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class VillageSiege implements CustomSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private boolean hasSetupSiege;
   private State siegeState;
   private int zombiesToSpawn;
   private int nextSpawnTime;
   private int spawnX;
   private int spawnY;
   private int spawnZ;

   public VillageSiege() {
      super();
      this.siegeState = VillageSiege.State.SIEGE_DONE;
   }

   public void tick(final ServerLevel level, final boolean spawnEnemies) {
      if (!level.isBrightOutside() && spawnEnemies) {
         Optional<Holder<WorldClock>> defaultClock = level.dimensionType().defaultClock();
         if (defaultClock.isPresent() && level.clockManager().isAtTimeMarker((Holder)defaultClock.get(), ClockTimeMarkers.ROLL_VILLAGE_SIEGE)) {
            this.siegeState = level.getRandom().nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
         }

         if (this.siegeState != VillageSiege.State.SIEGE_DONE) {
            if (!this.hasSetupSiege) {
               if (!this.tryToSetupSiege(level)) {
                  return;
               }

               this.hasSetupSiege = true;
            }

            if (this.nextSpawnTime > 0) {
               --this.nextSpawnTime;
            } else {
               this.nextSpawnTime = 2;
               if (this.zombiesToSpawn > 0) {
                  this.trySpawn(level);
                  --this.zombiesToSpawn;
               } else {
                  this.siegeState = VillageSiege.State.SIEGE_DONE;
               }

            }
         }
      } else {
         this.siegeState = VillageSiege.State.SIEGE_DONE;
         this.hasSetupSiege = false;
      }
   }

   private boolean tryToSetupSiege(final ServerLevel level) {
      RandomSource random = level.getRandom();

      for(Player player : level.players()) {
         if (!player.isSpectator()) {
            BlockPos center = player.blockPosition();
            if (level.isVillage(center) && !level.getBiome(center).is(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) {
               for(int i = 0; i < 10; ++i) {
                  float angle = random.nextFloat() * 6.2831855F;
                  this.spawnX = center.getX() + Mth.floor(Mth.cos((double)angle) * 32.0F);
                  this.spawnY = center.getY();
                  this.spawnZ = center.getZ() + Mth.floor(Mth.sin((double)angle) * 32.0F);
                  if (this.findRandomSpawnPos(level, new BlockPos(this.spawnX, this.spawnY, this.spawnZ)) != null) {
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

   private void trySpawn(final ServerLevel level) {
      Vec3 spawnPos = this.findRandomSpawnPos(level, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
      if (spawnPos != null) {
         Zombie zombie;
         try {
            zombie = new Zombie(level);
            zombie.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), EntitySpawnReason.EVENT, (SpawnGroupData)null);
         } catch (Exception e) {
            LOGGER.warn("Failed to create zombie for village siege at {}", spawnPos, e);
            return;
         }

         zombie.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
         level.addFreshEntityWithPassengers(zombie);
      }
   }

   private @Nullable Vec3 findRandomSpawnPos(final ServerLevel level, final BlockPos pos) {
      RandomSource random = level.getRandom();

      for(int i = 0; i < 10; ++i) {
         int x = pos.getX() + random.nextInt(16) - 8;
         int z = pos.getZ() + random.nextInt(16) - 8;
         int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
         BlockPos offset = new BlockPos(x, y, z);
         if (level.isVillage(offset) && Monster.checkMonsterSpawnRules(EntityTypes.ZOMBIE, level, EntitySpawnReason.EVENT, offset, random)) {
            return Vec3.atBottomCenterOf(offset);
         }
      }

      return null;
   }

   private static enum State {
      SIEGE_CAN_ACTIVATE,
      SIEGE_TONIGHT,
      SIEGE_DONE;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{SIEGE_CAN_ACTIVATE, SIEGE_TONIGHT, SIEGE_DONE};
      }
   }
}
