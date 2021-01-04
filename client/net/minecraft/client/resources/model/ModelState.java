package net.minecraft.client.resources.model;

public interface ModelState {
   default BlockModelRotation getRotation() {
      return BlockModelRotation.X0_Y0;
   }

   default boolean isUvLocked() {
      return false;
   }
}
