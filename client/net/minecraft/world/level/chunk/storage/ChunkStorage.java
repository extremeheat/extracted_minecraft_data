package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage implements AutoCloseable {
   public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
   private final IOWorker worker;
   protected final DataFixer fixerUpper;
   @Nullable
   private volatile LegacyStructureDataHandler legacyStructureHandler;

   public ChunkStorage(Path var1, DataFixer var2, boolean var3) {
      super();
      this.fixerUpper = var2;
      this.worker = new IOWorker(var1, var3, "chunk");
   }

   public boolean isOldChunkAround(ChunkPos var1, int var2) {
      return this.worker.isOldChunkAround(var1, var2);
   }

   public CompoundTag upgradeChunkTag(
      ResourceKey<Level> var1, Supplier<DimensionDataStorage> var2, CompoundTag var3, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> var4
   ) {
      int var5 = getVersion(var3);
      if (var5 < 1493) {
         var3 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var3, var5, 1493);
         if (var3.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            LegacyStructureDataHandler var6 = this.getLegacyStructureHandler(var1, var2);
            var3 = var6.updateFromLegacy(var3);
         }
      }

      injectDatafixingContext(var3, var1, var4);
      var3 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var3, Math.max(1493, var5));
      if (var5 < SharedConstants.getCurrentVersion().getWorldVersion()) {
         var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      }

      var3.remove("__context");
      return var3;
   }

   private LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> var1, Supplier<DimensionDataStorage> var2) {
      LegacyStructureDataHandler var3 = this.legacyStructureHandler;
      if (var3 == null) {
         synchronized(this) {
            var3 = this.legacyStructureHandler;
            if (var3 == null) {
               this.legacyStructureHandler = var3 = LegacyStructureDataHandler.getLegacyStructureHandler(var1, (DimensionDataStorage)var2.get());
            }
         }
      }

      return var3;
   }

   public static void injectDatafixingContext(CompoundTag var0, ResourceKey<Level> var1, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> var2) {
      CompoundTag var3 = new CompoundTag();
      var3.putString("dimension", var1.location().toString());
      var2.ifPresent(var1x -> var3.putString("generator", var1x.location().toString()));
      var0.put("__context", var3);
   }

   public static int getVersion(CompoundTag var0) {
      return var0.contains("DataVersion", 99) ? var0.getInt("DataVersion") : -1;
   }

   public CompletableFuture<Optional<CompoundTag>> read(ChunkPos var1) {
      return this.worker.loadAsync(var1);
   }

   public void write(ChunkPos var1, CompoundTag var2) {
      this.worker.store(var1, var2);
      if (this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex(var1.toLong());
      }
   }

   public void flushWorker() {
      this.worker.synchronize(true).join();
   }

   @Override
   public void close() throws IOException {
      this.worker.close();
   }

   public ChunkScanAccess chunkScanner() {
      return this.worker;
   }
}
