package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientSuggestionProvider implements SharedSuggestionProvider {
   private final ClientPacketListener connection;
   private final Minecraft minecraft;
   private int pendingSuggestionsId = -1;
   @Nullable
   private CompletableFuture<Suggestions> pendingSuggestionsFuture;
   private final Set<String> customCompletionSuggestions = new HashSet();

   public ClientSuggestionProvider(ClientPacketListener var1, Minecraft var2) {
      super();
      this.connection = var1;
      this.minecraft = var2;
   }

   public Collection<String> getOnlinePlayerNames() {
      ArrayList var1 = Lists.newArrayList();

      for(PlayerInfo var3 : this.connection.getOnlinePlayers()) {
         var1.add(var3.getProfile().getName());
      }

      return var1;
   }

   public Collection<String> getCustomTabSugggestions() {
      if (this.customCompletionSuggestions.isEmpty()) {
         return this.getOnlinePlayerNames();
      } else {
         HashSet var1 = new HashSet(this.getOnlinePlayerNames());
         var1.addAll(this.customCompletionSuggestions);
         return var1;
      }
   }

   public Collection<String> getSelectedEntities() {
      return (Collection<String>)(this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY ? Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID()) : Collections.emptyList());
   }

   public Collection<String> getAllTeams() {
      return this.connection.scoreboard().getTeamNames();
   }

   public Stream<ResourceLocation> getAvailableSounds() {
      return this.minecraft.getSoundManager().getAvailableSounds().stream();
   }

   public boolean hasPermission(int var1) {
      LocalPlayer var2 = this.minecraft.player;
      return var2 != null ? var2.hasPermissions(var1) : var1 == 0;
   }

   public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> var1, SharedSuggestionProvider.ElementSuggestionType var2, SuggestionsBuilder var3, CommandContext<?> var4) {
      return (CompletableFuture)this.registryAccess().lookup(var1).map((var3x) -> {
         this.suggestRegistryElements(var3x, var2, var3);
         return var3.buildFuture();
      }).orElseGet(() -> this.customSuggestion(var4));
   }

   public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> var1) {
      if (this.pendingSuggestionsFuture != null) {
         this.pendingSuggestionsFuture.cancel(false);
      }

      this.pendingSuggestionsFuture = new CompletableFuture();
      int var2 = ++this.pendingSuggestionsId;
      this.connection.send(new ServerboundCommandSuggestionPacket(var2, var1.getInput()));
      return this.pendingSuggestionsFuture;
   }

   private static String prettyPrint(double var0) {
      return String.format(Locale.ROOT, "%.2f", var0);
   }

   private static String prettyPrint(int var0) {
      return Integer.toString(var0);
   }

   public Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
      HitResult var1 = this.minecraft.hitResult;
      if (var1 != null && var1.getType() == HitResult.Type.BLOCK) {
         BlockPos var2 = ((BlockHitResult)var1).getBlockPos();
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(var2.getX()), prettyPrint(var2.getY()), prettyPrint(var2.getZ())));
      } else {
         return SharedSuggestionProvider.super.getRelevantCoordinates();
      }
   }

   public Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
      HitResult var1 = this.minecraft.hitResult;
      if (var1 != null && var1.getType() == HitResult.Type.BLOCK) {
         Vec3 var2 = var1.getLocation();
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(var2.x), prettyPrint(var2.y), prettyPrint(var2.z)));
      } else {
         return SharedSuggestionProvider.super.getAbsoluteCoordinates();
      }
   }

   public Set<ResourceKey<Level>> levels() {
      return this.connection.levels();
   }

   public RegistryAccess registryAccess() {
      return this.connection.registryAccess();
   }

   public FeatureFlagSet enabledFeatures() {
      return this.connection.enabledFeatures();
   }

   public void completeCustomSuggestions(int var1, Suggestions var2) {
      if (var1 == this.pendingSuggestionsId) {
         this.pendingSuggestionsFuture.complete(var2);
         this.pendingSuggestionsFuture = null;
         this.pendingSuggestionsId = -1;
      }

   }

   public void modifyCustomCompletions(ClientboundCustomChatCompletionsPacket.Action var1, List<String> var2) {
      switch (var1) {
         case ADD:
            this.customCompletionSuggestions.addAll(var2);
            break;
         case REMOVE:
            Set var10001 = this.customCompletionSuggestions;
            Objects.requireNonNull(var10001);
            var2.forEach(var10001::remove);
            break;
         case SET:
            this.customCompletionSuggestions.clear();
            this.customCompletionSuggestions.addAll(var2);
      }

   }
}
