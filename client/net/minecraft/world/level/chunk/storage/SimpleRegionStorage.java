package net.minecraft.world.level.chunk.storage;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public class SimpleRegionStorage implements AutoCloseable {
   private final IOWorker worker;
   private final DataFixer fixerUpper;
   private final DataFixTypes dataFixType;
   private final Supplier<LegacyTagFixer> legacyFixer;

   public SimpleRegionStorage(final RegionStorageInfo info, final Path folder, final DataFixer fixerUpper, final boolean syncWrites, final DataFixTypes dataFixType) {
      this(info, folder, fixerUpper, syncWrites, dataFixType, LegacyTagFixer.EMPTY);
   }

   public SimpleRegionStorage(final RegionStorageInfo info, final Path folder, final DataFixer fixerUpper, final boolean syncWrites, final DataFixTypes dataFixType, final Supplier<LegacyTagFixer> legacyFixer) {
      super();
      this.fixerUpper = fixerUpper;
      this.dataFixType = dataFixType;
      this.worker = new IOWorker(info, folder, syncWrites);
      Objects.requireNonNull(legacyFixer);
      this.legacyFixer = Suppliers.memoize(legacyFixer::get);
   }

   public boolean isOldChunkAround(final ChunkPos pos, final int range) {
      return this.worker.isOldChunkAround(pos, range);
   }

   public CompletableFuture<Optional<CompoundTag>> read(final ChunkPos pos) {
      return this.worker.loadAsync(pos);
   }

   public CompletableFuture<Void> write(final ChunkPos pos, final CompoundTag value) {
      return this.write(pos, (Supplier)(() -> value));
   }

   public CompletableFuture<Void> write(final ChunkPos pos, final Supplier<CompoundTag> supplier) {
      this.markChunkDone(pos);
      return this.worker.store(pos, supplier);
   }

   public CompoundTag upgradeChunkTag(CompoundTag chunkTag, final int defaultVersion, final @Nullable CompoundTag dataFixContextTag, final int targetVersion) {
      int version = NbtUtils.getDataVersion(chunkTag, defaultVersion);
      if (version >= targetVersion) {
         return chunkTag;
      } else {
         try {
            chunkTag = ((LegacyTagFixer)this.legacyFixer.get()).applyFix(chunkTag);
            injectDatafixingContext(chunkTag, dataFixContextTag);
            chunkTag = this.dataFixType.updateToCurrentVersion(this.fixerUpper, chunkTag, Math.max(((LegacyTagFixer)this.legacyFixer.get()).targetDataVersion(), version));
            removeDatafixingContext(chunkTag);
            NbtUtils.addCurrentDataVersion(chunkTag);
            return chunkTag;
         } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Updated chunk");
            CrashReportCategory details = report.addCategory("Updated chunk details");
            details.setDetail("Data version", version);
            throw new ReportedException(report);
         }
      }
   }

   public CompoundTag upgradeChunkTag(final CompoundTag chunkTag, final int defaultVersion) {
      return this.upgradeChunkTag(chunkTag, defaultVersion, (CompoundTag)null, SharedConstants.getCurrentVersion().dataVersion().version());
   }

   public Dynamic<Tag> upgradeChunkTag(final Dynamic<Tag> chunkTag, final int defaultVersion) {
      return new Dynamic(chunkTag.getOps(), this.upgradeChunkTag((CompoundTag)chunkTag.getValue(), defaultVersion, (CompoundTag)null, SharedConstants.getCurrentVersion().dataVersion().version()));
   }

   public static void injectDatafixingContext(final CompoundTag chunkTag, final @Nullable CompoundTag contextTag) {
      if (contextTag != null) {
         chunkTag.put("__context", contextTag);
      }

   }

   private static void removeDatafixingContext(final CompoundTag chunkTag) {
      chunkTag.remove("__context");
   }

   protected void markChunkDone(final ChunkPos pos) {
      ((LegacyTagFixer)this.legacyFixer.get()).markChunkDone(pos);
   }

   public CompletableFuture<Void> synchronize(final boolean flush) {
      return this.worker.synchronize(flush);
   }

   public void close() throws IOException {
      this.worker.close();
   }

   public ChunkScanAccess chunkScanner() {
      return this.worker;
   }

   public RegionStorageInfo storageInfo() {
      return this.worker.storageInfo();
   }
}
