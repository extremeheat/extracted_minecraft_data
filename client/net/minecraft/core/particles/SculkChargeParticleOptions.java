package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SculkChargeParticleOptions(float d) implements ParticleOptions {
   private final float roll;
   public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.FLOAT.fieldOf("roll").forGetter(var0x -> var0x.roll)).apply(var0, SculkChargeParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, SculkChargeParticleOptions> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT, var0 -> var0.roll, SculkChargeParticleOptions::new
   );
   public static final ParticleOptions.Deserializer<SculkChargeParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<SculkChargeParticleOptions>() {
      public SculkChargeParticleOptions fromCommand(ParticleType<SculkChargeParticleOptions> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         var2.expect(' ');
         float var4 = var2.readFloat();
         return new SculkChargeParticleOptions(var4);
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
   public String writeToString(HolderLookup.Provider var1) {
      return String.format(Locale.ROOT, "%s %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.roll);
   }
}
