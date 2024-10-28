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
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.FastBufferedInputStream;
import org.slf4j.Logger;

public class NbtToSnbt implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Iterable<Path> inputFolders;
   private final PackOutput output;

   public NbtToSnbt(PackOutput var1, Collection<Path> var2) {
      super();
      this.inputFolders = var2;
      this.output = var1;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder();
      ArrayList var3 = new ArrayList();
      Iterator var4 = this.inputFolders.iterator();

      while(var4.hasNext()) {
         Path var5 = (Path)var4.next();
         var3.add(CompletableFuture.supplyAsync(() -> {
            try {
               Stream var3 = Files.walk(var5);

               CompletableFuture var4;
               try {
                  var4 = CompletableFuture.allOf((CompletableFuture[])var3.filter((var0) -> {
                     return var0.toString().endsWith(".nbt");
                  }).map((var3x) -> {
                     return CompletableFuture.runAsync(() -> {
                        convertStructure(var1, var3x, getName(var5, var3x), var2);
                     }, Util.ioPool());
                  }).toArray((var0) -> {
                     return new CompletableFuture[var0];
                  }));
               } catch (Throwable var7) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (var3 != null) {
                  var3.close();
               }

               return var4;
            } catch (IOException var8) {
               LOGGER.error("Failed to read structure input directory", var8);
               return CompletableFuture.completedFuture((Object)null);
            }
         }, Util.backgroundExecutor()).thenCompose((var0) -> {
            return var0;
         }));
      }

      return CompletableFuture.allOf((CompletableFuture[])var3.toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
   }

   public final String getName() {
      return "NBT -> SNBT";
   }

   private static String getName(Path var0, Path var1) {
      String var2 = var0.relativize(var1).toString().replaceAll("\\\\", "/");
      return var2.substring(0, var2.length() - ".nbt".length());
   }

   @Nullable
   public static Path convertStructure(CachedOutput var0, Path var1, String var2, Path var3) {
      try {
         InputStream var4 = Files.newInputStream(var1);

         Path var7;
         try {
            FastBufferedInputStream var5 = new FastBufferedInputStream(var4);

            try {
               Path var6 = var3.resolve(var2 + ".snbt");
               writeSnbt(var0, var6, NbtUtils.structureToSnbt(NbtIo.readCompressed((InputStream)var5, NbtAccounter.unlimitedHeap())));
               LOGGER.info("Converted {} from NBT to SNBT", var2);
               var7 = var6;
            } catch (Throwable var10) {
               try {
                  ((InputStream)var5).close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            ((InputStream)var5).close();
         } catch (Throwable var11) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (var4 != null) {
            var4.close();
         }

         return var7;
      } catch (IOException var12) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", new Object[]{var2, var1, var12});
         return null;
      }
   }

   public static void writeSnbt(CachedOutput var0, Path var1, String var2) throws IOException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      HashingOutputStream var4 = new HashingOutputStream(Hashing.sha1(), var3);
      var4.write(var2.getBytes(StandardCharsets.UTF_8));
      var4.write(10);
      var0.writeIfNeeded(var1, var3.toByteArray(), var4.hash());
   }
}
