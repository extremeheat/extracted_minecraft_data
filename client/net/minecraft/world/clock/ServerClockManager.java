package net.minecraft.world.clock;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.timeline.Timeline;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ServerClockManager implements ClockManager {
   private final MinecraftServer server;
   private final Map<Holder<WorldClock>, ClockInstance> clocks = new HashMap();

   public ServerClockManager(final MinecraftServer server) {
      super();
      this.server = server;
      server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).listElements().forEach((definition) -> this.clocks.put(definition, new ClockInstance()));
      server.registryAccess().lookupOrThrow(Registries.TIMELINE).listElements().forEach((timeline) -> ((Timeline)timeline.value()).registerTimeMarkers(this::registerTimeMarker));
   }

   private void registerTimeMarker(final ResourceKey<ClockTimeMarker> timeMarkerId, final ClockTimeMarker timeMarker) {
      this.getInstance(timeMarker.clock()).timeMarkers.put(timeMarkerId, timeMarker);
   }

   public void loadFrom(final PackedClockStates states) {
      states.clocks().forEach((definition, state) -> {
         ClockInstance instance = this.getInstance(definition);
         instance.loadFrom(state);
      });
   }

   public PackedClockStates packState() {
      return new PackedClockStates(Util.mapValues(this.clocks, ClockInstance::packState));
   }

   public void tick() {
      boolean advanceTime = (Boolean)this.server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
      if (advanceTime) {
         this.clocks.values().forEach(ClockInstance::tick);
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
      this.modifyClock(clock, (instance) -> instance.totalTicks = totalTicks);
   }

   public boolean skipToTimeMarker(final Holder<WorldClock> clock, final ResourceKey<ClockTimeMarker> timeMarkerId) {
      MutableBoolean set = new MutableBoolean();
      this.modifyClock(clock, (instance) -> {
         ClockTimeMarker timeMarker = (ClockTimeMarker)instance.timeMarkers.get(timeMarkerId);
         if (timeMarker != null) {
            instance.totalTicks = timeMarker.getNextOccurenceAfter(instance.totalTicks);
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

   private void modifyClock(final Holder<WorldClock> clock, final Consumer<? super ClockInstance> action) {
      ClockInstance instance = this.getInstance(clock);
      action.accept(instance);
      Map<Holder<WorldClock>, ClockState> updates = Map.of(clock, instance.packNetworkState(this.server));
      this.server.getPlayerList().broadcastAll(new ClientboundSetTimePacket(this.getGameTime(), updates));
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

   private static class ClockInstance {
      private final Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> timeMarkers = new Reference2ObjectOpenHashMap();
      private long totalTicks;
      private boolean paused;

      private ClockInstance() {
         super();
      }

      public void loadFrom(final ClockState state) {
         this.totalTicks = state.totalTicks();
         this.paused = state.paused();
      }

      public void tick() {
         if (!this.paused) {
            ++this.totalTicks;
         }

      }

      public ClockState packState() {
         return new ClockState(this.totalTicks, this.paused);
      }

      public ClockState packNetworkState(final MinecraftServer server) {
         boolean advanceTime = (Boolean)server.getGlobalGameRules().get(GameRules.ADVANCE_TIME);
         return new ClockState(this.totalTicks, this.paused || !advanceTime);
      }
   }
}
