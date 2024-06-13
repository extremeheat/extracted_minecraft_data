package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
   private static final DynamicCommandExceptionType ERROR_NO_ACTION_PERFORMED = new DynamicCommandExceptionType(var0 -> (Component)var0);
   private static final Dynamic2CommandExceptionType ERROR_CRITERION_NOT_FOUND = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("commands.advancement.criterionNotFound", var0, var1)
   );
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = (var0, var1) -> {
      Collection var2 = ((CommandSourceStack)var0.getSource()).getServer().getAdvancements().getAllAdvancements();
      return SharedSuggestionProvider.suggestResource(var2.stream().map(AdvancementHolder::id), var1);
   };

   public AdvancementCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires(var0x -> var0x.hasPermission(2)))
               .then(
                  Commands.literal("grant")
                     .then(
                        ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                          "targets", EntityArgument.players()
                                       )
                                       .then(
                                          Commands.literal("only")
                                             .then(
                                                ((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id())
                                                      .suggests(SUGGEST_ADVANCEMENTS)
                                                      .executes(
                                                         var0x -> perform(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               EntityArgument.getPlayers(var0x, "targets"),
                                                               AdvancementCommands.Action.GRANT,
                                                               getAdvancements(
                                                                  var0x,
                                                                  ResourceLocationArgument.getAdvancement(var0x, "advancement"),
                                                                  AdvancementCommands.Mode.ONLY
                                                               )
                                                            )
                                                      ))
                                                   .then(
                                                      Commands.argument("criterion", StringArgumentType.greedyString())
                                                         .suggests(
                                                            (var0x, var1) -> SharedSuggestionProvider.suggest(
                                                                  ResourceLocationArgument.getAdvancement(var0x, "advancement").value().criteria().keySet(),
                                                                  var1
                                                               )
                                                         )
                                                         .executes(
                                                            var0x -> performCriterion(
                                                                  (CommandSourceStack)var0x.getSource(),
                                                                  EntityArgument.getPlayers(var0x, "targets"),
                                                                  AdvancementCommands.Action.GRANT,
                                                                  ResourceLocationArgument.getAdvancement(var0x, "advancement"),
                                                                  StringArgumentType.getString(var0x, "criterion")
                                                               )
                                                         )
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("from")
                                          .then(
                                             Commands.argument("advancement", ResourceLocationArgument.id())
                                                .suggests(SUGGEST_ADVANCEMENTS)
                                                .executes(
                                                   var0x -> perform(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         EntityArgument.getPlayers(var0x, "targets"),
                                                         AdvancementCommands.Action.GRANT,
                                                         getAdvancements(
                                                            var0x, ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.FROM
                                                         )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("until")
                                       .then(
                                          Commands.argument("advancement", ResourceLocationArgument.id())
                                             .suggests(SUGGEST_ADVANCEMENTS)
                                             .executes(
                                                var0x -> perform(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      EntityArgument.getPlayers(var0x, "targets"),
                                                      AdvancementCommands.Action.GRANT,
                                                      getAdvancements(
                                                         var0x, ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.UNTIL
                                                      )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("through")
                                    .then(
                                       Commands.argument("advancement", ResourceLocationArgument.id())
                                          .suggests(SUGGEST_ADVANCEMENTS)
                                          .executes(
                                             var0x -> perform(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   EntityArgument.getPlayers(var0x, "targets"),
                                                   AdvancementCommands.Action.GRANT,
                                                   getAdvancements(
                                                      var0x, ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.THROUGH
                                                   )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("everything")
                                 .executes(
                                    var0x -> perform(
                                          (CommandSourceStack)var0x.getSource(),
                                          EntityArgument.getPlayers(var0x, "targets"),
                                          AdvancementCommands.Action.GRANT,
                                          ((CommandSourceStack)var0x.getSource()).getServer().getAdvancements().getAllAdvancements()
                                       )
                                 )
                           )
                     )
               ))
            .then(
               Commands.literal("revoke")
                  .then(
                     ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                       "targets", EntityArgument.players()
                                    )
                                    .then(
                                       Commands.literal("only")
                                          .then(
                                             ((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id())
                                                   .suggests(SUGGEST_ADVANCEMENTS)
                                                   .executes(
                                                      var0x -> perform(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            EntityArgument.getPlayers(var0x, "targets"),
                                                            AdvancementCommands.Action.REVOKE,
                                                            getAdvancements(
                                                               var0x,
                                                               ResourceLocationArgument.getAdvancement(var0x, "advancement"),
                                                               AdvancementCommands.Mode.ONLY
                                                            )
                                                         )
                                                   ))
                                                .then(
                                                   Commands.argument("criterion", StringArgumentType.greedyString())
                                                      .suggests(
                                                         (var0x, var1) -> SharedSuggestionProvider.suggest(
                                                               ResourceLocationArgument.getAdvancement(var0x, "advancement").value().criteria().keySet(), var1
                                                            )
                                                      )
                                                      .executes(
                                                         var0x -> performCriterion(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               EntityArgument.getPlayers(var0x, "targets"),
                                                               AdvancementCommands.Action.REVOKE,
                                                               ResourceLocationArgument.getAdvancement(var0x, "advancement"),
                                                               StringArgumentType.getString(var0x, "criterion")
                                                            )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("from")
                                       .then(
                                          Commands.argument("advancement", ResourceLocationArgument.id())
                                             .suggests(SUGGEST_ADVANCEMENTS)
                                             .executes(
                                                var0x -> perform(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      EntityArgument.getPlayers(var0x, "targets"),
                                                      AdvancementCommands.Action.REVOKE,
                                                      getAdvancements(
                                                         var0x, ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.FROM
                                                      )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("until")
                                    .then(
                                       Commands.argument("advancement", ResourceLocationArgument.id())
                                          .suggests(SUGGEST_ADVANCEMENTS)
                                          .executes(
                                             var0x -> perform(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   EntityArgument.getPlayers(var0x, "targets"),
                                                   AdvancementCommands.Action.REVOKE,
                                                   getAdvancements(
                                                      var0x, ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.UNTIL
                                                   )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("through")
                                 .then(
                                    Commands.argument("advancement", ResourceLocationArgument.id())
                                       .suggests(SUGGEST_ADVANCEMENTS)
                                       .executes(
                                          var0x -> perform(
                                                (CommandSourceStack)var0x.getSource(),
                                                EntityArgument.getPlayers(var0x, "targets"),
                                                AdvancementCommands.Action.REVOKE,
                                                getAdvancements(
                                                   var0x, ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.THROUGH
                                                )
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("everything")
                              .executes(
                                 var0x -> perform(
                                       (CommandSourceStack)var0x.getSource(),
                                       EntityArgument.getPlayers(var0x, "targets"),
                                       AdvancementCommands.Action.REVOKE,
                                       ((CommandSourceStack)var0x.getSource()).getServer().getAdvancements().getAllAdvancements()
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int perform(CommandSourceStack var0, Collection<ServerPlayer> var1, AdvancementCommands.Action var2, Collection<AdvancementHolder> var3) throws CommandSyntaxException {
      int var4 = 0;

      for (ServerPlayer var6 : var1) {
         var4 += var2.perform(var6, var3);
      }

      if (var4 == 0) {
         if (var3.size() == 1) {
            if (var1.size() == 1) {
               throw ERROR_NO_ACTION_PERFORMED.create(
                  Component.translatable(
                     var2.getKey() + ".one.to.one.failure",
                     Advancement.name((AdvancementHolder)var3.iterator().next()),
                     ((ServerPlayer)var1.iterator().next()).getDisplayName()
                  )
               );
            } else {
               throw ERROR_NO_ACTION_PERFORMED.create(
                  Component.translatable(var2.getKey() + ".one.to.many.failure", Advancement.name((AdvancementHolder)var3.iterator().next()), var1.size())
               );
            }
         } else if (var1.size() == 1) {
            throw ERROR_NO_ACTION_PERFORMED.create(
               Component.translatable(var2.getKey() + ".many.to.one.failure", var3.size(), ((ServerPlayer)var1.iterator().next()).getDisplayName())
            );
         } else {
            throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(var2.getKey() + ".many.to.many.failure", var3.size(), var1.size()));
         }
      } else {
         if (var3.size() == 1) {
            if (var1.size() == 1) {
               var0.sendSuccess(
                  () -> Component.translatable(
                        var2.getKey() + ".one.to.one.success",
                        Advancement.name((AdvancementHolder)var3.iterator().next()),
                        ((ServerPlayer)var1.iterator().next()).getDisplayName()
                     ),
                  true
               );
            } else {
               var0.sendSuccess(
                  () -> Component.translatable(var2.getKey() + ".one.to.many.success", Advancement.name((AdvancementHolder)var3.iterator().next()), var1.size()),
                  true
               );
            }
         } else if (var1.size() == 1) {
            var0.sendSuccess(
               () -> Component.translatable(var2.getKey() + ".many.to.one.success", var3.size(), ((ServerPlayer)var1.iterator().next()).getDisplayName()), true
            );
         } else {
            var0.sendSuccess(() -> Component.translatable(var2.getKey() + ".many.to.many.success", var3.size(), var1.size()), true);
         }

         return var4;
      }
   }

   private static int performCriterion(
      CommandSourceStack var0, Collection<ServerPlayer> var1, AdvancementCommands.Action var2, AdvancementHolder var3, String var4
   ) throws CommandSyntaxException {
      int var5 = 0;
      Advancement var6 = var3.value();
      if (!var6.criteria().containsKey(var4)) {
         throw ERROR_CRITERION_NOT_FOUND.create(Advancement.name(var3), var4);
      } else {
         for (ServerPlayer var8 : var1) {
            if (var2.performCriterion(var8, var3, var4)) {
               var5++;
            }
         }

         if (var5 == 0) {
            if (var1.size() == 1) {
               throw ERROR_NO_ACTION_PERFORMED.create(
                  Component.translatable(
                     var2.getKey() + ".criterion.to.one.failure", var4, Advancement.name(var3), ((ServerPlayer)var1.iterator().next()).getDisplayName()
                  )
               );
            } else {
               throw ERROR_NO_ACTION_PERFORMED.create(
                  Component.translatable(var2.getKey() + ".criterion.to.many.failure", var4, Advancement.name(var3), var1.size())
               );
            }
         } else {
            if (var1.size() == 1) {
               var0.sendSuccess(
                  () -> Component.translatable(
                        var2.getKey() + ".criterion.to.one.success", var4, Advancement.name(var3), ((ServerPlayer)var1.iterator().next()).getDisplayName()
                     ),
                  true
               );
            } else {
               var0.sendSuccess(() -> Component.translatable(var2.getKey() + ".criterion.to.many.success", var4, Advancement.name(var3), var1.size()), true);
            }

            return var5;
         }
      }
   }

   private static List<AdvancementHolder> getAdvancements(CommandContext<CommandSourceStack> var0, AdvancementHolder var1, AdvancementCommands.Mode var2) {
      AdvancementTree var3 = ((CommandSourceStack)var0.getSource()).getServer().getAdvancements().tree();
      AdvancementNode var4 = var3.get(var1);
      if (var4 == null) {
         return List.of(var1);
      } else {
         ArrayList var5 = new ArrayList();
         if (var2.parents) {
            for (AdvancementNode var6 = var4.parent(); var6 != null; var6 = var6.parent()) {
               var5.add(var6.holder());
            }
         }

         var5.add(var1);
         if (var2.children) {
            addChildren(var4, var5);
         }

         return var5;
      }
   }

   private static void addChildren(AdvancementNode var0, List<AdvancementHolder> var1) {
      for (AdvancementNode var3 : var0.children()) {
         var1.add(var3.holder());
         addChildren(var3, var1);
      }
   }

   static enum Action {
      GRANT("grant") {
         @Override
         protected boolean perform(ServerPlayer var1, AdvancementHolder var2) {
            AdvancementProgress var3 = var1.getAdvancements().getOrStartProgress(var2);
            if (var3.isDone()) {
               return false;
            } else {
               for (String var5 : var3.getRemainingCriteria()) {
                  var1.getAdvancements().award(var2, var5);
               }

               return true;
            }
         }

         @Override
         protected boolean performCriterion(ServerPlayer var1, AdvancementHolder var2, String var3) {
            return var1.getAdvancements().award(var2, var3);
         }
      },
      REVOKE("revoke") {
         @Override
         protected boolean perform(ServerPlayer var1, AdvancementHolder var2) {
            AdvancementProgress var3 = var1.getAdvancements().getOrStartProgress(var2);
            if (!var3.hasProgress()) {
               return false;
            } else {
               for (String var5 : var3.getCompletedCriteria()) {
                  var1.getAdvancements().revoke(var2, var5);
               }

               return true;
            }
         }

         @Override
         protected boolean performCriterion(ServerPlayer var1, AdvancementHolder var2, String var3) {
            return var1.getAdvancements().revoke(var2, var3);
         }
      };

      private final String key;

      Action(final String nullxx) {
         this.key = "commands.advancement." + nullxx;
      }

      public int perform(ServerPlayer var1, Iterable<AdvancementHolder> var2) {
         int var3 = 0;

         for (AdvancementHolder var5 : var2) {
            if (this.perform(var1, var5)) {
               var3++;
            }
         }

         return var3;
      }

      protected abstract boolean perform(ServerPlayer var1, AdvancementHolder var2);

      protected abstract boolean performCriterion(ServerPlayer var1, AdvancementHolder var2, String var3);

      protected String getKey() {
         return this.key;
      }
   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      final boolean parents;
      final boolean children;

      private Mode(final boolean nullxx, final boolean nullxxx) {
         this.parents = nullxx;
         this.children = nullxxx;
      }
   }
}
