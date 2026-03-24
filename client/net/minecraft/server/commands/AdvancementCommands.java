package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
   private static final DynamicCommandExceptionType ERROR_NO_ACTION_PERFORMED = new DynamicCommandExceptionType((msg) -> (Component)msg);
   private static final Dynamic2CommandExceptionType ERROR_CRITERION_NOT_FOUND = new Dynamic2CommandExceptionType((name, criterion) -> Component.translatableEscape("commands.advancement.criterionNotFound", name, criterion));

   public AdvancementCommands() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((c, p) -> SharedSuggestionProvider.suggest(ResourceKeyArgument.getAdvancement(c, "advancement").value().criteria().keySet(), p)).executes((c) -> performCriterion((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.GRANT, ResourceKeyArgument.getAdvancement(c, "advancement"), StringArgumentType.getString(c, "criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.THROUGH)))))).then(Commands.literal("everything").executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.GRANT, ((CommandSourceStack)c.getSource()).getServer().getAdvancements().getAllAdvancements(), false)))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((c, p) -> SharedSuggestionProvider.suggest(ResourceKeyArgument.getAdvancement(c, "advancement").value().criteria().keySet(), p)).executes((c) -> performCriterion((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.REVOKE, ResourceKeyArgument.getAdvancement(c, "advancement"), StringArgumentType.getString(c, "criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT)).executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(c, ResourceKeyArgument.getAdvancement(c, "advancement"), AdvancementCommands.Mode.THROUGH)))))).then(Commands.literal("everything").executes((c) -> perform((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), AdvancementCommands.Action.REVOKE, ((CommandSourceStack)c.getSource()).getServer().getAdvancements().getAllAdvancements()))))));
   }

   private static int perform(final CommandSourceStack source, final Collection<ServerPlayer> players, final Action action, final Collection<AdvancementHolder> advancements) throws CommandSyntaxException {
      return perform(source, players, action, advancements, true);
   }

   private static int perform(final CommandSourceStack source, final Collection<ServerPlayer> players, final Action action, final Collection<AdvancementHolder> advancements, final boolean showAdvancements) throws CommandSyntaxException {
      int count = 0;

      for(ServerPlayer player : players) {
         count += action.perform(player, advancements, showAdvancements);
      }

      if (count == 0) {
         if (advancements.size() == 1) {
            if (players.size() == 1) {
               throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(action.getKey() + ".one.to.one.failure", Advancement.name((AdvancementHolder)advancements.iterator().next()), ((ServerPlayer)players.iterator().next()).getDisplayName()));
            } else {
               throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(action.getKey() + ".one.to.many.failure", Advancement.name((AdvancementHolder)advancements.iterator().next()), players.size()));
            }
         } else if (players.size() == 1) {
            throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(action.getKey() + ".many.to.one.failure", advancements.size(), ((ServerPlayer)players.iterator().next()).getDisplayName()));
         } else {
            throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(action.getKey() + ".many.to.many.failure", advancements.size(), players.size()));
         }
      } else {
         if (advancements.size() == 1) {
            if (players.size() == 1) {
               source.sendSuccess(() -> Component.translatable(action.getKey() + ".one.to.one.success", Advancement.name((AdvancementHolder)advancements.iterator().next()), ((ServerPlayer)players.iterator().next()).getDisplayName()), true);
            } else {
               source.sendSuccess(() -> Component.translatable(action.getKey() + ".one.to.many.success", Advancement.name((AdvancementHolder)advancements.iterator().next()), players.size()), true);
            }
         } else if (players.size() == 1) {
            source.sendSuccess(() -> Component.translatable(action.getKey() + ".many.to.one.success", advancements.size(), ((ServerPlayer)players.iterator().next()).getDisplayName()), true);
         } else {
            source.sendSuccess(() -> Component.translatable(action.getKey() + ".many.to.many.success", advancements.size(), players.size()), true);
         }

         return count;
      }
   }

   private static int performCriterion(final CommandSourceStack source, final Collection<ServerPlayer> players, final Action action, final AdvancementHolder holder, final String criterion) throws CommandSyntaxException {
      int count = 0;
      Advancement advancement = holder.value();
      if (!advancement.criteria().containsKey(criterion)) {
         throw ERROR_CRITERION_NOT_FOUND.create(Advancement.name(holder), criterion);
      } else {
         for(ServerPlayer player : players) {
            if (action.performCriterion(player, holder, criterion)) {
               ++count;
            }
         }

         if (count == 0) {
            if (players.size() == 1) {
               throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(action.getKey() + ".criterion.to.one.failure", criterion, Advancement.name(holder), ((ServerPlayer)players.iterator().next()).getDisplayName()));
            } else {
               throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(action.getKey() + ".criterion.to.many.failure", criterion, Advancement.name(holder), players.size()));
            }
         } else {
            if (players.size() == 1) {
               source.sendSuccess(() -> Component.translatable(action.getKey() + ".criterion.to.one.success", criterion, Advancement.name(holder), ((ServerPlayer)players.iterator().next()).getDisplayName()), true);
            } else {
               source.sendSuccess(() -> Component.translatable(action.getKey() + ".criterion.to.many.success", criterion, Advancement.name(holder), players.size()), true);
            }

            return count;
         }
      }
   }

   private static List<AdvancementHolder> getAdvancements(final CommandContext<CommandSourceStack> context, final AdvancementHolder target, final Mode mode) {
      AdvancementTree advancementTree = ((CommandSourceStack)context.getSource()).getServer().getAdvancements().tree();
      AdvancementNode targetNode = advancementTree.get(target);
      if (targetNode == null) {
         return List.of(target);
      } else {
         List<AdvancementHolder> advancements = new ArrayList();
         if (mode.parents) {
            for(AdvancementNode parent = targetNode.parent(); parent != null; parent = parent.parent()) {
               advancements.add(parent.holder());
            }
         }

         advancements.add(target);
         if (mode.children) {
            addChildren(targetNode, advancements);
         }

         return advancements;
      }
   }

   private static void addChildren(final AdvancementNode parent, final List<AdvancementHolder> output) {
      for(AdvancementNode child : parent.children()) {
         output.add(child.holder());
         addChildren(child, output);
      }

   }

   private static enum Action {
      GRANT("grant") {
         protected boolean perform(final ServerPlayer player, final AdvancementHolder advancement) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (progress.isDone()) {
               return false;
            } else {
               for(String criterion : progress.getRemainingCriteria()) {
                  player.getAdvancements().award(advancement, criterion);
               }

               return true;
            }
         }

         protected boolean performCriterion(final ServerPlayer player, final AdvancementHolder advancement, final String criterion) {
            return player.getAdvancements().award(advancement, criterion);
         }
      },
      REVOKE("revoke") {
         protected boolean perform(final ServerPlayer player, final AdvancementHolder advancement) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.hasProgress()) {
               return false;
            } else {
               for(String criterion : progress.getCompletedCriteria()) {
                  player.getAdvancements().revoke(advancement, criterion);
               }

               return true;
            }
         }

         protected boolean performCriterion(final ServerPlayer player, final AdvancementHolder advancement, final String criterion) {
            return player.getAdvancements().revoke(advancement, criterion);
         }
      };

      private final String key;

      private Action(final String key) {
         this.key = "commands.advancement." + key;
      }

      public int perform(final ServerPlayer player, final Iterable<AdvancementHolder> advancements, final boolean showAdvancements) {
         int count = 0;
         if (!showAdvancements) {
            player.getAdvancements().flushDirty(player, true);
         }

         for(AdvancementHolder advancement : advancements) {
            if (this.perform(player, advancement)) {
               ++count;
            }
         }

         if (!showAdvancements) {
            player.getAdvancements().flushDirty(player, false);
         }

         return count;
      }

      protected abstract boolean perform(ServerPlayer player, AdvancementHolder advancement);

      protected abstract boolean performCriterion(ServerPlayer player, AdvancementHolder advancement, String criterion);

      protected String getKey() {
         return this.key;
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{GRANT, REVOKE};
      }
   }

   private static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean parents;
      private final boolean children;

      private Mode(final boolean parents, final boolean children) {
         this.parents = parents;
         this.children = children;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{ONLY, THROUGH, FROM, UNTIL, EVERYTHING};
      }
   }
}
