package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
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
      double var4 = this.dragon.head.getY(0.5D);
      double var6 = this.dragon.head.getZ();

      for(int var8 = 0; var8 < 8; ++var8) {
         Random var9 = this.dragon.getRandom();
         double var10 = var2 + var9.nextGaussian() / 2.0D;
         double var12 = var4 + var9.nextGaussian() / 2.0D;
         double var14 = var6 + var9.nextGaussian() / 2.0D;
         Vec3 var16 = this.dragon.getDeltaMovement();
         this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, var10, var12, var14, -var1.field_414 * 0.07999999821186066D + var16.field_414, -var1.field_415 * 0.30000001192092896D + var16.field_415, -var1.field_416 * 0.07999999821186066D + var16.field_416);
         var1.yRot(0.19634955F);
      }

   }

   public void doServerTick() {
      if (this.targetLocation == null) {
         this.targetLocation = Vec3.atBottomCenterOf(this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
      }

      if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0D) {
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
