package net.minecraft.data.structures;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class NbtToSnbt implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Iterable<Path> inputFolders;
   private final PackOutput output;

   public NbtToSnbt(final PackOutput output, final Collection<Path> inputFolders) {
      super();
      this.inputFolders = inputFolders;
      this.output = output;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      Path output = this.output.getOutputFolder();
      List<CompletableFuture<?>> tasks = new ArrayList();

      for(Path input : this.inputFolders) {
         tasks.add(CompletableFuture.supplyAsync(() -> {
            try {
               Stream<Path> walk = Files.walk(input);

               CompletableFuture var4;
               try {
                  var4 = CompletableFuture.allOf((CompletableFuture[])walk.filter((path) -> path.toString().endsWith(".nbt")).map((path) -> CompletableFuture.runAsync(() -> convertStructure(cache, path, getName(input, path), output), Util.ioPool())).toArray((x$0) -> new CompletableFuture[x$0]));
               } catch (Throwable var7) {
                  if (walk != null) {
                     try {
                        walk.close();
                     } catch (Throwable x2) {
                        var7.addSuppressed(x2);
                     }
                  }

                  throw var7;
               }

               if (walk != null) {
                  walk.close();
               }

               return var4;
            } catch (IOException e) {
               LOGGER.error("Failed to read structure input directory", e);
               return CompletableFuture.completedFuture((Object)null);
            }
         }, Util.backgroundExecutor().forName("NbtToSnbt")).thenCompose((v) -> v));
      }

      return CompletableFuture.allOf((CompletableFuture[])tasks.toArray((x$0) -> new CompletableFuture[x$0]));
   }

   public final String getName() {
      return "NBT -> SNBT";
   }

   private static String getName(final Path root, final Path path) {
      String name = root.relativize(path).toString().replaceAll("\\\\", "/");
      return name.substring(0, name.length() - ".nbt".length());
   }

   public static @Nullable Path convertStructure(final CachedOutput cache, final Path path, final String name, final Path output) {
      try {
         InputStream rawInput = Files.newInputStream(path);

         Path var7;
         try {
            InputStream input = new FastBufferedInputStream(rawInput);

            try {
               Path resultPath = output.resolve(name + ".snbt");
               writeSnbt(cache, resultPath, NbtUtils.structureToSnbt(NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap())));
               LOGGER.info("Converted {} from NBT to SNBT", name);
               var7 = resultPath;
            } catch (Throwable var10) {
               try {
                  input.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            input.close();
         } catch (Throwable var11) {
            if (rawInput != null) {
               try {
                  rawInput.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (rawInput != null) {
            rawInput.close();
         }

         return var7;
      } catch (IOException e) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", new Object[]{name, path, e});
         return null;
      }
   }

   public static void writeSnbt(final CachedOutput cache, final Path destination, final String text) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      HashingOutputStream hashedBytes = new HashingOutputStream(Hashing.sha1(), bytes);
      hashedBytes.write(text.getBytes(StandardCharsets.UTF_8));
      hashedBytes.write(10);
      cache.writeIfNeeded(destination, bytes.toByteArray(), hashedBytes.hash());
   }
}
