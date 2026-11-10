package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.timeline.Timeline;

public class ResourceArgument implements SerializableArgument<Holder.Reference<?>> {
   private static final MapCodec<ResourceArgument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.REGISTRY_CODEC.fieldOf("registry").forGetter((o) -> o.registryKey)).apply(i, ResourceArgument::new));
   private static final StreamCodec<ByteBuf, ResourceArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<ResourceArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_NOT_SUMMONABLE_ENTITY;
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_RESOURCE;
   public static final Dynamic3CommandExceptionType ERROR_INVALID_RESOURCE_TYPE;
   private final ResourceKey<? extends Registry<?>> registryKey;

   private ResourceArgument(final ResourceKey<? extends Registry<?>> registryKey) {
      super();
      this.registryKey = registryKey;
   }

   public SerializableArgument.Type<ResourceArgument> type() {
      return TYPE;
   }

   public static <T> ArgumentType<Holder.Reference<?>> resource(final ResourceKey<? extends Registry<T>> key) {
      return (new ResourceArgument(key)).toBrigadier();
   }

   public static <T> Holder.Reference<T> getResource(final CommandContext<?> context, final String name, final ResourceKey<Registry<T>> registryKey) throws CommandSyntaxException {
      Holder.Reference<T> argument = (Holder.Reference)context.getArgument(name, Holder.Reference.class);
      ResourceKey<?> argumentKey = argument.key();
      if (argumentKey.isFor(registryKey)) {
         return argument;
      } else {
         throw ERROR_INVALID_RESOURCE_TYPE.create(argumentKey.identifier(), argumentKey.registry(), registryKey.identifier());
      }
   }

   public static Holder.Reference<Attribute> getAttribute(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.ATTRIBUTE);
   }

   public static Holder.Reference<EntityType<?>> getSummonableEntityType(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      Holder.Reference<EntityType<?>> result = getResource(context, name, Registries.ENTITY_TYPE);
      if (!((EntityType)result.value()).canSummon()) {
         throw ERROR_NOT_SUMMONABLE_ENTITY.create(result.key().identifier().toString());
      } else {
         return result;
      }
   }

   public static Holder.Reference<Enchantment> getEnchantment(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.ENCHANTMENT);
   }

   public static Holder.Reference<WorldClock> getClock(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.WORLD_CLOCK);
   }

   public static Holder.Reference<Timeline> getTimeline(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.TIMELINE);
   }

   public static Holder.Reference<Structure> getStructure(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.STRUCTURE);
   }

   public static Holder.Reference<StructureTemplatePool> getStructureTemplatePool(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.TEMPLATE_POOL);
   }

   public static RecipeHolder<Recipe<?>> getLearnableRecipe(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      Holder.Reference<Recipe<?>> recipe = getResource(context, name, Registries.RECIPE);
      if (((Recipe)recipe.value()).isSpecial()) {
         throw ERROR_UNKNOWN_RESOURCE.create(recipe.key().identifier(), Registries.RECIPE.identifier());
      } else {
         return RecipeHolder.<Recipe<?>>wrap(recipe);
      }
   }

   public static Holder.Reference<Advancement> getAdvancement(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name, Registries.ADVANCEMENT);
   }

   public Holder.Reference<?> parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      ResourceKey<?> resourceId = ResourceKey.create(this.registryKey, Identifier.read(reader));
      return (Holder.Reference)context.holderGetters().get(resourceId).orElseThrow(() -> ERROR_UNKNOWN_RESOURCE.createWithContext(reader, resourceId, this.registryKey.identifier()));
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return ((SuggestionContext)context.getSource()).suggestRegistryElements(context, this.registryKey, SuggestionContext.ElementSuggestionType.ELEMENTS, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceKey.REGISTRY_STREAM_CODEC, (o) -> o.registryKey, ResourceArgument::new);
      TYPE = new SerializableArgument.Type<ResourceArgument>(MAP_CODEC, STREAM_CODEC);
      EXAMPLES = List.of("foo", "foo:bar", "012");
      ERROR_NOT_SUMMONABLE_ENTITY = new DynamicCommandExceptionType((value) -> Component.translatableEscape("entity.not_summonable", value));
      ERROR_UNKNOWN_RESOURCE = new Dynamic2CommandExceptionType((id, registry) -> Component.translatableEscape("argument.resource.not_found", id, registry));
      ERROR_INVALID_RESOURCE_TYPE = new Dynamic3CommandExceptionType((id, actualRegistry, expectedRegistry) -> Component.translatableEscape("argument.resource.invalid_type", id, actualRegistry, expectedRegistry));
   }
}
