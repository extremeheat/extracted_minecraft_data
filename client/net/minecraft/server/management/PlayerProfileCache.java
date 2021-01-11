package net.minecraft.server.management;

import com.google.common.base.Charsets;
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
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {
   public static final SimpleDateFormat field_152659_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private final Map<String, PlayerProfileCache.ProfileEntry> field_152661_c = Maps.newHashMap();
   private final Map<UUID, PlayerProfileCache.ProfileEntry> field_152662_d = Maps.newHashMap();
   private final LinkedList<GameProfile> field_152663_e = Lists.newLinkedList();
   private final MinecraftServer field_152664_f;
   protected final Gson field_152660_b;
   private final File field_152665_g;
   private static final ParameterizedType field_152666_h = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{PlayerProfileCache.ProfileEntry.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public PlayerProfileCache(MinecraftServer var1, File var2) {
      super();
      this.field_152664_f = var1;
      this.field_152665_g = var2;
      GsonBuilder var3 = new GsonBuilder();
      var3.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
      this.field_152660_b = var3.create();
      this.func_152657_b();
   }

   private static GameProfile func_152650_a(MinecraftServer var0, String var1) {
      final GameProfile[] var2 = new GameProfile[1];
      ProfileLookupCallback var3 = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile var1) {
            var2[0] = var1;
         }

         public void onProfileLookupFailed(GameProfile var1, Exception var2x) {
            var2[0] = null;
         }
      };
      var0.func_152359_aw().findProfilesByNames(new String[]{var1}, Agent.MINECRAFT, var3);
      if (!var0.func_71266_T() && var2[0] == null) {
         UUID var4 = EntityPlayer.func_146094_a(new GameProfile((UUID)null, var1));
         GameProfile var5 = new GameProfile(var4, var1);
         var3.onProfileLookupSucceeded(var5);
      }

      return var2[0];
   }

   public void func_152649_a(GameProfile var1) {
      this.func_152651_a(var1, (Date)null);
   }

   private void func_152651_a(GameProfile var1, Date var2) {
      UUID var3 = var1.getId();
      if (var2 == null) {
         Calendar var4 = Calendar.getInstance();
         var4.setTime(new Date());
         var4.add(2, 1);
         var2 = var4.getTime();
      }

      String var7 = var1.getName().toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry var5 = new PlayerProfileCache.ProfileEntry(var1, var2);
      if (this.field_152662_d.containsKey(var3)) {
         PlayerProfileCache.ProfileEntry var6 = (PlayerProfileCache.ProfileEntry)this.field_152662_d.get(var3);
         this.field_152661_c.remove(var6.func_152668_a().getName().toLowerCase(Locale.ROOT));
         this.field_152663_e.remove(var1);
      }

      this.field_152661_c.put(var1.getName().toLowerCase(Locale.ROOT), var5);
      this.field_152662_d.put(var3, var5);
      this.field_152663_e.addFirst(var1);
      this.func_152658_c();
   }

   public GameProfile func_152655_a(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry var3 = (PlayerProfileCache.ProfileEntry)this.field_152661_c.get(var2);
      if (var3 != null && (new Date()).getTime() >= var3.field_152673_c.getTime()) {
         this.field_152662_d.remove(var3.func_152668_a().getId());
         this.field_152661_c.remove(var3.func_152668_a().getName().toLowerCase(Locale.ROOT));
         this.field_152663_e.remove(var3.func_152668_a());
         var3 = null;
      }

      GameProfile var4;
      if (var3 != null) {
         var4 = var3.func_152668_a();
         this.field_152663_e.remove(var4);
         this.field_152663_e.addFirst(var4);
      } else {
         var4 = func_152650_a(this.field_152664_f, var2);
         if (var4 != null) {
            this.func_152649_a(var4);
            var3 = (PlayerProfileCache.ProfileEntry)this.field_152661_c.get(var2);
         }
      }

      this.func_152658_c();
      return var3 == null ? null : var3.func_152668_a();
   }

   public String[] func_152654_a() {
      ArrayList var1 = Lists.newArrayList(this.field_152661_c.keySet());
      return (String[])var1.toArray(new String[var1.size()]);
   }

   public GameProfile func_152652_a(UUID var1) {
      PlayerProfileCache.ProfileEntry var2 = (PlayerProfileCache.ProfileEntry)this.field_152662_d.get(var1);
      return var2 == null ? null : var2.func_152668_a();
   }

   private PlayerProfileCache.ProfileEntry func_152653_b(UUID var1) {
      PlayerProfileCache.ProfileEntry var2 = (PlayerProfileCache.ProfileEntry)this.field_152662_d.get(var1);
      if (var2 != null) {
         GameProfile var3 = var2.func_152668_a();
         this.field_152663_e.remove(var3);
         this.field_152663_e.addFirst(var3);
      }

      return var2;
   }

   public void func_152657_b() {
      BufferedReader var1 = null;

      try {
         var1 = Files.newReader(this.field_152665_g, Charsets.UTF_8);
         List var2 = (List)this.field_152660_b.fromJson(var1, field_152666_h);
         this.field_152661_c.clear();
         this.field_152662_d.clear();
         this.field_152663_e.clear();
         Iterator var3 = Lists.reverse(var2).iterator();

         while(var3.hasNext()) {
            PlayerProfileCache.ProfileEntry var4 = (PlayerProfileCache.ProfileEntry)var3.next();
            if (var4 != null) {
               this.func_152651_a(var4.func_152668_a(), var4.func_152670_b());
            }
         }
      } catch (FileNotFoundException var9) {
      } catch (JsonParseException var10) {
      } finally {
         IOUtils.closeQuietly(var1);
      }

   }

   public void func_152658_c() {
      String var1 = this.field_152660_b.toJson(this.func_152656_a(1000));
      BufferedWriter var2 = null;

      try {
         var2 = Files.newWriter(this.field_152665_g, Charsets.UTF_8);
         var2.write(var1);
         return;
      } catch (FileNotFoundException var8) {
      } catch (IOException var9) {
         return;
      } finally {
         IOUtils.closeQuietly(var2);
      }

   }

   private List<PlayerProfileCache.ProfileEntry> func_152656_a(int var1) {
      ArrayList var2 = Lists.newArrayList();
      ArrayList var3 = Lists.newArrayList(Iterators.limit(this.field_152663_e.iterator(), var1));
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         PlayerProfileCache.ProfileEntry var6 = this.func_152653_b(var5.getId());
         if (var6 != null) {
            var2.add(var6);
         }
      }

      return var2;
   }

   class ProfileEntry {
      private final GameProfile field_152672_b;
      private final Date field_152673_c;

      private ProfileEntry(GameProfile var2, Date var3) {
         super();
         this.field_152672_b = var2;
         this.field_152673_c = var3;
      }

      public GameProfile func_152668_a() {
         return this.field_152672_b;
      }

      public Date func_152670_b() {
         return this.field_152673_c;
      }

      // $FF: synthetic method
      ProfileEntry(GameProfile var2, Date var3, Object var4) {
         this(var2, var3);
      }
   }

   class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
      private Serializer() {
         super();
      }

      public JsonElement serialize(PlayerProfileCache.ProfileEntry var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("name", var1.func_152668_a().getName());
         UUID var5 = var1.func_152668_a().getId();
         var4.addProperty("uuid", var5 == null ? "" : var5.toString());
         var4.addProperty("expiresOn", PlayerProfileCache.field_152659_a.format(var1.func_152670_b()));
         return var4;
      }

      public PlayerProfileCache.ProfileEntry deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
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
                     var10 = PlayerProfileCache.field_152659_a.parse(var7.getAsString());
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

                  PlayerProfileCache.ProfileEntry var12 = PlayerProfileCache.this.new ProfileEntry(new GameProfile(var11, var9), var10);
                  return var12;
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
         return this.serialize((PlayerProfileCache.ProfileEntry)var1, var2, var3);
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
