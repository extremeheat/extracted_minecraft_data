package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DragonStrafePlayerPhase extends AbstractDragonPhaseInstance {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int FIREBALL_CHARGE_AMOUNT = 5;
   private int fireballCharge;
   @Nullable
   private Path currentPath;
   @Nullable
   private Vec3 targetLocation;
   @Nullable
   private LivingEntity attackTarget;
   private boolean holdingPatternClockwise;

   public DragonStrafePlayerPhase(EnderDragon var1) {
      super(var1);
   }

   @Override
   public void doServerTick() {
      if (this.attackTarget == null) {
         LOGGER.warn("Skipping player strafe phase because no player was found");
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      } else {
         if (this.currentPath != null && this.currentPath.isDone()) {
            double var1 = this.attackTarget.getX();
            double var3 = this.attackTarget.getZ();
            double var5 = var1 - this.dragon.getX();
            double var7 = var3 - this.dragon.getZ();
            double var9 = Math.sqrt(var5 * var5 + var7 * var7);
            double var11 = Math.min(0.4000000059604645 + var9 / 80.0 - 1.0, 10.0);
            this.targetLocation = new Vec3(var1, this.attackTarget.getY() + var11, var3);
         }

         double var26 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         if (var26 < 100.0 || var26 > 22500.0) {
            this.findNewTarget();
         }

         double var27 = 64.0;
         if (this.attackTarget.distanceToSqr(this.dragon) < 4096.0) {
            if (this.dragon.hasLineOfSight(this.attackTarget)) {
               this.fireballCharge++;
               Vec3 var28 = new Vec3(this.attackTarget.getX() - this.dragon.getX(), 0.0, this.attackTarget.getZ() - this.dragon.getZ()).normalize();
               Vec3 var6 = new Vec3((double)Mth.sin(this.dragon.getYRot() * 0.017453292F), 0.0, (double)(-Mth.cos(this.dragon.getYRot() * 0.017453292F)))
                  .normalize();
               float var29 = (float)var6.dot(var28);
               float var8 = (float)(Math.acos((double)var29) * 57.2957763671875);
               var8 += 0.5F;
               if (this.fireballCharge >= 5 && var8 >= 0.0F && var8 < 10.0F) {
                  double var31 = 1.0;
                  Vec3 var32 = this.dragon.getViewVector(1.0F);
                  double var12 = this.dragon.head.getX() - var32.x * 1.0;
                  double var14 = this.dragon.head.getY(0.5) + 0.5;
                  double var16 = this.dragon.head.getZ() - var32.z * 1.0;
                  double var18 = this.attackTarget.getX() - var12;
                  double var20 = this.attackTarget.getY(0.5) - var14;
                  double var22 = this.attackTarget.getZ() - var16;
                  Vec3 var24 = new Vec3(var18, var20, var22);
                  if (!this.dragon.isSilent()) {
                     this.dragon.level().levelEvent(null, 1017, this.dragon.blockPosition(), 0);
                  }

                  DragonFireball var25 = new DragonFireball(this.dragon.level(), this.dragon, var24.normalize());
                  var25.moveTo(var12, var14, var16, 0.0F, 0.0F);
                  this.dragon.level().addFreshEntity(var25);
                  this.fireballCharge = 0;
                  if (this.currentPath != null) {
                     while (!this.currentPath.isDone()) {
                        this.currentPath.advance();
                     }
                  }

                  this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
               }
            } else if (this.fireballCharge > 0) {
               this.fireballCharge--;
            }
         } else if (this.fireballCharge > 0) {
            this.fireballCharge--;
         }
      }
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int var1 = this.dragon.findClosestNode();
         int var2 = var1;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.holdingPatternClockwise = !this.holdingPatternClockwise;
            var2 = var1 + 6;
         }

         if (this.holdingPatternClockwise) {
            var2++;
         } else {
            var2--;
         }

         if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() > 0) {
            var2 %= 12;
            if (var2 < 0) {
               var2 += 12;
            }
         } else {
            var2 -= 12;
            var2 &= 7;
            var2 += 12;
         }

         this.currentPath = this.dragon.findPath(var1, var2, null);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         BlockPos var1 = this.currentPath.getNextNodePos();
         this.currentPath.advance();
         double var2 = (double)var1.getX();
         double var6 = (double)var1.getZ();

         double var4;
         do {
            var4 = (double)((float)var1.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while (var4 < (double)var1.getY());

         this.targetLocation = new Vec3(var2, var4, var6);
      }
   }

   @Override
   public void begin() {
      this.fireballCharge = 0;
      this.targetLocation = null;
      this.currentPath = null;
      this.attackTarget = null;
   }

   public void setTarget(LivingEntity var1) {
      this.attackTarget = var1;
      int var2 = this.dragon.findClosestNode();
      int var3 = this.dragon.findClosestNode(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
      int var4 = this.attackTarget.getBlockX();
      int var5 = this.attackTarget.getBlockZ();
      double var6 = (double)var4 - this.dragon.getX();
      double var8 = (double)var5 - this.dragon.getZ();
      double var10 = Math.sqrt(var6 * var6 + var8 * var8);
      double var12 = Math.min(0.4000000059604645 + var10 / 80.0 - 1.0, 10.0);
      int var14 = Mth.floor(this.attackTarget.getY() + var12);
      Node var15 = new Node(var4, var14, var5);
      this.currentPath = this.dragon.findPath(var2, var3, var15);
      if (this.currentPath != null) {
         this.currentPath.advance();
         this.navigateToNextPathNode();
      }
   }

   @Nullable
   @Override
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   @Override
   public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
      return EnderDragonPhase.STRAFE_PLAYER;
   }
}
