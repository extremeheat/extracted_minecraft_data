package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;

public class VibrationParticleOption implements ParticleOptions {
   private static final Codec<PositionSource> SAFE_POSITION_SOURCE_CODEC = PositionSource.CODEC
      .validate(var0 -> var0 instanceof EntityPositionSource ? DataResult.error(() -> "Entity position sources are not allowed") : DataResult.success(var0));
   public static final MapCodec<VibrationParticleOption> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               SAFE_POSITION_SOURCE_CODEC.fieldOf("destination").forGetter(VibrationParticleOption::getDestination),
               Codec.INT.fieldOf("arrival_in_ticks").forGetter(VibrationParticleOption::getArrivalInTicks)
            )
            .apply(var0, VibrationParticleOption::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, VibrationParticleOption> STREAM_CODEC = StreamCodec.composite(
      PositionSource.STREAM_CODEC,
      VibrationParticleOption::getDestination,
      ByteBufCodecs.VAR_INT,
      VibrationParticleOption::getArrivalInTicks,
      VibrationParticleOption::new
   );
   private final PositionSource destination;
   private final int arrivalInTicks;

   public VibrationParticleOption(PositionSource var1, int var2) {
      super();
      this.destination = var1;
      this.arrivalInTicks = var2;
   }

   @Override
   public ParticleType<VibrationParticleOption> getType() {
      return ParticleTypes.VIBRATION;
   }

   public PositionSource getDestination() {
      return this.destination;
   }

   public int getArrivalInTicks() {
      return this.arrivalInTicks;
   }
}
