package net.minecraft.util.filefix.virtualfilesystem;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import org.jspecify.annotations.Nullable;

public class CopyOnWriteFileStore extends FileStore {
   private final String name;
   private final CopyOnWriteFileSystem fs;

   public CopyOnWriteFileStore(final String name, final CopyOnWriteFileSystem fs) {
      super();
      this.name = name;
      this.fs = fs;
   }

   public String name() {
      return this.name;
   }

   public String type() {
      return "copy-on-write";
   }

   public boolean isReadOnly() {
      return false;
   }

   public long getTotalSpace() throws IOException {
      return Files.getFileStore(this.fs.tmpDirectory()).getTotalSpace();
   }

   public long getUsableSpace() throws IOException {
      return Files.getFileStore(this.fs.tmpDirectory()).getUsableSpace();
   }

   public long getUnallocatedSpace() throws IOException {
      return Files.getFileStore(this.fs.tmpDirectory()).getUnallocatedSpace();
   }

   public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
      return type == BasicFileAttributeView.class;
   }

   public boolean supportsFileAttributeView(final String name) {
      return "basic".equals(name);
   }

   public <V extends FileStoreAttributeView> @Nullable V getFileStoreAttributeView(final Class<V> type) {
      throw new UnsupportedOperationException();
   }

   public Object getAttribute(final String attribute) throws IOException {
      throw new UnsupportedOperationException();
   }
}
