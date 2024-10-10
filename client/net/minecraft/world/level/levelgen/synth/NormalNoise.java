package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.util.RandomSource;

public class NormalNoise {
   private static final double INPUT_FACTOR = 1.0181268882175227;
   private static final double TARGET_DEVIATION = 0.3333333333333333;
   private final double valueFactor;
   private final PerlinNoise first;
   private final PerlinNoise second;
   private final double maxValue;
   private final NormalNoise.NoiseParameters parameters;

   @Deprecated
   public static NormalNoise createLegacyNetherBiome(RandomSource var0, NormalNoise.NoiseParameters var1) {
      return new NormalNoise(var0, var1, false);
   }

   public static NormalNoise create(RandomSource var0, int var1, double... var2) {
      return create(var0, new NormalNoise.NoiseParameters(var1, new DoubleArrayList(var2)));
   }

   public static NormalNoise create(RandomSource var0, NormalNoise.NoiseParameters var1) {
      return new NormalNoise(var0, var1, true);
   }

   private NormalNoise(RandomSource var1, NormalNoise.NoiseParameters var2, boolean var3) {
      super();
      int var4 = var2.firstOctave;
      DoubleList var5 = var2.amplitudes;
      this.parameters = var2;
      if (var3) {
         this.first = PerlinNoise.create(var1, var4, var5);
         this.second = PerlinNoise.create(var1, var4, var5);
      } else {
         this.first = PerlinNoise.createLegacyForLegacyNetherBiome(var1, var4, var5);
         this.second = PerlinNoise.createLegacyForLegacyNetherBiome(var1, var4, var5);
      }

      int var6 = 2147483647;
      int var7 = -2147483648;
      DoubleListIterator var8 = var5.iterator();

      while (var8.hasNext()) {
         int var9 = var8.nextIndex();
         double var10 = var8.nextDouble();
         if (var10 != 0.0) {
            var6 = Math.min(var6, var9);
            var7 = Math.max(var7, var9);
         }
      }

      this.valueFactor = 0.16666666666666666 / expectedDeviation(var7 - var6);
      this.maxValue = (this.first.maxValue() + this.second.maxValue()) * this.valueFactor;
   }

   public double maxValue() {
      return this.maxValue;
   }

   private static double expectedDeviation(int var0) {
      return 0.1 * (1.0 + 1.0 / (double)(var0 + 1));
   }

   public double getValue(double var1, double var3, double var5) {
      double var7 = var1 * 1.0181268882175227;
      double var9 = var3 * 1.0181268882175227;
      double var11 = var5 * 1.0181268882175227;
      return (this.first.getValue(var1, var3, var5) + this.second.getValue(var7, var9, var11)) * this.valueFactor;
   }

   public NormalNoise.NoiseParameters parameters() {
      return this.parameters;
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder var1) {
      var1.append("NormalNoise {");
      var1.append("first: ");
      this.first.parityConfigString(var1);
      var1.append(", second: ");
      this.second.parityConfigString(var1);
      var1.append("}");
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
