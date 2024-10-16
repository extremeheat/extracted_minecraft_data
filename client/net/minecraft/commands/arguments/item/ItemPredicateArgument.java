package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
   static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("argument.item.id.invalid", var0)
   );
   static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("arguments.item.tag.unknown", var0)
   );
   static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("arguments.item.component.unknown", var0)
   );
   static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("arguments.item.component.malformed", var0, var1)
   );
   static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("arguments.item.predicate.unknown", var0)
   );
   static final Dynamic2CommandExceptionType ERROR_MALFORMED_PREDICATE = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("arguments.item.predicate.malformed", var0, var1)
   );
   private static final ResourceLocation COUNT_ID = ResourceLocation.withDefaultNamespace("count");
   static final Map<ResourceLocation, ItemPredicateArgument.ComponentWrapper> PSEUDO_COMPONENTS = Stream.of(
         new ItemPredicateArgument.ComponentWrapper(COUNT_ID, var0 -> true, MinMaxBounds.Ints.CODEC.map(var0 -> var1 -> var0.matches(var1.getCount())))
      )
      .collect(Collectors.toUnmodifiableMap(ItemPredicateArgument.ComponentWrapper::id, var0 -> (ItemPredicateArgument.ComponentWrapper)var0));
   static final Map<ResourceLocation, ItemPredicateArgument.PredicateWrapper> PSEUDO_PREDICATES = Stream.of(
         new ItemPredicateArgument.PredicateWrapper(COUNT_ID, MinMaxBounds.Ints.CODEC.map(var0 -> var1 -> var0.matches(var1.getCount())))
      )
      .collect(Collectors.toUnmodifiableMap(ItemPredicateArgument.PredicateWrapper::id, var0 -> (ItemPredicateArgument.PredicateWrapper)var0));
   private final Grammar<List<Predicate<ItemStack>>> grammarWithContext;

   public ItemPredicateArgument(CommandBuildContext var1) {
      super();
      ItemPredicateArgument.Context var2 = new ItemPredicateArgument.Context(var1);
      this.grammarWithContext = ComponentPredicateParser.createGrammar(var2);
   }

   public static ItemPredicateArgument itemPredicate(CommandBuildContext var0) {
      return new ItemPredicateArgument(var0);
   }

   public ItemPredicateArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      return Util.allOf(this.grammarWithContext.parseForCommands(var1))::test;
   }

   public static ItemPredicateArgument.Result getItemPredicate(CommandContext<CommandSourceStack> var0, String var1) {
      return (ItemPredicateArgument.Result)var0.getArgument(var1, ItemPredicateArgument.Result.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return this.grammarWithContext.parseForSuggestions(var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static class Context
      implements ComponentPredicateParser.Context<Predicate<ItemStack>, ItemPredicateArgument.ComponentWrapper, ItemPredicateArgument.PredicateWrapper> {
      private final HolderLookup.RegistryLookup<Item> items;
      private final HolderLookup.RegistryLookup<DataComponentType<?>> components;
      private final HolderLookup.RegistryLookup<ItemSubPredicate.Type<?>> predicates;
      private final RegistryOps<Tag> registryOps;

      Context(HolderLookup.Provider var1) {
         super();
         this.items = var1.lookupOrThrow(Registries.ITEM);
         this.components = var1.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
         this.predicates = var1.lookupOrThrow(Registries.ITEM_SUB_PREDICATE_TYPE);
         this.registryOps = var1.createSerializationContext(NbtOps.INSTANCE);
      }

      public Predicate<ItemStack> forElementType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         Holder.Reference var3 = this.items
            .get(ResourceKey.create(Registries.ITEM, var2))
            .orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_ITEM.createWithContext(var1, var2));
         return var1x -> var1x.is(var3);
      }

      public Predicate<ItemStack> forTagType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         HolderSet var3 = this.items
            .get(TagKey.create(Registries.ITEM, var2))
            .orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_TAG.createWithContext(var1, var2));
         return var1x -> var1x.is(var3);
      }

      public ItemPredicateArgument.ComponentWrapper lookupComponentType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         ItemPredicateArgument.ComponentWrapper var3 = ItemPredicateArgument.PSEUDO_COMPONENTS.get(var2);
         if (var3 != null) {
            return var3;
         } else {
            DataComponentType var4 = this.components
               .get(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, var2))
               .map(Holder::value)
               .orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_COMPONENT.createWithContext(var1, var2));
            return ItemPredicateArgument.ComponentWrapper.create(var1, var2, var4);
         }
      }

      public Predicate<ItemStack> createComponentTest(ImmutableStringReader var1, ItemPredicateArgument.ComponentWrapper var2, Tag var3) throws CommandSyntaxException {
         return var2.decode(var1, this.registryOps, var3);
      }

      public Predicate<ItemStack> createComponentTest(ImmutableStringReader var1, ItemPredicateArgument.ComponentWrapper var2) {
         return var2.presenceChecker;
      }

      public ItemPredicateArgument.PredicateWrapper lookupPredicateType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         ItemPredicateArgument.PredicateWrapper var3 = ItemPredicateArgument.PSEUDO_PREDICATES.get(var2);
         return var3 != null
            ? var3
            : this.predicates
               .get(ResourceKey.create(Registries.ITEM_SUB_PREDICATE_TYPE, var2))
               .map(ItemPredicateArgument.PredicateWrapper::new)
               .orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_PREDICATE.createWithContext(var1, var2));
      }

      public Predicate<ItemStack> createPredicateTest(ImmutableStringReader var1, ItemPredicateArgument.PredicateWrapper var2, Tag var3) throws CommandSyntaxException {
         return var2.decode(var1, this.registryOps, var3);
      }

      @Override
      public Stream<ResourceLocation> listElementTypes() {
         return this.items.listElementIds().map(ResourceKey::location);
      }

      @Override
      public Stream<ResourceLocation> listTagTypes() {
         return this.items.listTagIds().map(TagKey::location);
      }

      @Override
      public Stream<ResourceLocation> listComponentTypes() {
         return Stream.concat(
            ItemPredicateArgument.PSEUDO_COMPONENTS.keySet().stream(),
            this.components.listElements().filter(var0 -> !var0.value().isTransient()).map(var0 -> var0.key().location())
         );
      }

      @Override
      public Stream<ResourceLocation> listPredicateTypes() {
         return Stream.concat(ItemPredicateArgument.PSEUDO_PREDICATES.keySet().stream(), this.predicates.listElementIds().map(ResourceKey::location));
      }

      public Predicate<ItemStack> negate(Predicate<ItemStack> var1) {
         return var1.negate();
      }

      public Predicate<ItemStack> anyOf(List<Predicate<ItemStack>> var1) {
         return Util.anyOf(var1);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public interface Result extends Predicate<ItemStack> {
   }
}
