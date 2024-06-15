package net.minecraft.client.quickplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameType;
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

   static record QuickPlayEntry(QuickPlayLog.QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameType gamemode) {
      public static final Codec<QuickPlayLog.QuickPlayEntry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  QuickPlayLog.QuickPlayWorld.MAP_CODEC.forGetter(QuickPlayLog.QuickPlayEntry::quickPlayWorld),
                  ExtraCodecs.INSTANT_ISO8601.fieldOf("lastPlayedTime").forGetter(QuickPlayLog.QuickPlayEntry::lastPlayedTime),
                  GameType.CODEC.fieldOf("gamemode").forGetter(QuickPlayLog.QuickPlayEntry::gamemode)
               )
               .apply(var0, QuickPlayLog.QuickPlayEntry::new)
      );

      QuickPlayEntry(QuickPlayLog.QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameType gamemode) {
         super();
         this.quickPlayWorld = quickPlayWorld;
         this.lastPlayedTime = lastPlayedTime;
         this.gamemode = gamemode;
      }
   }

   static record QuickPlayWorld(QuickPlayLog.Type type, String id, String name) {
      public static final MapCodec<QuickPlayLog.QuickPlayWorld> MAP_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  QuickPlayLog.Type.CODEC.fieldOf("type").forGetter(QuickPlayLog.QuickPlayWorld::type),
                  ExtraCodecs.ESCAPED_STRING.fieldOf("id").forGetter(QuickPlayLog.QuickPlayWorld::id),
                  Codec.STRING.fieldOf("name").forGetter(QuickPlayLog.QuickPlayWorld::name)
               )
               .apply(var0, QuickPlayLog.QuickPlayWorld::new)
      );

      QuickPlayWorld(QuickPlayLog.Type type, String id, String name) {
         super();
         this.type = type;
         this.id = id;
         this.name = name;
      }
   }

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
