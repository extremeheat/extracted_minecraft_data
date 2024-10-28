package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionReadEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionWriteEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.slf4j.Logger;

public class JfrProfiler implements JvmProfiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ROOT_CATEGORY = "Minecraft";
   public static final String WORLD_GEN_CATEGORY = "World Generation";
   public static final String TICK_CATEGORY = "Ticking";
   public static final String NETWORK_CATEGORY = "Network";
   public static final String STORAGE_CATEGORY = "Storage";
   private static final List<Class<? extends Event>> CUSTOM_EVENTS = List.of(ChunkGenerationEvent.class, ChunkRegionReadEvent.class, ChunkRegionWriteEvent.class, PacketReceivedEvent.class, PacketSentEvent.class, NetworkSummaryEvent.class, ServerTickTimeEvent.class, WorldLoadFinishedEvent.class);
   private static final String FLIGHT_RECORDER_CONFIG = "/flightrecorder-config.jfc";
   private static final DateTimeFormatter DATE_TIME_FORMATTER = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd-HHmmss").toFormatter().withZone(ZoneId.systemDefault());
   private static final JfrProfiler INSTANCE = new JfrProfiler();
   @Nullable
   Recording recording;
   private float currentAverageTickTime;
   private final Map<String, NetworkSummaryEvent.SumAggregation> networkTrafficByAddress = new ConcurrentHashMap();

   private JfrProfiler() {
      super();
      CUSTOM_EVENTS.forEach(FlightRecorder::register);
      FlightRecorder.addPeriodicEvent(ServerTickTimeEvent.class, () -> {
         (new ServerTickTimeEvent(this.currentAverageTickTime)).commit();
      });
      FlightRecorder.addPeriodicEvent(NetworkSummaryEvent.class, () -> {
         Iterator var1 = this.networkTrafficByAddress.values().iterator();

         while(var1.hasNext()) {
            ((NetworkSummaryEvent.SumAggregation)var1.next()).commitEvent();
            var1.remove();
         }

      });
   }

   public static JfrProfiler getInstance() {
      return INSTANCE;
   }

   public boolean start(Environment var1) {
      URL var2 = JfrProfiler.class.getResource("/flightrecorder-config.jfc");
      if (var2 == null) {
         LOGGER.warn("Could not find default flight recorder config at {}", "/flightrecorder-config.jfc");
         return false;
      } else {
         try {
            BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.openStream()));

            boolean var4;
            try {
               var4 = this.start(var3, var1);
            } catch (Throwable var7) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            var3.close();
            return var4;
         } catch (IOException var8) {
            LOGGER.warn("Failed to start flight recorder using configuration at {}", var2, var8);
            return false;
         }
      }
   }

   public Path stop() {
      if (this.recording == null) {
         throw new IllegalStateException("Not currently profiling");
      } else {
         this.networkTrafficByAddress.clear();
         Path var1 = this.recording.getDestination();
         this.recording.stop();
         return var1;
      }
   }

   public boolean isRunning() {
      return this.recording != null;
   }

   public boolean isAvailable() {
      return FlightRecorder.isAvailable();
   }

   private boolean start(Reader var1, Environment var2) {
      if (this.isRunning()) {
         LOGGER.warn("Profiling already in progress");
         return false;
      } else {
         try {
            Configuration var3 = Configuration.create(var1);
            String var4 = DATE_TIME_FORMATTER.format(Instant.now());
            this.recording = (Recording)Util.make(new Recording(var3), (var2x) -> {
               List var10000 = CUSTOM_EVENTS;
               Objects.requireNonNull(var2x);
               var10000.forEach(var2x::enable);
               var2x.setDumpOnExit(true);
               var2x.setToDisk(true);
               var2x.setName(String.format(Locale.ROOT, "%s-%s-%s", var2.getDescription(), SharedConstants.getCurrentVersion().getName(), var4));
            });
            Path var5 = Paths.get(String.format(Locale.ROOT, "debug/%s-%s.jfr", var2.getDescription(), var4));
            FileUtil.createDirectoriesSafe(var5.getParent());
            this.recording.setDestination(var5);
            this.recording.start();
            this.setupSummaryListener();
         } catch (ParseException | IOException var6) {
            LOGGER.warn("Failed to start jfr profiling", var6);
            return false;
         }

         LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", new Object[]{this.recording.getId(), this.recording.getName(), this.recording.getDestination()});
         return true;
      }
   }

   private void setupSummaryListener() {
      FlightRecorder.addListener(new FlightRecorderListener() {
         final SummaryReporter summaryReporter = new SummaryReporter(() -> {
            JfrProfiler.this.recording = null;
         });

         public void recordingStateChanged(Recording var1) {
            if (var1 == JfrProfiler.this.recording && var1.getState() == RecordingState.STOPPED) {
               this.summaryReporter.recordingStopped(var1.getDestination());
               FlightRecorder.removeListener(this);
            }
         }
      });
   }

   public void onServerTick(float var1) {
      if (ServerTickTimeEvent.TYPE.isEnabled()) {
         this.currentAverageTickTime = var1;
      }

   }

   public void onPacketReceived(ConnectionProtocol var1, PacketType<?> var2, SocketAddress var3, int var4) {
      if (PacketReceivedEvent.TYPE.isEnabled()) {
         (new PacketReceivedEvent(var1.id(), var2.flow().id(), var2.id().toString(), var3, var4)).commit();
      }

      if (NetworkSummaryEvent.TYPE.isEnabled()) {
         this.networkStatFor(var3).trackReceivedPacket(var4);
      }

   }

   public void onPacketSent(ConnectionProtocol var1, PacketType<?> var2, SocketAddress var3, int var4) {
      if (PacketSentEvent.TYPE.isEnabled()) {
         (new PacketSentEvent(var1.id(), var2.flow().id(), var2.id().toString(), var3, var4)).commit();
      }

      if (NetworkSummaryEvent.TYPE.isEnabled()) {
         this.networkStatFor(var3).trackSentPacket(var4);
      }

   }

   private NetworkSummaryEvent.SumAggregation networkStatFor(SocketAddress var1) {
      return (NetworkSummaryEvent.SumAggregation)this.networkTrafficByAddress.computeIfAbsent(var1.toString(), NetworkSummaryEvent.SumAggregation::new);
   }

   public void onRegionFileRead(RegionStorageInfo var1, ChunkPos var2, RegionFileVersion var3, int var4) {
      if (ChunkRegionReadEvent.TYPE.isEnabled()) {
         (new ChunkRegionReadEvent(var1, var2, var3, var4)).commit();
      }

   }

   public void onRegionFileWrite(RegionStorageInfo var1, ChunkPos var2, RegionFileVersion var3, int var4) {
      if (ChunkRegionWriteEvent.TYPE.isEnabled()) {
         (new ChunkRegionWriteEvent(var1, var2, var3, var4)).commit();
      }

   }

   @Nullable
   public ProfiledDuration onWorldLoadedStarted() {
      if (!WorldLoadFinishedEvent.TYPE.isEnabled()) {
         return null;
      } else {
         WorldLoadFinishedEvent var1 = new WorldLoadFinishedEvent();
         var1.begin();
         Objects.requireNonNull(var1);
         return var1::commit;
      }
   }

   @Nullable
   public ProfiledDuration onChunkGenerate(ChunkPos var1, ResourceKey<Level> var2, String var3) {
      if (!ChunkGenerationEvent.TYPE.isEnabled()) {
         return null;
      } else {
         ChunkGenerationEvent var4 = new ChunkGenerationEvent(var1, var2, var3);
         var4.begin();
         Objects.requireNonNull(var4);
         return var4::commit;
      }
   }
}
