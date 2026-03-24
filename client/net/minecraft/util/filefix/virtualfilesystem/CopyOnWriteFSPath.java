package net.minecraft.util.filefix.virtualfilesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSIllegalArgumentException;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSNoSuchFileException;
import org.jspecify.annotations.Nullable;

public class CopyOnWriteFSPath implements Path {
   private final Path path;
   private final CopyOnWriteFileSystem fs;
   private final boolean isAbsolute;

   private CopyOnWriteFSPath(final Path path, final CopyOnWriteFileSystem fs, final boolean isAbsolute) {
      super();
      this.path = path;
      this.fs = fs;
      this.isAbsolute = isAbsolute;
   }

   protected static CopyOnWriteFSPath of(final CopyOnWriteFileSystem fs, String first, final String... more) {
      boolean isAbsolute;
      for(isAbsolute = false; first.startsWith("/"); first = first.substring(1)) {
         isAbsolute = true;
      }

      return new CopyOnWriteFSPath(fs.backingFileSystem().getPath(first, more), fs, isAbsolute);
   }

   public CopyOnWriteFileSystem getFileSystem() {
      return this.fs;
   }

   public boolean isAbsolute() {
      return this.isAbsolute;
   }

   public @Nullable Path getRoot() {
      return this.isAbsolute() ? this.fs.rootPath() : null;
   }

   public @Nullable CopyOnWriteFSPath getFileName() {
      return this.isRoot() ? null : new CopyOnWriteFSPath(this.path.getFileName(), this.fs, false);
   }

   public @Nullable CopyOnWriteFSPath getParent() {
      if (this.isRoot()) {
         return null;
      } else {
         Path parent = this.path.getParent();
         if (parent == null) {
            return this.isAbsolute() ? this.fs.rootPath() : null;
         } else {
            return new CopyOnWriteFSPath(parent, this.fs, this.isAbsolute());
         }
      }
   }

   public int getNameCount() {
      return this.path.toString().isEmpty() && this.isAbsolute ? 0 : this.path.getNameCount();
   }

   public Path getName(final int index) {
      if (this.isRoot()) {
         throw new IllegalArgumentException();
      } else {
         return new CopyOnWriteFSPath(this.path.getName(index), this.fs, false);
      }
   }

   public Path subpath(final int beginIndex, final int endIndex) {
      return new CopyOnWriteFSPath(this.path.subpath(beginIndex, endIndex), this.fs, false);
   }

   public boolean startsWith(final Path other) {
      CopyOnWriteFSPath otherCow = asCow(other);
      return this.isAbsolute() != otherCow.isAbsolute() ? false : this.path.startsWith(otherCow.path);
   }

   public boolean endsWith(final Path other) {
      CopyOnWriteFSPath otherCow = asCow(other);
      if (otherCow.isAbsolute()) {
         return this.isAbsolute() ? this.equals(otherCow) : false;
      } else {
         return this.path.endsWith(otherCow.path);
      }
   }

   public CopyOnWriteFSPath normalize() {
      Path normalize = this.path.normalize();
      return !this.isAbsolute() || !normalize.startsWith(".") && !normalize.startsWith("..") ? new CopyOnWriteFSPath(normalize, this.fs, this.isAbsolute()) : this.fs.rootPath();
   }

   public CopyOnWriteFSPath resolve(final Path other) {
      CopyOnWriteFSPath otherCow = asCow(other);
      if (other.isAbsolute()) {
         return otherCow;
      } else {
         Path result = this.path.resolve(otherCow.path);
         return new CopyOnWriteFSPath(result, this.fs, this.isAbsolute());
      }
   }

   public CopyOnWriteFSPath resolve(final String other) {
      return this.resolve((Path)this.getFileSystem().getPath(other));
   }

   public CopyOnWriteFSPath resolve(final String first, final String... more) {
      CopyOnWriteFSPath result = this.resolve(first);

      for(String s : more) {
         result = result.resolve(s);
      }

      return result;
   }

   public CopyOnWriteFSPath relativize(final Path other) {
      CopyOnWriteFSPath otherCow = asCow(other);
      if (this.isAbsolute() != otherCow.isAbsolute()) {
         throw new IllegalArgumentException("'other' is different type of Path");
      } else {
         Path result = this.path.relativize(otherCow.path);
         return new CopyOnWriteFSPath(result, this.fs, false);
      }
   }

   public URI toUri() {
      throw new UnsupportedOperationException();
   }

   public CopyOnWriteFSPath toAbsolutePath() {
      return this.isAbsolute() ? this : new CopyOnWriteFSPath(this.path, this.fs, true);
   }

   public CopyOnWriteFSPath toRealPath(final LinkOption... options) throws CowFSNoSuchFileException {
      return this.fs.provider().getRealPath(this);
   }

   public WatchKey register(final WatchService watcher, final WatchEvent.Kind<?>[] events, final WatchEvent.Modifier... modifiers) throws IOException {
      throw new UnsupportedOperationException();
   }

   public int compareTo(final Path other) {
      CopyOnWriteFSPath otherCow = asCow(other);
      return this.toString().compareTo(otherCow.toString());
   }

   public String toString() {
      return this.isAbsolute() ? "/" + String.valueOf(this.path) : this.path.toString();
   }

   public boolean equals(final Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CopyOnWriteFSPath paths = (CopyOnWriteFSPath)o;
         return this.isAbsolute == paths.isAbsolute && Objects.equals(this.path, paths.path) && Objects.equals(this.fs, paths.fs);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.path, this.fs, this.isAbsolute});
   }

   protected boolean isRoot() {
      return this.getNameCount() == 0;
   }

   protected static CopyOnWriteFSPath asCow(final Path other) {
      if (other instanceof CopyOnWriteFSPath copyOnWriteFSPath) {
         return copyOnWriteFSPath;
      } else {
         throw new CowFSIllegalArgumentException("Other path is of mismatching file system");
      }
   }
}
