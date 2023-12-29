package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.PlayerTeam;

public class SpreadPlayersCommand {
   private static final int MAX_ITERATION_COUNT = 10000;
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType(
      (var0, var1, var2, var3) -> Component.translatableEscape("commands.spreadplayers.failed.teams", var0, var1, var2, var3)
   );
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType(
      (var0, var1, var2, var3) -> Component.translatableEscape("commands.spreadplayers.failed.entities", var0, var1, var2, var3)
   );
   private static final Dynamic2CommandExceptionType ERROR_INVALID_MAX_HEIGHT = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("commands.spreadplayers.failed.invalid.height", var0, var1)
   );

   public SpreadPlayersCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires(var0x -> var0x.hasPermission(2)))
            .then(
               Commands.argument("center", Vec2Argument.vec2())
                  .then(
                     Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F))
                        .then(
                           ((RequiredArgumentBuilder)Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F))
                                 .then(
                                    Commands.argument("respectTeams", BoolArgumentType.bool())
                                       .then(
                                          Commands.argument("targets", EntityArgument.entities())
                                             .executes(
                                                var0x -> spreadPlayers(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      Vec2Argument.getVec2(var0x, "center"),
                                                      FloatArgumentType.getFloat(var0x, "spreadDistance"),
                                                      FloatArgumentType.getFloat(var0x, "maxRange"),
                                                      ((CommandSourceStack)var0x.getSource()).getLevel().getMaxBuildHeight(),
                                                      BoolArgumentType.getBool(var0x, "respectTeams"),
                                                      EntityArgument.getEntities(var0x, "targets")
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("under")
                                    .then(
                                       Commands.argument("maxHeight", IntegerArgumentType.integer())
                                          .then(
                                             Commands.argument("respectTeams", BoolArgumentType.bool())
                                                .then(
                                                   Commands.argument("targets", EntityArgument.entities())
                                                      .executes(
                                                         var0x -> spreadPlayers(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               Vec2Argument.getVec2(var0x, "center"),
                                                               FloatArgumentType.getFloat(var0x, "spreadDistance"),
                                                               FloatArgumentType.getFloat(var0x, "maxRange"),
                                                               IntegerArgumentType.getInteger(var0x, "maxHeight"),
                                                               BoolArgumentType.getBool(var0x, "respectTeams"),
                                                               EntityArgument.getEntities(var0x, "targets")
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int spreadPlayers(CommandSourceStack var0, Vec2 var1, float var2, float var3, int var4, boolean var5, Collection<? extends Entity> var6) throws CommandSyntaxException {
      ServerLevel var7 = var0.getLevel();
      int var8 = var7.getMinBuildHeight();
      if (var4 < var8) {
         throw ERROR_INVALID_MAX_HEIGHT.create(var4, var8);
      } else {
         RandomSource var9 = RandomSource.create();
         double var10 = (double)(var1.x - var3);
         double var12 = (double)(var1.y - var3);
         double var14 = (double)(var1.x + var3);
         double var16 = (double)(var1.y + var3);
         SpreadPlayersCommand.Position[] var18 = createInitialPositions(var9, var5 ? getNumberOfTeams(var6) : var6.size(), var10, var12, var14, var16);
         spreadPositions(var1, (double)var2, var7, var9, var10, var12, var14, var16, var4, var18, var5);
         double var19 = setPlayerPositions(var6, var7, var18, var4, var5);
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.spreadplayers.success." + (var5 ? "teams" : "entities"), var18.length, var1.x, var1.y, String.format(Locale.ROOT, "%.2f", var19)
               ),
            true
         );
         return var18.length;
      }
   }

   private static int getNumberOfTeams(Collection<? extends Entity> var0) {
      HashSet var1 = Sets.newHashSet();

      for(Entity var3 : var0) {
         if (var3 instanceof Player) {
            var1.add(var3.getTeam());
         } else {
            var1.add(null);
         }
      }

      return var1.size();
   }

   private static void spreadPositions(
      Vec2 var0,
      double var1,
      ServerLevel var3,
      RandomSource var4,
      double var5,
      double var7,
      double var9,
      double var11,
      int var13,
      SpreadPlayersCommand.Position[] var14,
      boolean var15
   ) throws CommandSyntaxException {
      boolean var16 = true;
      double var18 = 3.4028234663852886E38;

      int var17;
      for(var17 = 0; var17 < 10000 && var16; ++var17) {
         var16 = false;
         var18 = 3.4028234663852886E38;

         for(int var20 = 0; var20 < var14.length; ++var20) {
            SpreadPlayersCommand.Position var21 = var14[var20];
            int var22 = 0;
            SpreadPlayersCommand.Position var23 = new SpreadPlayersCommand.Position();

            for(int var24 = 0; var24 < var14.length; ++var24) {
               if (var20 != var24) {
                  SpreadPlayersCommand.Position var25 = var14[var24];
                  double var26 = var21.dist(var25);
                  var18 = Math.min(var26, var18);
                  if (var26 < var1) {
                     ++var22;
                     var23.x += var25.x - var21.x;
                     var23.z += var25.z - var21.z;
                  }
               }
            }

            if (var22 > 0) {
               var23.x /= (double)var22;
               var23.z /= (double)var22;
               double var32 = var23.getLength();
               if (var32 > 0.0) {
                  var23.normalize();
                  var21.moveAway(var23);
               } else {
                  var21.randomize(var4, var5, var7, var9, var11);
               }

               var16 = true;
            }

            if (var21.clamp(var5, var7, var9, var11)) {
               var16 = true;
            }
         }

         if (!var16) {
            for(SpreadPlayersCommand.Position var31 : var14) {
               if (!var31.isSafe(var3, var13)) {
                  var31.randomize(var4, var5, var7, var9, var11);
                  var16 = true;
               }
            }
         }
      }

      if (var18 == 3.4028234663852886E38) {
         var18 = 0.0;
      }

      if (var17 >= 10000) {
         if (var15) {
            throw ERROR_FAILED_TO_SPREAD_TEAMS.create(var14.length, var0.x, var0.y, String.format(Locale.ROOT, "%.2f", var18));
         } else {
            throw ERROR_FAILED_TO_SPREAD_ENTITIES.create(var14.length, var0.x, var0.y, String.format(Locale.ROOT, "%.2f", var18));
         }
      }
   }

   private static double setPlayerPositions(Collection<? extends Entity> var0, ServerLevel var1, SpreadPlayersCommand.Position[] var2, int var3, boolean var4) {
      double var5 = 0.0;
      int var7 = 0;
      HashMap var8 = Maps.newHashMap();

      for(Entity var10 : var0) {
         SpreadPlayersCommand.Position var11;
         if (var4) {
            PlayerTeam var12 = var10 instanceof Player ? var10.getTeam() : null;
            if (!var8.containsKey(var12)) {
               var8.put(var12, var2[var7++]);
            }

            var11 = (SpreadPlayersCommand.Position)var8.get(var12);
         } else {
            var11 = var2[var7++];
         }

         var10.teleportTo(
            var1,
            (double)Mth.floor(var11.x) + 0.5,
            (double)var11.getSpawnY(var1, var3),
            (double)Mth.floor(var11.z) + 0.5,
            Set.of(),
            var10.getYRot(),
            var10.getXRot()
         );
         double var20 = 1.7976931348623157E308;

         for(SpreadPlayersCommand.Position var17 : var2) {
            if (var11 != var17) {
               double var18 = var11.dist(var17);
               var20 = Math.min(var18, var20);
            }
         }

         var5 += var20;
      }

      return var0.size() < 2 ? 0.0 : var5 / (double)var0.size();
   }

   private static SpreadPlayersCommand.Position[] createInitialPositions(RandomSource var0, int var1, double var2, double var4, double var6, double var8) {
      SpreadPlayersCommand.Position[] var10 = new SpreadPlayersCommand.Position[var1];

      for(int var11 = 0; var11 < var10.length; ++var11) {
         SpreadPlayersCommand.Position var12 = new SpreadPlayersCommand.Position();
         var12.randomize(var0, var2, var4, var6, var8);
         var10[var11] = var12;
      }

      return var10;
   }

   static class Position {
      double x;
      double z;

      Position() {
         super();
      }

      double dist(SpreadPlayersCommand.Position var1) {
         double var2 = this.x - var1.x;
         double var4 = this.z - var1.z;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      void normalize() {
         double var1 = this.getLength();
         this.x /= var1;
         this.z /= var1;
      }

      double getLength() {
         return Math.sqrt(this.x * this.x + this.z * this.z);
      }

      public void moveAway(SpreadPlayersCommand.Position var1) {
         this.x -= var1.x;
         this.z -= var1.z;
      }

      public boolean clamp(double var1, double var3, double var5, double var7) {
         boolean var9 = false;
         if (this.x < var1) {
            this.x = var1;
            var9 = true;
         } else if (this.x > var5) {
            this.x = var5;
            var9 = true;
         }

         if (this.z < var3) {
            this.z = var3;
            var9 = true;
         } else if (this.z > var7) {
            this.z = var7;
            var9 = true;
         }

         return var9;
      }

      public int getSpawnY(BlockGetter var1, int var2) {
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(this.x, (double)(var2 + 1), this.z);
         boolean var4 = var1.getBlockState(var3).isAir();
         var3.move(Direction.DOWN);

         boolean var6;
         for(boolean var5 = var1.getBlockState(var3).isAir(); var3.getY() > var1.getMinBuildHeight(); var5 = var6) {
            var3.move(Direction.DOWN);
            var6 = var1.getBlockState(var3).isAir();
            if (!var6 && var5 && var4) {
               return var3.getY() + 1;
            }

            var4 = var5;
         }

         return var2 + 1;
      }

      public boolean isSafe(BlockGetter var1, int var2) {
         BlockPos var3 = BlockPos.containing(this.x, (double)(this.getSpawnY(var1, var2) - 1), this.z);
         BlockState var4 = var1.getBlockState(var3);
         return var3.getY() < var2 && !var4.liquid() && !var4.is(BlockTags.FIRE);
      }

      public void randomize(RandomSource var1, double var2, double var4, double var6, double var8) {
         this.x = Mth.nextDouble(var1, var2, var6);
         this.z = Mth.nextDouble(var1, var4, var8);
      }
   }
}
