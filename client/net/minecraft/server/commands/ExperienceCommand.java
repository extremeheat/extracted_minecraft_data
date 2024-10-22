package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceCommand {
   private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(
      Component.translatable("commands.experience.set.points.invalid")
   );

   public ExperienceCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("experience")
                     .requires(var0x -> var0x.hasPermission(2)))
                  .then(
                     Commands.literal("add")
                        .then(
                           Commands.argument("target", EntityArgument.players())
                              .then(
                                 ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer())
                                          .executes(
                                             var0x -> addExperience(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   EntityArgument.getPlayers(var0x, "target"),
                                                   IntegerArgumentType.getInteger(var0x, "amount"),
                                                   ExperienceCommand.Type.POINTS
                                                )
                                          ))
                                       .then(
                                          Commands.literal("points")
                                             .executes(
                                                var0x -> addExperience(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      EntityArgument.getPlayers(var0x, "target"),
                                                      IntegerArgumentType.getInteger(var0x, "amount"),
                                                      ExperienceCommand.Type.POINTS
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("levels")
                                          .executes(
                                             var0x -> addExperience(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   EntityArgument.getPlayers(var0x, "target"),
                                                   IntegerArgumentType.getInteger(var0x, "amount"),
                                                   ExperienceCommand.Type.LEVELS
                                                )
                                          )
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("set")
                     .then(
                        Commands.argument("target", EntityArgument.players())
                           .then(
                              ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer(0))
                                       .executes(
                                          var0x -> setExperience(
                                                (CommandSourceStack)var0x.getSource(),
                                                EntityArgument.getPlayers(var0x, "target"),
                                                IntegerArgumentType.getInteger(var0x, "amount"),
                                                ExperienceCommand.Type.POINTS
                                             )
                                       ))
                                    .then(
                                       Commands.literal("points")
                                          .executes(
                                             var0x -> setExperience(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   EntityArgument.getPlayers(var0x, "target"),
                                                   IntegerArgumentType.getInteger(var0x, "amount"),
                                                   ExperienceCommand.Type.POINTS
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("levels")
                                       .executes(
                                          var0x -> setExperience(
                                                (CommandSourceStack)var0x.getSource(),
                                                EntityArgument.getPlayers(var0x, "target"),
                                                IntegerArgumentType.getInteger(var0x, "amount"),
                                                ExperienceCommand.Type.LEVELS
                                             )
                                       )
                                 )
                           )
                     )
               ))
            .then(
               Commands.literal("query")
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("target", EntityArgument.player())
                           .then(
                              Commands.literal("points")
                                 .executes(
                                    var0x -> queryExperience(
                                          (CommandSourceStack)var0x.getSource(), EntityArgument.getPlayer(var0x, "target"), ExperienceCommand.Type.POINTS
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("levels")
                              .executes(
                                 var0x -> queryExperience(
                                       (CommandSourceStack)var0x.getSource(), EntityArgument.getPlayer(var0x, "target"), ExperienceCommand.Type.LEVELS
                                    )
                              )
                        )
                  )
            )
      );
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("xp").requires(var0x -> var0x.hasPermission(2))).redirect(var1));
   }

   private static int queryExperience(CommandSourceStack var0, ServerPlayer var1, ExperienceCommand.Type var2) {
      int var3 = var2.query.applyAsInt(var1);
      var0.sendSuccess(() -> Component.translatable("commands.experience.query." + var2.name, var1.getDisplayName(), var3), false);
      return var3;
   }

   private static int addExperience(CommandSourceStack var0, Collection<? extends ServerPlayer> var1, int var2, ExperienceCommand.Type var3) {
      for (ServerPlayer var5 : var1) {
         var3.add.accept(var5, var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.experience.add." + var3.name + ".success.single", var2, ((ServerPlayer)var1.iterator().next()).getDisplayName()
               ),
            true
         );
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.experience.add." + var3.name + ".success.multiple", var2, var1.size()), true);
      }

      return var1.size();
   }

   private static int setExperience(CommandSourceStack var0, Collection<? extends ServerPlayer> var1, int var2, ExperienceCommand.Type var3) throws CommandSyntaxException {
      int var4 = 0;

      for (ServerPlayer var6 : var1) {
         if (var3.set.test(var6, var2)) {
            var4++;
         }
      }

      if (var4 == 0) {
         throw ERROR_SET_POINTS_INVALID.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(
               () -> Component.translatable(
                     "commands.experience.set." + var3.name + ".success.single", var2, ((ServerPlayer)var1.iterator().next()).getDisplayName()
                  ),
               true
            );
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.experience.set." + var3.name + ".success.multiple", var2, var1.size()), true);
         }

         return var1.size();
      }
   }

   static enum Type {
      POINTS("points", Player::giveExperiencePoints, (var0, var1) -> {
         if (var1 >= var0.getXpNeededForNextLevel()) {
            return false;
         } else {
            var0.setExperiencePoints(var1);
            return true;
         }
      }, var0 -> Mth.floor(var0.experienceProgress * (float)var0.getXpNeededForNextLevel())),
      LEVELS("levels", ServerPlayer::giveExperienceLevels, (var0, var1) -> {
         var0.setExperienceLevels(var1);
         return true;
      }, var0 -> var0.experienceLevel);

      public final BiConsumer<ServerPlayer, Integer> add;
      public final BiPredicate<ServerPlayer, Integer> set;
      public final String name;
      final ToIntFunction<ServerPlayer> query;

      private Type(
         final String nullxx,
         final BiConsumer<ServerPlayer, Integer> nullxxx,
         final BiPredicate<ServerPlayer, Integer> nullxxxx,
         final ToIntFunction<ServerPlayer> nullxxxxx
      ) {
         this.add = nullxxx;
         this.name = nullxx;
         this.set = nullxxxx;
         this.query = nullxxxxx;
      }
   }
}
