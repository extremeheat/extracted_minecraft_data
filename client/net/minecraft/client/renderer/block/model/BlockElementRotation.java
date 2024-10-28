package net.minecraft.client.renderer.block.model;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

public record BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
   public BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
      super();
      this.origin = origin;
      this.axis = axis;
      this.angle = angle;
      this.rescale = rescale;
   }

   public Vector3f origin() {
      return this.origin;
   }

   public Direction.Axis axis() {
      return this.axis;
   }

   public float angle() {
      return this.angle;
   }

   public boolean rescale() {
      return this.rescale;
   }
}
