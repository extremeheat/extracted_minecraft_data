package net.minecraft.server.players;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.IOUtils;

public class GameProfileCache {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private static boolean usesAuthentication;
   private final Map<String, GameProfileCache.GameProfileInfo> profilesByName = Maps.newHashMap();
   private final Map<UUID, GameProfileCache.GameProfileInfo> profilesByUUID = Maps.newHashMap();
   private final Deque<GameProfile> profileMRUList = Lists.newLinkedList();
   private final GameProfileRepository profileRepository;
   protected final Gson gson;
   private final File file;
   private static final ParameterizedType GAMEPROFILE_ENTRY_TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{GameProfileCache.GameProfileInfo.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public GameProfileCache(GameProfileRepository var1, File var2) {
      super();
      this.profileRepository = var1;
      this.file = var2;
      GsonBuilder var3 = new GsonBuilder();
      var3.registerTypeHierarchyAdapter(GameProfileCache.GameProfileInfo.class, new GameProfileCache.Serializer());
      this.gson = var3.create();
      this.load();
   }

   private static GameProfile lookupGameProfile(GameProfileRepository var0, String var1) {
      final GameProfile[] var2 = new GameProfile[1];
      ProfileLookupCallback var3 = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile var1) {
            var2[0] = var1;
         }

         public void onProfileLookupFailed(GameProfile var1, Exception var2x) {
            var2[0] = null;
         }
      };
      var0.findProfilesByNames(new String[]{var1}, Agent.MINECRAFT, var3);
      if (!usesAuthentication() && var2[0] == null) {
         UUID var4 = Player.createPlayerUUID(new GameProfile((UUID)null, var1));
         GameProfile var5 = new GameProfile(var4, var1);
         var3.onProfileLookupSucceeded(var5);
      }

      return var2[0];
   }

   public static void setUsesAuthentication(boolean var0) {
      usesAuthentication = var0;
   }

   private static boolean usesAuthentication() {
      return usesAuthentication;
   }

   public void add(GameProfile var1) {
      this.add(var1, (Date)null);
   }

   private void add(GameProfile var1, Date var2) {
      UUID var3 = var1.getId();
      if (var2 == null) {
         Calendar var4 = Calendar.getInstance();
         var4.setTime(new Date());
         var4.add(2, 1);
         var2 = var4.getTime();
      }

      GameProfileCache.GameProfileInfo var6 = new GameProfileCache.GameProfileInfo(var1, var2);
      if (this.profilesByUUID.containsKey(var3)) {
         GameProfileCache.GameProfileInfo var5 = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(var3);
         this.profilesByName.remove(var5.getProfile().getName().toLowerCase(Locale.ROOT));
         this.profileMRUList.remove(var1);
      }

      this.profilesByName.put(var1.getName().toLowerCase(Locale.ROOT), var6);
      this.profilesByUUID.put(var3, var6);
      this.profileMRUList.addFirst(var1);
      this.save();
   }

   @Nullable
   public GameProfile get(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      GameProfileCache.GameProfileInfo var3 = (GameProfileCache.GameProfileInfo)this.profilesByName.get(var2);
      if (var3 != null && (new Date()).getTime() >= var3.expirationDate.getTime()) {
         this.profilesByUUID.remove(var3.getProfile().getId());
         this.profilesByName.remove(var3.getProfile().getName().toLowerCase(Locale.ROOT));
         this.profileMRUList.remove(var3.getProfile());
         var3 = null;
      }

      GameProfile var4;
      if (var3 != null) {
         var4 = var3.getProfile();
         this.profileMRUList.remove(var4);
         this.profileMRUList.addFirst(var4);
      } else {
         var4 = lookupGameProfile(this.profileRepository, var2);
         if (var4 != null) {
            this.add(var4);
            var3 = (GameProfileCache.GameProfileInfo)this.profilesByName.get(var2);
         }
      }

      this.save();
      return var3 == null ? null : var3.getProfile();
   }

   @Nullable
   public GameProfile get(UUID var1) {
      GameProfileCache.GameProfileInfo var2 = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(var1);
      return var2 == null ? null : var2.getProfile();
   }

   private GameProfileCache.GameProfileInfo getProfileInfo(UUID var1) {
      GameProfileCache.GameProfileInfo var2 = (GameProfileCache.GameProfileInfo)this.profilesByUUID.get(var1);
      if (var2 != null) {
         GameProfile var3 = var2.getProfile();
         this.profileMRUList.remove(var3);
         this.profileMRUList.addFirst(var3);
      }

      return var2;
   }

   public void load() {
      BufferedReader var1 = null;

      try {
         var1 = Files.newReader(this.file, StandardCharsets.UTF_8);
         List var2 = (List)GsonHelper.fromJson(this.gson, (Reader)var1, (Type)GAMEPROFILE_ENTRY_TYPE);
         this.profilesByName.clear();
         this.profilesByUUID.clear();
         this.profileMRUList.clear();
         if (var2 != null) {
            Iterator var3 = Lists.reverse(var2).iterator();

            while(var3.hasNext()) {
               GameProfileCache.GameProfileInfo var4 = (GameProfileCache.GameProfileInfo)var3.next();
               if (var4 != null) {
                  this.add(var4.getProfile(), var4.getExpirationDate());
               }
            }
         }
      } catch (FileNotFoundException var9) {
      } catch (JsonParseException var10) {
      } finally {
         IOUtils.closeQuietly(var1);
      }

   }

   public void save() {
      String var1 = this.gson.toJson(this.getTopMRUProfiles(1000));
      BufferedWriter var2 = null;

      try {
         var2 = Files.newWriter(this.file, StandardCharsets.UTF_8);
         var2.write(var1);
         return;
      } catch (FileNotFoundException var8) {
      } catch (IOException var9) {
         return;
      } finally {
         IOUtils.closeQuietly(var2);
      }

   }

   private List<GameProfileCache.GameProfileInfo> getTopMRUProfiles(int var1) {
      ArrayList var2 = Lists.newArrayList();
      ArrayList var3 = Lists.newArrayList(Iterators.limit(this.profileMRUList.iterator(), var1));
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         GameProfileCache.GameProfileInfo var6 = this.getProfileInfo(var5.getId());
         if (var6 != null) {
            var2.add(var6);
         }
      }

      return var2;
   }

   class GameProfileInfo {
      private final GameProfile profile;
      private final Date expirationDate;

      private GameProfileInfo(GameProfile var2, Date var3) {
         super();
         this.profile = var2;
         this.expirationDate = var3;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }

      // $FF: synthetic method
      GameProfileInfo(GameProfile var2, Date var3, Object var4) {
         this(var2, var3);
      }
   }

   class Serializer implements JsonDeserializer<GameProfileCache.GameProfileInfo>, JsonSerializer<GameProfileCache.GameProfileInfo> {
      private Serializer() {
         super();
      }

      public JsonElement serialize(GameProfileCache.GameProfileInfo var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("name", var1.getProfile().getName());
         UUID var5 = var1.getProfile().getId();
         var4.addProperty("uuid", var5 == null ? "" : var5.toString());
         var4.addProperty("expiresOn", GameProfileCache.DATE_FORMAT.format(var1.getExpirationDate()));
         return var4;
      }

      public GameProfileCache.GameProfileInfo deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var4 = var1.getAsJsonObject();
            JsonElement var5 = var4.get("name");
            JsonElement var6 = var4.get("uuid");
            JsonElement var7 = var4.get("expiresOn");
            if (var5 != null && var6 != null) {
               String var8 = var6.getAsString();
               String var9 = var5.getAsString();
               Date var10 = null;
               if (var7 != null) {
                  try {
                     var10 = GameProfileCache.DATE_FORMAT.parse(var7.getAsString());
                  } catch (ParseException var14) {
                     var10 = null;
                  }
               }

               if (var9 != null && var8 != null) {
                  UUID var11;
                  try {
                     var11 = UUID.fromString(var8);
                  } catch (Throwable var13) {
                     return null;
                  }

                  return GameProfileCache.this.new GameProfileInfo(new GameProfile(var11, var9), var10);
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

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((GameProfileCache.GameProfileInfo)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }

      // $FF: synthetic method
      Serializer(Object var2) {
         this();
      }
   }
}
