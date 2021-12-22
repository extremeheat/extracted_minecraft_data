package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureFeatureManager {
   private final LevelAccessor level;
   private final WorldGenSettings worldGenSettings;
   private final StructureCheck structureCheck;

   public StructureFeatureManager(LevelAccessor var1, WorldGenSettings var2, StructureCheck var3) {
      super();
      this.level = var1;
      this.worldGenSettings = var2;
      this.structureCheck = var3;
   }

   public StructureFeatureManager forWorldGenRegion(WorldGenRegion var1) {
      if (var1.getLevel() != this.level) {
         ServerLevel var10002 = var1.getLevel();
         throw new IllegalStateException("Using invalid feature manager (source level: " + var10002 + ", region: " + var1);
      } else {
         return new StructureFeatureManager(var1, this.worldGenSettings, this.structureCheck);
      }
   }

   public List<? extends StructureStart<?>> startsForFeature(SectionPos var1, StructureFeature<?> var2) {
      LongSet var3 = this.level.getChunk(var1.method_78(), var1.method_80(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(var2);
      Builder var4 = ImmutableList.builder();
      LongIterator var5 = var3.iterator();

      while(var5.hasNext()) {
         long var6 = (Long)var5.next();
         SectionPos var8 = SectionPos.method_72(new ChunkPos(var6), this.level.getMinSection());
         StructureStart var9 = this.getStartForFeature(var8, var2, this.level.getChunk(var8.method_78(), var8.method_80(), ChunkStatus.STRUCTURE_STARTS));
         if (var9 != null && var9.isValid()) {
            var4.add(var9);
         }
      }

      return var4.build();
   }

   @Nullable
   public StructureStart<?> getStartForFeature(SectionPos var1, StructureFeature<?> var2, FeatureAccess var3) {
      return var3.getStartForFeature(var2);
   }

   public void setStartForFeature(SectionPos var1, StructureFeature<?> var2, StructureStart<?> var3, FeatureAccess var4) {
      var4.setStartForFeature(var2, var3);
   }

   public void addReferenceForFeature(SectionPos var1, StructureFeature<?> var2, long var3, FeatureAccess var5) {
      var5.addReferenceForFeature(var2, var3);
   }

   public boolean shouldGenerateFeatures() {
      return this.worldGenSettings.generateFeatures();
   }

   public StructureStart<?> getStructureAt(BlockPos var1, StructureFeature<?> var2) {
      Iterator var3 = this.startsForFeature(SectionPos.method_71(var1), var2).iterator();

      StructureStart var4;
      do {
         if (!var3.hasNext()) {
            return StructureStart.INVALID_START;
         }

         var4 = (StructureStart)var3.next();
      } while(!var4.getBoundingBox().isInside(var1));

      return var4;
   }

   public StructureStart<?> getStructureWithPieceAt(BlockPos var1, StructureFeature<?> var2) {
      Iterator var3 = this.startsForFeature(SectionPos.method_71(var1), var2).iterator();

      while(var3.hasNext()) {
         StructureStart var4 = (StructureStart)var3.next();
         Iterator var5 = var4.getPieces().iterator();

         while(var5.hasNext()) {
            StructurePiece var6 = (StructurePiece)var5.next();
            if (var6.getBoundingBox().isInside(var1)) {
               return var4;
            }
         }
      }

      return StructureStart.INVALID_START;
   }

   public boolean hasAnyStructureAt(BlockPos var1) {
      SectionPos var2 = SectionPos.method_71(var1);
      return this.level.getChunk(var2.method_78(), var2.method_80(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
   }

   public StructureCheckResult checkStructurePresence(ChunkPos var1, StructureFeature<?> var2, boolean var3) {
      return this.structureCheck.checkStart(var1, var2, var3);
   }

   public void addReference(StructureStart<?> var1) {
      var1.addReference();
      this.structureCheck.incrementReference(var1.getChunkPos(), var1.getFeature());
   }
}
