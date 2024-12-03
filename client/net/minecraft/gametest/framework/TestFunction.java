package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.world.level.block.Rotation;

public record TestFunction(String batchName, String testName, String structureName, Rotation rotation, int maxTicks, long setupTicks, boolean required, boolean manualOnly, int maxAttempts, int requiredSuccesses, boolean skyAccess, Consumer<GameTestHelper> function) {
   public TestFunction(String var1, String var2, String var3, int var4, long var5, boolean var7, Consumer<GameTestHelper> var8) {
      this(var1, var2, var3, Rotation.NONE, var4, var5, var7, false, 1, 1, false, var8);
   }

   public TestFunction(String var1, String var2, String var3, Rotation var4, int var5, long var6, boolean var8, Consumer<GameTestHelper> var9) {
      this(var1, var2, var3, var4, var5, var6, var8, false, 1, 1, false, var9);
   }

   public TestFunction(String var1, String var2, String var3, Rotation var4, int var5, long var6, boolean var8, boolean var9, int var10, int var11, boolean var12, Consumer<GameTestHelper> var13) {
      super();
      this.batchName = var1;
      this.testName = var2;
      this.structureName = var3;
      this.rotation = var4;
      this.maxTicks = var5;
      this.setupTicks = var6;
      this.required = var8;
      this.manualOnly = var9;
      this.maxAttempts = var10;
      this.requiredSuccesses = var11;
      this.skyAccess = var12;
      this.function = var13;
   }

   public void run(GameTestHelper var1) {
      this.function.accept(var1);
   }

   public String toString() {
      return this.testName;
   }

   public boolean isFlaky() {
      return this.maxAttempts > 1;
   }
}
