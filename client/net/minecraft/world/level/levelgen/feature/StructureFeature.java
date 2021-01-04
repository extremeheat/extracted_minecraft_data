package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfiguration> extends Feature<C> {
   private static final Logger LOGGER = LogManager.getLogger();

   public StructureFeature(Function<Dynamic<?>, ? extends C> var1) {
      super(var1, false);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, C var5) {
      if (!var1.getLevelData().isGenerateMapFeatures()) {
         return false;
      } else {
         int var6 = var4.getX() >> 4;
         int var7 = var4.getZ() >> 4;
         int var8 = var6 << 4;
         int var9 = var7 << 4;
         boolean var10 = false;
         LongIterator var11 = var1.getChunk(var6, var7).getReferencesForFeature(this.getFeatureName()).iterator();

         while(var11.hasNext()) {
            Long var12 = (Long)var11.next();
            ChunkPos var13 = new ChunkPos(var12);
            StructureStart var14 = var1.getChunk(var13.x, var13.z).getStartForFeature(this.getFeatureName());
            if (var14 != null && var14 != StructureStart.INVALID_START) {
               var14.postProcess(var1, var3, new BoundingBox(var8, var9, var8 + 15, var9 + 15), new ChunkPos(var6, var7));
               var10 = true;
            }
         }

         return var10;
      }
   }

   protected StructureStart getStructureAt(LevelAccessor var1, BlockPos var2, boolean var3) {
      List var4 = this.dereferenceStructureStarts(var1, var2.getX() >> 4, var2.getZ() >> 4);
      Iterator var5 = var4.iterator();

      while(true) {
         StructureStart var6;
         do {
            do {
               if (!var5.hasNext()) {
                  return StructureStart.INVALID_START;
               }

               var6 = (StructureStart)var5.next();
            } while(!var6.isValid());
         } while(!var6.getBoundingBox().isInside(var2));

         if (!var3) {
            return var6;
         }

         Iterator var7 = var6.getPieces().iterator();

         while(var7.hasNext()) {
            StructurePiece var8 = (StructurePiece)var7.next();
            if (var8.getBoundingBox().isInside(var2)) {
               return var6;
            }
         }
      }
   }

   public boolean isInsideBoundingFeature(LevelAccessor var1, BlockPos var2) {
      return this.getStructureAt(var1, var2, false).isValid();
   }

   public boolean isInsideFeature(LevelAccessor var1, BlockPos var2) {
      return this.getStructureAt(var1, var2, true).isValid();
   }

   @Nullable
   public BlockPos getNearestGeneratedFeature(Level var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, BlockPos var3, int var4, boolean var5) {
      if (!var2.getBiomeSource().canGenerateStructure(this)) {
         return null;
      } else {
         int var6 = var3.getX() >> 4;
         int var7 = var3.getZ() >> 4;
         int var8 = 0;

         for(WorldgenRandom var9 = new WorldgenRandom(); var8 <= var4; ++var8) {
            for(int var10 = -var8; var10 <= var8; ++var10) {
               boolean var11 = var10 == -var8 || var10 == var8;

               for(int var12 = -var8; var12 <= var8; ++var12) {
                  boolean var13 = var12 == -var8 || var12 == var8;
                  if (var11 || var13) {
                     ChunkPos var14 = this.getPotentialFeatureChunkFromLocationWithOffset(var2, var9, var6, var7, var10, var12);
                     StructureStart var15 = var1.getChunk(var14.x, var14.z, ChunkStatus.STRUCTURE_STARTS).getStartForFeature(this.getFeatureName());
                     if (var15 != null && var15.isValid()) {
                        if (var5 && var15.canBeReferenced()) {
                           var15.addReference();
                           return var15.getLocatePos();
                        }

                        if (!var5) {
                           return var15.getLocatePos();
                        }
                     }

                     if (var8 == 0) {
                        break;
                     }
                  }
               }

               if (var8 == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> dereferenceStructureStarts(LevelAccessor var1, int var2, int var3) {
      ArrayList var4 = Lists.newArrayList();
      ChunkAccess var5 = var1.getChunk(var2, var3, ChunkStatus.STRUCTURE_REFERENCES);
      LongIterator var6 = var5.getReferencesForFeature(this.getFeatureName()).iterator();

      while(var6.hasNext()) {
         long var7 = var6.nextLong();
         ChunkAccess var9 = var1.getChunk(ChunkPos.getX(var7), ChunkPos.getZ(var7), ChunkStatus.STRUCTURE_STARTS);
         StructureStart var10 = var9.getStartForFeature(this.getFeatureName());
         if (var10 != null) {
            var4.add(var10);
         }
      }

      return var4;
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      return new ChunkPos(var3 + var5, var4 + var6);
   }

   public abstract boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4);

   public abstract StructureFeature.StructureStartFactory getStartFactory();

   public abstract String getFeatureName();

   public abstract int getLookupRange();

   public interface StructureStartFactory {
      StructureStart create(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7);
   }
}
