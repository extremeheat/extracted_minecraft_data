package net.minecraft.world.level.validation;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class PathAllowList implements PathMatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String COMMENT_PREFIX = "#";
   private final List<ConfigEntry> entries;
   private final Map<String, PathMatcher> compiledPaths = new ConcurrentHashMap();

   public PathAllowList(List<ConfigEntry> var1) {
      super();
      this.entries = var1;
   }

   public PathMatcher getForFileSystem(FileSystem var1) {
      return (PathMatcher)this.compiledPaths.computeIfAbsent(var1.provider().getScheme(), (var2) -> {
         List var3;
         try {
            var3 = this.entries.stream().map((var1x) -> {
               return var1x.compile(var1);
            }).toList();
         } catch (Exception var5) {
            LOGGER.error("Failed to compile file pattern list", var5);
            return (var0) -> {
               return false;
            };
         }

         PathMatcher var10000;
         switch (var3.size()) {
            case 0 -> var10000 = (var0) -> {
   return false;
};
            case 1 -> var10000 = (PathMatcher)var3.get(0);
            default -> var10000 = (var1x) -> {
   Iterator var2 = var3.iterator();

   PathMatcher var3x;
   do {
      if (!var2.hasNext()) {
         return false;
      }

      var3x = (PathMatcher)var2.next();
   } while(!var3x.matches(var1x));

   return true;
};
         }

         return var10000;
      });
   }

   public boolean matches(Path var1) {
      return this.getForFileSystem(var1.getFileSystem()).matches(var1);
   }

   public static PathAllowList readPlain(BufferedReader var0) {
      return new PathAllowList(var0.lines().flatMap((var0x) -> {
         return PathAllowList.ConfigEntry.parse(var0x).stream();
      }).toList());
   }

   public static record ConfigEntry(EntryType type, String pattern) {
      public ConfigEntry(EntryType type, String pattern) {
         super();
         this.type = type;
         this.pattern = pattern;
      }

      public PathMatcher compile(FileSystem var1) {
         return this.type().compile(var1, this.pattern);
      }

      static Optional<ConfigEntry> parse(String var0) {
         if (!var0.isBlank() && !var0.startsWith("#")) {
            if (!var0.startsWith("[")) {
               return Optional.of(new ConfigEntry(PathAllowList.EntryType.PREFIX, var0));
            } else {
               int var1 = var0.indexOf(93, 1);
               if (var1 == -1) {
                  throw new IllegalArgumentException("Unterminated type in line '" + var0 + "'");
               } else {
                  String var2 = var0.substring(1, var1);
                  String var3 = var0.substring(var1 + 1);
                  Optional var10000;
                  switch (var2) {
                     case "glob":
                     case "regex":
                        var10000 = Optional.of(new ConfigEntry(PathAllowList.EntryType.FILESYSTEM, var2 + ":" + var3));
                        break;
                     case "prefix":
                        var10000 = Optional.of(new ConfigEntry(PathAllowList.EntryType.PREFIX, var3));
                        break;
                     default:
                        throw new IllegalArgumentException("Unsupported definition type in line '" + var0 + "'");
                  }

                  return var10000;
               }
            }
         } else {
            return Optional.empty();
         }
      }

      static ConfigEntry glob(String var0) {
         return new ConfigEntry(PathAllowList.EntryType.FILESYSTEM, "glob:" + var0);
      }

      static ConfigEntry regex(String var0) {
         return new ConfigEntry(PathAllowList.EntryType.FILESYSTEM, "regex:" + var0);
      }

      static ConfigEntry prefix(String var0) {
         return new ConfigEntry(PathAllowList.EntryType.PREFIX, var0);
      }

      public EntryType type() {
         return this.type;
      }

      public String pattern() {
         return this.pattern;
      }
   }

   @FunctionalInterface
   public interface EntryType {
      EntryType FILESYSTEM = FileSystem::getPathMatcher;
      EntryType PREFIX = (var0, var1) -> {
         return (var1x) -> {
            return var1x.toString().startsWith(var1);
         };
      };

      PathMatcher compile(FileSystem var1, String var2);
   }
}
