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
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class PotionArgument implements ArgumentType<Potion> {
   private static final Collection<String> field_201314_b = Arrays.asList("spooky", "effect");
   public static final DynamicCommandExceptionType field_197128_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("effect.effectNotFound", new Object[]{var0});
   });

   public PotionArgument() {
      super();
   }

   public static PotionArgument func_197126_a() {
      return new PotionArgument();
   }

   public static Potion func_197125_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return (Potion)var0.getArgument(var1, Potion.class);
   }

   public Potion parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.func_195826_a(var1);
      Potion var3 = (Potion)IRegistry.field_212631_t.func_212608_b(var2);
      if (var3 == null) {
         throw field_197128_a.create(var2);
      } else {
         return var3;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ISuggestionProvider.func_197014_a(IRegistry.field_212631_t.func_148742_b(), var2);
   }

   public Collection<String> getExamples() {
      return field_201314_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
