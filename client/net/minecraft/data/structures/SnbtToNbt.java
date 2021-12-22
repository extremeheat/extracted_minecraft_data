package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtToNbt implements DataProvider {
   @Nullable
   private static final Path DUMP_SNBT_TO = null;
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;
   private final List<SnbtToNbt.Filter> filters = Lists.newArrayList();

   public SnbtToNbt(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public SnbtToNbt addFilter(SnbtToNbt.Filter var1) {
      this.filters.add(var1);
      return this;
   }

   private CompoundTag applyFilters(String var1, CompoundTag var2) {
      CompoundTag var3 = var2;

      SnbtToNbt.Filter var5;
      for(Iterator var4 = this.filters.iterator(); var4.hasNext(); var3 = var5.apply(var1, var3)) {
         var5 = (SnbtToNbt.Filter)var4.next();
      }

      return var3;
   }

   public void run(HashCache var1) throws IOException {
      Path var2 = this.generator.getOutputFolder();
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = this.generator.getInputFolders().iterator();

      while(var4.hasNext()) {
         Path var5 = (Path)var4.next();
         Files.walk(var5).filter((var0) -> {
            return var0.toString().endsWith(".snbt");
         }).forEach((var3x) -> {
            var3.add(CompletableFuture.supplyAsync(() -> {
               return this.readStructure(var3x, this.getName(var5, var3x));
            }, Util.backgroundExecutor()));
         });
      }

      boolean var9 = false;
      Iterator var10 = var3.iterator();

      while(var10.hasNext()) {
         CompletableFuture var6 = (CompletableFuture)var10.next();

         try {
            this.storeStructureIfChanged(var1, (SnbtToNbt.TaskResult)var6.get(), var2);
         } catch (Exception var8) {
            LOGGER.error("Failed to process structure", var8);
            var9 = true;
         }
      }

      if (var9) {
         throw new IllegalStateException("Failed to convert all structures, aborting");
      }
   }

   public String getName() {
      return "SNBT -> NBT";
   }

   private String getName(Path var1, Path var2) {
      String var3 = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return var3.substring(0, var3.length() - ".snbt".length());
   }

   private SnbtToNbt.TaskResult readStructure(Path var1, String var2) {
      try {
         BufferedReader var3 = Files.newBufferedReader(var1);

         SnbtToNbt.TaskResult var10;
         try {
            String var4 = IOUtils.toString(var3);
            CompoundTag var5 = this.applyFilters(var2, NbtUtils.snbtToStructure(var4));
            ByteArrayOutputStream var6 = new ByteArrayOutputStream();
            NbtIo.writeCompressed(var5, (OutputStream)var6);
            byte[] var7 = var6.toByteArray();
            String var8 = SHA1.hashBytes(var7).toString();
            String var9;
            if (DUMP_SNBT_TO != null) {
               var9 = NbtUtils.structureToSnbt(var5);
            } else {
               var9 = null;
            }

            var10 = new SnbtToNbt.TaskResult(var2, var7, var9, var8);
         } catch (Throwable var12) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (var3 != null) {
            var3.close();
         }

         return var10;
      } catch (Throwable var13) {
         throw new SnbtToNbt.StructureConversionException(var1, var13);
      }
   }

   private void storeStructureIfChanged(HashCache var1, SnbtToNbt.TaskResult var2, Path var3) {
      Path var4;
      if (var2.snbtPayload != null) {
         var4 = DUMP_SNBT_TO.resolve(var2.name + ".snbt");

         try {
            NbtToSnbt.writeSnbt(var4, var2.snbtPayload);
         } catch (IOException var9) {
            LOGGER.error("Couldn't write structure SNBT {} at {}", var2.name, var4, var9);
         }
      }

      var4 = var3.resolve(var2.name + ".nbt");

      try {
         if (!Objects.equals(var1.getHash(var4), var2.hash) || !Files.exists(var4, new LinkOption[0])) {
            Files.createDirectories(var4.getParent());
            OutputStream var5 = Files.newOutputStream(var4);

            try {
               var5.write(var2.payload);
            } catch (Throwable var10) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }
               }

               throw var10;
            }

            if (var5 != null) {
               var5.close();
            }
         }

         var1.putNew(var4, var2.hash);
      } catch (IOException var11) {
         LOGGER.error("Couldn't write structure {} at {}", var2.name, var4, var11);
      }

   }

   @FunctionalInterface
   public interface Filter {
      CompoundTag apply(String var1, CompoundTag var2);
   }

   static class TaskResult {
      final String name;
      final byte[] payload;
      @Nullable
      final String snbtPayload;
      final String hash;

      public TaskResult(String var1, byte[] var2, @Nullable String var3, String var4) {
         super();
         this.name = var1;
         this.payload = var2;
         this.snbtPayload = var3;
         this.hash = var4;
      }
   }

   private static class StructureConversionException extends RuntimeException {
      public StructureConversionException(Path var1, Throwable var2) {
         super(var1.toAbsolutePath().toString(), var2);
      }
   }
}
