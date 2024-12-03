package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidStructure extends SinglePieceStructure {
   public static final MapCodec<DesertPyramidStructure> CODEC = simpleCodec(DesertPyramidStructure::new);

   public DesertPyramidStructure(Structure.StructureSettings var1) {
      super(DesertPyramidPiece::new, 21, 21, var1);
   }

   public void afterPlace(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7) {
      SortedArraySet var8 = SortedArraySet.create(Vec3i::compareTo);

      for(StructurePiece var10 : var7.pieces()) {
         if (var10 instanceof DesertPyramidPiece var11) {
            var8.addAll(var11.getPotentialSuspiciousSandWorldPositions());
            placeSuspiciousSand(var5, var1, var11.getRandomCollapsedRoofPos());
         }
      }

      ObjectArrayList var14 = new ObjectArrayList(var8.stream().toList());
      RandomSource var15 = RandomSource.create(var1.getSeed()).forkPositional().at(var7.calculateBoundingBox().getCenter());
      Util.shuffle(var14, var15);
      int var16 = Math.min(var8.size(), var15.nextInt(5, 8));
      ObjectListIterator var12 = var14.iterator();

      while(var12.hasNext()) {
         BlockPos var13 = (BlockPos)var12.next();
         if (var16 > 0) {
            --var16;
            placeSuspiciousSand(var5, var1, var13);
         } else if (var5.isInside(var13)) {
            var1.setBlock(var13, Blocks.SAND.defaultBlockState(), 2);
         }
      }

   }

   private static void placeSuspiciousSand(BoundingBox var0, WorldGenLevel var1, BlockPos var2) {
      if (var0.isInside(var2)) {
         var1.setBlock(var2, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2);
         var1.getBlockEntity(var2, BlockEntityType.BRUSHABLE_BLOCK).ifPresent((var1x) -> var1x.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, var2.asLong()));
      }

   }

   public StructureType<?> type() {
      return StructureType.DESERT_PYRAMID;
   }
}
