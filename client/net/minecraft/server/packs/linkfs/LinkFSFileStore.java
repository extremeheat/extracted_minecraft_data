package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import javax.annotation.Nullable;

class LinkFSFileStore extends FileStore {
   private final String name;

   public LinkFSFileStore(String var1) {
      super();
      this.name = var1;
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public String type() {
      return "index";
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   public long getTotalSpace() {
      return 0L;
   }

   @Override
   public long getUsableSpace() {
      return 0L;
   }

   @Override
   public long getUnallocatedSpace() {
      return 0L;
   }

   @Override
   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> var1) {
      return var1 == BasicFileAttributeView.class;
   }

   @Override
   public boolean supportsFileAttributeView(String var1) {
      return "basic".equals(var1);
   }

   @Nullable
   @Override
   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> var1) {
      return null;
   }

   @Override
   public Object getAttribute(String var1) throws IOException {
      throw new UnsupportedOperationException();
   }
}
