package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

public class GameTestBatchFactory {
   private static final int MAX_TESTS_PER_BATCH = 50;
   public static final TestDecorator DIRECT = (test, level) -> Stream.of(new GameTestInfo(test, Rotation.NONE, level, RetryOptions.noRetries()));

   public GameTestBatchFactory() {
      super();
   }

   public static List<GameTestBatch> divideIntoBatches(final Collection<Holder.Reference<GameTestInstance>> allTests, final TestDecorator decorator, final MinecraftServer server) {
      Map<BatchKey, List<Holder.Reference<GameTestInstance>>> testsPerBatch = (Map)allTests.stream().collect(Collectors.groupingBy((instance) -> new BatchKey(((GameTestInstance)instance.value()).batch(), ((GameTestInstance)instance.value()).info().dimension())));
      return testsPerBatch.entrySet().stream().flatMap((e) -> {
         BatchKey key = (BatchKey)e.getKey();
         Holder<TestEnvironmentDefinition<?>> batchKey = key.environment();
         ResourceKey<Level> dimensionKey = key.dimension();
         ServerLevel level = server.getLevel(dimensionKey);
         if (level == null) {
            throw new IllegalStateException("Missing level for dimension: " + String.valueOf(dimensionKey.identifier()));
         } else {
            List<GameTestInfo> testsInBatch = ((List)e.getValue()).stream().flatMap((test) -> decorator.decorate(test, level)).toList();
            return Streams.mapWithIndex(Lists.partition(testsInBatch, 50).stream(), (tests, index) -> toGameTestBatch(tests, batchKey, (int)index, dimensionKey));
         }
      }).toList();
   }

   public static GameTestRunner.GameTestBatcher fromGameTestInfo() {
      return fromGameTestInfo(50);
   }

   public static GameTestRunner.GameTestBatcher fromGameTestInfo(final int maxTestsPerBatch) {
      return (gameTestInfos) -> {
         Map<BatchKey, List<GameTestInfo>> testsPerBatch = (Map)gameTestInfos.stream().filter(Objects::nonNull).collect(Collectors.groupingBy((info) -> new BatchKey(info.getTest().batch(), info.getTest().info().dimension())));
         return testsPerBatch.entrySet().stream().flatMap((e) -> {
            BatchKey key = (BatchKey)e.getKey();
            List<GameTestInfo> testsInBatch = (List)e.getValue();
            return Streams.mapWithIndex(Lists.partition(testsInBatch, maxTestsPerBatch).stream(), (tests, index) -> toGameTestBatch(List.copyOf(tests), key.environment(), (int)index, key.dimension()));
         }).toList();
      };
   }

   public static GameTestBatch toGameTestBatch(final Collection<GameTestInfo> tests, final Holder<TestEnvironmentDefinition<?>> batch, final int counter, final ResourceKey<Level> dimension) {
      return new GameTestBatch(counter, tests, batch, dimension);
   }

   private static record BatchKey(Holder<TestEnvironmentDefinition<?>> environment, ResourceKey<Level> dimension) {
      private BatchKey {
         super();
      }
   }

   @FunctionalInterface
   public interface TestDecorator {
      Stream<GameTestInfo> decorate(Holder.Reference<GameTestInstance> test, ServerLevel level);
   }
}
