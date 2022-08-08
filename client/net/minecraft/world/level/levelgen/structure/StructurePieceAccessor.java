package net.minecraft.world.level.levelgen.structure;

import javax.annotation.Nullable;

public interface StructurePieceAccessor {
   void addPiece(StructurePiece var1);

   @Nullable
   StructurePiece findCollisionPiece(BoundingBox var1);
}
