package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.world.level.block.Rotation;

public class TestFunction {
   private final String batchName;
   private final String testName;
   private final String structureName;
   private final boolean required;
   private final int maxAttempts;
   private final int requiredSuccesses;
   private final Consumer<GameTestHelper> function;
   private final int maxTicks;
   private final long setupTicks;
   private final Rotation rotation;

   public TestFunction(String var1, String var2, String var3, int var4, long var5, boolean var7, Consumer<GameTestHelper> var8) {
      this(var1, var2, var3, Rotation.NONE, var4, var5, var7, 1, 1, var8);
   }

   public TestFunction(String var1, String var2, String var3, Rotation var4, int var5, long var6, boolean var8, Consumer<GameTestHelper> var9) {
      this(var1, var2, var3, var4, var5, var6, var8, 1, 1, var9);
   }

   public TestFunction(
      String var1, String var2, String var3, Rotation var4, int var5, long var6, boolean var8, int var9, int var10, Consumer<GameTestHelper> var11
   ) {
      super();
      this.batchName = var1;
      this.testName = var2;
      this.structureName = var3;
      this.rotation = var4;
      this.maxTicks = var5;
      this.required = var8;
      this.requiredSuccesses = var9;
      this.maxAttempts = var10;
      this.function = var11;
      this.setupTicks = var6;
   }

   public void run(GameTestHelper var1) {
      this.function.accept(var1);
   }

   public String getTestName() {
      return this.testName;
   }

   public String getStructureName() {
      return this.structureName;
   }

   @Override
   public String toString() {
      return this.testName;
   }

   public int getMaxTicks() {
      return this.maxTicks;
   }

   public boolean isRequired() {
      return this.required;
   }

   public String getBatchName() {
      return this.batchName;
   }

   public long getSetupTicks() {
      return this.setupTicks;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public boolean isFlaky() {
      return this.maxAttempts > 1;
   }

   public int getMaxAttempts() {
      return this.maxAttempts;
   }

   public int getRequiredSuccesses() {
      return this.requiredSuccesses;
   }
}
