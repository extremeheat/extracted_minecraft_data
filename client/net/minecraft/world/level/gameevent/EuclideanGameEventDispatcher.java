package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EuclideanGameEventDispatcher implements GameEventDispatcher {
   private final List<GameEventListener> listeners = Lists.newArrayList();
   private final Level level;

   public EuclideanGameEventDispatcher(Level var1) {
      super();
      this.level = var1;
   }

   public boolean isEmpty() {
      return this.listeners.isEmpty();
   }

   public void register(GameEventListener var1) {
      this.listeners.add(var1);
      DebugPackets.sendGameEventListenerInfo(this.level, var1);
   }

   public void unregister(GameEventListener var1) {
      this.listeners.remove(var1);
   }

   public void post(GameEvent var1, @Nullable Entity var2, BlockPos var3) {
      boolean var4 = false;
      Iterator var5 = this.listeners.iterator();

      while(var5.hasNext()) {
         GameEventListener var6 = (GameEventListener)var5.next();
         if (this.postToListener(this.level, var1, var2, var3, var6)) {
            var4 = true;
         }
      }

      if (var4) {
         DebugPackets.sendGameEventInfo(this.level, var1, var3);
      }

   }

   private boolean postToListener(Level var1, GameEvent var2, @Nullable Entity var3, BlockPos var4, GameEventListener var5) {
      Optional var6 = var5.getListenerSource().getPosition(var1);
      if (!var6.isPresent()) {
         return false;
      } else {
         double var7 = ((BlockPos)var6.get()).distSqr(var4, false);
         int var9 = var5.getListenerRadius() * var5.getListenerRadius();
         return var7 <= (double)var9 && var5.handleGameEvent(var1, var2, var3, var4);
      }
   }
}
