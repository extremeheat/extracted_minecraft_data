package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public class GameTestBatch {
   private final String name;
   private final Collection<TestFunction> testFunctions;
   @Nullable
   private final Consumer<ServerLevel> beforeBatchFunction;

   public GameTestBatch(String var1, Collection<TestFunction> var2, @Nullable Consumer<ServerLevel> var3) {
      super();
      if (var2.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
      } else {
         this.name = var1;
         this.testFunctions = var2;
         this.beforeBatchFunction = var3;
      }
   }

   public String getName() {
      return this.name;
   }

   public Collection<TestFunction> getTestFunctions() {
      return this.testFunctions;
   }

   public void runBeforeBatchFunction(ServerLevel var1) {
      if (this.beforeBatchFunction != null) {
         this.beforeBatchFunction.accept(var1);
      }

   }
}
