package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsManagerServer extends StatisticsManager {
   private static final Logger field_150889_b = LogManager.getLogger();
   private final MinecraftServer field_150890_c;
   private final File field_150887_d;
   private final Set<Stat<?>> field_150888_e = Sets.newHashSet();
   private int field_150885_f = -300;

   public StatisticsManagerServer(MinecraftServer var1, File var2) {
      super();
      this.field_150890_c = var1;
      this.field_150887_d = var2;
      if (var2.isFile()) {
         try {
            this.func_199062_a(var1.func_195563_aC(), FileUtils.readFileToString(var2));
         } catch (IOException var4) {
            field_150889_b.error("Couldn't read statistics file {}", var2, var4);
         } catch (JsonParseException var5) {
            field_150889_b.error("Couldn't parse statistics file {}", var2, var5);
         }
      }

   }

   public void func_150883_b() {
      try {
         FileUtils.writeStringToFile(this.field_150887_d, this.func_199061_b());
      } catch (IOException var2) {
         field_150889_b.error("Couldn't save stats", var2);
      }

   }

   public void func_150873_a(EntityPlayer var1, Stat<?> var2, int var3) {
      super.func_150873_a(var1, var2, var3);
      this.field_150888_e.add(var2);
   }

   private Set<Stat<?>> func_150878_c() {
      HashSet var1 = Sets.newHashSet(this.field_150888_e);
      this.field_150888_e.clear();
      return var1;
   }

   public void func_199062_a(DataFixer var1, String var2) {
      try {
         JsonReader var3 = new JsonReader(new StringReader(var2));
         Throwable var4 = null;

         try {
            var3.setLenient(false);
            JsonElement var5 = Streams.parse(var3);
            if (!var5.isJsonNull()) {
               NBTTagCompound var6 = func_199065_a(var5.getAsJsonObject());
               if (!var6.func_150297_b("DataVersion", 99)) {
                  var6.func_74768_a("DataVersion", 1343);
               }

               var6 = NBTUtil.func_210822_a(var1, DataFixTypes.STATS, var6, var6.func_74762_e("DataVersion"));
               if (var6.func_150297_b("stats", 10)) {
                  NBTTagCompound var7 = var6.func_74775_l("stats");
                  Iterator var8 = var7.func_150296_c().iterator();

                  while(true) {
                     while(true) {
                        String var9;
                        do {
                           if (!var8.hasNext()) {
                              return;
                           }

                           var9 = (String)var8.next();
                        } while(!var7.func_150297_b(var9, 10));

                        StatType var10 = (StatType)IRegistry.field_212634_w.func_212608_b(new ResourceLocation(var9));
                        if (var10 == null) {
                           field_150889_b.warn("Invalid statistic type in {}: Don't know what {} is", this.field_150887_d, var9);
                        } else {
                           NBTTagCompound var11 = var7.func_74775_l(var9);
                           Iterator var12 = var11.func_150296_c().iterator();

                           while(var12.hasNext()) {
                              String var13 = (String)var12.next();
                              if (var11.func_150297_b(var13, 99)) {
                                 Stat var14 = this.func_199063_a(var10, var13);
                                 if (var14 == null) {
                                    field_150889_b.warn("Invalid statistic in {}: Don't know what {} is", this.field_150887_d, var13);
                                 } else {
                                    this.field_150875_a.put(var14, var11.func_74762_e(var13));
                                 }
                              } else {
                                 field_150889_b.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.field_150887_d, var11.func_74781_a(var13), var13);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               field_150889_b.error("Unable to parse Stat data from {}", this.field_150887_d);
            }
         } catch (Throwable var23) {
            var4 = var23;
            throw var23;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var22) {
                     var4.addSuppressed(var22);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException | JsonParseException var25) {
         field_150889_b.error("Unable to parse Stat data from {}", this.field_150887_d, var25);
      }

   }

   @Nullable
   private <T> Stat<T> func_199063_a(StatType<T> var1, String var2) {
      ResourceLocation var3 = ResourceLocation.func_208304_a(var2);
      if (var3 == null) {
         return null;
      } else {
         Object var4 = var1.func_199080_a().func_212608_b(var3);
         return var4 == null ? null : var1.func_199076_b(var4);
      }
   }

   private static NBTTagCompound func_199065_a(JsonObject var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         JsonElement var4 = (JsonElement)var3.getValue();
         if (var4.isJsonObject()) {
            var1.func_74782_a((String)var3.getKey(), func_199065_a(var4.getAsJsonObject()));
         } else if (var4.isJsonPrimitive()) {
            JsonPrimitive var5 = var4.getAsJsonPrimitive();
            if (var5.isNumber()) {
               var1.func_74768_a((String)var3.getKey(), var5.getAsInt());
            }
         }
      }

      return var1;
   }

   protected String func_199061_b() {
      HashMap var1 = Maps.newHashMap();
      ObjectIterator var2 = this.field_150875_a.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var3 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         ((JsonObject)var1.computeIfAbsent(var4.func_197921_a(), (var0) -> {
            return new JsonObject();
         })).addProperty(func_199066_b(var4).toString(), var3.getIntValue());
      }

      JsonObject var5 = new JsonObject();
      Iterator var6 = var1.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var8 = (Entry)var6.next();
         var5.add(IRegistry.field_212634_w.func_177774_c(var8.getKey()).toString(), (JsonElement)var8.getValue());
      }

      JsonObject var7 = new JsonObject();
      var7.add("stats", var5);
      var7.addProperty("DataVersion", 1631);
      return var7.toString();
   }

   private static <T> ResourceLocation func_199066_b(Stat<T> var0) {
      return var0.func_197921_a().func_199080_a().func_177774_c(var0.func_197920_b());
   }

   public void func_150877_d() {
      this.field_150888_e.addAll(this.field_150875_a.keySet());
   }

   public void func_150876_a(EntityPlayerMP var1) {
      int var2 = this.field_150890_c.func_71259_af();
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      if (var2 - this.field_150885_f > 300) {
         this.field_150885_f = var2;
         Iterator var4 = this.func_150878_c().iterator();

         while(var4.hasNext()) {
            Stat var5 = (Stat)var4.next();
            var3.put(var5, this.func_77444_a(var5));
         }
      }

      var1.field_71135_a.func_147359_a(new SPacketStatistics(var3));
   }
}
