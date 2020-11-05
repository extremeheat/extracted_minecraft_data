package net.minecraft.util;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DirectoryLock implements AutoCloseable {
   private final FileChannel lockFile;
   private final FileLock lock;
   private static final ByteBuffer DUMMY;

   public static DirectoryLock create(Path var0) throws IOException {
      Path var1 = var0.resolve("session.lock");
      if (!Files.isDirectory(var0, new LinkOption[0])) {
         Files.createDirectories(var0);
      }

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
         Throwable var3 = null;

         Throwable var6;
         try {
            FileLock var4 = var2.tryLock();
            Throwable var5 = null;

            try {
               var6 = var4 == null;
            } catch (Throwable var33) {
               var6 = var33;
               var5 = var33;
               throw var33;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var32) {
                        var5.addSuppressed(var32);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (Throwable var35) {
            var3 = var35;
            throw var35;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var31) {
                     var3.addSuppressed(var31);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return (boolean)var6;
      } catch (AccessDeniedException var37) {
         return true;
      } catch (NoSuchFileException var38) {
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
