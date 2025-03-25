package net.minecraft.stats;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec<Map<Stat<?>, Integer>> STATS_CODEC;
   private final MinecraftServer server;
   private final File file;
   private final Set<Stat<?>> dirty = Sets.newHashSet();

   private static <T> Codec<Map<Stat<?>, Integer>> createTypedStatsCodec(StatType<T> var0) {
      Codec var1 = var0.getRegistry().byNameCodec();
      Objects.requireNonNull(var0);
      Codec var2 = var1.flatComapMap(var0::get, (var1x) -> var1x.getType() == var0 ? DataResult.success(var1x.getValue()) : DataResult.error(() -> {
            String var10000 = String.valueOf(var0);
            return "Expected type " + var10000 + ", but got " + String.valueOf(var1x.getType());
         }));
      return Codec.unboundedMap(var2, Codec.INT);
   }

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

         label35: {
            try {
               var3.setLenient(false);
               JsonElement var4 = Streams.parse(var3);
               if (!var4.isJsonNull()) {
                  Dynamic var5 = new Dynamic(JsonOps.INSTANCE, var4);
                  var5 = DataFixTypes.STATS.updateToCurrentVersion(var1, var5, NbtUtils.getDataVersion((Dynamic)var5, 1343));
                  this.stats.putAll((Map)STATS_CODEC.parse(var5.get("stats").orElseEmptyMap()).resultOrPartial((var1x) -> LOGGER.error("Failed to parse statistics for {}: {}", this.file, var1x)).orElse(Map.of()));
                  break label35;
               }

               LOGGER.error("Unable to parse Stat data from {}", this.file);
            } catch (Throwable var7) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            var3.close();
            return;
         }

         var3.close();
      } catch (IOException | JsonParseException var8) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, var8);
      }

   }

   protected String toJson() {
      JsonObject var1 = new JsonObject();
      var1.add("stats", (JsonElement)STATS_CODEC.encodeStart(JsonOps.INSTANCE, this.stats).getOrThrow());
      var1.addProperty("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
      return var1.toString();
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

   static {
      STATS_CODEC = Codec.dispatchedMap(BuiltInRegistries.STAT_TYPE.byNameCodec(), Util.memoize(ServerStatsCounter::createTypedStatsCodec)).xmap((var0) -> {
         HashMap var1 = new HashMap();
         var0.forEach((var1x, var2) -> var1.putAll(var2));
         return var1;
      }, (var0) -> (Map)var0.entrySet().stream().collect(Collectors.groupingBy((var0x) -> ((Stat)var0x.getKey()).getType(), Util.toMap())));
   }
}
