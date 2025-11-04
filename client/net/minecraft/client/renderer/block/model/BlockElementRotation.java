package net.minecraft.client.renderer.block.model;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record BlockElementRotation(Vector3fc origin, Direction.Axis axis, float angle, boolean rescale, Matrix4fc transform) {
   private static final Vector3fc NO_RESCALE = new Vector3f(1.0F, 1.0F, 1.0F);

   public BlockElementRotation(Vector3fc var1, Direction.Axis var2, float var3, boolean var4) {
      this(var1, var2, var3, var4, computeTransform(var2, var3, var4));
   }

   public BlockElementRotation(Vector3fc var1, Direction.Axis var2, float var3, boolean var4, Matrix4fc var5) {
      super();
      this.origin = var1;
      this.axis = var2;
      this.angle = var3;
      this.rescale = var4;
      this.transform = var5;
   }

   private static Matrix4f computeTransform(Direction.Axis var0, float var1, boolean var2) {
      Vector3fc var3 = var0.getPositive().getUnitVec3f();
      return (new Matrix4f()).rotation(var1 * 0.017453292F, var3).scale(computeRescale(var2, var1, var0));
   }

   private static Vector3fc computeRescale(boolean var0, float var1, Direction.Axis var2) {
      if (var0 && var1 != 0.0F) {
         float var3 = Math.abs(var1);
         float var4 = 1.0F / Mth.cos((double)(var3 * 0.017453292F));
         Vector3f var10000;
         switch (var2) {
            case X -> var10000 = new Vector3f(1.0F, var4, var4);
            case Y -> var10000 = new Vector3f(var4, 1.0F, var4);
            case Z -> var10000 = new Vector3f(var4, var4, 1.0F);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      } else {
         return NO_RESCALE;
      }
   }
}
