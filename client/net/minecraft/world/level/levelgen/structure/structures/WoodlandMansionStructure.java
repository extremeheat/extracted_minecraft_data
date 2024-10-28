package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WoodlandMansionStructure extends Structure {
   public static final MapCodec<WoodlandMansionStructure> CODEC = simpleCodec(WoodlandMansionStructure::new);

   public WoodlandMansionStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      Rotation var2 = Rotation.getRandom(var1.random());
      BlockPos var3 = this.getLowestYIn5by5BoxOffset7Blocks(var1, var2);
      return var3.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub(var3, (var4) -> {
         this.generatePieces(var4, var1, var3, var2);
      }));
   }

   private void generatePieces(StructurePiecesBuilder var1, Structure.GenerationContext var2, BlockPos var3, Rotation var4) {
      LinkedList var5 = Lists.newLinkedList();
      WoodlandMansionPieces.generateMansion(var2.structureTemplateManager(), var3, var4, var5, var2.random());
      Objects.requireNonNull(var1);
      var5.forEach(var1::addPiece);
   }

   public void afterPlace(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7) {
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
      int var9 = var1.getMinBuildHeight();
      BoundingBox var10 = var7.calculateBoundingBox();
      int var11 = var10.minY();

      for(int var12 = var5.minX(); var12 <= var5.maxX(); ++var12) {
         for(int var13 = var5.minZ(); var13 <= var5.maxZ(); ++var13) {
            var8.set(var12, var11, var13);
            if (!var1.isEmptyBlock(var8) && var10.isInside(var8) && var7.isInsidePiece(var8)) {
               for(int var14 = var11 - 1; var14 > var9; --var14) {
                  var8.setY(var14);
                  if (!var1.isEmptyBlock(var8) && !var1.getBlockState(var8).liquid()) {
                     break;
                  }

                  var1.setBlock(var8, Blocks.COBBLESTONE.defaultBlockState(), 2);
               }
            }
         }
      }

   }

   public StructureType<?> type() {
      return StructureType.WOODLAND_MANSION;
   }
}
