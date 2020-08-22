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

public class GameTestRegistry {
   private static final Collection testFunctions = Lists.newArrayList();
   private static final Set testClassNames = Sets.newHashSet();
   private static final Map beforeBatchFunctions = Maps.newHashMap();

   public static Collection getTestFunctionsForClassName(String var0) {
      return (Collection)testFunctions.stream().filter((var1) -> {
         return isTestFunctionPartOfClass(var1, var0);
      }).collect(Collectors.toList());
   }

   public static Collection getAllTestFunctions() {
      return testFunctions;
   }

   public static Collection getAllTestClassNames() {
      return testClassNames;
   }

   public static boolean isTestClass(String var0) {
      return testClassNames.contains(var0);
   }

   @Nullable
   public static Consumer getBeforeBatchFunction(String var0) {
      return (Consumer)beforeBatchFunctions.get(var0);
   }

   public static Optional findTestFunction(String var0) {
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
}
