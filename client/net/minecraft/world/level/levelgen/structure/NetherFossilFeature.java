package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class NetherFossilFeature extends NoiseAffectingStructureFeature<RangeConfiguration> {
   public NetherFossilFeature(Codec<RangeConfiguration> var1) {
      super(var1, NetherFossilFeature::pieceGeneratorSupplier);
   }

   private static Optional<PieceGenerator<RangeConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<RangeConfiguration> var0) {
      WorldgenRandom var1 = new WorldgenRandom(new LegacyRandomSource(0L));
      var1.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      int var2 = var0.chunkPos().getMinBlockX() + var1.nextInt(16);
      int var3 = var0.chunkPos().getMinBlockZ() + var1.nextInt(16);
      int var4 = var0.chunkGenerator().getSeaLevel();
      WorldGenerationContext var5 = new WorldGenerationContext(var0.chunkGenerator(), var0.heightAccessor());
      int var6 = ((RangeConfiguration)var0.config()).height.sample(var1, var5);
      NoiseColumn var7 = var0.chunkGenerator().getBaseColumn(var2, var3, var0.heightAccessor());
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(var2, var6, var3);

      while(var6 > var4) {
         BlockState var9 = var7.getBlock(var6);
         --var6;
         BlockState var10 = var7.getBlock(var6);
         if (var9.isAir() && (var10.is(Blocks.SOUL_SAND) || var10.isFaceSturdy(EmptyBlockGetter.INSTANCE, var8.setY(var6), Direction.field_526))) {
            break;
         }
      }

      if (var6 <= var4) {
         return Optional.empty();
      } else if (!var0.validBiome().test(var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var2), QuartPos.fromBlock(var6), QuartPos.fromBlock(var3)))) {
         return Optional.empty();
      } else {
         BlockPos var11 = new BlockPos(var2, var6, var3);
         return Optional.of((var3x, var4x) -> {
            NetherFossilPieces.addPieces(var0.structureManager(), var3x, var1, var11);
         });
      }
   }
}
