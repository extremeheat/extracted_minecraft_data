package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;

public record GameTestBatch(String name, Collection<GameTestInfo> gameTestInfos, Consumer<ServerLevel> beforeBatchFunction, Consumer<ServerLevel> afterBatchFunction) {
   public static final String DEFAULT_BATCH_NAME = "defaultBatch";

   public GameTestBatch(String name, Collection<GameTestInfo> gameTestInfos, Consumer<ServerLevel> beforeBatchFunction, Consumer<ServerLevel> afterBatchFunction) {
      super();
      if (gameTestInfos.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
      } else {
         this.name = name;
         this.gameTestInfos = gameTestInfos;
         this.beforeBatchFunction = beforeBatchFunction;
         this.afterBatchFunction = afterBatchFunction;
      }
   }

   public String name() {
      return this.name;
   }

   public Collection<GameTestInfo> gameTestInfos() {
      return this.gameTestInfos;
   }

   public Consumer<ServerLevel> beforeBatchFunction() {
      return this.beforeBatchFunction;
   }

   public Consumer<ServerLevel> afterBatchFunction() {
      return this.afterBatchFunction;
   }
}
