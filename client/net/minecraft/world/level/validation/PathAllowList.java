package net.minecraft.world.level.validation;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
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
         return switch (var3.size()) {
            case 0 -> var0 -> false;
            case 1 -> (PathMatcher)var3.get(0);
            default -> var1xx -> {
            for (PathMatcher var3x : var3) {
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   @FunctionalInterface
   public interface EntryType {
      PathAllowList.EntryType FILESYSTEM = FileSystem::getPathMatcher;
      PathAllowList.EntryType PREFIX = (var0, var1) -> var1x -> var1x.toString().startsWith(var1);

      PathMatcher compile(FileSystem var1, String var2);
   }
}
