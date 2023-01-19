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
import javax.annotation.Nullable;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.slf4j.Logger;

public class NbtToSnbt implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataGenerator generator;

   public NbtToSnbt(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   @Override
   public void run(CachedOutput var1) throws IOException {
      Path var2 = this.generator.getOutputFolder();

      for(Path var4 : this.generator.getInputFolders()) {
         Files.walk(var4).filter(var0 -> var0.toString().endsWith(".nbt")).forEach(var4x -> convertStructure(var1, var4x, this.getName(var4, var4x), var2));
      }
   }

   @Override
   public String getName() {
      return "NBT to SNBT";
   }

   private String getName(Path var1, Path var2) {
      String var3 = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return var3.substring(0, var3.length() - ".nbt".length());
   }

   @Nullable
   public static Path convertStructure(CachedOutput var0, Path var1, String var2, Path var3) {
      try {
         Path var6;
         try (InputStream var4 = Files.newInputStream(var1)) {
            Path var5 = var3.resolve(var2 + ".snbt");
            writeSnbt(var0, var5, NbtUtils.structureToSnbt(NbtIo.readCompressed(var4)));
            LOGGER.info("Converted {} from NBT to SNBT", var2);
            var6 = var5;
         }

         return var6;
      } catch (IOException var9) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", new Object[]{var2, var1, var9});
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
