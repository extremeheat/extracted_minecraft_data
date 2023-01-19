package net.minecraft.server.packs.linkfs;

import java.nio.file.Path;
import java.util.Map;

interface PathContents {
   PathContents MISSING = new PathContents() {
      @Override
      public String toString() {
         return "empty";
      }
   };
   PathContents RELATIVE = new PathContents() {
      @Override
      public String toString() {
         return "relative";
      }
   };

   public static record DirectoryContents(Map<String, LinkFSPath> c) implements PathContents {
      private final Map<String, LinkFSPath> children;

      public DirectoryContents(Map<String, LinkFSPath> var1) {
         super();
         this.children = var1;
      }
   }

   public static record FileContents(Path c) implements PathContents {
      private final Path contents;

      public FileContents(Path var1) {
         super();
         this.contents = var1;
      }
   }
}
