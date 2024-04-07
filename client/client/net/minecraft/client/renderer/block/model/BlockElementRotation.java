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
}
