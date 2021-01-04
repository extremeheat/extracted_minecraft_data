package net.minecraft.client.resources.model;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public enum BlockModelRotation implements ModelState {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map<Integer, BlockModelRotation> BY_INDEX = (Map)Arrays.stream(values()).sorted(Comparator.comparingInt((var0) -> {
      return var0.index;
   })).collect(Collectors.toMap((var0) -> {
      return var0.index;
   }, (var0) -> {
      return var0;
   }));
   private final int index;
   private final Quaternion rotation;
   private final int xSteps;
   private final int ySteps;

   private static int getIndex(int var0, int var1) {
      return var0 * 360 + var1;
   }

   private BlockModelRotation(int var3, int var4) {
      this.index = getIndex(var3, var4);
      Quaternion var5 = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-var4), true);
      var5.mul(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-var3), true));
      this.rotation = var5;
      this.xSteps = Mth.abs(var3 / 90);
      this.ySteps = Mth.abs(var4 / 90);
   }

   public BlockModelRotation getRotation() {
      return this;
   }

   public Quaternion getRotationQuaternion() {
      return this.rotation;
   }

   public Direction rotate(Direction var1) {
      Direction var2 = var1;

      int var3;
      for(var3 = 0; var3 < this.xSteps; ++var3) {
         var2 = var2.getClockWise(Direction.Axis.X);
      }

      if (var2.getAxis() != Direction.Axis.Y) {
         for(var3 = 0; var3 < this.ySteps; ++var3) {
            var2 = var2.getClockWise(Direction.Axis.Y);
         }
      }

      return var2;
   }

   public int rotateVertexIndex(Direction var1, int var2) {
      int var3 = var2;
      if (var1.getAxis() == Direction.Axis.X) {
         var3 = (var2 + this.xSteps) % 4;
      }

      Direction var4 = var1;

      for(int var5 = 0; var5 < this.xSteps; ++var5) {
         var4 = var4.getClockWise(Direction.Axis.X);
      }

      if (var4.getAxis() == Direction.Axis.Y) {
         var3 = (var3 + this.ySteps) % 4;
      }

      return var3;
   }

   public static BlockModelRotation by(int var0, int var1) {
      return (BlockModelRotation)BY_INDEX.get(getIndex(Mth.positiveModulo(var0, 360), Mth.positiveModulo(var1, 360)));
   }
}
