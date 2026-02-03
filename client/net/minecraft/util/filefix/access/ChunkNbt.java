package net.minecraft.util.filefix.access;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;

public class ChunkNbt implements AutoCloseable {
   private final SimpleRegionStorage storage;
   private final int targetVersion;

   public ChunkNbt(final RegionStorageInfo info, final Path path, final DataFixTypes type, final int targetVersion) {
      super();
      this.targetVersion = targetVersion;
      this.storage = new SimpleRegionStorage(info, path, DataFixers.getDataFixer(), false, type);
   }

   public void updateChunk(final ChunkPos pos, final CompoundTag dataFixContext, final UnaryOperator<CompoundTag> fixer) {
      this.storage.read(pos).thenApply((tag) -> tag.map((tag1) -> this.storage.upgradeChunkTag(tag1, -1, dataFixContext, this.targetVersion)).map(fixer)).thenCompose((value) -> (CompletionStage)value.map((tag2) -> this.storage.write(pos, tag2)).orElse(CompletableFuture.completedFuture((Object)null))).join();
   }

   public void close() throws IOException {
      this.storage.close();
   }
}
