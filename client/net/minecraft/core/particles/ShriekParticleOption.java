package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
   public static final ParticleOptions.Deserializer<ShriekParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ShriekParticleOption>() {
      public ShriekParticleOption fromCommand(ParticleType<ShriekParticleOption> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         var2.expect(' ');
         int var4 = var2.readInt();
         return new ShriekParticleOption(var4);
      }
   };
   private final int delay;

   public ShriekParticleOption(int var1) {
      super();
      this.delay = var1;
   }

   @Override
   public String writeToString(HolderLookup.Provider var1) {
      return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay);
   }

   @Override
   public ParticleType<ShriekParticleOption> getType() {
      return ParticleTypes.SHRIEK;
   }

   public int getDelay() {
      return this.delay;
   }
}
