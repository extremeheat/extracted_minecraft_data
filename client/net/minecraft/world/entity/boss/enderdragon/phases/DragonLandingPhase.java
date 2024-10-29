package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonLandingPhase extends AbstractDragonPhaseInstance {
   @Nullable
   private Vec3 targetLocation;

   public DragonLandingPhase(EnderDragon var1) {
      super(var1);
   }

   public void doClientTick() {
      Vec3 var1 = this.dragon.getHeadLookVector(1.0F).normalize();
      var1.yRot(-0.7853982F);
      double var2 = this.dragon.head.getX();
      double var4 = this.dragon.head.getY(0.5);
      double var6 = this.dragon.head.getZ();

      for(int var8 = 0; var8 < 8; ++var8) {
         RandomSource var9 = this.dragon.getRandom();
         double var10 = var2 + var9.nextGaussian() / 2.0;
         double var12 = var4 + var9.nextGaussian() / 2.0;
         double var14 = var6 + var9.nextGaussian() / 2.0;
         Vec3 var16 = this.dragon.getDeltaMovement();
         this.dragon.level().addParticle(ParticleTypes.DRAGON_BREATH, var10, var12, var14, -var1.x * 0.07999999821186066 + var16.x, -var1.y * 0.30000001192092896 + var16.y, -var1.z * 0.07999999821186066 + var16.z);
         var1.yRot(0.19634955F);
      }

   }

   public void doServerTick(ServerLevel var1) {
      if (this.targetLocation == null) {
         this.targetLocation = Vec3.atBottomCenterOf(var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.dragon.getFightOrigin())));
      }

      if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0) {
         ((DragonSittingFlamingPhase)this.dragon.getPhaseManager().getPhase(EnderDragonPhase.SITTING_FLAMING)).resetFlameCount();
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
      }

   }

   public float getFlySpeed() {
      return 1.5F;
   }

   public float getTurnSpeed() {
      float var1 = (float)this.dragon.getDeltaMovement().horizontalDistance() + 1.0F;
      float var2 = Math.min(var1, 40.0F);
      return var2 / var1;
   }

   public void begin() {
      this.targetLocation = null;
   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase<DragonLandingPhase> getPhase() {
      return EnderDragonPhase.LANDING;
   }
}
