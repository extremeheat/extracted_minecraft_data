package net.minecraft.client.renderer.block.model;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

public class BlockElementRotation {
   public final Vector3f origin;
   public final Direction.Axis axis;
   public final float angle;
   public final boolean rescale;

   public BlockElementRotation(Vector3f var1, Direction.Axis var2, float var3, boolean var4) {
      this.origin = var1;
      this.axis = var2;
      this.angle = var3;
      this.rescale = var4;
   }
}
