package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;

public class GameTestTicker {
   public static final GameTestTicker singleton = new GameTestTicker();
   private final Collection<GameTestInfo> testInfos = Lists.newCopyOnWriteArrayList();

   public GameTestTicker() {
      super();
   }

   public void add(GameTestInfo var1) {
      this.testInfos.add(var1);
   }

   public void clear() {
      this.testInfos.clear();
   }

   public void tick() {
      this.testInfos.forEach(GameTestInfo::tick);
      this.testInfos.removeIf(GameTestInfo::isDone);
   }
}
