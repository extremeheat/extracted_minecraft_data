package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class DustParticleOptions extends ScalableParticleOptionsBase {
   public static final int REDSTONE_PARTICLE_COLOR = 16711680;
   public static final DustParticleOptions REDSTONE = new DustParticleOptions(16711680, 1.0F);
   public static final MapCodec<DustParticleOptions> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter((var0x) -> {
         return var0x.color;
      }), SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)).apply(var0, DustParticleOptions::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, DustParticleOptions> STREAM_CODEC;
   private final int color;

   public DustParticleOptions(int var1, float var2) {
      super(var2);
      this.color = var1;
   }

   public ParticleType<DustParticleOptions> getType() {
      return ParticleTypes.DUST;
   }

   public Vector3f getColor() {
      return ARGB.vector3fFromRGB24(this.color);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, (var0) -> {
         return var0.color;
      }, ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale, DustParticleOptions::new);
   }
}
