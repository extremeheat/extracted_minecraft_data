package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;

public class VibrationParticleOption implements ParticleOptions {
   public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(VibrationPath.CODEC.fieldOf("vibration").forGetter((var0x) -> {
         return var0x.vibrationPath;
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
         float var6 = (float)var2.readDouble();
         var2.expect(' ');
         float var7 = (float)var2.readDouble();
         var2.expect(' ');
         float var8 = (float)var2.readDouble();
         var2.expect(' ');
         int var9 = var2.readInt();
         BlockPos var10 = new BlockPos((double)var3, (double)var4, (double)var5);
         BlockPos var11 = new BlockPos((double)var6, (double)var7, (double)var8);
         return new VibrationParticleOption(new VibrationPath(var10, new BlockPositionSource(var11), var9));
      }

      public VibrationParticleOption fromNetwork(ParticleType<VibrationParticleOption> var1, FriendlyByteBuf var2) {
         VibrationPath var3 = VibrationPath.read(var2);
         return new VibrationParticleOption(var3);
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
   private final VibrationPath vibrationPath;

   public VibrationParticleOption(VibrationPath var1) {
      super();
      this.vibrationPath = var1;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      VibrationPath.write(var1, this.vibrationPath);
   }

   public String writeToString() {
      BlockPos var1 = this.vibrationPath.getOrigin();
      double var2 = (double)var1.getX();
      double var4 = (double)var1.getY();
      double var6 = (double)var1.getZ();
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getKey(this.getType()), var2, var4, var6, var2, var4, var6, this.vibrationPath.getArrivalInTicks());
   }

   public ParticleType<VibrationParticleOption> getType() {
      return ParticleTypes.VIBRATION;
   }

   public VibrationPath getVibrationPath() {
      return this.vibrationPath;
   }
}
