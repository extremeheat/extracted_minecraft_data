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
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import org.slf4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftServer server;
   private final File file;
   private final Set<Stat<?>> dirty = Sets.newHashSet();

   public ServerStatsCounter(MinecraftServer var1, File var2) {
      super();
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

   public void setValue(Player var1, Stat<?> var2, int var3) {
      super.setValue(var1, var2, var3);
      this.dirty.add(var2);
   }

   private Set<Stat<?>> getDirty() {
      HashSet var1 = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return var1;
   }

   public void parseLocal(DataFixer var1, String var2) {
      try {
         JsonReader var3 = new JsonReader(new StringReader(var2));

         label66: {
            try {
               var3.setLenient(false);
               JsonElement var4 = Streams.parse(var3);
               if (var4.isJsonNull()) {
                  LOGGER.error("Unable to parse Stat data from {}", this.file);
                  break label66;
               }

               CompoundTag var5 = fromJson(var4.getAsJsonObject());
               if (!var5.contains("DataVersion", 99)) {
                  var5.putInt("DataVersion", 1343);
               }

               var5 = NbtUtils.update(var1, DataFixTypes.STATS, var5, var5.getInt("DataVersion"));
               if (var5.contains("stats", 10)) {
                  CompoundTag var6 = var5.getCompound("stats");
                  Iterator var7 = var6.getAllKeys().iterator();

                  while(var7.hasNext()) {
                     String var8 = (String)var7.next();
                     if (var6.contains(var8, 10)) {
                        Util.ifElse(Registry.STAT_TYPE.getOptional(new ResourceLocation(var8)), (var3x) -> {
                           CompoundTag var4 = var6.getCompound(var8);
                           Iterator var5 = var4.getAllKeys().iterator();

                           while(var5.hasNext()) {
                              String var6x = (String)var5.next();
                              if (var4.contains(var6x, 99)) {
                                 Util.ifElse(this.getStat(var3x, var6x), (var3) -> {
                                    this.stats.put(var3, var4.getInt(var6x));
                                 }, () -> {
                                    LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, var6x);
                                 });
                              } else {
                                 LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", new Object[]{this.file, var4.get(var6x), var6x});
                              }
                           }

                        }, () -> {
                           LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, var8);
                        });
                     }
                  }
               }
            } catch (Throwable var10) {
               try {
                  var3.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            var3.close();
            return;
         }

         var3.close();
      } catch (IOException | JsonParseException var11) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, var11);
      }
   }

   private <T> Optional<Stat<T>> getStat(StatType<T> var1, String var2) {
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(var2));
      Registry var10001 = var1.getRegistry();
      Objects.requireNonNull(var10001);
      var10000 = var10000.flatMap(var10001::getOptional);
      Objects.requireNonNull(var1);
      return var10000.map(var1::get);
   }

   private static CompoundTag fromJson(JsonObject var0) {
      CompoundTag var1 = new CompoundTag();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
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
         Object2IntMap.Entry var3 = (Object2IntMap.Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         ((JsonObject)var1.computeIfAbsent(var4.getType(), (var0) -> {
            return new JsonObject();
         })).addProperty(getKey(var4).toString(), var3.getIntValue());
      }

      JsonObject var5 = new JsonObject();
      Iterator var6 = var1.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry var8 = (Map.Entry)var6.next();
         var5.add(Registry.STAT_TYPE.getKey((StatType)var8.getKey()).toString(), (JsonElement)var8.getValue());
      }

      JsonObject var7 = new JsonObject();
      var7.add("stats", var5);
      var7.addProperty("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return var7.toString();
   }

   private static <T> ResourceLocation getKey(Stat<T> var0) {
      return var0.getType().getRegistry().getKey(var0.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.stats.keySet());
   }

   public void sendStats(ServerPlayer var1) {
      Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();
      Iterator var3 = this.getDirty().iterator();

      while(var3.hasNext()) {
         Stat var4 = (Stat)var3.next();
         var2.put(var4, this.getValue(var4));
      }

      var1.connection.send(new ClientboundAwardStatsPacket(var2));
   }
}
