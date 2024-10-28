package net.minecraft.server.packs.linkfs;

import java.nio.file.Path;
import java.util.Map;

interface PathContents {
   PathContents MISSING = new PathContents() {
      public String toString() {
         return "empty";
      }
   };
   PathContents RELATIVE = new PathContents() {
      public String toString() {
         return "relative";
      }
   };

   public static record DirectoryContents(Map<String, LinkFSPath> children) implements PathContents {
      public DirectoryContents(Map<String, LinkFSPath> children) {
         super();
         this.children = children;
      }

      public Map<String, LinkFSPath> children() {
         return this.children;
      }
   }

   public static record FileContents(Path contents) implements PathContents {
      public FileContents(Path contents) {
         super();
         this.contents = contents;
      }

      public Path contents() {
         return this.contents;
      }
   }
}
