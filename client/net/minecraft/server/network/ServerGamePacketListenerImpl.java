package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
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
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
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
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
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

public class ServerGamePacketListenerImpl implements ServerPlayerConnection, TickablePacketListener, ServerGamePacketListener {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int LATENCY_CHECK_INTERVAL = 15000;
   public static final double MAX_INTERACTION_DISTANCE = Mth.square(6.0);
   private static final int NO_BLOCK_UPDATES_TO_ACK = -1;
   private static final int TRACKED_MESSAGE_DISCONNECT_THRESHOLD = 4096;
   private static final Component CHAT_VALIDATION_FAILED = Component.translatable("multiplayer.disconnect.chat_validation_failed");
   private final Connection connection;
   private final MinecraftServer server;
   public ServerPlayer player;
   private int tickCount;
   private int ackBlockChangesUpTo = -1;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveChallenge;
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
   private final AtomicReference<Instant> lastChatTimeStamp = new AtomicReference<>(Instant.EPOCH);
   @Nullable
   private RemoteChatSession chatSession;
   private SignedMessageChain.Decoder signedMessageDecoder;
   private final LastSeenMessagesValidator lastSeenMessages = new LastSeenMessagesValidator(20);
   private final MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
   private final FutureChain chatMessageChain;

   public ServerGamePacketListenerImpl(MinecraftServer var1, Connection var2, ServerPlayer var3) {
      super();
      this.server = var1;
      this.connection = var2;
      var2.setListener(this);
      this.player = var3;
      var3.connection = this;
      this.keepAliveTime = Util.getMillis();
      var3.getTextFilter().join();
      this.signedMessageDecoder = var1.enforceSecureProfile() ? SignedMessageChain.Decoder.REJECT_ALL : SignedMessageChain.Decoder.unsigned(var3.getUUID());
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
      ++this.tickCount;
      this.knownMovePacketCount = this.receivedMovePacketCount;
      if (this.clientIsFloating && !this.player.isSleeping() && !this.player.isPassenger() && !this.player.isDeadOrDying()) {
         if (++this.aboveGroundTickCount > 80) {
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
         if (this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
            if (++this.aboveGroundVehicleTickCount > 80) {
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

      this.server.getProfiler().push("keepAlive");
      long var1 = Util.getMillis();
      if (var1 - this.keepAliveTime >= 15000L) {
         if (this.keepAlivePending) {
            this.disconnect(Component.translatable("disconnect.timeout"));
         } else {
            this.keepAlivePending = true;
            this.keepAliveTime = var1;
            this.keepAliveChallenge = var1;
            this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
         }
      }

      this.server.getProfiler().pop();
      if (this.chatSpamTickCount > 0) {
         --this.chatSpamTickCount;
      }

      if (this.dropSpamTickCount > 0) {
         --this.dropSpamTickCount;
      }

      if (this.player.getLastActionTime() > 0L
         && this.server.getPlayerIdleTimeout() > 0
         && Util.getMillis() - this.player.getLastActionTime() > (long)this.server.getPlayerIdleTimeout() * 1000L * 60L) {
         this.disconnect(Component.translatable("multiplayer.disconnect.idling"));
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
      return this.connection.isConnected();
   }

   private boolean isSingleplayerOwner() {
      return this.server.isSingleplayerOwner(this.player.getGameProfile());
   }

   public void disconnect(Component var1) {
      this.connection.send(new ClientboundDisconnectPacket(var1), PacketSendListener.thenRun(() -> this.connection.disconnect(var1)));
      this.connection.setReadOnly();
      this.server.executeBlocking(this.connection::handleDisconnection);
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
               this.connection.send(new ClientboundMoveVehiclePacket(var2));
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
               this.connection.send(new ClientboundMoveVehiclePacket(var2));
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
         ResourceLocation var2 = var1.getTab();
         Advancement var3 = this.server.getAdvancements().getAdvancement(var2);
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
      this.server
         .getCommands()
         .getDispatcher()
         .getCompletionSuggestions(var3)
         .thenAccept(var2x -> this.connection.send(new ClientboundCommandSuggestionsPacket(var1.getId(), var2x)));
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

            BlockState var12 = (switch(var1.getMode()) {
               case SEQUENCE -> Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
               case AUTO -> Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
               default -> Blocks.COMMAND_BLOCK.defaultBlockState();
            }).setValue(CommandBlock.FACING, var10).setValue(CommandBlock.CONDITIONAL, Boolean.valueOf(var1.isConditional()));
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
      AbstractContainerMenu var3 = this.player.containerMenu;
      if (var3 instanceof AnvilMenu var2) {
         if (!((AnvilMenu)var2).stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, var2);
            return;
         }

         ((AnvilMenu)var2).setItemName(var1.getName());
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleSetBeaconPacket(ServerboundSetBeaconPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      AbstractContainerMenu var3 = this.player.containerMenu;
      if (var3 instanceof BeaconMenu var2) {
         if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
            return;
         }

         var2.updateEffects(var1.getPrimary(), var1.getSecondary());
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockState var3 = this.player.level().getBlockState(var2);
         BlockEntity var4 = this.player.level().getBlockEntity(var2);
         if (var4 instanceof StructureBlockEntity var5) {
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
                  } else if (var5.loadStructure(this.player.serverLevel())) {
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockState var3 = this.player.level().getBlockState(var2);
         BlockEntity var4 = this.player.level().getBlockEntity(var2);
         if (var4 instanceof JigsawBlockEntity var5) {
            var5.setName(var1.getName());
            var5.setTarget(var1.getTarget());
            var5.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, var1.getPool()));
            var5.setFinalState(var1.getFinalState());
            var5.setJoint(var1.getJoint());
            var5.setChanged();
            this.player.level().sendBlockUpdated(var2, var3, var3, 3);
         }
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleJigsawGenerate(ServerboundJigsawGeneratePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockEntity var3 = this.player.level().getBlockEntity(var2);
         if (var3 instanceof JigsawBlockEntity var4) {
            var4.generate(this.player.serverLevel(), var1.levels(), var1.keepJigsaws());
         }
      }
   }

   @Override
   public void handleSelectTrade(ServerboundSelectTradePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      int var2 = var1.getItem();
      AbstractContainerMenu var4 = this.player.containerMenu;
      if (var4 instanceof MerchantMenu var3) {
         if (!((MerchantMenu)var3).stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, var3);
            return;
         }

         ((MerchantMenu)var3).setSelectionHint(var2);
         ((MerchantMenu)var3).tryMoveItems(var2);
      }
   }

   @Override
   public void handleEditBook(ServerboundEditBookPacket var1) {
      int var2 = var1.getSlot();
      if (Inventory.isHotbarSlot(var2) || var2 == 40) {
         ArrayList var3 = Lists.newArrayList();
         Optional var4 = var1.getTitle();
         var4.ifPresent(var3::add);
         var1.getPages().stream().limit(100L).forEach(var3::add);
         Consumer var5 = var4.isPresent()
            ? var2x -> this.signBook((FilteredText)var2x.get(0), var2x.subList(1, var2x.size()), var2)
            : var2x -> this.updateBookContents(var2x, var2);
         this.filterTextPacket(var3).thenAcceptAsync(var5, this.server);
      }
   }

   private void updateBookContents(List<FilteredText> var1, int var2) {
      ItemStack var3 = this.player.getInventory().getItem(var2);
      if (var3.is(Items.WRITABLE_BOOK)) {
         this.updateBookPages(var1, UnaryOperator.identity(), var3);
      }
   }

   private void signBook(FilteredText var1, List<FilteredText> var2, int var3) {
      ItemStack var4 = this.player.getInventory().getItem(var3);
      if (var4.is(Items.WRITABLE_BOOK)) {
         ItemStack var5 = new ItemStack(Items.WRITTEN_BOOK);
         CompoundTag var6 = var4.getTag();
         if (var6 != null) {
            var5.setTag(var6.copy());
         }

         var5.addTagElement("author", StringTag.valueOf(this.player.getName().getString()));
         if (this.player.isTextFilteringEnabled()) {
            var5.addTagElement("title", StringTag.valueOf(var1.filteredOrEmpty()));
         } else {
            var5.addTagElement("filtered_title", StringTag.valueOf(var1.filteredOrEmpty()));
            var5.addTagElement("title", StringTag.valueOf(var1.raw()));
         }

         this.updateBookPages(var2, var0 -> Component.Serializer.toJson(Component.literal(var0)), var5);
         this.player.getInventory().setItem(var3, var5);
      }
   }

   private void updateBookPages(List<FilteredText> var1, UnaryOperator<String> var2, ItemStack var3) {
      ListTag var4 = new ListTag();
      if (this.player.isTextFilteringEnabled()) {
         var1.stream().map(var1x -> StringTag.valueOf(var2.apply(var1x.filteredOrEmpty()))).forEach(var4::add);
      } else {
         CompoundTag var5 = new CompoundTag();
         int var6 = 0;

         for(int var7 = var1.size(); var6 < var7; ++var6) {
            FilteredText var8 = (FilteredText)var1.get(var6);
            String var9 = var8.raw();
            var4.add(StringTag.valueOf(var2.apply(var9)));
            if (var8.isFiltered()) {
               var5.putString(String.valueOf(var6), var2.apply(var8.filteredOrEmpty()));
            }
         }

         if (!var5.isEmpty()) {
            var3.addTagElement("filtered_pages", var5);
         }
      }

      var3.addTagElement("pages", var4);
   }

   @Override
   public void handleEntityTagQuery(ServerboundEntityTagQuery var1) {
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
   public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (this.player.hasPermissions(2)) {
         BlockEntity var2 = this.player.level().getBlockEntity(var1.getPos());
         CompoundTag var3 = var2 != null ? var2.saveWithoutMetadata() : null;
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
                     ++this.receivedMovePacketCount;
                     int var27 = this.receivedMovePacketCount - this.knownMovePacketCount;
                     if (var27 > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), var27);
                        var27 = 1;
                     }

                     if (!this.player.isChangingDimension()
                        && (!this.player.level().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                        float var28 = this.player.isFallFlying() ? 300.0F : 100.0F;
                        if (var25 - var23 > (double)(var28 * (float)var27) && !this.isSingleplayerOwner()) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName().getString(), var17, var19, var21});
                           this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
                           return;
                        }
                     }

                     AABB var41 = this.player.getBoundingBox();
                     var17 = var3 - this.lastGoodX;
                     var19 = var5 - this.lastGoodY;
                     var21 = var7 - this.lastGoodZ;
                     boolean var29 = var19 > 0.0;
                     if (this.player.onGround() && !var1.isOnGround() && var29) {
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
                        || (!var33 || !var2.noCollision(this.player, var41)) && !this.isPlayerCollidingWithAnythingNew(var2, var41, var3, var5, var7)) {
                        this.player.absMoveTo(var3, var5, var7, var9, var10);
                        this.clientIsFloating = var19 >= -0.03125
                           && !var30
                           && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR
                           && !this.server.isFlightAllowed()
                           && !this.player.getAbilities().mayfly
                           && !this.player.hasEffect(MobEffects.LEVITATION)
                           && !this.player.isFallFlying()
                           && !this.player.isAutoSpinAttack()
                           && this.noBlocksAround(this.player);
                        this.player.serverLevel().getChunkSource().move(this.player);
                        this.player.doCheckFallDamage(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15, var1.isOnGround());
                        this.player
                           .setOnGroundWithKnownMovement(
                              var1.isOnGround(), new Vec3(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15)
                           );
                        if (var29) {
                           this.player.resetFallDistance();
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

      for(VoxelShape var13 : var10) {
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
      switch(var3) {
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
         Vec3 var8 = Vec3.atCenterOf(var7);
         if (!(this.player.getEyePosition().distanceToSqr(var8) > MAX_INTERACTION_DISTANCE)) {
            Vec3 var9 = var6.subtract(var8);
            double var10 = 1.0000001;
            if (Math.abs(var9.x()) < 1.0000001 && Math.abs(var9.y()) < 1.0000001 && Math.abs(var9.z()) < 1.0000001) {
               Direction var12 = var5.getDirection();
               this.player.resetLastActionTime();
               int var13 = this.player.level().getMaxBuildHeight();
               if (var7.getY() < var13) {
                  if (this.awaitingPositionFromClient == null
                     && this.player.distanceToSqr((double)var7.getX() + 0.5, (double)var7.getY() + 0.5, (double)var7.getZ() + 0.5) < 64.0
                     && var2.mayInteract(this.player, var7)) {
                     InteractionResult var14 = this.player.gameMode.useItemOn(this.player, var2, var4, var3, var5);
                     if (var12 == Direction.UP && !var14.consumesAction() && var7.getY() >= var13 - 1 && wasBlockPlacementAttempt(this.player, var4)) {
                        MutableComponent var15 = Component.translatable("build.tooHigh", var13 - 1).withStyle(ChatFormatting.RED);
                        this.player.sendSystemMessage(var15, true);
                     } else if (var14.shouldSwing()) {
                        this.player.swing(var3, true);
                     }
                  }
               } else {
                  MutableComponent var16 = Component.translatable("build.tooHigh", var13 - 1).withStyle(ChatFormatting.RED);
                  this.player.sendSystemMessage(var16, true);
               }

               this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var7));
               this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var7.relative(var12)));
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
         for(ServerLevel var3 : this.server.getAllLevels()) {
            Entity var4 = var1.getEntity(var3);
            if (var4 != null) {
               this.player.teleportTo(var3, var4.getX(), var4.getY(), var4.getZ(), var4.getYRot(), var4.getXRot());
               return;
            }
         }
      }
   }

   @Override
   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      if (var1.getAction() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) {
         LOGGER.info("Disconnecting {} due to resource pack rejection", this.player.getName());
         this.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handlePaddleBoat(ServerboundPaddleBoatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      Entity var2 = this.player.getControlledVehicle();
      if (var2 instanceof Boat var3) {
         var3.setPaddleState(var1.getLeft(), var1.getRight());
      }
   }

   @Override
   public void handlePong(ServerboundPongPacket var1) {
   }

   @Override
   public void onDisconnect(Component var1) {
      this.chatMessageChain.close();
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), var1.getString());
      this.server.invalidateStatus();
      this.server
         .getPlayerList()
         .broadcastSystemMessage(Component.translatable("multiplayer.player.left", this.player.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);
      this.player.disconnect();
      this.server.getPlayerList().remove(this.player);
      this.player.getTextFilter().leave();
      if (this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.halt(false);
      }
   }

   public void ackBlockChangesUpTo(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Expected packet sequence nr >= 0");
      } else {
         this.ackBlockChangesUpTo = Math.max(var1, this.ackBlockChangesUpTo);
      }
   }

   @Override
   public void send(Packet<?> var1) {
      this.send(var1, null);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2) {
      try {
         this.connection.send(var1, var2);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Sending packet");
         CrashReportCategory var5 = var4.addCategory("Packet being sent");
         var5.setDetail("Packet class", () -> var1.getClass().getCanonicalName());
         throw new ReportedException(var4);
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
      if (isChatMessageIllegal(var1.message())) {
         this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
      } else {
         Optional var2 = this.tryHandleChat(var1.message(), var1.timeStamp(), var1.lastSeenMessages());
         if (var2.isPresent()) {
            this.server.submit(() -> {
               PlayerChatMessage var3;
               try {
                  var3 = this.getSignedMessage(var1, (LastSeenMessages)var2.get());
               } catch (SignedMessageChain.DecodeException var6) {
                  this.handleMessageDecodeFailure(var6);
                  return;
               }

               CompletableFuture var4 = this.filterTextPacket(var3.signedContent());
               CompletableFuture var5 = this.server.getChatDecorator().decorate(this.player, var3.decoratedContent());
               this.chatMessageChain.append(var4x -> CompletableFuture.allOf(var4, var5).thenAcceptAsync(var4xx -> {
                     PlayerChatMessage var5x = var3.withUnsignedContent((Component)var5.join()).filter(((FilteredText)var4.join()).mask());
                     this.broadcastChatMessage(var5x);
                  }, var4x));
            });
         }
      }
   }

   @Override
   public void handleChatCommand(ServerboundChatCommandPacket var1) {
      if (isChatMessageIllegal(var1.command())) {
         this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
      } else {
         Optional var2 = this.tryHandleChat(var1.command(), var1.timeStamp(), var1.lastSeenMessages());
         if (var2.isPresent()) {
            this.server.submit(() -> {
               this.performChatCommand(var1, (LastSeenMessages)var2.get());
               this.detectRateSpam();
            });
         }
      }
   }

   private void performChatCommand(ServerboundChatCommandPacket var1, LastSeenMessages var2) {
      ParseResults var3 = this.parseCommand(var1.command());

      Map var4;
      try {
         var4 = this.collectSignedArguments(var1, SignableCommand.of(var3), var2);
      } catch (SignedMessageChain.DecodeException var6) {
         this.handleMessageDecodeFailure(var6);
         return;
      }

      CommandSigningContext.SignedArguments var5 = new CommandSigningContext.SignedArguments(var4);
      var3 = Commands.mapSource(var3, var1x -> var1x.withSigningContext(var5));
      this.server.getCommands().performCommand(var3, var1.command());
   }

   private void handleMessageDecodeFailure(SignedMessageChain.DecodeException var1) {
      if (var1.shouldDisconnect()) {
         this.disconnect(var1.getComponent());
      } else {
         this.player.sendSystemMessage(var1.getComponent().copy().withStyle(ChatFormatting.RED));
      }
   }

   private Map<String, PlayerChatMessage> collectSignedArguments(ServerboundChatCommandPacket var1, SignableCommand<?> var2, LastSeenMessages var3) throws SignedMessageChain.DecodeException {
      Object2ObjectOpenHashMap var4 = new Object2ObjectOpenHashMap();

      for(SignableCommand.Argument var6 : var2.arguments()) {
         MessageSignature var7 = var1.argumentSignatures().get(var6.name());
         SignedMessageBody var8 = new SignedMessageBody(var6.value(), var1.timeStamp(), var1.salt(), var3);
         var4.put(var6.name(), this.signedMessageDecoder.unpack(var7, var8));
      }

      return var4;
   }

   private ParseResults<CommandSourceStack> parseCommand(String var1) {
      CommandDispatcher var2 = this.server.getCommands().getDispatcher();
      return var2.parse(var1, this.player.createCommandSourceStack());
   }

   private Optional<LastSeenMessages> tryHandleChat(String var1, Instant var2, LastSeenMessages.Update var3) {
      if (!this.updateChatOrder(var2)) {
         LOGGER.warn("{} sent out-of-order chat: '{}'", this.player.getName().getString(), var1);
         this.disconnect(Component.translatable("multiplayer.disconnect.out_of_order_chat"));
         return Optional.empty();
      } else {
         Optional var4 = this.unpackAndApplyLastSeen(var3);
         if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
            this.send(new ClientboundSystemChatPacket(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED), false));
            return Optional.empty();
         } else {
            this.player.resetLastActionTime();
            return var4;
         }
      }
   }

   private Optional<LastSeenMessages> unpackAndApplyLastSeen(LastSeenMessages.Update var1) {
      synchronized(this.lastSeenMessages) {
         Optional var3 = this.lastSeenMessages.applyUpdate(var1);
         if (var3.isEmpty()) {
            LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
            this.disconnect(CHAT_VALIDATION_FAILED);
         }

         return var3;
      }
   }

   private boolean updateChatOrder(Instant var1) {
      Instant var2;
      do {
         var2 = this.lastChatTimeStamp.get();
         if (var1.isBefore(var2)) {
            return false;
         }
      } while(!this.lastChatTimeStamp.compareAndSet(var2, var1));

      return true;
   }

   private static boolean isChatMessageIllegal(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         if (!SharedConstants.isAllowedChatCharacter(var0.charAt(var1))) {
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
      synchronized(this.lastSeenMessages) {
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handlePlayerCommand(ServerboundPlayerCommandPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      switch(var1.getAction()) {
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
            Entity var7 = this.player.getControlledVehicle();
            if (var7 instanceof PlayerRideableJumping var5) {
               int var8 = var1.getData();
               if (var5.canJump() && var8 > 0) {
                  var5.handleStartJump(var8);
               }
            }
            break;
         case STOP_RIDING_JUMP:
            Entity var6 = this.player.getControlledVehicle();
            if (var6 instanceof PlayerRideableJumping var4) {
               var4.handleStopJump();
            }
            break;
         case OPEN_INVENTORY:
            Entity var3 = this.player.getVehicle();
            if (var3 instanceof HasCustomInventoryScreen var2) {
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
         this.messageSignatureCache.push(var1);
         int var3;
         synchronized(this.lastSeenMessages) {
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
            var2.toNetwork(this.player.level().registryAccess())
         )
      );
      this.addPendingMessage(var1);
   }

   public void sendDisguisedChatMessage(Component var1, ChatType.Bound var2) {
      this.send(new ClientboundDisguisedChatPacket(var1, var2.toNetwork(this.player.level().registryAccess())));
   }

   public SocketAddress getRemoteAddress() {
      return this.connection.getRemoteAddress();
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
         if (var4.distanceToSqr(this.player.getEyePosition()) < MAX_INTERACTION_DISTANCE) {
            var1.dispatch(
               new ServerboundInteractPacket.Handler() {
                  private void performInteraction(InteractionHand var1, ServerGamePacketListenerImpl.EntityInteraction var2x) {
                     ItemStack var3x = ServerGamePacketListenerImpl.this.player.getItemInHand(var1);
                     if (var3x.isItemEnabled(var2.enabledFeatures())) {
                        ItemStack var4 = var3x.copy();
                        InteractionResult var5 = var2x.run(ServerGamePacketListenerImpl.this.player, var3, var1);
                        if (var5.consumesAction()) {
                           CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerGamePacketListenerImpl.this.player, var4, var3);
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
                        && !(var3 instanceof AbstractArrow)
                        && var3 != ServerGamePacketListenerImpl.this.player) {
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
      switch(var2) {
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

               while(var4.hasNext()) {
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
               .ifPresent(var2 -> ((RecipeBookMenu)this.player.containerMenu).handlePlacement(var1.isShiftDown(), var2, this.player));
         }
      }
   }

   @Override
   public void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.player.serverLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == var1.getContainerId() && !this.player.isSpectator()) {
         if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
         } else {
            boolean var2 = this.player.containerMenu.clickMenuButton(this.player, var1.getButtonId());
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
         boolean var2 = var1.getSlotNum() < 0;
         ItemStack var3 = var1.getItem();
         if (!var3.isItemEnabled(this.player.level().enabledFeatures())) {
            return;
         }

         CompoundTag var4 = BlockItem.getBlockEntityData(var3);
         if (!var3.isEmpty() && var4 != null && var4.contains("x") && var4.contains("y") && var4.contains("z")) {
            BlockPos var5 = BlockEntity.getPosFromTag(var4);
            if (this.player.level().isLoaded(var5)) {
               BlockEntity var6 = this.player.level().getBlockEntity(var5);
               if (var6 != null) {
                  var6.saveToItem(var3);
               }
            }
         }

         boolean var7 = var1.getSlotNum() >= 1 && var1.getSlotNum() <= 45;
         boolean var8 = var3.isEmpty() || var3.getDamageValue() >= 0 && var3.getCount() <= 64 && !var3.isEmpty();
         if (var7 && var8) {
            this.player.inventoryMenu.getSlot(var1.getSlotNum()).setByPlayer(var3);
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
      this.filterTextPacket(var2).thenAcceptAsync(var2x -> this.updateSignText(var1, var2x), this.server);
   }

   private void updateSignText(ServerboundSignUpdatePacket var1, List<FilteredText> var2) {
      this.player.resetLastActionTime();
      ServerLevel var3 = this.player.serverLevel();
      BlockPos var4 = var1.getPos();
      if (var3.hasChunkAt(var4)) {
         BlockEntity var5 = var3.getBlockEntity(var4);
         if (!(var5 instanceof SignBlockEntity)) {
            return;
         }

         SignBlockEntity var6 = (SignBlockEntity)var5;
         var6.updateSignText(this.player, var1.isFrontText(), var2);
      }
   }

   @Override
   public void handleKeepAlive(ServerboundKeepAlivePacket var1) {
      if (this.keepAlivePending && var1.getId() == this.keepAliveChallenge) {
         int var2 = (int)(Util.getMillis() - this.keepAliveTime);
         this.player.latency = (this.player.latency * 3 + var2) / 4;
         this.keepAlivePending = false;
      } else if (!this.isSingleplayerOwner()) {
         this.disconnect(Component.translatable("disconnect.timeout"));
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
      this.player.updateOptions(var1);
   }

   @Override
   public void handleCustomPayload(ServerboundCustomPayloadPacket var1) {
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

               this.resetPlayerChatState(var2.validate(this.player.getGameProfile(), var5, Duration.ZERO));
            } catch (ProfilePublicKey.ValidationException var6) {
               LOGGER.error("Failed to validate profile key: {}", var6.getMessage());
               this.disconnect(var6.getComponent());
            }
         }
      }
   }

   private void resetPlayerChatState(RemoteChatSession var1) {
      this.chatSession = var1;
      this.signedMessageDecoder = var1.createMessageDecoder(this.player.getUUID());
      this.chatMessageChain
         .append(
            var2 -> {
               this.player.setChatSession(var1);
               this.server
                  .getPlayerList()
                  .broadcastAll(
                     new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT), List.of(this.player))
                  );
               return CompletableFuture.completedFuture(null);
            }
         );
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
