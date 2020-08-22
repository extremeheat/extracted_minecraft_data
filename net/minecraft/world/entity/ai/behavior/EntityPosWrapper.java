package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;

public class EntityPosWrapper implements PositionWrapper {
   private final Entity entity;

   public EntityPosWrapper(Entity var1) {
      this.entity = var1;
   }

   public BlockPos getPos() {
      return new BlockPos(this.entity);
   }

   public Vec3 getLookAtPos() {
      return new Vec3(this.entity.getX(), this.entity.getEyeY(), this.entity.getZ());
   }

   public boolean isVisible(LivingEntity var1) {
      Optional var2 = var1.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
      return this.entity.isAlive() && var2.isPresent() && ((List)var2.get()).contains(this.entity);
   }

   public String toString() {
      return "EntityPosWrapper for " + this.entity;
   }
}
