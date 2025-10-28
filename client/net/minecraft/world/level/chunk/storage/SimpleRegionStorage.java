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

   public SimpleRegionStorage(RegionStorageInfo var1, Path var2, DataFixer var3, boolean var4, DataFixTypes var5) {
      this(var1, var2, var3, var4, var5, LegacyTagFixer.EMPTY);
   }

   public SimpleRegionStorage(RegionStorageInfo var1, Path var2, DataFixer var3, boolean var4, DataFixTypes var5, Supplier<LegacyTagFixer> var6) {
      super();
      this.fixerUpper = var3;
      this.dataFixType = var5;
      this.worker = new IOWorker(var1, var2, var4);
      Objects.requireNonNull(var6);
      this.legacyFixer = Suppliers.memoize(var6::get);
   }

   public boolean isOldChunkAround(ChunkPos var1, int var2) {
      return this.worker.isOldChunkAround(var1, var2);
   }

   public CompletableFuture<Optional<CompoundTag>> read(ChunkPos var1) {
      return this.worker.loadAsync(var1);
   }

   public CompletableFuture<Void> write(ChunkPos var1, CompoundTag var2) {
      return this.write(var1, (Supplier)(() -> var2));
   }

   public CompletableFuture<Void> write(ChunkPos var1, Supplier<CompoundTag> var2) {
      this.markChunkDone(var1);
      return this.worker.store(var1, var2);
   }

   public CompoundTag upgradeChunkTag(CompoundTag var1, int var2, @Nullable CompoundTag var3) {
      int var4 = NbtUtils.getDataVersion(var1, var2);
      if (var4 == SharedConstants.getCurrentVersion().dataVersion().version()) {
         return var1;
      } else {
         try {
            var1 = ((LegacyTagFixer)this.legacyFixer.get()).applyFix(var1);
            injectDatafixingContext(var1, var3);
            var1 = this.dataFixType.updateToCurrentVersion(this.fixerUpper, var1, Math.max(((LegacyTagFixer)this.legacyFixer.get()).targetDataVersion(), var4));
            removeDatafixingContext(var1);
            NbtUtils.addCurrentDataVersion(var1);
            return var1;
         } catch (Exception var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Updated chunk");
            CrashReportCategory var7 = var6.addCategory("Updated chunk details");
            var7.setDetail("Data version", var4);
            throw new ReportedException(var6);
         }
      }
   }

   public CompoundTag upgradeChunkTag(CompoundTag var1, int var2) {
      return this.upgradeChunkTag(var1, var2, (CompoundTag)null);
   }

   public Dynamic<Tag> upgradeChunkTag(Dynamic<Tag> var1, int var2) {
      return new Dynamic(var1.getOps(), this.upgradeChunkTag((CompoundTag)var1.getValue(), var2, (CompoundTag)null));
   }

   public static void injectDatafixingContext(CompoundTag var0, @Nullable CompoundTag var1) {
      if (var1 != null) {
         var0.put("__context", var1);
      }

   }

   private static void removeDatafixingContext(CompoundTag var0) {
      var0.remove("__context");
   }

   protected void markChunkDone(ChunkPos var1) {
      ((LegacyTagFixer)this.legacyFixer.get()).markChunkDone(var1);
   }

   public CompletableFuture<Void> synchronize(boolean var1) {
      return this.worker.synchronize(var1);
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
