package net.minecraft.server.packs.linkfs;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

class LinkFSPath implements Path {
   private static final BasicFileAttributes DIRECTORY_ATTRIBUTES = new DummyFileAttributes() {
      public boolean isRegularFile() {
         return false;
      }

      public boolean isDirectory() {
         return true;
      }
   };
   private static final BasicFileAttributes FILE_ATTRIBUTES = new DummyFileAttributes() {
      public boolean isRegularFile() {
         return true;
      }

      public boolean isDirectory() {
         return false;
      }
   };
   private static final Comparator<LinkFSPath> PATH_COMPARATOR = Comparator.comparing(LinkFSPath::pathToString);
   private final String name;
   private final LinkFileSystem fileSystem;
   @Nullable
   private final LinkFSPath parent;
   @Nullable
   private List<String> pathToRoot;
   @Nullable
   private String pathString;
   private final PathContents pathContents;

   public LinkFSPath(LinkFileSystem var1, String var2, @Nullable LinkFSPath var3, PathContents var4) {
      super();
      this.fileSystem = var1;
      this.name = var2;
      this.parent = var3;
      this.pathContents = var4;
   }

   private LinkFSPath createRelativePath(@Nullable LinkFSPath var1, String var2) {
      return new LinkFSPath(this.fileSystem, var2, var1, PathContents.RELATIVE);
   }

   public LinkFileSystem getFileSystem() {
      return this.fileSystem;
   }

   public boolean isAbsolute() {
      return this.pathContents != PathContents.RELATIVE;
   }

   public File toFile() {
      PathContents var2 = this.pathContents;
      if (var2 instanceof PathContents.FileContents var1) {
         return var1.contents().toFile();
      } else {
         throw new UnsupportedOperationException("Path " + this.pathToString() + " does not represent file");
      }
   }

   @Nullable
   public LinkFSPath getRoot() {
      return this.isAbsolute() ? this.fileSystem.rootPath() : null;
   }

   public LinkFSPath getFileName() {
      return this.createRelativePath((LinkFSPath)null, this.name);
   }

   @Nullable
   public LinkFSPath getParent() {
      return this.parent;
   }

   public int getNameCount() {
      return this.pathToRoot().size();
   }

   private List<String> pathToRoot() {
      if (this.name.isEmpty()) {
         return List.of();
      } else {
         if (this.pathToRoot == null) {
            ImmutableList.Builder var1 = ImmutableList.builder();
            if (this.parent != null) {
               var1.addAll(this.parent.pathToRoot());
            }

            var1.add(this.name);
            this.pathToRoot = var1.build();
         }

         return this.pathToRoot;
      }
   }

   public LinkFSPath getName(int var1) {
      List var2 = this.pathToRoot();
      if (var1 >= 0 && var1 < var2.size()) {
         return this.createRelativePath((LinkFSPath)null, (String)var2.get(var1));
      } else {
         throw new IllegalArgumentException("Invalid index: " + var1);
      }
   }

   public LinkFSPath subpath(int var1, int var2) {
      List var3 = this.pathToRoot();
      if (var1 >= 0 && var2 <= var3.size() && var1 < var2) {
         LinkFSPath var4 = null;

         for(int var5 = var1; var5 < var2; ++var5) {
            var4 = this.createRelativePath(var4, (String)var3.get(var5));
         }

         return var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean startsWith(Path var1) {
      if (var1.isAbsolute() != this.isAbsolute()) {
         return false;
      } else if (var1 instanceof LinkFSPath) {
         LinkFSPath var2 = (LinkFSPath)var1;
         if (var2.fileSystem != this.fileSystem) {
            return false;
         } else {
            List var3 = this.pathToRoot();
            List var4 = var2.pathToRoot();
            int var5 = var4.size();
            if (var5 > var3.size()) {
               return false;
            } else {
               for(int var6 = 0; var6 < var5; ++var6) {
                  if (!((String)var4.get(var6)).equals(var3.get(var6))) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean endsWith(Path var1) {
      if (var1.isAbsolute() && !this.isAbsolute()) {
         return false;
      } else if (var1 instanceof LinkFSPath) {
         LinkFSPath var2 = (LinkFSPath)var1;
         if (var2.fileSystem != this.fileSystem) {
            return false;
         } else {
            List var3 = this.pathToRoot();
            List var4 = var2.pathToRoot();
            int var5 = var4.size();
            int var6 = var3.size() - var5;
            if (var6 < 0) {
               return false;
            } else {
               for(int var7 = var5 - 1; var7 >= 0; --var7) {
                  if (!((String)var4.get(var7)).equals(var3.get(var6 + var7))) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public LinkFSPath normalize() {
      return this;
   }

   public LinkFSPath resolve(Path var1) {
      LinkFSPath var2 = this.toLinkPath(var1);
      return var1.isAbsolute() ? var2 : this.resolve(var2.pathToRoot());
   }

   private LinkFSPath resolve(List<String> var1) {
      LinkFSPath var2 = this;

      for(String var4 : var1) {
         var2 = var2.resolveName(var4);
      }

      return var2;
   }

   LinkFSPath resolveName(String var1) {
      if (isRelativeOrMissing(this.pathContents)) {
         return new LinkFSPath(this.fileSystem, var1, this, this.pathContents);
      } else {
         PathContents var3 = this.pathContents;
         if (var3 instanceof PathContents.DirectoryContents) {
            PathContents.DirectoryContents var2 = (PathContents.DirectoryContents)var3;
            LinkFSPath var4 = (LinkFSPath)var2.children().get(var1);
            return var4 != null ? var4 : new LinkFSPath(this.fileSystem, var1, this, PathContents.MISSING);
         } else if (this.pathContents instanceof PathContents.FileContents) {
            return new LinkFSPath(this.fileSystem, var1, this, PathContents.MISSING);
         } else {
            throw new AssertionError("All content types should be already handled");
         }
      }
   }

   private static boolean isRelativeOrMissing(PathContents var0) {
      return var0 == PathContents.MISSING || var0 == PathContents.RELATIVE;
   }

   public LinkFSPath relativize(Path var1) {
      LinkFSPath var2 = this.toLinkPath(var1);
      if (this.isAbsolute() != var2.isAbsolute()) {
         throw new IllegalArgumentException("absolute mismatch");
      } else {
         List var3 = this.pathToRoot();
         List var4 = var2.pathToRoot();
         if (var3.size() >= var4.size()) {
            throw new IllegalArgumentException();
         } else {
            for(int var5 = 0; var5 < var3.size(); ++var5) {
               if (!((String)var3.get(var5)).equals(var4.get(var5))) {
                  throw new IllegalArgumentException();
               }
            }

            return var2.subpath(var3.size(), var4.size());
         }
      }
   }

   public URI toUri() {
      try {
         return new URI("x-mc-link", this.fileSystem.store().name(), this.pathToString(), (String)null);
      } catch (URISyntaxException var2) {
         throw new AssertionError("Failed to create URI", var2);
      }
   }

   public LinkFSPath toAbsolutePath() {
      return this.isAbsolute() ? this : this.fileSystem.rootPath().resolve(this);
   }

   public LinkFSPath toRealPath(LinkOption... var1) {
      return this.toAbsolutePath();
   }

   public WatchKey register(WatchService var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) {
      throw new UnsupportedOperationException();
   }

   public int compareTo(Path var1) {
      LinkFSPath var2 = this.toLinkPath(var1);
      return PATH_COMPARATOR.compare(this, var2);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof LinkFSPath) {
         LinkFSPath var2 = (LinkFSPath)var1;
         if (this.fileSystem != var2.fileSystem) {
            return false;
         } else {
            boolean var3 = this.hasRealContents();
            if (var3 != var2.hasRealContents()) {
               return false;
            } else if (var3) {
               return this.pathContents == var2.pathContents;
            } else {
               return Objects.equals(this.parent, var2.parent) && Objects.equals(this.name, var2.name);
            }
         }
      } else {
         return false;
      }
   }

   private boolean hasRealContents() {
      return !isRelativeOrMissing(this.pathContents);
   }

   public int hashCode() {
      return this.hasRealContents() ? this.pathContents.hashCode() : this.name.hashCode();
   }

   public String toString() {
      return this.pathToString();
   }

   private String pathToString() {
      if (this.pathString == null) {
         StringBuilder var1 = new StringBuilder();
         if (this.isAbsolute()) {
            var1.append("/");
         }

         Joiner.on("/").appendTo(var1, this.pathToRoot());
         this.pathString = var1.toString();
      }

      return this.pathString;
   }

   private LinkFSPath toLinkPath(@Nullable Path var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var1 instanceof LinkFSPath) {
            LinkFSPath var2 = (LinkFSPath)var1;
            if (var2.fileSystem == this.fileSystem) {
               return var2;
            }
         }

         throw new ProviderMismatchException();
      }
   }

   public boolean exists() {
      return this.hasRealContents();
   }

   @Nullable
   public Path getTargetPath() {
      PathContents var2 = this.pathContents;
      Path var10000;
      if (var2 instanceof PathContents.FileContents var1) {
         var10000 = var1.contents();
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @Nullable
   public PathContents.DirectoryContents getDirectoryContents() {
      PathContents var2 = this.pathContents;
      PathContents.DirectoryContents var10000;
      if (var2 instanceof PathContents.DirectoryContents var1) {
         var10000 = var1;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public BasicFileAttributeView getBasicAttributeView() {
      return new BasicFileAttributeView() {
         public String name() {
            return "basic";
         }

         public BasicFileAttributes readAttributes() throws IOException {
            return LinkFSPath.this.getBasicAttributes();
         }

         public void setTimes(FileTime var1, FileTime var2, FileTime var3) {
            throw new ReadOnlyFileSystemException();
         }
      };
   }

   public BasicFileAttributes getBasicAttributes() throws IOException {
      if (this.pathContents instanceof PathContents.DirectoryContents) {
         return DIRECTORY_ATTRIBUTES;
      } else if (this.pathContents instanceof PathContents.FileContents) {
         return FILE_ATTRIBUTES;
      } else {
         throw new NoSuchFileException(this.pathToString());
      }
   }

   // $FF: synthetic method
   public Path toRealPath(final LinkOption[] var1) throws IOException {
      return this.toRealPath(var1);
   }

   // $FF: synthetic method
   public Path toAbsolutePath() {
      return this.toAbsolutePath();
   }

   // $FF: synthetic method
   public Path relativize(final Path var1) {
      return this.relativize(var1);
   }

   // $FF: synthetic method
   public Path resolve(final Path var1) {
      return this.resolve(var1);
   }

   // $FF: synthetic method
   public Path normalize() {
      return this.normalize();
   }

   // $FF: synthetic method
   public Path subpath(final int var1, final int var2) {
      return this.subpath(var1, var2);
   }

   // $FF: synthetic method
   public Path getName(final int var1) {
      return this.getName(var1);
   }

   // $FF: synthetic method
   @Nullable
   public Path getParent() {
      return this.getParent();
   }

   // $FF: synthetic method
   public Path getFileName() {
      return this.getFileName();
   }

   // $FF: synthetic method
   @Nullable
   public Path getRoot() {
      return this.getRoot();
   }

   // $FF: synthetic method
   public FileSystem getFileSystem() {
      return this.getFileSystem();
   }
}
