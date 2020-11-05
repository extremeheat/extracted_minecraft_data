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
         Throwable var4 = null;

         SnbtToNbt.TaskResult var11;
         try {
            String var5 = IOUtils.toString(var3);
            CompoundTag var6 = this.applyFilters(var2, NbtUtils.snbtToStructure(var5));
            ByteArrayOutputStream var7 = new ByteArrayOutputStream();
            NbtIo.writeCompressed(var6, (OutputStream)var7);
            byte[] var8 = var7.toByteArray();
            String var9 = SHA1.hashBytes(var8).toString();
            String var10;
            if (DUMP_SNBT_TO != null) {
               var10 = NbtUtils.structureToSnbt(var6);
            } else {
               var10 = null;
            }

            var11 = new SnbtToNbt.TaskResult(var2, var8, var10, var9);
         } catch (Throwable var21) {
            var4 = var21;
            throw var21;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var20) {
                     var4.addSuppressed(var20);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var11;
      } catch (Throwable var23) {
         throw new SnbtToNbt.StructureConversionException(var1, var23);
      }
   }

   private void storeStructureIfChanged(HashCache var1, SnbtToNbt.TaskResult var2, Path var3) {
      Path var4;
      if (var2.snbtPayload != null) {
         var4 = DUMP_SNBT_TO.resolve(var2.name + ".snbt");

         try {
            NbtToSnbt.writeSnbt(var4, var2.snbtPayload);
         } catch (IOException var18) {
            LOGGER.error("Couldn't write structure SNBT {} at {}", var2.name, var4, var18);
         }
      }

      var4 = var3.resolve(var2.name + ".nbt");

      try {
         if (!Objects.equals(var1.getHash(var4), var2.hash) || !Files.exists(var4, new LinkOption[0])) {
            Files.createDirectories(var4.getParent());
            OutputStream var5 = Files.newOutputStream(var4);
            Throwable var6 = null;

            try {
               var5.write(var2.payload);
            } catch (Throwable var17) {
               var6 = var17;
               throw var17;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var16) {
                        var6.addSuppressed(var16);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         }

         var1.putNew(var4, var2.hash);
      } catch (IOException var20) {
         LOGGER.error("Couldn't write structure {} at {}", var2.name, var4, var20);
      }

   }

   static class StructureConversionException extends RuntimeException {
      public StructureConversionException(Path var1, Throwable var2) {
         super(var1.toAbsolutePath().toString(), var2);
      }
   }

   @FunctionalInterface
   public interface Filter {
      CompoundTag apply(String var1, CompoundTag var2);
   }

   static class TaskResult {
      private final String name;
      private final byte[] payload;
      @Nullable
      private final String snbtPayload;
      private final String hash;

      public TaskResult(String var1, byte[] var2, @Nullable String var3, String var4) {
         super();
         this.name = var1;
         this.payload = var2;
         this.snbtPayload = var3;
         this.hash = var4;
      }
   }
}
