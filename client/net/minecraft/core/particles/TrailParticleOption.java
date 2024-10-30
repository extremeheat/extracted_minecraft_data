package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public record TrailParticleOption(Vec3 target, int color, int duration) implements ParticleOptions {
   public static final MapCodec<TrailParticleOption> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Vec3.CODEC.fieldOf("target").forGetter(TrailParticleOption::target), ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(TrailParticleOption::color), ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(TrailParticleOption::duration)).apply(var0, TrailParticleOption::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, TrailParticleOption> STREAM_CODEC;

   public TrailParticleOption(Vec3 var1, int var2, int var3) {
      super();
      this.target = var1;
      this.color = var2;
      this.duration = var3;
   }

   public ParticleType<TrailParticleOption> getType() {
      return ParticleTypes.TRAIL;
   }

   public Vec3 target() {
      return this.target;
   }

   public int color() {
      return this.color;
   }

   public int duration() {
      return this.duration;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, TrailParticleOption::target, ByteBufCodecs.INT, TrailParticleOption::color, ByteBufCodecs.VAR_INT, TrailParticleOption::duration, TrailParticleOption::new);
   }
}
