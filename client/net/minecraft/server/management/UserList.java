package net.minecraft.server.management;

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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>> {
   protected static final Logger field_152693_a = LogManager.getLogger();
   protected final Gson field_152694_b;
   private final File field_152695_c;
   private final Map<String, V> field_152696_d = Maps.newHashMap();
   private boolean field_152697_e = true;
   private static final ParameterizedType field_152698_f = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{UserListEntry.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public UserList(File var1) {
      super();
      this.field_152695_c = var1;
      GsonBuilder var2 = (new GsonBuilder()).setPrettyPrinting();
      var2.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
      this.field_152694_b = var2.create();
   }

   public boolean func_152689_b() {
      return this.field_152697_e;
   }

   public void func_152686_a(boolean var1) {
      this.field_152697_e = var1;
   }

   public File func_152691_c() {
      return this.field_152695_c;
   }

   public void func_152687_a(V var1) {
      this.field_152696_d.put(this.func_152681_a(var1.func_152640_f()), var1);

      try {
         this.func_152678_f();
      } catch (IOException var3) {
         field_152693_a.warn("Could not save the list after adding a user.", var3);
      }

   }

   @Nullable
   public V func_152683_b(K var1) {
      this.func_152680_h();
      return (UserListEntry)this.field_152696_d.get(this.func_152681_a(var1));
   }

   public void func_152684_c(K var1) {
      this.field_152696_d.remove(this.func_152681_a(var1));

      try {
         this.func_152678_f();
      } catch (IOException var3) {
         field_152693_a.warn("Could not save the list after removing a user.", var3);
      }

   }

   public void func_199042_b(UserListEntry<K> var1) {
      this.func_152684_c(var1.func_152640_f());
   }

   public String[] func_152685_a() {
      return (String[])this.field_152696_d.keySet().toArray(new String[this.field_152696_d.size()]);
   }

   public boolean func_152690_d() {
      return this.field_152696_d.size() < 1;
   }

   protected String func_152681_a(K var1) {
      return var1.toString();
   }

   protected boolean func_152692_d(K var1) {
      return this.field_152696_d.containsKey(this.func_152681_a(var1));
   }

   private void func_152680_h() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.field_152696_d.values().iterator();

      while(var2.hasNext()) {
         UserListEntry var3 = (UserListEntry)var2.next();
         if (var3.func_73682_e()) {
            var1.add(var3.func_152640_f());
         }
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var4 = var2.next();
         this.field_152696_d.remove(this.func_152681_a(var4));
      }

   }

   protected UserListEntry<K> func_152682_a(JsonObject var1) {
      return new UserListEntry((Object)null, var1);
   }

   public Collection<V> func_199043_f() {
      return this.field_152696_d.values();
   }

   public void func_152678_f() throws IOException {
      Collection var1 = this.field_152696_d.values();
      String var2 = this.field_152694_b.toJson(var1);
      BufferedWriter var3 = null;

      try {
         var3 = Files.newWriter(this.field_152695_c, StandardCharsets.UTF_8);
         var3.write(var2);
      } finally {
         IOUtils.closeQuietly(var3);
      }

   }

   public void func_152679_g() throws FileNotFoundException {
      if (this.field_152695_c.exists()) {
         BufferedReader var1 = null;

         try {
            var1 = Files.newReader(this.field_152695_c, StandardCharsets.UTF_8);
            Collection var2 = (Collection)JsonUtils.func_193841_a(this.field_152694_b, var1, field_152698_f);
            if (var2 != null) {
               this.field_152696_d.clear();
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  UserListEntry var4 = (UserListEntry)var3.next();
                  if (var4.func_152640_f() != null) {
                     this.field_152696_d.put(this.func_152681_a(var4.func_152640_f()), var4);
                  }
               }
            }
         } finally {
            IOUtils.closeQuietly(var1);
         }

      }
   }

   class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>> {
      private Serializer() {
         super();
      }

      public JsonElement serialize(UserListEntry<K> var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var1.func_152641_a(var4);
         return var4;
      }

      public UserListEntry<K> deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            JsonObject var4 = var1.getAsJsonObject();
            return UserList.this.func_152682_a(var4);
         } else {
            return null;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((UserListEntry)var1, var2, var3);
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
