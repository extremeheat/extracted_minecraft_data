package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File file;
   private final Set dirty = Sets.newHashSet();
   private int lastStatRequest = -300;

   public ServerStatsCounter(MinecraftServer var1, File var2) {
      this.server = var1;
      this.file = var2;
      if (var2.isFile()) {
         try {
            this.parseLocal(var1.getFixerUpper(), FileUtils.readFileToString(var2));
         } catch (IOException var4) {
            LOGGER.error("Couldn't read statistics file {}", var2, var4);
         } catch (JsonParseException var5) {
            LOGGER.error("Couldn't parse statistics file {}", var2, var5);
         }
      }

   }

   public void save() {
      try {
         FileUtils.writeStringToFile(this.file, this.toJson());
      } catch (IOException var2) {
         LOGGER.error("Couldn't save stats", var2);
      }

   }

   public void setValue(Player var1, Stat var2, int var3) {
      super.setValue(var1, var2, var3);
      this.dirty.add(var2);
   }

   private Set getDirty() {
      HashSet var1 = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return var1;
   }

   public void parseLocal(DataFixer var1, String var2) {
      try {
         JsonReader var3 = new JsonReader(new StringReader(var2));
         Throwable var4 = null;

         try {
            var3.setLenient(false);
            JsonElement var5 = Streams.parse(var3);
            if (!var5.isJsonNull()) {
               CompoundTag var6 = fromJson(var5.getAsJsonObject());
               if (!var6.contains("DataVersion", 99)) {
                  var6.putInt("DataVersion", 1343);
               }

               var6 = NbtUtils.update(var1, DataFixTypes.STATS, var6, var6.getInt("DataVersion"));
               if (var6.contains("stats", 10)) {
                  CompoundTag var7 = var6.getCompound("stats");
                  Iterator var8 = var7.getAllKeys().iterator();

                  while(var8.hasNext()) {
                     String var9 = (String)var8.next();
                     if (var7.contains(var9, 10)) {
                        Util.ifElse(Registry.STAT_TYPE.getOptional(new ResourceLocation(var9)), (var3x) -> {
                           CompoundTag var4 = var7.getCompound(var9);
                           Iterator var5 = var4.getAllKeys().iterator();

                           while(var5.hasNext()) {
                              String var6 = (String)var5.next();
                              if (var4.contains(var6, 99)) {
                                 Util.ifElse(this.getStat(var3x, var6), (var3) -> {
                                    this.stats.put(var3, var4.getInt(var6));
                                 }, () -> {
                                    LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, var6);
                                 });
                              } else {
                                 LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, var4.get(var6), var6);
                              }
                           }

                        }, () -> {
                           LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, var9);
                        });
                     }
                  }
               }

               return;
            }

            LOGGER.error("Unable to parse Stat data from {}", this.file);
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }

      } catch (IOException | JsonParseException var21) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, var21);
      }
   }

   private Optional getStat(StatType var1, String var2) {
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(var2));
      Registry var10001 = var1.getRegistry();
      var10001.getClass();
      var10000 = var10000.flatMap(var10001::getOptional);
      var1.getClass();
      return var10000.map(var1::get);
   }

   private static CompoundTag fromJson(JsonObject var0) {
      CompoundTag var1 = new CompoundTag();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         JsonElement var4 = (JsonElement)var3.getValue();
         if (var4.isJsonObject()) {
            var1.put((String)var3.getKey(), fromJson(var4.getAsJsonObject()));
         } else if (var4.isJsonPrimitive()) {
            JsonPrimitive var5 = var4.getAsJsonPrimitive();
            if (var5.isNumber()) {
               var1.putInt((String)var3.getKey(), var5.getAsInt());
            }
         }
      }

      return var1;
   }

   protected String toJson() {
      HashMap var1 = Maps.newHashMap();
      ObjectIterator var2 = this.stats.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var3 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         ((JsonObject)var1.computeIfAbsent(var4.getType(), (var0) -> {
            return new JsonObject();
         })).addProperty(getKey(var4).toString(), var3.getIntValue());
      }

      JsonObject var5 = new JsonObject();
      Iterator var6 = var1.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var8 = (Entry)var6.next();
         var5.add(Registry.STAT_TYPE.getKey(var8.getKey()).toString(), (JsonElement)var8.getValue());
      }

      JsonObject var7 = new JsonObject();
      var7.add("stats", var5);
      var7.addProperty("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return var7.toString();
   }

   private static ResourceLocation getKey(Stat var0) {
      return var0.getType().getRegistry().getKey(var0.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.stats.keySet());
   }

   public void sendStats(ServerPlayer var1) {
      int var2 = this.server.getTickCount();
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      if (var2 - this.lastStatRequest > 300) {
         this.lastStatRequest = var2;
         Iterator var4 = this.getDirty().iterator();

         while(var4.hasNext()) {
            Stat var5 = (Stat)var4.next();
            var3.put(var5, this.getValue(var5));
         }
      }

      var1.connection.send(new ClientboundAwardStatsPacket(var3));
   }
}
