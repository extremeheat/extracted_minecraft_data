package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import org.slf4j.Logger;

public class BlockMath {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Util.make(Maps.newEnumMap(Direction.class), var0 -> {
      var0.put(Direction.SOUTH, Transformation.identity());
      var0.put(Direction.EAST, new Transformation(null, Vector3f.YP.rotationDegrees(90.0F), null, null));
      var0.put(Direction.WEST, new Transformation(null, Vector3f.YP.rotationDegrees(-90.0F), null, null));
      var0.put(Direction.NORTH, new Transformation(null, Vector3f.YP.rotationDegrees(180.0F), null, null));
      var0.put(Direction.UP, new Transformation(null, Vector3f.XP.rotationDegrees(-90.0F), null, null));
      var0.put(Direction.DOWN, new Transformation(null, Vector3f.XP.rotationDegrees(90.0F), null, null));
   });
   public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Util.make(Maps.newEnumMap(Direction.class), var0 -> {
      for(Direction var4 : Direction.values()) {
         var0.put(var4, VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(var4).inverse());
      }
   });

   public BlockMath() {
      super();
   }

   public static Transformation blockCenterToCorner(Transformation var0) {
      Matrix4f var1 = Matrix4f.createTranslateMatrix(0.5F, 0.5F, 0.5F);
      var1.multiply(var0.getMatrix());
      var1.multiply(Matrix4f.createTranslateMatrix(-0.5F, -0.5F, -0.5F));
      return new Transformation(var1);
   }

   public static Transformation blockCornerToCenter(Transformation var0) {
      Matrix4f var1 = Matrix4f.createTranslateMatrix(-0.5F, -0.5F, -0.5F);
      var1.multiply(var0.getMatrix());
      var1.multiply(Matrix4f.createTranslateMatrix(0.5F, 0.5F, 0.5F));
      return new Transformation(var1);
   }

   public static Transformation getUVLockTransform(Transformation var0, Direction var1, Supplier<String> var2) {
      Direction var3 = Direction.rotate(var0.getMatrix(), var1);
      Transformation var4 = var0.inverse();
      if (var4 == null) {
         LOGGER.warn((String)var2.get());
         return new Transformation(null, null, new Vector3f(0.0F, 0.0F, 0.0F), null);
      } else {
         Transformation var5 = VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(var1).compose(var4).compose(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(var3));
         return blockCenterToCorner(var5);
      }
   }
}
