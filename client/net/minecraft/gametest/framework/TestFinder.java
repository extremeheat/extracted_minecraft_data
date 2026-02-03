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
   private static final TestInstanceFinder NO_FUNCTIONS = Stream::empty;
   private static final TestPosFinder NO_STRUCTURES = Stream::empty;
   private final TestInstanceFinder testInstanceFinder;
   private final TestPosFinder testPosFinder;
   private final CommandSourceStack source;

   public Stream<BlockPos> findTestPos() {
      return this.testPosFinder.findTestPos();
   }

   public static Builder builder() {
      return new Builder();
   }

   private TestFinder(final CommandSourceStack source, final TestInstanceFinder testInstanceFinder, final TestPosFinder testPosFinder) {
      super();
      this.source = source;
      this.testInstanceFinder = testInstanceFinder;
      this.testPosFinder = testPosFinder;
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
         this.testFinderWrapper = (f) -> f;
         this.structureBlockPosFinderWrapper = (f) -> f;
      }

      private Builder(final UnaryOperator<Supplier<Stream<Holder.Reference<GameTestInstance>>>> testFinderWrapper, final UnaryOperator<Supplier<Stream<BlockPos>>> structureBlockPosFinderWrapper) {
         super();
         this.testFinderWrapper = testFinderWrapper;
         this.structureBlockPosFinderWrapper = structureBlockPosFinderWrapper;
      }

      public Builder createMultipleCopies(final int amount) {
         return new Builder(createCopies(amount), createCopies(amount));
      }

      private static <Q> UnaryOperator<Supplier<Stream<Q>>> createCopies(final int amount) {
         return (source) -> {
            List<Q> copyList = new LinkedList();
            List<Q> sourceList = ((Stream)source.get()).toList();

            for(int i = 0; i < amount; ++i) {
               copyList.addAll(sourceList);
            }

            Objects.requireNonNull(copyList);
            return copyList::stream;
         };
      }

      private TestFinder build(final CommandSourceStack source, final TestInstanceFinder testInstanceFinder, final TestPosFinder testPosFinder) {
         UnaryOperator var10003 = this.testFinderWrapper;
         Objects.requireNonNull(testInstanceFinder);
         Supplier var4 = (Supplier)var10003.apply(testInstanceFinder::findTests);
         Objects.requireNonNull(var4);
         TestInstanceFinder var5 = var4::get;
         UnaryOperator var10004 = this.structureBlockPosFinderWrapper;
         Objects.requireNonNull(testPosFinder);
         Supplier var6 = (Supplier)var10004.apply(testPosFinder::findTestPos);
         Objects.requireNonNull(var6);
         return new TestFinder(source, var5, var6::get);
      }

      public TestFinder radius(final CommandContext<CommandSourceStack> sourceStack, final int radius) {
         CommandSourceStack source = (CommandSourceStack)sourceStack.getSource();
         BlockPos pos = BlockPos.containing(source.getPosition());
         return this.build(source, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findTestBlocks(pos, radius, source.getLevel()));
      }

      public TestFinder nearest(final CommandContext<CommandSourceStack> sourceStack) {
         CommandSourceStack source = (CommandSourceStack)sourceStack.getSource();
         BlockPos pos = BlockPos.containing(source.getPosition());
         return this.build(source, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findNearestTest(pos, 15, source.getLevel()).stream());
      }

      public TestFinder allNearby(final CommandContext<CommandSourceStack> sourceStack) {
         CommandSourceStack source = (CommandSourceStack)sourceStack.getSource();
         BlockPos pos = BlockPos.containing(source.getPosition());
         return this.build(source, TestFinder.NO_FUNCTIONS, () -> StructureUtils.findTestBlocks(pos, 250, source.getLevel()));
      }

      public TestFinder lookedAt(final CommandContext<CommandSourceStack> sourceStack) {
         CommandSourceStack source = (CommandSourceStack)sourceStack.getSource();
         return this.build(source, TestFinder.NO_FUNCTIONS, () -> StructureUtils.lookedAtTestPos(BlockPos.containing(source.getPosition()), source.getPlayer().getCamera(), source.getLevel()));
      }

      public TestFinder failedTests(final CommandContext<CommandSourceStack> sourceStack, final boolean onlyRequiredTests) {
         return this.build((CommandSourceStack)sourceStack.getSource(), () -> FailedTestTracker.getLastFailedTests().filter((test) -> !onlyRequiredTests || ((GameTestInstance)test.value()).required()), TestFinder.NO_STRUCTURES);
      }

      public TestFinder byResourceSelection(final CommandContext<CommandSourceStack> sourceStack, final Collection<Holder.Reference<GameTestInstance>> holders) {
         CommandSourceStack var10001 = (CommandSourceStack)sourceStack.getSource();
         Objects.requireNonNull(holders);
         return this.build(var10001, holders::stream, TestFinder.NO_STRUCTURES);
      }

      public TestFinder failedTests(final CommandContext<CommandSourceStack> sourceStack) {
         return this.failedTests(sourceStack, false);
      }
   }
}
