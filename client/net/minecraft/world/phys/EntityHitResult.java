package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class EntityHitResult extends HitResult {
   private final Entity entity;

   public EntityHitResult(Entity var1) {
      this(var1, var1.position());
   }

   public EntityHitResult(Entity var1, Vec3 var2) {
      super(var2);
      this.entity = var1;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public HitResult.Type getType() {
      return HitResult.Type.ENTITY;
   }
}
