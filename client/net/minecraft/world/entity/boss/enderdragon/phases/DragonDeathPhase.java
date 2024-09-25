package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonDeathPhase extends AbstractDragonPhaseInstance {
   @Nullable
   private Vec3 targetLocation;
   private int time;

   public DragonDeathPhase(EnderDragon var1) {
      super(var1);
   }

   @Override
   public void doClientTick() {
      if (this.time++ % 10 == 0) {
         float var1 = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.dragon.getRandom().nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
         this.dragon
            .level()
            .addParticle(
               ParticleTypes.EXPLOSION_EMITTER,
               this.dragon.getX() + (double)var1,
               this.dragon.getY() + 2.0 + (double)var2,
               this.dragon.getZ() + (double)var3,
               0.0,
               0.0,
               0.0
            );
      }
   }

   @Override
   public void doServerTick(ServerLevel var1) {
      this.time++;
      if (this.targetLocation == null) {
         BlockPos var2 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.dragon.getFightOrigin()));
         this.targetLocation = Vec3.atBottomCenterOf(var2);
      }

      double var4 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (!(var4 < 100.0) && !(var4 > 22500.0) && !this.dragon.horizontalCollision && !this.dragon.verticalCollision) {
         this.dragon.setHealth(1.0F);
      } else {
         this.dragon.setHealth(0.0F);
      }
   }

   @Override
   public void begin() {
      this.targetLocation = null;
      this.time = 0;
   }

   @Override
   public float getFlySpeed() {
      return 3.0F;
   }

   @Nullable
   @Override
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   @Override
   public EnderDragonPhase<DragonDeathPhase> getPhase() {
      return EnderDragonPhase.DYING;
   }
}
