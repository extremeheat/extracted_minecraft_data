package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionTypeArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.execute.blocks.toobig", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.execute.conditional.fail", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.execute.conditional.fail_count", new Object[]{var0});
   });
   private static final BinaryOperator CALLBACK_CHAINER = (var0, var1) -> {
      return (var2, var3, var4) -> {
         var0.onCommandComplete(var2, var3, var4);
         var1.onCommandComplete(var2, var3, var4);
      };
   };
   private static final SuggestionProvider SUGGEST_PREDICATE = (var0, var1) -> {
      PredicateManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getPredicateManager();
      return SharedSuggestionProvider.suggestResource((Iterable)var2.getKeys(), var1);
   };

   public static void register(CommandDispatcher var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.literal("execute").requires((var0x) -> {
         return var0x.hasPermission(2);
      }));
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("execute").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("run").redirect(var0.getRoot()))).then(addConditionals(var1, Commands.literal("if"), true))).then(addConditionals(var1, Commands.literal("unless"), false))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.getOptionalEntities(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSourceStack)var0x.getSource()).withEntity(var3));
         }

         return var1;
      })))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.getOptionalEntities(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSourceStack)var0x.getSource()).withLevel((ServerLevel)var3.level).withPosition(var3.getCommandSenderWorldPosition()).withRotation(var3.getRotationVector()));
         }

         return var1;
      })))).then(((LiteralArgumentBuilder)Commands.literal("store").then(wrapStores(var1, Commands.literal("result"), true))).then(wrapStores(var1, Commands.literal("success"), false)))).then(((LiteralArgumentBuilder)Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(var1, (var0x) -> {
         return ((CommandSourceStack)var0x.getSource()).withPosition(Vec3Argument.getVec3(var0x, "pos")).withAnchor(EntityAnchorArgument.Anchor.FEET);
      }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.getOptionalEntities(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSourceStack)var0x.getSource()).withPosition(var3.getCommandSenderWorldPosition()));
         }

         return var1;
      }))))).then(((LiteralArgumentBuilder)Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(var1, (var0x) -> {
         return ((CommandSourceStack)var0x.getSource()).withRotation(RotationArgument.getRotation(var0x, "rot").getRotation((CommandSourceStack)var0x.getSource()));
      }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.getOptionalEntities(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSourceStack)var0x.getSource()).withRotation(var3.getRotationVector()));
         }

         return var1;
      }))))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         EntityAnchorArgument.Anchor var2 = EntityAnchorArgument.getAnchor(var0x, "anchor");
         Iterator var3 = EntityArgument.getOptionalEntities(var0x, "targets").iterator();

         while(var3.hasNext()) {
            Entity var4 = (Entity)var3.next();
            var1.add(((CommandSourceStack)var0x.getSource()).facing(var4, var2));
         }

         return var1;
      }))))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(var1, (var0x) -> {
         return ((CommandSourceStack)var0x.getSource()).facing(Vec3Argument.getVec3(var0x, "pos"));
      })))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(var1, (var0x) -> {
         return ((CommandSourceStack)var0x.getSource()).withPosition(((CommandSourceStack)var0x.getSource()).getPosition().align(SwizzleArgument.getSwizzle(var0x, "axes")));
      })))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect(var1, (var0x) -> {
         return ((CommandSourceStack)var0x.getSource()).withAnchor(EntityAnchorArgument.getAnchor(var0x, "anchor"));
      })))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionTypeArgument.dimension()).redirect(var1, (var0x) -> {
         return ((CommandSourceStack)var0x.getSource()).withLevel(((CommandSourceStack)var0x.getSource()).getServer().getLevel(DimensionTypeArgument.getDimension(var0x, "dimension")));
      }))));
   }

   private static ArgumentBuilder wrapStores(LiteralCommandNode var0, LiteralArgumentBuilder var1, boolean var2) {
      var1.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(var0, (var1x) -> {
         return storeValue((CommandSourceStack)var1x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var1x, "targets"), ObjectiveArgument.getObjective(var1x, "objective"), var2);
      }))));
      var1.then(Commands.literal("bossbar").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommands.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect(var0, (var1x) -> {
         return storeValue((CommandSourceStack)var1x.getSource(), BossBarCommands.getBossBar(var1x), true, var2);
      }))).then(Commands.literal("max").redirect(var0, (var1x) -> {
         return storeValue((CommandSourceStack)var1x.getSource(), BossBarCommands.getBossBar(var1x), false, var2);
      }))));
      Iterator var3 = DataCommands.TARGET_PROVIDERS.iterator();

      while(var3.hasNext()) {
         DataCommands.DataProvider var4 = (DataCommands.DataProvider)var3.next();
         var4.wrap(var1, (var3x) -> {
            return var3x.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return storeData((CommandSourceStack)var2x.getSource(), var4.access(var2x), NbtPathArgument.getPath(var2x, "path"), (var1) -> {
                  return IntTag.valueOf((int)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale")));
               }, var2);
            })))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return storeData((CommandSourceStack)var2x.getSource(), var4.access(var2x), NbtPathArgument.getPath(var2x, "path"), (var1) -> {
                  return FloatTag.valueOf((float)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale")));
               }, var2);
            })))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return storeData((CommandSourceStack)var2x.getSource(), var4.access(var2x), NbtPathArgument.getPath(var2x, "path"), (var1) -> {
                  return ShortTag.valueOf((short)((int)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale"))));
               }, var2);
            })))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return storeData((CommandSourceStack)var2x.getSource(), var4.access(var2x), NbtPathArgument.getPath(var2x, "path"), (var1) -> {
                  return LongTag.valueOf((long)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale")));
               }, var2);
            })))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return storeData((CommandSourceStack)var2x.getSource(), var4.access(var2x), NbtPathArgument.getPath(var2x, "path"), (var1) -> {
                  return DoubleTag.valueOf((double)var1 * DoubleArgumentType.getDouble(var2x, "scale"));
               }, var2);
            })))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return storeData((CommandSourceStack)var2x.getSource(), var4.access(var2x), NbtPathArgument.getPath(var2x, "path"), (var1) -> {
                  return ByteTag.valueOf((byte)((int)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale"))));
               }, var2);
            }))));
         });
      }

      return var1;
   }

   private static CommandSourceStack storeValue(CommandSourceStack var0, Collection var1, Objective var2, boolean var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      return var0.withCallback((var4x, var5, var6) -> {
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            Score var9 = var4.getOrCreatePlayerScore(var8, var2);
            int var10 = var3 ? var6 : (var5 ? 1 : 0);
            var9.setScore(var10);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSourceStack storeValue(CommandSourceStack var0, CustomBossEvent var1, boolean var2, boolean var3) {
      return var0.withCallback((var3x, var4, var5) -> {
         int var6 = var3 ? var5 : (var4 ? 1 : 0);
         if (var2) {
            var1.setValue(var6);
         } else {
            var1.setMax(var6);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSourceStack storeData(CommandSourceStack var0, DataAccessor var1, NbtPathArgument.NbtPath var2, IntFunction var3, boolean var4) {
      return var0.withCallback((var4x, var5, var6) -> {
         try {
            CompoundTag var7 = var1.getData();
            int var8 = var4 ? var6 : (var5 ? 1 : 0);
            var2.set(var7, () -> {
               return (Tag)var3.apply(var8);
            });
            var1.setData(var7);
         } catch (CommandSyntaxException var9) {
         }

      }, CALLBACK_CHAINER);
   }

   private static ArgumentBuilder addConditionals(CommandNode var0, LiteralArgumentBuilder var1, boolean var2) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(addConditional(var0, Commands.argument("block", BlockPredicateArgument.blockPredicate()), var2, (var0x) -> {
         return BlockPredicateArgument.getBlockPredicate(var0x, "block").test(new BlockInWorld(((CommandSourceStack)var0x.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), true));
      }))))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(var0, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (var0x) -> {
         return checkScore(var0x, Integer::equals);
      }))))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(var0, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (var0x) -> {
         return checkScore(var0x, (var0, var1) -> {
            return var0 < var1;
         });
      }))))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(var0, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (var0x) -> {
         return checkScore(var0x, (var0, var1) -> {
            return var0 <= var1;
         });
      }))))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(var0, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (var0x) -> {
         return checkScore(var0x, (var0, var1) -> {
            return var0 > var1;
         });
      }))))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(var0, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (var0x) -> {
         return checkScore(var0x, (var0, var1) -> {
            return var0 >= var1;
         });
      }))))).then(Commands.literal("matches").then(addConditional(var0, Commands.argument("range", RangeArgument.intRange()), var2, (var0x) -> {
         return checkScore(var0x, RangeArgument.Ints.getRange(var0x, "range"));
      }))))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).then(addIfBlocksConditional(var0, Commands.literal("all"), var2, false))).then(addIfBlocksConditional(var0, Commands.literal("masked"), var2, true))))))).then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities()).fork(var0, (var1x) -> {
         return expect(var1x, var2, !EntityArgument.getOptionalEntities(var1x, "entities").isEmpty());
      })).executes(createNumericConditionalHandler(var2, (var0x) -> {
         return EntityArgument.getOptionalEntities(var0x, "entities").size();
      }))))).then(Commands.literal("predicate").then(addConditional(var0, Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE), var2, (var0x) -> {
         return checkCustomPredicate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getPredicate(var0x, "predicate"));
      })));
      Iterator var3 = DataCommands.SOURCE_PROVIDERS.iterator();

      while(var3.hasNext()) {
         DataCommands.DataProvider var4 = (DataCommands.DataProvider)var3.next();
         var1.then(var4.wrap(Commands.literal("data"), (var3x) -> {
            return var3x.then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).fork(var0, (var2x) -> {
               return expect(var2x, var2, checkMatchingData(var4.access(var2x), NbtPathArgument.getPath(var2x, "path")) > 0);
            })).executes(createNumericConditionalHandler(var2, (var1) -> {
               return checkMatchingData(var4.access(var1), NbtPathArgument.getPath(var1, "path"));
            })));
         }));
      }

      return var1;
   }

   private static Command createNumericConditionalHandler(boolean var0, ExecuteCommand.CommandNumericPredicate var1) {
      return var0 ? (var1x) -> {
         int var2 = var1.test(var1x);
         if (var2 > 0) {
            ((CommandSourceStack)var1x.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", new Object[]{var2}), false);
            return var2;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      } : (var1x) -> {
         int var2 = var1.test(var1x);
         if (var2 == 0) {
            ((CommandSourceStack)var1x.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create(var2);
         }
      };
   }

   private static int checkMatchingData(DataAccessor var0, NbtPathArgument.NbtPath var1) throws CommandSyntaxException {
      return var1.countMatching(var0.getData());
   }

   private static boolean checkScore(CommandContext var0, BiPredicate var1) throws CommandSyntaxException {
      String var2 = ScoreHolderArgument.getName(var0, "target");
      Objective var3 = ObjectiveArgument.getObjective(var0, "targetObjective");
      String var4 = ScoreHolderArgument.getName(var0, "source");
      Objective var5 = ObjectiveArgument.getObjective(var0, "sourceObjective");
      ServerScoreboard var6 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      if (var6.hasPlayerScore(var2, var3) && var6.hasPlayerScore(var4, var5)) {
         Score var7 = var6.getOrCreatePlayerScore(var2, var3);
         Score var8 = var6.getOrCreatePlayerScore(var4, var5);
         return var1.test(var7.getScore(), var8.getScore());
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext var0, MinMaxBounds.Ints var1) throws CommandSyntaxException {
      String var2 = ScoreHolderArgument.getName(var0, "target");
      Objective var3 = ObjectiveArgument.getObjective(var0, "targetObjective");
      ServerScoreboard var4 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      return !var4.hasPlayerScore(var2, var3) ? false : var1.matches(var4.getOrCreatePlayerScore(var2, var3).getScore());
   }

   private static boolean checkCustomPredicate(CommandSourceStack var0, LootItemCondition var1) {
      ServerLevel var2 = var0.getLevel();
      LootContext.Builder var3 = (new LootContext.Builder(var2)).withParameter(LootContextParams.BLOCK_POS, new BlockPos(var0.getPosition())).withOptionalParameter(LootContextParams.THIS_ENTITY, var0.getEntity());
      return var1.test(var3.create(LootContextParamSets.COMMAND));
   }

   private static Collection expect(CommandContext var0, boolean var1, boolean var2) {
      return (Collection)(var2 == var1 ? Collections.singleton(var0.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder addConditional(CommandNode var0, ArgumentBuilder var1, boolean var2, ExecuteCommand.CommandPredicate var3) {
      return var1.fork(var0, (var2x) -> {
         return expect(var2x, var2, var3.test(var2x));
      }).executes((var2x) -> {
         if (var2 == var3.test(var2x)) {
            ((CommandSourceStack)var2x.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder addIfBlocksConditional(CommandNode var0, ArgumentBuilder var1, boolean var2, boolean var3) {
      return var1.fork(var0, (var2x) -> {
         return expect(var2x, var2, checkRegions(var2x, var3).isPresent());
      }).executes(var2 ? (var1x) -> {
         return checkIfRegions(var1x, var3);
      } : (var1x) -> {
         return checkUnlessRegions(var1x, var3);
      });
   }

   private static int checkIfRegions(CommandContext var0, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = checkRegions(var0, var1);
      if (var2.isPresent()) {
         ((CommandSourceStack)var0.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", new Object[]{var2.getAsInt()}), false);
         return var2.getAsInt();
      } else {
         throw ERROR_CONDITIONAL_FAILED.create();
      }
   }

   private static int checkUnlessRegions(CommandContext var0, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = checkRegions(var0, var1);
      if (var2.isPresent()) {
         throw ERROR_CONDITIONAL_FAILED_COUNT.create(var2.getAsInt());
      } else {
         ((CommandSourceStack)var0.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass", new Object[0]), false);
         return 1;
      }
   }

   private static OptionalInt checkRegions(CommandContext var0, boolean var1) throws CommandSyntaxException {
      return checkRegions(((CommandSourceStack)var0.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(var0, "start"), BlockPosArgument.getLoadedBlockPos(var0, "end"), BlockPosArgument.getLoadedBlockPos(var0, "destination"), var1);
   }

   private static OptionalInt checkRegions(ServerLevel var0, BlockPos var1, BlockPos var2, BlockPos var3, boolean var4) throws CommandSyntaxException {
      BoundingBox var5 = new BoundingBox(var1, var2);
      BoundingBox var6 = new BoundingBox(var3, var3.offset(var5.getLength()));
      BlockPos var7 = new BlockPos(var6.x0 - var5.x0, var6.y0 - var5.y0, var6.z0 - var5.z0);
      int var8 = var5.getXSpan() * var5.getYSpan() * var5.getZSpan();
      if (var8 > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, var8);
      } else {
         int var9 = 0;

         for(int var10 = var5.z0; var10 <= var5.z1; ++var10) {
            for(int var11 = var5.y0; var11 <= var5.y1; ++var11) {
               for(int var12 = var5.x0; var12 <= var5.x1; ++var12) {
                  BlockPos var13 = new BlockPos(var12, var11, var10);
                  BlockPos var14 = var13.offset(var7);
                  BlockState var15 = var0.getBlockState(var13);
                  if (!var4 || var15.getBlock() != Blocks.AIR) {
                     if (var15 != var0.getBlockState(var14)) {
                        return OptionalInt.empty();
                     }

                     BlockEntity var16 = var0.getBlockEntity(var13);
                     BlockEntity var17 = var0.getBlockEntity(var14);
                     if (var16 != null) {
                        if (var17 == null) {
                           return OptionalInt.empty();
                        }

                        CompoundTag var18 = var16.save(new CompoundTag());
                        var18.remove("x");
                        var18.remove("y");
                        var18.remove("z");
                        CompoundTag var19 = var17.save(new CompoundTag());
                        var19.remove("x");
                        var19.remove("y");
                        var19.remove("z");
                        if (!var18.equals(var19)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++var9;
                  }
               }
            }
         }

         return OptionalInt.of(var9);
      }
   }

   @FunctionalInterface
   interface CommandNumericPredicate {
      int test(CommandContext var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface CommandPredicate {
      boolean test(CommandContext var1) throws CommandSyntaxException;
   }
}
