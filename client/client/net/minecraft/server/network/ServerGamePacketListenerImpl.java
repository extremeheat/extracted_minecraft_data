package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundDebugSampleSubscriptionPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.FutureChain;
import net.minecraft.util.Mth;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class ServerGamePacketListenerImpl
   extends ServerCommonPacketListenerImpl
   implements ServerGamePacketListener,
   ServerPlayerConnection,
   TickablePacketListener {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_BLOCK_UPDATES_TO_ACK = -1;
   private static final int TRACKED_MESSAGE_DISCONNECT_THRESHOLD = 4096;
   private static final int MAXIMUM_FLYING_TICKS = 80;
   private static final Component CHAT_VALIDATION_FAILED = Component.translatable("multiplayer.disconnect.chat_validation_failed");
   private static final Component INVALID_COMMAND_SIGNATURE = Component.translatable("chat.disabled.invalid_command_signature").withStyle(ChatFormatting.RED);
   private static final int MAX_COMMAND_SUGGESTIONS = 1000;
   public ServerPlayer player;
   public final PlayerChunkSender chunkSender;
   private int tickCount;
   private int ackBlockChangesUpTo = -1;
   private int chatSpamTickCount;
   private int dropSpamTickCount;
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   @Nullable
   private Entity lastVehicle;
   private double vehicleFirstGoodX;
   private double vehicleFirstGoodY;
   private double vehicleFirstGoodZ;
   private double vehicleLastGoodX;
   private double vehicleLastGoodY;
   private double vehicleLastGoodZ;
   @Nullable
   private Vec3 awaitingPositionFromClient;
   private int awaitingTeleport;
   private int awaitingTeleportTime;
   private boolean clientIsFloating;
   private int aboveGroundTickCount;
   private boolean clientVehicleIsFloating;
   private int aboveGroundVehicleTickCount;
   private int receivedMovePacketCount;
   private int knownMovePacketCount;
   @Nullable
   private RemoteChatSession chatSession;
   private SignedMessageChain.Decoder signedMessageDecoder;
   private final LastSeenMessagesValidator lastSeenMessages = new LastSeenMessagesValidator(20);
   private final MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
   private final FutureChain chatMessageChain;
   private boolean waitingForSwitchToConfig;

   public ServerGamePacketListenerImpl(MinecraftServer var1, Connection var2, ServerPlayer var3, CommonListenerCookie var4) {
      super(var1, var2, var4);
      this.chunkSender = new PlayerChunkSender(var2.isMemoryConnection());
      this.player = var3;
      var3.connection = this;
      var3.getTextFilter().join();
      this.signedMessageDecoder = SignedMessageChain.Decoder.unsigned(var3.getUUID(), var1::enforceSecureProfile);
      this.chatMessageChain = new FutureChain(var1);
   }

   @Override
   public void tick() {
      if (this.ackBlockChangesUpTo > -1) {
         this.send(new ClientboundBlockChangedAckPacket(this.ackBlockChangesUpTo));
         this.ackBlockChangesUpTo = -1;
      }

      this.resetPosition();
      this.player.xo = this.player.getX();
      this.player.yo = this.player.getY();
      this.player.zo = this.player.getZ();
      this.player.doTick();
      this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.getYRot(), this.player.getXRot());
      this.tickCount++;
      this.knownMovePacketCount = this.receivedMovePacketCount;
      if (this.clientIsFloating && !this.player.isSleeping() && !this.player.isPassenger() && !this.player.isDeadOrDying()) {
         if (++this.aboveGroundTickCount > this.getMaximumFlyingTicks(this.player)) {
            LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
            this.disconnect(Component.translatable("multiplayer.disconnect.flying"));
            return;
         }
      } else {
         this.clientIsFloating = false;
         this.aboveGroundTickCount = 0;
      }

      this.lastVehicle = this.player.getRootVehicle();
      if (this.lastVehicle != this.player && this.lastVehicle.getControllingPassenger() == this.player) {
         this.vehicleFirstGoodX = this.lastVehicle.getX();
         this.vehicleFirstGoodY = this.lastVehicle.getY();
         this.vehicleFirstGoodZ = this.lastVehicle.getZ();
         this.vehicleLastGoodX = this.lastVehicle.getX();
         this.vehicleLastGoodY = this.lastVehicle.getY();
         this.vehicleLastGoodZ = this.lastVehicle.getZ();
         if (this.clientVehicleIsFloating && this.lastVehicle.getControllingPassenger() == this.player) {
            if (++this.aboveGroundVehicleTickCount > this.getMaximumFlyingTicks(this.lastVehicle)) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
               this.disconnect(Component.translatable("multiplayer.disconnect.flying"));
               return;
            }
         } else {
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
         }
      } else {
         this.lastVehicle = null;
         this.clientVehicleIsFloating = false;
         this.aboveGroundVehicleTickCount = 0;
      }

      this.keepConnectionAlive();
      if (this.chatSpamTickCount > 0) {
         this.chatSpamTickCount--;
      }

      if (this.dropSpamTickCount > 0) {
         this.dropSpamTickCount--;
      }

      if (this.player.getLastActionTime() > 0L
         && this.server.getPlayerIdleTimeout() > 0
         && Util.getMillis() - this.player.getLastActionTime() > (long)this.server.getPlayerIdleTimeout() * 1000L * 60L) {
         this.disconnect(Component.translatable("multiplayer.disconnect.idling"));
      }
   }

   private int getMaximumFlyingTicks(Entity var1) {
      double var2 = var1.getGravity();
      if (var2 < 9.999999747378752E-6) {
         return 2147483647;
      } else {
         double var4 = 0.08 / var2;
         return Mth.ceil(80.0 * Math.max(var4, 1.0));
      }
   }

   public void resetPosition() {
      this.firstGoodX = this.player.getX();
      this.firstGoodY = this.player.getY();
      this.firstGoodZ = this.player.getZ();
      this.lastGoodX = this.player.getX();
      this.lastGoodY = this.player.getY();
      this.lastGoodZ = this.player.getZ();
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected() && !this.waitingForSwitchToConfig;
   }

   @Override
   public boolean shouldHandleMessage(Packet<?> var1) {
      return super.shouldHandleMessage(var1)
         ? true
         : this.waitingForSwitchToConfig && this.connection.isConnected() && var1 instanceof ServerboundConfigurationAcknowledgedPacket;
   }

   @Override
   protected GameProfile playerProfile() {
      return this.player.getGameProfile();
   }

   private <T, R> CompletableFuture<R> filterTextPacket(T var1, BiFunction<TextFilter, T, CompletableFuture<R>> var2) {
      return ((CompletableFuture)var2.apply(this.player.getTextFilter(), var1)).thenApply(var1x -> {
         if (!this.isAcceptingMessages()) {
            LOGGER.debug("Ignoring packet due to disconnection");
            throw new CancellationException("disconnected");
         } else {
            return (R)var1x;
         }
      });
   }

   private CompletableFuture<FilteredText> filterTextPacket(String var1) {
      return this.filterTextPacket(var1, TextFilter::processStreamMessage);
   }

   private CompletableFuture<List<FilteredText>> filterTextPacket(List<String> var1) {
      return this.filterTextPacket(var1, TextFilter::processMessageBundle);
   }

   @Override
   public void handlePlayerInput(ServerboundPlayerInputPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.setPlayerInput(var1.getXxa(), var1.getZza(), var1.isJumping(), var1.isShiftKeyDown());
   }

   private static boolean containsInvalidValues(double var0, double var2, double var4, float var6, float var7) {
      return Double.isNaN(var0) || Double.isNaN(var2) || Double.isNaN(var4) || !Floats.isFinite(var7) || !Floats.isFinite(var6);
   }

   private static double clampHorizontal(double var0) {
      return Mth.clamp(var0, -3.0E7, 3.0E7);
   }

   private static double clampVertical(double var0) {
      return Mth.clamp(var0, -2.0E7, 2.0E7);
   }

   @Override
   public void handleMoveVehicle(ServerboundMoveVehiclePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (containsInvalidValues(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), var1.getXRot())) {
         this.disconnect(Component.translatable("multiplayer.disconnect.invalid_vehicle_movement"));
      } else {
         Entity var2 = this.player.getRootVehicle();
         if (var2 != this.player && var2.getControllingPassenger() == this.player && var2 == this.lastVehicle) {
            ServerLevel var3 = this.player.serverLevel();
            double var4 = var2.getX();
            double var6 = var2.getY();
            double var8 = var2.getZ();
            double var10 = clampHorizontal(var1.getX());
            double var12 = clampVertical(var1.getY());
            double var14 = clampHorizontal(var1.getZ());
            float var16 = Mth.wrapDegrees(var1.getYRot());
            float var17 = Mth.wrapDegrees(var1.getXRot());
            double var18 = var10 - this.vehicleFirstGoodX;
            double var20 = var12 - this.vehicleFirstGoodY;
            double var22 = var14 - this.vehicleFirstGoodZ;
            double var24 = var2.getDeltaMovement().lengthSqr();
            double var26 = var18 * var18 + var20 * var20 + var22 * var22;
            if (var26 - var24 > 100.0 && !this.isSingleplayerOwner()) {
               LOGGER.warn(
                  "{} (vehicle of {}) moved too quickly! {},{},{}",
                  new Object[]{var2.getName().getString(), this.player.getName().getString(), var18, var20, var22}
               );
               this.send(new ClientboundMoveVehiclePacket(var2));
               return;
            }

            boolean var28 = var3.noCollision(var2, var2.getBoundingBox().deflate(0.0625));
            var18 = var10 - this.vehicleLastGoodX;
            var20 = var12 - this.vehicleLastGoodY - 1.0E-6;
            var22 = var14 - this.vehicleLastGoodZ;
            boolean var29 = var2.verticalCollisionBelow;
            if (var2 instanceof LivingEntity var30 && var30.onClimbable()) {
               var30.resetFallDistance();
            }

            var2.move(MoverType.PLAYER, new Vec3(var18, var20, var22));
            var18 = var10 - var2.getX();
            var20 = var12 - var2.getY();
            if (var20 > -0.5 || var20 < 0.5) {
               var20 = 0.0;
            }

            var22 = var14 - var2.getZ();
            var26 = var18 * var18 + var20 * var20 + var22 * var22;
            boolean var32 = false;
            if (var26 > 0.0625) {
               var32 = true;
               LOGGER.warn(
                  "{} (vehicle of {}) moved wrongly! {}", new Object[]{var2.getName().getString(), this.player.getName().getString(), Math.sqrt(var26)}
               );
            }

            var2.absMoveTo(var10, var12, var14, var16, var17);
            boolean var33 = var3.noCollision(var2, var2.getBoundingBox().deflate(0.0625));
            if (var28 && (var32 || !var33)) {
               var2.absMoveTo(var4, var6, var8, var16, var17);
               this.send(new ClientboundMoveVehiclePacket(var2));
               return;
            }

            this.player.serverLevel().getChunkSource().move(this.player);
            this.player.checkMovementStatistics(this.player.getX() - var4, this.player.getY() - var6, this.player.getZ() - var8);
            this.clientVehicleIsFloating = var20 >= -0.03125 && !var29 && !this.server.isFlightAllowed() && !var2.isNoGravity() && this.noBlocksAround(var2);
            this.vehicleLastGoodX = var2.getX();
            this.vehicleLastGoodY = var2.getY();
            this.vehicleLastGoodZ = var2.getZ();
         }
      }
   }

   private boolean noBlocksAround(Entity var1) {
      return var1.level().getBlockStates(var1.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0)).allMatch(BlockBehaviour.BlockStateBase::isAir);
   }

   @Override
   public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (var1.getId() == this.awaitingTeleport) {
         if (this.awaitingPositionFromClient == null) {
            this.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
            return;
         }

         this.player
            .absMoveTo(
               this.awaitingPositionFromClient.x,
               this.awaitingPositionFromClient.y,
               this.awaitingPositionFromClient.z,
               this.player.getYRot(),
               this.player.getXRot()
            );
         this.lastGoodX = this.awaitingPositionFromClient.x;
         this.lastGoodY = this.awaitingPositionFromClient.y;
         this.lastGoodZ = this.awaitingPositionFromClient.z;
         if (this.player.isChangingDimension()) {
            this.player.hasChangedDimension();
         }

         this.awaitingPositionFromClient = null;
      }
   }

   @Override
   public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.server.getRecipeManager().byKey(var1.getRecipe()).ifPresent(this.player.getRecipeBook()::removeHighlight);
   }

   @Override
   public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.getRecipeBook().setBookSetting(var1.getBookType(), var1.isOpen(), var1.isFiltering());
   }

   @Override
   public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (var1.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         ResourceLocation var2 = Objects.requireNonNull(var1.getTab());
         AdvancementHolder var3 = this.server.getAdvancements().get(var2);
         if (var3 != null) {
            this.player.getAdvancements().setSelectedTab(var3);
         }
      }
   }

   @Override
   public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      StringReader var2 = new StringReader(var1.getCommand());
      if (var2.canRead() && var2.peek() == '/') {
         var2.skip();
      }

      ParseResults var3 = this.server.getCommands().getDispatcher().parse(var2, this.player.createCommandSourceStack());
      this.server.getCommands().getDispatcher().getCompletionSuggestions(var3).thenAccept(var2x -> {
         Suggestions var3x = var2x.getList().size() <= 1000 ? var2x : new Suggestions(var2x.getRange(), var2x.getList().subList(0, 1000));
         this.send(new ClientboundCommandSuggestionsPacket(var1.getId(), var3x));
      });
   }

   @Override
   public void handleSetCommandBlock(ServerboundSetCommandBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendSystemMessage(Component.translatable("advMode.notEnabled"));
      } else if (!this.player.canUseGameMasterBlocks()) {
         this.player.sendSystemMessage(Component.translatable("advMode.notAllowed"));
      } else {
         BaseCommandBlock var2 = null;
         CommandBlockEntity var3 = null;
         BlockPos var4 = var1.getPos();
         BlockEntity var5 = this.player.level().getBlockEntity(var4);
         if (var5 instanceof CommandBlockEntity) {
            var3 = (CommandBlockEntity)var5;
            var2 = var3.getCommandBlock();
         }

         String var6 = var1.getCommand();
         boolean var7 = var1.isTrackOutput();
         if (var2 != null) {
            CommandBlockEntity.Mode var8 = var3.getMode();
            BlockState var9 = this.player.level().getBlockState(var4);
            Direction var10 = var9.getValue(CommandBlock.FACING);

            BlockState var11 = switch (var1.getMode()) {
               case SEQUENCE -> Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
               case AUTO -> Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
               default -> Blocks.COMMAND_BLOCK.defaultBlockState();
            };
            BlockState var12 = var11.setValue(CommandBlock.FACING, var10).setValue(CommandBlock.CONDITIONAL, Boolean.valueOf(var1.isConditional()));
            if (var12 != var9) {
               this.player.level().setBlock(var4, var12, 2);
               var5.setBlockState(var12);
               this.player.level().getChunkAt(var4).setBlockEntity(var5);
            }

            var2.setCommand(var6);
            var2.setTrackOutput(var7);
            if (!var7) {
               var2.setLastOutput(null);
            }

            var3.setAutomatic(var1.isAutomatic());
            if (var8 != var1.getMode()) {
               var3.onModeSwitch();
            }

            var2.onUpdated();
            if (!StringUtil.isNullOrEmpty(var6)) {
               this.player.sendSystemMessage(Component.translatable("advMode.setCommand.success", var6));
            }
         }
      }
   }

   @Override
   public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendSystemMessage(Component.translatable("advMode.notEnabled"));
      } else if (!this.player.canUseGameMasterBlocks()) {
         this.player.sendSystemMessage(Component.translatable("advMode.notAllowed"));
      } else {
         BaseCommandBlock var2 = var1.getCommandBlock(this.player.level());
         if (var2 != null) {
            var2.setCommand(var1.getCommand());
            var2.setTrackOutput(var1.isTrackOutput());
            if (!var1.isTrackOutput()) {
               var2.setLastOutput(null);
            }

            var2.onUpdated();
            this.player.sendSystemMessage(Component.translatable("advMode.setCommand.success", var1.getCommand()));
         }
      }
   }

   @Override
   public void handlePickItem(ServerboundPickItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.getInventory().pickSlot(var1.getSlot());
      this.player
         .connection
         .send(
            new ClientboundContainerSetSlotPacket(
               -2, 0, this.player.getInventory().selected, this.player.getInventory().getItem(this.player.getInventory().selected)
            )
         );
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, var1.getSlot(), this.player.getInventory().getItem(var1.getSlot())));
      this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.getInventory().selected));
   }

   @Override
   public void handleRenameItem(ServerboundRenameItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.containerMenu instanceof AnvilMenu var2) {
         if (!var2.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, var2);
            return;
         }

         var2.setItemName(var1.getName());
      }
   }

   @Override
   public void handleSetBeaconPacket(ServerboundSetBeaconPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.containerMenu instanceof BeaconMenu var2) {
         if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
            return;
         }

         var2.updateEffects(var1.primary(), var1.secondary());
      }
   }

   @Override
   public void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockState var3 = this.player.level().getBlockState(var2);
         if (this.player.level().getBlockEntity(var2) instanceof StructureBlockEntity var5) {
            var5.setMode(var1.getMode());
            var5.setStructureName(var1.getName());
            var5.setStructurePos(var1.getOffset());
            var5.setStructureSize(var1.getSize());
            var5.setMirror(var1.getMirror());
            var5.setRotation(var1.getRotation());
            var5.setMetaData(var1.getData());
            var5.setIgnoreEntities(var1.isIgnoreEntities());
            var5.setShowAir(var1.isShowAir());
            var5.setShowBoundingBox(var1.isShowBoundingBox());
            var5.setIntegrity(var1.getIntegrity());
            var5.setSeed(var1.getSeed());
            if (var5.hasStructureName()) {
               String var6 = var5.getStructureName();
               if (var1.getUpdateType() == StructureBlockEntity.UpdateType.SAVE_AREA) {
                  if (var5.saveStructure()) {
                     this.player.displayClientMessage(Component.translatable("structure_block.save_success", var6), false);
                  } else {
                     this.player.displayClientMessage(Component.translatable("structure_block.save_failure", var6), false);
                  }
               } else if (var1.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                  if (!var5.isStructureLoadable()) {
                     this.player.displayClientMessage(Component.translatable("structure_block.load_not_found", var6), false);
                  } else if (var5.placeStructureIfSameSize(this.player.serverLevel())) {
                     this.player.displayClientMessage(Component.translatable("structure_block.load_success", var6), false);
                  } else {
                     this.player.displayClientMessage(Component.translatable("structure_block.load_prepare", var6), false);
                  }
               } else if (var1.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                  if (var5.detectSize()) {
                     this.player.displayClientMessage(Component.translatable("structure_block.size_success", var6), false);
                  } else {
                     this.player.displayClientMessage(Component.translatable("structure_block.size_failure"), false);
                  }
               }
            } else {
               this.player.displayClientMessage(Component.translatable("structure_block.invalid_structure_name", var1.getName()), false);
            }

            var5.setChanged();
            this.player.level().sendBlockUpdated(var2, var3, var3, 3);
         }
      }
   }

   @Override
   public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockState var3 = this.player.level().getBlockState(var2);
         if (this.player.level().getBlockEntity(var2) instanceof JigsawBlockEntity var5) {
            var5.setName(var1.getName());
            var5.setTarget(var1.getTarget());
            var5.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, var1.getPool()));
            var5.setFinalState(var1.getFinalState());
            var5.setJoint(var1.getJoint());
            var5.setPlacementPriority(var1.getPlacementPriority());
            var5.setSelectionPriority(var1.getSelectionPriority());
            var5.setChanged();
            this.player.level().sendBlockUpdated(var2, var3, var3, 3);
         }
      }
   }

   @Override
   public void handleJigsawGenerate(ServerboundJigsawGeneratePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         if (this.player.level().getBlockEntity(var2) instanceof JigsawBlockEntity var4) {
            var4.generate(this.player.serverLevel(), var1.levels(), var1.keepJigsaws());
         }
      }
   }

   @Override
   public void handleSelectTrade(ServerboundSelectTradePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      int var2 = var1.getItem();
      if (this.player.containerMenu instanceof MerchantMenu var3) {
         if (!var3.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, var3);
            return;
         }

         var3.setSelectionHint(var2);
         var3.tryMoveItems(var2);
      }
   }

   @Override
   public void handleEditBook(ServerboundEditBookPacket var1) {
      int var2 = var1.slot();
      if (Inventory.isHotbarSlot(var2) || var2 == 40) {
         ArrayList var3 = Lists.newArrayList();
         Optional var4 = var1.title();
         var4.ifPresent(var3::add);
         var1.pages().stream().limit(100L).forEach(var3::add);
         Consumer var5 = var4.isPresent()
            ? var2x -> this.signBook((FilteredText)var2x.get(0), var2x.subList(1, var2x.size()), var2)
            : var2x -> this.updateBookContents(var2x, var2);
         this.filterTextPacket(var3).thenAcceptAsync(var5, this.server);
      }
   }

   private void updateBookContents(List<FilteredText> var1, int var2) {
      ItemStack var3 = this.player.getInventory().getItem(var2);
      if (var3.is(Items.WRITABLE_BOOK)) {
         List var4 = var1.stream().map(this::filterableFromOutgoing).toList();
         var3.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(var4));
      }
   }

   private void signBook(FilteredText var1, List<FilteredText> var2, int var3) {
      ItemStack var4 = this.player.getInventory().getItem(var3);
      if (var4.is(Items.WRITABLE_BOOK)) {
         ItemStack var5 = var4.transmuteCopy(Items.WRITTEN_BOOK, 1);
         var5.remove(DataComponents.WRITABLE_BOOK_CONTENT);
         List var6 = var2.stream().map(var1x -> this.filterableFromOutgoing(var1x).map(Component::literal)).toList();
         var5.set(
            DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(this.filterableFromOutgoing(var1), this.player.getName().getString(), 0, var6, true)
         );
         this.player.getInventory().setItem(var3, var5);
      }
   }

   private Filterable<String> filterableFromOutgoing(FilteredText var1) {
      return this.player.isTextFilteringEnabled() ? Filterable.passThrough(var1.filteredOrEmpty()) : Filterable.from(var1);
   }

   @Override
   public void handleEntityTagQuery(ServerboundEntityTagQueryPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.hasPermissions(2)) {
         Entity var2 = this.player.level().getEntity(var1.getEntityId());
         if (var2 != null) {
            CompoundTag var3 = var2.saveWithoutId(new CompoundTag());
            this.player.connection.send(new ClientboundTagQueryPacket(var1.getTransactionId(), var3));
         }
      }
   }

   @Override
   public void handleContainerSlotStateChanged(ServerboundContainerSlotStateChangedPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (!this.player.isSpectator() && var1.containerId() == this.player.containerMenu.containerId) {
         if (this.player.containerMenu instanceof CrafterMenu var2 && var2.getContainer() instanceof CrafterBlockEntity var3) {
            var3.setSlotState(var1.slotId(), var1.newState());
         }
      }
   }

   @Override
   public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQueryPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.hasPermissions(2)) {
         BlockEntity var2 = this.player.level().getBlockEntity(var1.getPos());
         CompoundTag var3 = var2 != null ? var2.saveWithoutMetadata(this.player.registryAccess()) : null;
         this.player.connection.send(new ClientboundTagQueryPacket(var1.getTransactionId(), var3));
      }
   }

   @Override
   public void handleMovePlayer(ServerboundMovePlayerPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (containsInvalidValues(var1.getX(0.0), var1.getY(0.0), var1.getZ(0.0), var1.getYRot(0.0F), var1.getXRot(0.0F))) {
         this.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
      } else {
         ServerLevel var2 = this.player.serverLevel();
         if (!this.player.wonGame) {
            if (this.tickCount == 0) {
               this.resetPosition();
            }

            if (this.awaitingPositionFromClient != null) {
               if (this.tickCount - this.awaitingTeleportTime > 20) {
                  this.awaitingTeleportTime = this.tickCount;
                  this.teleport(
                     this.awaitingPositionFromClient.x,
                     this.awaitingPositionFromClient.y,
                     this.awaitingPositionFromClient.z,
                     this.player.getYRot(),
                     this.player.getXRot()
                  );
               }
            } else {
               this.awaitingTeleportTime = this.tickCount;
               double var3 = clampHorizontal(var1.getX(this.player.getX()));
               double var5 = clampVertical(var1.getY(this.player.getY()));
               double var7 = clampHorizontal(var1.getZ(this.player.getZ()));
               float var9 = Mth.wrapDegrees(var1.getYRot(this.player.getYRot()));
               float var10 = Mth.wrapDegrees(var1.getXRot(this.player.getXRot()));
               if (this.player.isPassenger()) {
                  this.player.absMoveTo(this.player.getX(), this.player.getY(), this.player.getZ(), var9, var10);
                  this.player.serverLevel().getChunkSource().move(this.player);
               } else {
                  double var11 = this.player.getX();
                  double var13 = this.player.getY();
                  double var15 = this.player.getZ();
                  double var17 = var3 - this.firstGoodX;
                  double var19 = var5 - this.firstGoodY;
                  double var21 = var7 - this.firstGoodZ;
                  double var23 = this.player.getDeltaMovement().lengthSqr();
                  double var25 = var17 * var17 + var19 * var19 + var21 * var21;
                  if (this.player.isSleeping()) {
                     if (var25 > 1.0) {
                        this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), var9, var10);
                     }
                  } else {
                     boolean var27 = this.player.isFallFlying();
                     if (var2.tickRateManager().runsNormally()) {
                        this.receivedMovePacketCount++;
                        int var28 = this.receivedMovePacketCount - this.knownMovePacketCount;
                        if (var28 > 5) {
                           LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), var28);
                           var28 = 1;
                        }

                        if (!this.player.isChangingDimension()
                           && (!this.player.level().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !var27)) {
                           float var29 = var27 ? 300.0F : 100.0F;
                           if (var25 - var23 > (double)(var29 * (float)var28) && !this.isSingleplayerOwner()) {
                              LOGGER.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName().getString(), var17, var19, var21});
                              this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
                              return;
                           }
                        }
                     }

                     AABB var42 = this.player.getBoundingBox();
                     var17 = var3 - this.lastGoodX;
                     var19 = var5 - this.lastGoodY;
                     var21 = var7 - this.lastGoodZ;
                     boolean var43 = var19 > 0.0;
                     if (this.player.onGround() && !var1.isOnGround() && var43) {
                        this.player.jumpFromGround();
                     }

                     boolean var30 = this.player.verticalCollisionBelow;
                     this.player.move(MoverType.PLAYER, new Vec3(var17, var19, var21));
                     var17 = var3 - this.player.getX();
                     var19 = var5 - this.player.getY();
                     if (var19 > -0.5 || var19 < 0.5) {
                        var19 = 0.0;
                     }

                     var21 = var7 - this.player.getZ();
                     var25 = var17 * var17 + var19 * var19 + var21 * var21;
                     boolean var33 = false;
                     if (!this.player.isChangingDimension()
                        && var25 > 0.0625
                        && !this.player.isSleeping()
                        && !this.player.gameMode.isCreative()
                        && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                        var33 = true;
                        LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                     }

                     if (this.player.noPhysics
                        || this.player.isSleeping()
                        || (!var33 || !var2.noCollision(this.player, var42)) && !this.isPlayerCollidingWithAnythingNew(var2, var42, var3, var5, var7)) {
                        this.player.absMoveTo(var3, var5, var7, var9, var10);
                        boolean var34 = this.player.isAutoSpinAttack();
                        this.clientIsFloating = var19 >= -0.03125
                           && !var30
                           && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR
                           && !this.server.isFlightAllowed()
                           && !this.player.getAbilities().mayfly
                           && !this.player.hasEffect(MobEffects.LEVITATION)
                           && !var27
                           && !var34
                           && this.noBlocksAround(this.player);
                        this.player.serverLevel().getChunkSource().move(this.player);
                        this.player.doCheckFallDamage(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15, var1.isOnGround());
                        this.player
                           .setOnGroundWithKnownMovement(
                              var1.isOnGround(), new Vec3(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15)
                           );
                        if (var43) {
                           this.player.resetFallDistance();
                        }

                        if (var1.isOnGround() || this.player.isInLiquid() || this.player.onClimbable() || this.player.isSpectator() || var27 || var34) {
                           this.player.resetCurrentImpulseContext();
                        }

                        this.player.checkMovementStatistics(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15);
                        this.lastGoodX = this.player.getX();
                        this.lastGoodY = this.player.getY();
                        this.lastGoodZ = this.player.getZ();
                     } else {
                        this.teleport(var11, var13, var15, var9, var10);
                        this.player.doCheckFallDamage(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15, var1.isOnGround());
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPlayerCollidingWithAnythingNew(LevelReader var1, AABB var2, double var3, double var5, double var7) {
      AABB var9 = this.player.getBoundingBox().move(var3 - this.player.getX(), var5 - this.player.getY(), var7 - this.player.getZ());
      Iterable var10 = var1.getCollisions(this.player, var9.deflate(9.999999747378752E-6));
      VoxelShape var11 = Shapes.create(var2.deflate(9.999999747378752E-6));

      for (VoxelShape var13 : var10) {
         if (!Shapes.joinIsNotEmpty(var13, var11, BooleanOp.AND)) {
            return true;
         }
      }

      return false;
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8) {
      this.teleport(var1, var3, var5, var7, var8, Collections.emptySet());
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8, Set<RelativeMovement> var9) {
      double var10 = var9.contains(RelativeMovement.X) ? this.player.getX() : 0.0;
      double var12 = var9.contains(RelativeMovement.Y) ? this.player.getY() : 0.0;
      double var14 = var9.contains(RelativeMovement.Z) ? this.player.getZ() : 0.0;
      float var16 = var9.contains(RelativeMovement.Y_ROT) ? this.player.getYRot() : 0.0F;
      float var17 = var9.contains(RelativeMovement.X_ROT) ? this.player.getXRot() : 0.0F;
      this.awaitingPositionFromClient = new Vec3(var1, var3, var5);
      if (++this.awaitingTeleport == 2147483647) {
         this.awaitingTeleport = 0;
      }

      this.awaitingTeleportTime = this.tickCount;
      this.player.resetCurrentImpulseContext();
      this.player.absMoveTo(var1, var3, var5, var7, var8);
      this.player
         .connection
         .send(new ClientboundPlayerPositionPacket(var1 - var10, var3 - var12, var5 - var14, var7 - var16, var8 - var17, var9, this.awaitingTeleport));
   }

   @Override
   public void handlePlayerAction(ServerboundPlayerActionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      BlockPos var2 = var1.getPos();
      this.player.resetLastActionTime();
      ServerboundPlayerActionPacket.Action var3 = var1.getAction();
      switch (var3) {
         case SWAP_ITEM_WITH_OFFHAND:
            if (!this.player.isSpectator()) {
               ItemStack var4 = this.player.getItemInHand(InteractionHand.OFF_HAND);
               this.player.setItemInHand(InteractionHand.OFF_HAND, this.player.getItemInHand(InteractionHand.MAIN_HAND));
               this.player.setItemInHand(InteractionHand.MAIN_HAND, var4);
               this.player.stopUsingItem();
            }

            return;
         case DROP_ITEM:
            if (!this.player.isSpectator()) {
               this.player.drop(false);
            }

            return;
         case DROP_ALL_ITEMS:
            if (!this.player.isSpectator()) {
               this.player.drop(true);
            }

            return;
         case RELEASE_USE_ITEM:
            this.player.releaseUsingItem();
            return;
         case START_DESTROY_BLOCK:
         case ABORT_DESTROY_BLOCK:
         case STOP_DESTROY_BLOCK:
            this.player.gameMode.handleBlockBreakAction(var2, var3, var1.getDirection(), this.player.level().getMaxBuildHeight(), var1.getSequence());
            this.player.connection.ackBlockChangesUpTo(var1.getSequence());
            return;
         default:
            throw new IllegalArgumentException("Invalid player action");
      }
   }

   private static boolean wasBlockPlacementAttempt(ServerPlayer var0, ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         Item var2 = var1.getItem();
         return (var2 instanceof BlockItem || var2 instanceof BucketItem) && !var0.getCooldowns().isOnCooldown(var2);
      }
   }

   @Override
   public void handleUseItemOn(ServerboundUseItemOnPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.connection.ackBlockChangesUpTo(var1.getSequence());
      ServerLevel var2 = this.player.serverLevel();
      InteractionHand var3 = var1.getHand();
      ItemStack var4 = this.player.getItemInHand(var3);
      if (var4.isItemEnabled(var2.enabledFeatures())) {
         BlockHitResult var5 = var1.getHitResult();
         Vec3 var6 = var5.getLocation();
         BlockPos var7 = var5.getBlockPos();
         if (this.player.canInteractWithBlock(var7, 1.0)) {
            Vec3 var8 = var6.subtract(Vec3.atCenterOf(var7));
            double var9 = 1.0000001;
            if (Math.abs(var8.x()) < 1.0000001 && Math.abs(var8.y()) < 1.0000001 && Math.abs(var8.z()) < 1.0000001) {
               Direction var11 = var5.getDirection();
               this.player.resetLastActionTime();
               int var12 = this.player.level().getMaxBuildHeight();
               if (var7.getY() < var12) {
                  if (this.awaitingPositionFromClient == null && var2.mayInteract(this.player, var7)) {
                     InteractionResult var13 = this.player.gameMode.useItemOn(this.player, var2, var4, var3, var5);
                     if (var13.consumesAction()) {
                        CriteriaTriggers.ANY_BLOCK_USE.trigger(this.player, var5.getBlockPos(), var4.copy());
                     }

                     if (var11 == Direction.UP && !var13.consumesAction() && var7.getY() >= var12 - 1 && wasBlockPlacementAttempt(this.player, var4)) {
                        MutableComponent var14 = Component.translatable("build.tooHigh", var12 - 1).withStyle(ChatFormatting.RED);
                        this.player.sendSystemMessage(var14, true);
                     } else if (var13.shouldSwing()) {
                        this.player.swing(var3, true);
                     }
                  }
               } else {
                  MutableComponent var15 = Component.translatable("build.tooHigh", var12 - 1).withStyle(ChatFormatting.RED);
                  this.player.sendSystemMessage(var15, true);
               }

               this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var7));
               this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var7.relative(var11)));
            } else {
               LOGGER.warn(
                  "Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.",
                  new Object[]{this.player.getGameProfile().getName(), var6, var7}
               );
            }
         }
      }
   }

   @Override
   public void handleUseItem(ServerboundUseItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.ackBlockChangesUpTo(var1.getSequence());
      ServerLevel var2 = this.player.serverLevel();
      InteractionHand var3 = var1.getHand();
      ItemStack var4 = this.player.getItemInHand(var3);
      this.player.resetLastActionTime();
      if (!var4.isEmpty() && var4.isItemEnabled(var2.enabledFeatures())) {
         InteractionResult var5 = this.player.gameMode.useItem(this.player, var2, var4, var3);
         if (var5.shouldSwing()) {
            this.player.swing(var3, true);
         }
      }
   }

   @Override
   public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.isSpectator()) {
         for (ServerLevel var3 : this.server.getAllLevels()) {
            Entity var4 = var1.getEntity(var3);
            if (var4 != null) {
               this.player.teleportTo(var3, var4.getX(), var4.getY(), var4.getZ(), var4.getYRot(), var4.getXRot());
               return;
            }
         }
      }
   }

   @Override
   public void handlePaddleBoat(ServerboundPaddleBoatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.getControlledVehicle() instanceof Boat var3) {
         var3.setPaddleState(var1.getLeft(), var1.getRight());
      }
   }

   @Override
   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), var1.getString());
      this.removePlayerFromWorld();
      super.onDisconnect(var1);
   }

   private void removePlayerFromWorld() {
      this.chatMessageChain.close();
      this.server.invalidateStatus();
      this.server
         .getPlayerList()
         .broadcastSystemMessage(Component.translatable("multiplayer.player.left", this.player.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);
      this.player.disconnect();
      this.server.getPlayerList().remove(this.player);
      this.player.getTextFilter().leave();
   }

   public void ackBlockChangesUpTo(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Expected packet sequence nr >= 0");
      } else {
         this.ackBlockChangesUpTo = Math.max(var1, this.ackBlockChangesUpTo);
      }
   }

   @Override
   public void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (var1.getSlot() >= 0 && var1.getSlot() < Inventory.getSelectionSize()) {
         if (this.player.getInventory().selected != var1.getSlot() && this.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            this.player.stopUsingItem();
         }

         this.player.getInventory().selected = var1.getSlot();
         this.player.resetLastActionTime();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
      }
   }

   @Override
   public void handleChat(ServerboundChatPacket var1) {
      Optional var2 = this.unpackAndApplyLastSeen(var1.lastSeenMessages());
      if (!var2.isEmpty()) {
         this.tryHandleChat(var1.message(), () -> {
            PlayerChatMessage var3;
            try {
               var3 = this.getSignedMessage(var1, (LastSeenMessages)var2.get());
            } catch (SignedMessageChain.DecodeException var6) {
               this.handleMessageDecodeFailure(var6);
               return;
            }

            CompletableFuture var4 = this.filterTextPacket(var3.signedContent());
            Component var5 = this.server.getChatDecorator().decorate(this.player, var3.decoratedContent());
            this.chatMessageChain.append(var4, var3x -> {
               PlayerChatMessage var4x = var3.withUnsignedContent(var5).filter(var3x.mask());
               this.broadcastChatMessage(var4x);
            });
         });
      }
   }

   @Override
   public void handleChatCommand(ServerboundChatCommandPacket var1) {
      this.tryHandleChat(var1.command(), () -> {
         this.performUnsignedChatCommand(var1.command());
         this.detectRateSpam();
      });
   }

   private void performUnsignedChatCommand(String var1) {
      ParseResults var2 = this.parseCommand(var1);
      if (this.server.enforceSecureProfile() && SignableCommand.hasSignableArguments(var2)) {
         LOGGER.error("Received unsigned command packet from {}, but the command requires signable arguments: {}", this.player.getGameProfile().getName(), var1);
         this.player.sendSystemMessage(INVALID_COMMAND_SIGNATURE);
      } else {
         this.server.getCommands().performCommand(var2, var1);
      }
   }

   @Override
   public void handleSignedChatCommand(ServerboundChatCommandSignedPacket var1) {
      Optional var2 = this.unpackAndApplyLastSeen(var1.lastSeenMessages());
      if (!var2.isEmpty()) {
         this.tryHandleChat(var1.command(), () -> {
            this.performSignedChatCommand(var1, (LastSeenMessages)var2.get());
            this.detectRateSpam();
         });
      }
   }

   private void performSignedChatCommand(ServerboundChatCommandSignedPacket var1, LastSeenMessages var2) {
      ParseResults var3 = this.parseCommand(var1.command());

      Map var4;
      try {
         var4 = this.collectSignedArguments(var1, SignableCommand.of(var3), var2);
      } catch (SignedMessageChain.DecodeException var6) {
         this.handleMessageDecodeFailure(var6);
         return;
      }

      CommandSigningContext.SignedArguments var5 = new CommandSigningContext.SignedArguments(var4);
      var3 = Commands.mapSource(var3, var2x -> var2x.withSigningContext(var5, this.chatMessageChain));
      this.server.getCommands().performCommand(var3, var1.command());
   }

   private void handleMessageDecodeFailure(SignedMessageChain.DecodeException var1) {
      LOGGER.warn("Failed to update secure chat state for {}: '{}'", this.player.getGameProfile().getName(), var1.getComponent().getString());
      this.player.sendSystemMessage(var1.getComponent().copy().withStyle(ChatFormatting.RED));
   }

   private <S> Map<String, PlayerChatMessage> collectSignedArguments(ServerboundChatCommandSignedPacket var1, SignableCommand<S> var2, LastSeenMessages var3) throws SignedMessageChain.DecodeException {
      List var4 = var1.argumentSignatures().entries();
      List var5 = var2.arguments();
      if (var4.isEmpty()) {
         return this.collectUnsignedArguments(var5);
      } else {
         Object2ObjectOpenHashMap var6 = new Object2ObjectOpenHashMap();

         for (ArgumentSignatures.Entry var8 : var4) {
            SignableCommand.Argument var9 = var2.getArgument(var8.name());
            if (var9 == null) {
               this.signedMessageDecoder.setChainBroken();
               throw createSignedArgumentMismatchException(var1.command(), var4, var5);
            }

            SignedMessageBody var10 = new SignedMessageBody(var9.value(), var1.timeStamp(), var1.salt(), var3);
            var6.put(var9.name(), this.signedMessageDecoder.unpack(var8.signature(), var10));
         }

         for (SignableCommand.Argument var12 : var5) {
            if (!var6.containsKey(var12.name())) {
               throw createSignedArgumentMismatchException(var1.command(), var4, var5);
            }
         }

         return var6;
      }
   }

   private <S> Map<String, PlayerChatMessage> collectUnsignedArguments(List<SignableCommand.Argument<S>> var1) throws SignedMessageChain.DecodeException {
      HashMap var2 = new HashMap();

      for (SignableCommand.Argument var4 : var1) {
         SignedMessageBody var5 = SignedMessageBody.unsigned(var4.value());
         var2.put(var4.name(), this.signedMessageDecoder.unpack(null, var5));
      }

      return var2;
   }

   private static <S> SignedMessageChain.DecodeException createSignedArgumentMismatchException(
      String var0, List<ArgumentSignatures.Entry> var1, List<SignableCommand.Argument<S>> var2
   ) {
      String var3 = var1.stream().map(ArgumentSignatures.Entry::name).collect(Collectors.joining(", "));
      String var4 = var2.stream().map(SignableCommand.Argument::name).collect(Collectors.joining(", "));
      LOGGER.error("Signed command mismatch between server and client ('{}'): got [{}] from client, but expected [{}]", new Object[]{var0, var3, var4});
      return new SignedMessageChain.DecodeException(INVALID_COMMAND_SIGNATURE);
   }

   private ParseResults<CommandSourceStack> parseCommand(String var1) {
      CommandDispatcher var2 = this.server.getCommands().getDispatcher();
      return var2.parse(var1, this.player.createCommandSourceStack());
   }

   private void tryHandleChat(String var1, Runnable var2) {
      if (isChatMessageIllegal(var1)) {
         this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
      } else if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
         this.send(new ClientboundSystemChatPacket(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED), false));
      } else {
         this.player.resetLastActionTime();
         this.server.execute(var2);
      }
   }

   private Optional<LastSeenMessages> unpackAndApplyLastSeen(LastSeenMessages.Update var1) {
      synchronized (this.lastSeenMessages) {
         Optional var3 = this.lastSeenMessages.applyUpdate(var1);
         if (var3.isEmpty()) {
            LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
            this.disconnect(CHAT_VALIDATION_FAILED);
         }

         return var3;
      }
   }

   private static boolean isChatMessageIllegal(String var0) {
      for (int var1 = 0; var1 < var0.length(); var1++) {
         if (!StringUtil.isAllowedChatCharacter(var0.charAt(var1))) {
            return true;
         }
      }

      return false;
   }

   private PlayerChatMessage getSignedMessage(ServerboundChatPacket var1, LastSeenMessages var2) throws SignedMessageChain.DecodeException {
      SignedMessageBody var3 = new SignedMessageBody(var1.message(), var1.timeStamp(), var1.salt(), var2);
      return this.signedMessageDecoder.unpack(var1.signature(), var3);
   }

   private void broadcastChatMessage(PlayerChatMessage var1) {
      this.server.getPlayerList().broadcastChatMessage(var1, this.player, ChatType.bind(ChatType.CHAT, this.player));
      this.detectRateSpam();
   }

   private void detectRateSpam() {
      this.chatSpamTickCount += 20;
      if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
         this.disconnect(Component.translatable("disconnect.spam"));
      }
   }

   @Override
   public void handleChatAck(ServerboundChatAckPacket var1) {
      synchronized (this.lastSeenMessages) {
         if (!this.lastSeenMessages.applyOffset(var1.offset())) {
            LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
            this.disconnect(CHAT_VALIDATION_FAILED);
         }
      }
   }

   @Override
   public void handleAnimate(ServerboundSwingPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      this.player.swing(var1.getHand());
   }

   @Override
   public void handlePlayerCommand(ServerboundPlayerCommandPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      switch (var1.getAction()) {
         case PRESS_SHIFT_KEY:
            this.player.setShiftKeyDown(true);
            break;
         case RELEASE_SHIFT_KEY:
            this.player.setShiftKeyDown(false);
            break;
         case START_SPRINTING:
            this.player.setSprinting(true);
            break;
         case STOP_SPRINTING:
            this.player.setSprinting(false);
            break;
         case STOP_SLEEPING:
            if (this.player.isSleeping()) {
               this.player.stopSleepInBed(false, true);
               this.awaitingPositionFromClient = this.player.position();
            }
            break;
         case START_RIDING_JUMP:
            if (this.player.getControlledVehicle() instanceof PlayerRideableJumping var5) {
               int var8 = var1.getData();
               if (var5.canJump() && var8 > 0) {
                  var5.handleStartJump(var8);
               }
            }
            break;
         case STOP_RIDING_JUMP:
            if (this.player.getControlledVehicle() instanceof PlayerRideableJumping var4) {
               var4.handleStopJump();
            }
            break;
         case OPEN_INVENTORY:
            if (this.player.getVehicle() instanceof HasCustomInventoryScreen var2) {
               var2.openCustomInventoryScreen(this.player);
            }
            break;
         case START_FALL_FLYING:
            if (!this.player.tryToStartFallFlying()) {
               this.player.stopFallFlying();
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid client command!");
      }
   }

   public void addPendingMessage(PlayerChatMessage var1) {
      MessageSignature var2 = var1.signature();
      if (var2 != null) {
         this.messageSignatureCache.push(var1.signedBody(), var1.signature());
         int var3;
         synchronized (this.lastSeenMessages) {
            this.lastSeenMessages.addPending(var2);
            var3 = this.lastSeenMessages.trackedMessagesCount();
         }

         if (var3 > 4096) {
            this.disconnect(Component.translatable("multiplayer.disconnect.too_many_pending_chats"));
         }
      }
   }

   public void sendPlayerChatMessage(PlayerChatMessage var1, ChatType.Bound var2) {
      this.send(
         new ClientboundPlayerChatPacket(
            var1.link().sender(),
            var1.link().index(),
            var1.signature(),
            var1.signedBody().pack(this.messageSignatureCache),
            var1.unsignedContent(),
            var1.filterMask(),
            var2
         )
      );
      this.addPendingMessage(var1);
   }

   public void sendDisguisedChatMessage(Component var1, ChatType.Bound var2) {
      this.send(new ClientboundDisguisedChatPacket(var1, var2));
   }

   public SocketAddress getRemoteAddress() {
      return this.connection.getRemoteAddress();
   }

   public void switchToConfig() {
      this.waitingForSwitchToConfig = true;
      this.removePlayerFromWorld();
      this.send(ClientboundStartConfigurationPacket.INSTANCE);
      this.connection.setupOutboundProtocol(ConfigurationProtocols.CLIENTBOUND);
   }

   @Override
   public void handlePingRequest(ServerboundPingRequestPacket var1) {
      this.connection.send(new ClientboundPongResponsePacket(var1.getTime()));
   }

   @Override
   public void handleInteract(ServerboundInteractPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      final ServerLevel var2 = this.player.serverLevel();
      final Entity var3 = var1.getTarget(var2);
      this.player.resetLastActionTime();
      this.player.setShiftKeyDown(var1.isUsingSecondaryAction());
      if (var3 != null) {
         if (!var2.getWorldBorder().isWithinBounds(var3.blockPosition())) {
            return;
         }

         AABB var4 = var3.getBoundingBox();
         if (this.player.canInteractWithEntity(var4, 1.0)) {
            var1.dispatch(
               new ServerboundInteractPacket.Handler() {
                  private void performInteraction(InteractionHand var1, ServerGamePacketListenerImpl.EntityInteraction var2x) {
                     ItemStack var3x = ServerGamePacketListenerImpl.this.player.getItemInHand(var1);
                     if (var3x.isItemEnabled(var2.enabledFeatures())) {
                        ItemStack var4 = var3x.copy();
                        InteractionResult var5 = var2x.run(ServerGamePacketListenerImpl.this.player, var3, var1);
                        if (var5.consumesAction()) {
                           CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY
                              .trigger(ServerGamePacketListenerImpl.this.player, var5.indicateItemUse() ? var4 : ItemStack.EMPTY, var3);
                           if (var5.shouldSwing()) {
                              ServerGamePacketListenerImpl.this.player.swing(var1, true);
                           }
                        }
                     }
                  }
   
                  @Override
                  public void onInteraction(InteractionHand var1) {
                     this.performInteraction(var1, Player::interactOn);
                  }
   
                  @Override
                  public void onInteraction(InteractionHand var1, Vec3 var2x) {
                     this.performInteraction(var1, (var1x, var2xxx, var3xx) -> var2xxx.interactAt(var1x, var2x, var3xx));
                  }
   
                  @Override
                  public void onAttack() {
                     if (!(var3 instanceof ItemEntity)
                        && !(var3 instanceof ExperienceOrb)
                        && var3 != ServerGamePacketListenerImpl.this.player
                        && (!(var3 instanceof AbstractArrow) || var3.getType().is(EntityTypeTags.PUNCHABLE_PROJECTILES))) {
                        ItemStack var1 = ServerGamePacketListenerImpl.this.player.getItemInHand(InteractionHand.MAIN_HAND);
                        if (var1.isItemEnabled(var2.enabledFeatures())) {
                           ServerGamePacketListenerImpl.this.player.attack(var3);
                        }
                     } else {
                        ServerGamePacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                        ServerGamePacketListenerImpl.LOGGER
                           .warn("Player {} tried to attack an invalid entity", ServerGamePacketListenerImpl.this.player.getName().getString());
                     }
                  }
               }
            );
         }
      }
   }

   @Override
   public void handleClientCommand(ServerboundClientCommandPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      ServerboundClientCommandPacket.Action var2 = var1.getAction();
      switch (var2) {
         case PERFORM_RESPAWN:
            if (this.player.wonGame) {
               this.player.wonGame = false;
               this.player = this.server.getPlayerList().respawn(this.player, true);
               CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, Level.END, Level.OVERWORLD);
            } else {
               if (this.player.getHealth() > 0.0F) {
                  return;
               }

               this.player = this.server.getPlayerList().respawn(this.player, false);
               if (this.server.isHardcore()) {
                  this.player.setGameMode(GameType.SPECTATOR);
                  this.player.level().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(false, this.server);
               }
            }
            break;
         case REQUEST_STATS:
            this.player.getStats().sendStats(this.player);
      }
   }

   @Override
   public void handleContainerClose(ServerboundContainerClosePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.doCloseContainer();
   }

   @Override
   public void handleContainerClick(ServerboundContainerClickPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == var1.getContainerId()) {
         if (this.player.isSpectator()) {
            this.player.containerMenu.sendAllDataToRemote();
         } else if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
         } else {
            int var2 = var1.getSlotNum();
            if (!this.player.containerMenu.isValidSlotIndex(var2)) {
               LOGGER.debug(
                  "Player {} clicked invalid slot index: {}, available slots: {}",
                  new Object[]{this.player.getName(), var2, this.player.containerMenu.slots.size()}
               );
            } else {
               boolean var3 = var1.getStateId() != this.player.containerMenu.getStateId();
               this.player.containerMenu.suppressRemoteUpdates();
               this.player.containerMenu.clicked(var2, var1.getButtonNum(), var1.getClickType(), this.player);
               ObjectIterator var4 = Int2ObjectMaps.fastIterable(var1.getChangedSlots()).iterator();

               while (var4.hasNext()) {
                  Entry var5 = (Entry)var4.next();
                  this.player.containerMenu.setRemoteSlotNoCopy(var5.getIntKey(), (ItemStack)var5.getValue());
               }

               this.player.containerMenu.setRemoteCarried(var1.getCarriedItem());
               this.player.containerMenu.resumeRemoteUpdates();
               if (var3) {
                  this.player.containerMenu.broadcastFullState();
               } else {
                  this.player.containerMenu.broadcastChanges();
               }
            }
         }
      }
   }

   @Override
   public void handlePlaceRecipe(ServerboundPlaceRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      if (!this.player.isSpectator() && this.player.containerMenu.containerId == var1.getContainerId() && this.player.containerMenu instanceof RecipeBookMenu) {
         if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
         } else {
            this.server
               .getRecipeManager()
               .byKey(var1.getRecipe())
               .ifPresent(var2 -> ((RecipeBookMenu)this.player.containerMenu).handlePlacement(var1.isShiftDown(), (RecipeHolder<?>)var2, this.player));
         }
      }
   }

   @Override
   public void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == var1.containerId() && !this.player.isSpectator()) {
         if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
         } else {
            boolean var2 = this.player.containerMenu.clickMenuButton(this.player, var1.buttonId());
            if (var2) {
               this.player.containerMenu.broadcastChanges();
            }
         }
      }
   }

   @Override
   public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.gameMode.isCreative()) {
         boolean var2 = var1.slotNum() < 0;
         ItemStack var3 = var1.itemStack();
         if (!var3.isItemEnabled(this.player.level().enabledFeatures())) {
            return;
         }

         CustomData var4 = var3.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
         if (var4.contains("x") && var4.contains("y") && var4.contains("z")) {
            BlockPos var5 = BlockEntity.getPosFromTag(var4.getUnsafe());
            if (this.player.level().isLoaded(var5)) {
               BlockEntity var6 = this.player.level().getBlockEntity(var5);
               if (var6 != null) {
                  var6.saveToItem(var3, this.player.level().registryAccess());
               }
            }
         }

         boolean var7 = var1.slotNum() >= 1 && var1.slotNum() <= 45;
         boolean var8 = var3.isEmpty() || var3.getCount() <= var3.getMaxStackSize();
         if (var7 && var8) {
            this.player.inventoryMenu.getSlot(var1.slotNum()).setByPlayer(var3);
            this.player.inventoryMenu.broadcastChanges();
         } else if (var2 && var8 && this.dropSpamTickCount < 200) {
            this.dropSpamTickCount += 20;
            this.player.drop(var3, true);
         }
      }
   }

   @Override
   public void handleSignUpdate(ServerboundSignUpdatePacket var1) {
      List var2 = Stream.of(var1.getLines()).map(ChatFormatting::stripFormatting).collect(Collectors.toList());
      this.filterTextPacket(var2).thenAcceptAsync(var2x -> this.updateSignText(var1, (List<FilteredText>)var2x), this.server);
   }

   private void updateSignText(ServerboundSignUpdatePacket var1, List<FilteredText> var2) {
      this.player.resetLastActionTime();
      ServerLevel var3 = this.player.serverLevel();
      BlockPos var4 = var1.getPos();
      if (var3.hasChunkAt(var4)) {
         if (!(var3.getBlockEntity(var4) instanceof SignBlockEntity var6)) {
            return;
         }

         var6.updateSignText(this.player, var1.isFrontText(), var2);
      }
   }

   @Override
   public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.getAbilities().flying = var1.isFlying() && this.player.getAbilities().mayfly;
   }

   @Override
   public void handleClientInformation(ServerboundClientInformationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.updateOptions(var1.information());
   }

   @Override
   public void handleChangeDifficulty(ServerboundChangeDifficultyPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficulty(var1.getDifficulty(), false);
      }
   }

   @Override
   public void handleLockDifficulty(ServerboundLockDifficultyPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficultyLocked(var1.isLocked());
      }
   }

   @Override
   public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      RemoteChatSession.Data var2 = var1.chatSession();
      ProfilePublicKey.Data var3 = this.chatSession != null ? this.chatSession.profilePublicKey().data() : null;
      ProfilePublicKey.Data var4 = var2.profilePublicKey();
      if (!Objects.equals(var3, var4)) {
         if (var3 != null && var4.expiresAt().isBefore(var3.expiresAt())) {
            this.disconnect(ProfilePublicKey.EXPIRED_PROFILE_PUBLIC_KEY);
         } else {
            try {
               SignatureValidator var5 = this.server.getProfileKeySignatureValidator();
               if (var5 == null) {
                  LOGGER.warn("Ignoring chat session from {} due to missing Services public key", this.player.getGameProfile().getName());
                  return;
               }

               this.resetPlayerChatState(var2.validate(this.player.getGameProfile(), var5));
            } catch (ProfilePublicKey.ValidationException var6) {
               LOGGER.error("Failed to validate profile key: {}", var6.getMessage());
               this.disconnect(var6.getComponent());
            }
         }
      }
   }

   @Override
   public void handleConfigurationAcknowledged(ServerboundConfigurationAcknowledgedPacket var1) {
      if (!this.waitingForSwitchToConfig) {
         throw new IllegalStateException("Client acknowledged config, but none was requested");
      } else {
         this.connection
            .setupInboundProtocol(
               ConfigurationProtocols.SERVERBOUND,
               new ServerConfigurationPacketListenerImpl(this.server, this.connection, this.createCookie(this.player.clientInformation()))
            );
      }
   }

   @Override
   public void handleChunkBatchReceived(ServerboundChunkBatchReceivedPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.chunkSender.onChunkBatchReceivedByClient(var1.desiredChunksPerTick());
   }

   @Override
   public void handleDebugSampleSubscription(ServerboundDebugSampleSubscriptionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.server.subscribeToDebugSample(this.player, var1.sampleType());
   }

   private void resetPlayerChatState(RemoteChatSession var1) {
      this.chatSession = var1;
      this.signedMessageDecoder = var1.createMessageDecoder(this.player.getUUID());
      this.chatMessageChain
         .append(
            () -> {
               this.player.setChatSession(var1);
               this.server
                  .getPlayerList()
                  .broadcastAll(
                     new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT), List.of(this.player))
                  );
            }
         );
   }

   @Override
   public void handleCustomPayload(ServerboundCustomPayloadPacket var1) {
   }

   @Override
   public ServerPlayer getPlayer() {
      return this.player;
   }

   @FunctionalInterface
   interface EntityInteraction {
      InteractionResult run(ServerPlayer var1, Entity var2, InteractionHand var3);
   }
}
