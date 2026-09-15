package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record SculkPatchFeature(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds) implements Feature {
   public static final MapCodec<SculkPatchFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.intRange(1, 32).fieldOf("charge_count").forGetter(SculkPatchFeature::chargeCount), Codec.intRange(1, 500).fieldOf("amount_per_charge").forGetter(SculkPatchFeature::amountPerCharge), Codec.intRange(1, 64).fieldOf("spread_attempts").forGetter(SculkPatchFeature::spreadAttempts), Codec.intRange(0, 8).fieldOf("growth_rounds").forGetter(SculkPatchFeature::growthRounds), Codec.intRange(0, 8).fieldOf("spread_rounds").forGetter(SculkPatchFeature::spreadRounds)).apply(i, SculkPatchFeature::new));

   public SculkPatchFeature {
      super();
   }

   public MapCodec<SculkPatchFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      if (!this.canSpreadFrom(level, origin)) {
         return false;
      } else {
         SculkSpreader spreader = SculkSpreader.createWorldGenSpreader();
         int totalRounds = this.spreadRounds + this.growthRounds;

         for(int round = 0; round < totalRounds; ++round) {
            for(int i = 0; i < this.chargeCount; ++i) {
               spreader.addCursors(origin, this.amountPerCharge);
            }

            boolean spreadVeins = round < this.spreadRounds;

            for(int i = 0; i < this.spreadAttempts; ++i) {
               spreader.updateCursors(level, origin, random, spreadVeins);
            }

            spreader.clear();
         }

         return true;
      }
   }

   private boolean canSpreadFrom(final LevelAccessor level, final BlockPos origin) {
      BlockState start = level.getBlockState(origin);
      if (start.getBlock() instanceof SculkBehaviour) {
         return true;
      } else if (!start.isAir() && (!start.is(Blocks.WATER) || !start.getFluidState().isSource())) {
         return false;
      } else {
         Stream var10000 = Direction.stream();
         Objects.requireNonNull(origin);
         return var10000.map(origin::relative).anyMatch((pos) -> level.getBlockState(pos).isCollisionShapeFullBlock(level, pos));
      }
   }
}
