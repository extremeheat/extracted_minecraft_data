package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;

public final class RegionFileStorage implements AutoCloseable {
   public static final String ANVIL_EXTENSION = ".mca";
   private static final int MAX_CACHE_SIZE = 256;
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
   private final Path folder;
   private final boolean sync;

   RegionFileStorage(Path var1, boolean var2) {
      super();
      this.folder = var1;
      this.sync = var2;
   }

   private RegionFile getRegionFile(ChunkPos var1) throws IOException {
      long var2 = ChunkPos.asLong(var1.getRegionX(), var1.getRegionZ());
      RegionFile var4 = (RegionFile)this.regionCache.getAndMoveToFirst(var2);
      if (var4 != null) {
         return var4;
      } else {
         if (this.regionCache.size() >= 256) {
            ((RegionFile)this.regionCache.removeLast()).close();
         }

         FileUtil.createDirectoriesSafe(this.folder);
         Path var5 = this.folder.resolve("r." + var1.getRegionX() + "." + var1.getRegionZ() + ".mca");
         RegionFile var6 = new RegionFile(var5, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst(var2, var6);
         return var6;
      }
   }

   @Nullable
   public CompoundTag read(ChunkPos var1) throws IOException {
      RegionFile var2 = this.getRegionFile(var1);

      CompoundTag var4;
      try (DataInputStream var3 = var2.getChunkDataInputStream(var1)) {
         if (var3 == null) {
            return null;
         }

         var4 = NbtIo.read(var3);
      }

      return var4;
   }

   public void scanChunk(ChunkPos var1, StreamTagVisitor var2) throws IOException {
      RegionFile var3 = this.getRegionFile(var1);

      try (DataInputStream var4 = var3.getChunkDataInputStream(var1)) {
         if (var4 != null) {
            NbtIo.parse(var4, var2);
         }
      }
   }

   protected void write(ChunkPos var1, @Nullable CompoundTag var2) throws IOException {
      RegionFile var3 = this.getRegionFile(var1);
      if (var2 == null) {
         var3.clear(var1);
      } else {
         try (DataOutputStream var4 = var3.getChunkDataOutputStream(var1)) {
            NbtIo.write(var2, var4);
         }
      }
   }

   @Override
   public void close() throws IOException {
      ExceptionCollector var1 = new ExceptionCollector();
      ObjectIterator var2 = this.regionCache.values().iterator();

      while(var2.hasNext()) {
         RegionFile var3 = (RegionFile)var2.next();

         try {
            var3.close();
         } catch (IOException var5) {
            var1.add(var5);
         }
      }

      var1.throwIfPresent();
   }

   public void flush() throws IOException {
      ObjectIterator var1 = this.regionCache.values().iterator();

      while(var1.hasNext()) {
         RegionFile var2 = (RegionFile)var1.next();
         var2.flush();
      }
   }
}
