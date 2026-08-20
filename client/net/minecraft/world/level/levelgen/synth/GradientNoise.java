package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public abstract class GradientNoise implements Noise {
   protected static final Gradient[] GRADIENT = new Gradient[]{new Gradient(1, 1, 0), new Gradient(-1, 1, 0), new Gradient(1, -1, 0), new Gradient(-1, -1, 0), new Gradient(1, 0, 1), new Gradient(-1, 0, 1), new Gradient(1, 0, -1), new Gradient(-1, 0, -1), new Gradient(0, 1, 1), new Gradient(0, -1, 1), new Gradient(0, 1, -1), new Gradient(0, -1, -1), new Gradient(1, 1, 0), new Gradient(0, -1, 1), new Gradient(-1, 1, 0), new Gradient(0, -1, -1)};
   private static final int ROUND_OFF = 33554432;
   protected final byte[] perms;
   protected final double offsetX;
   protected final double offsetY;
   protected final double offsetZ;

   protected GradientNoise(final RandomSource random) {
      this(random, 256.0);
   }

   protected GradientNoise(final RandomSource random, final double noiseOffsetScale) {
      super();
      this.perms = new byte[256];
      this.offsetX = random.nextDouble() * noiseOffsetScale;
      this.offsetY = random.nextDouble() * noiseOffsetScale;
      this.offsetZ = random.nextDouble() * noiseOffsetScale;

      for(int i = 0; i < 256; ++i) {
         this.perms[i] = (byte)i;
      }

      for(int i = 0; i < 256; ++i) {
         int offset = random.nextInt(256 - i);
         byte tmp = this.perms[i];
         this.perms[i] = this.perms[offset + i];
         this.perms[offset + i] = tmp;
      }

   }

   protected int permute(final int x) {
      return this.perms[x & 255] & 255;
   }

   protected Gradient permuteToGrad(final int x) {
      return GRADIENT[this.permute(x) & 15];
   }

   protected static float gradDot(final int hash, final float x, final float y, final float z) {
      return GRADIENT[hash & 15].dot(x, y, z);
   }

   protected static double wrap(final double x) {
      return x - (double)Mth.lfloor(x / 3.3554432E7 + 0.5) * 3.3554432E7;
   }

   protected static record Gradient(int x, int y, int z) {
      protected Gradient {
         super();
      }

      public double dot(final double x, final double y, final double z) {
         return (double)this.x * x + (double)this.y * y + (double)this.z * z;
      }

      public float dot(final float x, final float y, final float z) {
         return (float)this.x * x + (float)this.y * y + (float)this.z * z;
      }

      public float dotXz(final float x, final float z) {
         return (float)this.x * x + (float)this.z * z;
      }
   }
}
