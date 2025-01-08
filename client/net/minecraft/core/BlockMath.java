package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.Map;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class BlockMath {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   public static Transformation getUVLockTransform(Transformation var0, Direction var1) {
      Direction var2 = Direction.rotate(var0.getMatrix(), var1);
      Transformation var3 = var0.inverse();
      if (var3 == null) {
         LOGGER.debug("Failed to invert transformation {}", var0);
         return Transformation.identity();
      } else {
         Transformation var4 = ((Transformation)VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(var1)).compose(var3).compose((Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(var2));
         return blockCenterToCorner(var4);
      }
   }

   static {
      VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Maps.newEnumMap(Map.of(Direction.SOUTH, Transformation.identity(), Direction.EAST, new Transformation((Vector3f)null, (new Quaternionf()).rotateY(1.5707964F), (Vector3f)null, (Quaternionf)null), Direction.WEST, new Transformation((Vector3f)null, (new Quaternionf()).rotateY(-1.5707964F), (Vector3f)null, (Quaternionf)null), Direction.NORTH, new Transformation((Vector3f)null, (new Quaternionf()).rotateY(3.1415927F), (Vector3f)null, (Quaternionf)null), Direction.UP, new Transformation((Vector3f)null, (new Quaternionf()).rotateX(-1.5707964F), (Vector3f)null, (Quaternionf)null), Direction.DOWN, new Transformation((Vector3f)null, (new Quaternionf()).rotateX(1.5707964F), (Vector3f)null, (Quaternionf)null)));
      VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Maps.newEnumMap(Util.mapValues(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL, Transformation::inverse));
   }
}
