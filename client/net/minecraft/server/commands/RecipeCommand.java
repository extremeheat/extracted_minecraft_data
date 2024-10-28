package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeCommand {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.recipe.give.failed"));
   private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.recipe.take.failed"));

   public RecipeCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("recipe").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("give").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((var0x) -> {
         return giveRecipes((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(var0x, "recipe")));
      }))).then(Commands.literal("*").executes((var0x) -> {
         return giveRecipes((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getServer().getRecipeManager().getRecipes());
      }))))).then(Commands.literal("take").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((var0x) -> {
         return takeRecipes((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(var0x, "recipe")));
      }))).then(Commands.literal("*").executes((var0x) -> {
         return takeRecipes((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getServer().getRecipeManager().getRecipes());
      })))));
   }

   private static int giveRecipes(CommandSourceStack var0, Collection<ServerPlayer> var1, Collection<RecipeHolder<?>> var2) throws CommandSyntaxException {
      int var3 = 0;

      ServerPlayer var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.awardRecipes(var2)) {
         var5 = (ServerPlayer)var4.next();
      }

      if (var3 == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.recipe.give.success.single", var2.size(), ((ServerPlayer)var1.iterator().next()).getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.recipe.give.success.multiple", var2.size(), var1.size());
            }, true);
         }

         return var3;
      }
   }

   private static int takeRecipes(CommandSourceStack var0, Collection<ServerPlayer> var1, Collection<RecipeHolder<?>> var2) throws CommandSyntaxException {
      int var3 = 0;

      ServerPlayer var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.resetRecipes(var2)) {
         var5 = (ServerPlayer)var4.next();
      }

      if (var3 == 0) {
         throw ERROR_TAKE_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.recipe.take.success.single", var2.size(), ((ServerPlayer)var1.iterator().next()).getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.recipe.take.success.multiple", var2.size(), var1.size());
            }, true);
         }

         return var3;
      }
   }
}
