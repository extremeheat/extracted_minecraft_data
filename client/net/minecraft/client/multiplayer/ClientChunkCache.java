package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkCache extends ChunkSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LevelChunk emptyChunk;
   private final LevelLightEngine lightEngine;
   private volatile ClientChunkCache.Storage storage;
   private final ClientLevel level;

   public ClientChunkCache(ClientLevel var1, int var2) {
      super();
      this.level = var1;
      this.emptyChunk = new EmptyLevelChunk(var1, new ChunkPos(0, 0));
      this.lightEngine = new LevelLightEngine(this, true, var1.dimensionType().hasSkyLight());
      this.storage = new ClientChunkCache.Storage(calculateStorageRange(var2));
   }

   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   private static boolean isValidChunk(@Nullable LevelChunk var0, int var1, int var2) {
      if (var0 == null) {
         return false;
      } else {
         ChunkPos var3 = var0.getPos();
         return var3.x == var1 && var3.z == var2;
      }
   }

   public void drop(int var1, int var2) {
      if (this.storage.inRange(var1, var2)) {
         int var3 = this.storage.getIndex(var1, var2);
         LevelChunk var4 = this.storage.getChunk(var3);
         if (isValidChunk(var4, var1, var2)) {
            this.storage.replace(var3, var4, (LevelChunk)null);
         }

      }
   }

   @Nullable
   public LevelChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      if (this.storage.inRange(var1, var2)) {
         LevelChunk var5 = this.storage.getChunk(this.storage.getIndex(var1, var2));
         if (isValidChunk(var5, var1, var2)) {
            return var5;
         }
      }

      return var4 ? this.emptyChunk : null;
   }

   public BlockGetter getLevel() {
      return this.level;
   }

   @Nullable
   public LevelChunk replaceWithPacketData(int var1, int var2, @Nullable ChunkBiomeContainer var3, FriendlyByteBuf var4, CompoundTag var5, int var6, boolean var7) {
      if (!this.storage.inRange(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
         return null;
      } else {
         int var8 = this.storage.getIndex(var1, var2);
         LevelChunk var9 = (LevelChunk)this.storage.chunks.get(var8);
         if (!var7 && isValidChunk(var9, var1, var2)) {
            var9.replaceWithPacketData(var3, var4, var5, var6);
         } else {
            if (var3 == null) {
               LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", var1, var2);
               return null;
            }

            var9 = new LevelChunk(this.level, new ChunkPos(var1, var2), var3);
            var9.replaceWithPacketData(var3, var4, var5, var6);
            this.storage.replace(var8, var9);
         }

         LevelChunkSection[] var10 = var9.getSections();
         LevelLightEngine var11 = this.getLightEngine();
         var11.enableLightSources(new ChunkPos(var1, var2), true);

         for(int var12 = 0; var12 < var10.length; ++var12) {
            LevelChunkSection var13 = var10[var12];
            var11.updateSectionStatus(SectionPos.of(var1, var12, var2), LevelChunkSection.isEmpty(var13));
         }

         this.level.onChunkLoaded(var1, var2);
         return var9;
      }
   }

   public void tick(BooleanSupplier var1) {
   }

   public void updateViewCenter(int var1, int var2) {
      this.storage.viewCenterX = var1;
      this.storage.viewCenterZ = var2;
   }

   public void updateViewRadius(int var1) {
      int var2 = this.storage.chunkRadius;
      int var3 = calculateStorageRange(var1);
      if (var2 != var3) {
         ClientChunkCache.Storage var4 = new ClientChunkCache.Storage(var3);
         var4.viewCenterX = this.storage.viewCenterX;
         var4.viewCenterZ = this.storage.viewCenterZ;

         for(int var5 = 0; var5 < this.storage.chunks.length(); ++var5) {
            LevelChunk var6 = (LevelChunk)this.storage.chunks.get(var5);
            if (var6 != null) {
               ChunkPos var7 = var6.getPos();
               if (var4.inRange(var7.x, var7.z)) {
                  var4.replace(var4.getIndex(var7.x, var7.z), var6);
               }
            }
         }

         this.storage = var4;
      }

   }

   private static int calculateStorageRange(int var0) {
      return Math.max(2, var0) + 3;
   }

   public String gatherStats() {
      return "Client Chunk Cache: " + this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   public int getLoadedChunksCount() {
      return this.storage.chunkCount;
   }

   public void onLightUpdate(LightLayer var1, SectionPos var2) {
      Minecraft.getInstance().levelRenderer.setSectionDirty(var2.x(), var2.y(), var2.z());
   }

   public boolean isTickingChunk(BlockPos var1) {
      return this.hasChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   public boolean isEntityTickingChunk(ChunkPos var1) {
      return this.hasChunk(var1.x, var1.z);
   }

   public boolean isEntityTickingChunk(Entity var1) {
      return this.hasChunk(Mth.floor(var1.getX()) >> 4, Mth.floor(var1.getZ()) >> 4);
   }

   // $FF: synthetic method
   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      return this.getChunk(var1, var2, var3, var4);
   }

   final class Storage {
      private final AtomicReferenceArray<LevelChunk> chunks;
      private final int chunkRadius;
      private final int viewRange;
      private volatile int viewCenterX;
      private volatile int viewCenterZ;
      private int chunkCount;

      private Storage(int var2) {
         super();
         this.chunkRadius = var2;
         this.viewRange = var2 * 2 + 1;
         this.chunks = new AtomicReferenceArray(this.viewRange * this.viewRange);
      }

      private int getIndex(int var1, int var2) {
         return Math.floorMod(var2, this.viewRange) * this.viewRange + Math.floorMod(var1, this.viewRange);
      }

      protected void replace(int var1, @Nullable LevelChunk var2) {
         LevelChunk var3 = (LevelChunk)this.chunks.getAndSet(var1, var2);
         if (var3 != null) {
            --this.chunkCount;
            ClientChunkCache.this.level.unload(var3);
         }

         if (var2 != null) {
            ++this.chunkCount;
         }

      }

      protected LevelChunk replace(int var1, LevelChunk var2, @Nullable LevelChunk var3) {
         if (this.chunks.compareAndSet(var1, var2, var3) && var3 == null) {
            --this.chunkCount;
         }

         ClientChunkCache.this.level.unload(var2);
         return var2;
      }

      private boolean inRange(int var1, int var2) {
         return Math.abs(var1 - this.viewCenterX) <= this.chunkRadius && Math.abs(var2 - this.viewCenterZ) <= this.chunkRadius;
      }

      @Nullable
      protected LevelChunk getChunk(int var1) {
         return (LevelChunk)this.chunks.get(var1);
      }

      // $FF: synthetic method
      Storage(int var2, Object var3) {
         this(var2);
      }
   }
}
