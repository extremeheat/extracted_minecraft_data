package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TintedParticleLeavesBlock extends LeavesBlock {
   public static final MapCodec<TintedParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter((var0x) -> var0x.leafParticleChance), propertiesCodec()).apply(var0, TintedParticleLeavesBlock::new));

   public TintedParticleLeavesBlock(float var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
   }

   protected void spawnFallingLeavesParticle(Level var1, BlockPos var2, RandomSource var3) {
      ColorParticleOption var4 = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, var1.getClientLeafTintColor(var2));
      ParticleUtils.spawnParticleBelow(var1, var2, var3, var4);
   }

   public MapCodec<? extends TintedParticleLeavesBlock> codec() {
      return CODEC;
   }
}
