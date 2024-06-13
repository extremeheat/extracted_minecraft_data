package net.minecraft.server.packs.linkfs;

import com.google.common.base.Splitter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class LinkFileSystem extends FileSystem {
   private static final Set<String> VIEWS = Set.of("basic");
   public static final String PATH_SEPARATOR = "/";
   private static final Splitter PATH_SPLITTER = Splitter.on('/');
   private final FileStore store;
   private final FileSystemProvider provider = new LinkFSProvider();
   private final LinkFSPath root;

   LinkFileSystem(String var1, LinkFileSystem.DirectoryEntry var2) {
      super();
      this.store = new LinkFSFileStore(var1);
      this.root = buildPath(var2, this, "", null);
   }

   private static LinkFSPath buildPath(LinkFileSystem.DirectoryEntry var0, LinkFileSystem var1, String var2, @Nullable LinkFSPath var3) {
      Object2ObjectOpenHashMap var4 = new Object2ObjectOpenHashMap();
      LinkFSPath var5 = new LinkFSPath(var1, var2, var3, new PathContents.DirectoryContents(var4));
      var0.files.forEach((var3x, var4x) -> var4.put(var3x, new LinkFSPath(var1, var3x, var5, new PathContents.FileContents(var4x))));
      var0.children.forEach((var3x, var4x) -> var4.put(var3x, buildPath(var4x, var1, var3x, var5)));
      var4.trim();
      return var5;
   }

   @Override
   public FileSystemProvider provider() {
      return this.provider;
   }

   @Override
   public void close() {
   }

   @Override
   public boolean isOpen() {
      return true;
   }

   @Override
   public boolean isReadOnly() {
      return true;
   }

   @Override
   public String getSeparator() {
      return "/";
   }

   @Override
   public Iterable<Path> getRootDirectories() {
      return List.of(this.root);
   }

   @Override
   public Iterable<FileStore> getFileStores() {
      return List.of(this.store);
   }

   @Override
   public Set<String> supportedFileAttributeViews() {
      return VIEWS;
   }

   @Override
   public Path getPath(String var1, String... var2) {
      Stream var3 = Stream.of(var1);
      if (var2.length > 0) {
         var3 = Stream.concat(var3, Stream.of(var2));
      }

      String var4 = var3.collect(Collectors.joining("/"));
      if (var4.equals("/")) {
         return this.root;
      } else if (var4.startsWith("/")) {
         LinkFSPath var8 = this.root;

         for (String var10 : PATH_SPLITTER.split(var4.substring(1))) {
            if (var10.isEmpty()) {
               throw new IllegalArgumentException("Empty paths not allowed");
            }

            var8 = var8.resolveName(var10);
         }

         return var8;
      } else {
         LinkFSPath var5 = null;

         for (String var7 : PATH_SPLITTER.split(var4)) {
            if (var7.isEmpty()) {
               throw new IllegalArgumentException("Empty paths not allowed");
            }

            var5 = new LinkFSPath(this, var7, var5, PathContents.RELATIVE);
         }

         if (var5 == null) {
            throw new IllegalArgumentException("Empty paths not allowed");
         } else {
            return var5;
         }
      }
   }

   @Override
   public PathMatcher getPathMatcher(String var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public UserPrincipalLookupService getUserPrincipalLookupService() {
      throw new UnsupportedOperationException();
   }

   @Override
   public WatchService newWatchService() {
      throw new UnsupportedOperationException();
   }

   public FileStore store() {
      return this.store;
   }

   public LinkFSPath rootPath() {
      return this.root;
   }

   public static LinkFileSystem.Builder builder() {
      return new LinkFileSystem.Builder();
   }

   public static class Builder {
      private final LinkFileSystem.DirectoryEntry root = new LinkFileSystem.DirectoryEntry();

      public Builder() {
         super();
      }

      public LinkFileSystem.Builder put(List<String> var1, String var2, Path var3) {
         LinkFileSystem.DirectoryEntry var4 = this.root;

         for (String var6 : var1) {
            var4 = var4.children.computeIfAbsent(var6, var0 -> new LinkFileSystem.DirectoryEntry());
         }

         var4.files.put(var2, var3);
         return this;
      }

      public LinkFileSystem.Builder put(List<String> var1, Path var2) {
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("Path can't be empty");
         } else {
            int var3 = var1.size() - 1;
            return this.put(var1.subList(0, var3), (String)var1.get(var3), var2);
         }
      }

      public FileSystem build(String var1) {
         return new LinkFileSystem(var1, this.root);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
