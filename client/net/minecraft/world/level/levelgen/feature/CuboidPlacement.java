package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public record CuboidPlacement(IntProvider xzSize, IntProvider ySize, boolean includeEdges, boolean includeInterior) implements PlacementModifier {
   public static final MapCodec<CuboidPlacement> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(IntProviders.codec(1, 16).fieldOf("xz_size").forGetter(CuboidPlacement::xzSize), IntProviders.codec(1, 16).fieldOf("y_size").forGetter(CuboidPlacement::ySize), Codec.BOOL.optionalFieldOf("include_edges", true).forGetter(CuboidPlacement::includeEdges), Codec.BOOL.optionalFieldOf("include_interior", true).forGetter(CuboidPlacement::includeInterior)).apply(i, CuboidPlacement::new));

   public CuboidPlacement {
      super();
   }

   public MapCodec<CuboidPlacement> codec() {
      return CODEC;
   }

   public void modify(final PlacementContext context, final RandomSource random, final BlockPos origin, final Consumer<BlockPos> output) {
      int height = this.ySize.sample(random);
      int width = this.xzSize.sample(random);
      int length = this.xzSize.sample(random);

      for(int x = 0; x < width; ++x) {
         boolean xFace = x == 0 || x == width - 1;

         for(int y = 0; y < height; ++y) {
            boolean yFace = y == 0 || y == height - 1;

            for(int z = 0; z < length; ++z) {
               boolean zFace = z == 0 || z == length - 1;
               if ((this.includeEdges || (!xFace || !yFace) && (!zFace || !yFace) && (!xFace || !zFace)) && (this.includeInterior || xFace || yFace || zFace)) {
                  output.accept(origin.offset(x, y, z));
               }
            }
         }
      }

   }

   public InclusiveRange<Integer> modifyXzDomain(final InclusiveRange<Integer> inputDomain) {
      return new InclusiveRange<Integer>(inputDomain.minInclusive(), (Integer)inputDomain.maxInclusive() + this.xzSize.maxInclusive() - 1);
   }
}
