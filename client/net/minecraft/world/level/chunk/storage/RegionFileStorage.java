package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
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
   private final Long2ObjectLinkedOpenHashMap<Optional<RegionFile>> regionCache = new Long2ObjectLinkedOpenHashMap();
   private final RegionStorageInfo info;
   private final Path folder;
   private final boolean sync;

   public RegionFileStorage(final RegionStorageInfo info, final Path folder, final boolean sync) {
      super();
      this.folder = folder;
      this.sync = sync;
      this.info = info;
   }

   private Path regionPath(final ChunkPos pos) {
      Path var10000 = this.folder;
      int var10001 = pos.getRegionX();
      return var10000.resolve("r." + var10001 + "." + pos.getRegionZ() + ".mca");
   }

   private @Nullable RegionFile getRegionFile(final ChunkPos pos, final boolean create) throws IOException {
      long key = ChunkPos.pack(pos.getRegionX(), pos.getRegionZ());
      Optional<RegionFile> cached = (Optional)this.regionCache.getAndMoveToFirst(key);
      if (cached != null) {
         if (cached.isPresent()) {
            return (RegionFile)cached.get();
         }

         if (!create) {
            return null;
         }
      }

      Path path = this.regionPath(pos);
      if (!create && !Files.isRegularFile(path, new LinkOption[0])) {
         this.cache(key, Optional.empty());
         return null;
      } else {
         FileUtil.createDirectoriesSafe(this.folder);
         RegionFile newRegion = new RegionFile(this.info, path, this.folder, this.sync);
         this.cache(key, Optional.of(newRegion));
         return newRegion;
      }
   }

   private RegionFile getOrCreateRegionFile(final ChunkPos pos) throws IOException {
      return (RegionFile)Objects.requireNonNull(this.getRegionFile(pos, true));
   }

   private void cache(final long key, final Optional<RegionFile> entry) throws IOException {
      this.regionCache.putAndMoveToFirst(key, entry);
      if (this.regionCache.size() > 256) {
         Optional<RegionFile> evicted = (Optional)this.regionCache.removeLast();
         if (evicted.isPresent()) {
            ((RegionFile)evicted.get()).close();
         }
      }

   }

   public @Nullable CompoundTag read(final ChunkPos pos) throws IOException {
      RegionFile region = this.getRegionFile(pos, false);
      if (region == null) {
         return null;
      } else {
         DataInputStream regionChunkInputStream = region.getChunkDataInputStream(pos);

         CompoundTag var8;
         label47: {
            try {
               if (regionChunkInputStream == null) {
                  var8 = null;
                  break label47;
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
   }

   public void scanChunk(final ChunkPos pos, final StreamTagVisitor scanner) throws IOException {
      RegionFile region = this.getRegionFile(pos, false);
      if (region != null) {
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
   }

   public void write(final ChunkPos pos, final @Nullable CompoundTag value) throws IOException {
      if (!SharedConstants.DEBUG_DONT_SAVE_WORLD) {
         RegionFile region = this.getOrCreateRegionFile(pos);
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
         Optional<RegionFile> entry = (Optional)var2.next();
         if (!entry.isEmpty()) {
            try {
               ((RegionFile)entry.get()).close();
            } catch (IOException e) {
               exception.add(e);
            }
         }
      }

      exception.throwIfPresent();
   }

   public void flush() throws IOException {
      ObjectIterator var1 = this.regionCache.values().iterator();

      while(var1.hasNext()) {
         Optional<RegionFile> entry = (Optional)var1.next();
         if (entry.isPresent()) {
            ((RegionFile)entry.get()).flush();
         }
      }

   }

   public RegionStorageInfo info() {
      return this.info;
   }
}
