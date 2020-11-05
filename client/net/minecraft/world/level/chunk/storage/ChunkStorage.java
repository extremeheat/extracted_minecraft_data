package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage implements AutoCloseable {
   private final IOWorker worker;
   protected final DataFixer fixerUpper;
   @Nullable
   private LegacyStructureDataHandler legacyStructureHandler;

   public ChunkStorage(File var1, DataFixer var2, boolean var3) {
      super();
      this.fixerUpper = var2;
      this.worker = new IOWorker(var1, var3, "chunk");
   }

   public CompoundTag upgradeChunkTag(ResourceKey<Level> var1, Supplier<DimensionDataStorage> var2, CompoundTag var3) {
      int var4 = getVersion(var3);
      boolean var5 = true;
      if (var4 < 1493) {
         var3 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var3, var4, 1493);
         if (var3.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.legacyStructureHandler == null) {
               this.legacyStructureHandler = LegacyStructureDataHandler.getLegacyStructureHandler(var1, (DimensionDataStorage)var2.get());
            }

            var3 = this.legacyStructureHandler.updateFromLegacy(var3);
         }
      }

      var3 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var3, Math.max(1493, var4));
      if (var4 < SharedConstants.getCurrentVersion().getWorldVersion()) {
         var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      }

      return var3;
   }

   public static int getVersion(CompoundTag var0) {
      return var0.contains("DataVersion", 99) ? var0.getInt("DataVersion") : -1;
   }

   @Nullable
   public CompoundTag read(ChunkPos var1) throws IOException {
      return this.worker.load(var1);
   }

   public void write(ChunkPos var1, CompoundTag var2) {
      this.worker.store(var1, var2);
      if (this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex(var1.toLong());
      }

   }

   public void flushWorker() {
      this.worker.synchronize().join();
   }

   public void close() throws IOException {
      this.worker.close();
   }
}
