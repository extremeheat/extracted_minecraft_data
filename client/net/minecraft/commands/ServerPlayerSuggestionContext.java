package net.minecraft.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ServerPlayerSuggestionContext implements SuggestionContext {
   private final MinecraftServer server;
   private final ServerPlayer serverPlayer;
   private final HolderLookup.Provider filteredProvider;

   public ServerPlayerSuggestionContext(final ServerPlayer serverPlayer) {
      super();
      this.serverPlayer = serverPlayer;
      ServerLevel level = serverPlayer.level();
      this.server = level.getServer();
      this.filteredProvider = this.server.reloadableRegistries().lookup().filterFeatures(level.enabledFeatures());
   }

   public PermissionSet permissions() {
      return this.serverPlayer.permissions();
   }

   public Vec3 position() {
      return this.serverPlayer.position();
   }

   public ServerLevel level() {
      return this.serverPlayer.level();
   }

   public FeatureFlagSet enabledFeatures() {
      return this.level().enabledFeatures();
   }

   public Entity entity() {
      return this.serverPlayer;
   }

   public MinecraftServer server() {
      return this.server;
   }

   public Collection<String> onlinePlayerNames() {
      return List.of(this.server.getPlayerNames());
   }

   public Collection<String> allTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Stream<Identifier> availableSounds() {
      return BuiltInRegistries.SOUND_EVENT.stream().map(SoundEvent::location);
   }

   public Stream<Identifier> availablePostEffects() {
      return Stream.empty();
   }

   public CompletableFuture<Suggestions> suggestObjectiveNames(final CommandContext<?> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(this.server.getScoreboard().getObjectiveNames(), builder);
   }

   public CompletableFuture<Suggestions> customSuggestion(final CommandContext<?> context) {
      return Suggestions.empty();
   }

   public <E> CompletableFuture<Suggestions> suggestRegistryElements(final CommandContext<?> context, final ResourceKey<? extends Registry<? extends E>> key, final SuggestionContext.ElementSuggestionType elements, final SuggestionsBuilder builder, final Predicate<E> filter) {
      return (CompletableFuture)this.holderLookups().lookup(key).map((registry) -> {
         this.suggestRegistryElements(registry, elements, builder, filter);
         return builder.buildFuture();
      }).orElseGet(Suggestions::empty);
   }

   public Set<ResourceKey<Level>> levels() {
      return this.server.levelKeys();
   }

   public HolderLookup.Provider holderLookups() {
      return this.filteredProvider;
   }
}
