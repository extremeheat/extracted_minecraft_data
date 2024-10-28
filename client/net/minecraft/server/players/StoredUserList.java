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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class StoredUserList<K, V extends StoredUserEntry<K>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final File file;
   private final Map<String, V> map = Maps.newHashMap();

   public StoredUserList(File var1) {
      super();
      this.file = var1;
   }

   public File getFile() {
      return this.file;
   }

   public void add(V var1) {
      this.map.put(this.getKeyForUser(var1.getUser()), var1);

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after adding a user.", var3);
      }

   }

   @Nullable
   public V get(K var1) {
      this.removeExpired();
      return (StoredUserEntry)this.map.get(this.getKeyForUser(var1));
   }

   public void remove(K var1) {
      this.map.remove(this.getKeyForUser(var1));

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after removing a user.", var3);
      }

   }

   public void remove(StoredUserEntry<K> var1) {
      this.remove(var1.getUser());
   }

   public String[] getUserList() {
      return (String[])this.map.keySet().toArray(new String[0]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String getKeyForUser(K var1) {
      return var1.toString();
   }

   protected boolean contains(K var1) {
      return this.map.containsKey(this.getKeyForUser(var1));
   }

   private void removeExpired() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.map.values().iterator();

      while(var2.hasNext()) {
         StoredUserEntry var3 = (StoredUserEntry)var2.next();
         if (var3.hasExpired()) {
            var1.add(var3.getUser());
         }
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var4 = var2.next();
         this.map.remove(this.getKeyForUser(var4));
      }

   }

   protected abstract StoredUserEntry<K> createEntry(JsonObject var1);

   public Collection<V> getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray var1 = new JsonArray();
      Stream var10000 = this.map.values().stream().map((var0) -> {
         JsonObject var10000 = new JsonObject();
         Objects.requireNonNull(var0);
         return (JsonObject)Util.make(var10000, var0::serialize);
      });
      Objects.requireNonNull(var1);
      var10000.forEach(var1::add);
      BufferedWriter var2 = Files.newWriter(this.file, StandardCharsets.UTF_8);

      try {
         GSON.toJson(var1, GSON.newJsonWriter(var2));
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

   }

   public void load() throws IOException {
      if (this.file.exists()) {
         BufferedReader var1 = Files.newReader(this.file, StandardCharsets.UTF_8);

         label54: {
            try {
               this.map.clear();
               JsonArray var2 = (JsonArray)GSON.fromJson(var1, JsonArray.class);
               if (var2 != null) {
                  Iterator var3 = var2.iterator();

                  while(true) {
                     if (!var3.hasNext()) {
                        break label54;
                     }

                     JsonElement var4 = (JsonElement)var3.next();
                     JsonObject var5 = GsonHelper.convertToJsonObject(var4, "entry");
                     StoredUserEntry var6 = this.createEntry(var5);
                     if (var6.getUser() != null) {
                        this.map.put(this.getKeyForUser(var6.getUser()), var6);
                     }
                  }
               }
            } catch (Throwable var8) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var1 != null) {
               var1.close();
            }

            return;
         }

         if (var1 != null) {
            var1.close();
         }

      }
   }
}
