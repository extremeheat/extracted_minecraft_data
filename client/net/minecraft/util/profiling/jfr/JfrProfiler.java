package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionReadEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionWriteEvent;
import net.minecraft.util.profiling.jfr.event.ClientFpsEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.StructureGenerationEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class JfrProfiler implements JvmProfiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ROOT_CATEGORY = "Minecraft";
   public static final String WORLD_GEN_CATEGORY = "World Generation";
   public static final String TICK_CATEGORY = "Ticking";
   public static final String NETWORK_CATEGORY = "Network";
   public static final String STORAGE_CATEGORY = "Storage";
   private static final List<Class<? extends Event>> CUSTOM_EVENTS = List.of(ChunkGenerationEvent.class, ChunkRegionReadEvent.class, ChunkRegionWriteEvent.class, PacketReceivedEvent.class, PacketSentEvent.class, NetworkSummaryEvent.class, ServerTickTimeEvent.class, ClientFpsEvent.class, StructureGenerationEvent.class, WorldLoadFinishedEvent.class);
   private static final String FLIGHT_RECORDER_CONFIG = "/flightrecorder-config.jfc";
   private static final DateTimeFormatter DATE_TIME_FORMATTER;
   private static final JfrProfiler INSTANCE;
   private @Nullable Recording recording;
   private int currentFPS;
   private float currentAverageTickTimeServer;
   private final Map<String, NetworkSummaryEvent.SumAggregation> networkTrafficByAddress = new ConcurrentHashMap();
   private final Runnable periodicClientFps = () -> (new ClientFpsEvent(this.currentFPS)).commit();
   private final Runnable periodicServerTickTime = () -> (new ServerTickTimeEvent(this.currentAverageTickTimeServer)).commit();
   private final Runnable periodicNetworkSummary = () -> {
      Iterator<NetworkSummaryEvent.SumAggregation> iterator = this.networkTrafficByAddress.values().iterator();

      while(iterator.hasNext()) {
         ((NetworkSummaryEvent.SumAggregation)iterator.next()).commitEvent();
         iterator.remove();
      }

   };

   private JfrProfiler() {
      super();
      CUSTOM_EVENTS.forEach(FlightRecorder::register);
      this.registerPeriodicEvents();
      FlightRecorder.addListener(new FlightRecorderListener() {
         {
            Objects.requireNonNull(JfrProfiler.this);
         }

         public void recordingStateChanged(final Recording rec) {
            switch (rec.getState()) {
               case STOPPED:
                  JfrProfiler.this.registerPeriodicEvents();
               case NEW:
               case DELAYED:
               case RUNNING:
               case CLOSED:
               default:
            }
         }
      });
   }

   private void registerPeriodicEvents() {
      addPeriodicEvent(ClientFpsEvent.class, this.periodicClientFps);
      addPeriodicEvent(ServerTickTimeEvent.class, this.periodicServerTickTime);
      addPeriodicEvent(NetworkSummaryEvent.class, this.periodicNetworkSummary);
   }

   private static void addPeriodicEvent(final Class<? extends Event> eventClass, final Runnable runnable) {
      FlightRecorder.removePeriodicEvent(runnable);
      FlightRecorder.addPeriodicEvent(eventClass, runnable);
   }

   public static JfrProfiler getInstance() {
      return INSTANCE;
   }

   public boolean start(final Environment environment) {
      URL resource = JfrProfiler.class.getResource("/flightrecorder-config.jfc");
      if (resource == null) {
         LOGGER.warn("Could not find default flight recorder config at {}", "/flightrecorder-config.jfc");
         return false;
      } else {
         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8));

            boolean var4;
            try {
               var4 = this.start(reader, environment);
            } catch (Throwable var7) {
               try {
                  reader.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            reader.close();
            return var4;
         } catch (IOException e) {
            LOGGER.warn("Failed to start flight recorder using configuration at {}", resource, e);
            return false;
         }
      }
   }

   public Path stop() {
      if (this.recording == null) {
         throw new IllegalStateException("Not currently profiling");
      } else {
         this.networkTrafficByAddress.clear();
         Path report = this.recording.getDestination();
         this.recording.stop();
         return report;
      }
   }

   public boolean isRunning() {
      return this.recording != null;
   }

   public boolean isAvailable() {
      return FlightRecorder.isAvailable();
   }

   private boolean start(final Reader configurationFile, final Environment environment) {
      if (this.isRunning()) {
         LOGGER.warn("Profiling already in progress");
         return false;
      } else {
         try {
            Configuration jfrConfig = Configuration.create(configurationFile);
            String startTimestamp = DATE_TIME_FORMATTER.format(Instant.now());
            this.recording = (Recording)Util.make(new Recording(jfrConfig), (self) -> {
               List var10000 = CUSTOM_EVENTS;
               Objects.requireNonNull(self);
               var10000.forEach(self::enable);
               self.setDumpOnExit(true);
               self.setToDisk(true);
               self.setName(String.format(Locale.ROOT, "%s-%s-%s", environment.getDescription(), SharedConstants.getCurrentVersion().name(), startTimestamp));
            });
            Path destination = Paths.get(String.format(Locale.ROOT, "debug/%s-%s.jfr", environment.getDescription(), startTimestamp));
            FileUtil.createDirectoriesSafe(destination.getParent());
            this.recording.setDestination(destination);
            this.recording.start();
            this.setupSummaryListener();
         } catch (ParseException | IOException exception) {
            LOGGER.warn("Failed to start jfr profiling", exception);
            return false;
         }

         LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", new Object[]{this.recording.getId(), this.recording.getName(), this.recording.getDestination()});
         return true;
      }
   }

   private void setupSummaryListener() {
      FlightRecorder.addListener(new FlightRecorderListener() {
         private final SummaryReporter summaryReporter;

         {
            Objects.requireNonNull(JfrProfiler.this);
            this.summaryReporter = new SummaryReporter(() -> JfrProfiler.this.recording = null);
         }

         public void recordingStateChanged(final Recording rec) {
            if (rec == JfrProfiler.this.recording) {
               switch (rec.getState()) {
                  case STOPPED:
                     this.summaryReporter.recordingStopped(rec.getDestination());
                     FlightRecorder.removeListener(this);
                  case NEW:
                  case DELAYED:
                  case RUNNING:
                  case CLOSED:
                  default:
               }
            }
         }
      });
   }

   public void onClientTick(final int fps) {
      if (ClientFpsEvent.TYPE.isEnabled()) {
         this.currentFPS = fps;
      }

   }

   public void onServerTick(final float currentAverageTickTime) {
      if (ServerTickTimeEvent.TYPE.isEnabled()) {
         this.currentAverageTickTimeServer = currentAverageTickTime;
      }

   }

   public void onPacketReceived(final ConnectionProtocol protocol, final PacketType<?> packetId, final SocketAddress remoteAddress, final int readableBytes) {
      if (PacketReceivedEvent.TYPE.isEnabled()) {
         (new PacketReceivedEvent(protocol.id(), packetId.flow().id(), packetId.id().toString(), remoteAddress, readableBytes)).commit();
      }

      if (NetworkSummaryEvent.TYPE.isEnabled()) {
         this.networkStatFor(remoteAddress).trackReceivedPacket(readableBytes);
      }

   }

   public void onPacketSent(final ConnectionProtocol protocol, final PacketType<?> packetId, final SocketAddress remoteAddress, final int writtenBytes) {
      if (PacketSentEvent.TYPE.isEnabled()) {
         (new PacketSentEvent(protocol.id(), packetId.flow().id(), packetId.id().toString(), remoteAddress, writtenBytes)).commit();
      }

      if (NetworkSummaryEvent.TYPE.isEnabled()) {
         this.networkStatFor(remoteAddress).trackSentPacket(writtenBytes);
      }

   }

   private NetworkSummaryEvent.SumAggregation networkStatFor(final SocketAddress remoteAddress) {
      return (NetworkSummaryEvent.SumAggregation)this.networkTrafficByAddress.computeIfAbsent(remoteAddress.toString(), NetworkSummaryEvent.SumAggregation::new);
   }

   public void onRegionFileRead(final RegionStorageInfo info, final ChunkPos pos, final RegionFileVersion version, final int readBytes) {
      if (ChunkRegionReadEvent.TYPE.isEnabled()) {
         (new ChunkRegionReadEvent(info, pos, version, readBytes)).commit();
      }

   }

   public void onRegionFileWrite(final RegionStorageInfo info, final ChunkPos pos, final RegionFileVersion version, final int writtenBytes) {
      if (ChunkRegionWriteEvent.TYPE.isEnabled()) {
         (new ChunkRegionWriteEvent(info, pos, version, writtenBytes)).commit();
      }

   }

   public @Nullable ProfiledDuration onWorldLoadedStarted() {
      if (!WorldLoadFinishedEvent.TYPE.isEnabled()) {
         return null;
      } else {
         WorldLoadFinishedEvent event = new WorldLoadFinishedEvent();
         event.begin();
         return (ignored) -> event.commit();
      }
   }

   public @Nullable ProfiledDuration onChunkGenerate(final ChunkPos pos, final ResourceKey<Level> dimension, final String name) {
      if (!ChunkGenerationEvent.TYPE.isEnabled()) {
         return null;
      } else {
         ChunkGenerationEvent event = new ChunkGenerationEvent(pos, dimension, name);
         event.begin();
         return (ignored) -> event.commit();
      }
   }

   public @Nullable ProfiledDuration onStructureGenerate(final ChunkPos sourceChunkPos, final ResourceKey<Level> dimension, final Holder<Structure> structure) {
      if (!StructureGenerationEvent.TYPE.isEnabled()) {
         return null;
      } else {
         StructureGenerationEvent event = new StructureGenerationEvent(sourceChunkPos, structure, dimension);
         event.begin();
         return (success) -> {
            event.success = success;
            event.commit();
         };
      }
   }

   static {
      DATE_TIME_FORMATTER = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd-HHmmss").toFormatter(Locale.ROOT).withZone(ZoneId.systemDefault());
      INSTANCE = new JfrProfiler();
   }
}
