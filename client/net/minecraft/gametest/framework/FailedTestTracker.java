package net.minecraft.gametest.framework;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;

public class FailedTestTracker {
   private static final Set<Holder.Reference<GameTestInstance>> LAST_FAILED_TESTS = Sets.newHashSet();

   public FailedTestTracker() {
      super();
   }

   public static Stream<Holder.Reference<GameTestInstance>> getLastFailedTests() {
      return LAST_FAILED_TESTS.stream();
   }

   public static void rememberFailedTest(Holder.Reference<GameTestInstance> var0) {
      LAST_FAILED_TESTS.add(var0);
   }

   public static void forgetFailedTests() {
      LAST_FAILED_TESTS.clear();
   }
}
