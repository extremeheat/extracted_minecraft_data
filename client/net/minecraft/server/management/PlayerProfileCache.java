package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
   private final Map field_152661_c = Maps.newHashMap();
   private final Map field_152662_d = Maps.newHashMap();
   private final LinkedList field_152663_e = Lists.newLinkedList();
   private final MinecraftServer field_152664_f;
   protected final Gson field_152660_b;
   private final File field_152665_g;
   private static final ParameterizedType field_152666_h = new PlayerProfileCache$2();

   public PlayerProfileCache(MinecraftServer var1, File var2) {
      super();
      this.field_152664_f = var1;
      this.field_152665_g = var2;
      GsonBuilder var3 = new GsonBuilder();
      var3.registerTypeHierarchyAdapter(PlayerProfileCache$ProfileEntry.class, new PlayerProfileCache$Serializer(this, null));
      this.field_152660_b = var3.create();
      this.func_152657_b();
   }

   private static GameProfile func_152650_a(MinecraftServer var0, String var1) {
      GameProfile[] var2 = new GameProfile[1];
      PlayerProfileCache$1 var3 = new PlayerProfileCache$1(var2);
      var0.func_152359_aw().findProfilesByNames(new String[]{var1}, Agent.MINECRAFT, var3);
      if (!var0.func_71266_T() && var2[0] == null) {
         UUID var4 = EntityPlayer.func_146094_a(new GameProfile(null, var1));
         GameProfile var5 = new GameProfile(var4, var1);
         var3.onProfileLookupSucceeded(var5);
      }

      return var2[0];
   }

   public void func_152649_a(GameProfile var1) {
      this.func_152651_a(var1, null);
   }

   private void func_152651_a(GameProfile var1, Date var2) {
      UUID var3 = var1.getId();
      if (var2 == null) {
         Calendar var4 = Calendar.getInstance();
         var4.setTime(new Date());
         var4.add(2, 1);
         var2 = var4.getTime();
      }

      String var10 = var1.getName().toLowerCase(Locale.ROOT);
      PlayerProfileCache$ProfileEntry var5 = new PlayerProfileCache$ProfileEntry(this, var1, var2, null);
      synchronized(this.field_152663_e) {
         if (this.field_152662_d.containsKey(var3)) {
            PlayerProfileCache$ProfileEntry var7 = (PlayerProfileCache$ProfileEntry)this.field_152662_d.get(var3);
            this.field_152661_c.remove(var7.func_152668_a().getName().toLowerCase(Locale.ROOT));
            this.field_152661_c.put(var1.getName().toLowerCase(Locale.ROOT), var5);
            this.field_152663_e.remove(var1);
         } else {
            this.field_152662_d.put(var3, var5);
            this.field_152661_c.put(var10, var5);
         }

         this.field_152663_e.addFirst(var1);
      }
   }

   public GameProfile func_152655_a(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      PlayerProfileCache$ProfileEntry var3 = (PlayerProfileCache$ProfileEntry)this.field_152661_c.get(var2);
      if (var3 != null && new Date().getTime() >= PlayerProfileCache$ProfileEntry.access$200(var3).getTime()) {
         this.field_152662_d.remove(var3.func_152668_a().getId());
         this.field_152661_c.remove(var3.func_152668_a().getName().toLowerCase(Locale.ROOT));
         synchronized(this.field_152663_e) {
            this.field_152663_e.remove(var3.func_152668_a());
         }

         var3 = null;
      }

      if (var3 != null) {
         GameProfile var9 = var3.func_152668_a();
         synchronized(this.field_152663_e) {
            this.field_152663_e.remove(var9);
            this.field_152663_e.addFirst(var9);
         }
      } else {
         GameProfile var10 = func_152650_a(this.field_152664_f, var2);
         if (var10 != null) {
            this.func_152649_a(var10);
            var3 = (PlayerProfileCache$ProfileEntry)this.field_152661_c.get(var2);
         }
      }

      this.func_152658_c();
      return var3 == null ? null : var3.func_152668_a();
   }

   public String[] func_152654_a() {
      ArrayList var1 = Lists.newArrayList(this.field_152661_c.keySet());
      return var1.toArray(new String[var1.size()]);
   }

   public GameProfile func_152652_a(UUID var1) {
      PlayerProfileCache$ProfileEntry var2 = (PlayerProfileCache$ProfileEntry)this.field_152662_d.get(var1);
      return var2 == null ? null : var2.func_152668_a();
   }

   private PlayerProfileCache$ProfileEntry func_152653_b(UUID var1) {
      PlayerProfileCache$ProfileEntry var2 = (PlayerProfileCache$ProfileEntry)this.field_152662_d.get(var1);
      if (var2 != null) {
         GameProfile var3 = var2.func_152668_a();
         synchronized(this.field_152663_e) {
            this.field_152663_e.remove(var3);
            this.field_152663_e.addFirst(var3);
         }
      }

      return var2;
   }

   public void func_152657_b() {
      Object var1 = null;
      BufferedReader var2 = null;

      label67: {
         try {
            var2 = Files.newReader(this.field_152665_g, Charsets.UTF_8);
            var12 = (List)this.field_152660_b.fromJson(var2, field_152666_h);
            break label67;
         } catch (FileNotFoundException var10) {
         } finally {
            IOUtils.closeQuietly(var2);
         }

         return;
      }

      if (var12 != null) {
         this.field_152661_c.clear();
         this.field_152662_d.clear();
         synchronized(this.field_152663_e) {
            this.field_152663_e.clear();
         }

         for(PlayerProfileCache$ProfileEntry var4 : Lists.reverse(var12)) {
            if (var4 != null) {
               this.func_152651_a(var4.func_152668_a(), var4.func_152670_b());
            }
         }
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
         return;
      } catch (IOException var9) {
      } finally {
         IOUtils.closeQuietly(var2);
      }
   }

   private List func_152656_a(int var1) {
      ArrayList var2 = Lists.newArrayList();
      ArrayList var3;
      synchronized(this.field_152663_e) {
         var3 = Lists.newArrayList(Iterators.limit(this.field_152663_e.iterator(), var1));
      }

      for(GameProfile var5 : var3) {
         PlayerProfileCache$ProfileEntry var6 = this.func_152653_b(var5.getId());
         if (var6 != null) {
            var2.add(var6);
         }
      }

      return var2;
   }
}
