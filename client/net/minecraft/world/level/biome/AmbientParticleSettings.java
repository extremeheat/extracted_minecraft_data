package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public record AmbientParticleSettings(ParticleOptions options, float probability) {
   public static final Codec<AmbientParticleSettings> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ParticleTypes.CODEC.fieldOf("options").forGetter((var0x) -> var0x.options), Codec.FLOAT.fieldOf("probability").forGetter((var0x) -> var0x.probability)).apply(var0, AmbientParticleSettings::new));

   public AmbientParticleSettings(ParticleOptions var1, float var2) {
      super();
      this.options = var1;
      this.probability = var2;
   }

   public boolean canSpawn(RandomSource var1) {
      return var1.nextFloat() <= this.probability;
   }
}
