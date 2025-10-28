package net.minecraft.stats;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec<Map<Stat<?>, Integer>> STATS_CODEC;
   private final Path file;
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

   public ServerStatsCounter(MinecraftServer var1, Path var2) {
      super();
      this.file = var2;
      if (Files.isRegularFile(var2, new LinkOption[0])) {
         try {
            BufferedReader var3 = Files.newBufferedReader(var2, StandardCharsets.UTF_8);

            try {
               JsonElement var4 = StrictJsonParser.parse((Reader)var3);
               this.parse(var1.getFixerUpper(), var4);
            } catch (Throwable var7) {
               if (var3 != null) {
                  try {
                     ((Reader)var3).close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var3 != null) {
               ((Reader)var3).close();
            }
         } catch (IOException var8) {
            LOGGER.error("Couldn't read statistics file {}", var2, var8);
         } catch (JsonParseException var9) {
            LOGGER.error("Couldn't parse statistics file {}", var2, var9);
         }
      }

   }

   public void save() {
      try {
         BufferedWriter var1 = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8);

         try {
            GSON.toJson(this.toJson(), GSON.newJsonWriter(var1));
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  ((Writer)var1).close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            ((Writer)var1).close();
         }
      } catch (JsonIOException | IOException var6) {
         LOGGER.error("Couldn't save stats to {}", this.file, var6);
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

   public void parse(DataFixer var1, JsonElement var2) {
      Dynamic var3 = new Dynamic(JsonOps.INSTANCE, var2);
      var3 = DataFixTypes.STATS.updateToCurrentVersion(var1, var3, NbtUtils.getDataVersion((Dynamic)var3, 1343));
      this.stats.putAll((Map)STATS_CODEC.parse(var3.get("stats").orElseEmptyMap()).resultOrPartial((var1x) -> LOGGER.error("Failed to parse statistics for {}: {}", this.file, var1x)).orElse(Map.of()));
   }

   protected JsonElement toJson() {
      JsonObject var1 = new JsonObject();
      var1.add("stats", (JsonElement)STATS_CODEC.encodeStart(JsonOps.INSTANCE, this.stats).getOrThrow());
      var1.addProperty("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
      return var1;
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
