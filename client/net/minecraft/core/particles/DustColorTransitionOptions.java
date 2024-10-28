package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustColorTransitionOptions extends ScalableParticleOptionsBase {
   public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
   public static final DustColorTransitionOptions SCULK_TO_REDSTONE;
   public static final MapCodec<DustColorTransitionOptions> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DustColorTransitionOptions> STREAM_CODEC;
   private final Vector3f fromColor;
   private final Vector3f toColor;

   public DustColorTransitionOptions(Vector3f var1, Vector3f var2, float var3) {
      super(var3);
      this.fromColor = var1;
      this.toColor = var2;
   }

   public Vector3f getFromColor() {
      return this.fromColor;
   }

   public Vector3f getToColor() {
      return this.toColor;
   }

   public ParticleType<DustColorTransitionOptions> getType() {
      return ParticleTypes.DUST_COLOR_TRANSITION;
   }

   static {
      SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ExtraCodecs.VECTOR3F.fieldOf("from_color").forGetter((var0x) -> {
            return var0x.fromColor;
         }), ExtraCodecs.VECTOR3F.fieldOf("to_color").forGetter((var0x) -> {
            return var0x.toColor;
         }), SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)).apply(var0, DustColorTransitionOptions::new);
      });
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VECTOR3F, (var0) -> {
         return var0.fromColor;
      }, ByteBufCodecs.VECTOR3F, (var0) -> {
         return var0.toColor;
      }, ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale, DustColorTransitionOptions::new);
   }
}
