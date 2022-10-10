package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.TextComponentTranslation;

public class RecipeCommand {
   private static final SimpleCommandExceptionType field_198595_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.recipe.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198596_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.recipe.take.failed", new Object[0]));

   public static void func_198589_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("recipe").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("give").then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(Commands.func_197056_a("recipe", ResourceLocationArgument.func_197197_a()).suggests(SuggestionProviders.field_197503_b).executes((var0x) -> {
         return func_198594_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), Collections.singleton(ResourceLocationArgument.func_197194_b(var0x, "recipe")));
      }))).then(Commands.func_197057_a("*").executes((var0x) -> {
         return func_198594_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197028_i().func_199529_aN().func_199510_b());
      }))))).then(Commands.func_197057_a("take").then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(Commands.func_197056_a("recipe", ResourceLocationArgument.func_197197_a()).suggests(SuggestionProviders.field_197503_b).executes((var0x) -> {
         return func_198590_b((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), Collections.singleton(ResourceLocationArgument.func_197194_b(var0x, "recipe")));
      }))).then(Commands.func_197057_a("*").executes((var0x) -> {
         return func_198590_b((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197028_i().func_199529_aN().func_199510_b());
      })))));
   }

   private static int func_198594_a(CommandSource var0, Collection<EntityPlayerMP> var1, Collection<IRecipe> var2) throws CommandSyntaxException {
      int var3 = 0;

      EntityPlayerMP var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.func_195065_a(var2)) {
         var5 = (EntityPlayerMP)var4.next();
      }

      if (var3 == 0) {
         throw field_198595_a.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.recipe.give.success.single", new Object[]{var2.size(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.recipe.give.success.multiple", new Object[]{var2.size(), var1.size()}), true);
         }

         return var3;
      }
   }

   private static int func_198590_b(CommandSource var0, Collection<EntityPlayerMP> var1, Collection<IRecipe> var2) throws CommandSyntaxException {
      int var3 = 0;

      EntityPlayerMP var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.func_195069_b(var2)) {
         var5 = (EntityPlayerMP)var4.next();
      }

      if (var3 == 0) {
         throw field_198596_b.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.recipe.take.success.single", new Object[]{var2.size(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.recipe.take.success.multiple", new Object[]{var2.size(), var1.size()}), true);
         }

         return var3;
      }
   }
}
