package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MangroveLeavesBlock extends LeavesBlock implements BonemealableBlock {
   public static final MapCodec<MangroveLeavesBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("leaf_particle_chance").forGetter((var0x) -> var0x.leafParticleChance), ParticleTypes.CODEC.fieldOf("leaf_particle").forGetter((var0x) -> var0x.leafParticle), propertiesCodec()).apply(var0, MangroveLeavesBlock::new));

   public MapCodec<MangroveLeavesBlock> codec() {
      return CODEC;
   }

   public MangroveLeavesBlock(int var1, ParticleOptions var2, BlockBehaviour.Properties var3) {
      super(var1, var2, var3);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.below()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.setBlock(var3.below(), MangrovePropaguleBlock.createNewHangingPropagule(), 2);
   }

   public BlockPos getParticlePos(BlockPos var1) {
      return var1.below();
   }
}
