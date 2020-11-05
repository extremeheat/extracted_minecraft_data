package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonStrafePlayerPhase extends AbstractDragonPhaseInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private int fireballCharge;
   private Path currentPath;
   private Vec3 targetLocation;
   private LivingEntity attackTarget;
   private boolean holdingPatternClockwise;

   public DragonStrafePlayerPhase(EnderDragon var1) {
      super(var1);
   }

   public void doServerTick() {
      if (this.attackTarget == null) {
         LOGGER.warn("Skipping player strafe phase because no player was found");
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      } else {
         double var1;
         double var3;
         double var9;
         if (this.currentPath != null && this.currentPath.isDone()) {
            var1 = this.attackTarget.getX();
            var3 = this.attackTarget.getZ();
            double var5 = var1 - this.dragon.getX();
            double var7 = var3 - this.dragon.getZ();
            var9 = (double)Mth.sqrt(var5 * var5 + var7 * var7);
            double var11 = Math.min(0.4000000059604645D + var9 / 80.0D - 1.0D, 10.0D);
            this.targetLocation = new Vec3(var1, this.attackTarget.getY() + var11, var3);
         }

         var1 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         if (var1 < 100.0D || var1 > 22500.0D) {
            this.findNewTarget();
         }

         var3 = 64.0D;
         if (this.attackTarget.distanceToSqr(this.dragon) < 4096.0D) {
            if (this.dragon.canSee(this.attackTarget)) {
               ++this.fireballCharge;
               Vec3 var25 = (new Vec3(this.attackTarget.getX() - this.dragon.getX(), 0.0D, this.attackTarget.getZ() - this.dragon.getZ())).normalize();
               Vec3 var6 = (new Vec3((double)Mth.sin(this.dragon.yRot * 0.017453292F), 0.0D, (double)(-Mth.cos(this.dragon.yRot * 0.017453292F)))).normalize();
               float var26 = (float)var6.dot(var25);
               float var8 = (float)(Math.acos((double)var26) * 57.2957763671875D);
               var8 += 0.5F;
               if (this.fireballCharge >= 5 && var8 >= 0.0F && var8 < 10.0F) {
                  var9 = 1.0D;
                  Vec3 var27 = this.dragon.getViewVector(1.0F);
                  double var12 = this.dragon.head.getX() - var27.x * 1.0D;
                  double var14 = this.dragon.head.getY(0.5D) + 0.5D;
                  double var16 = this.dragon.head.getZ() - var27.z * 1.0D;
                  double var18 = this.attackTarget.getX() - var12;
                  double var20 = this.attackTarget.getY(0.5D) - var14;
                  double var22 = this.attackTarget.getZ() - var16;
                  if (!this.dragon.isSilent()) {
                     this.dragon.level.levelEvent((Player)null, 1017, this.dragon.blockPosition(), 0);
                  }

                  DragonFireball var24 = new DragonFireball(this.dragon.level, this.dragon, var18, var20, var22);
                  var24.moveTo(var12, var14, var16, 0.0F, 0.0F);
                  this.dragon.level.addFreshEntity(var24);
                  this.fireballCharge = 0;
                  if (this.currentPath != null) {
                     while(!this.currentPath.isDone()) {
                        this.currentPath.advance();
                     }
                  }

                  this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
               }
            } else if (this.fireballCharge > 0) {
               --this.fireballCharge;
            }
         } else if (this.fireballCharge > 0) {
            --this.fireballCharge;
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
            ++var2;
         } else {
            --var2;
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

         this.currentPath = this.dragon.findPath(var1, var2, (Node)null);
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
         } while(var4 < (double)var1.getY());

         this.targetLocation = new Vec3(var2, var4, var6);
      }

   }

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
      double var10 = (double)Mth.sqrt(var6 * var6 + var8 * var8);
      double var12 = Math.min(0.4000000059604645D + var10 / 80.0D - 1.0D, 10.0D);
      int var14 = Mth.floor(this.attackTarget.getY() + var12);
      Node var15 = new Node(var4, var14, var5);
      this.currentPath = this.dragon.findPath(var2, var3, var15);
      if (this.currentPath != null) {
         this.currentPath.advance();
         this.navigateToNextPathNode();
      }

   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
      return EnderDragonPhase.STRAFE_PLAYER;
   }
}
