package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
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

   public ChunkStorage(RegionStorageInfo var1, Path var2, DataFixer var3, boolean var4) {
      super();
      this.fixerUpper = var3;
      this.worker = new IOWorker(var1, var2, var4);
   }

   public boolean isOldChunkAround(ChunkPos var1, int var2) {
      return this.worker.isOldChunkAround(var1, var2);
   }

   public CompoundTag upgradeChunkTag(ResourceKey<Level> var1, Supplier<DimensionDataStorage> var2, CompoundTag var3, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> var4) {
      int var5 = getVersion(var3);
      if (var5 == SharedConstants.getCurrentVersion().getDataVersion().getVersion()) {
         return var3;
      } else {
         try {
            if (var5 < 1493) {
               var3 = DataFixTypes.CHUNK.update(this.fixerUpper, (CompoundTag)var3, var5, 1493);
               if (var3.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                  LegacyStructureDataHandler var6 = this.getLegacyStructureHandler(var1, var2);
                  var3 = var6.updateFromLegacy(var3);
               }
            }

            injectDatafixingContext(var3, var1, var4);
            var3 = DataFixTypes.CHUNK.updateToCurrentVersion(this.fixerUpper, var3, Math.max(1493, var5));
            removeDatafixingContext(var3);
            NbtUtils.addCurrentDataVersion(var3);
            return var3;
         } catch (Exception var9) {
            CrashReport var7 = CrashReport.forThrowable(var9, "Updated chunk");
            CrashReportCategory var8 = var7.addCategory("Updated chunk details");
            var8.setDetail("Data version", (Object)var5);
            throw new ReportedException(var7);
         }
      }
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

   public static void injectDatafixingContext(CompoundTag var0, ResourceKey<Level> var1, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> var2) {
      CompoundTag var3 = new CompoundTag();
      var3.putString("dimension", var1.location().toString());
      var2.ifPresent((var1x) -> {
         var3.putString("generator", var1x.location().toString());
      });
      var0.put("__context", var3);
   }

   private static void removeDatafixingContext(CompoundTag var0) {
      var0.remove("__context");
   }

   public static int getVersion(CompoundTag var0) {
      return NbtUtils.getDataVersion(var0, -1);
   }

   public CompletableFuture<Optional<CompoundTag>> read(ChunkPos var1) {
      return this.worker.loadAsync(var1);
   }

   public CompletableFuture<Void> write(ChunkPos var1, CompoundTag var2) {
      this.handleLegacyStructureIndex(var1);
      return this.worker.store(var1, var2);
   }

   protected void handleLegacyStructureIndex(ChunkPos var1) {
      if (this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex(var1.toLong());
      }

   }

   public void flushWorker() {
      this.worker.synchronize(true).join();
   }

   public void close() throws IOException {
      this.worker.close();
   }

   public ChunkScanAccess chunkScanner() {
      return this.worker;
   }

   protected RegionStorageInfo storageInfo() {
      return this.worker.storageInfo();
   }
}
