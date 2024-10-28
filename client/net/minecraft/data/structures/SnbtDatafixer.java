package net.minecraft.data.structures;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.Bootstrap;

public class SnbtDatafixer {
   public SnbtDatafixer() {
      super();
   }

   public static void main(String[] var0) throws IOException {
      SharedConstants.setVersion(DetectedVersion.BUILT_IN);
      Bootstrap.bootStrap();
      String[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         updateInDirectory(var4);
      }

   }

   private static void updateInDirectory(String var0) throws IOException {
      Stream var1 = Files.walk(Paths.get(var0));

      try {
         var1.filter((var0x) -> {
            return var0x.toString().endsWith(".snbt");
         }).forEach((var0x) -> {
            try {
               String var1 = Files.readString(var0x);
               CompoundTag var2 = NbtUtils.snbtToStructure(var1);
               CompoundTag var3 = StructureUpdater.update(var0x.toString(), var2);
               NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, var0x, NbtUtils.structureToSnbt(var3));
            } catch (IOException | CommandSyntaxException var4) {
               throw new RuntimeException(var4);
            }
         });
      } catch (Throwable var5) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (var1 != null) {
         var1.close();
      }

   }
}
