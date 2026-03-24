package net.minecraft.client.quickplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class QuickPlayLog {
   private static final QuickPlayLog INACTIVE = new QuickPlayLog("") {
      public void log(final Minecraft minecraft) {
      }

      public void setWorldData(final Type type, final String id, final String name) {
      }
   };
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).create();
   private final Path path;
   private @Nullable QuickPlayWorld worldData;

   private QuickPlayLog(final String quickPlayPath) {
      super();
      this.path = Minecraft.getInstance().gameDirectory.toPath().resolve(quickPlayPath);
   }

   public static QuickPlayLog of(final @Nullable String path) {
      return path == null ? INACTIVE : new QuickPlayLog(path);
   }

   public void setWorldData(final Type type, final String id, final String name) {
      this.worldData = new QuickPlayWorld(type, id, name);
   }

   public void log(final Minecraft minecraft) {
      if (minecraft.gameMode != null && this.worldData != null) {
         Util.ioPool().execute(() -> {
            try {
               Files.deleteIfExists(this.path);
            } catch (IOException e) {
               LOGGER.error("Failed to delete quickplay log file {}", this.path, e);
            }

            QuickPlayEntry quickPlayEntry = new QuickPlayEntry(this.worldData, Instant.now(), minecraft.gameMode.getPlayerMode());
            DataResult var10000 = Codec.list(QuickPlayLog.QuickPlayEntry.CODEC).encodeStart(JsonOps.INSTANCE, List.of(quickPlayEntry));
            Logger var10002 = LOGGER;
            Objects.requireNonNull(var10002);
            var10000.resultOrPartial(Util.prefix("Quick Play: ", var10002::error)).ifPresent((json) -> {
               try {
                  Files.createDirectories(this.path.getParent());
                  Files.writeString(this.path, GSON.toJson(json));
               } catch (IOException e) {
                  LOGGER.error("Failed to write to quickplay log file {}", this.path, e);
               }

            });
         });
      } else {
         LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
      }
   }

   public static enum Type implements StringRepresentable {
      SINGLEPLAYER("singleplayer"),
      MULTIPLAYER("multiplayer"),
      REALMS("realms");

      private static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String name;

      private Type(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{SINGLEPLAYER, MULTIPLAYER, REALMS};
      }
   }

   private static record QuickPlayWorld(Type type, String id, String name) {
      public static final MapCodec<QuickPlayWorld> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(QuickPlayLog.Type.CODEC.fieldOf("type").forGetter(QuickPlayWorld::type), ExtraCodecs.ESCAPED_STRING.fieldOf("id").forGetter(QuickPlayWorld::id), Codec.STRING.fieldOf("name").forGetter(QuickPlayWorld::name)).apply(i, QuickPlayWorld::new));

      private QuickPlayWorld {
         super();
      }
   }

   private static record QuickPlayEntry(QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameType gamemode) {
      public static final Codec<QuickPlayEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(QuickPlayLog.QuickPlayWorld.MAP_CODEC.forGetter(QuickPlayEntry::quickPlayWorld), ExtraCodecs.INSTANT_ISO8601.fieldOf("lastPlayedTime").forGetter(QuickPlayEntry::lastPlayedTime), GameType.CODEC.fieldOf("gamemode").forGetter(QuickPlayEntry::gamemode)).apply(i, QuickPlayEntry::new));

      private QuickPlayEntry {
         super();
      }
   }
}
