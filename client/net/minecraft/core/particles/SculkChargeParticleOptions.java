package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public record SculkChargeParticleOptions(float c) implements ParticleOptions {
   private final float roll;
   public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("roll").forGetter((var0x) -> {
         return var0x.roll;
      })).apply(var0, SculkChargeParticleOptions::new);
   });
   public static final ParticleOptions.Deserializer<SculkChargeParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<SculkChargeParticleOptions>() {
      public SculkChargeParticleOptions fromCommand(ParticleType<SculkChargeParticleOptions> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         float var3 = var2.readFloat();
         return new SculkChargeParticleOptions(var3);
      }

      public SculkChargeParticleOptions fromNetwork(ParticleType<SculkChargeParticleOptions> var1, FriendlyByteBuf var2) {
         return new SculkChargeParticleOptions(var2.readFloat());
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

   public SculkChargeParticleOptions(float var1) {
      super();
      this.roll = var1;
   }

   public ParticleType<SculkChargeParticleOptions> getType() {
      return ParticleTypes.SCULK_CHARGE;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeFloat(this.roll);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.roll);
   }

   public float roll() {
      return this.roll;
   }
}
