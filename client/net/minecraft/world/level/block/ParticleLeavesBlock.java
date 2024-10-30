package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleLeavesBlock extends LeavesBlock {
   public static final MapCodec<ParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("chance").forGetter((var0x) -> {
         return var0x.chance;
      }), ParticleTypes.CODEC.fieldOf("particle").forGetter((var0x) -> {
         return var0x.particle;
      }), propertiesCodec()).apply(var0, ParticleLeavesBlock::new);
   });
   private final ParticleOptions particle;
   private final int chance;

   public MapCodec<ParticleLeavesBlock> codec() {
      return CODEC;
   }

   public ParticleLeavesBlock(int var1, ParticleOptions var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.chance = var1;
      this.particle = var2;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      super.animateTick(var1, var2, var3, var4);
      if (var4.nextInt(this.chance) == 0) {
         BlockPos var5 = var3.below();
         BlockState var6 = var2.getBlockState(var5);
         if (!isFaceFull(var6.getCollisionShape(var2, var5), Direction.UP)) {
            ParticleUtils.spawnParticleBelow(var2, var3, var4, this.particle);
         }
      }
   }
}
