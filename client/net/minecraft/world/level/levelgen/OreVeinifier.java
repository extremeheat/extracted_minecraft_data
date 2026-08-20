package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record OreVeinifier(BlockState oreBlock, BlockState rawOreBlock, BlockState fillerBlock, float rawOreChance, DensityFunction density, DensityFunction richness, DensityFunction fillerGap) {
   public static final float VEININESS_THRESHOLD = 0.4F;
   public static final int EDGE_ROUNDOFF_BEGIN = 20;
   public static final float MAX_EDGE_ROUNDOFF = 0.2F;
   public static final float VEIN_SOLIDNESS = 0.7F;
   public static final float MIN_RICHNESS = 0.1F;
   public static final float MAX_RICHNESS = 0.3F;
   public static final float MAX_RICHNESS_THRESHOLD = 0.6F;
   private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
   public static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;
   public static final Codec<OreVeinifier> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockState.CODEC.fieldOf("ore_block").forGetter(OreVeinifier::oreBlock), BlockState.CODEC.fieldOf("raw_ore_block").forGetter(OreVeinifier::rawOreBlock), BlockState.CODEC.fieldOf("filler_block").forGetter(OreVeinifier::fillerBlock), Codec.floatRange(0.0F, 1.0F).fieldOf("raw_ore_chance").forGetter(OreVeinifier::rawOreChance), DensityFunction.CODEC.fieldOf("density").forGetter(OreVeinifier::density), DensityFunction.CODEC.fieldOf("richness").forGetter(OreVeinifier::richness), DensityFunction.CODEC.fieldOf("filler_gap").forGetter(OreVeinifier::fillerGap)).apply(i, OreVeinifier::new));

   public OreVeinifier {
      super();
   }

   public OreVeinifier mapAll(final DensityFunction.Visitor visitor) {
      return new OreVeinifier(this.oreBlock, this.rawOreBlock, this.fillerBlock, this.rawOreChance, this.density.mapAll(visitor), this.richness.mapAll(visitor), this.fillerGap.mapAll(visitor));
   }

   public NoiseChunk.BlockStateFiller createFiller(final PositionalRandomFactory randomFactory) {
      BlockState defaultState = SharedConstants.DEBUG_ORE_VEINS ? Blocks.AIR.defaultBlockState() : null;
      return (context) -> {
         double density = (double)this.density.compute(context);
         if (density <= 0.0) {
            return defaultState;
         } else {
            RandomSource random = randomFactory.at(context.blockX(), context.blockY(), context.blockZ());
            if ((double)random.nextFloat() > density) {
               return defaultState;
            } else {
               double richness = (double)this.richness.compute(context);
               if ((double)random.nextFloat() < richness && (double)this.fillerGap.compute(context) < 0.0) {
                  return random.nextFloat() < this.rawOreChance ? this.rawOreBlock : this.oreBlock;
               } else {
                  return SharedConstants.DEBUG_ORE_VEINS ? Blocks.OAK_BUTTON.defaultBlockState() : this.fillerBlock;
               }
            }
         }
      };
   }

   protected static enum VeinType {
      COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
      IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

      private final BlockState oreBlock;
      private final BlockState rawOreBlock;
      private final BlockState fillerBlock;
      protected final int minY;
      protected final int maxY;

      private VeinType(final BlockState oreBlock, final BlockState rawOreBlock, final BlockState fillerBlock, final int minY, final int maxY) {
         this.oreBlock = oreBlock;
         this.rawOreBlock = rawOreBlock;
         this.fillerBlock = fillerBlock;
         this.minY = minY;
         this.maxY = maxY;
      }

      public OreVeinifier create(final DensityFunction density, final DensityFunction richness, final DensityFunction fillerGap) {
         return new OreVeinifier(this.oreBlock, this.rawOreBlock, this.fillerBlock, 0.02F, density, richness, fillerGap);
      }

      // $FF: synthetic method
      private static VeinType[] $values() {
         return new VeinType[]{COPPER, IRON};
      }
   }
}
