package net.minecraft.server.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class CachedUserNameToIdResolver implements UserNameToIdResolver {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int GAMEPROFILES_MRU_LIMIT = 1000;
   private static final int GAMEPROFILES_EXPIRATION_MONTHS = 1;
   private boolean resolveOfflineUsers = true;
   private final Map<String, GameProfileInfo> profilesByName = new ConcurrentHashMap();
   private final Map<UUID, GameProfileInfo> profilesByUUID = new ConcurrentHashMap();
   private final Map<String, CompletableFuture<Optional<NameAndId>>> requests = new ConcurrentHashMap();
   private final GameProfileRepository profileRepository;
   private final Gson gson = (new GsonBuilder()).create();
   private final File file;
   private final AtomicLong operationCount = new AtomicLong();

   public CachedUserNameToIdResolver(GameProfileRepository var1, File var2) {
      super();
      this.profileRepository = var1;
      this.file = var2;
      Lists.reverse(this.load()).forEach(this::safeAdd);
   }

   private void safeAdd(GameProfileInfo var1) {
      NameAndId var2 = var1.nameAndId();
      var1.setLastAccess(this.getNextOperation());
      this.profilesByName.put(var2.name().toLowerCase(Locale.ROOT), var1);
      this.profilesByUUID.put(var2.id(), var1);
   }

   private Optional<NameAndId> lookupGameProfile(GameProfileRepository var1, String var2) {
      if (!StringUtil.isValidPlayerName(var2)) {
         return this.createUnknownProfile(var2);
      } else {
         Optional var3 = var1.findProfileByName(var2).map(NameAndId::new);
         return var3.isEmpty() ? this.createUnknownProfile(var2) : var3;
      }
   }

   private Optional<NameAndId> createUnknownProfile(String var1) {
      return this.resolveOfflineUsers ? Optional.of(NameAndId.createOffline(var1)) : Optional.empty();
   }

   public void resolveOfflineUsers(boolean var1) {
      this.resolveOfflineUsers = var1;
   }

   public void add(NameAndId var1) {
      this.addInternal(var1);
   }

   private GameProfileInfo addInternal(NameAndId var1) {
      Calendar var2 = Calendar.getInstance();
      var2.setTime(new Date());
      var2.add(2, 1);
      Date var3 = var2.getTime();
      GameProfileInfo var4 = new GameProfileInfo(var1, var3);
      this.safeAdd(var4);
      this.save();
      return var4;
   }

   private long getNextOperation() {
      return this.operationCount.incrementAndGet();
   }

   public Optional<NameAndId> get(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      GameProfileInfo var3 = (GameProfileInfo)this.profilesByName.get(var2);
      boolean var4 = false;
      if (var3 != null && (new Date()).getTime() >= var3.expirationDate.getTime()) {
         this.profilesByUUID.remove(var3.nameAndId().id());
         this.profilesByName.remove(var3.nameAndId().name().toLowerCase(Locale.ROOT));
         var4 = true;
         var3 = null;
      }

      Optional var5;
      if (var3 != null) {
         var3.setLastAccess(this.getNextOperation());
         var5 = Optional.of(var3.nameAndId());
      } else {
         Optional var6 = this.lookupGameProfile(this.profileRepository, var2);
         if (var6.isPresent()) {
            var5 = Optional.of(this.addInternal((NameAndId)var6.get()).nameAndId());
            var4 = false;
         } else {
            var5 = Optional.empty();
         }
      }

      if (var4) {
         this.save();
      }

      return var5;
   }

   public CompletableFuture<Optional<NameAndId>> getAsync(String var1) {
      CompletableFuture var2 = (CompletableFuture)this.requests.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         CompletableFuture var3 = CompletableFuture.supplyAsync(() -> this.get(var1), Util.backgroundExecutor().forName("getProfile"));
         this.requests.put(var1, var3);
         var3.whenComplete((var3x, var4) -> this.requests.remove(var1, var3));
         return var3;
      }
   }

   public Optional<NameAndId> get(UUID var1) {
      GameProfileInfo var2 = (GameProfileInfo)this.profilesByUUID.get(var1);
      if (var2 == null) {
         return Optional.empty();
      } else {
         var2.setLastAccess(this.getNextOperation());
         return Optional.of(var2.nameAndId());
      }
   }

   private static DateFormat createDateFormat() {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
   }

   private List<GameProfileInfo> load() {
      ArrayList var1 = Lists.newArrayList();

      try {
         BufferedReader var2 = Files.newReader(this.file, StandardCharsets.UTF_8);

         ArrayList var9;
         label61: {
            try {
               JsonArray var3 = (JsonArray)this.gson.fromJson(var2, JsonArray.class);
               if (var3 == null) {
                  var9 = var1;
                  break label61;
               }

               DateFormat var4 = createDateFormat();
               var3.forEach((var2x) -> {
                  Optional var10000 = readGameProfile(var2x, var4);
                  Objects.requireNonNull(var1);
                  var10000.ifPresent(var1::add);
               });
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     ((Reader)var2).close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               ((Reader)var2).close();
            }

            return var1;
         }

         if (var2 != null) {
            ((Reader)var2).close();
         }

         return var9;
      } catch (FileNotFoundException var7) {
      } catch (JsonParseException | IOException var8) {
         LOGGER.warn("Failed to load profile cache {}", this.file, var8);
      }

      return var1;
   }

   public void save() {
      JsonArray var1 = new JsonArray();
      DateFormat var2 = createDateFormat();
      this.getTopMRUProfiles(1000).forEach((var2x) -> var1.add(writeGameProfile(var2x, var2)));
      String var3 = this.gson.toJson(var1);

      try {
         BufferedWriter var4 = Files.newWriter(this.file, StandardCharsets.UTF_8);

         try {
            ((Writer)var4).write(var3);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  ((Writer)var4).close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            ((Writer)var4).close();
         }
      } catch (IOException var9) {
      }

   }

   private Stream<GameProfileInfo> getTopMRUProfiles(int var1) {
      return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::lastAccess).reversed()).limit((long)var1);
   }

   private static JsonElement writeGameProfile(GameProfileInfo var0, DateFormat var1) {
      JsonObject var2 = new JsonObject();
      var0.nameAndId().appendTo(var2);
      var2.addProperty("expiresOn", var1.format(var0.expirationDate()));
      return var2;
   }

   private static Optional<GameProfileInfo> readGameProfile(JsonElement var0, DateFormat var1) {
      if (var0.isJsonObject()) {
         JsonObject var2 = var0.getAsJsonObject();
         NameAndId var3 = NameAndId.fromJson(var2);
         if (var3 != null) {
            JsonElement var4 = var2.get("expiresOn");
            if (var4 != null) {
               String var5 = var4.getAsString();

               try {
                  Date var6 = var1.parse(var5);
                  return Optional.of(new GameProfileInfo(var3, var6));
               } catch (ParseException var7) {
                  LOGGER.warn("Failed to parse date {}", var5, var7);
               }
            }
         }
      }

      return Optional.empty();
   }

   static class GameProfileInfo {
      private final NameAndId nameAndId;
      final Date expirationDate;
      private volatile long lastAccess;

      GameProfileInfo(NameAndId var1, Date var2) {
         super();
         this.nameAndId = var1;
         this.expirationDate = var2;
      }

      public NameAndId nameAndId() {
         return this.nameAndId;
      }

      public Date expirationDate() {
         return this.expirationDate;
      }

      public void setLastAccess(long var1) {
         this.lastAccess = var1;
      }

      public long lastAccess() {
         return this.lastAccess;
      }
   }
}
