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
import com.mojang.logging.LogUtils;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import org.slf4j.Logger;

public class GameProfileCache {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int GAMEPROFILES_MRU_LIMIT = 1000;
   private static final int GAMEPROFILES_EXPIRATION_MONTHS = 1;
   private static boolean usesAuthentication;
   private final Map<String, GameProfileInfo> profilesByName = Maps.newConcurrentMap();
   private final Map<UUID, GameProfileInfo> profilesByUUID = Maps.newConcurrentMap();
   private final Map<String, CompletableFuture<Optional<GameProfile>>> requests = Maps.newConcurrentMap();
   private final GameProfileRepository profileRepository;
   private final Gson gson = (new GsonBuilder()).create();
   private final File file;
   private final AtomicLong operationCount = new AtomicLong();
   @Nullable
   private Executor executor;

   public GameProfileCache(GameProfileRepository var1, File var2) {
      super();
      this.profileRepository = var1;
      this.file = var2;
      Lists.reverse(this.load()).forEach(this::safeAdd);
   }

   private void safeAdd(GameProfileInfo var1) {
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

   private static Optional<GameProfile> lookupGameProfile(GameProfileRepository var0, String var1) {
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
         UUID var5 = UUIDUtil.getOrCreatePlayerUUID(new GameProfile((UUID)null, var1));
         return Optional.of(new GameProfile(var5, var1));
      } else {
         return Optional.ofNullable(var4);
      }
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
      GameProfileInfo var4 = new GameProfileInfo(var1, var3);
      this.safeAdd(var4);
      this.save();
   }

   private long getNextOperation() {
      return this.operationCount.incrementAndGet();
   }

   public Optional<GameProfile> get(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      GameProfileInfo var3 = (GameProfileInfo)this.profilesByName.get(var2);
      boolean var4 = false;
      if (var3 != null && (new Date()).getTime() >= var3.expirationDate.getTime()) {
         this.profilesByUUID.remove(var3.getProfile().getId());
         this.profilesByName.remove(var3.getProfile().getName().toLowerCase(Locale.ROOT));
         var4 = true;
         var3 = null;
      }

      Optional var5;
      if (var3 != null) {
         var3.setLastAccess(this.getNextOperation());
         var5 = Optional.of(var3.getProfile());
      } else {
         var5 = lookupGameProfile(this.profileRepository, var2);
         if (var5.isPresent()) {
            this.add((GameProfile)var5.get());
            var4 = false;
         }
      }

      if (var4) {
         this.save();
      }

      return var5;
   }

   public void getAsync(String var1, Consumer<Optional<GameProfile>> var2) {
      if (this.executor == null) {
         throw new IllegalStateException("No executor");
      } else {
         CompletableFuture var3 = (CompletableFuture)this.requests.get(var1);
         if (var3 != null) {
            this.requests.put(var1, var3.whenCompleteAsync((var1x, var2x) -> {
               var2.accept(var1x);
            }, this.executor));
         } else {
            this.requests.put(var1, CompletableFuture.supplyAsync(() -> {
               return this.get(var1);
            }, Util.backgroundExecutor()).whenCompleteAsync((var2x, var3x) -> {
               this.requests.remove(var1);
            }, this.executor).whenCompleteAsync((var1x, var2x) -> {
               var2.accept(var1x);
            }, this.executor));
         }

      }
   }

   public Optional<GameProfile> get(UUID var1) {
      GameProfileInfo var2 = (GameProfileInfo)this.profilesByUUID.get(var1);
      if (var2 == null) {
         return Optional.empty();
      } else {
         var2.setLastAccess(this.getNextOperation());
         return Optional.of(var2.getProfile());
      }
   }

   public void setExecutor(Executor var1) {
      this.executor = var1;
   }

   public void clearExecutor() {
      this.executor = null;
   }

   private static DateFormat createDateFormat() {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   }

   public List<GameProfileInfo> load() {
      ArrayList var1 = Lists.newArrayList();

      try {
         BufferedReader var2 = Files.newReader(this.file, StandardCharsets.UTF_8);

         label54: {
            ArrayList var4;
            try {
               JsonArray var3 = (JsonArray)this.gson.fromJson(var2, JsonArray.class);
               if (var3 != null) {
                  DateFormat var9 = createDateFormat();
                  var3.forEach((var2x) -> {
                     Optional var10000 = readGameProfile(var2x, var9);
                     Objects.requireNonNull(var1);
                     var10000.ifPresent(var1::add);
                  });
                  break label54;
               }

               var4 = var1;
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }

            return var4;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (FileNotFoundException var7) {
      } catch (JsonParseException | IOException var8) {
         LOGGER.warn("Failed to load profile cache {}", this.file, var8);
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

         try {
            var4.write(var3);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (IOException var9) {
      }

   }

   private Stream<GameProfileInfo> getTopMRUProfiles(int var1) {
      return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::getLastAccess).reversed()).limit((long)var1);
   }

   private static JsonElement writeGameProfile(GameProfileInfo var0, DateFormat var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("name", var0.getProfile().getName());
      UUID var3 = var0.getProfile().getId();
      var2.addProperty("uuid", var3 == null ? "" : var3.toString());
      var2.addProperty("expiresOn", var1.format(var0.getExpirationDate()));
      return var2;
   }

   private static Optional<GameProfileInfo> readGameProfile(JsonElement var0, DateFormat var1) {
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
                  return Optional.empty();
               }

               return Optional.of(new GameProfileInfo(new GameProfile(var9, var7), var8));
            } else {
               return Optional.empty();
            }
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   static class GameProfileInfo {
      private final GameProfile profile;
      final Date expirationDate;
      private volatile long lastAccess;

      GameProfileInfo(GameProfile var1, Date var2) {
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
   }
}
