package net.minecraft.server.players;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StoredUserList {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final Gson gson;
   private final File file;
   private final Map map = Maps.newHashMap();
   private boolean enabled = true;
   private static final ParameterizedType USERLIST_ENTRY_TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{StoredUserEntry.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public StoredUserList(File var1) {
      this.file = var1;
      GsonBuilder var2 = (new GsonBuilder()).setPrettyPrinting();
      var2.registerTypeHierarchyAdapter(StoredUserEntry.class, new StoredUserList.Serializer());
      this.gson = var2.create();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   public File getFile() {
      return this.file;
   }

   public void add(StoredUserEntry var1) {
      this.map.put(this.getKeyForUser(var1.getUser()), var1);

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after adding a user.", var3);
      }

   }

   @Nullable
   public StoredUserEntry get(Object var1) {
      this.removeExpired();
      return (StoredUserEntry)this.map.get(this.getKeyForUser(var1));
   }

   public void remove(Object var1) {
      this.map.remove(this.getKeyForUser(var1));

      try {
         this.save();
      } catch (IOException var3) {
         LOGGER.warn("Could not save the list after removing a user.", var3);
      }

   }

   public void remove(StoredUserEntry var1) {
      this.remove(var1.getUser());
   }

   public String[] getUserList() {
      return (String[])this.map.keySet().toArray(new String[this.map.size()]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String getKeyForUser(Object var1) {
      return var1.toString();
   }

   protected boolean contains(Object var1) {
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

   protected StoredUserEntry createEntry(JsonObject var1) {
      return new StoredUserEntry((Object)null, var1);
   }

   public Collection getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      Collection var1 = this.map.values();
      String var2 = this.gson.toJson(var1);
      BufferedWriter var3 = null;

      try {
         var3 = Files.newWriter(this.file, StandardCharsets.UTF_8);
         var3.write(var2);
      } finally {
         IOUtils.closeQuietly(var3);
      }

   }

   public void load() throws FileNotFoundException {
      if (this.file.exists()) {
         BufferedReader var1 = null;

         try {
            var1 = Files.newReader(this.file, StandardCharsets.UTF_8);
            Collection var2 = (Collection)GsonHelper.fromJson(this.gson, (Reader)var1, (Type)USERLIST_ENTRY_TYPE);
            if (var2 != null) {
               this.map.clear();
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  StoredUserEntry var4 = (StoredUserEntry)var3.next();
                  if (var4.getUser() != null) {
                     this.map.put(this.getKeyForUser(var4.getUser()), var4);
                  }
               }
            }
         } finally {
            IOUtils.closeQuietly(var1);
         }

      }
   }

   class Serializer implements JsonDeserializer, JsonSerializer {
      private Serializer() {
      }

      public JsonElement serialize(StoredUserEntry var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var1.serialize(var4);
         return var4;
      }

      public StoredUserEntry deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var4 = var1.getAsJsonObject();
            return StoredUserList.this.createEntry(var4);
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((StoredUserEntry)var1, var2, var3);
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
