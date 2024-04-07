package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class VibrationParticleOption implements ParticleOptions {
   public static final MapCodec<VibrationParticleOption> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               PositionSource.CODEC.fieldOf("destination").forGetter(VibrationParticleOption::getDestination),
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
   public static final ParticleOptions.Deserializer<VibrationParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<VibrationParticleOption>() {
      public VibrationParticleOption fromCommand(ParticleType<VibrationParticleOption> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         var2.expect(' ');
         float var4 = (float)var2.readDouble();
         var2.expect(' ');
         float var5 = (float)var2.readDouble();
         var2.expect(' ');
         float var6 = (float)var2.readDouble();
         var2.expect(' ');
         int var7 = var2.readInt();
         BlockPos var8 = BlockPos.containing((double)var4, (double)var5, (double)var6);
         return new VibrationParticleOption(new BlockPositionSource(var8), var7);
      }
   };
   private final PositionSource destination;
   private final int arrivalInTicks;

   public VibrationParticleOption(PositionSource var1, int var2) {
      super();
      this.destination = var1;
      this.arrivalInTicks = var2;
   }

   @Override
   public String writeToString(HolderLookup.Provider var1) {
      Vec3 var2 = this.destination.getPosition(null).get();
      double var3 = var2.x();
      double var5 = var2.y();
      double var7 = var2.z();
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), var3, var5, var7, this.arrivalInTicks);
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
