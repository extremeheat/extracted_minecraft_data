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
import java.util.Objects;
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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument<T> implements ArgumentType<Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagKeyArgument(final ResourceKey<? extends Registry<T>> registryKey) {
      super();
      this.registryKey = registryKey;
   }

   public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(final ResourceKey<? extends Registry<T>> key) {
      return new ResourceOrTagKeyArgument<T>(key);
   }

   public static <T> Result<T> getResourceOrTagKey(final CommandContext<CommandSourceStack> context, final String name, final ResourceKey<Registry<T>> registryKey, final DynamicCommandExceptionType exceptionType) throws CommandSyntaxException {
      Result<?> argument = (Result)context.getArgument(name, Result.class);
      Optional<Result<T>> value = argument.cast(registryKey);
      return (Result)value.orElseThrow(() -> exceptionType.create(argument));
   }

   public Result<T> parse(final StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '#') {
         int cursor = reader.getCursor();

         try {
            reader.skip();
            Identifier tagId = Identifier.read(reader);
            return new TagResult<T>(TagKey.create(this.registryKey, tagId));
         } catch (CommandSyntaxException e) {
            reader.setCursor(cursor);
            throw e;
         }
      } else {
         Identifier resourceId = Identifier.read(reader);
         return new ResourceResult<T>(ResourceKey.create(this.registryKey, resourceId));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static record ResourceResult<T>(ResourceKey<T> key) implements Result<T> {
      private ResourceResult {
         super();
      }

      public Either<ResourceKey<T>, TagKey<T>> unwrap() {
         return Either.left(this.key);
      }

      public <E> Optional<Result<E>> cast(final ResourceKey<? extends Registry<E>> registryKey) {
         return this.key.cast(registryKey).map(ResourceResult::new);
      }

      public boolean test(final Holder<T> holder) {
         return holder.is(this.key);
      }

      public String asPrintable() {
         return this.key.identifier().toString();
      }
   }

   private static record TagResult<T>(TagKey<T> key) implements Result<T> {
      private TagResult {
         super();
      }

      public Either<ResourceKey<T>, TagKey<T>> unwrap() {
         return Either.right(this.key);
      }

      public <E> Optional<Result<E>> cast(final ResourceKey<? extends Registry<E>> registryKey) {
         return this.key.cast(registryKey).map(TagResult::new);
      }

      public boolean test(final Holder<T> holder) {
         return holder.is(this.key);
      }

      public String asPrintable() {
         return "#" + String.valueOf(this.key.location());
      }
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, Info<T>.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(final Info<T>.Template template, final FriendlyByteBuf out) {
         out.writeResourceKey(template.registryKey);
      }

      public Info<T>.Template deserializeFromNetwork(final FriendlyByteBuf in) {
         return new Template(in.readRegistryKey());
      }

      public void serializeToJson(final Info<T>.Template template, final JsonObject out) {
         out.addProperty("registry", template.registryKey.identifier().toString());
      }

      public Info<T>.Template unpack(final ResourceOrTagKeyArgument<T> argument) {
         return new Template(argument.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagKeyArgument<T>> {
         private final ResourceKey<? extends Registry<T>> registryKey;

         private Template(final ResourceKey<? extends Registry<T>> registryKey) {
            Objects.requireNonNull(Info.this);
            super();
            this.registryKey = registryKey;
         }

         public ResourceOrTagKeyArgument<T> instantiate(final CommandBuildContext context) {
            return new ResourceOrTagKeyArgument<T>(this.registryKey);
         }

         public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<ResourceKey<T>, TagKey<T>> unwrap();

      <E> Optional<Result<E>> cast(final ResourceKey<? extends Registry<E>> registryKey);

      String asPrintable();
   }
}
