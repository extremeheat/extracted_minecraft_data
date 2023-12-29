package net.minecraft.util;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.FileUtil;

public class DirectoryLock implements AutoCloseable {
   public static final String LOCK_FILE = "session.lock";
   private final FileChannel lockFile;
   private final FileLock lock;
   private static final ByteBuffer DUMMY;

   public static DirectoryLock create(Path var0) throws IOException {
      Path var1 = var0.resolve("session.lock");
      FileUtil.createDirectoriesSafe(var0);
      FileChannel var2 = FileChannel.open(var1, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

      try {
         var2.write(DUMMY.duplicate());
         var2.force(true);
         FileLock var3 = var2.tryLock();
         if (var3 == null) {
            throw DirectoryLock.LockException.alreadyLocked(var1);
         } else {
            return new DirectoryLock(var2, var3);
         }
      } catch (IOException var6) {
         try {
            var2.close();
         } catch (IOException var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }
   }

   private DirectoryLock(FileChannel var1, FileLock var2) {
      super();
      this.lockFile = var1;
      this.lock = var2;
   }

   @Override
   public void close() throws IOException {
      try {
         if (this.lock.isValid()) {
            this.lock.release();
         }
      } finally {
         if (this.lockFile.isOpen()) {
            this.lockFile.close();
         }
      }
   }

   public boolean isValid() {
      return this.lock.isValid();
   }

   public static boolean isLocked(Path var0) throws IOException {
      Path var1 = var0.resolve("session.lock");

      try {
         boolean var4;
         try (
            FileChannel var2 = FileChannel.open(var1, StandardOpenOption.WRITE);
            FileLock var3 = var2.tryLock();
         ) {
            var4 = var3 == null;
         }

         return var4;
      } catch (AccessDeniedException var10) {
         return true;
      } catch (NoSuchFileException var11) {
         return false;
      }
   }

   static {
      byte[] var0 = "\u2603".getBytes(Charsets.UTF_8);
      DUMMY = ByteBuffer.allocateDirect(var0.length);
      DUMMY.put(var0);
      DUMMY.flip();
   }

   public static class LockException extends IOException {
      private LockException(Path var1, String var2) {
         super(var1.toAbsolutePath() + ": " + var2);
      }

      public static DirectoryLock.LockException alreadyLocked(Path var0) {
         return new DirectoryLock.LockException(var0, "already locked (possibly by other Minecraft instance?)");
      }
   }
}
