package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ShriekParticleOption implements ParticleOptions {
   public static final MapCodec<ShriekParticleOption> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.INT.fieldOf("delay").forGetter(var0x -> var0x.delay)).apply(var0, ShriekParticleOption::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ShriekParticleOption> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, var0 -> var0.delay, ShriekParticleOption::new
   );
   private final int delay;

   public ShriekParticleOption(int var1) {
      super();
      this.delay = var1;
   }

   @Override
   public ParticleType<ShriekParticleOption> getType() {
      return ParticleTypes.SHRIEK;
   }

   public int getDelay() {
      return this.delay;
   }
}
