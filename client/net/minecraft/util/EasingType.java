package net.minecraft.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public interface EasingType {
   ExtraCodecs.LateBoundIdMapper<String, EasingType> SIMPLE_REGISTRY = new ExtraCodecs.LateBoundIdMapper<String, EasingType>();
   Codec<EasingType> CODEC = Codec.either(SIMPLE_REGISTRY.codec(Codec.STRING), EasingType.CubicBezier.CODEC).xmap(Either::unwrap, (var0) -> {
      Either var10000;
      if (var0 instanceof CubicBezier var1) {
         var10000 = Either.right(var1);
      } else {
         var10000 = Either.left(var0);
      }

      return var10000;
   });
   EasingType CONSTANT = registerSimple("constant", (var0) -> 0.0F);
   EasingType LINEAR = registerSimple("linear", (var0) -> var0);
   EasingType IN_BACK = registerSimple("in_back", Ease::inBack);
   EasingType IN_BOUNCE = registerSimple("in_bounce", Ease::inBounce);
   EasingType IN_CIRC = registerSimple("in_circ", Ease::inCirc);
   EasingType IN_CUBIC = registerSimple("in_cubic", Ease::inCubic);
   EasingType IN_ELASTIC = registerSimple("in_elastic", Ease::inElastic);
   EasingType IN_EXPO = registerSimple("in_expo", Ease::inExpo);
   EasingType IN_QUAD = registerSimple("in_quad", Ease::inQuad);
   EasingType IN_QUART = registerSimple("in_quart", Ease::inQuart);
   EasingType IN_QUINT = registerSimple("in_quint", Ease::inQuint);
   EasingType IN_SINE = registerSimple("in_sine", Ease::inSine);
   EasingType IN_OUT_BACK = registerSimple("in_out_back", Ease::inOutBack);
   EasingType IN_OUT_BOUNCE = registerSimple("in_out_bounce", Ease::inOutBounce);
   EasingType IN_OUT_CIRC = registerSimple("in_out_circ", Ease::inOutCirc);
   EasingType IN_OUT_CUBIC = registerSimple("in_out_cubic", Ease::inOutCubic);
   EasingType IN_OUT_ELASTIC = registerSimple("in_out_elastic", Ease::inOutElastic);
   EasingType IN_OUT_EXPO = registerSimple("in_out_expo", Ease::inOutExpo);
   EasingType IN_OUT_QUAD = registerSimple("in_out_quad", Ease::inOutQuad);
   EasingType IN_OUT_QUART = registerSimple("in_out_quart", Ease::inOutQuart);
   EasingType IN_OUT_QUINT = registerSimple("in_out_quint", Ease::inOutQuint);
   EasingType IN_OUT_SINE = registerSimple("in_out_sine", Ease::inOutSine);
   EasingType OUT_BACK = registerSimple("out_back", Ease::outBack);
   EasingType OUT_BOUNCE = registerSimple("out_bounce", Ease::outBounce);
   EasingType OUT_CIRC = registerSimple("out_circ", Ease::outCirc);
   EasingType OUT_CUBIC = registerSimple("out_cubic", Ease::outCubic);
   EasingType OUT_ELASTIC = registerSimple("out_elastic", Ease::outElastic);
   EasingType OUT_EXPO = registerSimple("out_expo", Ease::outExpo);
   EasingType OUT_QUAD = registerSimple("out_quad", Ease::outQuad);
   EasingType OUT_QUART = registerSimple("out_quart", Ease::outQuart);
   EasingType OUT_QUINT = registerSimple("out_quint", Ease::outQuint);
   EasingType OUT_SINE = registerSimple("out_sine", Ease::outSine);

   static EasingType registerSimple(String var0, EasingType var1) {
      SIMPLE_REGISTRY.put(var0, var1);
      return var1;
   }

   static EasingType cubicBezier(float var0, float var1, float var2, float var3) {
      return new CubicBezier(new CubicBezierControls(var0, var1, var2, var3));
   }

   static EasingType symmetricCubicBezier(float var0, float var1) {
      return cubicBezier(var0, var1, 1.0F - var0, 1.0F - var1);
   }

   float apply(float var1);

   public static final class CubicBezier implements EasingType {
      public static final Codec<CubicBezier> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EasingType.CubicBezierControls.CODEC.fieldOf("cubic_bezier").forGetter((var0x) -> var0x.controls)).apply(var0, CubicBezier::new));
      private static final int NEWTON_RAPHSON_ITERATIONS = 4;
      private final CubicBezierControls controls;
      private final CubicCurve xCurve;
      private final CubicCurve yCurve;

      public CubicBezier(CubicBezierControls var1) {
         super();
         this.controls = var1;
         this.xCurve = curveFromControls(var1.x1, var1.x2);
         this.yCurve = curveFromControls(var1.y1, var1.y2);
      }

      private static CubicCurve curveFromControls(float var0, float var1) {
         return new CubicCurve(3.0F * var0 - 3.0F * var1 + 1.0F, -6.0F * var0 + 3.0F * var1, 3.0F * var0);
      }

      public float apply(float var1) {
         float var2 = var1;

         for(int var3 = 0; var3 < 4; ++var3) {
            float var4 = this.xCurve.sampleGradient(var2);
            if (var4 < 1.0E-5F) {
               break;
            }

            float var5 = this.xCurve.sample(var2) - var1;
            var2 -= var5 / var4;
         }

         return this.yCurve.sample(var2);
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (var1 instanceof CubicBezier var2) {
            if (this.controls.equals(var2.controls)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         return this.controls.hashCode();
      }

      public String toString() {
         return "CubicBezier(" + this.controls.x1 + ", " + this.controls.y1 + ", " + this.controls.x2 + ", " + this.controls.y2 + ")";
      }

      static record CubicCurve(float a, float b, float c) {
         CubicCurve(float var1, float var2, float var3) {
            super();
            this.a = var1;
            this.b = var2;
            this.c = var3;
         }

         public float sample(float var1) {
            return ((this.a * var1 + this.b) * var1 + this.c) * var1;
         }

         public float sampleGradient(float var1) {
            return (3.0F * this.a * var1 + 2.0F * this.b) * var1 + this.c;
         }
      }
   }

   public static record CubicBezierControls(float x1, float y1, float x2, float y2) {
      final float x1;
      final float y1;
      final float x2;
      final float y2;
      public static final Codec<CubicBezierControls> CODEC;

      public CubicBezierControls(float var1, float var2, float var3, float var4) {
         super();
         this.x1 = var1;
         this.y1 = var2;
         this.x2 = var3;
         this.y2 = var4;
      }

      private DataResult<CubicBezierControls> validate() {
         if (!(this.x1 < 0.0F) && !(this.x1 > 1.0F)) {
            return !(this.x2 < 0.0F) && !(this.x2 > 1.0F) ? DataResult.success(this) : DataResult.error(() -> "x2 must be in range [0; 1]");
         } else {
            return DataResult.error(() -> "x1 must be in range [0; 1]");
         }
      }

      static {
         CODEC = Codec.FLOAT.listOf(4, 4).xmap((var0) -> new CubicBezierControls((Float)var0.get(0), (Float)var0.get(1), (Float)var0.get(2), (Float)var0.get(3)), (var0) -> List.of(var0.x1, var0.y1, var0.x2, var0.y2)).validate(CubicBezierControls::validate);
      }
   }
}
