package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class EuclideanGameEventDispatcher implements GameEventDispatcher {
   private final List<GameEventListener> listeners = Lists.newArrayList();
   private final Set<GameEventListener> listenersToRemove = Sets.newHashSet();
   private final List<GameEventListener> listenersToAdd = Lists.newArrayList();
   private boolean processing;
   private final ServerLevel level;

   public EuclideanGameEventDispatcher(ServerLevel var1) {
      super();
      this.level = var1;
   }

   @Override
   public boolean isEmpty() {
      return this.listeners.isEmpty();
   }

   @Override
   public void register(GameEventListener var1) {
      if (this.processing) {
         this.listenersToAdd.add(var1);
      } else {
         this.listeners.add(var1);
      }

      DebugPackets.sendGameEventListenerInfo(this.level, var1);
   }

   @Override
   public void unregister(GameEventListener var1) {
      if (this.processing) {
         this.listenersToRemove.add(var1);
      } else {
         this.listeners.remove(var1);
      }
   }

   @Override
   public boolean walkListeners(GameEvent var1, Vec3 var2, GameEvent.Context var3, BiConsumer<GameEventListener, Vec3> var4) {
      this.processing = true;
      boolean var5 = false;

      try {
         Iterator var6 = this.listeners.iterator();

         while(var6.hasNext()) {
            GameEventListener var7 = (GameEventListener)var6.next();
            if (this.listenersToRemove.remove(var7)) {
               var6.remove();
            } else {
               Optional var8 = getPostableListenerPosition(this.level, var2, var7);
               if (var8.isPresent()) {
                  var4.accept(var7, (Vec3)var8.get());
                  var5 = true;
               }
            }
         }
      } finally {
         this.processing = false;
      }

      if (!this.listenersToAdd.isEmpty()) {
         this.listeners.addAll(this.listenersToAdd);
         this.listenersToAdd.clear();
      }

      if (!this.listenersToRemove.isEmpty()) {
         this.listeners.removeAll(this.listenersToRemove);
         this.listenersToRemove.clear();
      }

      return var5;
   }

   private static Optional<Vec3> getPostableListenerPosition(ServerLevel var0, Vec3 var1, GameEventListener var2) {
      Optional var3 = var2.getListenerSource().getPosition(var0);
      if (var3.isEmpty()) {
         return Optional.empty();
      } else {
         double var4 = ((Vec3)var3.get()).distanceToSqr(var1);
         int var6 = var2.getListenerRadius() * var2.getListenerRadius();
         return var4 > (double)var6 ? Optional.empty() : var3;
      }
   }
}
