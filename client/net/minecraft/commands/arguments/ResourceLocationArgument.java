package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("advancement.advancementNotFound", var0)
   );
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("recipe.notFound", var0)
   );

   public ResourceLocationArgument() {
      super();
   }

   public static ResourceLocationArgument id() {
      return new ResourceLocationArgument();
   }

   public static AdvancementHolder getAdvancement(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = getId(var0, var1);
      AdvancementHolder var3 = ((CommandSourceStack)var0.getSource()).getServer().getAdvancements().get(var2);
      if (var3 == null) {
         throw ERROR_UNKNOWN_ADVANCEMENT.create(var2);
      } else {
         return var3;
      }
   }

   public static RecipeHolder<?> getRecipe(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      RecipeManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getRecipeManager();
      ResourceLocation var3 = getId(var0, var1);
      return (RecipeHolder<?>)var2.byKey(var3).orElseThrow(() -> ERROR_UNKNOWN_RECIPE.create(var3));
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
}
