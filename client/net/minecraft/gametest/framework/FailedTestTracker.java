package net.minecraft.gametest.framework;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;

public class FailedTestTracker {
   private static final Set<Holder.Reference<GameTestInstance>> LAST_FAILED_TESTS = new HashSet();

   public FailedTestTracker() {
      super();
   }

   public static Stream<Holder.Reference<GameTestInstance>> getLastFailedTests() {
      return LAST_FAILED_TESTS.stream();
   }

   public static void rememberFailedTest(final Holder.Reference<GameTestInstance> test) {
      LAST_FAILED_TESTS.add(test);
   }

   public static void forgetFailedTests() {
      LAST_FAILED_TESTS.clear();
   }
}
