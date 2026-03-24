package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class StoredUserList<K, V extends StoredUserEntry<K>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final File file;
   private final Map<String, V> map = Maps.newHashMap();
   protected final NotificationService notificationService;

   public StoredUserList(final File file, final NotificationService notificationService) {
      super();
      this.file = file;
      this.notificationService = notificationService;
   }

   public File getFile() {
      return this.file;
   }

   public boolean add(final V infos) {
      String keyForUser = this.getKeyForUser(((StoredUserEntry)infos).getUser());
      V previous = (V)(this.map.get(keyForUser));
      if (infos.equals(previous)) {
         return false;
      } else {
         this.map.put(keyForUser, infos);

         try {
            this.save();
         } catch (IOException e) {
            LOGGER.warn("Could not save the list after adding a user.", e);
         }

         return true;
      }
   }

   public @Nullable V get(final K user) {
      this.removeExpired();
      return (V)(this.map.get(this.getKeyForUser(user)));
   }

   public boolean remove(final K user) {
      V removed = (V)(this.map.remove(this.getKeyForUser(user)));
      if (removed == null) {
         return false;
      } else {
         try {
            this.save();
         } catch (IOException e) {
            LOGGER.warn("Could not save the list after removing a user.", e);
         }

         return true;
      }
   }

   public boolean remove(final StoredUserEntry<K> infos) {
      return this.remove(Objects.requireNonNull(infos.getUser()));
   }

   public void clear() {
      this.map.clear();

      try {
         this.save();
      } catch (IOException e) {
         LOGGER.warn("Could not save the list after removing a user.", e);
      }

   }

   public String[] getUserList() {
      return (String[])this.map.keySet().toArray(new String[0]);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   protected String getKeyForUser(final K user) {
      return user.toString();
   }

   protected boolean contains(final K user) {
      return this.map.containsKey(this.getKeyForUser(user));
   }

   private void removeExpired() {
      List<K> toRemove = Lists.newArrayList();

      for(V entry : this.map.values()) {
         if (entry.hasExpired()) {
            toRemove.add(((StoredUserEntry)entry).getUser());
         }
      }

      for(K user : toRemove) {
         this.map.remove(this.getKeyForUser(user));
      }

   }

   protected abstract StoredUserEntry<K> createEntry(final JsonObject object);

   public Collection<V> getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray result = new JsonArray();
      Stream var10000 = this.map.values().stream().map((entry) -> {
         JsonObject var10000 = new JsonObject();
         Objects.requireNonNull(entry);
         return (JsonObject)Util.make(var10000, entry::serialize);
      });
      Objects.requireNonNull(result);
      var10000.forEach(result::add);
      BufferedWriter writer = Files.newWriter(this.file, StandardCharsets.UTF_8);

      try {
         GSON.toJson(result, GSON.newJsonWriter(writer));
      } catch (Throwable var6) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (writer != null) {
         writer.close();
      }

   }

   public void load() throws IOException {
      if (this.file.exists()) {
         BufferedReader reader = Files.newReader(this.file, StandardCharsets.UTF_8);

         label54: {
            try {
               this.map.clear();
               JsonArray contents = (JsonArray)GSON.fromJson(reader, JsonArray.class);
               if (contents == null) {
                  break label54;
               }

               for(JsonElement element : contents) {
                  JsonObject object = GsonHelper.convertToJsonObject(element, "entry");
                  StoredUserEntry<K> entry = this.createEntry(object);
                  if (entry.getUser() != null) {
                     this.map.put(this.getKeyForUser(entry.getUser()), entry);
                  }
               }
            } catch (Throwable var8) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (reader != null) {
               reader.close();
            }

            return;
         }

         if (reader != null) {
            reader.close();
         }

      }
   }
}
