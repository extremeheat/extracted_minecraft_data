package net.minecraft.server.packs.linkfs;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.jspecify.annotations.Nullable;

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
   private final @Nullable LinkFSPath parent;
   private @Nullable List<String> pathToRoot;
   private @Nullable String pathString;
   private final PathContents pathContents;

   public LinkFSPath(final LinkFileSystem fileSystem, final String name, final @Nullable LinkFSPath parent, final PathContents pathContents) {
      super();
      this.fileSystem = fileSystem;
      this.name = name;
      this.parent = parent;
      this.pathContents = pathContents;
   }

   private LinkFSPath createRelativePath(final @Nullable LinkFSPath parent, final String name) {
      return new LinkFSPath(this.fileSystem, name, parent, PathContents.RELATIVE);
   }

   public LinkFileSystem getFileSystem() {
      return this.fileSystem;
   }

   public boolean isAbsolute() {
      return this.pathContents != PathContents.RELATIVE;
   }

   public File toFile() {
      PathContents var2 = this.pathContents;
      if (var2 instanceof PathContents.FileContents file) {
         return file.contents().toFile();
      } else {
         throw new UnsupportedOperationException("Path " + this.pathToString() + " does not represent file");
      }
   }

   public @Nullable LinkFSPath getRoot() {
      return this.isAbsolute() ? this.fileSystem.rootPath() : null;
   }

   public LinkFSPath getFileName() {
      return this.createRelativePath((LinkFSPath)null, this.name);
   }

   public @Nullable LinkFSPath getParent() {
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
            ImmutableList.Builder<String> result = ImmutableList.builder();
            if (this.parent != null) {
               result.addAll(this.parent.pathToRoot());
            }

            result.add(this.name);
            this.pathToRoot = result.build();
         }

         return this.pathToRoot;
      }
   }

   public LinkFSPath getName(final int index) {
      List<String> names = this.pathToRoot();
      if (index >= 0 && index < names.size()) {
         return this.createRelativePath((LinkFSPath)null, (String)names.get(index));
      } else {
         throw new IllegalArgumentException("Invalid index: " + index);
      }
   }

   public LinkFSPath subpath(final int beginIndex, final int endIndex) {
      List<String> names = this.pathToRoot();
      if (beginIndex >= 0 && endIndex <= names.size() && beginIndex < endIndex) {
         LinkFSPath current = null;

         for(int i = beginIndex; i < endIndex; ++i) {
            current = this.createRelativePath(current, (String)names.get(i));
         }

         return current;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean startsWith(final Path other) {
      if (other.isAbsolute() != this.isAbsolute()) {
         return false;
      } else if (other instanceof LinkFSPath) {
         LinkFSPath otherLink = (LinkFSPath)other;
         if (otherLink.fileSystem != this.fileSystem) {
            return false;
         } else {
            List<String> thisNames = this.pathToRoot();
            List<String> otherNames = otherLink.pathToRoot();
            int otherSize = otherNames.size();
            if (otherSize > thisNames.size()) {
               return false;
            } else {
               for(int i = 0; i < otherSize; ++i) {
                  if (!((String)otherNames.get(i)).equals(thisNames.get(i))) {
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

   public boolean endsWith(final Path other) {
      if (other.isAbsolute() && !this.isAbsolute()) {
         return false;
      } else if (other instanceof LinkFSPath) {
         LinkFSPath otherLink = (LinkFSPath)other;
         if (otherLink.fileSystem != this.fileSystem) {
            return false;
         } else {
            List<String> thisNames = this.pathToRoot();
            List<String> otherNames = otherLink.pathToRoot();
            int otherSize = otherNames.size();
            int delta = thisNames.size() - otherSize;
            if (delta < 0) {
               return false;
            } else {
               for(int i = otherSize - 1; i >= 0; --i) {
                  if (!((String)otherNames.get(i)).equals(thisNames.get(delta + i))) {
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

   public LinkFSPath resolve(final Path other) {
      LinkFSPath otherLink = this.toLinkPath(other);
      return other.isAbsolute() ? otherLink : this.resolve(otherLink.pathToRoot());
   }

   private LinkFSPath resolve(final List<String> names) {
      LinkFSPath current = this;

      for(String name : names) {
         current = current.resolveName(name);
      }

      return current;
   }

   LinkFSPath resolveName(final String name) {
      if (isRelativeOrMissing(this.pathContents)) {
         return new LinkFSPath(this.fileSystem, name, this, this.pathContents);
      } else {
         PathContents var3 = this.pathContents;
         if (var3 instanceof PathContents.DirectoryContents) {
            PathContents.DirectoryContents directory = (PathContents.DirectoryContents)var3;
            LinkFSPath child = (LinkFSPath)directory.children().get(name);
            return child != null ? child : new LinkFSPath(this.fileSystem, name, this, PathContents.MISSING);
         } else if (this.pathContents instanceof PathContents.FileContents) {
            return new LinkFSPath(this.fileSystem, name, this, PathContents.MISSING);
         } else {
            throw new AssertionError("All content types should be already handled");
         }
      }
   }

   private static boolean isRelativeOrMissing(final PathContents contents) {
      return contents == PathContents.MISSING || contents == PathContents.RELATIVE;
   }

   public LinkFSPath relativize(final Path other) {
      LinkFSPath otherLink = this.toLinkPath(other);
      if (this.isAbsolute() != otherLink.isAbsolute()) {
         throw new IllegalArgumentException("absolute mismatch");
      } else {
         List<String> thisNames = this.pathToRoot();
         List<String> otherNames = otherLink.pathToRoot();
         if (thisNames.size() >= otherNames.size()) {
            throw new IllegalArgumentException();
         } else {
            for(int i = 0; i < thisNames.size(); ++i) {
               if (!((String)thisNames.get(i)).equals(otherNames.get(i))) {
                  throw new IllegalArgumentException();
               }
            }

            return otherLink.subpath(thisNames.size(), otherNames.size());
         }
      }
   }

   public URI toUri() {
      try {
         return new URI("x-mc-link", this.fileSystem.store().name(), this.pathToString(), (String)null);
      } catch (URISyntaxException e) {
         throw new AssertionError("Failed to create URI", e);
      }
   }

   public LinkFSPath toAbsolutePath() {
      return this.isAbsolute() ? this : this.fileSystem.rootPath().resolve(this);
   }

   public LinkFSPath toRealPath(final LinkOption... options) {
      return this.toAbsolutePath();
   }

   public WatchKey register(final WatchService watcher, final WatchEvent.Kind<?>[] events, final WatchEvent.Modifier... modifiers) {
      throw new UnsupportedOperationException();
   }

   public int compareTo(final Path other) {
      LinkFSPath otherPath = this.toLinkPath(other);
      return PATH_COMPARATOR.compare(this, otherPath);
   }

   public boolean equals(final Object other) {
      if (other == this) {
         return true;
      } else if (other instanceof LinkFSPath) {
         LinkFSPath that = (LinkFSPath)other;
         if (this.fileSystem != that.fileSystem) {
            return false;
         } else {
            boolean hasRealContents = this.hasRealContents();
            if (hasRealContents != that.hasRealContents()) {
               return false;
            } else if (hasRealContents) {
               return this.pathContents == that.pathContents;
            } else {
               return Objects.equals(this.parent, that.parent) && Objects.equals(this.name, that.name);
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
         StringBuilder builder = new StringBuilder();
         if (this.isAbsolute()) {
            builder.append("/");
         }

         Joiner.on("/").appendTo(builder, this.pathToRoot());
         this.pathString = builder.toString();
      }

      return this.pathString;
   }

   private LinkFSPath toLinkPath(final @Nullable Path path) {
      if (path == null) {
         throw new NullPointerException();
      } else {
         if (path instanceof LinkFSPath) {
            LinkFSPath p = (LinkFSPath)path;
            if (p.fileSystem == this.fileSystem) {
               return p;
            }
         }

         throw new ProviderMismatchException();
      }
   }

   public boolean exists() {
      return this.hasRealContents();
   }

   public @Nullable Path getTargetPath() {
      PathContents var2 = this.pathContents;
      Path var10000;
      if (var2 instanceof PathContents.FileContents file) {
         var10000 = file.contents();
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public PathContents.@Nullable DirectoryContents getDirectoryContents() {
      PathContents var2 = this.pathContents;
      PathContents.DirectoryContents var10000;
      if (var2 instanceof PathContents.DirectoryContents dir) {
         var10000 = dir;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public BasicFileAttributeView getBasicAttributeView() {
      return new BasicFileAttributeView() {
         {
            Objects.requireNonNull(LinkFSPath.this);
         }

         public String name() {
            return "basic";
         }

         public BasicFileAttributes readAttributes() throws IOException {
            return LinkFSPath.this.getBasicAttributes();
         }

         public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) {
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
}
