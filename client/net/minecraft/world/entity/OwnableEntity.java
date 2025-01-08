package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public interface OwnableEntity {
   @Nullable
   EntityReference<LivingEntity> getOwnerReference();

   Level level();

   @Nullable
   default LivingEntity getOwner() {
      return (LivingEntity)EntityReference.get(this.getOwnerReference(), this.level(), LivingEntity.class);
   }
}
