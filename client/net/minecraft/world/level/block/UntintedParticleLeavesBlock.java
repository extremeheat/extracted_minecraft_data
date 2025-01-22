package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class UntintedParticleLeavesBlock extends LeavesBlock {
   public static final MapCodec<UntintedParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter((var0x) -> var0x.leafParticleChance), ParticleTypes.CODEC.fieldOf("leaf_particle").forGetter((var0x) -> var0x.leafParticle), propertiesCodec()).apply(var0, UntintedParticleLeavesBlock::new));
   protected final ParticleOptions leafParticle;

   public UntintedParticleLeavesBlock(float var1, ParticleOptions var2, BlockBehaviour.Properties var3) {
      super(var1, var3);
      this.leafParticle = var2;
   }

   protected void spawnFallingLeavesParticle(Level var1, BlockPos var2, RandomSource var3) {
      ParticleUtils.spawnParticleBelow(var1, var2, var3, this.leafParticle);
   }

   public MapCodec<UntintedParticleLeavesBlock> codec() {
      return CODEC;
   }
}
