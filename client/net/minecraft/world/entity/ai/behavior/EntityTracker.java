package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.Vec3;

public class EntityTracker implements PositionTracker {
   private final Entity entity;
   private final boolean trackEyeHeight;

   public EntityTracker(Entity var1, boolean var2) {
      super();
      this.entity = var1;
      this.trackEyeHeight = var2;
   }

   @Override
   public Vec3 currentPosition() {
      return this.trackEyeHeight ? this.entity.position().add(0.0, (double)this.entity.getEyeHeight(), 0.0) : this.entity.position();
   }

   @Override
   public BlockPos currentBlockPosition() {
      return this.entity.blockPosition();
   }

   @Override
   public boolean isVisibleBy(LivingEntity var1) {
      Entity var3 = this.entity;
      if (var3 instanceof LivingEntity var2) {
         if (!((LivingEntity)var2).isAlive()) {
            return false;
         } else {
            Optional var4 = var1.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
            return var4.isPresent() && ((NearestVisibleLivingEntities)var4.get()).contains((LivingEntity)var2);
         }
      } else {
         return true;
      }
   }

   public Entity getEntity() {
      return this.entity;
   }

   @Override
   public String toString() {
      return "EntityTracker for " + this.entity;
   }
}
