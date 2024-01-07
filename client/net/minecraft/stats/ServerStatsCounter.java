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
import net.minecraft.core.registries.BuiltInRegistries;
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

   @Override
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

         label47: {
            try {
               var3.setLenient(false);
               JsonElement var4 = Streams.parse(var3);
               if (!var4.isJsonNull()) {
                  CompoundTag var5 = fromJson(var4.getAsJsonObject());
                  var5 = DataFixTypes.STATS.updateToCurrentVersion(var1, var5, NbtUtils.getDataVersion(var5, 1343));
                  if (!var5.contains("stats", 10)) {
                     break label47;
                  }

                  CompoundTag var6 = var5.getCompound("stats");
                  Iterator var7 = var6.getAllKeys().iterator();

                  while(true) {
                     if (!var7.hasNext()) {
                        break label47;
                     }

                     String var8 = (String)var7.next();
                     if (var6.contains(var8, 10)) {
                        Util.ifElse(
                           BuiltInRegistries.STAT_TYPE.getOptional(new ResourceLocation(var8)),
                           var3x -> {
                              CompoundTag var4xx = var6.getCompound(var8);
   
                              for(String var6xx : var4xx.getAllKeys()) {
                                 if (var4xx.contains(var6xx, 99)) {
                                    Util.ifElse(
                                       this.getStat(var3x, var6xx),
                                       var3xx -> this.stats.put(var3xx, var4x.getInt(var6x)),
                                       () -> LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, var6x)
                                    );
                                 } else {
                                    LOGGER.warn(
                                       "Invalid statistic value in {}: Don't know what {} is for key {}", new Object[]{this.file, var4xx.get(var6xx), var6xx}
                                    );
                                 }
                              }
                           },
                           () -> LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, var8)
                        );
                     }
                  }
               }

               LOGGER.error("Unable to parse Stat data from {}", this.file);
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
      return Optional.ofNullable(ResourceLocation.tryParse(var2)).flatMap(var1.getRegistry()::getOptional).map(var1::get);
   }

   private static CompoundTag fromJson(JsonObject var0) {
      CompoundTag var1 = new CompoundTag();

      for(Entry var3 : var0.entrySet()) {
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
         ((JsonObject)var1.computeIfAbsent(var4.getType(), var0 -> new JsonObject())).addProperty(getKey(var4).toString(), var3.getIntValue());
      }

      JsonObject var5 = new JsonObject();

      for(Entry var8 : var1.entrySet()) {
         var5.add(BuiltInRegistries.STAT_TYPE.getKey((StatType<?>)var8.getKey()).toString(), (JsonElement)var8.getValue());
      }

      JsonObject var7 = new JsonObject();
      var7.add("stats", var5);
      var7.addProperty("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
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

      for(Stat var4 : this.getDirty()) {
         var2.put(var4, this.getValue(var4));
      }

      var1.connection.send(new ClientboundAwardStatsPacket(var2));
   }
}
