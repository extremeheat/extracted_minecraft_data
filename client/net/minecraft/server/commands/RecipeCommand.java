package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeCommand {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.recipe.give.failed"));
   private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.recipe.take.failed"));
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_LEARNABLE_RECIPE = SuggestionProviders.serverOnly((var0, context, builder) -> ((SuggestionContext)context.getSource()).suggestRegistryElements(context, Registries.RECIPE, SuggestionContext.ElementSuggestionType.ELEMENTS, builder, (r) -> !r.isSpecial()));
   private static final CommandResponseTracker.Messages<ServerPlayer> RESPONSE_GIVE;
   private static final CommandResponseTracker.Messages<ServerPlayer> RESPONSE_TAKE;

   public RecipeCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("recipe").requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS))).then(Commands.literal("give").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceArgument.resource(Registries.RECIPE)).suggests(SUGGEST_LEARNABLE_RECIPE).executes((c) -> giveRecipes((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), Collections.singleton(ResourceArgument.getLearnableRecipe(c, "recipe")))))).then(Commands.literal("*").executes((c) -> giveRecipes((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), ((CommandSourceStack)c.getSource()).getServer().getRecipeManager().getLearnableRecipes())))))).then(Commands.literal("take").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceArgument.resource(Registries.RECIPE)).suggests(SUGGEST_LEARNABLE_RECIPE).executes((c) -> takeRecipes((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), Collections.singleton(ResourceArgument.getLearnableRecipe(c, "recipe")))))).then(Commands.literal("*").executes((c) -> takeRecipes((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), ((CommandSourceStack)c.getSource()).getServer().getRecipeManager().getLearnableRecipes()))))));
   }

   private static int giveRecipes(final CommandSourceStack source, final Collection<ServerPlayer> players, final Collection<RecipeHolder<?>> recipes) throws CommandSyntaxException {
      CommandResponseTracker<ServerPlayer> response = CommandResponseTracker.<ServerPlayer>create();

      for(ServerPlayer player : players) {
         response.track(player, player.awardRecipes(recipes));
      }

      return response.sendFeedback(source, true, RESPONSE_GIVE);
   }

   private static int takeRecipes(final CommandSourceStack source, final Collection<ServerPlayer> players, final Collection<RecipeHolder<?>> recipes) throws CommandSyntaxException {
      CommandResponseTracker<ServerPlayer> response = CommandResponseTracker.<ServerPlayer>create();

      for(ServerPlayer player : players) {
         response.track(player, player.resetRecipes(recipes));
      }

      return response.sendFeedback(source, true, RESPONSE_TAKE);
   }

   static {
      RESPONSE_GIVE = CommandResponseTracker.messages((SimpleCommandExceptionType)ERROR_GIVE_FAILED, (CommandResponseTracker.SingleHandler)((player, totalValue) -> Component.translatable("commands.recipe.give.success.single", totalValue, player.getDisplayName())), (CommandResponseTracker.MultipleHandler)((playerCount, totalValue) -> Component.translatable("commands.recipe.give.success.multiple", totalValue, playerCount)));
      SimpleCommandExceptionType var10000 = ERROR_TAKE_FAILED;
      Objects.requireNonNull(var10000);
      RESPONSE_TAKE = CommandResponseTracker.messages((CommandResponseTracker.ErrorHandler)(var10000::create), (CommandResponseTracker.SingleHandler)((player, totalValue) -> Component.translatable("commands.recipe.take.success.single", totalValue, player.getDisplayName())), (CommandResponseTracker.MultipleHandler)((playerCount, totalValue) -> Component.translatable("commands.recipe.take.success.multiple", totalValue, playerCount)));
   }
}
