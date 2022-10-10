package net.minecraft.command.arguments;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class EnchantmentArgument implements ArgumentType<Enchantment> {
   private static final Collection<String> field_201947_b = Arrays.asList("unbreaking", "silk_touch");
   public static final DynamicCommandExceptionType field_201946_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("enchantment.unknown", new Object[]{var0});
   });

   public EnchantmentArgument() {
      super();
   }

   public static EnchantmentArgument func_201945_a() {
      return new EnchantmentArgument();
   }

   public static Enchantment func_201944_a(CommandContext<CommandSource> var0, String var1) {
      return (Enchantment)var0.getArgument(var1, Enchantment.class);
   }

   public Enchantment parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.func_195826_a(var1);
      Enchantment var3 = (Enchantment)IRegistry.field_212628_q.func_212608_b(var2);
      if (var3 == null) {
         throw field_201946_a.create(var2);
      } else {
         return var3;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ISuggestionProvider.func_197014_a(IRegistry.field_212628_q.func_148742_b(), var2);
   }

   public Collection<String> getExamples() {
      return field_201947_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
