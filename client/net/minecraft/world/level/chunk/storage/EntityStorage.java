package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
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
   private final IOWorker worker;
   private final LongSet emptyChunks = new LongOpenHashSet();
   private final ProcessorMailbox<Runnable> entityDeserializerQueue;
   protected final DataFixer fixerUpper;

   public EntityStorage(ServerLevel var1, Path var2, DataFixer var3, boolean var4, Executor var5) {
      super();
      this.level = var1;
      this.fixerUpper = var3;
      this.entityDeserializerQueue = ProcessorMailbox.create(var5, "entity-deserializer");
      this.worker = new IOWorker(var2, var4, "entities");
   }

   @Override
   public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos var1) {
      return this.emptyChunks.contains(var1.toLong())
         ? CompletableFuture.completedFuture(emptyChunk(var1))
         : this.worker.loadAsync(var1).thenApplyAsync(var2 -> {
            if (var2.isEmpty()) {
               this.emptyChunks.add(var1.toLong());
               return emptyChunk(var1);
            } else {
               try {
                  ChunkPos var3 = readChunkPos(var2.get());
                  if (!Objects.equals(var1, var3)) {
                     LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", new Object[]{var1, var1, var3});
                  }
               } catch (Exception var6) {
                  LOGGER.warn("Failed to parse chunk {} position info", var1, var6);
               }
   
               CompoundTag var7 = this.upgradeChunkTag(var2.get());
               ListTag var4 = var7.getList("Entities", 10);
               List var5 = EntityType.loadEntitiesRecursive(var4, this.level).collect(ImmutableList.toImmutableList());
               return new ChunkEntities<>(var1, var5);
            }
         }, this.entityDeserializerQueue::tell);
   }

   private static ChunkPos readChunkPos(CompoundTag var0) {
      int[] var1 = var0.getIntArray("Position");
      return new ChunkPos(var1[0], var1[1]);
   }

   private static void writeChunkPos(CompoundTag var0, ChunkPos var1) {
      var0.put("Position", new IntArrayTag(new int[]{var1.x, var1.z}));
   }

   private static ChunkEntities<Entity> emptyChunk(ChunkPos var0) {
      return new ChunkEntities<>(var0, ImmutableList.of());
   }

   @Override
   public void storeEntities(ChunkEntities<Entity> var1) {
      ChunkPos var2 = var1.getPos();
      if (var1.isEmpty()) {
         if (this.emptyChunks.add(var2.toLong())) {
            this.worker.store(var2, null);
         }
      } else {
         ListTag var3 = new ListTag();
         var1.getEntities().forEach(var1x -> {
            CompoundTag var2x = new CompoundTag();
            if (var1x.save(var2x)) {
               var3.add(var2x);
            }
         });
         CompoundTag var4 = NbtUtils.addCurrentDataVersion(new CompoundTag());
         var4.put("Entities", var3);
         writeChunkPos(var4, var2);
         this.worker.store(var2, var4).exceptionally(var1x -> {
            LOGGER.error("Failed to store chunk {}", var2, var1x);
            return null;
         });
         this.emptyChunks.remove(var2.toLong());
      }
   }

   @Override
   public void flush(boolean var1) {
      this.worker.synchronize(var1).join();
      this.entityDeserializerQueue.runAll();
   }

   private CompoundTag upgradeChunkTag(CompoundTag var1) {
      int var2 = NbtUtils.getDataVersion(var1, -1);
      return DataFixTypes.ENTITY_CHUNK.updateToCurrentVersion(this.fixerUpper, var1, var2);
   }

   @Override
   public void close() throws IOException {
      this.worker.close();
   }
}
