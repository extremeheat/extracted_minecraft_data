package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;

public class ClientChunkCache extends ChunkSource {
   static final Logger LOGGER = LogUtils.getLogger();
   private final LevelChunk emptyChunk;
   private final LevelLightEngine lightEngine;
   volatile ClientChunkCache.Storage storage;
   final ClientLevel level;

   public ClientChunkCache(ClientLevel var1, int var2) {
      super();
      this.level = var1;
      this.emptyChunk = new EmptyLevelChunk(var1, new ChunkPos(0, 0), var1.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS));
      this.lightEngine = new LevelLightEngine(this, true, var1.dimensionType().hasSkyLight());
      this.storage = new ClientChunkCache.Storage(calculateStorageRange(var2));
   }

   @Override
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

   public void drop(ChunkPos var1) {
      if (this.storage.inRange(var1.x, var1.z)) {
         int var2 = this.storage.getIndex(var1.x, var1.z);
         LevelChunk var3 = this.storage.getChunk(var2);
         if (isValidChunk(var3, var1.x, var1.z)) {
            this.storage.replace(var2, var3, null);
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

   @Override
   public BlockGetter getLevel() {
      return this.level;
   }

   public void replaceBiomes(int var1, int var2, FriendlyByteBuf var3) {
      if (!this.storage.inRange(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
      } else {
         int var4 = this.storage.getIndex(var1, var2);
         LevelChunk var5 = this.storage.chunks.get(var4);
         if (!isValidChunk(var5, var1, var2)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", var1, var2);
         } else {
            var5.replaceBiomes(var3);
         }
      }
   }

   @Nullable
   public LevelChunk replaceWithPacketData(
      int var1, int var2, FriendlyByteBuf var3, CompoundTag var4, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> var5
   ) {
      if (!this.storage.inRange(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
         return null;
      } else {
         int var6 = this.storage.getIndex(var1, var2);
         LevelChunk var7 = this.storage.chunks.get(var6);
         ChunkPos var8 = new ChunkPos(var1, var2);
         if (!isValidChunk(var7, var1, var2)) {
            var7 = new LevelChunk(this.level, var8);
            var7.replaceWithPacketData(var3, var4, var5);
            this.storage.replace(var6, var7);
         } else {
            var7.replaceWithPacketData(var3, var4, var5);
         }

         this.level.onChunkLoaded(var8);
         return var7;
      }
   }

   @Override
   public void tick(BooleanSupplier var1, boolean var2) {
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
            LevelChunk var6 = this.storage.chunks.get(var5);
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

   @Override
   public String gatherStats() {
      return this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   @Override
   public int getLoadedChunksCount() {
      return this.storage.chunkCount;
   }

   @Override
   public void onLightUpdate(LightLayer var1, SectionPos var2) {
      Minecraft.getInstance().levelRenderer.setSectionDirty(var2.x(), var2.y(), var2.z());
   }

   final class Storage {
      final AtomicReferenceArray<LevelChunk> chunks;
      final int chunkRadius;
      private final int viewRange;
      volatile int viewCenterX;
      volatile int viewCenterZ;
      int chunkCount;

      Storage(int var2) {
         super();
         this.chunkRadius = var2;
         this.viewRange = var2 * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.viewRange * this.viewRange);
      }

      int getIndex(int var1, int var2) {
         return Math.floorMod(var2, this.viewRange) * this.viewRange + Math.floorMod(var1, this.viewRange);
      }

      protected void replace(int var1, @Nullable LevelChunk var2) {
         LevelChunk var3 = this.chunks.getAndSet(var1, var2);
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

      boolean inRange(int var1, int var2) {
         return Math.abs(var1 - this.viewCenterX) <= this.chunkRadius && Math.abs(var2 - this.viewCenterZ) <= this.chunkRadius;
      }

      @Nullable
      protected LevelChunk getChunk(int var1) {
         return this.chunks.get(var1);
      }

      private void dumpChunks(String var1) {
         try (FileOutputStream var2 = new FileOutputStream(var1)) {
            int var3 = ClientChunkCache.this.storage.chunkRadius;

            for(int var4 = this.viewCenterZ - var3; var4 <= this.viewCenterZ + var3; ++var4) {
               for(int var5 = this.viewCenterX - var3; var5 <= this.viewCenterX + var3; ++var5) {
                  LevelChunk var6 = ClientChunkCache.this.storage.chunks.get(ClientChunkCache.this.storage.getIndex(var5, var4));
                  if (var6 != null) {
                     ChunkPos var7 = var6.getPos();
                     var2.write((var7.x + "\t" + var7.z + "\t" + var6.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                  }
               }
            }
         } catch (IOException var10) {
            ClientChunkCache.LOGGER.error("Failed to dump chunks to file {}", var1, var10);
         }
      }
   }
}
