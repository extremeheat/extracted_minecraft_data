package net.minecraft.world.level.levelgen.densityfunction;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public enum DistanceMetric implements StringRepresentable {
   EUCLIDEAN("euclidean"),
   EUCLIDEAN_SQUARED("euclidean_squared"),
   MANHATTAN("manhattan"),
   CHEBYSHEV("chebyshev");

   public static final Codec<DistanceMetric> CODEC = StringRepresentable.<DistanceMetric>fromEnum(DistanceMetric::values);
   private final String name;

   private DistanceMetric(final String name) {
      this.name = name;
   }

   public float compute(final float deltaX, final float deltaY, final float deltaZ) {
      float var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = Mth.length(deltaX, deltaY, deltaZ);
         case 1 -> var10000 = Mth.lengthSquared(deltaX, deltaY, deltaZ);
         case 2 -> var10000 = Math.abs(deltaX) + Math.abs(deltaY) + Math.abs(deltaZ);
         case 3 -> var10000 = Math.max(Math.max(Math.abs(deltaX), Math.abs(deltaY)), Math.abs(deltaZ));
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float compute(final float deltaX, final float deltaZ) {
      float var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = Mth.length(deltaX, deltaZ);
         case 1 -> var10000 = Mth.lengthSquared(deltaX, deltaZ);
         case 2 -> var10000 = Math.abs(deltaX) + Math.abs(deltaZ);
         case 3 -> var10000 = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static DistanceMetric[] $values() {
      return new DistanceMetric[]{EUCLIDEAN, EUCLIDEAN_SQUARED, MANHATTAN, CHEBYSHEV};
   }
}
