package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public class GameTestBatch {
   private final String name;
   private final Collection testFunctions;
   @Nullable
   private final Consumer beforeBatchFunction;

   public GameTestBatch(String var1, Collection var2, @Nullable Consumer var3) {
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

   public Collection getTestFunctions() {
      return this.testFunctions;
   }

   public void runBeforeBatchFunction(ServerLevel var1) {
      if (this.beforeBatchFunction != null) {
         this.beforeBatchFunction.accept(var1);
      }

   }
}
