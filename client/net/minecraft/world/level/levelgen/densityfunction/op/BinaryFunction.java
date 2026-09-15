package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;
import org.slf4j.Logger;

public record BinaryFunction(Type type, DensityFunction left, DensityFunction right) implements DensityFunction {
   private static final Logger LOGGER = LogUtils.getLogger();

   public BinaryFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler left = this.left.compileSampler(context);
      DensitySampler right = this.right.compileSampler(context);
      Object var10000;
      switch (this.type.ordinal()) {
         case 0:
            DensityFunction var43 = this.left;
            if (var43 instanceof ConstantFunction var29) {
               ConstantFunction var78 = var29;

               try {
                  var79 = var78.value();
               } catch (Throwable var24) {
                  throw new MatchException(var24.toString(), var24);
               }

               float leftValue = var79;
               if (true) {
                  var10000 = new ConstAddSampler(right, leftValue);
                  break;
               }
            }

            var43 = this.right;
            if (var43 instanceof ConstantFunction var35) {
               ConstantFunction var80 = var35;

               try {
                  var81 = var80.value();
               } catch (Throwable var23) {
                  throw new MatchException(var23.toString(), var23);
               }

               float leftValue = var81;
               if (true) {
                  var10000 = new ConstAddSampler(left, leftValue);
                  break;
               }
            }

            var10000 = new AddSampler(left, right);
            break;
         case 1:
            DensityFunction var41 = this.left;
            if (var41 instanceof ConstantFunction var28) {
               ConstantFunction var74 = var28;

               try {
                  var75 = var74.value();
               } catch (Throwable var22) {
                  throw new MatchException(var22.toString(), var22);
               }

               float leftValue = var75;
               if (true) {
                  var10000 = new ConstSubSampler(leftValue, right);
                  break;
               }
            }

            var41 = this.right;
            if (var41 instanceof ConstantFunction var34) {
               ConstantFunction var76 = var34;

               try {
                  var77 = var76.value();
               } catch (Throwable var21) {
                  throw new MatchException(var21.toString(), var21);
               }

               float leftValue = var77;
               if (true) {
                  var10000 = new ConstAddSampler(left, -leftValue);
                  break;
               }
            }

            var10000 = new SubSampler(left, right);
            break;
         case 2:
            DensityFunction var39 = this.left;
            if (var39 instanceof ConstantFunction var27) {
               ConstantFunction var70 = var27;

               try {
                  var71 = var70.value();
               } catch (Throwable var20) {
                  throw new MatchException(var20.toString(), var20);
               }

               float leftValue = var71;
               if (true) {
                  var10000 = new ConstMulSampler(right, leftValue);
                  break;
               }
            }

            var39 = this.right;
            if (var39 instanceof ConstantFunction var33) {
               ConstantFunction var72 = var33;

               try {
                  var73 = var72.value();
               } catch (Throwable var19) {
                  throw new MatchException(var19.toString(), var19);
               }

               float leftValue = var73;
               if (true) {
                  var10000 = new ConstMulSampler(left, leftValue);
                  break;
               }
            }

            var10000 = new MulSampler(left, right);
            break;
         case 3:
            DensityFunction var37 = this.left;
            if (var37 instanceof ConstantFunction var26) {
               ConstantFunction var66 = var26;

               try {
                  var67 = var66.value();
               } catch (Throwable var18) {
                  throw new MatchException(var18.toString(), var18);
               }

               float leftValue = var67;
               if (true) {
                  var10000 = new ConstDivSampler(leftValue, right);
                  break;
               }
            }

            var37 = this.right;
            if (var37 instanceof ConstantFunction var32) {
               ConstantFunction var68 = var32;

               try {
                  var69 = var68.value();
               } catch (Throwable var17) {
                  throw new MatchException(var17.toString(), var17);
               }

               float leftValue = var69;
               if (true) {
                  var10000 = new ConstMulSampler(left, 1.0F / leftValue);
                  break;
               }
            }

            var10000 = new DivSampler(left, right);
            break;
         case 4:
            Interval leftRange = this.left.range();
            Interval rightRange = this.right.range();
            if (leftRange.max() < rightRange.min()) {
               this.warnNonIntersecting();
               var10000 = left;
            } else if (rightRange.max() < leftRange.min()) {
               this.warnNonIntersecting();
               var10000 = right;
            } else {
               DensityFunction var53 = this.left;
               if (var53 instanceof ConstantFunction) {
                  ConstantFunction var31 = (ConstantFunction)var53;
                  ConstantFunction var62 = var31;

                  try {
                     var63 = var62.value();
                  } catch (Throwable var16) {
                     throw new MatchException(var16.toString(), var16);
                  }

                  float leftValue = var63;
                  if (true) {
                     var10000 = new ConstMinSampler(right, leftValue);
                     break;
                  }
               }

               var53 = this.right;
               if (var53 instanceof ConstantFunction) {
                  ConstantFunction var36 = (ConstantFunction)var53;
                  ConstantFunction var64 = var36;

                  try {
                     var65 = var64.value();
                  } catch (Throwable var15) {
                     throw new MatchException(var15.toString(), var15);
                  }

                  float leftValue = var65;
                  if (true) {
                     var10000 = new ConstMinSampler(left, leftValue);
                     break;
                  }
               }

               var10000 = new MinSampler(left, right, rightRange.min());
            }
            break;
         case 5:
            Interval leftRange = this.left.range();
            Interval rightRange = this.right.range();
            if (leftRange.min() > rightRange.max()) {
               this.warnNonIntersecting();
               var10000 = left;
            } else if (rightRange.min() > leftRange.max()) {
               this.warnNonIntersecting();
               var10000 = right;
            } else {
               DensityFunction var10 = this.left;
               if (var10 instanceof ConstantFunction) {
                  ConstantFunction var6 = (ConstantFunction)var10;
                  ConstantFunction var58 = var6;

                  try {
                     var59 = var58.value();
                  } catch (Throwable var14) {
                     throw new MatchException(var14.toString(), var14);
                  }

                  float leftValue = var59;
                  if (true) {
                     var10000 = new ConstMaxSampler(right, leftValue);
                     break;
                  }
               }

               var10 = this.right;
               if (var10 instanceof ConstantFunction) {
                  ConstantFunction var8 = (ConstantFunction)var10;
                  ConstantFunction var60 = var8;

                  try {
                     var61 = var60.value();
                  } catch (Throwable var13) {
                     throw new MatchException(var13.toString(), var13);
                  }

                  float leftValue = var61;
                  if (true) {
                     var10000 = new ConstMaxSampler(left, leftValue);
                     break;
                  }
               }

               var10000 = new MaxSampler(left, right, rightRange.max());
            }
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (DensitySampler)var10000;
   }

   private void warnNonIntersecting() {
      LOGGER.warn("Compiling a {} function between two non-overlapping inputs: {} ({}) and {} ({})", new Object[]{this.type, this.left, this.left.range(), this.right, this.right.range()});
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction left = rule.rewrite(this.left);
      DensityFunction right = rule.rewrite(this.right);
      return left == this.left && this.right == right ? this : new BinaryFunction(this.type, left, right);
   }

   public Interval range() {
      Interval left = this.left.range();
      Interval right = this.right.range();
      Interval var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = Interval.add(left, right);
         case 1 -> var10000 = Interval.sub(left, right);
         case 2 -> var10000 = Interval.mul(left, right);
         case 3 -> var10000 = Interval.div(left, right);
         case 4 -> var10000 = Interval.min(left, right);
         case 5 -> var10000 = Interval.max(left, right);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.left.domainAxes() | this.right.domainAxes();
   }

   public MapCodec<BinaryFunction> codec() {
      return this.type().codec;
   }

   public static enum Type {
      ADD("add"),
      SUB("sub"),
      MUL("mul"),
      DIV("div"),
      MIN("min"),
      MAX("max");

      public final String id;
      public final MapCodec<BinaryFunction> codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("left").forGetter(BinaryFunction::left), DensityFunction.CODEC.fieldOf("right").forGetter(BinaryFunction::right)).apply(i, (left, right) -> new BinaryFunction(this, left, right)));

      private Type(final String id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{ADD, SUB, MUL, DIV, MIN, MAX};
      }
   }

   public static record AddSampler(DensitySampler left, DensitySampler right) implements DensitySampler {
      public AddSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer rightBuffer = context.acquireBuffer(volume)) {
            this.right.sampleVolume(context, rightBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               outputBuffer.addTo(i, rightBuffer.get(i));
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.left.sampleValue(context, blockX, blockY, blockZ) + this.right.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record ConstAddSampler(DensitySampler left, float right) implements DensitySampler {
      public ConstAddSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.addTo(i, this.right);
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.left.sampleValue(context, blockX, blockY, blockZ) + this.right;
      }
   }

   public static record MulSampler(DensitySampler left, DensitySampler right) implements DensitySampler {
      public MulSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer rightBuffer = context.acquireBuffer(volume)) {
            this.right.sampleVolume(context, rightBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               outputBuffer.set(i, outputBuffer.get(i) * rightBuffer.get(i));
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float left = this.left.sampleValue(context, blockX, blockY, blockZ);
         return left == 0.0F ? 0.0F : left * this.right.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record ConstMulSampler(DensitySampler left, float right) implements DensitySampler {
      public ConstMulSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, outputBuffer.get(i) * this.right);
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.left.sampleValue(context, blockX, blockY, blockZ) * this.right;
      }
   }

   public static record ConstDivSampler(float left, DensitySampler right) implements DensitySampler {
      public ConstDivSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.right.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, this.left / outputBuffer.get(i));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.left / this.right.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record SubSampler(DensitySampler left, DensitySampler right) implements DensitySampler {
      public SubSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer rightBuffer = context.acquireBuffer(volume)) {
            this.right.sampleVolume(context, rightBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               outputBuffer.addTo(i, -rightBuffer.get(i));
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.left.sampleValue(context, blockX, blockY, blockZ) - this.right.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record ConstSubSampler(float left, DensitySampler right) implements DensitySampler {
      public ConstSubSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.right.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, this.left - outputBuffer.get(i));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.left - this.right.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record DivSampler(DensitySampler left, DensitySampler right) implements DensitySampler {
      public DivSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer rightBuffer = context.acquireBuffer(volume)) {
            this.right.sampleVolume(context, rightBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               outputBuffer.set(i, outputBuffer.get(i) / rightBuffer.get(i));
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float left = this.left.sampleValue(context, blockX, blockY, blockZ);
         return left == 0.0F ? 0.0F : left / this.right.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   public static record MinSampler(DensitySampler left, DensitySampler right, float rightMinValue) implements DensitySampler {
      public MinSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer rightBuffer = context.acquireBuffer(volume)) {
            this.right.sampleVolume(context, rightBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               float rightValue = rightBuffer.get(i);
               if (rightValue < outputBuffer.get(i)) {
                  outputBuffer.set(i, rightValue);
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float left = this.left.sampleValue(context, blockX, blockY, blockZ);
         return left <= this.rightMinValue ? left : Math.min(left, this.right.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record MaxSampler(DensitySampler left, DensitySampler right, float rightMaxValue) implements DensitySampler {
      public MaxSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer rightBuffer = context.acquireBuffer(volume)) {
            this.right.sampleVolume(context, rightBuffer, volume);

            for(int i = 0; i < outputBuffer.size(); ++i) {
               float rightValue = rightBuffer.get(i);
               if (rightValue > outputBuffer.get(i)) {
                  outputBuffer.set(i, rightValue);
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float left = this.left.sampleValue(context, blockX, blockY, blockZ);
         return left >= this.rightMaxValue ? left : Math.max(left, this.right.sampleValue(context, blockX, blockY, blockZ));
      }
   }

   public static record ConstMinSampler(DensitySampler left, float right) implements DensitySampler {
      public ConstMinSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            if (this.right < outputBuffer.get(i)) {
               outputBuffer.set(i, this.right);
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Math.min(this.left.sampleValue(context, blockX, blockY, blockZ), this.right);
      }
   }

   public static record ConstMaxSampler(DensitySampler left, float right) implements DensitySampler {
      public ConstMaxSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.left.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            if (this.right > outputBuffer.get(i)) {
               outputBuffer.set(i, this.right);
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Math.max(this.left.sampleValue(context, blockX, blockY, blockZ), this.right);
      }
   }
}
