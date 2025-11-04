package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.math.MatrixUtil;
import com.mojang.math.Transformation;
import java.util.Map;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BlockMath {
   private static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL;
   private static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL;

   public BlockMath() {
      super();
   }

   public static Transformation blockCenterToCorner(Transformation var0) {
      Matrix4f var1 = (new Matrix4f()).translation(0.5F, 0.5F, 0.5F);
      var1.mul(var0.getMatrix());
      var1.translate(-0.5F, -0.5F, -0.5F);
      return new Transformation(var1);
   }

   public static Transformation blockCornerToCenter(Transformation var0) {
      Matrix4f var1 = (new Matrix4f()).translation(-0.5F, -0.5F, -0.5F);
      var1.mul(var0.getMatrix());
      var1.translate(0.5F, 0.5F, 0.5F);
      return new Transformation(var1);
   }

   public static Transformation getFaceTransformation(Transformation var0, Direction var1) {
      if (MatrixUtil.isIdentity(var0.getMatrix())) {
         return var0;
      } else {
         Transformation var2 = (Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(var1);
         var2 = var0.compose(var2);
         Vector3f var3 = var2.getMatrix().transformDirection(new Vector3f(0.0F, 0.0F, 1.0F));
         Direction var4 = Direction.getApproximateNearest(var3.x, var3.y, var3.z);
         return ((Transformation)VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(var4)).compose(var2);
      }
   }

   static {
      VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Maps.newEnumMap(Map.of(Direction.SOUTH, Transformation.identity(), Direction.EAST, new Transformation((Vector3fc)null, (new Quaternionf()).rotateY(1.5707964F), (Vector3fc)null, (Quaternionfc)null), Direction.WEST, new Transformation((Vector3fc)null, (new Quaternionf()).rotateY(-1.5707964F), (Vector3fc)null, (Quaternionfc)null), Direction.NORTH, new Transformation((Vector3fc)null, (new Quaternionf()).rotateY(3.1415927F), (Vector3fc)null, (Quaternionfc)null), Direction.UP, new Transformation((Vector3fc)null, (new Quaternionf()).rotateX(-1.5707964F), (Vector3fc)null, (Quaternionfc)null), Direction.DOWN, new Transformation((Vector3fc)null, (new Quaternionf()).rotateX(1.5707964F), (Vector3fc)null, (Quaternionfc)null)));
      VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Maps.newEnumMap(Util.mapValues(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL, Transformation::inverse));
   }
}
