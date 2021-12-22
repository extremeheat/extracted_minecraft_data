package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientSuggestionProvider implements SharedSuggestionProvider {
   private final ClientPacketListener connection;
   private final Minecraft minecraft;
   private int pendingSuggestionsId = -1;
   private CompletableFuture<Suggestions> pendingSuggestionsFuture;

   public ClientSuggestionProvider(ClientPacketListener var1, Minecraft var2) {
      super();
      this.connection = var1;
      this.minecraft = var2;
   }

   public Collection<String> getOnlinePlayerNames() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.connection.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         PlayerInfo var3 = (PlayerInfo)var2.next();
         var1.add(var3.getProfile().getName());
      }

      return var1;
   }

   public Collection<String> getSelectedEntities() {
      return (Collection)(this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY ? Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID()) : Collections.emptyList());
   }

   public Collection<String> getAllTeams() {
      return this.connection.getLevel().getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getAvailableSoundEvents() {
      return this.minecraft.getSoundManager().getAvailableSounds();
   }

   public Stream<ResourceLocation> getRecipeNames() {
      return this.connection.getRecipeManager().getRecipeIds();
   }

   public boolean hasPermission(int var1) {
      LocalPlayer var2 = this.minecraft.player;
      return var2 != null ? var2.hasPermissions(var1) : var1 == 0;
   }

   public CompletableFuture<Suggestions> customSuggestion(CommandContext<SharedSuggestionProvider> var1, SuggestionsBuilder var2) {
      if (this.pendingSuggestionsFuture != null) {
         this.pendingSuggestionsFuture.cancel(false);
      }

      this.pendingSuggestionsFuture = new CompletableFuture();
      int var3 = ++this.pendingSuggestionsId;
      this.connection.send((Packet)(new ServerboundCommandSuggestionPacket(var3, var1.getInput())));
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
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(var2.field_414), prettyPrint(var2.field_415), prettyPrint(var2.field_416)));
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

   public void completeCustomSuggestions(int var1, Suggestions var2) {
      if (var1 == this.pendingSuggestionsId) {
         this.pendingSuggestionsFuture.complete(var2);
         this.pendingSuggestionsFuture = null;
         this.pendingSuggestionsId = -1;
      }

   }
}
