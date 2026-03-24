package net.minecraft.world.clock;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.timeline.Timeline;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ServerClockManager extends SavedData implements ClockManager {
   public static final SavedDataType<ServerClockManager> TYPE;
   private final PackedClockStates packedClockStates;
   private MinecraftServer server;
   private final Map<Holder<WorldClock>, ClockInstance> clocks = new HashMap();

   private ServerClockManager(final PackedClockStates packedClockStates) {
      super();
      this.packedClockStates = packedClockStates;
   }

   public void init(final MinecraftServer server) {
      this.server = server;
      server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).listElements().forEach((definition) -> this.clocks.put(definition, new ClockInstance()));
      server.registryAccess().lookupOrThrow(Registries.TIMELINE).listElements().forEach((timeline) -> ((Timeline)timeline.value()).registerTimeMarkers(this::registerTimeMarker));
      this.packedClockStates.clocks().forEach((definition, state) -> {
         ClockInstance instance = this.getInstance(definition);
         instance.loadFrom(state);
      });
   }

   private void registerTimeMarker(final ResourceKey<ClockTimeMarker> timeMarkerId, final ClockTimeMarker timeMarker) {
      this.getInstance(timeMarker.clock()).timeMarkers.put(timeMarkerId, timeMarker);
   }

   public PackedClockStates packState() {
      return new PackedClockStates(Util.mapValues(this.clocks, ClockInstance::packState));
   }

   public void tick() {
      boolean advanceTime = (Boolean)this.server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
      if (advanceTime) {
         this.clocks.values().forEach(ClockInstance::tick);
         this.setDirty();
      }

   }

   private ClockInstance getInstance(final Holder<WorldClock> definition) {
      ClockInstance instance = (ClockInstance)this.clocks.get(definition);
      if (instance == null) {
         throw new IllegalStateException("No clock initialized for definition: " + String.valueOf(definition));
      } else {
         return instance;
      }
   }

   public void setTotalTicks(final Holder<WorldClock> clock, final long totalTicks) {
      this.modifyClock(clock, (instance) -> {
         instance.totalTicks = totalTicks;
         instance.partialTick = 0.0F;
      });
   }

   public boolean moveToTimeMarker(final Holder<WorldClock> clock, final ResourceKey<ClockTimeMarker> timeMarkerId) {
      MutableBoolean set = new MutableBoolean();
      this.modifyClock(clock, (instance) -> {
         ClockTimeMarker timeMarker = (ClockTimeMarker)instance.timeMarkers.get(timeMarkerId);
         if (timeMarker != null) {
            instance.totalTicks = timeMarker.resolveTimeToMoveTo(instance.totalTicks);
            instance.partialTick = 0.0F;
            set.setTrue();
         }
      });
      return set.booleanValue();
   }

   public void addTicks(final Holder<WorldClock> clock, final int ticks) {
      this.modifyClock(clock, (instance) -> instance.totalTicks = Math.max(instance.totalTicks + (long)ticks, 0L));
   }

   public void setPaused(final Holder<WorldClock> clock, final boolean paused) {
      this.modifyClock(clock, (instance) -> instance.paused = paused);
   }

   public void setRate(final Holder<WorldClock> clock, final float rate) {
      this.modifyClock(clock, (instance) -> instance.rate = rate);
   }

   private void modifyClock(final Holder<WorldClock> clock, final Consumer<? super ClockInstance> action) {
      ClockInstance instance = this.getInstance(clock);
      action.accept(instance);
      Map<Holder<WorldClock>, ClockNetworkState> updates = Map.of(clock, instance.packNetworkState(this.server));
      this.server.getPlayerList().broadcastAll(new ClientboundSetTimePacket(this.getGameTime(), updates));
      this.setDirty();

      for(ServerLevel level : this.server.getAllLevels()) {
         level.environmentAttributes().invalidateTickCache();
      }

   }

   public long getTotalTicks(final Holder<WorldClock> definition) {
      return this.getInstance(definition).totalTicks;
   }

   public ClientboundSetTimePacket createFullSyncPacket() {
      return new ClientboundSetTimePacket(this.getGameTime(), Util.mapValues(this.clocks, (clock) -> clock.packNetworkState(this.server)));
   }

   private long getGameTime() {
      return this.server.overworld().getGameTime();
   }

   public boolean isAtTimeMarker(final Holder<WorldClock> clock, final ResourceKey<ClockTimeMarker> timeMarkerId) {
      ClockInstance clockInstance = this.getInstance(clock);
      ClockTimeMarker timeMarker = (ClockTimeMarker)clockInstance.timeMarkers.get(timeMarkerId);
      return timeMarker != null && timeMarker.occursAt(clockInstance.totalTicks);
   }

   public Stream<ResourceKey<ClockTimeMarker>> commandTimeMarkersForClock(final Holder<WorldClock> clock) {
      return this.getInstance(clock).timeMarkers.entrySet().stream().filter((entry) -> ((ClockTimeMarker)entry.getValue()).showInCommands()).map(Map.Entry::getKey);
   }

   static {
      TYPE = new SavedDataType<ServerClockManager>(Identifier.withDefaultNamespace("world_clocks"), () -> new ServerClockManager(PackedClockStates.EMPTY), PackedClockStates.CODEC.xmap(ServerClockManager::new, ServerClockManager::packState), DataFixTypes.SAVED_DATA_WORLD_CLOCKS);
   }

   private static class ClockInstance {
      private final Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> timeMarkers = new Reference2ObjectOpenHashMap();
      private long totalTicks;
      private float partialTick;
      private float rate = 1.0F;
      private boolean paused;

      private ClockInstance() {
         super();
      }

      public void loadFrom(final ClockState state) {
         this.totalTicks = state.totalTicks();
         this.partialTick = state.partialTick();
         this.rate = state.rate();
         this.paused = state.paused();
      }

      public void tick() {
         if (!this.paused) {
            this.partialTick += this.rate;
            int fullTicks = Mth.floor(this.partialTick);
            this.partialTick -= (float)fullTicks;
            this.totalTicks += (long)fullTicks;
         }

      }

      public ClockState packState() {
         return new ClockState(this.totalTicks, this.partialTick, this.rate, this.paused);
      }

      public ClockNetworkState packNetworkState(final MinecraftServer server) {
         boolean advanceTime = (Boolean)server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
         boolean paused = this.paused || !advanceTime;
         return new ClockNetworkState(this.totalTicks, this.partialTick, paused ? 0.0F : this.rate);
      }
   }
}
