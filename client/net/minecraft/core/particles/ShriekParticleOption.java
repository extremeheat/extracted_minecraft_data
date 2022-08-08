package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public class ShriekParticleOption implements ParticleOptions {
   public static final Codec<ShriekParticleOption> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("delay").forGetter((var0x) -> {
         return var0x.delay;
      })).apply(var0, ShriekParticleOption::new);
   });
   public static final ParticleOptions.Deserializer<ShriekParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ShriekParticleOption>() {
      public ShriekParticleOption fromCommand(ParticleType<ShriekParticleOption> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         int var3 = var2.readInt();
         return new ShriekParticleOption(var3);
      }

      public ShriekParticleOption fromNetwork(ParticleType<ShriekParticleOption> var1, FriendlyByteBuf var2) {
         return new ShriekParticleOption(var2.readVarInt());
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
   private final int delay;

   public ShriekParticleOption(int var1) {
      super();
      this.delay = var1;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeVarInt(this.delay);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %d", Registry.PARTICLE_TYPE.getKey(this.getType()), this.delay);
   }

   public ParticleType<ShriekParticleOption> getType() {
      return ParticleTypes.SHRIEK;
   }

   public int getDelay() {
      return this.delay;
   }
}
