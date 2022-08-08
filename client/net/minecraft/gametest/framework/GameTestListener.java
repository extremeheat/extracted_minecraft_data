package net.minecraft.gametest.framework;

public interface GameTestListener {
   void testStructureLoaded(GameTestInfo var1);

   void testPassed(GameTestInfo var1);

   void testFailed(GameTestInfo var1);
}
