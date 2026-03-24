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
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;

class LinkFSProvider extends FileSystemProvider {
   public static final String SCHEME = "x-mc-link";

   LinkFSProvider() {
      super();
   }

   public String getScheme() {
      return "x-mc-link";
   }

   public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) {
      throw new UnsupportedOperationException();
   }

   public FileSystem getFileSystem(final URI uri) {
      throw new UnsupportedOperationException();
   }

   public Path getPath(final URI uri) {
      throw new UnsupportedOperationException();
   }

   public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
      if (!options.contains(StandardOpenOption.CREATE_NEW) && !options.contains(StandardOpenOption.CREATE) && !options.contains(StandardOpenOption.APPEND) && !options.contains(StandardOpenOption.WRITE)) {
         Path targetPath = toLinkPath(path).toAbsolutePath().getTargetPath();
         if (targetPath == null) {
            throw new NoSuchFileException(path.toString());
         } else {
            return Files.newByteChannel(targetPath, options, attrs);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {
      final PathContents.DirectoryContents directoryContents = toLinkPath(dir).toAbsolutePath().getDirectoryContents();
      if (directoryContents == null) {
         throw new NotDirectoryException(dir.toString());
      } else {
         return new DirectoryStream<Path>() {
            {
               Objects.requireNonNull(LinkFSProvider.this);
            }

            public Iterator<Path> iterator() {
               return directoryContents.children().values().stream().filter((path) -> {
                  try {
                     return filter.accept(path);
                  } catch (IOException e) {
                     throw new DirectoryIteratorException(e);
                  }
               }).map((path) -> path).iterator();
            }

            public void close() {
            }
         };
      }
   }

   public void createDirectory(final Path dir, final FileAttribute<?>... attrs) {
      throw new ReadOnlyFileSystemException();
   }

   public void delete(final Path path) {
      throw new ReadOnlyFileSystemException();
   }

   public void copy(final Path source, final Path target, final CopyOption... options) {
      throw new ReadOnlyFileSystemException();
   }

   public void move(final Path source, final Path target, final CopyOption... options) {
      throw new ReadOnlyFileSystemException();
   }

   public boolean isSameFile(final Path path, final Path path2) {
      return path instanceof LinkFSPath && path2 instanceof LinkFSPath && path.equals(path2);
   }

   public boolean isHidden(final Path path) {
      return false;
   }

   public FileStore getFileStore(final Path path) {
      return toLinkPath(path).getFileSystem().store();
   }

   public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
      if (modes.length == 0 && !toLinkPath(path).exists()) {
         throw new NoSuchFileException(path.toString());
      } else {
         AccessMode[] var3 = modes;
         int var4 = modes.length;
         int var5 = 0;

         while(var5 < var4) {
            AccessMode mode = var3[var5];
            switch (mode) {
               case READ:
                  if (!toLinkPath(path).exists()) {
                     throw new NoSuchFileException(path.toString());
                  }
               default:
                  ++var5;
                  break;
               case EXECUTE:
               case WRITE:
                  throw new AccessDeniedException(mode.toString());
            }
         }

      }
   }

   public <V extends FileAttributeView> @Nullable V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
      LinkFSPath linkPath = toLinkPath(path);
      return (V)(type == BasicFileAttributeView.class ? linkPath.getBasicAttributeView() : null);
   }

   public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) throws IOException {
      LinkFSPath linkPath = toLinkPath(path).toAbsolutePath();
      if (type == BasicFileAttributes.class) {
         return (A)linkPath.getBasicAttributes();
      } else {
         throw new UnsupportedOperationException("Attributes of type " + type.getName() + " not supported");
      }
   }

   public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options) {
      throw new UnsupportedOperationException();
   }

   public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) {
      throw new ReadOnlyFileSystemException();
   }

   private static LinkFSPath toLinkPath(final @Nullable Path path) {
      if (path == null) {
         throw new NullPointerException();
      } else if (path instanceof LinkFSPath) {
         LinkFSPath p = (LinkFSPath)path;
         return p;
      } else {
         throw new ProviderMismatchException();
      }
   }
}
