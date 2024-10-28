package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class LinkFSProvider extends FileSystemProvider {
   public static final String SCHEME = "x-mc-link";

   LinkFSProvider() {
      super();
   }

   public String getScheme() {
      return "x-mc-link";
   }

   public FileSystem newFileSystem(URI var1, Map<String, ?> var2) {
      throw new UnsupportedOperationException();
   }

   public FileSystem getFileSystem(URI var1) {
      throw new UnsupportedOperationException();
   }

   public Path getPath(URI var1) {
      throw new UnsupportedOperationException();
   }

   public SeekableByteChannel newByteChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      if (!var2.contains(StandardOpenOption.CREATE_NEW) && !var2.contains(StandardOpenOption.CREATE) && !var2.contains(StandardOpenOption.APPEND) && !var2.contains(StandardOpenOption.WRITE)) {
         Path var4 = toLinkPath(var1).toAbsolutePath().getTargetPath();
         if (var4 == null) {
            throw new NoSuchFileException(var1.toString());
         } else {
            return Files.newByteChannel(var4, var2, var3);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public DirectoryStream<Path> newDirectoryStream(Path var1, final DirectoryStream.Filter<? super Path> var2) throws IOException {
      final PathContents.DirectoryContents var3 = toLinkPath(var1).toAbsolutePath().getDirectoryContents();
      if (var3 == null) {
         throw new NotDirectoryException(var1.toString());
      } else {
         return new DirectoryStream<Path>(this) {
            public Iterator<Path> iterator() {
               return var3.children().values().stream().filter((var1) -> {
                  try {
                     return var2.accept(var1);
                  } catch (IOException var3x) {
                     throw new DirectoryIteratorException(var3x);
                  }
               }).map((var0) -> {
                  return var0;
               }).iterator();
            }

            public void close() {
            }
         };
      }
   }

   public void createDirectory(Path var1, FileAttribute<?>... var2) {
      throw new ReadOnlyFileSystemException();
   }

   public void delete(Path var1) {
      throw new ReadOnlyFileSystemException();
   }

   public void copy(Path var1, Path var2, CopyOption... var3) {
      throw new ReadOnlyFileSystemException();
   }

   public void move(Path var1, Path var2, CopyOption... var3) {
      throw new ReadOnlyFileSystemException();
   }

   public boolean isSameFile(Path var1, Path var2) {
      return var1 instanceof LinkFSPath && var2 instanceof LinkFSPath && var1.equals(var2);
   }

   public boolean isHidden(Path var1) {
      return false;
   }

   public FileStore getFileStore(Path var1) {
      return toLinkPath(var1).getFileSystem().store();
   }

   public void checkAccess(Path var1, AccessMode... var2) throws IOException {
      if (var2.length == 0 && !toLinkPath(var1).exists()) {
         throw new NoSuchFileException(var1.toString());
      } else {
         AccessMode[] var3 = var2;
         int var4 = var2.length;
         int var5 = 0;

         while(var5 < var4) {
            AccessMode var6 = var3[var5];
            switch (var6) {
               case READ:
                  if (!toLinkPath(var1).exists()) {
                     throw new NoSuchFileException(var1.toString());
                  }
               default:
                  ++var5;
                  break;
               case EXECUTE:
               case WRITE:
                  throw new AccessDeniedException(var6.toString());
            }
         }

      }
   }

   @Nullable
   public <V extends FileAttributeView> V getFileAttributeView(Path var1, Class<V> var2, LinkOption... var3) {
      LinkFSPath var4 = toLinkPath(var1);
      return var2 == BasicFileAttributeView.class ? var4.getBasicAttributeView() : null;
   }

   public <A extends BasicFileAttributes> A readAttributes(Path var1, Class<A> var2, LinkOption... var3) throws IOException {
      LinkFSPath var4 = toLinkPath(var1).toAbsolutePath();
      if (var2 == BasicFileAttributes.class) {
         return var4.getBasicAttributes();
      } else {
         throw new UnsupportedOperationException("Attributes of type " + var2.getName() + " not supported");
      }
   }

   public Map<String, Object> readAttributes(Path var1, String var2, LinkOption... var3) {
      throw new UnsupportedOperationException();
   }

   public void setAttribute(Path var1, String var2, Object var3, LinkOption... var4) {
      throw new ReadOnlyFileSystemException();
   }

   private static LinkFSPath toLinkPath(@Nullable Path var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var0 instanceof LinkFSPath) {
         LinkFSPath var1 = (LinkFSPath)var0;
         return var1;
      } else {
         throw new ProviderMismatchException();
      }
   }
}
