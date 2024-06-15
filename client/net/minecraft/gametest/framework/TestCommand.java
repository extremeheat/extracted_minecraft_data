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
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class TestCommand {
   public static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
   public static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_CLEAR_RADIUS = 200;
   private static final int MAX_CLEAR_RADIUS = 1024;
   private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
   private static final int SHOW_POS_DURATION_MS = 10000;
   private static final int DEFAULT_X_SIZE = 5;
   private static final int DEFAULT_Y_SIZE = 5;
   private static final int DEFAULT_Z_SIZE = 5;
   private static final String STRUCTURE_BLOCK_ENTITY_COULD_NOT_BE_FOUND = "Structure block entity could not be found";
   private static final TestFinder.Builder<TestCommand.Runner> testFinder = new TestFinder.Builder<>(TestCommand.Runner::new);

   public TestCommand() {
      super();
   }

   private static ArgumentBuilder<CommandSourceStack, ?> runWithRetryOptions(
      ArgumentBuilder<CommandSourceStack, ?> var0,
      Function<CommandContext<CommandSourceStack>, TestCommand.Runner> var1,
      Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2
   ) {
      return var0.executes(var1x -> ((TestCommand.Runner)var1.apply(var1x)).run())
         .then(
            ((RequiredArgumentBuilder)Commands.argument("numberOfTimes", IntegerArgumentType.integer(0))
                  .executes(
                     var1x -> ((TestCommand.Runner)var1.apply(var1x)).run(new RetryOptions(IntegerArgumentType.getInteger(var1x, "numberOfTimes"), false))
                  ))
               .then(
                  (ArgumentBuilder)var2.apply(
                     Commands.argument("untilFailed", BoolArgumentType.bool())
                        .executes(
                           var1x -> ((TestCommand.Runner)var1.apply(var1x))
                                 .run(new RetryOptions(IntegerArgumentType.getInteger(var1x, "numberOfTimes"), BoolArgumentType.getBool(var1x, "untilFailed")))
                        )
                  )
               )
         );
   }

   private static ArgumentBuilder<CommandSourceStack, ?> runWithRetryOptions(
      ArgumentBuilder<CommandSourceStack, ?> var0, Function<CommandContext<CommandSourceStack>, TestCommand.Runner> var1
   ) {
      return runWithRetryOptions(var0, var1, var0x -> var0x);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> runWithRetryOptionsAndBuildInfo(
      ArgumentBuilder<CommandSourceStack, ?> var0, Function<CommandContext<CommandSourceStack>, TestCommand.Runner> var1
   ) {
      return runWithRetryOptions(
         var0,
         var1,
         var1x -> var1x.then(
               ((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer())
                     .executes(
                        var1xx -> ((TestCommand.Runner)var1.apply(var1xx))
                              .run(
                                 new RetryOptions(IntegerArgumentType.getInteger(var1xx, "numberOfTimes"), BoolArgumentType.getBool(var1xx, "untilFailed")),
                                 IntegerArgumentType.getInteger(var1xx, "rotationSteps")
                              )
                     ))
                  .then(
                     Commands.argument("testsPerRow", IntegerArgumentType.integer())
                        .executes(
                           var1xx -> ((TestCommand.Runner)var1.apply(var1xx))
                                 .run(
                                    new RetryOptions(IntegerArgumentType.getInteger(var1xx, "numberOfTimes"), BoolArgumentType.getBool(var1xx, "untilFailed")),
                                    IntegerArgumentType.getInteger(var1xx, "rotationSteps"),
                                    IntegerArgumentType.getInteger(var1xx, "testsPerRow")
                                 )
                        )
                  )
            )
      );
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      ArgumentBuilder var1 = runWithRetryOptionsAndBuildInfo(
         Commands.argument("onlyRequiredTests", BoolArgumentType.bool()),
         var0x -> testFinder.failedTests(var0x, BoolArgumentType.getBool(var0x, "onlyRequiredTests"))
      );
      ArgumentBuilder var2 = runWithRetryOptionsAndBuildInfo(
         Commands.argument("testClassName", TestClassNameArgument.testClassName()),
         var0x -> testFinder.allTestsInClass(var0x, TestClassNameArgument.getTestClassName(var0x, "testClassName"))
      );
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                                                              "test"
                                                                           )
                                                                           .then(
                                                                              Commands.literal("run")
                                                                                 .then(
                                                                                    runWithRetryOptionsAndBuildInfo(
                                                                                       Commands.argument(
                                                                                          "testName", TestFunctionArgument.testFunctionArgument()
                                                                                       ),
                                                                                       var0x -> testFinder.byArgument(var0x, "testName")
                                                                                    )
                                                                                 )
                                                                           ))
                                                                        .then(
                                                                           Commands.literal("runmultiple")
                                                                              .then(
                                                                                 ((RequiredArgumentBuilder)Commands.argument(
                                                                                          "testName", TestFunctionArgument.testFunctionArgument()
                                                                                       )
                                                                                       .executes(var0x -> testFinder.byArgument(var0x, "testName").run()))
                                                                                    .then(
                                                                                       Commands.argument("amount", IntegerArgumentType.integer())
                                                                                          .executes(
                                                                                             var0x -> testFinder.createMultipleCopies(
                                                                                                      IntegerArgumentType.getInteger(var0x, "amount")
                                                                                                   )
                                                                                                   .byArgument(var0x, "testName")
                                                                                                   .run()
                                                                                          )
                                                                                    )
                                                                              )
                                                                        ))
                                                                     .then(
                                                                        runWithRetryOptionsAndBuildInfo(
                                                                           Commands.literal("runall").then(var2), testFinder::allTests
                                                                        )
                                                                     ))
                                                                  .then(runWithRetryOptions(Commands.literal("runthese"), testFinder::allNearby)))
                                                               .then(runWithRetryOptions(Commands.literal("runclosest"), testFinder::nearest)))
                                                            .then(runWithRetryOptions(Commands.literal("runthat"), testFinder::lookedAt)))
                                                         .then(
                                                            runWithRetryOptionsAndBuildInfo(Commands.literal("runfailed").then(var1), testFinder::failedTests)
                                                         ))
                                                      .then(
                                                         Commands.literal("locate")
                                                            .then(
                                                               Commands.argument("testName", TestFunctionArgument.testFunctionArgument())
                                                                  .executes(
                                                                     var0x -> testFinder.locateByName(
                                                                              var0x,
                                                                              "minecraft:"
                                                                                 + TestFunctionArgument.getTestFunction(var0x, "testName").structureName()
                                                                           )
                                                                           .locate()
                                                                  )
                                                            )
                                                      ))
                                                   .then(Commands.literal("resetclosest").executes(var0x -> testFinder.nearest(var0x).reset())))
                                                .then(Commands.literal("resetthese").executes(var0x -> testFinder.allNearby(var0x).reset())))
                                             .then(Commands.literal("resetthat").executes(var0x -> testFinder.lookedAt(var0x).reset())))
                                          .then(
                                             Commands.literal("export")
                                                .then(
                                                   Commands.argument("testName", StringArgumentType.word())
                                                      .executes(
                                                         var0x -> exportTestStructure(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               "minecraft:" + StringArgumentType.getString(var0x, "testName")
                                                            )
                                                      )
                                                )
                                          ))
                                       .then(Commands.literal("exportclosest").executes(var0x -> testFinder.nearest(var0x).export())))
                                    .then(Commands.literal("exportthese").executes(var0x -> testFinder.allNearby(var0x).export())))
                                 .then(Commands.literal("exportthat").executes(var0x -> testFinder.lookedAt(var0x).export())))
                              .then(Commands.literal("clearthat").executes(var0x -> testFinder.lookedAt(var0x).clear())))
                           .then(Commands.literal("clearthese").executes(var0x -> testFinder.allNearby(var0x).clear())))
                        .then(
                           ((LiteralArgumentBuilder)Commands.literal("clearall").executes(var0x -> testFinder.radius(var0x, 200).clear()))
                              .then(
                                 Commands.argument("radius", IntegerArgumentType.integer())
                                    .executes(var0x -> testFinder.radius(var0x, Mth.clamp(IntegerArgumentType.getInteger(var0x, "radius"), 0, 1024)).clear())
                              )
                        ))
                     .then(
                        Commands.literal("import")
                           .then(
                              Commands.argument("testName", StringArgumentType.word())
                                 .executes(var0x -> importTestStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName")))
                           )
                     ))
                  .then(Commands.literal("stop").executes(var0x -> stopTests())))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("pos").executes(var0x -> showPos((CommandSourceStack)var0x.getSource(), "pos")))
                     .then(
                        Commands.argument("var", StringArgumentType.word())
                           .executes(var0x -> showPos((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "var")))
                     )
               ))
            .then(
               Commands.literal("create")
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("testName", StringArgumentType.word())
                           .suggests(TestFunctionArgument::suggestTestFunction)
                           .executes(
                              var0x -> createNewStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"), 5, 5, 5)
                           ))
                        .then(
                           ((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer())
                                 .executes(
                                    var0x -> createNewStructure(
                                          (CommandSourceStack)var0x.getSource(),
                                          StringArgumentType.getString(var0x, "testName"),
                                          IntegerArgumentType.getInteger(var0x, "width"),
                                          IntegerArgumentType.getInteger(var0x, "width"),
                                          IntegerArgumentType.getInteger(var0x, "width")
                                       )
                                 ))
                              .then(
                                 Commands.argument("height", IntegerArgumentType.integer())
                                    .then(
                                       Commands.argument("depth", IntegerArgumentType.integer())
                                          .executes(
                                             var0x -> createNewStructure(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   StringArgumentType.getString(var0x, "testName"),
                                                   IntegerArgumentType.getInteger(var0x, "width"),
                                                   IntegerArgumentType.getInteger(var0x, "height"),
                                                   IntegerArgumentType.getInteger(var0x, "depth")
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int resetGameTestInfo(GameTestInfo var0) {
      var0.getLevel().getEntities(null, var0.getStructureBounds()).stream().forEach(var0x -> var0x.remove(Entity.RemovalReason.DISCARDED));
      var0.getStructureBlockEntity().placeStructure(var0.getLevel());
      StructureUtils.removeBarriers(var0.getStructureBounds(), var0.getLevel());
      say(var0.getLevel(), "Reset succeded for: " + var0.getTestName(), ChatFormatting.GREEN);
      return 1;
   }

   static Stream<GameTestInfo> toGameTestInfos(CommandSourceStack var0, RetryOptions var1, StructureBlockPosFinder var2) {
      return var2.findStructureBlockPos().map(var2x -> createGameTestInfo(var2x, var0.getLevel(), var1)).flatMap(Optional::stream);
   }

   static Stream<GameTestInfo> toGameTestInfo(CommandSourceStack var0, RetryOptions var1, TestFunctionFinder var2, int var3) {
      return var2.findTestFunctions()
         .filter(var1x -> verifyStructureExists(var0.getLevel(), var1x.structureName()))
         .map(var3x -> new GameTestInfo(var3x, StructureUtils.getRotationForRotationSteps(var3), var0.getLevel(), var1));
   }

   private static Optional<GameTestInfo> createGameTestInfo(BlockPos var0, ServerLevel var1, RetryOptions var2) {
      StructureBlockEntity var3 = (StructureBlockEntity)var1.getBlockEntity(var0);
      if (var3 == null) {
         say(var1, "Structure block entity could not be found", ChatFormatting.RED);
         return Optional.empty();
      } else {
         String var4 = var3.getMetaData();
         Optional var5 = GameTestRegistry.findTestFunction(var4);
         if (var5.isEmpty()) {
            say(var1, "Test function for test " + var4 + " could not be found", ChatFormatting.RED);
            return Optional.empty();
         } else {
            TestFunction var6 = (TestFunction)var5.get();
            GameTestInfo var7 = new GameTestInfo(var6, var3.getRotation(), var1, var2);
            var7.setStructureBlockPos(var0);
            return !verifyStructureExists(var1, var7.getStructureName()) ? Optional.empty() : Optional.of(var7);
         }
      }
   }

   private static int createNewStructure(CommandSourceStack var0, String var1, int var2, int var3, int var4) {
      if (var2 <= 48 && var3 <= 48 && var4 <= 48) {
         ServerLevel var5 = var0.getLevel();
         BlockPos var6 = createTestPositionAround(var0).below();
         StructureUtils.createNewEmptyStructureBlock(var1.toLowerCase(), var6, new Vec3i(var2, var3, var4), Rotation.NONE, var5);
         BlockPos var7 = var6.above();
         BlockPos var8 = var7.offset(var2 - 1, 0, var4 - 1);
         BlockPos.betweenClosedStream(var7, var8).forEach(var1x -> var5.setBlockAndUpdate(var1x, Blocks.BEDROCK.defaultBlockState()));
         StructureUtils.addCommandBlockAndButtonToStartTest(var6, new BlockPos(1, 0, -1), Rotation.NONE, var5);
         return 0;
      } else {
         throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
      }
   }

   private static int showPos(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      BlockHitResult var2 = (BlockHitResult)var0.getPlayerOrException().pick(10.0, 1.0F, false);
      BlockPos var3 = var2.getBlockPos();
      ServerLevel var4 = var0.getLevel();
      Optional var5 = StructureUtils.findStructureBlockContainingPos(var3, 15, var4);
      if (var5.isEmpty()) {
         var5 = StructureUtils.findStructureBlockContainingPos(var3, 200, var4);
      }

      if (var5.isEmpty()) {
         var0.sendFailure(Component.literal("Can't find a structure block that contains the targeted pos " + var3));
         return 0;
      } else {
         StructureBlockEntity var6 = (StructureBlockEntity)var4.getBlockEntity((BlockPos)var5.get());
         if (var6 == null) {
            say(var4, "Structure block entity could not be found", ChatFormatting.RED);
            return 0;
         } else {
            BlockPos var7 = var3.subtract((Vec3i)var5.get());
            String var8 = var7.getX() + ", " + var7.getY() + ", " + var7.getZ();
            String var9 = var6.getMetaData();
            MutableComponent var10 = Component.literal(var8)
               .setStyle(
                  Style.EMPTY
                     .withBold(true)
                     .withColor(ChatFormatting.GREEN)
                     .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard")))
                     .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + var1 + " = new BlockPos(" + var8 + ");"))
               );
            var0.sendSuccess(() -> Component.literal("Position relative to " + var9 + ": ").append(var10), false);
            DebugPackets.sendGameTestAddMarker(var4, new BlockPos(var3), var8, -2147418368, 10000);
            return 1;
         }
      }
   }

   static int stopTests() {
      GameTestTicker.SINGLETON.clear();
      return 1;
   }

   static int trackAndStartRunner(CommandSourceStack var0, ServerLevel var1, GameTestRunner var2) {
      var2.addListener(new TestCommand.TestBatchSummaryDisplayer(var0));
      MultipleTestTracker var3 = new MultipleTestTracker(var2.getTestInfos());
      var3.addListener(new TestCommand.TestSummaryDisplayer(var1, var3));
      var3.addFailureListener(var0x -> GameTestRegistry.rememberFailedTest(var0x.getTestFunction()));
      var2.start();
      return 1;
   }

   static int saveAndExportTestStructure(CommandSourceStack var0, StructureBlockEntity var1) {
      String var2 = var1.getStructureName();
      if (!var1.saveStructure(true)) {
         say(var0, "Failed to save structure " + var2);
      }

      return exportTestStructure(var0, var2);
   }

   private static int exportTestStructure(CommandSourceStack var0, String var1) {
      Path var2 = Paths.get(StructureUtils.testStructuresDir);
      ResourceLocation var3 = new ResourceLocation(var1);
      Path var4 = var0.getLevel().getStructureManager().getPathToGeneratedStructure(var3, ".nbt");
      Path var5 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, var4, var3.getPath(), var2);
      if (var5 == null) {
         say(var0, "Failed to export " + var4);
         return 1;
      } else {
         try {
            FileUtil.createDirectoriesSafe(var5.getParent());
         } catch (IOException var7) {
            say(var0, "Could not create folder " + var5.getParent());
            LOGGER.error("Could not create export folder", var7);
            return 1;
         }

         say(var0, "Exported " + var1 + " to " + var5.toAbsolutePath());
         return 0;
      }
   }

   private static boolean verifyStructureExists(ServerLevel var0, String var1) {
      if (var0.getStructureManager().get(new ResourceLocation(var1)).isEmpty()) {
         say(var0, "Test structure " + var1 + " could not be found", ChatFormatting.RED);
         return false;
      } else {
         return true;
      }
   }

   static BlockPos createTestPositionAround(CommandSourceStack var0) {
      BlockPos var1 = BlockPos.containing(var0.getPosition());
      int var2 = var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var1).getY();
      return new BlockPos(var1.getX(), var2 + 1, var1.getZ() + 3);
   }

   static void say(CommandSourceStack var0, String var1) {
      var0.sendSuccess(() -> Component.literal(var1), false);
   }

   private static int importTestStructure(CommandSourceStack var0, String var1) {
      Path var2 = Paths.get(StructureUtils.testStructuresDir, var1 + ".snbt");
      ResourceLocation var3 = new ResourceLocation("minecraft", var1);
      Path var4 = var0.getLevel().getStructureManager().getPathToGeneratedStructure(var3, ".nbt");

      try {
         BufferedReader var5 = Files.newBufferedReader(var2);
         String var6 = IOUtils.toString(var5);
         Files.createDirectories(var4.getParent());

         try (OutputStream var7 = Files.newOutputStream(var4)) {
            NbtIo.writeCompressed(NbtUtils.snbtToStructure(var6), var7);
         }

         var0.getLevel().getStructureManager().remove(var3);
         say(var0, "Imported to " + var4.toAbsolutePath());
         return 0;
      } catch (CommandSyntaxException | IOException var12) {
         LOGGER.error("Failed to load structure {}", var1, var12);
         return 1;
      }
   }

   static void say(ServerLevel var0, String var1, ChatFormatting var2) {
      var0.getPlayers(var0x -> true).forEach(var2x -> var2x.sendSystemMessage(Component.literal(var1).withStyle(var2)));
   }

   public static class Runner {
      private final TestFinder<TestCommand.Runner> finder;

      public Runner(TestFinder<TestCommand.Runner> var1) {
         super();
         this.finder = var1;
      }

      public int reset() {
         TestCommand.stopTests();
         return TestCommand.toGameTestInfos(this.finder.source(), RetryOptions.noRetries(), this.finder).map(TestCommand::resetGameTestInfo).toList().isEmpty()
            ? 0
            : 1;
      }

      private <T> void logAndRun(Stream<T> var1, ToIntFunction<T> var2, Runnable var3, Consumer<Integer> var4) {
         int var5 = var1.mapToInt(var2).sum();
         if (var5 == 0) {
            var3.run();
         } else {
            var4.accept(var5);
         }
      }

      public int clear() {
         TestCommand.stopTests();
         CommandSourceStack var1 = this.finder.source();
         ServerLevel var2 = var1.getLevel();
         GameTestRunner.clearMarkers(var2);
         this.logAndRun(
            this.finder.findStructureBlockPos(),
            var1x -> {
               StructureBlockEntity var2x = (StructureBlockEntity)var2.getBlockEntity(var1x);
               if (var2x == null) {
                  return 0;
               } else {
                  BoundingBox var3 = StructureUtils.getStructureBoundingBox(var2x);
                  StructureUtils.clearSpaceForStructure(var3, var2);
                  return 1;
               }
            },
            () -> TestCommand.say(var2, "Could not find any structures to clear", ChatFormatting.RED),
            var1x -> TestCommand.say(var1, "Cleared " + var1x + " structures")
         );
         return 1;
      }

      public int export() {
         MutableBoolean var1 = new MutableBoolean(true);
         CommandSourceStack var2 = this.finder.source();
         ServerLevel var3 = var2.getLevel();
         this.logAndRun(
            this.finder.findStructureBlockPos(),
            var3x -> {
               StructureBlockEntity var4 = (StructureBlockEntity)var3.getBlockEntity(var3x);
               if (var4 == null) {
                  TestCommand.say(var3, "Structure block entity could not be found", ChatFormatting.RED);
                  var1.setFalse();
                  return 0;
               } else {
                  if (TestCommand.saveAndExportTestStructure(var2, var4) != 0) {
                     var1.setFalse();
                  }

                  return 1;
               }
            },
            () -> TestCommand.say(var3, "Could not find any structures to export", ChatFormatting.RED),
            var1x -> TestCommand.say(var2, "Exported " + var1x + " structures")
         );
         return var1.getValue() ? 0 : 1;
      }

      public int run(RetryOptions var1, int var2, int var3) {
         TestCommand.stopTests();
         CommandSourceStack var4 = this.finder.source();
         ServerLevel var5 = var4.getLevel();
         BlockPos var6 = TestCommand.createTestPositionAround(var4);
         List var7 = Stream.concat(TestCommand.toGameTestInfos(var4, var1, this.finder), TestCommand.toGameTestInfo(var4, var1, this.finder, var2)).toList();
         if (var7.isEmpty()) {
            TestCommand.say(var4, "No tests found");
            return 0;
         } else {
            GameTestRunner.clearMarkers(var5);
            GameTestRegistry.forgetFailedTests();
            TestCommand.say(var4, "Running " + var7.size() + " tests...");
            GameTestRunner var8 = GameTestRunner.Builder.fromInfo(var7, var5).newStructureSpawner(new StructureGridSpawner(var6, var3)).build();
            return TestCommand.trackAndStartRunner(var4, var5, var8);
         }
      }

      public int run(int var1, int var2) {
         return this.run(RetryOptions.noRetries(), var1, var2);
      }

      public int run(int var1) {
         return this.run(RetryOptions.noRetries(), var1, 8);
      }

      public int run(RetryOptions var1, int var2) {
         return this.run(var1, var2, 8);
      }

      public int run(RetryOptions var1) {
         return this.run(var1, 0, 8);
      }

      public int run() {
         return this.run(RetryOptions.noRetries());
      }

      public int locate() {
         TestCommand.say(this.finder.source(), "Started locating test structures, this might take a while..");
         MutableInt var1 = new MutableInt(0);
         BlockPos var2 = BlockPos.containing(this.finder.source().getPosition());
         this.finder
            .findStructureBlockPos()
            .forEach(
               var3x -> {
                  StructureBlockEntity var4 = (StructureBlockEntity)this.finder.source().getLevel().getBlockEntity(var3x);
                  if (var4 != null) {
                     Direction var5 = var4.getRotation().rotate(Direction.NORTH);
                     BlockPos var6 = var4.getBlockPos().relative(var5, 2);
                     int var7 = (int)var5.getOpposite().toYRot();
                     String var8 = String.format("/tp @s %d %d %d %d 0", var6.getX(), var6.getY(), var6.getZ(), var7);
                     int var9 = var2.getX() - var3x.getX();
                     int var10 = var2.getZ() - var3x.getZ();
                     int var11 = Mth.floor(Mth.sqrt((float)(var9 * var9 + var10 * var10)));
                     MutableComponent var12 = ComponentUtils.wrapInSquareBrackets(
                           Component.translatable("chat.coordinates", var3x.getX(), var3x.getY(), var3x.getZ())
                        )
                        .withStyle(
                           var1xx -> var1xx.withColor(ChatFormatting.GREEN)
                                 .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, var8))
                                 .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                        );
                     MutableComponent var13 = Component.literal("Found structure at: ").append(var12).append(" (distance: " + var11 + ")");
                     this.finder.source().sendSuccess(() -> var13, false);
                     var1.increment();
                  }
               }
            );
         int var3 = var1.intValue();
         if (var3 == 0) {
            TestCommand.say(this.finder.source().getLevel(), "No such test structure found", ChatFormatting.RED);
            return 0;
         } else {
            TestCommand.say(this.finder.source().getLevel(), "Finished locating, found " + var3 + " structure(s)", ChatFormatting.GREEN);
            return 1;
         }
      }
   }

   static record TestBatchSummaryDisplayer(CommandSourceStack source) implements GameTestBatchListener {
      TestBatchSummaryDisplayer(CommandSourceStack source) {
         super();
         this.source = source;
      }

      @Override
      public void testBatchStarting(GameTestBatch var1) {
         TestCommand.say(this.source, "Starting batch: " + var1.name());
      }

      @Override
      public void testBatchFinished(GameTestBatch var1) {
      }
   }

   public static record TestSummaryDisplayer(ServerLevel level, MultipleTestTracker tracker) implements GameTestListener {
      public TestSummaryDisplayer(ServerLevel level, MultipleTestTracker tracker) {
         super();
         this.level = level;
         this.tracker = tracker;
      }

      @Override
      public void testStructureLoaded(GameTestInfo var1) {
      }

      @Override
      public void testPassed(GameTestInfo var1, GameTestRunner var2) {
         showTestSummaryIfAllDone(this.level, this.tracker);
      }

      @Override
      public void testFailed(GameTestInfo var1, GameTestRunner var2) {
         showTestSummaryIfAllDone(this.level, this.tracker);
      }

      @Override
      public void testAddedForRerun(GameTestInfo var1, GameTestInfo var2, GameTestRunner var3) {
         this.tracker.addTestToTrack(var2);
      }

      private static void showTestSummaryIfAllDone(ServerLevel var0, MultipleTestTracker var1) {
         if (var1.isDone()) {
            TestCommand.say(var0, "GameTest done! " + var1.getTotalCount() + " tests were run", ChatFormatting.WHITE);
            if (var1.hasFailedRequired()) {
               TestCommand.say(var0, var1.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
            } else {
               TestCommand.say(var0, "All required tests passed :)", ChatFormatting.GREEN);
            }

            if (var1.hasFailedOptional()) {
               TestCommand.say(var0, var1.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
            }
         }
      }
   }
}
