package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.SharedConstants;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityStorage implements EntityPersistentStorage<Entity> {
   private static final Logger LOGGER = LogManager.getLogger();
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

   public CompletableFuture<ChunkEntities<Entity>> loadEntities(ChunkPos var1) {
      if (this.emptyChunks.contains(var1.toLong())) {
         return CompletableFuture.completedFuture(emptyChunk(var1));
      } else {
         CompletableFuture var10000 = this.worker.loadAsync(var1);
         Function var10001 = (var2) -> {
            if (var2 == null) {
               this.emptyChunks.add(var1.toLong());
               return emptyChunk(var1);
            } else {
               try {
                  ChunkPos var3 = readChunkPos(var2);
                  if (!Objects.equals(var1, var3)) {
                     LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", var1, var1, var3);
                  }
               } catch (Exception var6) {
                  LOGGER.warn("Failed to parse chunk {} position info", var1, var6);
               }

               CompoundTag var7 = this.upgradeChunkTag(var2);
               ListTag var4 = var7.getList("Entities", 10);
               List var5 = (List)EntityType.loadEntitiesRecursive(var4, this.level).collect(ImmutableList.toImmutableList());
               return new ChunkEntities(var1, var5);
            }
         };
         ProcessorMailbox var10002 = this.entityDeserializerQueue;
         Objects.requireNonNull(var10002);
         return var10000.thenApplyAsync(var10001, var10002::tell);
      }
   }

   private static ChunkPos readChunkPos(CompoundTag var0) {
      int[] var1 = var0.getIntArray("Position");
      return new ChunkPos(var1[0], var1[1]);
   }

   private static void writeChunkPos(CompoundTag var0, ChunkPos var1) {
      var0.put("Position", new IntArrayTag(new int[]{var1.field_504, var1.field_505}));
   }

   private static ChunkEntities<Entity> emptyChunk(ChunkPos var0) {
      return new ChunkEntities(var0, ImmutableList.of());
   }

   public void storeEntities(ChunkEntities<Entity> var1) {
      ChunkPos var2 = var1.getPos();
      if (var1.isEmpty()) {
         if (this.emptyChunks.add(var2.toLong())) {
            this.worker.store(var2, (CompoundTag)null);
         }

      } else {
         ListTag var3 = new ListTag();
         var1.getEntities().forEach((var1x) -> {
            CompoundTag var2 = new CompoundTag();
            if (var1x.save(var2)) {
               var3.add(var2);
            }

         });
         CompoundTag var4 = new CompoundTag();
         var4.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
         var4.put("Entities", var3);
         writeChunkPos(var4, var2);
         this.worker.store(var2, var4).exceptionally((var1x) -> {
            LOGGER.error("Failed to store chunk {}", var2, var1x);
            return null;
         });
         this.emptyChunks.remove(var2.toLong());
      }
   }

   public void flush(boolean var1) {
      this.worker.synchronize(var1).join();
      this.entityDeserializerQueue.runAll();
   }

   private CompoundTag upgradeChunkTag(CompoundTag var1) {
      int var2 = getVersion(var1);
      return NbtUtils.update(this.fixerUpper, DataFixTypes.ENTITY_CHUNK, var1, var2);
   }

   public static int getVersion(CompoundTag var0) {
      return var0.contains("DataVersion", 99) ? var0.getInt("DataVersion") : -1;
   }

   public void close() throws IOException {
      this.worker.close();
   }
}
