package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.RangeArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.command.impl.data.IDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.CustomBossEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType field_198421_a = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.execute.blocks.toobig", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType field_210456_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.execute.conditional.fail", new Object[0]));
   private static final DynamicCommandExceptionType field_210457_c = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.execute.conditional.fail_count", new Object[]{var0});
   });
   private static final BinaryOperator<ResultConsumer<CommandSource>> field_209957_b = (var0, var1) -> {
      return (var2, var3, var4) -> {
         var0.onCommandComplete(var2, var3, var4);
         var1.onCommandComplete(var2, var3, var4);
      };
   };

   public static void func_198378_a(CommandDispatcher<CommandSource> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)Commands.func_197057_a("execute").requires((var0x) -> {
         return var0x.func_197034_c(2);
      }));
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("execute").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("run").redirect(var0.getRoot()))).then(func_198394_a(var1, Commands.func_197057_a("if"), true))).then(func_198394_a(var1, Commands.func_197057_a("unless"), false))).then(Commands.func_197057_a("as").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.func_197087_c(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSource)var0x.getSource()).func_197024_a(var3));
         }

         return var1;
      })))).then(Commands.func_197057_a("at").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.func_197087_c(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSource)var0x.getSource()).func_201003_a((WorldServer)var3.field_70170_p).func_201009_a(var3.func_174791_d()).func_201007_a(var3.func_189653_aC()));
         }

         return var1;
      })))).then(((LiteralArgumentBuilder)Commands.func_197057_a("store").then(func_198392_a(var1, Commands.func_197057_a("result"), true))).then(func_198392_a(var1, Commands.func_197057_a("success"), false)))).then(((LiteralArgumentBuilder)Commands.func_197057_a("positioned").then(Commands.func_197056_a("pos", Vec3Argument.func_197301_a()).redirect(var1, (var0x) -> {
         return ((CommandSource)var0x.getSource()).func_201009_a(Vec3Argument.func_197300_a(var0x, "pos"));
      }))).then(Commands.func_197057_a("as").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.func_197087_c(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSource)var0x.getSource()).func_201009_a(var3.func_174791_d()));
         }

         return var1;
      }))))).then(((LiteralArgumentBuilder)Commands.func_197057_a("rotated").then(Commands.func_197056_a("rot", RotationArgument.func_197288_a()).redirect(var1, (var0x) -> {
         return ((CommandSource)var0x.getSource()).func_201007_a(RotationArgument.func_200384_a(var0x, "rot").func_197282_b((CommandSource)var0x.getSource()));
      }))).then(Commands.func_197057_a("as").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = EntityArgument.func_197087_c(var0x, "targets").iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var1.add(((CommandSource)var0x.getSource()).func_201007_a(var3.func_189653_aC()));
         }

         return var1;
      }))))).then(((LiteralArgumentBuilder)Commands.func_197057_a("facing").then(Commands.func_197057_a("entity").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).then(Commands.func_197056_a("anchor", EntityAnchorArgument.func_201024_a()).fork(var1, (var0x) -> {
         ArrayList var1 = Lists.newArrayList();
         EntityAnchorArgument.Type var2 = EntityAnchorArgument.func_201023_a(var0x, "anchor");
         Iterator var3 = EntityArgument.func_197087_c(var0x, "targets").iterator();

         while(var3.hasNext()) {
            Entity var4 = (Entity)var3.next();
            var1.add(((CommandSource)var0x.getSource()).func_201006_a(var4, var2));
         }

         return var1;
      }))))).then(Commands.func_197056_a("pos", Vec3Argument.func_197301_a()).redirect(var1, (var0x) -> {
         return ((CommandSource)var0x.getSource()).func_201005_b(Vec3Argument.func_197300_a(var0x, "pos"));
      })))).then(Commands.func_197057_a("align").then(Commands.func_197056_a("axes", SwizzleArgument.func_197293_a()).redirect(var1, (var0x) -> {
         return ((CommandSource)var0x.getSource()).func_201009_a(((CommandSource)var0x.getSource()).func_197036_d().func_197746_a(SwizzleArgument.func_197291_a(var0x, "axes")));
      })))).then(Commands.func_197057_a("anchored").then(Commands.func_197056_a("anchor", EntityAnchorArgument.func_201024_a()).redirect(var1, (var0x) -> {
         return ((CommandSource)var0x.getSource()).func_201010_a(EntityAnchorArgument.func_201023_a(var0x, "anchor"));
      })))).then(Commands.func_197057_a("in").then(Commands.func_197056_a("dimension", DimensionArgument.func_212595_a()).redirect(var1, (var0x) -> {
         return ((CommandSource)var0x.getSource()).func_201003_a(((CommandSource)var0x.getSource()).func_197028_i().func_71218_a(DimensionArgument.func_212592_a(var0x, "dimension")));
      }))));
   }

   private static ArgumentBuilder<CommandSource, ?> func_198392_a(LiteralCommandNode<CommandSource> var0, LiteralArgumentBuilder<CommandSource> var1, boolean var2) {
      var1.then(Commands.func_197057_a("score").then(Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).redirect(var0, (var1x) -> {
         return func_209930_a((CommandSource)var1x.getSource(), ScoreHolderArgument.func_211707_c(var1x, "targets"), ObjectiveArgument.func_197158_a(var1x, "objective"), var2);
      }))));
      var1.then(Commands.func_197057_a("bossbar").then(((RequiredArgumentBuilder)Commands.func_197056_a("id", ResourceLocationArgument.func_197197_a()).suggests(BossBarCommand.field_201431_a).then(Commands.func_197057_a("value").redirect(var0, (var1x) -> {
         return func_209952_a((CommandSource)var1x.getSource(), BossBarCommand.func_201416_a(var1x), true, var2);
      }))).then(Commands.func_197057_a("max").redirect(var0, (var1x) -> {
         return func_209952_a((CommandSource)var1x.getSource(), BossBarCommand.func_201416_a(var1x), false, var2);
      }))));
      Iterator var3 = DataCommand.field_198948_a.iterator();

      while(var3.hasNext()) {
         DataCommand.IDataProvider var4 = (DataCommand.IDataProvider)var3.next();
         var4.func_198920_a(var1, (var3x) -> {
            return var3x.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("path", NBTPathArgument.func_197149_a()).then(Commands.func_197057_a("int").then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return func_198397_a((CommandSource)var2x.getSource(), var4.func_198919_a(var2x), NBTPathArgument.func_197148_a(var2x, "path"), (var1) -> {
                  return new NBTTagInt((int)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale")));
               }, var2);
            })))).then(Commands.func_197057_a("float").then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return func_198397_a((CommandSource)var2x.getSource(), var4.func_198919_a(var2x), NBTPathArgument.func_197148_a(var2x, "path"), (var1) -> {
                  return new NBTTagFloat((float)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale")));
               }, var2);
            })))).then(Commands.func_197057_a("short").then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return func_198397_a((CommandSource)var2x.getSource(), var4.func_198919_a(var2x), NBTPathArgument.func_197148_a(var2x, "path"), (var1) -> {
                  return new NBTTagShort((short)((int)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale"))));
               }, var2);
            })))).then(Commands.func_197057_a("long").then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return func_198397_a((CommandSource)var2x.getSource(), var4.func_198919_a(var2x), NBTPathArgument.func_197148_a(var2x, "path"), (var1) -> {
                  return new NBTTagLong((long)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale")));
               }, var2);
            })))).then(Commands.func_197057_a("double").then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return func_198397_a((CommandSource)var2x.getSource(), var4.func_198919_a(var2x), NBTPathArgument.func_197148_a(var2x, "path"), (var1) -> {
                  return new NBTTagDouble((double)var1 * DoubleArgumentType.getDouble(var2x, "scale"));
               }, var2);
            })))).then(Commands.func_197057_a("byte").then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).redirect(var0, (var2x) -> {
               return func_198397_a((CommandSource)var2x.getSource(), var4.func_198919_a(var2x), NBTPathArgument.func_197148_a(var2x, "path"), (var1) -> {
                  return new NBTTagByte((byte)((int)((double)var1 * DoubleArgumentType.getDouble(var2x, "scale"))));
               }, var2);
            }))));
         });
      }

      return var1;
   }

   private static CommandSource func_209930_a(CommandSource var0, Collection<String> var1, ScoreObjective var2, boolean var3) {
      ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
      return var0.func_209550_a((var4x, var5, var6) -> {
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            Score var9 = var4.func_96529_a(var8, var2);
            int var10 = var3 ? var6 : (var5 ? 1 : 0);
            var9.func_96647_c(var10);
         }

      }, field_209957_b);
   }

   private static CommandSource func_209952_a(CommandSource var0, CustomBossEvent var1, boolean var2, boolean var3) {
      return var0.func_209550_a((var3x, var4, var5) -> {
         int var6 = var3 ? var5 : (var4 ? 1 : 0);
         if (var2) {
            var1.func_201362_a(var6);
         } else {
            var1.func_201366_b(var6);
         }

      }, field_209957_b);
   }

   private static CommandSource func_198397_a(CommandSource var0, IDataAccessor var1, NBTPathArgument.NBTPath var2, IntFunction<INBTBase> var3, boolean var4) {
      return var0.func_209550_a((var4x, var5, var6) -> {
         try {
            NBTTagCompound var7 = var1.func_198923_a();
            int var8 = var4 ? var6 : (var5 ? 1 : 0);
            var2.func_197142_a(var7, (INBTBase)var3.apply(var8));
            var1.func_198925_a(var7);
         } catch (CommandSyntaxException var9) {
         }

      }, field_209957_b);
   }

   private static ArgumentBuilder<CommandSource, ?> func_198394_a(CommandNode<CommandSource> var0, LiteralArgumentBuilder<CommandSource> var1, boolean var2) {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(Commands.func_197057_a("block").then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a()).then(func_210415_a(var0, Commands.func_197056_a("block", BlockPredicateArgument.func_199824_a()), var2, (var0x) -> {
         return BlockPredicateArgument.func_199825_a(var0x, "block").test(new BlockWorldState(((CommandSource)var0x.getSource()).func_197023_e(), BlockPosArgument.func_197273_a(var0x, "pos"), true));
      }))))).then(Commands.func_197057_a("score").then(Commands.func_197056_a("target", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("targetObjective", ObjectiveArgument.func_197157_a()).then(Commands.func_197057_a("=").then(Commands.func_197056_a("source", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(var0, Commands.func_197056_a("sourceObjective", ObjectiveArgument.func_197157_a()), var2, (var0x) -> {
         return func_198371_a(var0x, Integer::equals);
      }))))).then(Commands.func_197057_a("<").then(Commands.func_197056_a("source", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(var0, Commands.func_197056_a("sourceObjective", ObjectiveArgument.func_197157_a()), var2, (var0x) -> {
         return func_198371_a(var0x, (var0, var1) -> {
            return var0 < var1;
         });
      }))))).then(Commands.func_197057_a("<=").then(Commands.func_197056_a("source", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(var0, Commands.func_197056_a("sourceObjective", ObjectiveArgument.func_197157_a()), var2, (var0x) -> {
         return func_198371_a(var0x, (var0, var1) -> {
            return var0 <= var1;
         });
      }))))).then(Commands.func_197057_a(">").then(Commands.func_197056_a("source", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(var0, Commands.func_197056_a("sourceObjective", ObjectiveArgument.func_197157_a()), var2, (var0x) -> {
         return func_198371_a(var0x, (var0, var1) -> {
            return var0 > var1;
         });
      }))))).then(Commands.func_197057_a(">=").then(Commands.func_197056_a("source", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(func_210415_a(var0, Commands.func_197056_a("sourceObjective", ObjectiveArgument.func_197157_a()), var2, (var0x) -> {
         return func_198371_a(var0x, (var0, var1) -> {
            return var0 >= var1;
         });
      }))))).then(Commands.func_197057_a("matches").then(func_210415_a(var0, Commands.func_197056_a("range", RangeArgument.func_211371_a()), var2, (var0x) -> {
         return func_201115_a(var0x, RangeArgument.IntRange.func_211372_a(var0x, "range"));
      }))))))).then(Commands.func_197057_a("blocks").then(Commands.func_197056_a("start", BlockPosArgument.func_197276_a()).then(Commands.func_197056_a("end", BlockPosArgument.func_197276_a()).then(((RequiredArgumentBuilder)Commands.func_197056_a("destination", BlockPosArgument.func_197276_a()).then(func_212178_a(var0, Commands.func_197057_a("all"), var2, false))).then(func_212178_a(var0, Commands.func_197057_a("masked"), var2, true))))))).then(Commands.func_197057_a("entity").then(((RequiredArgumentBuilder)Commands.func_197056_a("entities", EntityArgument.func_197093_b()).fork(var0, (var1x) -> {
         return func_198411_a(var1x, var2, !EntityArgument.func_197087_c(var1x, "entities").isEmpty());
      })).executes(var2 ? (var0x) -> {
         int var1 = EntityArgument.func_197087_c(var0x, "entities").size();
         if (var1 > 0) {
            ((CommandSource)var0x.getSource()).func_197030_a(new TextComponentTranslation("commands.execute.conditional.pass_count", new Object[]{var1}), false);
            return var1;
         } else {
            throw field_210456_b.create();
         }
      } : (var0x) -> {
         int var1 = EntityArgument.func_197087_c(var0x, "entities").size();
         if (var1 == 0) {
            ((CommandSource)var0x.getSource()).func_197030_a(new TextComponentTranslation("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw field_210457_c.create(var1);
         }
      })));
   }

   private static boolean func_198371_a(CommandContext<CommandSource> var0, BiPredicate<Integer, Integer> var1) throws CommandSyntaxException {
      String var2 = ScoreHolderArgument.func_197211_a(var0, "target");
      ScoreObjective var3 = ObjectiveArgument.func_197158_a(var0, "targetObjective");
      String var4 = ScoreHolderArgument.func_197211_a(var0, "source");
      ScoreObjective var5 = ObjectiveArgument.func_197158_a(var0, "sourceObjective");
      ServerScoreboard var6 = ((CommandSource)var0.getSource()).func_197028_i().func_200251_aP();
      if (var6.func_178819_b(var2, var3) && var6.func_178819_b(var4, var5)) {
         Score var7 = var6.func_96529_a(var2, var3);
         Score var8 = var6.func_96529_a(var4, var5);
         return var1.test(var7.func_96652_c(), var8.func_96652_c());
      } else {
         return false;
      }
   }

   private static boolean func_201115_a(CommandContext<CommandSource> var0, MinMaxBounds.IntBound var1) throws CommandSyntaxException {
      String var2 = ScoreHolderArgument.func_197211_a(var0, "target");
      ScoreObjective var3 = ObjectiveArgument.func_197158_a(var0, "targetObjective");
      ServerScoreboard var4 = ((CommandSource)var0.getSource()).func_197028_i().func_200251_aP();
      return !var4.func_178819_b(var2, var3) ? false : var1.func_211339_d(var4.func_96529_a(var2, var3).func_96652_c());
   }

   private static Collection<CommandSource> func_198411_a(CommandContext<CommandSource> var0, boolean var1, boolean var2) {
      return (Collection)(var2 == var1 ? Collections.singleton(var0.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSource, ?> func_210415_a(CommandNode<CommandSource> var0, ArgumentBuilder<CommandSource, ?> var1, boolean var2, ExecuteCommand.ExecuteTest var3) {
      return var1.fork(var0, (var2x) -> {
         return func_198411_a(var2x, var2, var3.test(var2x));
      }).executes((var2x) -> {
         if (var2 == var3.test(var2x)) {
            ((CommandSource)var2x.getSource()).func_197030_a(new TextComponentTranslation("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw field_210456_b.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSource, ?> func_212178_a(CommandNode<CommandSource> var0, ArgumentBuilder<CommandSource, ?> var1, boolean var2, boolean var3) {
      return var1.fork(var0, (var2x) -> {
         return func_198411_a(var2x, var2, func_212169_c(var2x, var3).isPresent());
      }).executes(var2 ? (var1x) -> {
         return func_212175_a(var1x, var3);
      } : (var1x) -> {
         return func_212173_b(var1x, var3);
      });
   }

   private static int func_212175_a(CommandContext<CommandSource> var0, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = func_212169_c(var0, var1);
      if (var2.isPresent()) {
         ((CommandSource)var0.getSource()).func_197030_a(new TextComponentTranslation("commands.execute.conditional.pass_count", new Object[]{var2.getAsInt()}), false);
         return var2.getAsInt();
      } else {
         throw field_210456_b.create();
      }
   }

   private static int func_212173_b(CommandContext<CommandSource> var0, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = func_212169_c(var0, var1);
      if (!var2.isPresent()) {
         ((CommandSource)var0.getSource()).func_197030_a(new TextComponentTranslation("commands.execute.conditional.pass", new Object[0]), false);
         return 1;
      } else {
         throw field_210457_c.create(var2.getAsInt());
      }
   }

   private static OptionalInt func_212169_c(CommandContext<CommandSource> var0, boolean var1) throws CommandSyntaxException {
      return func_198395_a(((CommandSource)var0.getSource()).func_197023_e(), BlockPosArgument.func_197273_a(var0, "start"), BlockPosArgument.func_197273_a(var0, "end"), BlockPosArgument.func_197273_a(var0, "destination"), var1);
   }

   private static OptionalInt func_198395_a(WorldServer var0, BlockPos var1, BlockPos var2, BlockPos var3, boolean var4) throws CommandSyntaxException {
      MutableBoundingBox var5 = new MutableBoundingBox(var1, var2);
      MutableBoundingBox var6 = new MutableBoundingBox(var3, var3.func_177971_a(var5.func_175896_b()));
      BlockPos var7 = new BlockPos(var6.field_78897_a - var5.field_78897_a, var6.field_78895_b - var5.field_78895_b, var6.field_78896_c - var5.field_78896_c);
      int var8 = var5.func_78883_b() * var5.func_78882_c() * var5.func_78880_d();
      if (var8 > 32768) {
         throw field_198421_a.create(32768, var8);
      } else {
         int var9 = 0;

         for(int var10 = var5.field_78896_c; var10 <= var5.field_78892_f; ++var10) {
            for(int var11 = var5.field_78895_b; var11 <= var5.field_78894_e; ++var11) {
               for(int var12 = var5.field_78897_a; var12 <= var5.field_78893_d; ++var12) {
                  BlockPos var13 = new BlockPos(var12, var11, var10);
                  BlockPos var14 = var13.func_177971_a(var7);
                  IBlockState var15 = var0.func_180495_p(var13);
                  if (!var4 || var15.func_177230_c() != Blocks.field_150350_a) {
                     if (var15 != var0.func_180495_p(var14)) {
                        return OptionalInt.empty();
                     }

                     TileEntity var16 = var0.func_175625_s(var13);
                     TileEntity var17 = var0.func_175625_s(var14);
                     if (var16 != null) {
                        if (var17 == null) {
                           return OptionalInt.empty();
                        }

                        NBTTagCompound var18 = var16.func_189515_b(new NBTTagCompound());
                        var18.func_82580_o("x");
                        var18.func_82580_o("y");
                        var18.func_82580_o("z");
                        NBTTagCompound var19 = var17.func_189515_b(new NBTTagCompound());
                        var19.func_82580_o("x");
                        var19.func_82580_o("y");
                        var19.func_82580_o("z");
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
   interface ExecuteTest {
      boolean test(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }
}
