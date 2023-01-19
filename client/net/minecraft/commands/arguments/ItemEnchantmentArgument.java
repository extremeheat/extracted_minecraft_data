package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemEnchantmentArgument implements ArgumentType<Enchantment> {
   private static final Collection<String> EXAMPLES = Arrays.asList("unbreaking", "silk_touch");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENCHANTMENT = new DynamicCommandExceptionType(
      var0 -> Component.translatable("enchantment.unknown", var0)
   );

   public ItemEnchantmentArgument() {
      super();
   }

   public static ItemEnchantmentArgument enchantment() {
      return new ItemEnchantmentArgument();
   }

   public static Enchantment getEnchantment(CommandContext<CommandSourceStack> var0, String var1) {
      return (Enchantment)var0.getArgument(var1, Enchantment.class);
   }

   public Enchantment parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var1);
      return Registry.ENCHANTMENT.getOptional(var2).orElseThrow(() -> ERROR_UNKNOWN_ENCHANTMENT.create(var2));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggestResource(Registry.ENCHANTMENT.keySet(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
