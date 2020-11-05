package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect(1);
   private final FileChannel file;
   private final Path externalFileDir;
   private final RegionFileVersion version;
   private final ByteBuffer header;
   private final IntBuffer offsets;
   private final IntBuffer timestamps;
   @VisibleForTesting
   protected final RegionBitmap usedSectors;

   public RegionFile(File var1, File var2, boolean var3) throws IOException {
      this(var1.toPath(), var2.toPath(), RegionFileVersion.VERSION_DEFLATE, var3);
   }

   public RegionFile(Path var1, Path var2, RegionFileVersion var3, boolean var4) throws IOException {
      super();
      this.header = ByteBuffer.allocateDirect(8192);
      this.usedSectors = new RegionBitmap();
      this.version = var3;
      if (!Files.isDirectory(var2, new LinkOption[0])) {
         throw new IllegalArgumentException("Expected directory, got " + var2.toAbsolutePath());
      } else {
         this.externalFileDir = var2;
         this.offsets = this.header.asIntBuffer();
         this.offsets.limit(1024);
         this.header.position(4096);
         this.timestamps = this.header.asIntBuffer();
         if (var4) {
            this.file = FileChannel.open(var1, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC);
         } else {
            this.file = FileChannel.open(var1, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
         }

         this.usedSectors.force(0, 2);
         this.header.position(0);
         int var5 = this.file.read(this.header, 0L);
         if (var5 != -1) {
            if (var5 != 8192) {
               LOGGER.warn("Region file {} has truncated header: {}", var1, var5);
            }

            long var6 = Files.size(var1);

            for(int var8 = 0; var8 < 1024; ++var8) {
               int var9 = this.offsets.get(var8);
               if (var9 != 0) {
                  int var10 = getSectorNumber(var9);
                  int var11 = getNumSectors(var9);
                  if (var10 < 2) {
                     LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", var1, var8, var10);
                     this.offsets.put(var8, 0);
                  } else if (var11 == 0) {
                     LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", var1, var8);
                     this.offsets.put(var8, 0);
                  } else if ((long)var10 * 4096L > var6) {
                     LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", var1, var8, var10);
                     this.offsets.put(var8, 0);
                  } else {
                     this.usedSectors.force(var10, var11);
                  }
               }
            }
         }

      }
   }

   private Path getExternalChunkPath(ChunkPos var1) {
      String var2 = "c." + var1.x + "." + var1.z + ".mcc";
      return this.externalFileDir.resolve(var2);
   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(ChunkPos var1) throws IOException {
      int var2 = this.getOffset(var1);
      if (var2 == 0) {
         return null;
      } else {
         int var3 = getSectorNumber(var2);
         int var4 = getNumSectors(var2);
         int var5 = var4 * 4096;
         ByteBuffer var6 = ByteBuffer.allocate(var5);
         this.file.read(var6, (long)(var3 * 4096));
         var6.flip();
         if (var6.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", var1, var5, var6.remaining());
            return null;
         } else {
            int var7 = var6.getInt();
            byte var8 = var6.get();
            if (var7 == 0) {
               LOGGER.warn("Chunk {} is allocated, but stream is missing", var1);
               return null;
            } else {
               int var9 = var7 - 1;
               if (isExternalStreamChunk(var8)) {
                  if (var9 != 0) {
                     LOGGER.warn("Chunk has both internal and external streams");
                  }

                  return this.createExternalChunkInputStream(var1, getExternalChunkVersion(var8));
               } else if (var9 > var6.remaining()) {
                  LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", var1, var9, var6.remaining());
                  return null;
               } else if (var9 < 0) {
                  LOGGER.error("Declared size {} of chunk {} is negative", var7, var1);
                  return null;
               } else {
                  return this.createChunkInputStream(var1, var8, createStream(var6, var9));
               }
            }
         }
      }
   }

   private static boolean isExternalStreamChunk(byte var0) {
      return (var0 & 128) != 0;
   }

   private static byte getExternalChunkVersion(byte var0) {
      return (byte)(var0 & -129);
   }

   @Nullable
   private DataInputStream createChunkInputStream(ChunkPos var1, byte var2, InputStream var3) throws IOException {
      RegionFileVersion var4 = RegionFileVersion.fromId(var2);
      if (var4 == null) {
         LOGGER.error("Chunk {} has invalid chunk stream version {}", var1, var2);
         return null;
      } else {
         return new DataInputStream(new BufferedInputStream(var4.wrap(var3)));
      }
   }

   @Nullable
   private DataInputStream createExternalChunkInputStream(ChunkPos var1, byte var2) throws IOException {
      Path var3 = this.getExternalChunkPath(var1);
      if (!Files.isRegularFile(var3, new LinkOption[0])) {
         LOGGER.error("External chunk path {} is not file", var3);
         return null;
      } else {
         return this.createChunkInputStream(var1, var2, Files.newInputStream(var3));
      }
   }

   private static ByteArrayInputStream createStream(ByteBuffer var0, int var1) {
      return new ByteArrayInputStream(var0.array(), var0.position(), var1);
   }

   private int packSectorOffset(int var1, int var2) {
      return var1 << 8 | var2;
   }

   private static int getNumSectors(int var0) {
      return var0 & 255;
   }

   private static int getSectorNumber(int var0) {
      return var0 >> 8 & 16777215;
   }

   private static int sizeToSectors(int var0) {
      return (var0 + 4096 - 1) / 4096;
   }

   public boolean doesChunkExist(ChunkPos var1) {
      int var2 = this.getOffset(var1);
      if (var2 == 0) {
         return false;
      } else {
         int var3 = getSectorNumber(var2);
         int var4 = getNumSectors(var2);
         ByteBuffer var5 = ByteBuffer.allocate(5);

         try {
            this.file.read(var5, (long)(var3 * 4096));
            var5.flip();
            if (var5.remaining() != 5) {
               return false;
            } else {
               int var6 = var5.getInt();
               byte var7 = var5.get();
               if (isExternalStreamChunk(var7)) {
                  if (!RegionFileVersion.isValidVersion(getExternalChunkVersion(var7))) {
                     return false;
                  }

                  if (!Files.isRegularFile(this.getExternalChunkPath(var1), new LinkOption[0])) {
                     return false;
                  }
               } else {
                  if (!RegionFileVersion.isValidVersion(var7)) {
                     return false;
                  }

                  if (var6 == 0) {
                     return false;
                  }

                  int var8 = var6 - 1;
                  if (var8 < 0 || var8 > 4096 * var4) {
                     return false;
                  }
               }

               return true;
            }
         } catch (IOException var9) {
            return false;
         }
      }
   }

   public DataOutputStream getChunkDataOutputStream(ChunkPos var1) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(this.version.wrap((OutputStream)(new RegionFile.ChunkBuffer(var1)))));
   }

   public void flush() throws IOException {
      this.file.force(true);
   }

   protected synchronized void write(ChunkPos var1, ByteBuffer var2) throws IOException {
      int var3 = getOffsetIndex(var1);
      int var4 = this.offsets.get(var3);
      int var5 = getSectorNumber(var4);
      int var6 = getNumSectors(var4);
      int var7 = var2.remaining();
      int var8 = sizeToSectors(var7);
      int var9;
      RegionFile.CommitOp var10;
      if (var8 >= 256) {
         Path var11 = this.getExternalChunkPath(var1);
         LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", var1, var7, var11);
         var8 = 1;
         var9 = this.usedSectors.allocate(var8);
         var10 = this.writeToExternalFile(var11, var2);
         ByteBuffer var12 = this.createExternalStub();
         this.file.write(var12, (long)(var9 * 4096));
      } else {
         var9 = this.usedSectors.allocate(var8);
         var10 = () -> {
            Files.deleteIfExists(this.getExternalChunkPath(var1));
         };
         this.file.write(var2, (long)(var9 * 4096));
      }

      int var13 = (int)(Util.getEpochMillis() / 1000L);
      this.offsets.put(var3, this.packSectorOffset(var9, var8));
      this.timestamps.put(var3, var13);
      this.writeHeader();
      var10.run();
      if (var5 != 0) {
         this.usedSectors.free(var5, var6);
      }

   }

   private ByteBuffer createExternalStub() {
      ByteBuffer var1 = ByteBuffer.allocate(5);
      var1.putInt(1);
      var1.put((byte)(this.version.getId() | 128));
      var1.flip();
      return var1;
   }

   private RegionFile.CommitOp writeToExternalFile(Path var1, ByteBuffer var2) throws IOException {
      Path var3 = Files.createTempFile(this.externalFileDir, "tmp", (String)null);
      FileChannel var4 = FileChannel.open(var3, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      Throwable var5 = null;

      try {
         var2.position(5);
         var4.write(var2);
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

      return () -> {
         Files.move(var3, var1, StandardCopyOption.REPLACE_EXISTING);
      };
   }

   private void writeHeader() throws IOException {
      this.header.position(0);
      this.file.write(this.header, 0L);
   }

   private int getOffset(ChunkPos var1) {
      return this.offsets.get(getOffsetIndex(var1));
   }

   public boolean hasChunk(ChunkPos var1) {
      return this.getOffset(var1) != 0;
   }

   private static int getOffsetIndex(ChunkPos var0) {
      return var0.getRegionLocalX() + var0.getRegionLocalZ() * 32;
   }

   public void close() throws IOException {
      try {
         this.padToFullSector();
      } finally {
         try {
            this.file.force(true);
         } finally {
            this.file.close();
         }
      }

   }

   private void padToFullSector() throws IOException {
      int var1 = (int)this.file.size();
      int var2 = sizeToSectors(var1) * 4096;
      if (var1 != var2) {
         ByteBuffer var3 = PADDING_BUFFER.duplicate();
         var3.position(0);
         this.file.write(var3, (long)(var2 - 1));
      }

   }

   interface CommitOp {
      void run() throws IOException;
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos var2) {
         super(8096);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(RegionFile.this.version.getId());
         this.pos = var2;
      }

      public void close() throws IOException {
         ByteBuffer var1 = ByteBuffer.wrap(this.buf, 0, this.count);
         var1.putInt(0, this.count - 5 + 1);
         RegionFile.this.write(this.pos, var1);
      }
   }
}
