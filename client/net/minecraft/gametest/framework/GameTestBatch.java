package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;

public record GameTestBatch(String name, Collection<GameTestInfo> gameTestInfos, Consumer<ServerLevel> beforeBatchFunction, Consumer<ServerLevel> afterBatchFunction) {
   public static final String DEFAULT_BATCH_NAME = "defaultBatch";

   public GameTestBatch(String var1, Collection<GameTestInfo> var2, Consumer<ServerLevel> var3, Consumer<ServerLevel> var4) {
      super();
      if (var2.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
      } else {
         this.name = var1;
         this.gameTestInfos = var2;
         this.beforeBatchFunction = var3;
         this.afterBatchFunction = var4;
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
