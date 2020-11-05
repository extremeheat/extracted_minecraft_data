package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public class GameTestRegistry {
   private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.newArrayList();
   private static final Set<String> TEST_CLASS_NAMES = Sets.newHashSet();
   private static final Map<String, Consumer<ServerLevel>> BEFORE_BATCH_FUNCTIONS = Maps.newHashMap();
   private static final Map<String, Consumer<ServerLevel>> AFTER_BATCH_FUNCTIONS = Maps.newHashMap();
   private static final Collection<TestFunction> LAST_FAILED_TESTS = Sets.newHashSet();

   public static Collection<TestFunction> getTestFunctionsForClassName(String var0) {
      return (Collection)TEST_FUNCTIONS.stream().filter((var1) -> {
         return isTestFunctionPartOfClass(var1, var0);
      }).collect(Collectors.toList());
   }

   public static Collection<TestFunction> getAllTestFunctions() {
      return TEST_FUNCTIONS;
   }

   public static Collection<String> getAllTestClassNames() {
      return TEST_CLASS_NAMES;
   }

   public static boolean isTestClass(String var0) {
      return TEST_CLASS_NAMES.contains(var0);
   }

   @Nullable
   public static Consumer<ServerLevel> getBeforeBatchFunction(String var0) {
      return (Consumer)BEFORE_BATCH_FUNCTIONS.get(var0);
   }

   @Nullable
   public static Consumer<ServerLevel> getAfterBatchFunction(String var0) {
      return (Consumer)AFTER_BATCH_FUNCTIONS.get(var0);
   }

   public static Optional<TestFunction> findTestFunction(String var0) {
      return getAllTestFunctions().stream().filter((var1) -> {
         return var1.getTestName().equalsIgnoreCase(var0);
      }).findFirst();
   }

   public static TestFunction getTestFunction(String var0) {
      Optional var1 = findTestFunction(var0);
      if (!var1.isPresent()) {
         throw new IllegalArgumentException("Can't find the test function for " + var0);
      } else {
         return (TestFunction)var1.get();
      }
   }

   private static boolean isTestFunctionPartOfClass(TestFunction var0, String var1) {
      return var0.getTestName().toLowerCase().startsWith(var1.toLowerCase() + ".");
   }

   public static Collection<TestFunction> getLastFailedTests() {
      return LAST_FAILED_TESTS;
   }

   public static void rememberFailedTest(TestFunction var0) {
      LAST_FAILED_TESTS.add(var0);
   }

   public static void forgetFailedTests() {
      LAST_FAILED_TESTS.clear();
   }
}
