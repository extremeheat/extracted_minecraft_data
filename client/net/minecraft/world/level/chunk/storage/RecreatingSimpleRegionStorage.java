package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.io.FileUtils;

public class RecreatingSimpleRegionStorage extends SimpleRegionStorage {
   private final IOWorker writeWorker;
   private final Path writeFolder;

   public RecreatingSimpleRegionStorage(RegionStorageInfo var1, Path var2, RegionStorageInfo var3, Path var4, DataFixer var5, boolean var6, DataFixTypes var7) {
      super(var1, var2, var5, var6, var7);
      this.writeFolder = var4;
      this.writeWorker = new IOWorker(var3, var4, var6);
   }

   public CompletableFuture<Void> write(ChunkPos var1, @Nullable CompoundTag var2) {
      return this.writeWorker.store(var1, var2);
   }

   public void close() throws IOException {
      super.close();
      this.writeWorker.close();
      if (this.writeFolder.toFile().exists()) {
         FileUtils.deleteDirectory(this.writeFolder.toFile());
      }

   }
}
