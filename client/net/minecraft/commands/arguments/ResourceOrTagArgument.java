package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagArgument<T> implements ArgumentType<Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_TAG = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("argument.resource_tag.not_found", var0, var1));
   private static final Dynamic3CommandExceptionType ERROR_INVALID_TAG_TYPE = new Dynamic3CommandExceptionType((var0, var1, var2) -> Component.translatableEscape("argument.resource_tag.invalid_type", var0, var1, var2));
   private final HolderLookup<T> registryLookup;
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagArgument(CommandBuildContext var1, ResourceKey<? extends Registry<T>> var2) {
      super();
      this.registryKey = var2;
      this.registryLookup = var1.lookupOrThrow(var2);
   }

   public static <T> ResourceOrTagArgument<T> resourceOrTag(CommandBuildContext var0, ResourceKey<? extends Registry<T>> var1) {
      return new ResourceOrTagArgument<T>(var0, var1);
   }

   public static <T> Result<T> getResourceOrTag(CommandContext<CommandSourceStack> var0, String var1, ResourceKey<Registry<T>> var2) throws CommandSyntaxException {
      Result var3 = (Result)var0.getArgument(var1, Result.class);
      Optional var4 = var3.cast(var2);
      return (Result)var4.orElseThrow(() -> (CommandSyntaxException)var3.unwrap().map((var1) -> {
            ResourceKey var2x = var1.key();
            return ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create(var2x.location(), var2x.registry(), var2.location());
         }, (var1) -> {
            TagKey var2x = var1.key();
            return ERROR_INVALID_TAG_TYPE.create(var2x.location(), var2x.registry(), var2.location());
         }));
   }

   public Result<T> parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '#') {
         int var7 = var1.getCursor();

         try {
            var1.skip();
            ResourceLocation var8 = ResourceLocation.read(var1);
            TagKey var9 = TagKey.create(this.registryKey, var8);
            HolderSet.Named var5 = (HolderSet.Named)this.registryLookup.get(var9).orElseThrow(() -> ERROR_UNKNOWN_TAG.createWithContext(var1, var8, this.registryKey.location()));
            return new TagResult<T>(var5);
         } catch (CommandSyntaxException var6) {
            var1.setCursor(var7);
            throw var6;
         }
      } else {
         ResourceLocation var2 = ResourceLocation.read(var1);
         ResourceKey var3 = ResourceKey.create(this.registryKey, var2);
         Holder.Reference var4 = (Holder.Reference)this.registryLookup.get(var3).orElseThrow(() -> ResourceArgument.ERROR_UNKNOWN_RESOURCE.createWithContext(var1, var2, this.registryKey.location()));
         return new ResourceResult<T>(var4);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      SharedSuggestionProvider.suggestResource(this.registryLookup.listTagIds().map(TagKey::location), var2, "#");
      return SharedSuggestionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static record ResourceResult<T>(Holder.Reference<T> value) implements Result<T> {
      ResourceResult(Holder.Reference<T> var1) {
         super();
         this.value = var1;
      }

      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.left(this.value);
      }

      public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> var1) {
         return this.value.key().isFor(var1) ? Optional.of(this) : Optional.empty();
      }

      public boolean test(Holder<T> var1) {
         return var1.equals(this.value);
      }

      public String asPrintable() {
         return this.value.key().location().toString();
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Holder)var1);
      }
   }

   static record TagResult<T>(HolderSet.Named<T> tag) implements Result<T> {
      TagResult(HolderSet.Named<T> var1) {
         super();
         this.tag = var1;
      }

      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.right(this.tag);
      }

      public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> var1) {
         return this.tag.key().isFor(var1) ? Optional.of(this) : Optional.empty();
      }

      public boolean test(Holder<T> var1) {
         return this.tag.contains(var1);
      }

      public String asPrintable() {
         return "#" + String.valueOf(this.tag.key().location());
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Holder)var1);
      }
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagArgument<T>, Info<T>.Template> {
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

      public Info<T>.Template unpack(ResourceOrTagArgument<T> var1) {
         return new Template(var1.registryKey);
      }

      // $FF: synthetic method
      public ArgumentTypeInfo.Template deserializeFromNetwork(final FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(final ResourceKey<? extends Registry<T>> var2) {
            super();
            this.registryKey = var2;
         }

         public ResourceOrTagArgument<T> instantiate(CommandBuildContext var1) {
            return new ResourceOrTagArgument<T>(var1, this.registryKey);
         }

         public ArgumentTypeInfo<ResourceOrTagArgument<T>, ?> type() {
            return Info.this;
         }

         // $FF: synthetic method
         public ArgumentType instantiate(final CommandBuildContext var1) {
            return this.instantiate(var1);
         }
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap();

      <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

      String asPrintable();
   }
}
