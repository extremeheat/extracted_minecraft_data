package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack implements SharedSuggestionProvider {
   public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.player", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.entity", new Object[0]));
   private final CommandSource source;
   private final Vec3 worldPosition;
   private final ServerLevel level;
   private final int permissionLevel;
   private final String textName;
   private final Component displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   private final ResultConsumer consumer;
   private final EntityAnchorArgument.Anchor anchor;
   private final Vec2 rotation;

   public CommandSourceStack(CommandSource var1, Vec3 var2, Vec2 var3, ServerLevel var4, int var5, String var6, Component var7, MinecraftServer var8, @Nullable Entity var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, false, (var0, var1x, var2x) -> {
      }, EntityAnchorArgument.Anchor.FEET);
   }

   protected CommandSourceStack(CommandSource var1, Vec3 var2, Vec2 var3, ServerLevel var4, int var5, String var6, Component var7, MinecraftServer var8, @Nullable Entity var9, boolean var10, ResultConsumer var11, EntityAnchorArgument.Anchor var12) {
      this.source = var1;
      this.worldPosition = var2;
      this.level = var4;
      this.silent = var10;
      this.entity = var9;
      this.permissionLevel = var5;
      this.textName = var6;
      this.displayName = var7;
      this.server = var8;
      this.consumer = var11;
      this.anchor = var12;
      this.rotation = var3;
   }

   public CommandSourceStack withEntity(Entity var1) {
      return this.entity == var1 ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, var1.getName().getString(), var1.getDisplayName(), this.server, var1, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withPosition(Vec3 var1) {
      return this.worldPosition.equals(var1) ? this : new CommandSourceStack(this.source, var1, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withRotation(Vec2 var1) {
      return this.rotation.equals(var1) ? this : new CommandSourceStack(this.source, this.worldPosition, var1, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withCallback(ResultConsumer var1) {
      return this.consumer.equals(var1) ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, var1, this.anchor);
   }

   public CommandSourceStack withCallback(ResultConsumer var1, BinaryOperator var2) {
      ResultConsumer var3 = (ResultConsumer)var2.apply(this.consumer, var1);
      return this.withCallback(var3);
   }

   public CommandSourceStack withSuppressedOutput() {
      return this.silent ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor);
   }

   public CommandSourceStack withPermission(int var1) {
      return var1 == this.permissionLevel ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, var1, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withMaximumPermission(int var1) {
      return var1 <= this.permissionLevel ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, var1, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor var1) {
      return var1 == this.anchor ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, var1);
   }

   public CommandSourceStack withLevel(ServerLevel var1) {
      return var1 == this.level ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, var1, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack facing(Entity var1, EntityAnchorArgument.Anchor var2) throws CommandSyntaxException {
      return this.facing(var2.apply(var1));
   }

   public CommandSourceStack facing(Vec3 var1) throws CommandSyntaxException {
      Vec3 var2 = this.anchor.apply(this);
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = (double)Mth.sqrt(var3 * var3 + var7 * var7);
      float var11 = Mth.wrapDegrees((float)(-(Mth.atan2(var5, var9) * 57.2957763671875D)));
      float var12 = Mth.wrapDegrees((float)(Mth.atan2(var7, var3) * 57.2957763671875D) - 90.0F);
      return this.withRotation(new Vec2(var11, var12));
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public String getTextName() {
      return this.textName;
   }

   public boolean hasPermission(int var1) {
      return this.permissionLevel >= var1;
   }

   public Vec3 getPosition() {
      return this.worldPosition;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrException() throws CommandSyntaxException {
      if (this.entity == null) {
         throw ERROR_NOT_ENTITY.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
      if (!(this.entity instanceof ServerPlayer)) {
         throw ERROR_NOT_PLAYER.create();
      } else {
         return (ServerPlayer)this.entity;
      }
   }

   public Vec2 getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Anchor getAnchor() {
      return this.anchor;
   }

   public void sendSuccess(Component var1, boolean var2) {
      if (this.source.acceptsSuccess() && !this.silent) {
         this.source.sendMessage(var1);
      }

      if (var2 && this.source.shouldInformAdmins() && !this.silent) {
         this.broadcastToAdmins(var1);
      }

   }

   private void broadcastToAdmins(Component var1) {
      Component var2 = (new TranslatableComponent("chat.type.admin", new Object[]{this.getDisplayName(), var1})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
      if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
         Iterator var3 = this.server.getPlayerList().getPlayers().iterator();

         while(var3.hasNext()) {
            ServerPlayer var4 = (ServerPlayer)var3.next();
            if (var4 != this.source && this.server.getPlayerList().isOp(var4.getGameProfile())) {
               var4.sendMessage(var2);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
         this.server.sendMessage(var2);
      }

   }

   public void sendFailure(Component var1) {
      if (this.source.acceptsFailure() && !this.silent) {
         this.source.sendMessage((new TextComponent("")).append(var1).withStyle(ChatFormatting.RED));
      }

   }

   public void onCommandComplete(CommandContext var1, boolean var2, int var3) {
      if (this.consumer != null) {
         this.consumer.onCommandComplete(var1, var2, var3);
      }

   }

   public Collection getOnlinePlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   public Collection getAllTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Collection getAvailableSoundEvents() {
      return Registry.SOUND_EVENT.keySet();
   }

   public Stream getRecipeNames() {
      return this.server.getRecipeManager().getRecipeIds();
   }

   public CompletableFuture customSuggestion(CommandContext var1, SuggestionsBuilder var2) {
      return null;
   }
}
