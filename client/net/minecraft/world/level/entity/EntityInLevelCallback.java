package net.minecraft.world.level.entity;

import net.minecraft.world.entity.Entity;

public interface EntityInLevelCallback {
   EntityInLevelCallback NULL = new EntityInLevelCallback() {
      public void onMove() {
      }

      public void onRemove(final Entity.RemovalReason reason) {
      }
   };

   void onMove();

   void onRemove(final Entity.RemovalReason reason);
}
