package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;

public class RegionFile implements AutoCloseable {
   private static final byte[] EMPTY_SECTOR = new byte[4096];
   private final RandomAccessFile file;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private final List<Boolean> sectorFree;

   public RegionFile(File var1) throws IOException {
      super();
      this.file = new RandomAccessFile(var1, "rw");
      if (this.file.length() < 4096L) {
         this.file.write(EMPTY_SECTOR);
         this.file.write(EMPTY_SECTOR);
      }

      int var2;
      if ((this.file.length() & 4095L) != 0L) {
         for(var2 = 0; (long)var2 < (this.file.length() & 4095L); ++var2) {
            this.file.write(0);
         }
      }

      var2 = (int)this.file.length() / 4096;
      this.sectorFree = Lists.newArrayListWithCapacity(var2);

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.sectorFree.add(true);
      }

      this.sectorFree.set(0, false);
      this.sectorFree.set(1, false);
      this.file.seek(0L);

      int var4;
      for(var3 = 0; var3 < 1024; ++var3) {
         var4 = this.file.readInt();
         this.offsets[var3] = var4;
         if (var4 != 0 && (var4 >> 8) + (var4 & 255) <= this.sectorFree.size()) {
            for(int var5 = 0; var5 < (var4 & 255); ++var5) {
               this.sectorFree.set((var4 >> 8) + var5, false);
            }
         }
      }

      for(var3 = 0; var3 < 1024; ++var3) {
         var4 = this.file.readInt();
         this.chunkTimestamps[var3] = var4;
      }

   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(ChunkPos var1) throws IOException {
      int var2 = this.getOffset(var1);
      if (var2 == 0) {
         return null;
      } else {
         int var3 = var2 >> 8;
         int var4 = var2 & 255;
         if (var3 + var4 > this.sectorFree.size()) {
            return null;
         } else {
            this.file.seek((long)(var3 * 4096));
            int var5 = this.file.readInt();
            if (var5 > 4096 * var4) {
               return null;
            } else if (var5 <= 0) {
               return null;
            } else {
               byte var6 = this.file.readByte();
               byte[] var7;
               if (var6 == 1) {
                  var7 = new byte[var5 - 1];
                  this.file.read(var7);
                  return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var7))));
               } else if (var6 == 2) {
                  var7 = new byte[var5 - 1];
                  this.file.read(var7);
                  return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(var7))));
               } else {
                  return null;
               }
            }
         }
      }
   }

   public boolean doesChunkExist(ChunkPos var1) {
      int var2 = this.getOffset(var1);
      if (var2 == 0) {
         return false;
      } else {
         int var3 = var2 >> 8;
         int var4 = var2 & 255;
         if (var3 + var4 > this.sectorFree.size()) {
            return false;
         } else {
            try {
               this.file.seek((long)(var3 * 4096));
               int var5 = this.file.readInt();
               if (var5 > 4096 * var4) {
                  return false;
               } else {
                  return var5 > 0;
               }
            } catch (IOException var6) {
               return false;
            }
         }
      }
   }

   public DataOutputStream getChunkDataOutputStream(ChunkPos var1) {
      return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(var1))));
   }

   protected synchronized void write(ChunkPos var1, byte[] var2, int var3) throws IOException {
      int var4 = this.getOffset(var1);
      int var5 = var4 >> 8;
      int var6 = var4 & 255;
      int var7 = (var3 + 5) / 4096 + 1;
      if (var7 >= 256) {
         throw new RuntimeException(String.format("Too big to save, %d > 1048576", var3));
      } else {
         if (var5 != 0 && var6 == var7) {
            this.write(var5, var2, var3);
         } else {
            int var8;
            for(var8 = 0; var8 < var6; ++var8) {
               this.sectorFree.set(var5 + var8, true);
            }

            var8 = this.sectorFree.indexOf(true);
            int var9 = 0;
            int var10;
            if (var8 != -1) {
               for(var10 = var8; var10 < this.sectorFree.size(); ++var10) {
                  if (var9 != 0) {
                     if ((Boolean)this.sectorFree.get(var10)) {
                        ++var9;
                     } else {
                        var9 = 0;
                     }
                  } else if ((Boolean)this.sectorFree.get(var10)) {
                     var8 = var10;
                     var9 = 1;
                  }

                  if (var9 >= var7) {
                     break;
                  }
               }
            }

            if (var9 >= var7) {
               var5 = var8;
               this.setOffset(var1, var8 << 8 | var7);

               for(var10 = 0; var10 < var7; ++var10) {
                  this.sectorFree.set(var5 + var10, false);
               }

               this.write(var5, var2, var3);
            } else {
               this.file.seek(this.file.length());
               var5 = this.sectorFree.size();

               for(var10 = 0; var10 < var7; ++var10) {
                  this.file.write(EMPTY_SECTOR);
                  this.sectorFree.add(false);
               }

               this.write(var5, var2, var3);
               this.setOffset(var1, var5 << 8 | var7);
            }
         }

         this.setTimestamp(var1, (int)(Util.getEpochMillis() / 1000L));
      }
   }

   private void write(int var1, byte[] var2, int var3) throws IOException {
      this.file.seek((long)(var1 * 4096));
      this.file.writeInt(var3 + 1);
      this.file.writeByte(2);
      this.file.write(var2, 0, var3);
   }

   private int getOffset(ChunkPos var1) {
      return this.offsets[this.getOffsetIndex(var1)];
   }

   public boolean hasChunk(ChunkPos var1) {
      return this.getOffset(var1) != 0;
   }

   private void setOffset(ChunkPos var1, int var2) throws IOException {
      int var3 = this.getOffsetIndex(var1);
      this.offsets[var3] = var2;
      this.file.seek((long)(var3 * 4));
      this.file.writeInt(var2);
   }

   private int getOffsetIndex(ChunkPos var1) {
      return var1.getRegionLocalX() + var1.getRegionLocalZ() * 32;
   }

   private void setTimestamp(ChunkPos var1, int var2) throws IOException {
      int var3 = this.getOffsetIndex(var1);
      this.chunkTimestamps[var3] = var2;
      this.file.seek((long)(4096 + var3 * 4));
      this.file.writeInt(var2);
   }

   public void close() throws IOException {
      this.file.close();
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos var2) {
         super(8096);
         this.pos = var2;
      }

      public void close() throws IOException {
         RegionFile.this.write(this.pos, this.buf, this.count);
      }
   }
}
