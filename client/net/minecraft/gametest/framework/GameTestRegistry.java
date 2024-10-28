package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

public class GameTestRegistry {
   private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.newArrayList();
   private static final Set<String> TEST_CLASS_NAMES = Sets.newHashSet();
   private static final Map<String, Consumer<ServerLevel>> BEFORE_BATCH_FUNCTIONS = Maps.newHashMap();
   private static final Map<String, Consumer<ServerLevel>> AFTER_BATCH_FUNCTIONS = Maps.newHashMap();
   private static final Set<TestFunction> LAST_FAILED_TESTS = Sets.newHashSet();

   public GameTestRegistry() {
      super();
   }

   public static void register(Class<?> var0) {
      Arrays.stream(var0.getDeclaredMethods()).sorted(Comparator.comparing(Method::getName)).forEach(GameTestRegistry::register);
   }

   public static void register(Method var0) {
      String var1 = var0.getDeclaringClass().getSimpleName();
      GameTest var2 = (GameTest)var0.getAnnotation(GameTest.class);
      if (var2 != null) {
         TEST_FUNCTIONS.add(turnMethodIntoTestFunction(var0));
         TEST_CLASS_NAMES.add(var1);
      }

      GameTestGenerator var3 = (GameTestGenerator)var0.getAnnotation(GameTestGenerator.class);
      if (var3 != null) {
         TEST_FUNCTIONS.addAll(useTestGeneratorMethod(var0));
         TEST_CLASS_NAMES.add(var1);
      }

      registerBatchFunction(var0, BeforeBatch.class, BeforeBatch::batch, BEFORE_BATCH_FUNCTIONS);
      registerBatchFunction(var0, AfterBatch.class, AfterBatch::batch, AFTER_BATCH_FUNCTIONS);
   }

   private static <T extends Annotation> void registerBatchFunction(Method var0, Class<T> var1, Function<T, String> var2, Map<String, Consumer<ServerLevel>> var3) {
      Annotation var4 = var0.getAnnotation(var1);
      if (var4 != null) {
         String var5 = (String)var2.apply(var4);
         Consumer var6 = (Consumer)var3.putIfAbsent(var5, turnMethodIntoConsumer(var0));
         if (var6 != null) {
            String var10002 = String.valueOf(var1);
            throw new RuntimeException("Hey, there should only be one " + var10002 + " method per batch. Batch '" + var5 + "' has more than one!");
         }
      }

   }

   public static Stream<TestFunction> getTestFunctionsForClassName(String var0) {
      return TEST_FUNCTIONS.stream().filter((var1) -> {
         return isTestFunctionPartOfClass(var1, var0);
      });
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

   public static Consumer<ServerLevel> getBeforeBatchFunction(String var0) {
      return (Consumer)BEFORE_BATCH_FUNCTIONS.getOrDefault(var0, (var0x) -> {
      });
   }

   public static Consumer<ServerLevel> getAfterBatchFunction(String var0) {
      return (Consumer)AFTER_BATCH_FUNCTIONS.getOrDefault(var0, (var0x) -> {
      });
   }

   public static Optional<TestFunction> findTestFunction(String var0) {
      return getAllTestFunctions().stream().filter((var1) -> {
         return var1.testName().equalsIgnoreCase(var0);
      }).findFirst();
   }

   public static TestFunction getTestFunction(String var0) {
      Optional var1 = findTestFunction(var0);
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Can't find the test function for " + var0);
      } else {
         return (TestFunction)var1.get();
      }
   }

   private static Collection<TestFunction> useTestGeneratorMethod(Method var0) {
      try {
         Object var1 = var0.getDeclaringClass().newInstance();
         return (Collection)var0.invoke(var1);
      } catch (ReflectiveOperationException var2) {
         throw new RuntimeException(var2);
      }
   }

   private static TestFunction turnMethodIntoTestFunction(Method var0) {
      GameTest var1 = (GameTest)var0.getAnnotation(GameTest.class);
      String var2 = var0.getDeclaringClass().getSimpleName();
      String var3 = var2.toLowerCase();
      String var4 = var3 + "." + var0.getName().toLowerCase();
      String var5 = var1.template().isEmpty() ? var4 : var3 + "." + var1.template();
      String var6 = var1.batch();
      Rotation var7 = StructureUtils.getRotationForRotationSteps(var1.rotationSteps());
      return new TestFunction(var6, var4, var5, var7, var1.timeoutTicks(), var1.setupTicks(), var1.required(), var1.manualOnly(), var1.requiredSuccesses(), var1.attempts(), var1.skyAccess(), turnMethodIntoConsumer(var0));
   }

   private static Consumer<?> turnMethodIntoConsumer(Method var0) {
      return (var1) -> {
         try {
            Object var2 = var0.getDeclaringClass().newInstance();
            var0.invoke(var2, var1);
         } catch (InvocationTargetException var3) {
            if (var3.getCause() instanceof RuntimeException) {
               throw (RuntimeException)var3.getCause();
            } else {
               throw new RuntimeException(var3.getCause());
            }
         } catch (ReflectiveOperationException var4) {
            throw new RuntimeException(var4);
         }
      };
   }

   private static boolean isTestFunctionPartOfClass(TestFunction var0, String var1) {
      return var0.testName().toLowerCase().startsWith(var1.toLowerCase() + ".");
   }

   public static Stream<TestFunction> getLastFailedTests() {
      return LAST_FAILED_TESTS.stream();
   }

   public static void rememberFailedTest(TestFunction var0) {
      LAST_FAILED_TESTS.add(var0);
   }

   public static void forgetFailedTests() {
      LAST_FAILED_TESTS.clear();
   }
}
