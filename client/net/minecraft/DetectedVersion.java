package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.PackType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class DetectedVersion implements WorldVersion {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldVersion BUILT_IN = new DetectedVersion();
   private final String id;
   private final String name;
   private final boolean stable;
   private final DataVersion worldVersion;
   private final int protocolVersion;
   private final int resourcePackVersion;
   private final int dataPackVersion;
   private final Date buildTime;
   private final String releaseTarget;

   private DetectedVersion() {
      super();
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.19";
      this.stable = true;
      this.worldVersion = new DataVersion(3105, "main");
      this.protocolVersion = SharedConstants.getProtocolVersion();
      this.resourcePackVersion = 9;
      this.dataPackVersion = 10;
      this.buildTime = new Date();
      this.releaseTarget = "1.19";
   }

   private DetectedVersion(JsonObject var1) {
      super();
      this.id = GsonHelper.getAsString(var1, "id");
      this.name = GsonHelper.getAsString(var1, "name");
      this.releaseTarget = GsonHelper.getAsString(var1, "release_target");
      this.stable = GsonHelper.getAsBoolean(var1, "stable");
      this.worldVersion = new DataVersion(GsonHelper.getAsInt(var1, "world_version"), GsonHelper.getAsString(var1, "series_id", DataVersion.MAIN_SERIES));
      this.protocolVersion = GsonHelper.getAsInt(var1, "protocol_version");
      JsonObject var2 = GsonHelper.getAsJsonObject(var1, "pack_version");
      this.resourcePackVersion = GsonHelper.getAsInt(var2, "resource");
      this.dataPackVersion = GsonHelper.getAsInt(var2, "data");
      this.buildTime = Date.from(ZonedDateTime.parse(GsonHelper.getAsString(var1, "build_time")).toInstant());
   }

   public static WorldVersion tryDetectVersion() {
      try {
         DetectedVersion var2;
         try (InputStream var0 = DetectedVersion.class.getResourceAsStream("/version.json")) {
            if (var0 == null) {
               LOGGER.warn("Missing version information!");
               return BUILT_IN;
            }

            try (InputStreamReader var1 = new InputStreamReader(var0)) {
               var2 = new DetectedVersion(GsonHelper.parse(var1));
            }
         }

         return var2;
      } catch (JsonParseException | IOException var8) {
         throw new IllegalStateException("Game version information is corrupt", var8);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getReleaseTarget() {
      return this.releaseTarget;
   }

   @Override
   public DataVersion getDataVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion(PackType var1) {
      return var1 == PackType.DATA ? this.dataPackVersion : this.resourcePackVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}
