package net.minecraft.gametest.framework;

import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

public class TestFinder implements TestInstanceFinder, TestPosFinder {
   static final TestInstanceFinder NO_FUNCTIONS = Stream::empty;
   static final TestPosFinder NO_STRUCTURES = Stream::empty;
   private final TestInstanceFinder testInstanceFinder;
   private final TestPosFinder testPosFinder;
   private final CommandSourceStack source;

   public Stream<BlockPos> findTestPos() {
      return this.testPosFinder.findTestPos();
   }

   public static Builder builder() {
      return new Builder();
   }

   TestFinder(CommandSourceStack var1, TestInstanceFinder var2, TestPosFinder var3) {
      super();
      this.source = var1;
      this.testInstanceFinder = var2;
      this.testPosFinder = var3;
   }

   public CommandSourceStack source() {
      return this.source;
   }

   public Stream<Holder.Reference<GameTestInstance>> findTests() {
      return this.testInstanceFinder.findTests();
   }

   public static class Builder {
      private final UnaryOperator<Supplier<Stream<Holder.Reference<GameTestInstance>>>> testFinderWrapper;
      private final UnaryOperator<Supplier<Stream<BlockPos>>> structureBlockPosFinderWrapper;

      public Builder() {
         super();
         this.testFinderWrapper = (var0) -> var0;
         this.structureBlockPosFinderWrapper = (var0) -> var0;
      }

      private Builder(UnaryOperator<Supplier<Stream<Holder.Reference<GameTestInstance>>>> var1, UnaryOperator<Supplier<Stream<BlockPos>>> var2) {
         super();
         this.testFinderWrapper = var1;
         this.structureBlockPosFinderWrapper = var2;
      }

      public Builder createMultipleCopies(int var1) {
         return new Builder(createCopies(var1), createCopies(var1));
      }

      private static <Q> UnaryOperator<Supplier<Stream<Q>>> createCopies(int var0) {
         return (var1) -> {
            LinkedList var2 = new LinkedList();
            List var3 = ((Stream)var1.get()).toList();

            for(int var4 = 0; var4 < var0; ++var4) {
               var2.addAll(var3);
            }

            Objects.requireNonNull(var2);
            return var2::stream;
         };
      }

      private TestFinder build(CommandSourceStack var1, TestInstanceFinder var2, TestPosFinder var3) {
         UnaryOperator var10003 = this.testFinderWrapper;
         Objects.requireNonNull(var2);
         Supplier var4 = (Supplier)var10003.apply(var2::findTests);
         Objects.requireNonNull(var4);
         TestInstanceFinder var5 = var4::get;
         UnaryOperator var10004 = this.structureBlockPosFinderWrapper;
         Objects.requireNonNull(var3);
         Supplier var6 = (Supplier)var10004.apply(var3::findTestPos);
         Objects.requireNonNull(var6);
         return new TestFinder(var1, var5, var6::get);
      }

      public TestFinder radius(CommandContext<CommandSourceStack> var1, int var2) {
         CommandSourceStack var3 = (CommandSourceStack)var1.getSource();
         BlockPos var4 = BlockPos.containing(var3.getPosition());
         return this.build(var3, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findTestBlocks(var4, var2, var3.getLevel()));
      }

      public TestFinder nearest(CommandContext<CommandSourceStack> var1) {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         BlockPos var3 = BlockPos.containing(var2.getPosition());
         return this.build(var2, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findNearestTest(var3, 15, var2.getLevel()).stream());
      }

      public TestFinder allNearby(CommandContext<CommandSourceStack> var1) {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         BlockPos var3 = BlockPos.containing(var2.getPosition());
         return this.build(var2, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findTestBlocks(var3, 200, var2.getLevel()));
      }

      public TestFinder lookedAt(CommandContext<CommandSourceStack> var1) {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         return this.build(var2, TestFinder.NO_FUNCTIONS, () -> StructureUtils.lookedAtTestPos(BlockPos.containing(var2.getPosition()), var2.getPlayer().getCamera(), var2.getLevel()));
      }

      public TestFinder failedTests(CommandContext<CommandSourceStack> var1, boolean var2) {
         return this.build((CommandSourceStack)var1.getSource(), () -> FailedTestTracker.getLastFailedTests().filter((var1) -> !var2 || ((GameTestInstance)var1.value()).required()), TestFinder.NO_STRUCTURES);
      }

      public TestFinder byResourceSelection(CommandContext<CommandSourceStack> var1, Collection<Holder.Reference<GameTestInstance>> var2) {
         CommandSourceStack var10001 = (CommandSourceStack)var1.getSource();
         Objects.requireNonNull(var2);
         return this.build(var10001, var2::stream, TestFinder.NO_STRUCTURES);
      }

      public TestFinder failedTests(CommandContext<CommandSourceStack> var1) {
         return this.failedTests(var1, false);
      }
   }
}
