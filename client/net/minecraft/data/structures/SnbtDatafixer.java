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

      for(String var4 : var0) {
         updateInDirectory(var4);
      }
   }

   private static void updateInDirectory(String var0) throws IOException {
      try (Stream var1 = Files.walk(Paths.get(var0))) {
         var1.filter(var0x -> var0x.toString().endsWith(".snbt")).forEach(var0x -> {
            try {
               String var1x = Files.readString(var0x);
               CompoundTag var2 = NbtUtils.snbtToStructure(var1x);
               CompoundTag var3 = StructureUpdater.update(var0x.toString(), var2);
               NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, var0x, NbtUtils.structureToSnbt(var3));
            } catch (IOException | CommandSyntaxException var4) {
               throw new RuntimeException(var4);
            }
         });
      }
   }
}
