package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class VibrationParticleOption implements ParticleOptions {
   public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(PositionSource.CODEC.fieldOf("destination").forGetter((var0x) -> {
         return var0x.destination;
      }), Codec.INT.fieldOf("arrival_in_ticks").forGetter((var0x) -> {
         return var0x.arrivalInTicks;
      })).apply(var0, VibrationParticleOption::new);
   });
   public static final ParticleOptions.Deserializer<VibrationParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<VibrationParticleOption>() {
      public VibrationParticleOption fromCommand(ParticleType<VibrationParticleOption> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         float var3 = (float)var2.readDouble();
         var2.expect(' ');
         float var4 = (float)var2.readDouble();
         var2.expect(' ');
         float var5 = (float)var2.readDouble();
         var2.expect(' ');
         int var6 = var2.readInt();
         BlockPos var7 = new BlockPos((double)var3, (double)var4, (double)var5);
         return new VibrationParticleOption(new BlockPositionSource(var7), var6);
      }

      public VibrationParticleOption fromNetwork(ParticleType<VibrationParticleOption> var1, FriendlyByteBuf var2) {
         PositionSource var3 = PositionSourceType.fromNetwork(var2);
         int var4 = var2.readVarInt();
         return new VibrationParticleOption(var3, var4);
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };
   private final PositionSource destination;
   private final int arrivalInTicks;

   public VibrationParticleOption(PositionSource var1, int var2) {
      super();
      this.destination = var1;
      this.arrivalInTicks = var2;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      PositionSourceType.toNetwork(this.destination, var1);
      var1.writeVarInt(this.arrivalInTicks);
   }

   public String writeToString() {
      Vec3 var1 = (Vec3)this.destination.getPosition((Level)null).get();
      double var2 = var1.x();
      double var4 = var1.y();
      double var6 = var1.z();
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getKey(this.getType()), var2, var4, var6, this.arrivalInTicks);
   }

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
