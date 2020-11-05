package net.minecraft.gametest.framework;

import java.util.Iterator;
import java.util.List;

public class GameTestSequence {
   private final GameTestInfo parent;
   private final List<GameTestEvent> events;
   private long lastTick;

   public void tickAndContinue(long var1) {
      try {
         this.tick(var1);
      } catch (Exception var4) {
      }

   }

   public void tickAndFailIfNotComplete(long var1) {
      try {
         this.tick(var1);
      } catch (Exception var4) {
         this.parent.fail(var4);
      }

   }

   private void tick(long var1) {
      Iterator var3 = this.events.iterator();

      while(var3.hasNext()) {
         GameTestEvent var4 = (GameTestEvent)var3.next();
         var4.assertion.run();
         var3.remove();
         long var5 = var1 - this.lastTick;
         long var7 = this.lastTick;
         this.lastTick = var1;
         if (var4.expectedDelay != null && var4.expectedDelay != var5) {
            this.parent.fail(new GameTestAssertException("Succeeded in invalid tick: expected " + (var7 + var4.expectedDelay) + ", but current tick is " + var1));
            break;
         }
      }

   }
}
