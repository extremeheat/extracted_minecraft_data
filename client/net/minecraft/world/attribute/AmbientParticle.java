package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public record AmbientParticle(ParticleOptions particle, float probability) {
   public static final Codec<AmbientParticle> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ParticleTypes.CODEC.fieldOf("particle").forGetter((var0x) -> var0x.particle), Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((var0x) -> var0x.probability)).apply(var0, AmbientParticle::new));

   public AmbientParticle(ParticleOptions var1, float var2) {
      super();
      this.particle = var1;
      this.probability = var2;
   }

   public boolean canSpawn(RandomSource var1) {
      return var1.nextFloat() <= this.probability;
   }

   public static List<AmbientParticle> of(ParticleOptions var0, float var1) {
      return List.of(new AmbientParticle(var0, var1));
   }
}
