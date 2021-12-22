package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public final class StructureStart<C extends FeatureConfiguration> {
   public static final String INVALID_START_ID = "INVALID";
   public static final StructureStart<?> INVALID_START = new StructureStart((StructureFeature)null, new ChunkPos(0, 0), 0, new PiecesContainer(List.of()));
   private final StructureFeature<C> feature;
   private final PiecesContainer pieceContainer;
   private final ChunkPos chunkPos;
   private int references;
   @Nullable
   private volatile BoundingBox cachedBoundingBox;

   public StructureStart(StructureFeature<C> var1, ChunkPos var2, int var3, PiecesContainer var4) {
      super();
      this.feature = var1;
      this.chunkPos = var2;
      this.references = var3;
      this.pieceContainer = var4;
   }

   public BoundingBox getBoundingBox() {
      BoundingBox var1 = this.cachedBoundingBox;
      if (var1 == null) {
         var1 = this.feature.adjustBoundingBox(this.pieceContainer.calculateBoundingBox());
         this.cachedBoundingBox = var1;
      }

      return var1;
   }

   public void placeInChunk(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6) {
      List var7 = this.pieceContainer.pieces();
      if (!var7.isEmpty()) {
         BoundingBox var8 = ((StructurePiece)var7.get(0)).boundingBox;
         BlockPos var9 = var8.getCenter();
         BlockPos var10 = new BlockPos(var9.getX(), var8.minY(), var9.getZ());
         Iterator var11 = var7.iterator();

         while(var11.hasNext()) {
            StructurePiece var12 = (StructurePiece)var11.next();
            if (var12.getBoundingBox().intersects(var5)) {
               var12.postProcess(var1, var2, var3, var4, var5, var6, var10);
            }
         }

         this.feature.getPostPlacementProcessor().afterPlace(var1, var2, var3, var4, var5, var6, this.pieceContainer);
      }
   }

   public CompoundTag createTag(StructurePieceSerializationContext var1, ChunkPos var2) {
      CompoundTag var3 = new CompoundTag();
      if (this.isValid()) {
         var3.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
         var3.putInt("ChunkX", var2.field_504);
         var3.putInt("ChunkZ", var2.field_505);
         var3.putInt("references", this.references);
         var3.put("Children", this.pieceContainer.save(var1));
         return var3;
      } else {
         var3.putString("id", "INVALID");
         return var3;
      }
   }

   public boolean isValid() {
      return !this.pieceContainer.isEmpty();
   }

   public ChunkPos getChunkPos() {
      return this.chunkPos;
   }

   public boolean canBeReferenced() {
      return this.references < this.getMaxReferences();
   }

   public void addReference() {
      ++this.references;
   }

   public int getReferences() {
      return this.references;
   }

   protected int getMaxReferences() {
      return 1;
   }

   public StructureFeature<?> getFeature() {
      return this.feature;
   }

   public List<StructurePiece> getPieces() {
      return this.pieceContainer.pieces();
   }
}
