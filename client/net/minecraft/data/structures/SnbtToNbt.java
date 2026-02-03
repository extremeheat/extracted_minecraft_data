package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtToNbt implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput output;
   private final Iterable<Path> inputFolders;
   private final List<Filter> filters;

   public SnbtToNbt(final PackOutput output, final Path inputFolder) {
      this(output, (Iterable)List.of(inputFolder));
   }

   public SnbtToNbt(final PackOutput output, final Iterable<Path> inputFolders) {
      super();
      this.filters = Lists.newArrayList();
      this.output = output;
      this.inputFolders = inputFolders;
   }

   public SnbtToNbt addFilter(final Filter filter) {
      this.filters.add(filter);
      return this;
   }

   private CompoundTag applyFilters(final String name, final CompoundTag input) {
      CompoundTag result = input;

      for(Filter filter : this.filters) {
         result = filter.apply(name, result);
      }

      return result;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      Path output = this.output.getOutputFolder();
      List<CompletableFuture<?>> tasks = Lists.newArrayList();

      for(Path input : this.inputFolders) {
         tasks.add(CompletableFuture.supplyAsync(() -> {
            try {
               Stream<Path> files = Files.walk(input);

               CompletableFuture var5;
               try {
                  var5 = CompletableFuture.allOf((CompletableFuture[])files.filter((path) -> path.toString().endsWith(".snbt")).map((path) -> CompletableFuture.runAsync(() -> {
                        TaskResult structure = this.readStructure(path, this.getName(input, path));
                        this.storeStructureIfChanged(cache, structure, output);
                     }, Util.backgroundExecutor().forName("SnbtToNbt"))).toArray((x$0) -> new CompletableFuture[x$0]));
               } catch (Throwable var8) {
                  if (files != null) {
                     try {
                        files.close();
                     } catch (Throwable x2) {
                        var8.addSuppressed(x2);
                     }
                  }

                  throw var8;
               }

               if (files != null) {
                  files.close();
               }

               return var5;
            } catch (Exception e) {
               throw new RuntimeException("Failed to read structure input directory, aborting", e);
            }
         }, Util.backgroundExecutor().forName("SnbtToNbt")).thenCompose((v) -> v));
      }

      return Util.sequenceFailFast(tasks);
   }

   public final String getName() {
      return "SNBT -> NBT";
   }

   private String getName(final Path root, final Path path) {
      String name = root.relativize(path).toString().replaceAll("\\\\", "/");
      return name.substring(0, name.length() - ".snbt".length());
   }

   private TaskResult readStructure(final Path path, final String name) {
      try {
         BufferedReader reader = Files.newBufferedReader(path);

         TaskResult var10;
         try {
            String input = IOUtils.toString(reader);
            CompoundTag updated = this.applyFilters(name, NbtUtils.snbtToStructure(input));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HashingOutputStream hos = new HashingOutputStream(Hashing.sha1(), bos);
            NbtIo.writeCompressed(updated, (OutputStream)hos);
            byte[] bytes = bos.toByteArray();
            HashCode hash = hos.hash();
            var10 = new TaskResult(name, bytes, hash);
         } catch (Throwable var12) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (reader != null) {
            reader.close();
         }

         return var10;
      } catch (Throwable t) {
         throw new StructureConversionException(path, t);
      }
   }

   private void storeStructureIfChanged(final CachedOutput cache, final TaskResult task, final Path output) {
      Path destination = output.resolve(task.name + ".nbt");

      try {
         cache.writeIfNeeded(destination, task.payload, task.hash);
      } catch (IOException e) {
         LOGGER.error("Couldn't write structure {} at {}", new Object[]{task.name, destination, e});
      }

   }

   private static record TaskResult(String name, byte[] payload, HashCode hash) {
      private TaskResult {
         super();
      }
   }

   private static class StructureConversionException extends RuntimeException {
      public StructureConversionException(final Path path, final Throwable t) {
         super(path.toAbsolutePath().toString(), t);
      }
   }

   @FunctionalInterface
   public interface Filter {
      CompoundTag apply(final String name, final CompoundTag input);
   }
}
