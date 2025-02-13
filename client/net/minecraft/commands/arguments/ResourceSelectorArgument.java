package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public class ResourceSelectorArgument<T> implements ArgumentType<Collection<Holder.Reference<T>>> {
   private static final Collection<String> EXAMPLES = List.of("minecraft:*", "*:asset", "*");
   public static final Dynamic2CommandExceptionType ERROR_NO_MATCHES = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("argument.resource_selector.not_found", var0, var1));
   final ResourceKey<? extends Registry<T>> registryKey;
   private final HolderLookup<T> registryLookup;

   ResourceSelectorArgument(CommandBuildContext var1, ResourceKey<? extends Registry<T>> var2) {
      super();
      this.registryKey = var2;
      this.registryLookup = var1.lookupOrThrow(var2);
   }

   public Collection<Holder.Reference<T>> parse(StringReader var1) throws CommandSyntaxException {
      String var2 = ensureNamespaced(readPattern(var1));
      List var3 = this.registryLookup.listElements().filter((var1x) -> matches(var2, var1x.key().location())).toList();
      if (var3.isEmpty()) {
         throw ERROR_NO_MATCHES.createWithContext(var1, var2, this.registryKey.location());
      } else {
         return var3;
      }
   }

   public static <T> Collection<Holder.Reference<T>> parse(StringReader var0, HolderLookup<T> var1) {
      String var2 = ensureNamespaced(readPattern(var0));
      return var1.listElements().filter((var1x) -> matches(var2, var1x.key().location())).toList();
   }

   private static String readPattern(StringReader var0) {
      int var1 = var0.getCursor();

      while(var0.canRead() && isAllowedPatternCharacter(var0.peek())) {
         var0.skip();
      }

      return var0.getString().substring(var1, var0.getCursor());
   }

   private static boolean isAllowedPatternCharacter(char var0) {
      return ResourceLocation.isAllowedInResourceLocation(var0) || var0 == '*' || var0 == '?';
   }

   private static String ensureNamespaced(String var0) {
      return !var0.contains(":") ? "minecraft:" + var0 : var0;
   }

   private static boolean matches(String var0, ResourceLocation var1) {
      return FilenameUtils.wildcardMatch(var1.toString(), var0);
   }

   public static <T> ResourceSelectorArgument<T> resourceSelector(CommandBuildContext var0, ResourceKey<? extends Registry<T>> var1) {
      return new ResourceSelectorArgument<T>(var0, var1);
   }

   public static <T> Collection<Holder.Reference<T>> getSelectedResources(CommandContext<CommandSourceStack> var0, String var1, ResourceKey<? extends Registry<T>> var2) {
      return (Collection)var0.getArgument(var1, Collection.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      Object var4 = var1.getSource();
      if (var4 instanceof SharedSuggestionProvider var3) {
         return var3.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS, var2, var1);
      } else {
         return SharedSuggestionProvider.suggest(this.registryLookup.listElementIds().map(ResourceKey::location).map(ResourceLocation::toString), var2);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceSelectorArgument<T>, Info<T>.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(Info<T>.Template var1, FriendlyByteBuf var2) {
         var2.writeResourceKey(var1.registryKey);
      }

      public Info<T>.Template deserializeFromNetwork(FriendlyByteBuf var1) {
         return new Template(var1.readRegistryKey());
      }

      public void serializeToJson(Info<T>.Template var1, JsonObject var2) {
         var2.addProperty("registry", var1.registryKey.location().toString());
      }

      public Info<T>.Template unpack(ResourceSelectorArgument<T> var1) {
         return new Template(var1.registryKey);
      }

      // $FF: synthetic method
      public ArgumentTypeInfo.Template deserializeFromNetwork(final FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceSelectorArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(final ResourceKey<? extends Registry<T>> var2) {
            super();
            this.registryKey = var2;
         }

         public ResourceSelectorArgument<T> instantiate(CommandBuildContext var1) {
            return new ResourceSelectorArgument<T>(var1, this.registryKey);
         }

         public ArgumentTypeInfo<ResourceSelectorArgument<T>, ?> type() {
            return Info.this;
         }

         // $FF: synthetic method
         public ArgumentType instantiate(final CommandBuildContext var1) {
            return this.instantiate(var1);
         }
      }
   }
}
