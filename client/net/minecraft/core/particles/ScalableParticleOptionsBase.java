package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public abstract class ScalableParticleOptionsBase implements ParticleOptions {
   public static final float MIN_SCALE = 0.01F;
   public static final float MAX_SCALE = 4.0F;
   protected static final Codec<Float> SCALE;
   private final float scale;

   public ScalableParticleOptionsBase(float var1) {
      super();
      this.scale = Mth.clamp(var1, 0.01F, 4.0F);
   }

   public float getScale() {
      return this.scale;
   }

   static {
      SCALE = Codec.FLOAT.validate((var0) -> {
         return var0 >= 0.01F && var0 <= 4.0F ? DataResult.success(var0) : DataResult.error(() -> {
            return "Value must be within range [0.01;4.0]: " + var0;
         });
      });
   }
}
