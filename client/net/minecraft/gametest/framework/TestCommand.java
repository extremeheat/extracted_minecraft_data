package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TestCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_CLEAR_RADIUS = 200;
   private static final int MAX_CLEAR_RADIUS = 1024;
   private static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
   private static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
   private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
   private static final int SHOW_POS_DURATION_MS = 10000;
   private static final int DEFAULT_X_SIZE = 5;
   private static final int DEFAULT_Y_SIZE = 5;
   private static final int DEFAULT_Z_SIZE = 5;

   public TestCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                                   "test"
                                                )
                                                .then(
                                                   ((LiteralArgumentBuilder)Commands.literal("runthis")
                                                         .executes(var0x -> runNearbyTest((CommandSourceStack)var0x.getSource(), false)))
                                                      .then(
                                                         Commands.literal("untilFailed")
                                                            .executes(var0x -> runNearbyTest((CommandSourceStack)var0x.getSource(), true))
                                                      )
                                                ))
                                             .then(Commands.literal("resetthis").executes(var0x -> resetNearbyTest((CommandSourceStack)var0x.getSource()))))
                                          .then(
                                             Commands.literal("runthese").executes(var0x -> runAllNearbyTests((CommandSourceStack)var0x.getSource(), false))
                                          ))
                                       .then(
                                          ((LiteralArgumentBuilder)Commands.literal("runfailed")
                                                .executes(var0x -> runLastFailedTests((CommandSourceStack)var0x.getSource(), false, 0, 8)))
                                             .then(
                                                ((RequiredArgumentBuilder)Commands.argument("onlyRequiredTests", BoolArgumentType.bool())
                                                      .executes(
                                                         var0x -> runLastFailedTests(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               BoolArgumentType.getBool(var0x, "onlyRequiredTests"),
                                                               0,
                                                               8
                                                            )
                                                      ))
                                                   .then(
                                                      ((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                                            .executes(
                                                               var0x -> runLastFailedTests(
                                                                     (CommandSourceStack)var0x.getSource(),
                                                                     BoolArgumentType.getBool(var0x, "onlyRequiredTests"),
                                                                     IntegerArgumentType.getInteger(var0x, "rotationSteps"),
                                                                     8
                                                                  )
                                                            ))
                                                         .then(
                                                            Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                                               .executes(
                                                                  var0x -> runLastFailedTests(
                                                                        (CommandSourceStack)var0x.getSource(),
                                                                        BoolArgumentType.getBool(var0x, "onlyRequiredTests"),
                                                                        IntegerArgumentType.getInteger(var0x, "rotationSteps"),
                                                                        IntegerArgumentType.getInteger(var0x, "testsPerRow")
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("run")
                                          .then(
                                             ((RequiredArgumentBuilder)Commands.argument("testName", TestFunctionArgument.testFunctionArgument())
                                                   .executes(
                                                      var0x -> runTest(
                                                            (CommandSourceStack)var0x.getSource(), TestFunctionArgument.getTestFunction(var0x, "testName"), 0
                                                         )
                                                   ))
                                                .then(
                                                   Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                                      .executes(
                                                         var0x -> runTest(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               TestFunctionArgument.getTestFunction(var0x, "testName"),
                                                               IntegerArgumentType.getInteger(var0x, "rotationSteps")
                                                            )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("runall")
                                             .executes(var0x -> runAllTests((CommandSourceStack)var0x.getSource(), 0, 8)))
                                          .then(
                                             ((RequiredArgumentBuilder)Commands.argument("testClassName", TestClassNameArgument.testClassName())
                                                   .executes(
                                                      var0x -> runAllTestsInClass(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            TestClassNameArgument.getTestClassName(var0x, "testClassName"),
                                                            0,
                                                            8
                                                         )
                                                   ))
                                                .then(
                                                   ((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                                         .executes(
                                                            var0x -> runAllTestsInClass(
                                                                  (CommandSourceStack)var0x.getSource(),
                                                                  TestClassNameArgument.getTestClassName(var0x, "testClassName"),
                                                                  IntegerArgumentType.getInteger(var0x, "rotationSteps"),
                                                                  8
                                                               )
                                                         ))
                                                      .then(
                                                         Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                                            .executes(
                                                               var0x -> runAllTestsInClass(
                                                                     (CommandSourceStack)var0x.getSource(),
                                                                     TestClassNameArgument.getTestClassName(var0x, "testClassName"),
                                                                     IntegerArgumentType.getInteger(var0x, "rotationSteps"),
                                                                     IntegerArgumentType.getInteger(var0x, "testsPerRow")
                                                                  )
                                                            )
                                                      )
                                                )
                                          ))
                                       .then(
                                          ((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                                .executes(
                                                   var0x -> runAllTests(
                                                         (CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "rotationSteps"), 8
                                                      )
                                                ))
                                             .then(
                                                Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                                   .executes(
                                                      var0x -> runAllTests(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            IntegerArgumentType.getInteger(var0x, "rotationSteps"),
                                                            IntegerArgumentType.getInteger(var0x, "testsPerRow")
                                                         )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("export")
                                    .then(
                                       Commands.argument("testName", StringArgumentType.word())
                                          .executes(
                                             var0x -> exportTestStructure(
                                                   (CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName")
                                                )
                                          )
                                    )
                              ))
                           .then(Commands.literal("exportthis").executes(var0x -> exportNearestTestStructure((CommandSourceStack)var0x.getSource()))))
                        .then(Commands.literal("exportthese").executes(var0x -> exportAllNearbyTests((CommandSourceStack)var0x.getSource()))))
                     .then(
                        Commands.literal("import")
                           .then(
                              Commands.argument("testName", StringArgumentType.word())
                                 .executes(
                                    var0x -> importTestStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"))
                                 )
                           )
                     ))
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
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("clearall").executes(var0x -> clearAllTests((CommandSourceStack)var0x.getSource(), 200)))
                  .then(
                     Commands.argument("radius", IntegerArgumentType.integer())
                        .executes(var0x -> clearAllTests((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "radius")))
                  )
            )
      );
   }

   private static int createNewStructure(CommandSourceStack var0, String var1, int var2, int var3, int var4) {
      if (var2 <= 48 && var3 <= 48 && var4 <= 48) {
         ServerLevel var5 = var0.getLevel();
         BlockPos var6 = createTestPositionAround(var0).below();
         StructureUtils.createNewEmptyStructureBlock(var1.toLowerCase(), var6, new Vec3i(var2, var3, var4), Rotation.NONE, var5);

         for(int var7 = 0; var7 < var2; ++var7) {
            for(int var8 = 0; var8 < var4; ++var8) {
               BlockPos var9 = new BlockPos(var6.getX() + var7, var6.getY() + 1, var6.getZ() + var8);
               Block var10 = Blocks.POLISHED_ANDESITE;
               BlockInput var11 = new BlockInput(var10.defaultBlockState(), Collections.emptySet(), null);
               var11.place(var5, var9, 2);
            }
         }

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

   private static int runNearbyTest(CommandSourceStack var0, boolean var1) {
      BlockPos var2 = BlockPos.containing(var0.getPosition());
      ServerLevel var3 = var0.getLevel();
      BlockPos var4 = StructureUtils.findNearestStructureBlock(var2, 15, var3);
      if (var4 == null) {
         say(var3, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         GameTestRunner.clearMarkers(var3);
         runTest(var3, var4, null, var1);
         return 1;
      }
   }

   private static int resetNearbyTest(CommandSourceStack var0) {
      BlockPos var1 = BlockPos.containing(var0.getPosition());
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = StructureUtils.findNearestStructureBlock(var1, 15, var2);
      if (var3 == null) {
         say(var2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         StructureBlockEntity var4 = (StructureBlockEntity)var2.getBlockEntity(var3);
         var4.placeStructure(var2);
         String var5 = var4.getMetaData();
         TestFunction var6 = GameTestRegistry.getTestFunction(var5);
         say(var2, "Reset succeded for: " + var6, ChatFormatting.GREEN);
         return 1;
      }
   }

   private static int runAllNearbyTests(CommandSourceStack var0, boolean var1) {
      BlockPos var2 = BlockPos.containing(var0.getPosition());
      ServerLevel var3 = var0.getLevel();
      Collection var4 = StructureUtils.findStructureBlocks(var2, 200, var3);
      if (var4.isEmpty()) {
         say(var3, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
         return 1;
      } else {
         GameTestRunner.clearMarkers(var3);
         say(var0, "Running " + var4.size() + " tests...");
         MultipleTestTracker var5 = new MultipleTestTracker();
         var4.forEach(var3x -> runTest(var3, var3x, var5, var1));
         return 1;
      }
   }

   private static void runTest(ServerLevel var0, BlockPos var1, @Nullable MultipleTestTracker var2, boolean var3) {
      StructureBlockEntity var4 = (StructureBlockEntity)var0.getBlockEntity(var1);
      String var5 = var4.getMetaData();
      Optional var6 = GameTestRegistry.findTestFunction(var5);
      if (var6.isEmpty()) {
         say(var0, "Test function for test " + var5 + " could not be found", ChatFormatting.RED);
      } else {
         TestFunction var7 = (TestFunction)var6.get();
         GameTestInfo var8 = new GameTestInfo(var7, var4.getRotation(), var0);
         var8.setRerunUntilFailed(var3);
         if (var2 != null) {
            var2.addTestToTrack(var8);
            var8.addListener(new TestCommand.TestSummaryDisplayer(var0, var2));
         }

         if (verifyStructureExists(var0, var8)) {
            runTestPreparation(var7, var0);
            BoundingBox var9 = StructureUtils.getStructureBoundingBox(var4);
            BlockPos var10 = new BlockPos(var9.minX(), var9.minY(), var9.minZ());
            GameTestRunner.runTest(var8, var10, GameTestTicker.SINGLETON);
         }
      }
   }

   private static boolean verifyStructureExists(ServerLevel var0, GameTestInfo var1) {
      if (var0.getStructureManager().get(new ResourceLocation(var1.getStructureName())).isEmpty()) {
         say(var0, "Test structure " + var1.getStructureName() + " could not be found", ChatFormatting.RED);
         return false;
      } else {
         return true;
      }
   }

   static void showTestSummaryIfAllDone(ServerLevel var0, MultipleTestTracker var1) {
      if (var1.isDone()) {
         say(var0, "GameTest done! " + var1.getTotalCount() + " tests were run", ChatFormatting.WHITE);
         if (var1.hasFailedRequired()) {
            say(var0, var1.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
         } else {
            say(var0, "All required tests passed :)", ChatFormatting.GREEN);
         }

         if (var1.hasFailedOptional()) {
            say(var0, var1.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
         }
      }
   }

   private static int clearAllTests(CommandSourceStack var0, int var1) {
      ServerLevel var2 = var0.getLevel();
      GameTestRunner.clearMarkers(var2);
      BlockPos var3 = BlockPos.containing(
         var0.getPosition().x,
         (double)var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.containing(var0.getPosition())).getY(),
         var0.getPosition().z
      );
      GameTestRunner.clearAllTests(var2, var3, GameTestTicker.SINGLETON, Mth.clamp(var1, 0, 1024));
      return 1;
   }

   private static int runTest(CommandSourceStack var0, TestFunction var1, int var2) {
      ServerLevel var3 = var0.getLevel();
      BlockPos var4 = createTestPositionAround(var0);
      GameTestRunner.clearMarkers(var3);
      runTestPreparation(var1, var3);
      Rotation var5 = StructureUtils.getRotationForRotationSteps(var2);
      GameTestInfo var6 = new GameTestInfo(var1, var5, var3);
      if (!verifyStructureExists(var3, var6)) {
         return 0;
      } else {
         GameTestRunner.runTest(var6, var4, GameTestTicker.SINGLETON);
         return 1;
      }
   }

   private static BlockPos createTestPositionAround(CommandSourceStack var0) {
      BlockPos var1 = BlockPos.containing(var0.getPosition());
      int var2 = var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var1).getY();
      return new BlockPos(var1.getX(), var2 + 1, var1.getZ() + 3);
   }

   private static void runTestPreparation(TestFunction var0, ServerLevel var1) {
      Consumer var2 = GameTestRegistry.getBeforeBatchFunction(var0.getBatchName());
      if (var2 != null) {
         var2.accept(var1);
      }
   }

   private static int runAllTests(CommandSourceStack var0, int var1, int var2) {
      GameTestRunner.clearMarkers(var0.getLevel());
      Collection var3 = GameTestRegistry.getAllTestFunctions();
      say(var0, "Running all " + var3.size() + " tests...");
      GameTestRegistry.forgetFailedTests();
      runTests(var0, var3, var1, var2);
      return 1;
   }

   private static int runAllTestsInClass(CommandSourceStack var0, String var1, int var2, int var3) {
      Collection var4 = GameTestRegistry.getTestFunctionsForClassName(var1);
      GameTestRunner.clearMarkers(var0.getLevel());
      say(var0, "Running " + var4.size() + " tests from " + var1 + "...");
      GameTestRegistry.forgetFailedTests();
      runTests(var0, var4, var2, var3);
      return 1;
   }

   private static int runLastFailedTests(CommandSourceStack var0, boolean var1, int var2, int var3) {
      Collection var4;
      if (var1) {
         var4 = GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
      } else {
         var4 = GameTestRegistry.getLastFailedTests();
      }

      if (var4.isEmpty()) {
         say(var0, "No failed tests to rerun");
         return 0;
      } else {
         GameTestRunner.clearMarkers(var0.getLevel());
         say(var0, "Rerunning " + var4.size() + " failed tests (" + (var1 ? "only required tests" : "including optional tests") + ")");
         runTests(var0, var4, var2, var3);
         return 1;
      }
   }

   private static void runTests(CommandSourceStack var0, Collection<TestFunction> var1, int var2, int var3) {
      BlockPos var4 = createTestPositionAround(var0);
      ServerLevel var5 = var0.getLevel();
      Rotation var6 = StructureUtils.getRotationForRotationSteps(var2);
      Collection var7 = GameTestRunner.runTests(var1, var4, var6, var5, GameTestTicker.SINGLETON, var3);
      MultipleTestTracker var8 = new MultipleTestTracker(var7);
      var8.addListener(new TestCommand.TestSummaryDisplayer(var5, var8));
      var8.addFailureListener(var0x -> GameTestRegistry.rememberFailedTest(var0x.getTestFunction()));
   }

   private static void say(CommandSourceStack var0, String var1) {
      var0.sendSuccess(() -> Component.literal(var1), false);
   }

   private static int exportNearestTestStructure(CommandSourceStack var0) {
      BlockPos var1 = BlockPos.containing(var0.getPosition());
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = StructureUtils.findNearestStructureBlock(var1, 15, var2);
      if (var3 == null) {
         say(var2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         StructureBlockEntity var4 = (StructureBlockEntity)var2.getBlockEntity(var3);
         return saveAndExportTestStructure(var0, var4);
      }
   }

   private static int exportAllNearbyTests(CommandSourceStack var0) {
      BlockPos var1 = BlockPos.containing(var0.getPosition());
      ServerLevel var2 = var0.getLevel();
      Collection var3 = StructureUtils.findStructureBlocks(var1, 200, var2);
      if (var3.isEmpty()) {
         say(var2, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
         return 1;
      } else {
         boolean var4 = true;

         for(BlockPos var6 : var3) {
            StructureBlockEntity var7 = (StructureBlockEntity)var2.getBlockEntity(var6);
            if (saveAndExportTestStructure(var0, var7) != 0) {
               var4 = false;
            }
         }

         return var4 ? 0 : 1;
      }
   }

   private static int saveAndExportTestStructure(CommandSourceStack var0, StructureBlockEntity var1) {
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

   private static int importTestStructure(CommandSourceStack var0, String var1) {
      Path var2 = Paths.get(StructureUtils.testStructuresDir, var1 + ".snbt");
      ResourceLocation var3 = new ResourceLocation(var1);
      Path var4 = var0.getLevel().getStructureManager().getPathToGeneratedStructure(var3, ".nbt");

      try {
         BufferedReader var5 = Files.newBufferedReader(var2);
         String var6 = IOUtils.toString(var5);
         Files.createDirectories(var4.getParent());

         try (OutputStream var7 = Files.newOutputStream(var4)) {
            NbtIo.writeCompressed(NbtUtils.snbtToStructure(var6), var7);
         }

         say(var0, "Imported to " + var4.toAbsolutePath());
         return 0;
      } catch (CommandSyntaxException | IOException var12) {
         LOGGER.error("Failed to load structure {}", var1, var12);
         return 1;
      }
   }

   private static void say(ServerLevel var0, String var1, ChatFormatting var2) {
      var0.getPlayers(var0x -> true).forEach(var2x -> var2x.sendSystemMessage(Component.literal(var1).withStyle(var2)));
   }

   static class TestSummaryDisplayer implements GameTestListener {
      private final ServerLevel level;
      private final MultipleTestTracker tracker;

      public TestSummaryDisplayer(ServerLevel var1, MultipleTestTracker var2) {
         super();
         this.level = var1;
         this.tracker = var2;
      }

      @Override
      public void testStructureLoaded(GameTestInfo var1) {
      }

      @Override
      public void testPassed(GameTestInfo var1) {
         TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
      }

      @Override
      public void testFailed(GameTestInfo var1) {
         TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
      }
   }
}
