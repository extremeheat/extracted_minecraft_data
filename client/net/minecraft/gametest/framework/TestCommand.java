package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceSelectorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.InCommandFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableInt;

public class TestCommand {
   public static final int TEST_NEARBY_SEARCH_RADIUS = 15;
   public static final int TEST_FULL_SEARCH_RADIUS = 250;
   public static final int VERIFY_TEST_GRID_AXIS_SIZE = 10;
   public static final int VERIFY_TEST_BATCH_SIZE = 100;
   private static final int DEFAULT_CLEAR_RADIUS = 250;
   private static final int MAX_CLEAR_RADIUS = 1024;
   private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
   private static final int SHOW_POS_DURATION_MS = 10000;
   private static final int DEFAULT_X_SIZE = 5;
   private static final int DEFAULT_Y_SIZE = 5;
   private static final int DEFAULT_Z_SIZE = 5;
   private static final SimpleCommandExceptionType CLEAR_NO_TESTS = new SimpleCommandExceptionType(Component.translatable("commands.test.clear.error.no_tests"));
   private static final SimpleCommandExceptionType RESET_NO_TESTS = new SimpleCommandExceptionType(Component.translatable("commands.test.reset.error.no_tests"));
   private static final SimpleCommandExceptionType TEST_INSTANCE_COULD_NOT_BE_FOUND = new SimpleCommandExceptionType(Component.translatable("commands.test.error.test_instance_not_found"));
   private static final SimpleCommandExceptionType NO_STRUCTURES_TO_EXPORT = new SimpleCommandExceptionType(Component.literal("Could not find any structures to export"));
   private static final SimpleCommandExceptionType NO_TEST_INSTANCES = new SimpleCommandExceptionType(Component.translatable("commands.test.error.no_test_instances"));
   private static final Dynamic3CommandExceptionType NO_TEST_CONTAINING = new Dynamic3CommandExceptionType((var0, var1, var2) -> Component.translatableEscape("commands.test.error.no_test_containing_pos", var0, var1, var2));
   private static final DynamicCommandExceptionType TOO_LARGE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.test.error.too_large", var0));

   public TestCommand() {
      super();
   }

   private static int reset(TestFinder var0) throws CommandSyntaxException {
      stopTests();
      int var1 = toGameTestInfos(var0.source(), RetryOptions.noRetries(), var0).map((var1x) -> resetGameTestInfo(var0.source(), var1x)).toList().size();
      if (var1 == 0) {
         throw CLEAR_NO_TESTS.create();
      } else {
         var0.source().sendSuccess(() -> Component.translatable("commands.test.reset.success", var1), true);
         return var1;
      }
   }

   private static int clear(TestFinder var0) throws CommandSyntaxException {
      stopTests();
      CommandSourceStack var1 = var0.source();
      ServerLevel var2 = var1.getLevel();
      GameTestRunner.clearMarkers(var2);
      List var3 = var0.findTestPos().flatMap((var1x) -> var2.getBlockEntity(var1x, BlockEntityType.TEST_INSTANCE_BLOCK).stream()).toList();

      for(TestInstanceBlockEntity var5 : var3) {
         StructureUtils.clearSpaceForStructure(var5.getStructureBoundingBox(), var2);
         var5.removeBarriers();
         var2.destroyBlock(var5.getBlockPos(), false);
      }

      if (var3.isEmpty()) {
         throw CLEAR_NO_TESTS.create();
      } else {
         var1.sendSuccess(() -> Component.translatable("commands.test.clear.success", var3.size()), true);
         return var3.size();
      }
   }

   private static int export(TestFinder var0) throws CommandSyntaxException {
      CommandSourceStack var1 = var0.source();
      ServerLevel var2 = var1.getLevel();
      int var3 = 0;
      boolean var4 = true;

      for(Iterator var5 = var0.findTestPos().iterator(); var5.hasNext(); ++var3) {
         BlockPos var6 = (BlockPos)var5.next();
         BlockEntity var8 = var2.getBlockEntity(var6);
         if (!(var8 instanceof TestInstanceBlockEntity)) {
            throw TEST_INSTANCE_COULD_NOT_BE_FOUND.create();
         }

         TestInstanceBlockEntity var7 = (TestInstanceBlockEntity)var8;
         Objects.requireNonNull(var1);
         if (!var7.exportTest(var1::sendSystemMessage)) {
            var4 = false;
         }
      }

      if (var3 == 0) {
         throw NO_STRUCTURES_TO_EXPORT.create();
      } else {
         String var9 = "Exported " + var3 + " structures";
         var0.source().sendSuccess(() -> Component.literal(var9), true);
         return var4 ? 0 : 1;
      }
   }

   private static int verify(TestFinder var0) {
      stopTests();
      CommandSourceStack var1 = var0.source();
      ServerLevel var2 = var1.getLevel();
      BlockPos var3 = createTestPositionAround(var1);
      List var4 = Stream.concat(toGameTestInfos(var1, RetryOptions.noRetries(), var0), toGameTestInfo(var1, RetryOptions.noRetries(), var0, 0)).toList();
      GameTestRunner.clearMarkers(var2);
      FailedTestTracker.forgetFailedTests();
      ArrayList var5 = new ArrayList();

      for(GameTestInfo var7 : var4) {
         for(Rotation var11 : Rotation.values()) {
            ArrayList var12 = new ArrayList();

            for(int var13 = 0; var13 < 100; ++var13) {
               GameTestInfo var14 = new GameTestInfo(var7.getTestHolder(), var11, var2, new RetryOptions(1, true));
               var14.setTestBlockPos(var7.getTestBlockPos());
               var12.add(var14);
            }

            GameTestBatch var17 = GameTestBatchFactory.toGameTestBatch(var12, var7.getTest().batch(), var11.ordinal());
            var5.add(var17);
         }
      }

      StructureGridSpawner var15 = new StructureGridSpawner(var3, 10, true);
      GameTestRunner var16 = GameTestRunner.Builder.fromBatches(var5, var2).batcher(GameTestBatchFactory.fromGameTestInfo(100)).newStructureSpawner(var15).existingStructureSpawner(var15).haltOnError().clearBetweenBatches().build();
      return trackAndStartRunner(var1, var16);
   }

   private static int run(TestFinder var0, RetryOptions var1, int var2, int var3) {
      stopTests();
      CommandSourceStack var4 = var0.source();
      ServerLevel var5 = var4.getLevel();
      BlockPos var6 = createTestPositionAround(var4);
      List var7 = Stream.concat(toGameTestInfos(var4, var1, var0), toGameTestInfo(var4, var1, var0, var2)).toList();
      if (var7.isEmpty()) {
         var4.sendSuccess(() -> Component.translatable("commands.test.no_tests"), false);
         return 0;
      } else {
         GameTestRunner.clearMarkers(var5);
         FailedTestTracker.forgetFailedTests();
         var4.sendSuccess(() -> Component.translatable("commands.test.run.running", var7.size()), false);
         GameTestRunner var8 = GameTestRunner.Builder.fromInfo(var7, var5).newStructureSpawner(new StructureGridSpawner(var6, var3, false)).build();
         return trackAndStartRunner(var4, var8);
      }
   }

   private static int locate(TestFinder var0) throws CommandSyntaxException {
      var0.source().sendSystemMessage(Component.translatable("commands.test.locate.started"));
      MutableInt var1 = new MutableInt(0);
      BlockPos var2 = BlockPos.containing(var0.source().getPosition());
      var0.findTestPos().forEach((var3x) -> {
         BlockEntity var5 = var0.source().getLevel().getBlockEntity(var3x);
         if (var5 instanceof TestInstanceBlockEntity var4) {
            Direction var13 = var4.getRotation().rotate(Direction.NORTH);
            BlockPos var6 = var4.getBlockPos().relative((Direction)var13, 2);
            int var7 = (int)var13.getOpposite().toYRot();
            String var8 = String.format(Locale.ROOT, "/tp @s %d %d %d %d 0", var6.getX(), var6.getY(), var6.getZ(), var7);
            int var9 = var2.getX() - var3x.getX();
            int var10 = var2.getZ() - var3x.getZ();
            int var11 = Mth.floor(Mth.sqrt((float)(var9 * var9 + var10 * var10)));
            MutableComponent var12 = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", var3x.getX(), var3x.getY(), var3x.getZ())).withStyle((UnaryOperator)((var1x) -> var1x.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent.SuggestCommand(var8)).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip")))));
            var0.source().sendSuccess(() -> Component.translatable("commands.test.locate.found", var12, var11), false);
            var1.increment();
         }
      });
      int var3 = var1.intValue();
      if (var3 == 0) {
         throw NO_TEST_INSTANCES.create();
      } else {
         var0.source().sendSuccess(() -> Component.translatable("commands.test.locate.done", var3), true);
         return var3;
      }
   }

   private static ArgumentBuilder<CommandSourceStack, ?> runWithRetryOptions(ArgumentBuilder<CommandSourceStack, ?> var0, InCommandFunction<CommandContext<CommandSourceStack>, TestFinder> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2) {
      return var0.executes((var1x) -> run((TestFinder)var1.apply(var1x), RetryOptions.noRetries(), 0, 8)).then(((RequiredArgumentBuilder)Commands.argument("numberOfTimes", IntegerArgumentType.integer(0)).executes((var1x) -> run((TestFinder)var1.apply(var1x), new RetryOptions(IntegerArgumentType.getInteger(var1x, "numberOfTimes"), false), 0, 8))).then((ArgumentBuilder)var2.apply(Commands.argument("untilFailed", BoolArgumentType.bool()).executes((var1x) -> run((TestFinder)var1.apply(var1x), new RetryOptions(IntegerArgumentType.getInteger(var1x, "numberOfTimes"), BoolArgumentType.getBool(var1x, "untilFailed")), 0, 8)))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> runWithRetryOptions(ArgumentBuilder<CommandSourceStack, ?> var0, InCommandFunction<CommandContext<CommandSourceStack>, TestFinder> var1) {
      return runWithRetryOptions(var0, var1, (var0x) -> var0x);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> runWithRetryOptionsAndBuildInfo(ArgumentBuilder<CommandSourceStack, ?> var0, InCommandFunction<CommandContext<CommandSourceStack>, TestFinder> var1) {
      return runWithRetryOptions(var0, var1, (var1x) -> var1x.then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((var1xx) -> run((TestFinder)var1.apply(var1xx), new RetryOptions(IntegerArgumentType.getInteger(var1xx, "numberOfTimes"), BoolArgumentType.getBool(var1xx, "untilFailed")), IntegerArgumentType.getInteger(var1xx, "rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((var1xx) -> run((TestFinder)var1.apply(var1xx), new RetryOptions(IntegerArgumentType.getInteger(var1xx, "numberOfTimes"), BoolArgumentType.getBool(var1xx, "untilFailed")), IntegerArgumentType.getInteger(var1xx, "rotationSteps"), IntegerArgumentType.getInteger(var1xx, "testsPerRow"))))));
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      ArgumentBuilder var2 = runWithRetryOptionsAndBuildInfo(Commands.argument("onlyRequiredTests", BoolArgumentType.bool()), (var0x) -> TestFinder.builder().failedTests(var0x, BoolArgumentType.getBool(var0x, "onlyRequiredTests")));
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("test").requires(Commands.hasPermission(2))).then(Commands.literal("run").then(runWithRetryOptionsAndBuildInfo(Commands.argument("tests", ResourceSelectorArgument.resourceSelector(var1, Registries.TEST_INSTANCE)), (var0x) -> TestFinder.builder().byResourceSelection(var0x, ResourceSelectorArgument.getSelectedResources(var0x, "tests")))))).then(Commands.literal("runmultiple").then(((RequiredArgumentBuilder)Commands.argument("tests", ResourceSelectorArgument.resourceSelector(var1, Registries.TEST_INSTANCE)).executes((var0x) -> run(TestFinder.builder().byResourceSelection(var0x, ResourceSelectorArgument.getSelectedResources(var0x, "tests")), RetryOptions.noRetries(), 0, 8))).then(Commands.argument("amount", IntegerArgumentType.integer()).executes((var0x) -> run(TestFinder.builder().createMultipleCopies(IntegerArgumentType.getInteger(var0x, "amount")).byResourceSelection(var0x, ResourceSelectorArgument.getSelectedResources(var0x, "tests")), RetryOptions.noRetries(), 0, 8)))));
      LiteralArgumentBuilder var10001 = Commands.literal("runthese");
      TestFinder.Builder var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      var10000 = (LiteralArgumentBuilder)var10000.then(runWithRetryOptions(var10001, var10002::allNearby));
      var10001 = Commands.literal("runclosest");
      var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      var10000 = (LiteralArgumentBuilder)var10000.then(runWithRetryOptions(var10001, var10002::nearest));
      var10001 = Commands.literal("runthat");
      var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      var10000 = (LiteralArgumentBuilder)var10000.then(runWithRetryOptions(var10001, var10002::lookedAt));
      ArgumentBuilder var9 = Commands.literal("runfailed").then(var2);
      var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      LiteralArgumentBuilder var3 = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var10000.then(runWithRetryOptionsAndBuildInfo(var9, var10002::failedTests))).then(Commands.literal("verify").then(Commands.argument("tests", ResourceSelectorArgument.resourceSelector(var1, Registries.TEST_INSTANCE)).executes((var0x) -> verify(TestFinder.builder().byResourceSelection(var0x, ResourceSelectorArgument.getSelectedResources(var0x, "tests"))))))).then(Commands.literal("locate").then(Commands.argument("tests", ResourceSelectorArgument.resourceSelector(var1, Registries.TEST_INSTANCE)).executes((var0x) -> locate(TestFinder.builder().byResourceSelection(var0x, ResourceSelectorArgument.getSelectedResources(var0x, "tests"))))))).then(Commands.literal("resetclosest").executes((var0x) -> reset(TestFinder.builder().nearest(var0x))))).then(Commands.literal("resetthese").executes((var0x) -> reset(TestFinder.builder().allNearby(var0x))))).then(Commands.literal("resetthat").executes((var0x) -> reset(TestFinder.builder().lookedAt(var0x))))).then(Commands.literal("clearthat").executes((var0x) -> clear(TestFinder.builder().lookedAt(var0x))))).then(Commands.literal("clearthese").executes((var0x) -> clear(TestFinder.builder().allNearby(var0x))))).then(((LiteralArgumentBuilder)Commands.literal("clearall").executes((var0x) -> clear(TestFinder.builder().radius(var0x, 250)))).then(Commands.argument("radius", IntegerArgumentType.integer()).executes((var0x) -> clear(TestFinder.builder().radius(var0x, Mth.clamp(IntegerArgumentType.getInteger(var0x, "radius"), 0, 1024))))))).then(Commands.literal("stop").executes((var0x) -> stopTests()))).then(((LiteralArgumentBuilder)Commands.literal("pos").executes((var0x) -> showPos((CommandSourceStack)var0x.getSource(), "pos"))).then(Commands.argument("var", StringArgumentType.word()).executes((var0x) -> showPos((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "var")))))).then(Commands.literal("create").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(TestCommand::suggestTestFunction).executes((var0x) -> createNewStructure((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"), 5, 5, 5))).then(((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer()).executes((var0x) -> createNewStructure((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"), IntegerArgumentType.getInteger(var0x, "width"), IntegerArgumentType.getInteger(var0x, "width"), IntegerArgumentType.getInteger(var0x, "width")))).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes((var0x) -> createNewStructure((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"), IntegerArgumentType.getInteger(var0x, "width"), IntegerArgumentType.getInteger(var0x, "height"), IntegerArgumentType.getInteger(var0x, "depth"))))))));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         var3 = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var3.then(Commands.literal("export").then(Commands.argument("test", ResourceArgument.resource(var1, Registries.TEST_INSTANCE)).executes((var0x) -> exportTestStructure((CommandSourceStack)var0x.getSource(), ResourceArgument.getResource(var0x, "test", Registries.TEST_INSTANCE)))))).then(Commands.literal("exportclosest").executes((var0x) -> export(TestFinder.builder().nearest(var0x))))).then(Commands.literal("exportthese").executes((var0x) -> export(TestFinder.builder().allNearby(var0x))))).then(Commands.literal("exportthat").executes((var0x) -> export(TestFinder.builder().lookedAt(var0x))));
      }

      var0.register(var3);
   }

   public static CompletableFuture<Suggestions> suggestTestFunction(CommandContext<CommandSourceStack> var0, SuggestionsBuilder var1) {
      Stream var2 = ((CommandSourceStack)var0.getSource()).registryAccess().lookupOrThrow(Registries.TEST_FUNCTION).listElements().map(Holder::getRegisteredName);
      return SharedSuggestionProvider.suggest(var2, var1);
   }

   private static int resetGameTestInfo(CommandSourceStack var0, GameTestInfo var1) {
      TestInstanceBlockEntity var2 = var1.getTestInstanceBlockEntity();
      Objects.requireNonNull(var0);
      var2.resetTest(var0::sendSystemMessage);
      return 1;
   }

   private static Stream<GameTestInfo> toGameTestInfos(CommandSourceStack var0, RetryOptions var1, TestPosFinder var2) {
      return var2.findTestPos().map((var2x) -> createGameTestInfo(var2x, var0, var1)).flatMap(Optional::stream);
   }

   private static Stream<GameTestInfo> toGameTestInfo(CommandSourceStack var0, RetryOptions var1, TestInstanceFinder var2, int var3) {
      return var2.findTests().filter((var1x) -> verifyStructureExists(var0, ((GameTestInstance)var1x.value()).structure())).map((var3x) -> new GameTestInfo(var3x, StructureUtils.getRotationForRotationSteps(var3), var0.getLevel(), var1));
   }

   private static Optional<GameTestInfo> createGameTestInfo(BlockPos var0, CommandSourceStack var1, RetryOptions var2) {
      ServerLevel var3 = var1.getLevel();
      BlockEntity var5 = var3.getBlockEntity(var0);
      if (var5 instanceof TestInstanceBlockEntity var4) {
         Optional var10000 = var4.test();
         Registry var10001 = var1.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE);
         Objects.requireNonNull(var10001);
         Optional var8 = var10000.flatMap(var10001::get);
         if (var8.isEmpty()) {
            var1.sendFailure(Component.translatable("commands.test.error.non_existant_test", var4.getTestName()));
            return Optional.empty();
         } else {
            Holder.Reference var6 = (Holder.Reference)var8.get();
            GameTestInfo var7 = new GameTestInfo(var6, var4.getRotation(), var3, var2);
            var7.setTestBlockPos(var0);
            return !verifyStructureExists(var1, var7.getStructure()) ? Optional.empty() : Optional.of(var7);
         }
      } else {
         var1.sendFailure(Component.translatable("commands.test.error.test_instance_not_found.position", var0.getX(), var0.getY(), var0.getZ()));
         return Optional.empty();
      }
   }

   private static int createNewStructure(CommandSourceStack var0, ResourceLocation var1, int var2, int var3, int var4) throws CommandSyntaxException {
      if (var2 <= 48 && var3 <= 48 && var4 <= 48) {
         ServerLevel var5 = var0.getLevel();
         BlockPos var6 = createTestPositionAround(var0);
         TestInstanceBlockEntity var7 = StructureUtils.createNewEmptyTest(var1, var6, new Vec3i(var2, var3, var4), Rotation.NONE, var5);
         BlockPos var8 = var7.getStructurePos();
         BlockPos var9 = var8.offset(var2 - 1, 0, var4 - 1);
         BlockPos.betweenClosedStream(var8, var9).forEach((var1x) -> var5.setBlockAndUpdate(var1x, Blocks.BEDROCK.defaultBlockState()));
         var0.sendSuccess(() -> Component.translatable("commands.test.create.success", var7.getTestName()), true);
         return 1;
      } else {
         throw TOO_LARGE.create(48);
      }
   }

   private static int showPos(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      BlockHitResult var2 = (BlockHitResult)var0.getPlayerOrException().pick(10.0, 1.0F, false);
      BlockPos var3 = var2.getBlockPos();
      ServerLevel var4 = var0.getLevel();
      Optional var5 = StructureUtils.findTestContainingPos(var3, 15, var4);
      if (var5.isEmpty()) {
         var5 = StructureUtils.findTestContainingPos(var3, 250, var4);
      }

      if (var5.isEmpty()) {
         throw NO_TEST_CONTAINING.create(var3.getX(), var3.getY(), var3.getZ());
      } else {
         BlockEntity var7 = var4.getBlockEntity((BlockPos)var5.get());
         if (var7 instanceof TestInstanceBlockEntity) {
            TestInstanceBlockEntity var6 = (TestInstanceBlockEntity)var7;
            BlockPos var12 = var6.getStructurePos();
            BlockPos var8 = var3.subtract(var12);
            int var10000 = var8.getX();
            String var9 = var10000 + ", " + var8.getY() + ", " + var8.getZ();
            String var10 = var6.getTestName().getString();
            MutableComponent var11 = Component.translatable("commands.test.coordinates", var8.getX(), var8.getY(), var8.getZ()).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent.ShowText(Component.translatable("commands.test.coordinates.copy"))).withClickEvent(new ClickEvent.CopyToClipboard("final BlockPos " + var1 + " = new BlockPos(" + var9 + ");")));
            var0.sendSuccess(() -> Component.translatable("commands.test.relative_position", var10, var11), false);
            DebugPackets.sendGameTestAddMarker(var4, new BlockPos(var3), var9, -2147418368, 10000);
            return 1;
         } else {
            throw TEST_INSTANCE_COULD_NOT_BE_FOUND.create();
         }
      }
   }

   private static int stopTests() {
      GameTestTicker.SINGLETON.clear();
      return 1;
   }

   public static int trackAndStartRunner(CommandSourceStack var0, GameTestRunner var1) {
      var1.addListener(new TestBatchSummaryDisplayer(var0));
      MultipleTestTracker var2 = new MultipleTestTracker(var1.getTestInfos());
      var2.addListener(new TestSummaryDisplayer(var0, var2));
      var2.addFailureListener((var0x) -> FailedTestTracker.rememberFailedTest(var0x.getTestHolder()));
      var1.start();
      return 1;
   }

   private static int exportTestStructure(CommandSourceStack var0, Holder<GameTestInstance> var1) {
      ServerLevel var10000 = var0.getLevel();
      ResourceLocation var10001 = ((GameTestInstance)var1.value()).structure();
      Objects.requireNonNull(var0);
      return !TestInstanceBlockEntity.export(var10000, var10001, var0::sendSystemMessage) ? 0 : 1;
   }

   private static boolean verifyStructureExists(CommandSourceStack var0, ResourceLocation var1) {
      if (var0.getLevel().getStructureManager().get(var1).isEmpty()) {
         var0.sendFailure(Component.translatable("commands.test.error.structure_not_found", Component.translationArg(var1)));
         return false;
      } else {
         return true;
      }
   }

   private static BlockPos createTestPositionAround(CommandSourceStack var0) {
      BlockPos var1 = BlockPos.containing(var0.getPosition());
      int var2 = var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var1).getY();
      return new BlockPos(var1.getX(), var2, var1.getZ() + 3);
   }

   public static record TestSummaryDisplayer(CommandSourceStack source, MultipleTestTracker tracker) implements GameTestListener {
      public TestSummaryDisplayer(CommandSourceStack var1, MultipleTestTracker var2) {
         super();
         this.source = var1;
         this.tracker = var2;
      }

      public void testStructureLoaded(GameTestInfo var1) {
      }

      public void testPassed(GameTestInfo var1, GameTestRunner var2) {
         this.showTestSummaryIfAllDone();
      }

      public void testFailed(GameTestInfo var1, GameTestRunner var2) {
         this.showTestSummaryIfAllDone();
      }

      public void testAddedForRerun(GameTestInfo var1, GameTestInfo var2, GameTestRunner var3) {
         this.tracker.addTestToTrack(var2);
      }

      private void showTestSummaryIfAllDone() {
         if (this.tracker.isDone()) {
            this.source.sendSuccess(() -> Component.translatable("commands.test.summary", this.tracker.getTotalCount()).withStyle(ChatFormatting.WHITE), true);
            if (this.tracker.hasFailedRequired()) {
               this.source.sendFailure(Component.translatable("commands.test.summary.failed", this.tracker.getFailedRequiredCount()));
            } else {
               this.source.sendSuccess(() -> Component.translatable("commands.test.summary.all_required_passed").withStyle(ChatFormatting.GREEN), true);
            }

            if (this.tracker.hasFailedOptional()) {
               this.source.sendSystemMessage(Component.translatable("commands.test.summary.optional_failed", this.tracker.getFailedOptionalCount()));
            }
         }

      }
   }

   static record TestBatchSummaryDisplayer(CommandSourceStack source) implements GameTestBatchListener {
      TestBatchSummaryDisplayer(CommandSourceStack var1) {
         super();
         this.source = var1;
      }

      public void testBatchStarting(GameTestBatch var1) {
         this.source.sendSuccess(() -> Component.translatable("commands.test.batch.starting", var1.environment().getRegisteredName(), var1.index()), true);
      }

      public void testBatchFinished(GameTestBatch var1) {
      }
   }
}
