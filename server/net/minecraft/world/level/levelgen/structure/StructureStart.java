package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class StructureStart<C extends FeatureConfiguration> {
   public static final StructureStart<?> INVALID_START;
   private final StructureFeature<C> feature;
   protected final List<StructurePiece> pieces = Lists.newArrayList();
   protected BoundingBox boundingBox;
   private final int chunkX;
   private final int chunkZ;
   private int references;
   protected final WorldgenRandom random;

   public StructureStart(StructureFeature<C> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
      super();
      this.feature = var1;
      this.chunkX = var2;
      this.chunkZ = var3;
      this.references = var5;
      this.random = new WorldgenRandom();
      this.random.setLargeFeatureSeed(var6, var2, var3);
      this.boundingBox = var4;
   }

   public abstract void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, C var7);

   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public List<StructurePiece> getPieces() {
      return this.pieces;
   }

   public void placeInChunk(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6) {
      synchronized(this.pieces) {
         if (!this.pieces.isEmpty()) {
            BoundingBox var8 = ((StructurePiece)this.pieces.get(0)).boundingBox;
            Vec3i var9 = var8.getCenter();
            BlockPos var10 = new BlockPos(var9.getX(), var8.y0, var9.getZ());
            Iterator var11 = this.pieces.iterator();

            while(var11.hasNext()) {
               StructurePiece var12 = (StructurePiece)var11.next();
               if (var12.getBoundingBox().intersects(var5) && !var12.postProcess(var1, var2, var3, var4, var5, var6, var10)) {
                  var11.remove();
               }
            }

            this.calculateBoundingBox();
         }
      }
   }

   protected void calculateBoundingBox() {
      this.boundingBox = BoundingBox.getUnknownBox();
      Iterator var1 = this.pieces.iterator();

      while(var1.hasNext()) {
         StructurePiece var2 = (StructurePiece)var1.next();
         this.boundingBox.expand(var2.getBoundingBox());
      }

   }

   public CompoundTag createTag(int var1, int var2) {
      CompoundTag var3 = new CompoundTag();
      if (this.isValid()) {
         var3.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
         var3.putInt("ChunkX", var1);
         var3.putInt("ChunkZ", var2);
         var3.putInt("references", this.references);
         var3.put("BB", this.boundingBox.createTag());
         ListTag var4 = new ListTag();
         synchronized(this.pieces) {
            Iterator var6 = this.pieces.iterator();

            while(true) {
               if (!var6.hasNext()) {
                  break;
               }

               StructurePiece var7 = (StructurePiece)var6.next();
               var4.add(var7.createTag());
            }
         }

         var3.put("Children", var4);
         return var3;
      } else {
         var3.putString("id", "INVALID");
         return var3;
      }
   }

   protected void moveBelowSeaLevel(int var1, Random var2, int var3) {
      int var4 = var1 - var3;
      int var5 = this.boundingBox.getYSpan() + 1;
      if (var5 < var4) {
         var5 += var2.nextInt(var4 - var5);
      }

      int var6 = var5 - this.boundingBox.y1;
      this.boundingBox.move(0, var6, 0);
      Iterator var7 = this.pieces.iterator();

      while(var7.hasNext()) {
         StructurePiece var8 = (StructurePiece)var7.next();
         var8.move(0, var6, 0);
      }

   }

   protected void moveInsideHeights(Random var1, int var2, int var3) {
      int var4 = var3 - var2 + 1 - this.boundingBox.getYSpan();
      int var5;
      if (var4 > 1) {
         var5 = var2 + var1.nextInt(var4);
      } else {
         var5 = var2;
      }

      int var6 = var5 - this.boundingBox.y0;
      this.boundingBox.move(0, var6, 0);
      Iterator var7 = this.pieces.iterator();

      while(var7.hasNext()) {
         StructurePiece var8 = (StructurePiece)var7.next();
         var8.move(0, var6, 0);
      }

   }

   public boolean isValid() {
      return !this.pieces.isEmpty();
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public BlockPos getLocatePos() {
      return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
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

   static {
      INVALID_START = new StructureStart<MineshaftConfiguration>(StructureFeature.MINESHAFT, 0, 0, BoundingBox.getUnknownBox(), 0, 0L) {
         public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, MineshaftConfiguration var7) {
         }
      };
   }
}
