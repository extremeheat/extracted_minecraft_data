package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.loot.packs.UpdateOneTwentyBuiltInLootTables;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.flag.FeatureFlags;
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

public class DesertPyramidStructure extends SinglePieceStructure {
   public static final Codec<DesertPyramidStructure> CODEC = simpleCodec(DesertPyramidStructure::new);

   public DesertPyramidStructure(Structure.StructureSettings var1) {
      super(DesertPyramidPiece::new, 21, 21, var1);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void afterPlace(
      WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7
   ) {
      if (var1.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
         SortedArraySet var8 = SortedArraySet.create(Vec3i::compareTo);

         for(StructurePiece var10 : var7.pieces()) {
            if (var10 instanceof DesertPyramidPiece var11) {
               var8.addAll(var11.getPotentialSuspiciousSandWorldPositions());
            }
         }

         ObjectArrayList var13 = new ObjectArrayList(var8.stream().toList());
         Util.shuffle(var13, var4);
         int var14 = Math.min(var8.size(), var4.nextInt(5, 8));
         ObjectListIterator var15 = var13.iterator();

         while(var15.hasNext()) {
            BlockPos var12 = (BlockPos)var15.next();
            if (var14 > 0) {
               --var14;
               var1.setBlock(var12, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2);
               var1.getBlockEntity(var12, BlockEntityType.SUSPICIOUS_SAND)
                  .ifPresent(var1x -> var1x.setLootTable(UpdateOneTwentyBuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, var12.asLong()));
            } else {
               var1.setBlock(var12, Blocks.SAND.defaultBlockState(), 2);
            }
         }
      }
   }

   @Override
   public StructureType<?> type() {
      return StructureType.DESERT_PYRAMID;
   }
}
