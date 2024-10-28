package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustParticleOptions extends ScalableParticleOptionsBase {
   public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3.fromRGB24(16711680).toVector3f();
   public static final DustParticleOptions REDSTONE;
   public static final MapCodec<DustParticleOptions> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DustParticleOptions> STREAM_CODEC;
   private final Vector3f color;

   public DustParticleOptions(Vector3f var1, float var2) {
      super(var2);
      this.color = var1;
   }

   public ParticleType<DustParticleOptions> getType() {
      return ParticleTypes.DUST;
   }

   public Vector3f getColor() {
      return this.color;
   }

   static {
      REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((var0x) -> {
            return var0x.color;
         }), SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)).apply(var0, DustParticleOptions::new);
      });
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VECTOR3F, (var0) -> {
         return var0.color;
      }, ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale, DustParticleOptions::new);
   }
}
