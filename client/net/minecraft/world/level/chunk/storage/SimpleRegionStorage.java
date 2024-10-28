package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;

public class SimpleRegionStorage implements AutoCloseable {
   private final IOWorker worker;
   private final DataFixer fixerUpper;
   private final DataFixTypes dataFixType;

   public SimpleRegionStorage(RegionStorageInfo var1, Path var2, DataFixer var3, boolean var4, DataFixTypes var5) {
      super();
      this.fixerUpper = var3;
      this.dataFixType = var5;
      this.worker = new IOWorker(var1, var2, var4);
   }

   public CompletableFuture<Optional<CompoundTag>> read(ChunkPos var1) {
      return this.worker.loadAsync(var1);
   }

   public CompletableFuture<Void> write(ChunkPos var1, @Nullable CompoundTag var2) {
      return this.worker.store(var1, var2);
   }

   public CompoundTag upgradeChunkTag(CompoundTag var1, int var2) {
      int var3 = NbtUtils.getDataVersion(var1, var2);
      return this.dataFixType.updateToCurrentVersion(this.fixerUpper, var1, var3);
   }

   public Dynamic<Tag> upgradeChunkTag(Dynamic<Tag> var1, int var2) {
      return this.dataFixType.updateToCurrentVersion(this.fixerUpper, var1, var2);
   }

   public CompletableFuture<Void> synchronize(boolean var1) {
      return this.worker.synchronize(var1);
   }

   public void close() throws IOException {
      this.worker.close();
   }

   public RegionStorageInfo storageInfo() {
      return this.worker.storageInfo();
   }
}
