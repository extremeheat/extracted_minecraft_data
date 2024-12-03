package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TelemetryEventType {
   static final Map<String, TelemetryEventType> REGISTRY = new Object2ObjectLinkedOpenHashMap();
   public static final Codec<TelemetryEventType> CODEC;
   private static final List<TelemetryProperty<?>> GLOBAL_PROPERTIES;
   private static final List<TelemetryProperty<?>> WORLD_SESSION_PROPERTIES;
   public static final TelemetryEventType WORLD_LOADED;
   public static final TelemetryEventType PERFORMANCE_METRICS;
   public static final TelemetryEventType WORLD_LOAD_TIMES;
   public static final TelemetryEventType WORLD_UNLOADED;
   public static final TelemetryEventType ADVANCEMENT_MADE;
   public static final TelemetryEventType GAME_LOAD_TIMES;
   private final String id;
   private final String exportKey;
   private final List<TelemetryProperty<?>> properties;
   private final boolean isOptIn;
   private final MapCodec<TelemetryEventInstance> codec;

   TelemetryEventType(String var1, String var2, List<TelemetryProperty<?>> var3, boolean var4) {
      super();
      this.id = var1;
      this.exportKey = var2;
      this.properties = var3;
      this.isOptIn = var4;
      this.codec = TelemetryPropertyMap.createCodec(var3).xmap((var1x) -> new TelemetryEventInstance(this, var1x), TelemetryEventInstance::properties);
   }

   public static Builder builder(String var0, String var1) {
      return new Builder(var0, var1);
   }

   public String id() {
      return this.id;
   }

   public List<TelemetryProperty<?>> properties() {
      return this.properties;
   }

   public MapCodec<TelemetryEventInstance> codec() {
      return this.codec;
   }

   public boolean isOptIn() {
      return this.isOptIn;
   }

   public TelemetryEvent export(TelemetrySession var1, TelemetryPropertyMap var2) {
      TelemetryEvent var3 = var1.createNewEvent(this.exportKey);

      for(TelemetryProperty var5 : this.properties) {
         var5.export(var2, var3);
      }

      return var3;
   }

   public <T> boolean contains(TelemetryProperty<T> var1) {
      return this.properties.contains(var1);
   }

   public String toString() {
      return "TelemetryEventType[" + this.id + "]";
   }

   public MutableComponent title() {
      return this.makeTranslation("title");
   }

   public MutableComponent description() {
      return this.makeTranslation("description");
   }

   private MutableComponent makeTranslation(String var1) {
      return Component.translatable("telemetry.event." + this.id + "." + var1);
   }

   public static List<TelemetryEventType> values() {
      return List.copyOf(REGISTRY.values());
   }

   static {
      CODEC = Codec.STRING.comapFlatMap((var0) -> {
         TelemetryEventType var1 = (TelemetryEventType)REGISTRY.get(var0);
         return var1 != null ? DataResult.success(var1) : DataResult.error(() -> "No TelemetryEventType with key: '" + var0 + "'");
      }, TelemetryEventType::id);
      GLOBAL_PROPERTIES = List.of(TelemetryProperty.USER_ID, TelemetryProperty.CLIENT_ID, TelemetryProperty.MINECRAFT_SESSION_ID, TelemetryProperty.GAME_VERSION, TelemetryProperty.OPERATING_SYSTEM, TelemetryProperty.PLATFORM, TelemetryProperty.CLIENT_MODDED, TelemetryProperty.LAUNCHER_NAME, TelemetryProperty.EVENT_TIMESTAMP_UTC, TelemetryProperty.OPT_IN);
      WORLD_SESSION_PROPERTIES = Stream.concat(GLOBAL_PROPERTIES.stream(), Stream.of(TelemetryProperty.WORLD_SESSION_ID, TelemetryProperty.SERVER_MODDED, TelemetryProperty.SERVER_TYPE)).toList();
      WORLD_LOADED = builder("world_loaded", "WorldLoaded").defineAll(WORLD_SESSION_PROPERTIES).define(TelemetryProperty.GAME_MODE).define(TelemetryProperty.REALMS_MAP_CONTENT).register();
      PERFORMANCE_METRICS = builder("performance_metrics", "PerformanceMetrics").defineAll(WORLD_SESSION_PROPERTIES).define(TelemetryProperty.FRAME_RATE_SAMPLES).define(TelemetryProperty.RENDER_TIME_SAMPLES).define(TelemetryProperty.USED_MEMORY_SAMPLES).define(TelemetryProperty.NUMBER_OF_SAMPLES).define(TelemetryProperty.RENDER_DISTANCE).define(TelemetryProperty.DEDICATED_MEMORY_KB).optIn().register();
      WORLD_LOAD_TIMES = builder("world_load_times", "WorldLoadTimes").defineAll(WORLD_SESSION_PROPERTIES).define(TelemetryProperty.WORLD_LOAD_TIME_MS).define(TelemetryProperty.NEW_WORLD).optIn().register();
      WORLD_UNLOADED = builder("world_unloaded", "WorldUnloaded").defineAll(WORLD_SESSION_PROPERTIES).define(TelemetryProperty.SECONDS_SINCE_LOAD).define(TelemetryProperty.TICKS_SINCE_LOAD).register();
      ADVANCEMENT_MADE = builder("advancement_made", "AdvancementMade").defineAll(WORLD_SESSION_PROPERTIES).define(TelemetryProperty.ADVANCEMENT_ID).define(TelemetryProperty.ADVANCEMENT_GAME_TIME).optIn().register();
      GAME_LOAD_TIMES = builder("game_load_times", "GameLoadTimes").defineAll(GLOBAL_PROPERTIES).define(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS).define(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS).define(TelemetryProperty.LOAD_TIME_BOOTSTRAP_MS).define(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS).optIn().register();
   }

   public static class Builder {
      private final String id;
      private final String exportKey;
      private final List<TelemetryProperty<?>> properties = new ArrayList();
      private boolean isOptIn;

      Builder(String var1, String var2) {
         super();
         this.id = var1;
         this.exportKey = var2;
      }

      public Builder defineAll(List<TelemetryProperty<?>> var1) {
         this.properties.addAll(var1);
         return this;
      }

      public <T> Builder define(TelemetryProperty<T> var1) {
         this.properties.add(var1);
         return this;
      }

      public Builder optIn() {
         this.isOptIn = true;
         return this;
      }

      public TelemetryEventType register() {
         TelemetryEventType var1 = new TelemetryEventType(this.id, this.exportKey, List.copyOf(this.properties), this.isOptIn);
         if (TelemetryEventType.REGISTRY.putIfAbsent(this.id, var1) != null) {
            throw new IllegalStateException("Duplicate TelemetryEventType with key: '" + this.id + "'");
         } else {
            return var1;
         }
      }
   }
}
