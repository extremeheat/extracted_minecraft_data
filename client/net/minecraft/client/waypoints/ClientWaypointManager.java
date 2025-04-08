package net.minecraft.client.waypoints;

import com.mojang.datafixers.util.Either;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.TrackedWaypointManager;
import net.minecraft.world.waypoints.Waypoint;

public class ClientWaypointManager implements TrackedWaypointManager {
   private final Map<Either<UUID, String>, TrackedWaypoint> waypoints = new ConcurrentHashMap();

   public ClientWaypointManager() {
      super();
   }

   public void trackWaypoint(TrackedWaypoint var1) {
      this.waypoints.put(var1.id(), var1);
   }

   public void updateWaypoint(TrackedWaypoint var1) {
      ((TrackedWaypoint)this.waypoints.get(var1.id())).update(var1);
   }

   public void untrackWaypoint(TrackedWaypoint var1) {
      this.waypoints.remove(var1.id());
   }

   public boolean hasWaypoints() {
      return !this.waypoints.isEmpty();
   }

   public void forEachWaypoint(Entity var1, Consumer<TrackedWaypoint> var2) {
      this.waypoints.values().stream().sorted(Comparator.comparingDouble((var1x) -> var1x.distanceSquared(var1)).reversed()).forEachOrdered(var2);
   }

   // $FF: synthetic method
   public void untrackWaypoint(final Waypoint var1) {
      this.untrackWaypoint((TrackedWaypoint)var1);
   }

   // $FF: synthetic method
   public void trackWaypoint(final Waypoint var1) {
      this.trackWaypoint((TrackedWaypoint)var1);
   }
}
