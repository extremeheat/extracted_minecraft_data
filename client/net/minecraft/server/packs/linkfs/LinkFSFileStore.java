package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import org.jspecify.annotations.Nullable;

class LinkFSFileStore extends FileStore {
   private final String name;

   public LinkFSFileStore(final String name) {
      super();
      this.name = name;
   }

   public String name() {
      return this.name;
   }

   public String type() {
      return "index";
   }

   public boolean isReadOnly() {
      return true;
   }

   public long getTotalSpace() {
      return 0L;
   }

   public long getUsableSpace() {
      return 0L;
   }

   public long getUnallocatedSpace() {
      return 0L;
   }

   public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
      return type == BasicFileAttributeView.class;
   }

   public boolean supportsFileAttributeView(final String name) {
      return "basic".equals(name);
   }

   public <V extends FileStoreAttributeView> @Nullable V getFileStoreAttributeView(final Class<V> type) {
      return null;
   }

   public Object getAttribute(final String attribute) throws IOException {
      throw new UnsupportedOperationException();
   }
}
