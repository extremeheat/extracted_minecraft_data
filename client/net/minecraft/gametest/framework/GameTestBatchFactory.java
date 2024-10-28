package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;

public class GameTestBatchFactory {
   private static final int MAX_TESTS_PER_BATCH = 50;

   public GameTestBatchFactory() {
      super();
   }

   public static Collection<GameTestBatch> fromTestFunction(Collection<TestFunction> var0, ServerLevel var1) {
      Map var2 = (Map)var0.stream().collect(Collectors.groupingBy(TestFunction::batchName));
      return var2.entrySet().stream().flatMap((var1x) -> {
         String var2 = (String)var1x.getKey();
         List var3 = (List)var1x.getValue();
         return Streams.mapWithIndex(Lists.partition(var3, 50).stream(), (var2x, var3x) -> {
            return toGameTestBatch(var2x.stream().map((var1x) -> {
               return toGameTestInfo(var1x, 0, var1);
            }).toList(), var2, var3x);
         });
      }).toList();
   }

   public static GameTestInfo toGameTestInfo(TestFunction var0, int var1, ServerLevel var2) {
      return new GameTestInfo(var0, StructureUtils.getRotationForRotationSteps(var1), var2, RetryOptions.noRetries());
   }

   public static GameTestRunner.GameTestBatcher fromGameTestInfo() {
      return (var0) -> {
         Map var1 = (Map)var0.stream().filter(Objects::nonNull).collect(Collectors.groupingBy((var0x) -> {
            return var0x.getTestFunction().batchName();
         }));
         return var1.entrySet().stream().flatMap((var0x) -> {
            String var1 = (String)var0x.getKey();
            List var2 = (List)var0x.getValue();
            return Streams.mapWithIndex(Lists.partition(var2, 50).stream(), (var1x, var2x) -> {
               return toGameTestBatch(List.copyOf(var1x), var1, var2x);
            });
         }).toList();
      };
   }

   private static GameTestBatch toGameTestBatch(List<GameTestInfo> var0, String var1, long var2) {
      Consumer var4 = GameTestRegistry.getBeforeBatchFunction(var1);
      Consumer var5 = GameTestRegistry.getAfterBatchFunction(var1);
      return new GameTestBatch(var1 + ":" + var2, var0, var4, var5);
   }
}
