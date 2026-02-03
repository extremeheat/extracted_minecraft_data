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

   public static Transformation blockCenterToCorner(final Transformation transform) {
      Matrix4f ret = (new Matrix4f()).translation(0.5F, 0.5F, 0.5F);
      ret.mul(transform.getMatrix());
      ret.translate(-0.5F, -0.5F, -0.5F);
      return new Transformation(ret);
   }

   public static Transformation blockCornerToCenter(final Transformation transform) {
      Matrix4f ret = (new Matrix4f()).translation(-0.5F, -0.5F, -0.5F);
      ret.mul(transform.getMatrix());
      ret.translate(0.5F, 0.5F, 0.5F);
      return new Transformation(ret);
   }

   public static Transformation getFaceTransformation(final Transformation transformation, final Direction originalSide) {
      if (MatrixUtil.isIdentity(transformation.getMatrix())) {
         return transformation;
      } else {
         Transformation faceAction = (Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(originalSide);
         faceAction = transformation.compose(faceAction);
         Vector3f transformedNormal = faceAction.getMatrix().transformDirection(new Vector3f(0.0F, 0.0F, 1.0F));
         Direction newSide = Direction.getApproximateNearest(transformedNormal.x, transformedNormal.y, transformedNormal.z);
         return ((Transformation)VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(newSide)).compose(faceAction);
      }
   }

   static {
      VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Maps.newEnumMap(Map.of(Direction.SOUTH, Transformation.identity(), Direction.EAST, new Transformation((Vector3fc)null, (new Quaternionf()).rotateY(1.5707964F), (Vector3fc)null, (Quaternionfc)null), Direction.WEST, new Transformation((Vector3fc)null, (new Quaternionf()).rotateY(-1.5707964F), (Vector3fc)null, (Quaternionfc)null), Direction.NORTH, new Transformation((Vector3fc)null, (new Quaternionf()).rotateY(3.1415927F), (Vector3fc)null, (Quaternionfc)null), Direction.UP, new Transformation((Vector3fc)null, (new Quaternionf()).rotateX(-1.5707964F), (Vector3fc)null, (Quaternionfc)null), Direction.DOWN, new Transformation((Vector3fc)null, (new Quaternionf()).rotateX(1.5707964F), (Vector3fc)null, (Quaternionfc)null)));
      VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Maps.newEnumMap(Util.mapValues(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL, Transformation::inverse));
   }
}
