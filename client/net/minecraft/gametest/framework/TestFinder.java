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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

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

      public static ResourceKey<Level> levelForDimension(final TestEnvironmentDefinition<?> definition) {
         Objects.requireNonNull(definition);
         byte var2 = 0;
         ResourceKey var10000;
         //$FF: var2->value
         //0->net/minecraft/gametest/framework/TestEnvironmentDefinition$Dimension
         //1->net/minecraft/gametest/framework/TestEnvironmentDefinition$AllOf
         switch (definition.typeSwitch<invokedynamic>(definition, var2)) {
            case 0:
               TestEnvironmentDefinition.Dimension dimension = (TestEnvironmentDefinition.Dimension)definition;
               switch (dimension.type()) {
                  case OVERWORLD:
                     var10000 = Level.OVERWORLD;
                     return var10000;
                  case NETHER:
                     var10000 = Level.NETHER;
                     return var10000;
                  case END:
                     var10000 = Level.END;
                     return var10000;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }
            case 1:
               TestEnvironmentDefinition.AllOf allOf = (TestEnvironmentDefinition.AllOf)definition;
               var10000 = (ResourceKey)allOf.definitions().stream().map((h) -> levelForDimension((TestEnvironmentDefinition)h.value())).filter((key) -> key != Level.OVERWORLD).findFirst().orElse(Level.OVERWORLD);
               break;
            default:
               var10000 = Level.OVERWORLD;
         }

         return var10000;
      }

      private static CommandSourceStack resolveTargetSource(final CommandSourceStack source, final List<Holder.Reference<GameTestInstance>> tests) {
         ResourceKey<Level> requestedLevel = (ResourceKey)tests.stream().map((test) -> levelForDimension((TestEnvironmentDefinition)((GameTestInstance)test.value()).batch().value())).findFirst().orElse(source.getLevel().dimension());
         ServerLevel targetLevel = source.getServer().getLevel(requestedLevel);
         return targetLevel != null ? source.withLevel(targetLevel) : source;
      }

      private TestFinder build(final CommandSourceStack source, final TestInstanceFinder testInstanceFinder, final TestPosFinder testPosFinder) {
         List<Holder.Reference<GameTestInstance>> selectedTests = testInstanceFinder.findTests().toList();
         CommandSourceStack targetSource = resolveTargetSource(source, selectedTests);
         UnaryOperator var10003 = this.testFinderWrapper;
         Objects.requireNonNull(selectedTests);
         Supplier var6 = (Supplier)var10003.apply(selectedTests::stream);
         Objects.requireNonNull(var6);
         TestInstanceFinder var7 = var6::get;
         UnaryOperator var10004 = this.structureBlockPosFinderWrapper;
         Objects.requireNonNull(testPosFinder);
         Supplier var8 = (Supplier)var10004.apply(testPosFinder::findTestPos);
         Objects.requireNonNull(var8);
         return new TestFinder(targetSource, var7, var8::get);
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
