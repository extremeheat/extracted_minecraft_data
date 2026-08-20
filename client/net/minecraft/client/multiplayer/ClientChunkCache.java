package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ClientChunkCache extends ChunkSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LevelChunk emptyChunk;
   private final LevelLightEngine lightEngine;
   private volatile Storage storage;
   private final ClientLevel level;

   public ClientChunkCache(final ClientLevel level, final int serverChunkRadius) {
      super();
      this.level = level;
      this.emptyChunk = new EmptyLevelChunk(level, new ChunkPos(0, 0), level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS));
      this.lightEngine = new LevelLightEngine(this, true, level.dimensionType().hasSkyLight());
      this.storage = new Storage(calculateStorageRange(serverChunkRadius));
   }

   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   private static boolean isValidChunk(final @Nullable LevelChunk chunk, final int x, final int z) {
      if (chunk == null) {
         return false;
      } else {
         ChunkPos pos = chunk.getPos();
         return pos.x() == x && pos.z() == z;
      }
   }

   public void drop(final ChunkPos pos) {
      if (this.storage.inRange(pos.x(), pos.z())) {
         int index = this.storage.getIndex(pos.x(), pos.z());
         LevelChunk currentChunk = this.storage.getChunk(index);
         if (isValidChunk(currentChunk, pos.x(), pos.z())) {
            this.storage.drop(index, currentChunk);
         }

      }
   }

   public @Nullable LevelChunk getChunk(final int x, final int z, final ChunkStatus targetStatus, final boolean loadOrGenerate) {
      if (this.storage.inRange(x, z)) {
         LevelChunk chunk = this.storage.getChunk(this.storage.getIndex(x, z));
         if (isValidChunk(chunk, x, z)) {
            return chunk;
         }
      }

      return loadOrGenerate ? this.emptyChunk : null;
   }

   public BlockGetter getLevel() {
      return this.level;
   }

   public void replaceBiomes(final int chunkX, final int chunkZ, final FriendlyByteBuf readBuffer) {
      if (!this.storage.inRange(chunkX, chunkZ)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ);
      } else {
         int index = this.storage.getIndex(chunkX, chunkZ);
         LevelChunk chunk = (LevelChunk)this.storage.chunks.get(index);
         if (!isValidChunk(chunk, chunkX, chunkZ)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", chunkX, chunkZ);
         } else {
            chunk.replaceBiomes(readBuffer);
         }

      }
   }

   public @Nullable LevelChunk replaceWithPacketData(final int chunkX, final int chunkZ, final FriendlyByteBuf readBuffer, final Map<Heightmap.Types, long[]> heightmaps, final Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> blockEntities) {
      if (!this.storage.inRange(chunkX, chunkZ)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ);
         return null;
      } else {
         int index = this.storage.getIndex(chunkX, chunkZ);
         LevelChunk chunk = (LevelChunk)this.storage.chunks.get(index);
         ChunkPos pos = new ChunkPos(chunkX, chunkZ);
         if (!isValidChunk(chunk, chunkX, chunkZ)) {
            chunk = new LevelChunk(this.level, pos);
            chunk.replaceWithPacketData(readBuffer, heightmaps, blockEntities);
            this.storage.replace(index, chunk);
         } else {
            chunk.replaceWithPacketData(readBuffer, heightmaps, blockEntities);
            this.storage.refreshEmptySections(chunk);
         }

         this.level.onChunkLoaded(pos);
         return chunk;
      }
   }

   public void tick(final BooleanSupplier haveTime, final boolean tickChunks) {
   }

   public void updateViewCenter(final int x, final int z) {
      this.storage.viewCenterX = x;
      this.storage.viewCenterZ = z;
   }

   public void updateViewRadius(final int viewRange) {
      int chunkRadius = this.storage.chunkRadius;
      int newChunkRadius = calculateStorageRange(viewRange);
      if (chunkRadius != newChunkRadius) {
         Storage newStorage = new Storage(newChunkRadius);
         newStorage.viewCenterX = this.storage.viewCenterX;
         newStorage.viewCenterZ = this.storage.viewCenterZ;

         for(int i = 0; i < this.storage.chunks.length(); ++i) {
            LevelChunk chunk = (LevelChunk)this.storage.chunks.get(i);
            if (chunk != null) {
               ChunkPos pos = chunk.getPos();
               if (newStorage.inRange(pos.x(), pos.z())) {
                  newStorage.replace(newStorage.getIndex(pos.x(), pos.z()), chunk);
               }
            }
         }

         this.storage = newStorage;
      }

   }

   private static int calculateStorageRange(final int viewRange) {
      return Math.max(2, viewRange) + 3;
   }

   public String gatherStats() {
      int var10000 = this.storage.chunks.length();
      return var10000 + ", " + this.getLoadedChunksCount();
   }

   public int getLoadedChunksCount() {
      return this.storage.chunkCount;
   }

   public void onLightUpdate(final LightLayer layer, final SectionPos pos) {
      Minecraft.getInstance().levelExtractor.setSectionDirty(pos.x(), pos.y(), pos.z());
   }

   public LongOpenHashSet addedEmptySections() {
      return this.storage.addedEmptySections[this.storage.updatingSetsIndex];
   }

   public LongOpenHashSet removedEmptySections() {
      return this.storage.removedEmptySections[this.storage.updatingSetsIndex];
   }

   public LongOpenHashSet addedLoadedChunks() {
      return this.storage.addedLoadedChunks[this.storage.updatingSetsIndex];
   }

   public LongOpenHashSet removedLoadedChunks() {
      return this.storage.removedLoadedChunks[this.storage.updatingSetsIndex];
   }

   public void flipUpdateTrackingSets() {
      this.storage.updatingSetsIndex = (this.storage.updatingSetsIndex + 1) % 2;
      this.storage.addedEmptySections[this.storage.updatingSetsIndex].clear();
      this.storage.removedEmptySections[this.storage.updatingSetsIndex].clear();
      this.storage.addedLoadedChunks[this.storage.updatingSetsIndex].clear();
      this.storage.removedLoadedChunks[this.storage.updatingSetsIndex].clear();
   }

   public void onSectionEmptinessChanged(final int sectionX, final int sectionY, final int sectionZ, final boolean empty) {
      this.storage.onSectionEmptinessChanged(sectionX, sectionY, sectionZ, empty);
   }

   private final class Storage {
      private static final int UPDATE_TRACKING_BUFFERS = 2;
      private final AtomicReferenceArray<@Nullable LevelChunk> chunks;
      private final LongOpenHashSet[] addedEmptySections;
      private final LongOpenHashSet[] removedEmptySections;
      private final LongOpenHashSet[] addedLoadedChunks;
      private final LongOpenHashSet[] removedLoadedChunks;
      private int updatingSetsIndex;
      private final int chunkRadius;
      private final int viewRange;
      private volatile int viewCenterX;
      private volatile int viewCenterZ;
      private int chunkCount;

      private Storage(final int chunkRadius) {
         Objects.requireNonNull(ClientChunkCache.this);
         super();
         this.addedEmptySections = new LongOpenHashSet[2];
         this.removedEmptySections = new LongOpenHashSet[2];
         this.addedLoadedChunks = new LongOpenHashSet[2];
         this.removedLoadedChunks = new LongOpenHashSet[2];
         this.chunkRadius = chunkRadius;
         this.viewRange = chunkRadius * 2 + 1;
         this.chunks = new AtomicReferenceArray(this.viewRange * this.viewRange);

         for(int i = 0; i < 2; ++i) {
            this.addedEmptySections[i] = new LongOpenHashSet();
            this.removedEmptySections[i] = new LongOpenHashSet();
            this.addedLoadedChunks[i] = new LongOpenHashSet();
            this.removedLoadedChunks[i] = new LongOpenHashSet();
         }

      }

      private int getIndex(final int chunkX, final int chunkZ) {
         return Math.floorMod(chunkZ, this.viewRange) * this.viewRange + Math.floorMod(chunkX, this.viewRange);
      }

      private void replace(final int index, final @Nullable LevelChunk newChunk) {
         LevelChunk removedChunk = (LevelChunk)this.chunks.getAndSet(index, newChunk);
         if (removedChunk != null) {
            --this.chunkCount;
            this.onChunkRemoved(removedChunk);
            ClientChunkCache.this.level.unload(removedChunk);
         }

         if (newChunk != null) {
            ++this.chunkCount;
            this.onChunkAdded(newChunk);
         }

      }

      private void drop(final int index, final LevelChunk oldChunk) {
         if (this.chunks.compareAndSet(index, oldChunk, (Object)null)) {
            --this.chunkCount;
            this.onChunkRemoved(oldChunk);
         }

         ClientChunkCache.this.level.unload(oldChunk);
      }

      public void onSectionEmptinessChanged(final int sectionX, final int sectionY, final int sectionZ, final boolean empty) {
         if (this.inRange(sectionX, sectionZ)) {
            long sectionNode = SectionPos.asLong(sectionX, sectionY, sectionZ);
            if (empty) {
               this.markSectionEmpty(sectionNode);
            } else {
               this.markSectionNotEmpty(sectionNode);
            }

         }
      }

      private void onChunkRemoved(final LevelChunk chunk) {
         ChunkPos chunkPos = chunk.getPos();
         long chunkNode = chunkPos.pack();
         this.addedLoadedChunks[this.updatingSetsIndex].remove(chunkNode);
         this.removedLoadedChunks[this.updatingSetsIndex].add(chunkNode);
         LevelChunkSection[] sections = chunk.getSections();

         for(int sectionIndex = 0; sectionIndex < sections.length; ++sectionIndex) {
            this.markSectionEmpty(SectionPos.asLong(chunkPos.x(), chunk.getSectionYFromSectionIndex(sectionIndex), chunkPos.z()));
         }

      }

      private void onChunkAdded(final LevelChunk chunk) {
         ChunkPos chunkPos = chunk.getPos();
         long chunkNode = chunkPos.pack();
         this.removedLoadedChunks[this.updatingSetsIndex].remove(chunkNode);
         this.addedLoadedChunks[this.updatingSetsIndex].add(chunkNode);
         LevelChunkSection[] sections = chunk.getSections();

         for(int sectionIndex = 0; sectionIndex < sections.length; ++sectionIndex) {
            LevelChunkSection section = sections[sectionIndex];
            long sectionNode = SectionPos.asLong(chunkPos.x(), chunk.getSectionYFromSectionIndex(sectionIndex), chunkPos.z());
            if (section.hasOnlyAir()) {
               this.markSectionEmpty(sectionNode);
            } else {
               this.markSectionNotEmpty(sectionNode);
            }
         }

      }

      private void refreshEmptySections(final LevelChunk chunk) {
         ChunkPos chunkPos = chunk.getPos();
         LevelChunkSection[] sections = chunk.getSections();

         for(int sectionIndex = 0; sectionIndex < sections.length; ++sectionIndex) {
            LevelChunkSection section = sections[sectionIndex];
            long sectionNode = SectionPos.asLong(chunkPos.x(), chunk.getSectionYFromSectionIndex(sectionIndex), chunkPos.z());
            if (section.hasOnlyAir()) {
               this.markSectionEmpty(sectionNode);
            } else {
               this.markSectionNotEmpty(sectionNode);
            }
         }

      }

      private void markSectionEmpty(final long sectionNode) {
         this.removedEmptySections[this.updatingSetsIndex].remove(sectionNode);
         this.addedEmptySections[this.updatingSetsIndex].add(sectionNode);
      }

      private void markSectionNotEmpty(final long sectionNode) {
         this.addedEmptySections[this.updatingSetsIndex].remove(sectionNode);
         this.removedEmptySections[this.updatingSetsIndex].add(sectionNode);
      }

      private boolean inRange(final int chunkX, final int chunkZ) {
         return Math.abs(chunkX - this.viewCenterX) <= this.chunkRadius && Math.abs(chunkZ - this.viewCenterZ) <= this.chunkRadius;
      }

      public @Nullable LevelChunk getChunk(final int index) {
         return (LevelChunk)this.chunks.get(index);
      }

      private void dumpChunks(final String file) {
         try {
            FileOutputStream stream = new FileOutputStream(file);

            try {
               int chunkRadius = ClientChunkCache.this.storage.chunkRadius;

               for(int z = this.viewCenterZ - chunkRadius; z <= this.viewCenterZ + chunkRadius; ++z) {
                  for(int x = this.viewCenterX - chunkRadius; x <= this.viewCenterX + chunkRadius; ++x) {
                     LevelChunk chunk = (LevelChunk)ClientChunkCache.this.storage.chunks.get(ClientChunkCache.this.storage.getIndex(x, z));
                     if (chunk != null) {
                        ChunkPos pos = chunk.getPos();
                        int var10001 = pos.x();
                        stream.write((var10001 + "\t" + pos.z() + "\t" + chunk.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                     }
                  }
               }
            } catch (Throwable var9) {
               try {
                  stream.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            stream.close();
         } catch (IOException e) {
            ClientChunkCache.LOGGER.error("Failed to dump chunks to file {}", file, e);
         }

      }
   }
}
