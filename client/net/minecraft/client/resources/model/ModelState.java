package net.minecraft.client.resources.model;

import com.mojang.math.Transformation;

public interface ModelState {
   default Transformation getRotation() {
      return Transformation.identity();
   }

   default boolean isUvLocked() {
      return false;
   }
}
