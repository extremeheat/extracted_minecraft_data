package net.minecraft.client.renderer.block.model;

import net.minecraft.core.Direction;
import org.joml.Vector3f;

public record BlockElementRotation(Vector3f a, Direction.Axis b, float c, boolean d) {
   private final Vector3f origin;
   private final Direction.Axis axis;
   private final float angle;
   private final boolean rescale;

   public BlockElementRotation(Vector3f var1, Direction.Axis var2, float var3, boolean var4) {
      super();
      this.origin = var1;
      this.axis = var2;
      this.angle = var3;
      this.rescale = var4;
   }
}
