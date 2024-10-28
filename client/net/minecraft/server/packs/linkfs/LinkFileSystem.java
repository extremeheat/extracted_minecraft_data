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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

   LinkFileSystem(String var1, DirectoryEntry var2) {
      super();
      this.store = new LinkFSFileStore(var1);
      this.root = buildPath(var2, this, "", (LinkFSPath)null);
   }

   private static LinkFSPath buildPath(DirectoryEntry var0, LinkFileSystem var1, String var2, @Nullable LinkFSPath var3) {
      Object2ObjectOpenHashMap var4 = new Object2ObjectOpenHashMap();
      LinkFSPath var5 = new LinkFSPath(var1, var2, var3, new PathContents.DirectoryContents(var4));
      var0.files.forEach((var3x, var4x) -> {
         var4.put(var3x, new LinkFSPath(var1, var3x, var5, new PathContents.FileContents(var4x)));
      });
      var0.children.forEach((var3x, var4x) -> {
         var4.put(var3x, buildPath(var4x, var1, var3x, var5));
      });
      var4.trim();
      return var5;
   }

   public FileSystemProvider provider() {
      return this.provider;
   }

   public void close() {
   }

   public boolean isOpen() {
      return true;
   }

   public boolean isReadOnly() {
      return true;
   }

   public String getSeparator() {
      return "/";
   }

   public Iterable<Path> getRootDirectories() {
      return List.of(this.root);
   }

   public Iterable<FileStore> getFileStores() {
      return List.of(this.store);
   }

   public Set<String> supportedFileAttributeViews() {
      return VIEWS;
   }

   public Path getPath(String var1, String... var2) {
      Stream var3 = Stream.of(var1);
      if (var2.length > 0) {
         var3 = Stream.concat(var3, Stream.of(var2));
      }

      String var4 = (String)var3.collect(Collectors.joining("/"));
      if (var4.equals("/")) {
         return this.root;
      } else {
         LinkFSPath var5;
         Iterator var6;
         String var7;
         if (var4.startsWith("/")) {
            var5 = this.root;

            for(var6 = PATH_SPLITTER.split(var4.substring(1)).iterator(); var6.hasNext(); var5 = var5.resolveName(var7)) {
               var7 = (String)var6.next();
               if (var7.isEmpty()) {
                  throw new IllegalArgumentException("Empty paths not allowed");
               }
            }

            return var5;
         } else {
            var5 = null;

            for(var6 = PATH_SPLITTER.split(var4).iterator(); var6.hasNext(); var5 = new LinkFSPath(this, var7, var5, PathContents.RELATIVE)) {
               var7 = (String)var6.next();
               if (var7.isEmpty()) {
                  throw new IllegalArgumentException("Empty paths not allowed");
               }
            }

            if (var5 == null) {
               throw new IllegalArgumentException("Empty paths not allowed");
            } else {
               return var5;
            }
         }
      }
   }

   public PathMatcher getPathMatcher(String var1) {
      throw new UnsupportedOperationException();
   }

   public UserPrincipalLookupService getUserPrincipalLookupService() {
      throw new UnsupportedOperationException();
   }

   public WatchService newWatchService() {
      throw new UnsupportedOperationException();
   }

   public FileStore store() {
      return this.store;
   }

   public LinkFSPath rootPath() {
      return this.root;
   }

   public static Builder builder() {
      return new Builder();
   }

   static record DirectoryEntry(Map<String, DirectoryEntry> children, Map<String, Path> files) {
      final Map<String, DirectoryEntry> children;
      final Map<String, Path> files;

      public DirectoryEntry() {
         this(new HashMap(), new HashMap());
      }

      private DirectoryEntry(Map<String, DirectoryEntry> var1, Map<String, Path> var2) {
         super();
         this.children = var1;
         this.files = var2;
      }

      public Map<String, DirectoryEntry> children() {
         return this.children;
      }

      public Map<String, Path> files() {
         return this.files;
      }
   }

   public static class Builder {
      private final DirectoryEntry root = new DirectoryEntry();

      public Builder() {
         super();
      }

      public Builder put(List<String> var1, String var2, Path var3) {
         DirectoryEntry var4 = this.root;

         String var6;
         for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 = (DirectoryEntry)var4.children.computeIfAbsent(var6, (var0) -> {
            return new DirectoryEntry();
         })) {
            var6 = (String)var5.next();
         }

         var4.files.put(var2, var3);
         return this;
      }

      public Builder put(List<String> var1, Path var2) {
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
}
