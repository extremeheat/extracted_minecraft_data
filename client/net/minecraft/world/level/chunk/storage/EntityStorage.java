package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import org.slf4j.Logger;

public class EntityStorage implements EntityPersistentStorage<Entity> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String ENTITIES_TAG = "Entities";
   private static final String POSITION_TAG = "Position";
   private final ServerLevel level;
   private final SimpleRegionStorage simpleRegionStorage;
   private final LongSet emptyChunks = new LongOpenHashSet();
   private final ProcessorMailbox<Runnable> entityDeserializerQueue;

   public EntityStorage(SimpleRegionStorage var1, ServerLevel var2, Executor var3) {
      super();
      this.simpleRegionStorage = var1;
      this.level = var2;
      this.entityDeserializerQueue = ProcessorMailbox.create(var3, "entity-deserializer");
   }

   public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos var1) {
      if (this.emptyChunks.contains(var1.toLong())) {
         return CompletableFuture.completedFuture(emptyChunk(var1));
      } else {
         CompletableFuture var2 = this.simpleRegionStorage.read(var1);
         this.reportLoadFailureIfPresent(var2, var1);
         Function var10001 = (var2x) -> {
            if (var2x.isEmpty()) {
               this.emptyChunks.add(var1.toLong());
               return emptyChunk(var1);
            } else {
               try {
                  ChunkPos var3 = readChunkPos((CompoundTag)var2x.get());
                  if (!Objects.equals(var1, var3)) {
                     LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", new Object[]{var1, var1, var3});
                     this.level.getServer().reportMisplacedChunk(var3, var1, this.simpleRegionStorage.storageInfo());
                  }
               } catch (Exception var6) {
                  LOGGER.warn("Failed to parse chunk {} position info", var1, var6);
                  this.level.getServer().reportChunkLoadFailure(var6, this.simpleRegionStorage.storageInfo(), var1);
               }

               CompoundTag var7 = this.simpleRegionStorage.upgradeChunkTag((CompoundTag)((CompoundTag)var2x.get()), -1);
               ListTag var4 = var7.getList("Entities", 10);
               List var5 = (List)EntityType.loadEntitiesRecursive(var4, this.level).collect(ImmutableList.toImmutableList());
               return new ChunkEntities(var1, var5);
            }
         };
         ProcessorMailbox var10002 = this.entityDeserializerQueue;
         Objects.requireNonNull(var10002);
         return var2.thenApplyAsync(var10001, var10002::tell);
      }
   }

   private static ChunkPos readChunkPos(CompoundTag var0) {
      int[] var1 = var0.getIntArray("Position");
      return new ChunkPos(var1[0], var1[1]);
   }

   private static void writeChunkPos(CompoundTag var0, ChunkPos var1) {
      var0.put("Position", new IntArrayTag(new int[]{var1.x, var1.z}));
   }

   private static ChunkEntities<Entity> emptyChunk(ChunkPos var0) {
      return new ChunkEntities(var0, ImmutableList.of());
   }

   public void storeEntities(ChunkEntities<Entity> var1) {
      ChunkPos var2 = var1.getPos();
      if (var1.isEmpty()) {
         if (this.emptyChunks.add(var2.toLong())) {
            this.reportSaveFailureIfPresent(this.simpleRegionStorage.write(var2, (CompoundTag)null), var2);
         }

      } else {
         ListTag var3 = new ListTag();
         var1.getEntities().forEach((var1x) -> {
            CompoundTag var2 = new CompoundTag();
            if (var1x.save(var2)) {
               var3.add(var2);
            }

         });
         CompoundTag var4 = NbtUtils.addCurrentDataVersion(new CompoundTag());
         var4.put("Entities", var3);
         writeChunkPos(var4, var2);
         this.reportSaveFailureIfPresent(this.simpleRegionStorage.write(var2, var4), var2);
         this.emptyChunks.remove(var2.toLong());
      }
   }

   private void reportSaveFailureIfPresent(CompletableFuture<?> var1, ChunkPos var2) {
      var1.exceptionally((var2x) -> {
         LOGGER.error("Failed to store entity chunk {}", var2, var2x);
         this.level.getServer().reportChunkSaveFailure(var2x, this.simpleRegionStorage.storageInfo(), var2);
         return null;
      });
   }

   private void reportLoadFailureIfPresent(CompletableFuture<?> var1, ChunkPos var2) {
      var1.exceptionally((var2x) -> {
         LOGGER.error("Failed to load entity chunk {}", var2, var2x);
         this.level.getServer().reportChunkLoadFailure(var2x, this.simpleRegionStorage.storageInfo(), var2);
         return null;
      });
   }

   public void flush(boolean var1) {
      this.simpleRegionStorage.synchronize(var1).join();
      this.entityDeserializerQueue.runAll();
   }

   public void close() throws IOException {
      this.simpleRegionStorage.close();
   }
}
