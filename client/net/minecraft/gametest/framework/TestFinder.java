package net.minecraft.gametest.framework;

import com.mojang.brigadier.context.CommandContext;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;

public class TestFinder<T> implements StructureBlockPosFinder, TestFunctionFinder {
   static final TestFunctionFinder NO_FUNCTIONS = Stream::empty;
   static final StructureBlockPosFinder NO_STRUCTURES = Stream::empty;
   private final TestFunctionFinder testFunctionFinder;
   private final StructureBlockPosFinder structureBlockPosFinder;
   private final CommandSourceStack source;
   private final Function<TestFinder<T>, T> contextProvider;

   @Override
   public Stream<BlockPos> findStructureBlockPos() {
      return this.structureBlockPosFinder.findStructureBlockPos();
   }

   TestFinder(CommandSourceStack var1, Function<TestFinder<T>, T> var2, TestFunctionFinder var3, StructureBlockPosFinder var4) {
      super();
      this.source = var1;
      this.contextProvider = var2;
      this.testFunctionFinder = var3;
      this.structureBlockPosFinder = var4;
   }

   T get() {
      return this.contextProvider.apply(this);
   }

   public CommandSourceStack source() {
      return this.source;
   }

   @Override
   public Stream<TestFunction> findTestFunctions() {
      return this.testFunctionFinder.findTestFunctions();
   }

   public static class Builder<T> {
      private final Function<TestFinder<T>, T> contextProvider;
      private final UnaryOperator<Supplier<Stream<TestFunction>>> testFunctionFinderWrapper;
      private final UnaryOperator<Supplier<Stream<BlockPos>>> structureBlockPosFinderWrapper;

      public Builder(Function<TestFinder<T>, T> var1) {
         super();
         this.contextProvider = var1;
         this.testFunctionFinderWrapper = var0 -> var0;
         this.structureBlockPosFinderWrapper = var0 -> var0;
      }

      private Builder(Function<TestFinder<T>, T> var1, UnaryOperator<Supplier<Stream<TestFunction>>> var2, UnaryOperator<Supplier<Stream<BlockPos>>> var3) {
         super();
         this.contextProvider = var1;
         this.testFunctionFinderWrapper = var2;
         this.structureBlockPosFinderWrapper = var3;
      }

      public TestFinder.Builder<T> createMultipleCopies(int var1) {
         return new TestFinder.Builder<>(this.contextProvider, createCopies(var1), createCopies(var1));
      }

      private static <Q> UnaryOperator<Supplier<Stream<Q>>> createCopies(int var0) {
         return var1 -> {
            LinkedList var2 = new LinkedList();
            List var3 = ((Stream)var1.get()).toList();

            for(int var4 = 0; var4 < var0; ++var4) {
               var2.addAll(var3);
            }

            return var2::stream;
         };
      }

      private T build(CommandSourceStack var1, TestFunctionFinder var2, StructureBlockPosFinder var3) {
         return new TestFinder<>(
               var1,
               this.contextProvider,
               this.testFunctionFinderWrapper.apply(var2::findTestFunctions)::get,
               this.structureBlockPosFinderWrapper.apply(var3::findStructureBlockPos)::get
            )
            .get();
      }

      public T radius(CommandContext<CommandSourceStack> var1, int var2) {
         CommandSourceStack var3 = (CommandSourceStack)var1.getSource();
         return this.build(var3, TestFinder.NO_FUNCTIONS, () -> StructureUtils.radiusStructureBlockPos(var2, var3.getPosition(), var3.getLevel()));
      }

      public T nearest(CommandContext<CommandSourceStack> var1) {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         BlockPos var3 = BlockPos.containing(var2.getPosition());
         return this.build(var2, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findNearestStructureBlock(var3, 15, var2.getLevel()).stream());
      }

      public T allNearby(CommandContext<CommandSourceStack> var1) {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         BlockPos var3 = BlockPos.containing(var2.getPosition());
         return this.build(var2, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findStructureBlocks(var3, 200, var2.getLevel()));
      }

      public T lookedAt(CommandContext<CommandSourceStack> var1) {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         return this.build(
            var2,
            TestFinder.NO_FUNCTIONS,
            () -> StructureUtils.lookedAtStructureBlockPos(BlockPos.containing(var2.getPosition()), var2.getPlayer().getCamera(), var2.getLevel())
         );
      }

      public T allTests(CommandContext<CommandSourceStack> var1) {
         return this.build(
            (CommandSourceStack)var1.getSource(),
            () -> GameTestRegistry.getAllTestFunctions().stream().filter(var0 -> !var0.manualOnly()),
            TestFinder.NO_STRUCTURES
         );
      }

      public T allTestsInClass(CommandContext<CommandSourceStack> var1, String var2) {
         return this.build(
            (CommandSourceStack)var1.getSource(),
            () -> GameTestRegistry.getTestFunctionsForClassName(var2).filter(var0x -> !var0x.manualOnly()),
            TestFinder.NO_STRUCTURES
         );
      }

      public T failedTests(CommandContext<CommandSourceStack> var1, boolean var2) {
         return this.build(
            (CommandSourceStack)var1.getSource(),
            () -> GameTestRegistry.getLastFailedTests().filter(var1x -> !var2 || var1x.required()),
            TestFinder.NO_STRUCTURES
         );
      }

      public T byArgument(CommandContext<CommandSourceStack> var1, String var2) {
         return this.build((CommandSourceStack)var1.getSource(), () -> Stream.of((T)TestFunctionArgument.getTestFunction(var1, var2)), TestFinder.NO_STRUCTURES);
      }

      public T failedTests(CommandContext<CommandSourceStack> var1) {
         return this.failedTests(var1, false);
      }
   }
}
