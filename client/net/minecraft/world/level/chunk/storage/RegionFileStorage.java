package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.util.FileUtil;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public final class RegionFileStorage implements AutoCloseable {
   public static final String ANVIL_EXTENSION = ".mca";
   private static final int MAX_CACHE_SIZE = 256;
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
   private final RegionStorageInfo info;
   private final Path folder;
   private final boolean sync;

   public RegionFileStorage(final RegionStorageInfo info, final Path folder, final boolean sync) {
      super();
      this.folder = folder;
      this.sync = sync;
      this.info = info;
   }

   private RegionFile getRegionFile(final ChunkPos pos) throws IOException {
      long key = ChunkPos.pack(pos.getRegionX(), pos.getRegionZ());
      RegionFile region = (RegionFile)this.regionCache.getAndMoveToFirst(key);
      if (region != null) {
         return region;
      } else {
         if (this.regionCache.size() >= 256) {
            ((RegionFile)this.regionCache.removeLast()).close();
         }

         FileUtil.createDirectoriesSafe(this.folder);
         Path var10000 = this.folder;
         int var10001 = pos.getRegionX();
         Path file = var10000.resolve("r." + var10001 + "." + pos.getRegionZ() + ".mca");
         RegionFile newRegion = new RegionFile(this.info, file, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst(key, newRegion);
         return newRegion;
      }
   }

   public @Nullable CompoundTag read(final ChunkPos pos) throws IOException {
      RegionFile region = this.getRegionFile(pos);
      DataInputStream regionChunkInputStream = region.getChunkDataInputStream(pos);

      CompoundTag var8;
      label43: {
         try {
            if (regionChunkInputStream == null) {
               var8 = null;
               break label43;
            }

            var8 = NbtIo.read((DataInput)regionChunkInputStream);
         } catch (Throwable var7) {
            if (regionChunkInputStream != null) {
               try {
                  regionChunkInputStream.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (regionChunkInputStream != null) {
            regionChunkInputStream.close();
         }

         return var8;
      }

      if (regionChunkInputStream != null) {
         regionChunkInputStream.close();
      }

      return var8;
   }

   public void scanChunk(final ChunkPos pos, final StreamTagVisitor scanner) throws IOException {
      RegionFile region = this.getRegionFile(pos);
      DataInputStream regionChunkInputStream = region.getChunkDataInputStream(pos);

      try {
         if (regionChunkInputStream != null) {
            NbtIo.parse(regionChunkInputStream, scanner, NbtAccounter.unlimitedHeap());
         }
      } catch (Throwable var8) {
         if (regionChunkInputStream != null) {
            try {
               regionChunkInputStream.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (regionChunkInputStream != null) {
         regionChunkInputStream.close();
      }

   }

   public void write(final ChunkPos pos, final @Nullable CompoundTag value) throws IOException {
      if (!SharedConstants.DEBUG_DONT_SAVE_WORLD) {
         RegionFile region = this.getRegionFile(pos);
         if (value == null) {
            region.clear(pos);
         } else {
            DataOutputStream output = region.getChunkDataOutputStream(pos);

            try {
               NbtIo.write(value, (DataOutput)output);
            } catch (Throwable var8) {
               if (output != null) {
                  try {
                     output.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (output != null) {
               output.close();
            }
         }

      }
   }

   public void close() throws IOException {
      ExceptionCollector<IOException> exception = new ExceptionCollector<IOException>();
      ObjectIterator var2 = this.regionCache.values().iterator();

      while(var2.hasNext()) {
         RegionFile regionFile = (RegionFile)var2.next();

         try {
            regionFile.close();
         } catch (IOException e) {
            exception.add(e);
         }
      }

      exception.throwIfPresent();
   }

   public void flush() throws IOException {
      ObjectIterator var1 = this.regionCache.values().iterator();

      while(var1.hasNext()) {
         RegionFile regionFile = (RegionFile)var1.next();
         regionFile.flush();
      }

   }

   public RegionStorageInfo info() {
      return this.info;
   }
}
