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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtToNbt implements DataProvider {
   @Nullable
   private static final Path DUMP_SNBT_TO = null;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataGenerator generator;
   private final List<Filter> filters = Lists.newArrayList();

   public SnbtToNbt(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public SnbtToNbt addFilter(Filter var1) {
      this.filters.add(var1);
      return this;
   }

   private CompoundTag applyFilters(String var1, CompoundTag var2) {
      CompoundTag var3 = var2;

      Filter var5;
      for(Iterator var4 = this.filters.iterator(); var4.hasNext(); var3 = var5.apply(var1, var3)) {
         var5 = (Filter)var4.next();
      }

      return var3;
   }

   public void run(CachedOutput var1) throws IOException {
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
            this.storeStructureIfChanged(var1, (TaskResult)var6.get(), var2);
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

   private TaskResult readStructure(Path var1, String var2) {
      try {
         BufferedReader var3 = Files.newBufferedReader(var1);

         TaskResult var11;
         try {
            String var4 = IOUtils.toString(var3);
            CompoundTag var5 = this.applyFilters(var2, NbtUtils.snbtToStructure(var4));
            ByteArrayOutputStream var6 = new ByteArrayOutputStream();
            HashingOutputStream var7 = new HashingOutputStream(Hashing.sha1(), var6);
            NbtIo.writeCompressed(var5, (OutputStream)var7);
            byte[] var8 = var6.toByteArray();
            HashCode var9 = var7.hash();
            String var10;
            if (DUMP_SNBT_TO != null) {
               var10 = NbtUtils.structureToSnbt(var5);
            } else {
               var10 = null;
            }

            var11 = new TaskResult(var2, var8, var10, var9);
         } catch (Throwable var13) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (var3 != null) {
            var3.close();
         }

         return var11;
      } catch (Throwable var14) {
         throw new StructureConversionException(var1, var14);
      }
   }

   private void storeStructureIfChanged(CachedOutput var1, TaskResult var2, Path var3) {
      Path var4;
      if (var2.snbtPayload != null) {
         var4 = DUMP_SNBT_TO.resolve(var2.name + ".snbt");

         try {
            NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, var4, var2.snbtPayload);
         } catch (IOException var7) {
            LOGGER.error("Couldn't write structure SNBT {} at {}", new Object[]{var2.name, var4, var7});
         }
      }

      var4 = var3.resolve(var2.name + ".nbt");

      try {
         var1.writeIfNeeded(var4, var2.payload, var2.hash);
      } catch (IOException var6) {
         LOGGER.error("Couldn't write structure {} at {}", new Object[]{var2.name, var4, var6});
      }

   }

   @FunctionalInterface
   public interface Filter {
      CompoundTag apply(String var1, CompoundTag var2);
   }

   static record TaskResult(String a, byte[] b, @Nullable String c, HashCode d) {
      final String name;
      final byte[] payload;
      @Nullable
      final String snbtPayload;
      final HashCode hash;

      TaskResult(String var1, byte[] var2, @Nullable String var3, HashCode var4) {
         super();
         this.name = var1;
         this.payload = var2;
         this.snbtPayload = var3;
         this.hash = var4;
      }

      public String name() {
         return this.name;
      }

      public byte[] payload() {
         return this.payload;
      }

      @Nullable
      public String snbtPayload() {
         return this.snbtPayload;
      }

      public HashCode hash() {
         return this.hash;
      }
   }

   private static class StructureConversionException extends RuntimeException {
      public StructureConversionException(Path var1, Throwable var2) {
         super(var1.toAbsolutePath().toString(), var2);
      }
   }
}
