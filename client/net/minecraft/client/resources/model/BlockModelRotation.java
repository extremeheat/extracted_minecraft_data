package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Transformation;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

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
   private static final Map<Integer, BlockModelRotation> BY_INDEX = Arrays.stream(values())
      .collect(Collectors.toMap(var0 -> var0.index, var0 -> (BlockModelRotation)var0));
   private final Transformation transformation;
   private final OctahedralGroup actualRotation;
   private final int index;

   private static int getIndex(int var0, int var1) {
      return var0 * 360 + var1;
   }

   private BlockModelRotation(final int nullxx, final int nullxxx) {
      this.index = getIndex(nullxx, nullxxx);
      Quaternionf var5 = new Quaternionf().rotateYXZ((float)(-nullxxx) * 0.017453292F, (float)(-nullxx) * 0.017453292F, 0.0F);
      OctahedralGroup var6 = OctahedralGroup.IDENTITY;

      for (byte var7 = 0; var7 < nullxxx; var7 += 90) {
         var6 = var6.compose(OctahedralGroup.ROT_90_Y_NEG);
      }

      for (byte var8 = 0; var8 < nullxx; var8 += 90) {
         var6 = var6.compose(OctahedralGroup.ROT_90_X_NEG);
      }

      this.transformation = new Transformation(null, var5, null, null);
      this.actualRotation = var6;
   }

   @Override
   public Transformation getRotation() {
      return this.transformation;
   }

   public static BlockModelRotation by(int var0, int var1) {
      return BY_INDEX.get(getIndex(Mth.positiveModulo(var0, 360), Mth.positiveModulo(var1, 360)));
   }

   public OctahedralGroup actualRotation() {
      return this.actualRotation;
   }
}
