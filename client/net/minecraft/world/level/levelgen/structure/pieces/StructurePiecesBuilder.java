package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;

public class StructurePiecesBuilder implements StructurePieceAccessor {
   private final List<StructurePiece> pieces = Lists.newArrayList();

   public StructurePiecesBuilder() {
      super();
   }

   public void addPiece(StructurePiece var1) {
      this.pieces.add(var1);
   }

   @Nullable
   public StructurePiece findCollisionPiece(BoundingBox var1) {
      return StructurePiece.findCollisionPiece(this.pieces, var1);
   }

   /** @deprecated */
   @Deprecated
   public void offsetPiecesVertically(int var1) {
      Iterator var2 = this.pieces.iterator();

      while(var2.hasNext()) {
         StructurePiece var3 = (StructurePiece)var2.next();
         var3.move(0, var1, 0);
      }

   }

   /** @deprecated */
   @Deprecated
   public int moveBelowSeaLevel(int var1, int var2, RandomSource var3, int var4) {
      int var5 = var1 - var4;
      BoundingBox var6 = this.getBoundingBox();
      int var7 = var6.getYSpan() + var2 + 1;
      if (var7 < var5) {
         var7 += var3.nextInt(var5 - var7);
      }

      int var8 = var7 - var6.maxY();
      this.offsetPiecesVertically(var8);
      return var8;
   }

   /** @deprecated */
   public void moveInsideHeights(RandomSource var1, int var2, int var3) {
      BoundingBox var4 = this.getBoundingBox();
      int var5 = var3 - var2 + 1 - var4.getYSpan();
      int var6;
      if (var5 > 1) {
         var6 = var2 + var1.nextInt(var5);
      } else {
         var6 = var2;
      }

      int var7 = var6 - var4.minY();
      this.offsetPiecesVertically(var7);
   }

   public PiecesContainer build() {
      return new PiecesContainer(this.pieces);
   }

   public void clear() {
      this.pieces.clear();
   }

   public boolean isEmpty() {
      return this.pieces.isEmpty();
   }

   public BoundingBox getBoundingBox() {
      return StructurePiece.createBoundingBox(this.pieces.stream());
   }
}
