package net.minecraft.server.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameProfileCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private static boolean usesAuthentication;
   private final Map<String, GameProfileCache.GameProfileInfo> profilesByName = Maps.newConcurrentMap();
   private final Map<UUID, GameProfileCache.GameProfileInfo> profilesByUUID = Maps.newConcurrentMap();
   private final GameProfileRepository profileRepository;
   private final Gson gson = (new GsonBuilder()).create();
   private final File file;
   private final AtomicLong operationCount = new AtomicLong();

   public GameProfileCache(GameProfileRepository var1, File var2) {
      super();
      this.profileRepository = var1;
      this.file = var2;
      Lists.reverse(this.load()).forEach(this::safeAdd);
   }

   private void safeAdd(GameProfileCache.GameProfileInfo var1) {
      GameProfile var2 = var1.getProfile();
      var1.setLastAccess(this.getNextOperation());
      String var3 = var2.getName();
      if (var3 != null) {
         this.profilesByName.put(var3.toLowerCase(Locale.ROOT), var1);
      }

      UUID var4 = var2.getId();
      if (var4 != null) {
         this.profilesByUUID.put(var4, var1);
      }

   }

   @Nullable
   private static GameProfile lookupGameProfile(GameProfileRepository var0, String var1) {
      final AtomicReference var2 = new AtomicReference();
      ProfileLookupCallback var3 = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile var1) {
            var2.set(var1);
         }

         public void onProfileLookupFailed(GameProfile var1, Exception var2x) {
            var2.set((Object)null);
         }
      };
      var0.findProfilesByNames(new String[]{var1}, Agent.MINECRAFT, var3);
      GameProfile var4 = (GameProfile)var2.get();
      if (!usesAuthentication() && var4 == null) {
         UUID var5 = Player.createPlayerUUID(new GameProfile((UUID)null, var1));
         var4 = new GameProfile(var5, var1);
      }

      return var4;
   }

   public static void setUsesAuthentication(boolean var0) {
      usesAuthentication = var0;
   }

   private static boolean usesAuthentication() {
      return usesAuthentication;
   }

   public void add(GameProfile var1) {
      Calendar var2 = Calendar.getInstance();
      var2.setTime(new Date());
      var2.add(2, 1);
      Date var3 = var2.getTime();
      GameProfileCache.GameProfileInfo var4 = new GameProfileCache.GameProfileInfo(var1, var3);
      this.safeAdd(var4);
      this.save();
   }

   private long getNextOperation() {
      return this.operationCount.incrementAndGet();
   }

   @Nullable
   public GameProfile get(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      GameProfileCache.GameProfileInfo var3 = (GameProfileCache.GameProfileInfo)this.profilesByName.get(var2);
      boolean var4 = false;
      if (var3 != null && (new Date()).getTime() >= var3.expirationDate.getTime()) {
         this.profilesByUUID.remove(var3.getProfile().getId());
         this.profilesByName.remove(var3.getProfile().getName().toLowerCase(Locale.ROOT));
         var4 = true;
         var3 = null;
      }

      GameProfile var5;
      if (var3 != null) {
         var3.setLastAccess(this.getNextOperation());
         var5 = var3.getProfile();
      } else {
         var5 = lookupGameProfile(this.profileRepository, var2);
         if (var5 != null) {
            this.add(var5);
            var4 = false;
         }
      }

      if (var4) {
         this.save();
      }

      return var5;
   }

   @Nullable
   public GameProfile get(UUID var1) {
      GameProfileCache.GameProfileInfo var2 = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(var1);
      if (var2 == null) {
         return null;
      } else {
         var2.setLastAccess(this.getNextOperation());
         return var2.getProfile();
      }
   }

   private static DateFormat createDateFormat() {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   }

   public List<GameProfileCache.GameProfileInfo> load() {
      ArrayList var1 = Lists.newArrayList();

      try {
         BufferedReader var2 = Files.newReader(this.file, StandardCharsets.UTF_8);
         Throwable var3 = null;

         ArrayList var5;
         try {
            JsonArray var4 = (JsonArray)this.gson.fromJson(var2, JsonArray.class);
            if (var4 != null) {
               DateFormat var21 = createDateFormat();
               var4.forEach((var2x) -> {
                  GameProfileCache.GameProfileInfo var3 = readGameProfile(var2x, var21);
                  if (var3 != null) {
                     var1.add(var3);
                  }

               });
               return var1;
            }

            var5 = var1;
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var5;
      } catch (FileNotFoundException var19) {
      } catch (JsonParseException | IOException var20) {
         LOGGER.warn("Failed to load profile cache {}", this.file, var20);
      }

      return var1;
   }

   public void save() {
      JsonArray var1 = new JsonArray();
      DateFormat var2 = createDateFormat();
      this.getTopMRUProfiles(1000).forEach((var2x) -> {
         var1.add(writeGameProfile(var2x, var2));
      });
      String var3 = this.gson.toJson(var1);

      try {
         BufferedWriter var4 = Files.newWriter(this.file, StandardCharsets.UTF_8);
         Throwable var5 = null;

         try {
            var4.write(var3);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var17) {
      }

   }

   private Stream<GameProfileCache.GameProfileInfo> getTopMRUProfiles(int var1) {
      return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileCache.GameProfileInfo::getLastAccess).reversed()).limit((long)var1);
   }

   private static JsonElement writeGameProfile(GameProfileCache.GameProfileInfo var0, DateFormat var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("name", var0.getProfile().getName());
      UUID var3 = var0.getProfile().getId();
      var2.addProperty("uuid", var3 == null ? "" : var3.toString());
      var2.addProperty("expiresOn", var1.format(var0.getExpirationDate()));
      return var2;
   }

   @Nullable
   private static GameProfileCache.GameProfileInfo readGameProfile(JsonElement var0, DateFormat var1) {
      if (var0.isJsonObject()) {
         JsonObject var2 = var0.getAsJsonObject();
         JsonElement var3 = var2.get("name");
         JsonElement var4 = var2.get("uuid");
         JsonElement var5 = var2.get("expiresOn");
         if (var3 != null && var4 != null) {
            String var6 = var4.getAsString();
            String var7 = var3.getAsString();
            Date var8 = null;
            if (var5 != null) {
               try {
                  var8 = var1.parse(var5.getAsString());
               } catch (ParseException var12) {
               }
            }

            if (var7 != null && var6 != null && var8 != null) {
               UUID var9;
               try {
                  var9 = UUID.fromString(var6);
               } catch (Throwable var11) {
                  return null;
               }

               return new GameProfileCache.GameProfileInfo(new GameProfile(var9, var7), var8);
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   static class GameProfileInfo {
      private final GameProfile profile;
      private final Date expirationDate;
      private volatile long lastAccess;

      private GameProfileInfo(GameProfile var1, Date var2) {
         super();
         this.profile = var1;
         this.expirationDate = var2;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }

      public void setLastAccess(long var1) {
         this.lastAccess = var1;
      }

      public long getLastAccess() {
         return this.lastAccess;
      }

      // $FF: synthetic method
      GameProfileInfo(GameProfile var1, Date var2, Object var3) {
         this(var1, var2);
      }
   }
}
