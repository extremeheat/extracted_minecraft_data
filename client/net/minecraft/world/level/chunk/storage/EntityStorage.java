package net.minecraft.world.level.chunk.storage;

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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.slf4j.Logger;

public class EntityStorage implements EntityPersistentStorage<Entity> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String ENTITIES_TAG = "Entities";
   private static final String POSITION_TAG = "Position";
   private final ServerLevel level;
   private final SimpleRegionStorage simpleRegionStorage;
   private final LongSet emptyChunks = new LongOpenHashSet();
   private final ConsecutiveExecutor entityDeserializerQueue;

   public EntityStorage(SimpleRegionStorage var1, ServerLevel var2, Executor var3) {
      super();
      this.simpleRegionStorage = var1;
      this.level = var2;
      this.entityDeserializerQueue = new ConsecutiveExecutor(var3, "entity-deserializer");
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
                  ChunkPos var3 = (ChunkPos)((CompoundTag)var2x.get()).read("Position", ChunkPos.CODEC).orElseThrow();
                  if (!Objects.equals(var1, var3)) {
                     LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", new Object[]{var1, var1, var3});
                     this.level.getServer().reportMisplacedChunk(var3, var1, this.simpleRegionStorage.storageInfo());
                  }
               } catch (Exception var11) {
                  LOGGER.warn("Failed to parse chunk {} position info", var1, var11);
                  this.level.getServer().reportChunkLoadFailure(var11, this.simpleRegionStorage.storageInfo(), var1);
               }

               CompoundTag var12 = this.simpleRegionStorage.upgradeChunkTag((CompoundTag)var2x.get(), -1);

               try (ProblemReporter.ScopedCollector var4 = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath(var1), LOGGER)) {
                  ValueInput var5 = TagValueInput.create(var4, this.level.registryAccess(), var12);
                  ValueInput.ValueInputList var6 = var5.childrenListOrEmpty("Entities");
                  List var7 = EntityType.loadEntitiesRecursive(var6, this.level, EntitySpawnReason.LOAD).toList();
                  return new ChunkEntities(var1, var7);
               }
            }
         };
         ConsecutiveExecutor var10002 = this.entityDeserializerQueue;
         Objects.requireNonNull(var10002);
         return var2.thenApplyAsync(var10001, var10002::schedule);
      }
   }

   private static ChunkEntities<Entity> emptyChunk(ChunkPos var0) {
      return new ChunkEntities<Entity>(var0, List.of());
   }

   public void storeEntities(ChunkEntities<Entity> var1) {
      ChunkPos var2 = var1.getPos();
      if (var1.isEmpty()) {
         if (this.emptyChunks.add(var2.toLong())) {
            this.reportSaveFailureIfPresent(this.simpleRegionStorage.write(var2, (CompoundTag)null), var2);
         }

      } else {
         try (ProblemReporter.ScopedCollector var3 = new ProblemReporter.ScopedCollector(ChunkAccess.problemPath(var2), LOGGER)) {
            ListTag var4 = new ListTag();
            var1.getEntities().forEach((var2x) -> {
               TagValueOutput var3x = TagValueOutput.createWithContext(var3.forChild(var2x.problemPath()), var2x.registryAccess());
               if (var2x.save(var3x)) {
                  CompoundTag var4x = var3x.buildResult();
                  var4.add(var4x);
               }

            });
            CompoundTag var5 = NbtUtils.addCurrentDataVersion(new CompoundTag());
            var5.put("Entities", var4);
            var5.store("Position", ChunkPos.CODEC, var2);
            this.reportSaveFailureIfPresent(this.simpleRegionStorage.write(var2, var5), var2);
            this.emptyChunks.remove(var2.toLong());
         }

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
