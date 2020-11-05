package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;

public class TestCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("test").then(Commands.literal("runthis").executes((var0x) -> {
         return runNearbyTest((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("runthese").executes((var0x) -> {
         return runAllNearbyTests((CommandSourceStack)var0x.getSource());
      }))).then(((LiteralArgumentBuilder)Commands.literal("runfailed").executes((var0x) -> {
         return runLastFailedTests((CommandSourceStack)var0x.getSource(), false, 0, 8);
      })).then(((RequiredArgumentBuilder)Commands.argument("onlyRequiredTests", BoolArgumentType.bool()).executes((var0x) -> {
         return runLastFailedTests((CommandSourceStack)var0x.getSource(), BoolArgumentType.getBool(var0x, "onlyRequiredTests"), 0, 8);
      })).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((var0x) -> {
         return runLastFailedTests((CommandSourceStack)var0x.getSource(), BoolArgumentType.getBool(var0x, "onlyRequiredTests"), IntegerArgumentType.getInteger(var0x, "rotationSteps"), 8);
      })).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((var0x) -> {
         return runLastFailedTests((CommandSourceStack)var0x.getSource(), BoolArgumentType.getBool(var0x, "onlyRequiredTests"), IntegerArgumentType.getInteger(var0x, "rotationSteps"), IntegerArgumentType.getInteger(var0x, "testsPerRow"));
      })))))).then(Commands.literal("run").then(((RequiredArgumentBuilder)Commands.argument("testName", TestFunctionArgument.testFunctionArgument()).executes((var0x) -> {
         return runTest((CommandSourceStack)var0x.getSource(), TestFunctionArgument.getTestFunction(var0x, "testName"), 0);
      })).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((var0x) -> {
         return runTest((CommandSourceStack)var0x.getSource(), TestFunctionArgument.getTestFunction(var0x, "testName"), IntegerArgumentType.getInteger(var0x, "rotationSteps"));
      }))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("runall").executes((var0x) -> {
         return runAllTests((CommandSourceStack)var0x.getSource(), 0, 8);
      })).then(((RequiredArgumentBuilder)Commands.argument("testClassName", TestClassNameArgument.testClassName()).executes((var0x) -> {
         return runAllTestsInClass((CommandSourceStack)var0x.getSource(), TestClassNameArgument.getTestClassName(var0x, "testClassName"), 0, 8);
      })).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((var0x) -> {
         return runAllTestsInClass((CommandSourceStack)var0x.getSource(), TestClassNameArgument.getTestClassName(var0x, "testClassName"), IntegerArgumentType.getInteger(var0x, "rotationSteps"), 8);
      })).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((var0x) -> {
         return runAllTestsInClass((CommandSourceStack)var0x.getSource(), TestClassNameArgument.getTestClassName(var0x, "testClassName"), IntegerArgumentType.getInteger(var0x, "rotationSteps"), IntegerArgumentType.getInteger(var0x, "testsPerRow"));
      }))))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((var0x) -> {
         return runAllTests((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "rotationSteps"), 8);
      })).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((var0x) -> {
         return runAllTests((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "rotationSteps"), IntegerArgumentType.getInteger(var0x, "testsPerRow"));
      }))))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes((var0x) -> {
         return exportTestStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"));
      })))).then(Commands.literal("exportthis").executes((var0x) -> {
         return exportNearestTestStructure((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes((var0x) -> {
         return importTestStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("pos").executes((var0x) -> {
         return showPos((CommandSourceStack)var0x.getSource(), "pos");
      })).then(Commands.argument("var", StringArgumentType.word()).executes((var0x) -> {
         return showPos((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "var"));
      })))).then(Commands.literal("create").then(((RequiredArgumentBuilder)Commands.argument("testName", StringArgumentType.word()).executes((var0x) -> {
         return createNewStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"), 5, 5, 5);
      })).then(((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer()).executes((var0x) -> {
         return createNewStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"), IntegerArgumentType.getInteger(var0x, "width"), IntegerArgumentType.getInteger(var0x, "width"), IntegerArgumentType.getInteger(var0x, "width"));
      })).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes((var0x) -> {
         return createNewStructure((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "testName"), IntegerArgumentType.getInteger(var0x, "width"), IntegerArgumentType.getInteger(var0x, "height"), IntegerArgumentType.getInteger(var0x, "depth"));
      }))))))).then(((LiteralArgumentBuilder)Commands.literal("clearall").executes((var0x) -> {
         return clearAllTests((CommandSourceStack)var0x.getSource(), 200);
      })).then(Commands.argument("radius", IntegerArgumentType.integer()).executes((var0x) -> {
         return clearAllTests((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "radius"));
      }))));
   }

   private static int createNewStructure(CommandSourceStack var0, String var1, int var2, int var3, int var4) {
      if (var2 <= 48 && var3 <= 48 && var4 <= 48) {
         ServerLevel var5 = var0.getLevel();
         BlockPos var6 = new BlockPos(var0.getPosition());
         BlockPos var7 = new BlockPos(var6.getX(), var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var6).getY(), var6.getZ() + 3);
         StructureUtils.createNewEmptyStructureBlock(var1.toLowerCase(), var7, new BlockPos(var2, var3, var4), Rotation.NONE, var5);

         for(int var8 = 0; var8 < var2; ++var8) {
            for(int var9 = 0; var9 < var4; ++var9) {
               BlockPos var10 = new BlockPos(var7.getX() + var8, var7.getY() + 1, var7.getZ() + var9);
               Block var11 = Blocks.POLISHED_ANDESITE;
               BlockInput var12 = new BlockInput(var11.defaultBlockState(), Collections.EMPTY_SET, (CompoundTag)null);
               var12.place(var5, var10, 2);
            }
         }

         StructureUtils.addCommandBlockAndButtonToStartTest(var7, new BlockPos(1, 0, -1), Rotation.NONE, var5);
         return 0;
      } else {
         throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
      }
   }

   private static int showPos(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      BlockHitResult var2 = (BlockHitResult)var0.getPlayerOrException().pick(10.0D, 1.0F, false);
      BlockPos var3 = var2.getBlockPos();
      ServerLevel var4 = var0.getLevel();
      Optional var5 = StructureUtils.findStructureBlockContainingPos(var3, 15, var4);
      if (!var5.isPresent()) {
         var5 = StructureUtils.findStructureBlockContainingPos(var3, 200, var4);
      }

      if (!var5.isPresent()) {
         var0.sendFailure(new TextComponent("Can't find a structure block that contains the targeted pos " + var3));
         return 0;
      } else {
         StructureBlockEntity var6 = (StructureBlockEntity)var4.getBlockEntity((BlockPos)var5.get());
         BlockPos var7 = var3.subtract((Vec3i)var5.get());
         String var8 = var7.getX() + ", " + var7.getY() + ", " + var7.getZ();
         String var9 = var6.getStructurePath();
         MutableComponent var10 = (new TextComponent(var8)).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + var1 + " = new BlockPos(" + var8 + ");")));
         var0.sendSuccess((new TextComponent("Position relative to " + var9 + ": ")).append(var10), false);
         DebugPackets.sendGameTestAddMarker(var4, new BlockPos(var3), var8, -2147418368, 10000);
         return 1;
      }
   }

   private static int runNearbyTest(CommandSourceStack var0) {
      BlockPos var1 = new BlockPos(var0.getPosition());
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = StructureUtils.findNearestStructureBlock(var1, 15, var2);
      if (var3 == null) {
         say(var2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         GameTestRunner.clearMarkers(var2);
         runTest(var2, var3, (MultipleTestTracker)null);
         return 1;
      }
   }

   private static int runAllNearbyTests(CommandSourceStack var0) {
      BlockPos var1 = new BlockPos(var0.getPosition());
      ServerLevel var2 = var0.getLevel();
      Collection var3 = StructureUtils.findStructureBlocks(var1, 200, var2);
      if (var3.isEmpty()) {
         say(var2, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
         return 1;
      } else {
         GameTestRunner.clearMarkers(var2);
         say(var0, "Running " + var3.size() + " tests...");
         MultipleTestTracker var4 = new MultipleTestTracker();
         var3.forEach((var2x) -> {
            runTest(var2, var2x, var4);
         });
         return 1;
      }
   }

   private static void runTest(ServerLevel var0, BlockPos var1, @Nullable MultipleTestTracker var2) {
      StructureBlockEntity var3 = (StructureBlockEntity)var0.getBlockEntity(var1);
      String var4 = var3.getStructurePath();
      TestFunction var5 = GameTestRegistry.getTestFunction(var4);
      GameTestInfo var6 = new GameTestInfo(var5, var3.getRotation(), var0);
      if (var2 != null) {
         var2.addTestToTrack(var6);
         var6.addListener(new TestCommand.TestSummaryDisplayer(var0, var2));
      }

      runTestPreparation(var5, var0);
      AABB var7 = StructureUtils.getStructureBounds(var3);
      BlockPos var8 = new BlockPos(var7.minX, var7.minY, var7.minZ);
      GameTestRunner.runTest(var6, var8, GameTestTicker.singleton);
   }

   private static void showTestSummaryIfAllDone(ServerLevel var0, MultipleTestTracker var1) {
      if (var1.isDone()) {
         say(var0, "GameTest done! " + var1.getTotalCount() + " tests were run", ChatFormatting.WHITE);
         if (var1.hasFailedRequired()) {
            say(var0, "" + var1.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
         } else {
            say(var0, "All required tests passed :)", ChatFormatting.GREEN);
         }

         if (var1.hasFailedOptional()) {
            say(var0, "" + var1.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
         }
      }

   }

   private static int clearAllTests(CommandSourceStack var0, int var1) {
      ServerLevel var2 = var0.getLevel();
      GameTestRunner.clearMarkers(var2);
      BlockPos var3 = new BlockPos(var0.getPosition().x, (double)var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(var0.getPosition())).getY(), var0.getPosition().z);
      GameTestRunner.clearAllTests(var2, var3, GameTestTicker.singleton, Mth.clamp(var1, 0, 1024));
      return 1;
   }

   private static int runTest(CommandSourceStack var0, TestFunction var1, int var2) {
      ServerLevel var3 = var0.getLevel();
      BlockPos var4 = new BlockPos(var0.getPosition());
      int var5 = var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var4).getY();
      BlockPos var6 = new BlockPos(var4.getX(), var5, var4.getZ() + 3);
      GameTestRunner.clearMarkers(var3);
      runTestPreparation(var1, var3);
      Rotation var7 = StructureUtils.getRotationForRotationSteps(var2);
      GameTestInfo var8 = new GameTestInfo(var1, var7, var3);
      GameTestRunner.runTest(var8, var6, GameTestTicker.singleton);
      return 1;
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
         var4 = (Collection)GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
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
      BlockPos var4 = new BlockPos(var0.getPosition());
      BlockPos var5 = new BlockPos(var4.getX(), var0.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var4).getY(), var4.getZ() + 3);
      ServerLevel var6 = var0.getLevel();
      Rotation var7 = StructureUtils.getRotationForRotationSteps(var2);
      Collection var8 = GameTestRunner.runTests(var1, var5, var7, var6, GameTestTicker.singleton, var3);
      MultipleTestTracker var9 = new MultipleTestTracker(var8);
      var9.addListener(new TestCommand.TestSummaryDisplayer(var6, var9));
      var9.addFailureListener((var0x) -> {
         GameTestRegistry.rememberFailedTest(var0x.getTestFunction());
      });
   }

   private static void say(CommandSourceStack var0, String var1) {
      var0.sendSuccess(new TextComponent(var1), false);
   }

   private static int exportNearestTestStructure(CommandSourceStack var0) {
      BlockPos var1 = new BlockPos(var0.getPosition());
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = StructureUtils.findNearestStructureBlock(var1, 15, var2);
      if (var3 == null) {
         say(var2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         StructureBlockEntity var4 = (StructureBlockEntity)var2.getBlockEntity(var3);
         String var5 = var4.getStructurePath();
         return exportTestStructure(var0, var5);
      }
   }

   private static int exportTestStructure(CommandSourceStack var0, String var1) {
      Path var2 = Paths.get(StructureUtils.testStructuresDir);
      ResourceLocation var3 = new ResourceLocation("minecraft", var1);
      Path var4 = var0.getLevel().getStructureManager().createPathToStructure(var3, ".nbt");
      Path var5 = NbtToSnbt.convertStructure(var4, var1, var2);
      if (var5 == null) {
         say(var0, "Failed to export " + var4);
         return 1;
      } else {
         try {
            Files.createDirectories(var5.getParent());
         } catch (IOException var7) {
            say(var0, "Could not create folder " + var5.getParent());
            var7.printStackTrace();
            return 1;
         }

         say(var0, "Exported " + var1 + " to " + var5.toAbsolutePath());
         return 0;
      }
   }

   private static int importTestStructure(CommandSourceStack var0, String var1) {
      Path var2 = Paths.get(StructureUtils.testStructuresDir, var1 + ".snbt");
      ResourceLocation var3 = new ResourceLocation("minecraft", var1);
      Path var4 = var0.getLevel().getStructureManager().createPathToStructure(var3, ".nbt");

      try {
         BufferedReader var5 = Files.newBufferedReader(var2);
         String var6 = IOUtils.toString(var5);
         Files.createDirectories(var4.getParent());
         OutputStream var7 = Files.newOutputStream(var4);
         Throwable var8 = null;

         try {
            NbtIo.writeCompressed(TagParser.parseTag(var6), var7);
         } catch (Throwable var18) {
            var8 = var18;
            throw var18;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var17) {
                     var8.addSuppressed(var17);
                  }
               } else {
                  var7.close();
               }
            }

         }

         say(var0, "Imported to " + var4.toAbsolutePath());
         return 0;
      } catch (CommandSyntaxException | IOException var20) {
         System.err.println("Failed to load structure " + var1);
         var20.printStackTrace();
         return 1;
      }
   }

   private static void say(ServerLevel var0, String var1, ChatFormatting var2) {
      var0.getPlayers((var0x) -> {
         return true;
      }).forEach((var2x) -> {
         var2x.sendMessage(new TextComponent(var2 + var1), Util.NIL_UUID);
      });
   }

   static class TestSummaryDisplayer implements GameTestListener {
      private final ServerLevel level;
      private final MultipleTestTracker tracker;

      public TestSummaryDisplayer(ServerLevel var1, MultipleTestTracker var2) {
         super();
         this.level = var1;
         this.tracker = var2;
      }

      public void testStructureLoaded(GameTestInfo var1) {
      }

      public void testFailed(GameTestInfo var1) {
         TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
      }
   }
}
