package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;

public record DomainWarpFunction(DensityFunction input, DensityFunction offsetX, DensityFunction offsetY, DensityFunction offsetZ) implements DensityFunction {
   public static final MapCodec<DomainWarpFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(DomainWarpFunction::input), DensityFunction.CODEC.optionalFieldOf("offset_x", DensityFunctions.zero()).forGetter(DomainWarpFunction::offsetX), DensityFunction.CODEC.optionalFieldOf("offset_y", DensityFunctions.zero()).forGetter(DomainWarpFunction::offsetY), DensityFunction.CODEC.optionalFieldOf("offset_z", DensityFunctions.zero()).forGetter(DomainWarpFunction::offsetZ)).apply(i, DomainWarpFunction::new)).validate(DomainWarpFunction::validate);
   private static final int EXPECTED_VALUE_TO_VOLUME_COST_RATIO = 16;

   public DomainWarpFunction {
      super();
   }

   private static DataResult<DomainWarpFunction> validate(final DomainWarpFunction function) {
      return function.offsetX.equals(DensityFunctions.zero()) && function.offsetY.equals(DensityFunctions.zero()) && function.offsetZ.equals(DensityFunctions.zero()) ? DataResult.error(() -> "At least one of offset_x, offset_y, or offset_z must be specified") : DataResult.success(function);
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler input = this.input.compileSampler(context);
      DensityFunction var9 = this.offsetX;
      if (var9 instanceof ConstantFunction var3) {
         ConstantFunction var10000 = var3;

         try {
            var22 = var10000.value();
         } catch (Throwable var14) {
            throw new MatchException(var14.toString(), var14);
         }

         float offsetZValue = var22;
         if (true) {
            float offsetXValue = offsetZValue;
            var9 = this.offsetY;
            if (var9 instanceof ConstantFunction) {
               ConstantFunction var5 = (ConstantFunction)var9;
               var10000 = var5;

               try {
                  var24 = var10000.value();
               } catch (Throwable var13) {
                  throw new MatchException(var13.toString(), var13);
               }

               offsetZValue = var24;
               if (true) {
                  float offsetYValue = offsetZValue;
                  var9 = this.offsetZ;
                  if (var9 instanceof ConstantFunction) {
                     ConstantFunction var7 = (ConstantFunction)var9;
                     var10000 = var7;

                     try {
                        var26 = var10000.value();
                     } catch (Throwable var12) {
                        throw new MatchException(var12.toString(), var12);
                     }

                     offsetZValue = var26;
                     if (true) {
                        return new ConstOffsetSampler(input, (int)offsetXValue, (int)offsetYValue, (int)offsetZValue);
                     }
                  }
               }
            }
         }
      }

      DensitySampler offsetX = this.offsetX.compileSampler(context);
      DensitySampler offsetZ = this.offsetZ.compileSampler(context);
      if ((this.input.domainAxes() & 2) == 0) {
         return new FlatXzSampler(input, offsetX, offsetZ);
      } else {
         DensitySampler offsetY = this.offsetY.compileSampler(context);
         return new Sampler(input, offsetX, offsetY, offsetZ);
      }
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      DensityFunction offsetX = rule.rewrite(this.offsetX);
      DensityFunction offsetY = rule.rewrite(this.offsetY);
      DensityFunction offsetZ = rule.rewrite(this.offsetZ);
      return input == this.input && offsetX == this.offsetX && offsetY == this.offsetY && offsetZ == this.offsetZ ? this : new DomainWarpFunction(input, offsetX, offsetY, offsetZ);
   }

   public Interval range() {
      return this.input.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes() | this.offsetX.domainAxes() | this.offsetY.domainAxes() | this.offsetZ.domainAxes();
   }

   public MapCodec<DomainWarpFunction> codec() {
      return CODEC;
   }

   private static boolean shouldSampleNaively(final DensityVolume originalVolume, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
      long sizeX = (long)maxX - (long)minX + 1L;
      long sizeY = (long)maxY - (long)minY + 1L;
      long sizeZ = (long)maxZ - (long)minZ + 1L;

      try {
         long valueCount = Math.multiplyExact(Math.multiplyExact(sizeX, sizeY), sizeZ);
         return valueCount > 2147483647L || valueCount > (long)originalVolume.size() * 16L;
      } catch (ArithmeticException var15) {
         return false;
      }
   }

   public static record ConstOffsetSampler(DensitySampler input, int offsetX, int offsetY, int offsetZ) implements DensitySampler {
      public ConstOffsetSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, new DensityVolume(volume.sizeX(), volume.sizeY(), volume.sizeZ(), volume.minBlockX() + this.offsetX, volume.minBlockY() + this.offsetY, volume.minBlockZ() + this.offsetZ, volume.stepBlockX(), volume.stepBlockY(), volume.stepBlockZ()));
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.input.sampleValue(context, blockX + this.offsetX, blockY + this.offsetY, blockZ + this.offsetZ);
      }
   }

   public static record FlatXzSampler(DensitySampler input, DensitySampler offsetX, DensitySampler offsetZ) implements DensitySampler {
      public FlatXzSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         int[] inputCoordinates = new int[volume.size() * 2];
         int minInputX = 2147483647;
         int minInputZ = 2147483647;
         int maxInputX = -2147483648;
         int maxInputZ = -2147483648;
         this.offsetX.sampleVolume(context, outputBuffer, volume);
         int i = 0;

         for(int z = 0; z < volume.sizeZ(); ++z) {
            for(int x = 0; x < volume.sizeX(); ++x) {
               int blockX = volume.blockX(x);

               for(int y = 0; y < volume.sizeY(); ++y) {
                  int inputX = blockX + (int)outputBuffer.get(i);
                  minInputX = Math.min(inputX, minInputX);
                  maxInputX = Math.max(inputX, maxInputX);
                  inputCoordinates[i * 2] = inputX;
                  ++i;
               }
            }
         }

         this.offsetZ.sampleVolume(context, outputBuffer, volume);
         i = 0;

         for(int z = 0; z < volume.sizeZ(); ++z) {
            int blockZ = volume.blockZ(z);

            for(int xy = 0; xy < volume.strideZ(); ++xy) {
               int inputZ = blockZ + (int)outputBuffer.get(i);
               minInputZ = Math.min(inputZ, minInputZ);
               maxInputZ = Math.max(inputZ, maxInputZ);
               inputCoordinates[i * 2 + 1] = inputZ;
               ++i;
            }
         }

         if (DomainWarpFunction.shouldSampleNaively(volume, minInputX, 0, minInputZ, maxInputX, 0, maxInputZ)) {
            this.sampleShiftedNaive(context, outputBuffer, inputCoordinates, volume);
         } else {
            DensityVolume inputVolume = new DensityVolume(maxInputX - minInputX + 1, 1, maxInputZ - minInputZ + 1, minInputX, 0, minInputZ, 1, 1, 1);

            try (ScopedDensityBuffer inputBuffer = context.acquireBuffer(inputVolume)) {
               this.input.sampleVolume(context, inputBuffer, inputVolume);

               for(int index = 0; index < volume.size(); ++index) {
                  int inputIndex = inputVolume.indexUnchecked(inputCoordinates[index * 2] - minInputX, 0, inputCoordinates[index * 2 + 1] - minInputZ);
                  outputBuffer.set(index, inputBuffer.get(inputIndex));
               }
            }

         }
      }

      private void sampleShiftedNaive(final SamplerContext context, final DensityBuffer outputBuffer, final int[] inputCoordinates, final DensityVolume volume) {
         for(int index = 0; index < volume.size(); ++index) {
            outputBuffer.set(index, this.input.sampleValue(context, inputCoordinates[index * 2], 0, inputCoordinates[index * 2 + 1]));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         int offsetX = (int)this.offsetX.sampleValue(context, blockX, blockY, blockZ);
         int offsetZ = (int)this.offsetZ.sampleValue(context, blockX, blockY, blockZ);
         return this.input.sampleValue(context, blockX + offsetX, 0, blockZ + offsetZ);
      }
   }

   public static record Sampler(DensitySampler input, DensitySampler offsetX, DensitySampler offsetY, DensitySampler offsetZ) implements DensitySampler {
      public Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         int[] inputCoordinates = new int[volume.size() * 3];
         int minInputX = 2147483647;
         int minInputY = 2147483647;
         int minInputZ = 2147483647;
         int maxInputX = -2147483648;
         int maxInputY = -2147483648;
         int maxInputZ = -2147483648;
         this.offsetX.sampleVolume(context, outputBuffer, volume);
         int i = 0;

         for(int z = 0; z < volume.sizeZ(); ++z) {
            for(int x = 0; x < volume.sizeX(); ++x) {
               int blockX = volume.blockX(x);

               for(int y = 0; y < volume.sizeY(); ++y) {
                  int inputX = blockX + (int)outputBuffer.get(i);
                  minInputX = Math.min(inputX, minInputX);
                  maxInputX = Math.max(inputX, maxInputX);
                  inputCoordinates[i * 3] = inputX;
                  ++i;
               }
            }
         }

         this.offsetY.sampleVolume(context, outputBuffer, volume);
         i = 0;

         for(int xz = 0; xz < volume.sizeX() * volume.sizeZ(); ++xz) {
            for(int y = 0; y < volume.sizeY(); ++y) {
               int blockY = volume.blockY(y);
               int inputY = blockY + (int)outputBuffer.get(i);
               minInputY = Math.min(inputY, minInputY);
               maxInputY = Math.max(inputY, maxInputY);
               inputCoordinates[i * 3 + 1] = inputY;
               ++i;
            }
         }

         this.offsetZ.sampleVolume(context, outputBuffer, volume);
         i = 0;

         for(int z = 0; z < volume.sizeZ(); ++z) {
            int blockZ = volume.blockZ(z);

            for(int xy = 0; xy < volume.strideZ(); ++xy) {
               int inputZ = blockZ + (int)outputBuffer.get(i);
               minInputZ = Math.min(inputZ, minInputZ);
               maxInputZ = Math.max(inputZ, maxInputZ);
               inputCoordinates[i * 3 + 2] = inputZ;
               ++i;
            }
         }

         if (DomainWarpFunction.shouldSampleNaively(volume, minInputX, minInputY, minInputZ, maxInputX, maxInputY, maxInputZ)) {
            this.sampleShiftedNaive(context, outputBuffer, inputCoordinates, volume);
         } else {
            DensityVolume inputVolume = new DensityVolume(maxInputX - minInputX + 1, maxInputY - minInputY + 1, maxInputZ - minInputZ + 1, minInputX, minInputY, minInputZ, 1, 1, 1);

            try (ScopedDensityBuffer inputBuffer = context.acquireBuffer(inputVolume)) {
               this.input.sampleVolume(context, inputBuffer, inputVolume);

               for(int index = 0; index < volume.size(); ++index) {
                  int inputIndex = inputVolume.indexUnchecked(inputCoordinates[index * 3] - minInputX, inputCoordinates[index * 3 + 1] - minInputY, inputCoordinates[index * 3 + 2] - minInputZ);
                  outputBuffer.set(index, inputBuffer.get(inputIndex));
               }
            }

         }
      }

      private void sampleShiftedNaive(final SamplerContext context, final DensityBuffer outputBuffer, final int[] inputCoordinates, final DensityVolume volume) {
         for(int index = 0; index < volume.size(); ++index) {
            outputBuffer.set(index, this.input.sampleValue(context, inputCoordinates[index * 3], inputCoordinates[index * 3 + 1], inputCoordinates[index * 3 + 2]));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         int offsetX = (int)this.offsetX.sampleValue(context, blockX, blockY, blockZ);
         int offsetY = (int)this.offsetY.sampleValue(context, blockX, blockY, blockZ);
         int offsetZ = (int)this.offsetZ.sampleValue(context, blockX, blockY, blockZ);
         return this.input.sampleValue(context, blockX + offsetX, blockY + offsetY, blockZ + offsetZ);
      }
   }
}
