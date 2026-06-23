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
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class UntintedParticleLeavesBlock extends FallingParticlesLeavesBlock {
   public static final MapCodec<UntintedParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter((e) -> e.leafParticleChance), ParticleTypes.CODEC.fieldOf("leaf_particle").forGetter((e) -> e.leafParticle), AmbientLeavesBlockSoundPlayer.CODEC.fieldOf("ambient_leaves_block_sound_player").forGetter((e) -> e.ambientLeavesBlockSoundPlayer), propertiesCodec()).apply(i, UntintedParticleLeavesBlock::new));
   protected final ParticleOptions leafParticle;

   public UntintedParticleLeavesBlock(final float leafParticleChance, final ParticleOptions leafParticle, final AmbientLeavesBlockSoundPlayer ambientLeavesBlockSoundPlayer, final BlockBehaviour.Properties properties) {
      super(leafParticleChance, ambientLeavesBlockSoundPlayer, properties);
      this.leafParticle = leafParticle;
   }

   protected void spawnFallingLeavesParticle(final Level level, final BlockPos pos, final RandomSource random) {
      ParticleUtils.spawnParticleBelow(level, pos, random, this.leafParticle);
   }

   public MapCodec<UntintedParticleLeavesBlock> codec() {
      return CODEC;
   }
}
