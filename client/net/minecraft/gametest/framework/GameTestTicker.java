package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.slf4j.Logger;

public class GameTestTicker {
   public static final GameTestTicker SINGLETON = new GameTestTicker();
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Collection<GameTestInfo> testInfos = Lists.newCopyOnWriteArrayList();
   @Nullable
   private GameTestRunner runner;
   private State state;

   private GameTestTicker() {
      super();
      this.state = GameTestTicker.State.IDLE;
   }

   public void add(GameTestInfo var1) {
      this.testInfos.add(var1);
   }

   public void clear() {
      if (this.state != GameTestTicker.State.IDLE) {
         this.state = GameTestTicker.State.HALTING;
      } else {
         this.testInfos.clear();
         if (this.runner != null) {
            this.runner.stop();
            this.runner = null;
         }

      }
   }

   public void setRunner(GameTestRunner var1) {
      if (this.runner != null) {
         Util.logAndPauseIfInIde("The runner was already set in GameTestTicker");
      }

      this.runner = var1;
   }

   public void tick() {
      if (this.runner != null) {
         this.state = GameTestTicker.State.RUNNING;
         this.testInfos.forEach((var1x) -> var1x.tick(this.runner));
         this.testInfos.removeIf(GameTestInfo::isDone);
         State var1 = this.state;
         this.state = GameTestTicker.State.IDLE;
         if (var1 == GameTestTicker.State.HALTING) {
            this.clear();
         }

      }
   }

   static enum State {
      IDLE,
      RUNNING,
      HALTING;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{IDLE, RUNNING, HALTING};
      }
   }
}
