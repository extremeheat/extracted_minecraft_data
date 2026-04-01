package net.minecraft.world.entity.livingblock.behavior;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.phys.Vec3;

public class LaunchCollidingBlockBehavior implements LivingBlockBehavior {
   private final double launchVelocity;
   private final double minDistance;
   private final List<LivingBlock> launchedBlocks = new ArrayList();

   public LaunchCollidingBlockBehavior(final double launchVelocity, final double minDistance) {
      super();
      this.launchVelocity = launchVelocity;
      this.minDistance = minDistance;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return true;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      for(LivingBlock collidingEntity : level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.minDistance), (otherEntity) -> otherEntity != entity)) {
         Vec3 deltaMovement = collidingEntity.getDeltaMovement();
         collidingEntity.setDeltaMovement(deltaMovement.x, this.launchVelocity, deltaMovement.z);
         collidingEntity.needsSync = true;
         if (!this.launchedBlocks.contains(collidingEntity)) {
            this.launchedBlocks.add(collidingEntity);
         }
      }

      Iterator<LivingBlock> iterator = this.launchedBlocks.iterator();

      while(iterator.hasNext()) {
         LivingBlock launchedBlock = (LivingBlock)iterator.next();
         if (launchedBlock.isAlive() && !(launchedBlock.getDeltaMovement().y <= 0.0)) {
            level.sendParticles(ParticleTypes.FIREWORK, true, true, launchedBlock.getX(), launchedBlock.getY(), launchedBlock.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
         } else {
            iterator.remove();
         }
      }

      return true;
   }

   public static LivingBlockBehaviorType launchCollidingBlocks(final double launchVelocity, final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new LaunchCollidingBlockBehavior(launchVelocity, minDistance)));
   }
}
