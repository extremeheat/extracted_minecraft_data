package net.minecraft.client.quickplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public class QuickPlayLog {
   private static final QuickPlayLog INACTIVE = new QuickPlayLog("") {
      @Override
      public void log(Minecraft var1) {
      }

      @Override
      public void setWorldData(QuickPlayLog.Type var1, String var2, String var3) {
      }
   };
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().create();
   private final Path path;
   @Nullable
   private QuickPlayLog.QuickPlayWorld worldData;

   QuickPlayLog(String var1) {
      super();
      this.path = Minecraft.getInstance().gameDirectory.toPath().resolve(var1);
   }

   public static QuickPlayLog of(@Nullable String var0) {
      return var0 == null ? INACTIVE : new QuickPlayLog(var0);
   }

   public void setWorldData(QuickPlayLog.Type var1, String var2, String var3) {
      this.worldData = new QuickPlayLog.QuickPlayWorld(var1, var2, var3);
   }

   public void log(Minecraft var1) {
      if (var1.gameMode != null && this.worldData != null) {
         Util.ioPool()
            .execute(
               () -> {
                  try {
                     Files.deleteIfExists(this.path);
                  } catch (IOException var3) {
                     LOGGER.error("Failed to delete quickplay log file {}", this.path, var3);
                  }

                  QuickPlayLog.QuickPlayEntry var2 = new QuickPlayLog.QuickPlayEntry(this.worldData, Instant.now(), var1.gameMode.getPlayerMode());
                  Codec.list(QuickPlayLog.QuickPlayEntry.CODEC)
                     .encodeStart(JsonOps.INSTANCE, List.of(var2))
                     .resultOrPartial(Util.prefix("Quick Play: ", LOGGER::error))
                     .ifPresent(var1xx -> {
                        try {
                           Files.createDirectories(this.path.getParent());
                           Files.writeString(this.path, GSON.toJson(var1xx));
                        } catch (IOException var3x) {
                           LOGGER.error("Failed to write to quickplay log file {}", this.path, var3x);
                        }
                     });
               }
            );
      } else {
         LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static enum Type implements StringRepresentable {
      SINGLEPLAYER("singleplayer"),
      MULTIPLAYER("multiplayer"),
      REALMS("realms");

      static final Codec<QuickPlayLog.Type> CODEC = StringRepresentable.fromEnum(QuickPlayLog.Type::values);
      private final String name;

      private Type(final String nullxx) {
         this.name = nullxx;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
