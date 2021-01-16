package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeTraverser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
@AndroidIncompatible
@GwtIncompatible
public final class MoreFiles {
   private MoreFiles() {
      super();
   }

   public static ByteSource asByteSource(Path var0, OpenOption... var1) {
      return new MoreFiles.PathByteSource(var0, var1);
   }

   public static ByteSink asByteSink(Path var0, OpenOption... var1) {
      return new MoreFiles.PathByteSink(var0, var1);
   }

   public static CharSource asCharSource(Path var0, Charset var1, OpenOption... var2) {
      return asByteSource(var0, var2).asCharSource(var1);
   }

   public static CharSink asCharSink(Path var0, Charset var1, OpenOption... var2) {
      return asByteSink(var0, var2).asCharSink(var1);
   }

   public static ImmutableList<Path> listFiles(Path var0) throws IOException {
      try {
         DirectoryStream var1 = java.nio.file.Files.newDirectoryStream(var0);
         Throwable var2 = null;

         ImmutableList var3;
         try {
            var3 = ImmutableList.copyOf((Iterable)var1);
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var3;
      } catch (DirectoryIteratorException var15) {
         throw var15.getCause();
      }
   }

   public static TreeTraverser<Path> directoryTreeTraverser() {
      return MoreFiles.DirectoryTreeTraverser.INSTANCE;
   }

   public static Predicate<Path> isDirectory(LinkOption... var0) {
      final LinkOption[] var1 = (LinkOption[])var0.clone();
      return new Predicate<Path>() {
         public boolean apply(Path var1x) {
            return java.nio.file.Files.isDirectory(var1x, var1);
         }

         public String toString() {
            return "MoreFiles.isDirectory(" + Arrays.toString(var1) + ")";
         }
      };
   }

   public static Predicate<Path> isRegularFile(LinkOption... var0) {
      final LinkOption[] var1 = (LinkOption[])var0.clone();
      return new Predicate<Path>() {
         public boolean apply(Path var1x) {
            return java.nio.file.Files.isRegularFile(var1x, var1);
         }

         public String toString() {
            return "MoreFiles.isRegularFile(" + Arrays.toString(var1) + ")";
         }
      };
   }

   public static void touch(Path var0) throws IOException {
      Preconditions.checkNotNull(var0);

      try {
         java.nio.file.Files.setLastModifiedTime(var0, FileTime.fromMillis(System.currentTimeMillis()));
      } catch (NoSuchFileException var4) {
         try {
            java.nio.file.Files.createFile(var0);
         } catch (FileAlreadyExistsException var3) {
         }
      }

   }

   public static void createParentDirectories(Path var0, FileAttribute<?>... var1) throws IOException {
      Path var2 = var0.toAbsolutePath().normalize();
      Path var3 = var2.getParent();
      if (var3 != null) {
         if (!java.nio.file.Files.isDirectory(var3, new LinkOption[0])) {
            java.nio.file.Files.createDirectories(var3, var1);
            if (!java.nio.file.Files.isDirectory(var3, new LinkOption[0])) {
               throw new IOException("Unable to create parent directories of " + var0);
            }
         }

      }
   }

   public static String getFileExtension(Path var0) {
      Path var1 = var0.getFileName();
      if (var1 == null) {
         return "";
      } else {
         String var2 = var1.toString();
         int var3 = var2.lastIndexOf(46);
         return var3 == -1 ? "" : var2.substring(var3 + 1);
      }
   }

   public static String getNameWithoutExtension(Path var0) {
      Path var1 = var0.getFileName();
      if (var1 == null) {
         return "";
      } else {
         String var2 = var1.toString();
         int var3 = var2.lastIndexOf(46);
         return var3 == -1 ? var2 : var2.substring(0, var3);
      }
   }

   public static void deleteRecursively(Path var0, RecursiveDeleteOption... var1) throws IOException {
      Path var2 = getParentPath(var0);
      if (var2 == null) {
         throw new FileSystemException(var0.toString(), (String)null, "can't delete recursively");
      } else {
         Collection var3 = null;

         try {
            boolean var4 = false;
            DirectoryStream var5 = java.nio.file.Files.newDirectoryStream(var2);
            Throwable var6 = null;

            try {
               if (var5 instanceof SecureDirectoryStream) {
                  var4 = true;
                  var3 = deleteRecursivelySecure((SecureDirectoryStream)var5, var0.getFileName());
               }
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }

            if (!var4) {
               checkAllowsInsecure(var0, var1);
               var3 = deleteRecursivelyInsecure(var0);
            }
         } catch (IOException var18) {
            if (var3 == null) {
               throw var18;
            }

            var3.add(var18);
         }

         if (var3 != null) {
            throwDeleteFailed(var0, var3);
         }

      }
   }

   public static void deleteDirectoryContents(Path var0, RecursiveDeleteOption... var1) throws IOException {
      Collection var2 = null;

      try {
         DirectoryStream var3 = java.nio.file.Files.newDirectoryStream(var0);
         Throwable var4 = null;

         try {
            if (var3 instanceof SecureDirectoryStream) {
               SecureDirectoryStream var5 = (SecureDirectoryStream)var3;
               var2 = deleteDirectoryContentsSecure(var5);
            } else {
               checkAllowsInsecure(var0, var1);
               var2 = deleteDirectoryContentsInsecure(var3);
            }
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var16) {
         if (var2 == null) {
            throw var16;
         }

         var2.add(var16);
      }

      if (var2 != null) {
         throwDeleteFailed(var0, var2);
      }

   }

   @Nullable
   private static Collection<IOException> deleteRecursivelySecure(SecureDirectoryStream<Path> var0, Path var1) {
      Collection var2 = null;

      try {
         if (isDirectory(var0, var1, LinkOption.NOFOLLOW_LINKS)) {
            SecureDirectoryStream var3 = var0.newDirectoryStream(var1, LinkOption.NOFOLLOW_LINKS);
            Throwable var4 = null;

            try {
               var2 = deleteDirectoryContentsSecure(var3);
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

            if (var2 == null) {
               var0.deleteDirectory(var1);
            }
         } else {
            var0.deleteFile(var1);
         }

         return var2;
      } catch (IOException var16) {
         return addException(var2, var16);
      }
   }

   @Nullable
   private static Collection<IOException> deleteDirectoryContentsSecure(SecureDirectoryStream<Path> var0) {
      Collection var1 = null;

      try {
         Path var3;
         for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = concat(var1, deleteRecursivelySecure(var0, var3.getFileName()))) {
            var3 = (Path)var2.next();
         }

         return var1;
      } catch (DirectoryIteratorException var4) {
         return addException(var1, var4.getCause());
      }
   }

   @Nullable
   private static Collection<IOException> deleteRecursivelyInsecure(Path var0) {
      Collection var1 = null;

      try {
         if (java.nio.file.Files.isDirectory(var0, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            DirectoryStream var2 = java.nio.file.Files.newDirectoryStream(var0);
            Throwable var3 = null;

            try {
               var1 = deleteDirectoryContentsInsecure(var2);
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     var2.close();
                  }
               }

            }
         }

         if (var1 == null) {
            java.nio.file.Files.delete(var0);
         }

         return var1;
      } catch (IOException var15) {
         return addException(var1, var15);
      }
   }

   @Nullable
   private static Collection<IOException> deleteDirectoryContentsInsecure(DirectoryStream<Path> var0) {
      Collection var1 = null;

      try {
         Path var3;
         for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 = concat(var1, deleteRecursivelyInsecure(var3))) {
            var3 = (Path)var2.next();
         }

         return var1;
      } catch (DirectoryIteratorException var4) {
         return addException(var1, var4.getCause());
      }
   }

   @Nullable
   private static Path getParentPath(Path var0) throws IOException {
      Path var1 = var0.getParent();
      if (var1 != null) {
         return var1;
      } else {
         return var0.getNameCount() == 0 ? null : var0.getFileSystem().getPath(".");
      }
   }

   private static void checkAllowsInsecure(Path var0, RecursiveDeleteOption[] var1) throws InsecureRecursiveDeleteException {
      if (!Arrays.asList(var1).contains(RecursiveDeleteOption.ALLOW_INSECURE)) {
         throw new InsecureRecursiveDeleteException(var0.toString());
      }
   }

   private static boolean isDirectory(SecureDirectoryStream<Path> var0, Path var1, LinkOption... var2) throws IOException {
      return ((BasicFileAttributeView)var0.getFileAttributeView(var1, BasicFileAttributeView.class, var2)).readAttributes().isDirectory();
   }

   private static Collection<IOException> addException(@Nullable Collection<IOException> var0, IOException var1) {
      if (var0 == null) {
         var0 = new ArrayList();
      }

      ((Collection)var0).add(var1);
      return (Collection)var0;
   }

   @Nullable
   private static Collection<IOException> concat(@Nullable Collection<IOException> var0, @Nullable Collection<IOException> var1) {
      if (var0 == null) {
         return var1;
      } else {
         if (var1 != null) {
            var0.addAll(var1);
         }

         return var0;
      }
   }

   private static void throwDeleteFailed(Path var0, Collection<IOException> var1) throws FileSystemException {
      FileSystemException var2 = new FileSystemException(var0.toString(), (String)null, "failed to delete one or more files; see suppressed exceptions for details");
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         IOException var4 = (IOException)var3.next();
         var2.addSuppressed(var4);
      }

      throw var2;
   }

   private static final class DirectoryTreeTraverser extends TreeTraverser<Path> {
      private static final MoreFiles.DirectoryTreeTraverser INSTANCE = new MoreFiles.DirectoryTreeTraverser();

      private DirectoryTreeTraverser() {
         super();
      }

      public Iterable<Path> children(Path var1) {
         if (java.nio.file.Files.isDirectory(var1, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            try {
               return MoreFiles.listFiles(var1);
            } catch (IOException var3) {
               throw new DirectoryIteratorException(var3);
            }
         } else {
            return ImmutableList.of();
         }
      }
   }

   private static final class PathByteSink extends ByteSink {
      private final Path path;
      private final OpenOption[] options;

      private PathByteSink(Path var1, OpenOption... var2) {
         super();
         this.path = (Path)Preconditions.checkNotNull(var1);
         this.options = (OpenOption[])var2.clone();
      }

      public OutputStream openStream() throws IOException {
         return java.nio.file.Files.newOutputStream(this.path, this.options);
      }

      public String toString() {
         return "MoreFiles.asByteSink(" + this.path + ", " + Arrays.toString(this.options) + ")";
      }

      // $FF: synthetic method
      PathByteSink(Path var1, OpenOption[] var2, Object var3) {
         this(var1, var2);
      }
   }

   private static final class PathByteSource extends ByteSource {
      private static final LinkOption[] FOLLOW_LINKS = new LinkOption[0];
      private final Path path;
      private final OpenOption[] options;
      private final boolean followLinks;

      private PathByteSource(Path var1, OpenOption... var2) {
         super();
         this.path = (Path)Preconditions.checkNotNull(var1);
         this.options = (OpenOption[])var2.clone();
         this.followLinks = followLinks(this.options);
      }

      private static boolean followLinks(OpenOption[] var0) {
         OpenOption[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            OpenOption var4 = var1[var3];
            if (var4 == LinkOption.NOFOLLOW_LINKS) {
               return false;
            }
         }

         return true;
      }

      public InputStream openStream() throws IOException {
         return java.nio.file.Files.newInputStream(this.path, this.options);
      }

      private BasicFileAttributes readAttributes() throws IOException {
         return java.nio.file.Files.readAttributes(this.path, BasicFileAttributes.class, this.followLinks ? FOLLOW_LINKS : new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
      }

      public Optional<Long> sizeIfKnown() {
         BasicFileAttributes var1;
         try {
            var1 = this.readAttributes();
         } catch (IOException var3) {
            return Optional.absent();
         }

         return !var1.isDirectory() && !var1.isSymbolicLink() ? Optional.of(var1.size()) : Optional.absent();
      }

      public long size() throws IOException {
         BasicFileAttributes var1 = this.readAttributes();
         if (var1.isDirectory()) {
            throw new IOException("can't read: is a directory");
         } else if (var1.isSymbolicLink()) {
            throw new IOException("can't read: is a symbolic link");
         } else {
            return var1.size();
         }
      }

      public byte[] read() throws IOException {
         SeekableByteChannel var1 = java.nio.file.Files.newByteChannel(this.path, this.options);
         Throwable var2 = null;

         byte[] var3;
         try {
            var3 = Files.readFile(Channels.newInputStream(var1), var1.size());
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var11) {
                     var2.addSuppressed(var11);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var3;
      }

      public String toString() {
         return "MoreFiles.asByteSource(" + this.path + ", " + Arrays.toString(this.options) + ")";
      }

      // $FF: synthetic method
      PathByteSource(Path var1, OpenOption[] var2, Object var3) {
         this(var1, var2);
      }
   }
}
