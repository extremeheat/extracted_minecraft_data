package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;

public final class RegionFileStorage implements AutoCloseable {
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
   private final File folder;
   private final boolean sync;

   RegionFileStorage(File var1, boolean var2) {
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

         if (!this.folder.exists()) {
            this.folder.mkdirs();
         }

         File var5 = new File(this.folder, "r." + var1.getRegionX() + "." + var1.getRegionZ() + ".mca");
         RegionFile var6 = new RegionFile(var5, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst(var2, var6);
         return var6;
      }
   }

   @Nullable
   public CompoundTag read(ChunkPos var1) throws IOException {
      RegionFile var2 = this.getRegionFile(var1);
      DataInputStream var3 = var2.getChunkDataInputStream(var1);
      Throwable var4 = null;

      CompoundTag var5;
      try {
         if (var3 == null) {
            var5 = null;
            return var5;
         }

         var5 = NbtIo.read((DataInput)var3);
      } catch (Throwable var15) {
         var4 = var15;
         throw var15;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var14) {
                  var4.addSuppressed(var14);
               }
            } else {
               var3.close();
            }
         }

      }

      return var5;
   }

   protected void write(ChunkPos var1, @Nullable CompoundTag var2) throws IOException {
      RegionFile var3 = this.getRegionFile(var1);
      if (var2 == null) {
         var3.clear(var1);
      } else {
         DataOutputStream var4 = var3.getChunkDataOutputStream(var1);
         Throwable var5 = null;

         try {
            NbtIo.write(var2, (DataOutput)var4);
         } catch (Throwable var14) {
            var5 = var14;
            throw var14;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var13) {
                     var5.addSuppressed(var13);
                  }
               } else {
                  var4.close();
               }
            }

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
}
