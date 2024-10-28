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
         FileChannel var2 = FileChannel.open(var1, StandardOpenOption.WRITE);

         boolean var4;
         try {
            FileLock var3 = var2.tryLock();

            try {
               var4 = var3 == null;
            } catch (Throwable var8) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (Throwable var9) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }
            }

            throw var9;
         }

         if (var2 != null) {
            var2.close();
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
         String var10001 = String.valueOf(var1.toAbsolutePath());
         super(var10001 + ": " + var2);
      }

      public static LockException alreadyLocked(Path var0) {
         return new LockException(var0, "already locked (possibly by other Minecraft instance?)");
      }
   }
}
