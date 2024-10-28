package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public record TargetColorParticleOption(Vec3 target, int color) implements ParticleOptions {
   public static final MapCodec<TargetColorParticleOption> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Vec3.CODEC.fieldOf("target").forGetter(TargetColorParticleOption::target), ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(TargetColorParticleOption::color)).apply(var0, TargetColorParticleOption::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, TargetColorParticleOption> STREAM_CODEC;

   public TargetColorParticleOption(Vec3 var1, int var2) {
      super();
      this.target = var1;
      this.color = var2;
   }

   public ParticleType<TargetColorParticleOption> getType() {
      return ParticleTypes.TRAIL;
   }

   public Vec3 target() {
      return this.target;
   }

   public int color() {
      return this.color;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, TargetColorParticleOption::target, ByteBufCodecs.INT, TargetColorParticleOption::color, TargetColorParticleOption::new);
   }
}
