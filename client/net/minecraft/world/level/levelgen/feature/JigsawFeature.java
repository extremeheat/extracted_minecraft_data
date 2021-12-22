package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureFeature;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class JigsawFeature extends NoiseAffectingStructureFeature<JigsawConfiguration> {
   public JigsawFeature(Codec<JigsawConfiguration> var1, int var2, boolean var3, boolean var4, Predicate<PieceGeneratorSupplier.Context<JigsawConfiguration>> var5) {
      super(var1, (var4x) -> {
         if (!var5.test(var4x)) {
            return Optional.empty();
         } else {
            BlockPos var5x = new BlockPos(var4x.chunkPos().getMinBlockX(), var2, var4x.chunkPos().getMinBlockZ());
            Pools.bootstrap();
            return JigsawPlacement.addPieces(var4x, PoolElementStructurePiece::new, var5x, var3, var4);
         }
      });
   }
}
