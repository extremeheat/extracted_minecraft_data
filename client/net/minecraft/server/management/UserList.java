package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList {
   protected static final Logger field_152693_a = LogManager.getLogger();
   protected final Gson field_152694_b;
   private final File field_152695_c;
   private final Map field_152696_d = Maps.newHashMap();
   private boolean field_152697_e = true;
   private static final ParameterizedType field_152698_f = new UserList$1();

   public UserList(File var1) {
      super();
      this.field_152695_c = var1;
      GsonBuilder var2 = new GsonBuilder().setPrettyPrinting();
      var2.registerTypeHierarchyAdapter(UserListEntry.class, new UserList$Serializer(this, null));
      this.field_152694_b = var2.create();
   }

   public boolean func_152689_b() {
      return this.field_152697_e;
   }

   public void func_152686_a(boolean var1) {
      this.field_152697_e = var1;
   }

   public void func_152687_a(UserListEntry var1) {
      this.field_152696_d.put(this.func_152681_a(var1.func_152640_f()), var1);

      try {
         this.func_152678_f();
      } catch (IOException var3) {
         field_152693_a.warn("Could not save the list after adding a user.", var3);
      }
   }

   public UserListEntry func_152683_b(Object var1) {
      this.func_152680_h();
      return (UserListEntry)this.field_152696_d.get(this.func_152681_a(var1));
   }

   public void func_152684_c(Object var1) {
      this.field_152696_d.remove(this.func_152681_a(var1));

      try {
         this.func_152678_f();
      } catch (IOException var3) {
         field_152693_a.warn("Could not save the list after removing a user.", var3);
      }
   }

   public String[] func_152685_a() {
      return this.field_152696_d.keySet().toArray(new String[this.field_152696_d.size()]);
   }

   protected String func_152681_a(Object var1) {
      return var1.toString();
   }

   protected boolean func_152692_d(Object var1) {
      return this.field_152696_d.containsKey(this.func_152681_a(var1));
   }

   private void func_152680_h() {
      ArrayList var1 = Lists.newArrayList();

      for(UserListEntry var3 : this.field_152696_d.values()) {
         if (var3.func_73682_e()) {
            var1.add(var3.func_152640_f());
         }
      }

      for(Object var5 : var1) {
         this.field_152696_d.remove(var5);
      }
   }

   protected UserListEntry func_152682_a(JsonObject var1) {
      return new UserListEntry((Object)null, var1);
   }

   protected Map func_152688_e() {
      return this.field_152696_d;
   }

   public void func_152678_f() {
      Collection var1 = this.field_152696_d.values();
      String var2 = this.field_152694_b.toJson(var1);
      BufferedWriter var3 = null;

      try {
         var3 = Files.newWriter(this.field_152695_c, Charsets.UTF_8);
         var3.write(var2);
      } finally {
         IOUtils.closeQuietly(var3);
      }
   }
}
