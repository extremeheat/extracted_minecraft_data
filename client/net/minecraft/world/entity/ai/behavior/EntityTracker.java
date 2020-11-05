package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;

public class EntityTracker implements PositionTracker {
   private final Entity entity;
   private final boolean trackEyeHeight;

   public EntityTracker(Entity var1, boolean var2) {
      super();
      this.entity = var1;
      this.trackEyeHeight = var2;
   }

   public Vec3 currentPosition() {
      return this.trackEyeHeight ? this.entity.position().add(0.0D, (double)this.entity.getEyeHeight(), 0.0D) : this.entity.position();
   }

   public BlockPos currentBlockPosition() {
      return this.entity.blockPosition();
   }

   public boolean isVisibleBy(LivingEntity var1) {
      if (!(this.entity instanceof LivingEntity)) {
         return true;
      } else {
         Optional var2 = var1.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
         return this.entity.isAlive() && var2.isPresent() && ((List)var2.get()).contains(this.entity);
      }
   }

   public String toString() {
      return "EntityTracker for " + this.entity;
   }
}
