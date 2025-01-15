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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

public class GameTestBatchFactory {
   private static final int MAX_TESTS_PER_BATCH = 50;
   public static final TestDecorator DIRECT = (var0, var1) -> Stream.of(new GameTestInfo(var0, Rotation.NONE, var1, RetryOptions.noRetries()));

   public GameTestBatchFactory() {
      super();
   }

   public static List<GameTestBatch> divideIntoBatches(Collection<Holder.Reference<GameTestInstance>> var0, TestDecorator var1, ServerLevel var2) {
      Map var3 = (Map)var0.stream().flatMap((var2x) -> var1.decorate(var2x, var2)).collect(Collectors.groupingBy((var0x) -> var0x.getTest().batch()));
      return var3.entrySet().stream().flatMap((var0x) -> {
         Holder var1 = (Holder)var0x.getKey();
         List var2 = (List)var0x.getValue();
         return Streams.mapWithIndex(Lists.partition(var2, 50).stream(), (var1x, var2x) -> toGameTestBatch(var1x, var1, (int)var2x));
      }).toList();
   }

   public static GameTestRunner.GameTestBatcher fromGameTestInfo() {
      return fromGameTestInfo(50);
   }

   public static GameTestRunner.GameTestBatcher fromGameTestInfo(int var0) {
      return (var1) -> {
         Map var2 = (Map)var1.stream().filter(Objects::nonNull).collect(Collectors.groupingBy((var0x) -> var0x.getTest().batch()));
         return var2.entrySet().stream().flatMap((var1x) -> {
            Holder var2 = (Holder)var1x.getKey();
            List var3 = (List)var1x.getValue();
            return Streams.mapWithIndex(Lists.partition(var3, var0).stream(), (var1, var2x) -> toGameTestBatch(List.copyOf(var1), var2, (int)var2x));
         }).toList();
      };
   }

   public static GameTestBatch toGameTestBatch(Collection<GameTestInfo> var0, Holder<TestEnvironmentDefinition> var1, int var2) {
      return new GameTestBatch(var2, var0, var1);
   }

   @FunctionalInterface
   public interface TestDecorator {
      Stream<GameTestInfo> decorate(Holder.Reference<GameTestInstance> var1, ServerLevel var2);
   }
}
