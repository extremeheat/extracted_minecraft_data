package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument<T> implements ArgumentType<ResourceOrTagKeyArgument.Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagKeyArgument(ResourceKey<? extends Registry<T>> var1) {
      super();
      this.registryKey = var1;
   }

   public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(ResourceKey<? extends Registry<T>> var0) {
      return new ResourceOrTagKeyArgument<>(var0);
   }

   public static <T> ResourceOrTagKeyArgument.Result<T> getResourceOrTagKey(
      CommandContext<CommandSourceStack> var0, String var1, ResourceKey<Registry<T>> var2, DynamicCommandExceptionType var3
   ) throws CommandSyntaxException {
      ResourceOrTagKeyArgument.Result var4 = (ResourceOrTagKeyArgument.Result)var0.getArgument(var1, ResourceOrTagKeyArgument.Result.class);
      Optional var5 = var4.cast(var2);
      return (ResourceOrTagKeyArgument.Result<T>)var5.orElseThrow(() -> var3.create(var4));
   }

   public ResourceOrTagKeyArgument.Result<T> parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '#') {
         int var5 = var1.getCursor();

         try {
            var1.skip();
            ResourceLocation var3 = ResourceLocation.read(var1);
            return new ResourceOrTagKeyArgument.TagResult<>(TagKey.create(this.registryKey, var3));
         } catch (CommandSyntaxException var4) {
            var1.setCursor(var5);
            throw var4;
         }
      } else {
         ResourceLocation var2 = ResourceLocation.read(var1);
         return new ResourceOrTagKeyArgument.ResourceResult<>(ResourceKey.create(this.registryKey, var2));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return var1.getSource() instanceof SharedSuggestionProvider var3
         ? var3.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL, var2, var1)
         : var2.buildFuture();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ResourceOrTagKeyArgument.Info<T>.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(ResourceOrTagKeyArgument.Info<T>.Template var1, FriendlyByteBuf var2) {
         var2.writeResourceKey(var1.registryKey);
      }

      public ResourceOrTagKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf var1) {
         return new ResourceOrTagKeyArgument.Info.Template(var1.readRegistryKey());
      }

      public void serializeToJson(ResourceOrTagKeyArgument.Info<T>.Template var1, JsonObject var2) {
         var2.addProperty("registry", var1.registryKey.location().toString());
      }

      public ResourceOrTagKeyArgument.Info<T>.Template unpack(ResourceOrTagKeyArgument<T> var1) {
         return new ResourceOrTagKeyArgument.Info.Template(var1.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagKeyArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(ResourceKey<? extends Registry<T>> var2) {
            super();
            this.registryKey = var2;
         }

         public ResourceOrTagKeyArgument<T> instantiate(CommandBuildContext var1) {
            return new ResourceOrTagKeyArgument<>(this.registryKey);
         }

         @Override
         public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }

   static record ResourceResult<T>(ResourceKey<T> key) implements ResourceOrTagKeyArgument.Result<T> {
      ResourceResult(ResourceKey<T> key) {
         super();
         this.key = key;
      }

      @Override
      public Either<ResourceKey<T>, TagKey<T>> unwrap() {
         return Either.left(this.key);
      }

      @Override
      public <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1) {
         return this.key.<T>cast(var1).map(ResourceOrTagKeyArgument.ResourceResult::new);
      }

      public boolean test(Holder<T> var1) {
         return var1.is(this.key);
      }

      @Override
      public String asPrintable() {
         return this.key.location().toString();
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<ResourceKey<T>, TagKey<T>> unwrap();

      <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

      String asPrintable();
   }

   static record TagResult<T>(TagKey<T> key) implements ResourceOrTagKeyArgument.Result<T> {
      TagResult(TagKey<T> key) {
         super();
         this.key = key;
      }

      @Override
      public Either<ResourceKey<T>, TagKey<T>> unwrap() {
         return Either.right(this.key);
      }

      @Override
      public <E> Optional<ResourceOrTagKeyArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1) {
         return this.key.<T>cast(var1).map(ResourceOrTagKeyArgument.TagResult::new);
      }

      public boolean test(Holder<T> var1) {
         return var1.is(this.key);
      }

      @Override
      public String asPrintable() {
         return "#" + this.key.location();
      }
   }
}
