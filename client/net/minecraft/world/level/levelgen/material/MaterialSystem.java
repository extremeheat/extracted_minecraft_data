package net.minecraft.world.level.levelgen.material;

import java.util.Arrays;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceExtensions;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.synth.Noise;

public class MaterialSystem {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;
   private static final BlockState YELLOW_TERRACOTTA;
   private static final BlockState BROWN_TERRACOTTA;
   private static final BlockState RED_TERRACOTTA;
   private static final BlockState LIGHT_GRAY_TERRACOTTA;
   private final int seaLevel;
   private final DensityFunction preliminarySurfaceFunction;
   private final BlockState[] clayBands;
   private final Noise clayBandsOffsetNoise;
   private final PositionalRandomFactory noiseRandom;
   private final Noise surfaceNoise;
   private final Noise surfaceSecondaryNoise;
   private final SurfaceExtensions surfaceExtensions;

   public MaterialSystem(final RandomState randomState, final int seaLevel, final DensityFunction preliminarySurfaceFunction, final PositionalRandomFactory noiseRandom) {
      super();
      this.seaLevel = seaLevel;
      this.preliminarySurfaceFunction = preliminarySurfaceFunction;
      this.noiseRandom = noiseRandom;
      this.clayBandsOffsetNoise = randomState.getOrCreateNoise(Noises.CLAY_BANDS_OFFSET);
      this.clayBands = generateBands(noiseRandom.fromHashOf(Identifier.withDefaultNamespace("clay_bands")));
      this.surfaceNoise = randomState.getOrCreateNoise(Noises.SURFACE);
      this.surfaceSecondaryNoise = randomState.getOrCreateNoise(Noises.SURFACE_SECONDARY);
      this.surfaceExtensions = new SurfaceExtensions(randomState, seaLevel, noiseRandom);
   }

   protected int getSurfaceDepth(final int blockX, final int blockZ) {
      double noiseValue = (double)this.surfaceNoise.get((double)blockX, 0.0, (double)blockZ);
      return (int)(noiseValue * 2.75 + 3.0 + this.noiseRandom.at(blockX, 0, blockZ).nextDouble() * 0.25);
   }

   protected double getSurfaceSecondary(final int blockX, final int blockZ) {
      return (double)this.surfaceSecondaryNoise.get((double)blockX, 0.0, (double)blockZ);
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   private static BlockState[] generateBands(final RandomSource random) {
      BlockState[] clayBands = new BlockState[192];
      Arrays.fill(clayBands, TERRACOTTA);

      for(int i = 0; i < clayBands.length; ++i) {
         i += random.nextInt(5) + 1;
         if (i < clayBands.length) {
            clayBands[i] = ORANGE_TERRACOTTA;
         }
      }

      makeBands(random, clayBands, 1, YELLOW_TERRACOTTA);
      makeBands(random, clayBands, 2, BROWN_TERRACOTTA);
      makeBands(random, clayBands, 1, RED_TERRACOTTA);
      int whiteBandCount = random.nextIntBetweenInclusive(9, 15);
      int i = 0;

      for(int start = 0; i < whiteBandCount && start < clayBands.length; start += random.nextInt(16) + 4) {
         clayBands[start] = WHITE_TERRACOTTA;
         if (start - 1 > 0 && random.nextBoolean()) {
            clayBands[start - 1] = LIGHT_GRAY_TERRACOTTA;
         }

         if (start + 1 < clayBands.length && random.nextBoolean()) {
            clayBands[start + 1] = LIGHT_GRAY_TERRACOTTA;
         }

         ++i;
      }

      return clayBands;
   }

   private static void makeBands(final RandomSource random, final BlockState[] clayBands, final int baseWidth, final BlockState state) {
      int bandCount = random.nextIntBetweenInclusive(6, 15);

      for(int i = 0; i < bandCount; ++i) {
         int width = baseWidth + random.nextInt(3);
         int start = random.nextInt(clayBands.length);

         for(int p = 0; start + p < clayBands.length && p < width; ++p) {
            clayBands[start + p] = state;
         }
      }

   }

   protected BlockState getBand(final int worldX, final int y, final int worldZ) {
      int offset = Math.round(this.clayBandsOffsetNoise.get((double)worldX, 0.0, (double)worldZ) * 4.0F);
      return this.clayBands[(y + offset + this.clayBands.length) % this.clayBands.length];
   }

   public DensityFunction preliminarySurfaceFunction() {
      return this.preliminarySurfaceFunction;
   }

   public SurfaceExtensions surfaceExtensions() {
      return this.surfaceExtensions;
   }

   static {
      WHITE_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.white()).defaultBlockState();
      ORANGE_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.orange()).defaultBlockState();
      TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
      YELLOW_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.yellow()).defaultBlockState();
      BROWN_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.brown()).defaultBlockState();
      RED_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.red()).defaultBlockState();
      LIGHT_GRAY_TERRACOTTA = ((Block)Blocks.DYED_TERRACOTTA.lightGray()).defaultBlockState();
   }
}
