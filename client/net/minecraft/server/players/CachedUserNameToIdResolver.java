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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class CachedUserNameToIdResolver implements UserNameToIdResolver {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int GAMEPROFILES_MRU_LIMIT = 1000;
   private static final int GAMEPROFILES_EXPIRATION_MONTHS = 1;
   private boolean resolveOfflineUsers = true;
   private final Map<String, GameProfileInfo> profilesByName = new ConcurrentHashMap();
   private final Map<UUID, GameProfileInfo> profilesByUUID = new ConcurrentHashMap();
   private final GameProfileRepository profileRepository;
   private final Gson gson = (new GsonBuilder()).create();
   private final File file;
   private final AtomicLong operationCount = new AtomicLong();

   public CachedUserNameToIdResolver(final GameProfileRepository profileRepository, final File file) {
      super();
      this.profileRepository = profileRepository;
      this.file = file;
      Lists.reverse(this.load()).forEach(this::safeAdd);
   }

   private void safeAdd(final GameProfileInfo profileInfo) {
      NameAndId nameAndId = profileInfo.nameAndId();
      profileInfo.setLastAccess(this.getNextOperation());
      this.profilesByName.put(nameAndId.name().toLowerCase(Locale.ROOT), profileInfo);
      this.profilesByUUID.put(nameAndId.id(), profileInfo);
   }

   private Optional<NameAndId> lookupGameProfile(final GameProfileRepository profileRepository, final String name) {
      if (!StringUtil.isValidPlayerName(name)) {
         return this.createUnknownProfile(name);
      } else {
         Optional<NameAndId> profile = profileRepository.findProfileByName(name).map(NameAndId::new);
         return profile.isEmpty() ? this.createUnknownProfile(name) : profile;
      }
   }

   private Optional<NameAndId> createUnknownProfile(final String name) {
      return this.resolveOfflineUsers ? Optional.of(NameAndId.createOffline(name)) : Optional.empty();
   }

   public void resolveOfflineUsers(final boolean value) {
      this.resolveOfflineUsers = value;
   }

   public void add(final NameAndId nameAndId) {
      this.addInternal(nameAndId);
   }

   private GameProfileInfo addInternal(final NameAndId profile) {
      Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.ROOT);
      c.setTime(new Date());
      c.add(2, 1);
      Date expirationDate = c.getTime();
      GameProfileInfo profileInfo = new GameProfileInfo(profile, expirationDate);
      this.safeAdd(profileInfo);
      this.save();
      return profileInfo;
   }

   private long getNextOperation() {
      return this.operationCount.incrementAndGet();
   }

   public Optional<NameAndId> get(final String name) {
      String userName = name.toLowerCase(Locale.ROOT);
      GameProfileInfo profileInfo = (GameProfileInfo)this.profilesByName.get(userName);
      boolean needsSave = false;
      if (profileInfo != null && (new Date()).getTime() >= profileInfo.expirationDate.getTime()) {
         this.profilesByUUID.remove(profileInfo.nameAndId().id());
         this.profilesByName.remove(profileInfo.nameAndId().name().toLowerCase(Locale.ROOT));
         needsSave = true;
         profileInfo = null;
      }

      Optional<NameAndId> result;
      if (profileInfo != null) {
         profileInfo.setLastAccess(this.getNextOperation());
         result = Optional.of(profileInfo.nameAndId());
      } else {
         Optional<NameAndId> profile = this.lookupGameProfile(this.profileRepository, userName);
         if (profile.isPresent()) {
            result = Optional.of(this.addInternal((NameAndId)profile.get()).nameAndId());
            needsSave = false;
         } else {
            result = Optional.empty();
         }
      }

      if (needsSave) {
         this.save();
      }

      return result;
   }

   public Optional<NameAndId> get(final UUID id) {
      GameProfileInfo profileInfo = (GameProfileInfo)this.profilesByUUID.get(id);
      if (profileInfo == null) {
         return Optional.empty();
      } else {
         profileInfo.setLastAccess(this.getNextOperation());
         return Optional.of(profileInfo.nameAndId());
      }
   }

   private static DateFormat createDateFormat() {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
   }

   private List<GameProfileInfo> load() {
      List<GameProfileInfo> result = Lists.newArrayList();

      try {
         Reader reader = Files.newReader(this.file, StandardCharsets.UTF_8);

         Object var9;
         label61: {
            try {
               JsonArray entryList = (JsonArray)this.gson.fromJson(reader, JsonArray.class);
               if (entryList == null) {
                  var9 = result;
                  break label61;
               }

               DateFormat dateFormat = createDateFormat();
               entryList.forEach((element) -> {
                  Optional var10000 = readGameProfile(element, dateFormat);
                  Objects.requireNonNull(result);
                  var10000.ifPresent(result::add);
               });
            } catch (Throwable var6) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (reader != null) {
               reader.close();
            }

            return result;
         }

         if (reader != null) {
            reader.close();
         }

         return (List<GameProfileInfo>)var9;
      } catch (FileNotFoundException var7) {
      } catch (JsonParseException | IOException e) {
         LOGGER.warn("Failed to load profile cache {}", this.file, e);
      }

      return result;
   }

   public void save() {
      JsonArray entryList = new JsonArray();
      DateFormat dateFormat = createDateFormat();
      this.getTopMRUProfiles(1000).forEach((entry) -> entryList.add(writeGameProfile(entry, dateFormat)));
      String toSave = this.gson.toJson(entryList);

      try {
         Writer writer = Files.newWriter(this.file, StandardCharsets.UTF_8);

         try {
            writer.write(toSave);
         } catch (Throwable var8) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (writer != null) {
            writer.close();
         }
      } catch (IOException var9) {
      }

   }

   private Stream<GameProfileInfo> getTopMRUProfiles(final int limit) {
      return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::lastAccess).reversed()).limit((long)limit);
   }

   private static JsonElement writeGameProfile(final GameProfileInfo src, final DateFormat dateFormat) {
      JsonObject object = new JsonObject();
      src.nameAndId().appendTo(object);
      object.addProperty("expiresOn", dateFormat.format(src.expirationDate()));
      return object;
   }

   private static Optional<GameProfileInfo> readGameProfile(final JsonElement json, final DateFormat dateFormat) {
      if (json.isJsonObject()) {
         JsonObject object = json.getAsJsonObject();
         NameAndId nameAndId = NameAndId.fromJson(object);
         if (nameAndId != null) {
            JsonElement expirationElement = object.get("expiresOn");
            if (expirationElement != null) {
               String dateAsString = expirationElement.getAsString();

               try {
                  Date expirationDate = dateFormat.parse(dateAsString);
                  return Optional.of(new GameProfileInfo(nameAndId, expirationDate));
               } catch (ParseException e) {
                  LOGGER.warn("Failed to parse date {}", dateAsString, e);
               }
            }
         }
      }

      return Optional.empty();
   }

   private static class GameProfileInfo {
      private final NameAndId nameAndId;
      private final Date expirationDate;
      private volatile long lastAccess;

      private GameProfileInfo(final NameAndId nameAndId, final Date expirationDate) {
         super();
         this.nameAndId = nameAndId;
         this.expirationDate = expirationDate;
      }

      public NameAndId nameAndId() {
         return this.nameAndId;
      }

      public Date expirationDate() {
         return this.expirationDate;
      }

      public void setLastAccess(final long currentOperation) {
         this.lastAccess = currentOperation;
      }

      public long lastAccess() {
         return this.lastAccess;
      }
   }
}
