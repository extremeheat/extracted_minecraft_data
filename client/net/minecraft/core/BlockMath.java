package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.Map;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.slf4j.Logger;

public class BlockMath {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Util.make(Maps.newEnumMap(Direction.class), var0 -> {
      var0.put(Direction.SOUTH, Transformation.identity());
      var0.put(Direction.EAST, new Transformation(null, new Quaternionf().rotateY(1.5707964F), null, null));
      var0.put(Direction.WEST, new Transformation(null, new Quaternionf().rotateY(-1.5707964F), null, null));
      var0.put(Direction.NORTH, new Transformation(null, new Quaternionf().rotateY(3.1415927F), null, null));
      var0.put(Direction.UP, new Transformation(null, new Quaternionf().rotateX(-1.5707964F), null, null));
      var0.put(Direction.DOWN, new Transformation(null, new Quaternionf().rotateX(1.5707964F), null, null));
   });
   public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Util.make(Maps.newEnumMap(Direction.class), var0 -> {
      for (Direction var4 : Direction.values()) {
         var0.put(var4, VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(var4).inverse());
      }
   });

   public BlockMath() {
      super();
   }

   public static Transformation blockCenterToCorner(Transformation var0) {
      Matrix4f var1 = new Matrix4f().translation(0.5F, 0.5F, 0.5F);
      var1.mul(var0.getMatrix());
      var1.translate(-0.5F, -0.5F, -0.5F);
      return new Transformation(var1);
   }

   public static Transformation blockCornerToCenter(Transformation var0) {
      Matrix4f var1 = new Matrix4f().translation(-0.5F, -0.5F, -0.5F);
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
         Transformation var4 = VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(var1).compose(var3).compose(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(var2));
         return blockCenterToCorner(var4);
      }
   }
}
