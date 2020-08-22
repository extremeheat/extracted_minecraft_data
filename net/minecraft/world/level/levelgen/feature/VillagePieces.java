package net.minecraft.world.level.levelgen.feature;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.VillageConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class VillagePieces {
   public static void addPieces(ChunkGenerator var0, StructureManager var1, BlockPos var2, List var3, WorldgenRandom var4, VillageConfiguration var5) {
      PlainVillagePools.bootstrap();
      SnowyVillagePools.bootstrap();
      SavannaVillagePools.bootstrap();
      DesertVillagePools.bootstrap();
      TaigaVillagePools.bootstrap();
      JigsawPlacement.addPieces(var5.startPool, var5.size, VillagePieces.VillagePiece::new, var0, var1, var2, var3, var4);
   }

   public static class VillagePiece extends PoolElementStructurePiece {
      public VillagePiece(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, Rotation var5, BoundingBox var6) {
         super(StructurePieceType.VILLAGE, var1, var2, var3, var4, var5, var6);
      }

      public VillagePiece(StructureManager var1, CompoundTag var2) {
         super(var1, var2, StructurePieceType.VILLAGE);
      }
   }
}
