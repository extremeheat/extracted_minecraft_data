package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.Entity;

public class TagCommand {
   private static final SimpleCommandExceptionType ERROR_ADD_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.tag.add.failed"));
   private static final SimpleCommandExceptionType ERROR_REMOVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.tag.remove.failed"));
   private static final CommandResponseTracker.Messages<Entity> RESPONSE_NO_TAGS = CommandResponseTracker.messages((CommandResponseTracker.SingleHandler)((entity, var1) -> Component.translatable("commands.tag.list.single.empty", entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, var1) -> Component.translatable("commands.tag.list.multiple.empty", entityCount)));
   private static final CommandResponseTracker.MessagesWithArg<Entity, String> RESPONSE_REMOVE;
   private static final CommandResponseTracker.MessagesWithArg<Entity, String> RESPONSE_ADD;
   private static final CommandResponseTracker.MessagesWithArg<Entity, Set<String>> RESPONSE_LIST;

   public TagCommand() {
      super();
   }

   private static SuggestionProvider<CommandSourceStack> suggestEntityTags() {
      return SuggestionProviders.serverOnly((server, context, builder) -> SuggestionContext.suggest(getTags(EntityArgument.getSuggestedEntities(server, context, "targets")), builder));
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tag").requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(Commands.literal("add").then(Commands.argument("name", StringArgument.word()).executes((c) -> addTag((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), StringArgument.getString(c, "name")))))).then(Commands.literal("remove").then(Commands.argument("name", StringArgument.word()).suggests(suggestEntityTags()).executes((c) -> removeTag((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), StringArgument.getString(c, "name")))))).then(Commands.literal("list").executes((c) -> listTags((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"))))));
   }

   private static Stream<String> getTags(final Stream<? extends Entity> entities) {
      return entities.flatMap((e) -> e.entityTags().stream()).distinct();
   }

   private static int addTag(final CommandSourceStack source, final Collection<? extends Entity> targets, final String name) throws CommandSyntaxException {
      CommandResponseTracker<Entity> response = CommandResponseTracker.<Entity>create();

      for(Entity entity : targets) {
         response.track(entity, entity.addTag(name));
      }

      return response.sendFeedback(source, true, (CommandResponseTracker.MessagesWithArg)RESPONSE_ADD, name);
   }

   private static int removeTag(final CommandSourceStack source, final Collection<? extends Entity> targets, final String name) throws CommandSyntaxException {
      CommandResponseTracker<Entity> response = CommandResponseTracker.<Entity>create();

      for(Entity entity : targets) {
         response.track(entity, entity.removeTag(name));
      }

      return response.sendFeedback(source, true, (CommandResponseTracker.MessagesWithArg)RESPONSE_REMOVE, name);
   }

   private static int listTags(final CommandSourceStack source, final Collection<? extends Entity> targets) throws CommandSyntaxException {
      CommandResponseTracker<Entity> response = CommandResponseTracker.<Entity>create();
      Set<String> tags = new HashSet();

      for(Entity entity : targets) {
         Set<String> entityTags = entity.entityTags();
         tags.addAll(entityTags);
         response.track(entity, entityTags.size());
      }

      if (tags.isEmpty()) {
         response.sendFeedback(source, false, RESPONSE_NO_TAGS);
      } else {
         response.sendFeedback(source, false, (CommandResponseTracker.MessagesWithArg)RESPONSE_LIST, tags);
      }

      return tags.size();
   }

   static {
      RESPONSE_REMOVE = CommandResponseTracker.messages((SimpleCommandExceptionType)ERROR_REMOVE_FAILED, (CommandResponseTracker.SingleHandlerWithArg)((entity, var1, name) -> Component.translatable("commands.tag.remove.success.single", name, entity.getDisplayName())), (CommandResponseTracker.MultipleHandlerWithArg)((entityCount, var1, name) -> Component.translatable("commands.tag.remove.success.multiple", name, entityCount)));
      RESPONSE_ADD = CommandResponseTracker.messages((SimpleCommandExceptionType)ERROR_ADD_FAILED, (CommandResponseTracker.SingleHandlerWithArg)((entity, var1, name) -> Component.translatable("commands.tag.add.success.single", name, entity.getDisplayName())), (CommandResponseTracker.MultipleHandlerWithArg)((entityCount, var1, name) -> Component.translatable("commands.tag.add.success.multiple", name, entityCount)));
      RESPONSE_LIST = CommandResponseTracker.messages((CommandResponseTracker.SingleHandlerWithArg)((entity, var1, tags) -> Component.translatable("commands.tag.list.single.success", entity.getDisplayName(), tags.size(), ComponentUtils.formatList(tags))), (CommandResponseTracker.MultipleHandlerWithArg)((entityCount, var1, tags) -> Component.translatable("commands.tag.list.multiple.success", entityCount, tags.size(), ComponentUtils.formatList(tags))));
   }
}
