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

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder();
      ArrayList var3 = new ArrayList();

      for (Path var5 : this.inputFolders) {
         var3.add(
            CompletableFuture.<CompletableFuture>supplyAsync(
                  () -> {
                     try {
                        CompletableFuture var4;
                        try (Stream var3x = Files.walk(var5)) {
                           var4 = CompletableFuture.allOf(
                              var3x.filter(var0x -> var0x.toString().endsWith(".nbt"))
                                 .map(var3xx -> CompletableFuture.runAsync(() -> convertStructure(var1, var3xx, getName(var5, var3xx), var2), Util.ioPool()))
                                 .toArray(CompletableFuture[]::new)
                           );
                        }
         
                        return var4;
                     } catch (IOException var8) {
                        LOGGER.error("Failed to read structure input directory", var8);
                        return CompletableFuture.completedFuture(null);
                     }
                  },
                  Util.backgroundExecutor()
               )
               .thenCompose(var0 -> var0)
         );
      }

      return CompletableFuture.allOf(var3.toArray(CompletableFuture[]::new));
   }

   @Override
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
         Path var7;
         try (
            InputStream var4 = Files.newInputStream(var1);
            FastBufferedInputStream var5 = new FastBufferedInputStream(var4);
         ) {
            Path var6 = var3.resolve(var2 + ".snbt");
            writeSnbt(var0, var6, NbtUtils.structureToSnbt(NbtIo.readCompressed(var5, NbtAccounter.unlimitedHeap())));
            LOGGER.info("Converted {} from NBT to SNBT", var2);
            var7 = var6;
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
