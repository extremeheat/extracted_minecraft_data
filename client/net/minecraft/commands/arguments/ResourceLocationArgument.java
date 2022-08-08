package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("advancement.advancementNotFound", var0);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("recipe.notFound", var0);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("predicate.unknown", var0);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM_MODIFIER = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("item_modifier.unknown", var0);
   });

   public ResourceLocationArgument() {
      super();
   }

   public static ResourceLocationArgument id() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = getId(var0, var1);
      Advancement var3 = ((CommandSourceStack)var0.getSource()).getServer().getAdvancements().getAdvancement(var2);
      if (var3 == null) {
         throw ERROR_UNKNOWN_ADVANCEMENT.create(var2);
      } else {
         return var3;
      }
   }

   public static Recipe<?> getRecipe(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      RecipeManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getRecipeManager();
      ResourceLocation var3 = getId(var0, var1);
      return (Recipe)var2.byKey(var3).orElseThrow(() -> {
         return ERROR_UNKNOWN_RECIPE.create(var3);
      });
   }

   public static LootItemCondition getPredicate(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = getId(var0, var1);
      PredicateManager var3 = ((CommandSourceStack)var0.getSource()).getServer().getPredicateManager();
      LootItemCondition var4 = var3.get(var2);
      if (var4 == null) {
         throw ERROR_UNKNOWN_PREDICATE.create(var2);
      } else {
         return var4;
      }
   }

   public static LootItemFunction getItemModifier(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = getId(var0, var1);
      ItemModifierManager var3 = ((CommandSourceStack)var0.getSource()).getServer().getItemModifierManager();
      LootItemFunction var4 = var3.get(var2);
      if (var4 == null) {
         throw ERROR_UNKNOWN_ITEM_MODIFIER.create(var2);
      } else {
         return var4;
      }
   }

   public static ResourceLocation getId(CommandContext<CommandSourceStack> var0, String var1) {
      return (ResourceLocation)var0.getArgument(var1, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader var1) throws CommandSyntaxException {
      return ResourceLocation.read(var1);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
