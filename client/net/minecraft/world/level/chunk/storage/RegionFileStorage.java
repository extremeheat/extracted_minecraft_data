package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;

public final class RegionFileStorage implements AutoCloseable {
   public static final String ANVIL_EXTENSION = ".mca";
   private static final int MAX_CACHE_SIZE = 256;
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
   private final RegionStorageInfo info;
   private final Path folder;
   private final boolean sync;

   RegionFileStorage(RegionStorageInfo var1, Path var2, boolean var3) {
      super();
      this.folder = var2;
      this.sync = var3;
      this.info = var1;
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
         Path var10000 = this.folder;
         int var10001 = var1.getRegionX();
         Path var5 = var10000.resolve("r." + var10001 + "." + var1.getRegionZ() + ".mca");
         RegionFile var6 = new RegionFile(this.info, var5, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst(var2, var6);
         return var6;
      }
   }

   @Nullable
   public CompoundTag read(ChunkPos var1) throws IOException {
      RegionFile var2 = this.getRegionFile(var1);
      DataInputStream var3 = var2.getChunkDataInputStream(var1);

      CompoundTag var4;
      label43: {
         try {
            if (var3 == null) {
               var4 = null;
               break label43;
            }

            var4 = NbtIo.read((DataInput)var3);
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }

         return var4;
      }

      if (var3 != null) {
         var3.close();
      }

      return var4;
   }

   public void scanChunk(ChunkPos var1, StreamTagVisitor var2) throws IOException {
      RegionFile var3 = this.getRegionFile(var1);
      DataInputStream var4 = var3.getChunkDataInputStream(var1);

      try {
         if (var4 != null) {
            NbtIo.parse(var4, var2, NbtAccounter.unlimitedHeap());
         }
      } catch (Throwable var8) {
         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var4 != null) {
         var4.close();
      }

   }

   protected void write(ChunkPos var1, @Nullable CompoundTag var2) throws IOException {
      RegionFile var3 = this.getRegionFile(var1);
      if (var2 == null) {
         var3.clear(var1);
      } else {
         DataOutputStream var4 = var3.getChunkDataOutputStream(var1);

         try {
            NbtIo.write(var2, (DataOutput)var4);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            var4.close();
         }
      }

   }

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

   public RegionStorageInfo info() {
      return this.info;
   }
}
