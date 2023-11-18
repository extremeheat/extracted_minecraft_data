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
   private final List<PathAllowList.ConfigEntry> entries;
   private final Map<String, PathMatcher> compiledPaths = new ConcurrentHashMap<>();

   public PathAllowList(List<PathAllowList.ConfigEntry> var1) {
      super();
      this.entries = var1;
   }

   public PathMatcher getForFileSystem(FileSystem var1) {
      return this.compiledPaths.computeIfAbsent(var1.provider().getScheme(), var2 -> {
         List var3;
         try {
            var3 = this.entries.stream().map(var1xx -> var1xx.compile(var1)).toList();
         } catch (Exception var5) {
            LOGGER.error("Failed to compile file pattern list", var5);
            return var0 -> false;
         }
         return switch(var3.size()) {
            case 0 -> var0 -> false;
            case 1 -> (PathMatcher)var3.get(0);
            default -> var1xx -> {
            for(PathMatcher var3x : var3) {
               if (var3x.matches(var1xx)) {
                  return true;
               }
            }

            return false;
         };
         };
      });
   }

   @Override
   public boolean matches(Path var1) {
      return this.getForFileSystem(var1.getFileSystem()).matches(var1);
   }

   public static PathAllowList readPlain(BufferedReader var0) {
      return new PathAllowList(var0.lines().flatMap(var0x -> PathAllowList.ConfigEntry.parse(var0x).stream()).toList());
   }

   public static record ConfigEntry(PathAllowList.EntryType a, String b) {
      private final PathAllowList.EntryType type;
      private final String pattern;

      public ConfigEntry(PathAllowList.EntryType var1, String var2) {
         super();
         this.type = var1;
         this.pattern = var2;
      }

      public PathMatcher compile(FileSystem var1) {
         return this.type().compile(var1, this.pattern);
      }

      static Optional<PathAllowList.ConfigEntry> parse(String var0) {
         if (var0.isBlank() || var0.startsWith("#")) {
            return Optional.empty();
         } else if (!var0.startsWith("[")) {
            return Optional.of(new PathAllowList.ConfigEntry(PathAllowList.EntryType.PREFIX, var0));
         } else {
            int var1 = var0.indexOf(93, 1);
            if (var1 == -1) {
               throw new IllegalArgumentException("Unterminated type in line '" + var0 + "'");
            } else {
               String var2 = var0.substring(1, var1);
               String var3 = var0.substring(var1 + 1);

               return switch(var2) {
                  case "glob", "regex" -> Optional.of(new PathAllowList.ConfigEntry(PathAllowList.EntryType.FILESYSTEM, var2 + ":" + var3));
                  case "prefix" -> Optional.of(new PathAllowList.ConfigEntry(PathAllowList.EntryType.PREFIX, var3));
                  default -> throw new IllegalArgumentException("Unsupported definition type in line '" + var0 + "'");
               };
            }
         }
      }

      static PathAllowList.ConfigEntry glob(String var0) {
         return new PathAllowList.ConfigEntry(PathAllowList.EntryType.FILESYSTEM, "glob:" + var0);
      }

      static PathAllowList.ConfigEntry regex(String var0) {
         return new PathAllowList.ConfigEntry(PathAllowList.EntryType.FILESYSTEM, "regex:" + var0);
      }

      static PathAllowList.ConfigEntry prefix(String var0) {
         return new PathAllowList.ConfigEntry(PathAllowList.EntryType.PREFIX, var0);
      }
   }

   @FunctionalInterface
   public interface EntryType {
      PathAllowList.EntryType FILESYSTEM = FileSystem::getPathMatcher;
      PathAllowList.EntryType PREFIX = (var0, var1) -> var1x -> var1x.toString().startsWith(var1);

      PathMatcher compile(FileSystem var1, String var2);
   }
}
