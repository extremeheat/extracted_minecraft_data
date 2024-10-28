package net.minecraft.world.level.levelgen.structure;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import org.slf4j.Logger;

public final class StructureStart {
   public static final String INVALID_START_ID = "INVALID";
   public static final StructureStart INVALID_START = new StructureStart((Structure)null, new ChunkPos(0, 0), 0, new PiecesContainer(List.of()));
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Structure structure;
   private final PiecesContainer pieceContainer;
   private final ChunkPos chunkPos;
   private int references;
   @Nullable
   private volatile BoundingBox cachedBoundingBox;

   public StructureStart(Structure var1, ChunkPos var2, int var3, PiecesContainer var4) {
      super();
      this.structure = var1;
      this.chunkPos = var2;
      this.references = var3;
      this.pieceContainer = var4;
   }

   @Nullable
   public static StructureStart loadStaticStart(StructurePieceSerializationContext var0, CompoundTag var1, long var2) {
      String var4 = var1.getString("id");
      if ("INVALID".equals(var4)) {
         return INVALID_START;
      } else {
         Registry var5 = var0.registryAccess().registryOrThrow(Registries.STRUCTURE);
         Structure var6 = (Structure)var5.get(ResourceLocation.parse(var4));
         if (var6 == null) {
            LOGGER.error("Unknown stucture id: {}", var4);
            return null;
         } else {
            ChunkPos var7 = new ChunkPos(var1.getInt("ChunkX"), var1.getInt("ChunkZ"));
            int var8 = var1.getInt("references");
            ListTag var9 = var1.getList("Children", 10);

            try {
               PiecesContainer var10 = PiecesContainer.load(var9, var0);
               if (var6 instanceof OceanMonumentStructure) {
                  var10 = OceanMonumentStructure.regeneratePiecesAfterLoad(var7, var2, var10);
               }

               return new StructureStart(var6, var7, var8, var10);
            } catch (Exception var11) {
               LOGGER.error("Failed Start with id {}", var4, var11);
               return null;
            }
         }
      }
   }

   public BoundingBox getBoundingBox() {
      BoundingBox var1 = this.cachedBoundingBox;
      if (var1 == null) {
         var1 = this.structure.adjustBoundingBox(this.pieceContainer.calculateBoundingBox());
         this.cachedBoundingBox = var1;
      }

      return var1;
   }

   public void placeInChunk(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6) {
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

         this.structure.afterPlace(var1, var2, var3, var4, var5, var6, this.pieceContainer);
      }
   }

   public CompoundTag createTag(StructurePieceSerializationContext var1, ChunkPos var2) {
      CompoundTag var3 = new CompoundTag();
      if (this.isValid()) {
         var3.putString("id", var1.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(this.structure).toString());
         var3.putInt("ChunkX", var2.x);
         var3.putInt("ChunkZ", var2.z);
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

   public Structure getStructure() {
      return this.structure;
   }

   public List<StructurePiece> getPieces() {
      return this.pieceContainer.pieces();
   }
}
