package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class RegionFile implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SECTOR_BYTES = 4096;
   @VisibleForTesting
   protected static final int SECTOR_INTS = 1024;
   private static final int CHUNK_HEADER_SIZE = 5;
   private static final int HEADER_OFFSET = 0;
   private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect(1);
   private static final String EXTERNAL_FILE_EXTENSION = ".mcc";
   private static final int EXTERNAL_STREAM_FLAG = 128;
   private static final int EXTERNAL_CHUNK_THRESHOLD = 256;
   private static final int CHUNK_NOT_PRESENT = 0;
   final RegionStorageInfo info;
   private final Path path;
   private final FileChannel file;
   private final Path externalFileDir;
   final RegionFileVersion version;
   private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
   private final IntBuffer offsets;
   private final IntBuffer timestamps;
   @VisibleForTesting
   protected final RegionBitmap usedSectors = new RegionBitmap();

   public RegionFile(RegionStorageInfo var1, Path var2, Path var3, boolean var4) throws IOException {
      this(var1, var2, var3, RegionFileVersion.getSelected(), var4);
   }

   public RegionFile(RegionStorageInfo var1, Path var2, Path var3, RegionFileVersion var4, boolean var5) throws IOException {
      super();
      this.info = var1;
      this.path = var2;
      this.version = var4;
      if (!Files.isDirectory(var3)) {
         throw new IllegalArgumentException("Expected directory, got " + var3.toAbsolutePath());
      } else {
         this.externalFileDir = var3;
         this.offsets = this.header.asIntBuffer();
         this.offsets.limit(1024);
         this.header.position(4096);
         this.timestamps = this.header.asIntBuffer();
         if (var5) {
            this.file = FileChannel.open(var2, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC);
         } else {
            this.file = FileChannel.open(var2, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
         }

         this.usedSectors.force(0, 2);
         this.header.position(0);
         int var6 = this.file.read(this.header, 0L);
         if (var6 != -1) {
            if (var6 != 8192) {
               LOGGER.warn("Region file {} has truncated header: {}", var2, var6);
            }

            long var7 = Files.size(var2);

            for (int var9 = 0; var9 < 1024; var9++) {
               int var10 = this.offsets.get(var9);
               if (var10 != 0) {
                  int var11 = getSectorNumber(var10);
                  int var12 = getNumSectors(var10);
                  if (var11 < 2) {
                     LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", new Object[]{var2, var9, var11});
                     this.offsets.put(var9, 0);
                  } else if (var12 == 0) {
                     LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", var2, var9);
                     this.offsets.put(var9, 0);
                  } else if ((long)var11 * 4096L > var7) {
                     LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", new Object[]{var2, var9, var11});
                     this.offsets.put(var9, 0);
                  } else {
                     this.usedSectors.force(var11, var12);
                  }
               }
            }
         }
      }
   }

   public Path getPath() {
      return this.path;
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
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", new Object[]{var1, var5, var6.remaining()});
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
                  LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", new Object[]{var1, var9, var6.remaining()});
                  return null;
               } else if (var9 < 0) {
                  LOGGER.error("Declared size {} of chunk {} is negative", var7, var1);
                  return null;
               } else {
                  JvmProfiler.INSTANCE.onRegionFileRead(this.info, var1, this.version, var9);
                  return this.createChunkInputStream(var1, var8, createStream(var6, var9));
               }
            }
         }
      }
   }

   private static int getTimestamp() {
      return (int)(Util.getEpochMillis() / 1000L);
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
      if (var4 == RegionFileVersion.VERSION_CUSTOM) {
         String var5 = new DataInputStream(var3).readUTF();
         ResourceLocation var6 = ResourceLocation.tryParse(var5);
         if (var6 != null) {
            LOGGER.error("Unrecognized custom compression {}", var6);
            return null;
         } else {
            LOGGER.error("Invalid custom compression id {}", var5);
            return null;
         }
      } else if (var4 == null) {
         LOGGER.error("Chunk {} has invalid chunk stream version {}", var1, var2);
         return null;
      } else {
         return new DataInputStream(var4.wrap(var3));
      }
   }

   @Nullable
   private DataInputStream createExternalChunkInputStream(ChunkPos var1, byte var2) throws IOException {
      Path var3 = this.getExternalChunkPath(var1);
      if (!Files.isRegularFile(var3)) {
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
      return var0 & 0xFF;
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

                  if (!Files.isRegularFile(this.getExternalChunkPath(var1))) {
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
      return new DataOutputStream(this.version.wrap(new RegionFile.ChunkBuffer(var1)));
   }

   public void flush() throws IOException {
      this.file.force(true);
   }

   public void clear(ChunkPos var1) throws IOException {
      int var2 = getOffsetIndex(var1);
      int var3 = this.offsets.get(var2);
      if (var3 != 0) {
         this.offsets.put(var2, 0);
         this.timestamps.put(var2, getTimestamp());
         this.writeHeader();
         Files.deleteIfExists(this.getExternalChunkPath(var1));
         this.usedSectors.free(getSectorNumber(var3), getNumSectors(var3));
      }
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
         LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", new Object[]{var1, var7, var11});
         var8 = 1;
         var9 = this.usedSectors.allocate(var8);
         var10 = this.writeToExternalFile(var11, var2);
         ByteBuffer var12 = this.createExternalStub();
         this.file.write(var12, (long)(var9 * 4096));
      } else {
         var9 = this.usedSectors.allocate(var8);
         var10 = () -> Files.deleteIfExists(this.getExternalChunkPath(var1));
         this.file.write(var2, (long)(var9 * 4096));
      }

      this.offsets.put(var3, this.packSectorOffset(var9, var8));
      this.timestamps.put(var3, getTimestamp());
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
      Path var3 = Files.createTempFile(this.externalFileDir, "tmp", null);

      try (FileChannel var4 = FileChannel.open(var3, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
         var2.position(5);
         var4.write(var2);
      }

      return () -> Files.move(var3, var1, StandardCopyOption.REPLACE_EXISTING);
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

   @Override
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

      @Override
      public void close() throws IOException {
         ByteBuffer var1 = ByteBuffer.wrap(this.buf, 0, this.count);
         int var2 = this.count - 5 + 1;
         JvmProfiler.INSTANCE.onRegionFileWrite(RegionFile.this.info, this.pos, RegionFile.this.version, var2);
         var1.putInt(0, var2);
         RegionFile.this.write(this.pos, var1);
      }
   }

   interface CommitOp {
      void run() throws IOException;
   }
}
