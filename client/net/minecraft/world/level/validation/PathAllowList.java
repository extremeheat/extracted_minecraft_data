package net.minecraft.world.level.validation;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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

   public PathAllowList(final List<ConfigEntry> entries) {
      super();
      this.entries = entries;
   }

   public PathMatcher getForFileSystem(final FileSystem fileSystem) {
      return (PathMatcher)this.compiledPaths.computeIfAbsent(fileSystem.provider().getScheme(), (scheme) -> {
         List<PathMatcher> compiledMatchers;
         try {
            compiledMatchers = this.entries.stream().map((ex) -> ex.compile(fileSystem)).toList();
         } catch (Exception e) {
            LOGGER.error("Failed to compile file pattern list", e);
            return (path) -> false;
         }

         PathMatcher var10000;
         switch (compiledMatchers.size()) {
            case 0 -> var10000 = (path) -> false;
            case 1 -> var10000 = (PathMatcher)compiledMatchers.get(0);
            default -> var10000 = (path) -> {
   for(PathMatcher matcher : compiledMatchers) {
      if (matcher.matches(path)) {
         return true;
      }
   }

   return false;
};
         }

         return var10000;
      });
   }

   public boolean matches(final Path path) {
      return this.getForFileSystem(path.getFileSystem()).matches(path);
   }

   public static PathAllowList readPlain(final BufferedReader reader) {
      return new PathAllowList(reader.lines().flatMap((line) -> PathAllowList.ConfigEntry.parse(line).stream()).toList());
   }

   @FunctionalInterface
   public interface EntryType {
      EntryType FILESYSTEM = FileSystem::getPathMatcher;
      EntryType PREFIX = (fileSystem, pattern) -> (path) -> path.toString().startsWith(pattern);

      PathMatcher compile(FileSystem fileSystem, String pattern);
   }

   public static record ConfigEntry(EntryType type, String pattern) {
      public ConfigEntry {
         super();
      }

      public PathMatcher compile(final FileSystem fileSystem) {
         return this.type().compile(fileSystem, this.pattern);
      }

      static Optional<ConfigEntry> parse(final String definition) {
         if (!definition.isBlank() && !definition.startsWith("#")) {
            if (!definition.startsWith("[")) {
               return Optional.of(new ConfigEntry(PathAllowList.EntryType.PREFIX, definition));
            } else {
               int split = definition.indexOf(93, 1);
               if (split == -1) {
                  throw new IllegalArgumentException("Unterminated type in line '" + definition + "'");
               } else {
                  String type = definition.substring(1, split);
                  String contents = definition.substring(split + 1);
                  Optional var10000;
                  switch (type) {
                     case "glob":
                     case "regex":
                        var10000 = Optional.of(new ConfigEntry(PathAllowList.EntryType.FILESYSTEM, type + ":" + contents));
                        break;
                     case "prefix":
                        var10000 = Optional.of(new ConfigEntry(PathAllowList.EntryType.PREFIX, contents));
                        break;
                     default:
                        throw new IllegalArgumentException("Unsupported definition type in line '" + definition + "'");
                  }

                  return var10000;
               }
            }
         } else {
            return Optional.empty();
         }
      }

      static ConfigEntry glob(final String pattern) {
         return new ConfigEntry(PathAllowList.EntryType.FILESYSTEM, "glob:" + pattern);
      }

      static ConfigEntry regex(final String pattern) {
         return new ConfigEntry(PathAllowList.EntryType.FILESYSTEM, "regex:" + pattern);
      }

      static ConfigEntry prefix(final String pattern) {
         return new ConfigEntry(PathAllowList.EntryType.PREFIX, pattern);
      }
   }
}
