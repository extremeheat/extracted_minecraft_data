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

   public TestFunction(String batchName, String testName, String structureName, Rotation rotation, int maxTicks, long setupTicks, boolean required, boolean manualOnly, int maxAttempts, int requiredSuccesses, boolean skyAccess, Consumer<GameTestHelper> function) {
      super();
      this.batchName = batchName;
      this.testName = testName;
      this.structureName = structureName;
      this.rotation = rotation;
      this.maxTicks = maxTicks;
      this.setupTicks = setupTicks;
      this.required = required;
      this.manualOnly = manualOnly;
      this.maxAttempts = maxAttempts;
      this.requiredSuccesses = requiredSuccesses;
      this.skyAccess = skyAccess;
      this.function = function;
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

   public String batchName() {
      return this.batchName;
   }

   public String testName() {
      return this.testName;
   }

   public String structureName() {
      return this.structureName;
   }

   public Rotation rotation() {
      return this.rotation;
   }

   public int maxTicks() {
      return this.maxTicks;
   }

   public long setupTicks() {
      return this.setupTicks;
   }

   public boolean required() {
      return this.required;
   }

   public boolean manualOnly() {
      return this.manualOnly;
   }

   public int maxAttempts() {
      return this.maxAttempts;
   }

   public int requiredSuccesses() {
      return this.requiredSuccesses;
   }

   public boolean skyAccess() {
      return this.skyAccess;
   }

   public Consumer<GameTestHelper> function() {
      return this.function;
   }
}
