package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record SculkChargeParticleOptions(float c) implements ParticleOptions {
   private final float roll;
   public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.FLOAT.fieldOf("roll").forGetter(var0x -> var0x.roll)).apply(var0, SculkChargeParticleOptions::new)
   );
   public static final ParticleOptions.Deserializer<SculkChargeParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<SculkChargeParticleOptions>() {
      public SculkChargeParticleOptions fromCommand(ParticleType<SculkChargeParticleOptions> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         float var3 = var2.readFloat();
         return new SculkChargeParticleOptions(var3);
      }

      public SculkChargeParticleOptions fromNetwork(ParticleType<SculkChargeParticleOptions> var1, FriendlyByteBuf var2) {
         return new SculkChargeParticleOptions(var2.readFloat());
      }
   };

   public SculkChargeParticleOptions(float var1) {
      super();
      this.roll = var1;
   }

   @Override
   public ParticleType<SculkChargeParticleOptions> getType() {
      return ParticleTypes.SCULK_CHARGE;
   }

   @Override
   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeFloat(this.roll);
   }

   @Override
   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.roll);
   }
}
