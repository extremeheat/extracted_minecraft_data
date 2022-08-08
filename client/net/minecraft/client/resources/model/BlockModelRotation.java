package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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

   private static final int DEGREES = 360;
   private static final Map<Integer, BlockModelRotation> BY_INDEX = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
      return var0.index;
   }, (var0) -> {
      return var0;
   }));
   private final Transformation transformation;
   private final OctahedralGroup actualRotation;
   private final int index;

   private static int getIndex(int var0, int var1) {
      return var0 * 360 + var1;
   }

   private BlockModelRotation(int var3, int var4) {
      this.index = getIndex(var3, var4);
      Quaternion var5 = Vector3f.YP.rotationDegrees((float)(-var4));
      var5.mul(Vector3f.XP.rotationDegrees((float)(-var3)));
      OctahedralGroup var6 = OctahedralGroup.IDENTITY;

      int var7;
      for(var7 = 0; var7 < var4; var7 += 90) {
         var6 = var6.compose(OctahedralGroup.ROT_90_Y_NEG);
      }

      for(var7 = 0; var7 < var3; var7 += 90) {
         var6 = var6.compose(OctahedralGroup.ROT_90_X_NEG);
      }

      this.transformation = new Transformation((Vector3f)null, var5, (Vector3f)null, (Quaternion)null);
      this.actualRotation = var6;
   }

   public Transformation getRotation() {
      return this.transformation;
   }

   public static BlockModelRotation by(int var0, int var1) {
      return (BlockModelRotation)BY_INDEX.get(getIndex(Mth.positiveModulo(var0, 360), Mth.positiveModulo(var1, 360)));
   }

   public OctahedralGroup actualRotation() {
      return this.actualRotation;
   }

   // $FF: synthetic method
   private static BlockModelRotation[] $values() {
      return new BlockModelRotation[]{X0_Y0, X0_Y90, X0_Y180, X0_Y270, X90_Y0, X90_Y90, X90_Y180, X90_Y270, X180_Y0, X180_Y90, X180_Y180, X180_Y270, X270_Y0, X270_Y90, X270_Y180, X270_Y270};
   }
}
