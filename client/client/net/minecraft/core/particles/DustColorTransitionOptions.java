package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustColorTransitionOptions extends DustParticleOptionsBase {
   public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
   public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(
      SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F
   );
   public static final MapCodec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ExtraCodecs.VECTOR3F.fieldOf("from_color").forGetter(var0x -> var0x.color),
               ExtraCodecs.VECTOR3F.fieldOf("to_color").forGetter(var0x -> var0x.toColor),
               Codec.FLOAT.fieldOf("scale").forGetter(var0x -> var0x.scale)
            )
            .apply(var0, DustColorTransitionOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, DustColorTransitionOptions> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VECTOR3F,
      var0 -> var0.color,
      ByteBufCodecs.VECTOR3F,
      var0 -> var0.toColor,
      ByteBufCodecs.FLOAT,
      var0 -> var0.scale,
      DustColorTransitionOptions::new
   );
   private final Vector3f toColor;

   public DustColorTransitionOptions(Vector3f var1, Vector3f var2, float var3) {
      super(var1, var3);
      this.toColor = var2;
   }

   public Vector3f getFromColor() {
      return this.color;
   }

   public Vector3f getToColor() {
      return this.toColor;
   }

   @Override
   public ParticleType<DustColorTransitionOptions> getType() {
      return ParticleTypes.DUST_COLOR_TRANSITION;
   }
}
