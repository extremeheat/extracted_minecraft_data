package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerGamePacketListenerImpl implements ServerPlayerConnection, ServerGamePacketListener {
   static final Logger LOGGER = LogManager.getLogger();
   private static final int LATENCY_CHECK_INTERVAL = 15000;
   public final Connection connection;
   private final MinecraftServer server;
   public ServerPlayer player;
   private int tickCount;
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

   public ServerGamePacketListenerImpl(MinecraftServer var1, Connection var2, ServerPlayer var3) {
      super();
      this.server = var1;
      this.connection = var2;
      var2.setListener(this);
      this.player = var3;
      var3.connection = this;
      this.keepAliveTime = Util.getMillis();
      var3.getTextFilter().join();
   }

   public void tick() {
      this.resetPosition();
      this.player.xo = this.player.getX();
      this.player.yo = this.player.getY();
      this.player.zo = this.player.getZ();
      this.player.doTick();
      this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.getYRot(), this.player.getXRot());
      ++this.tickCount;
      this.knownMovePacketCount = this.receivedMovePacketCount;
      if (this.clientIsFloating && !this.player.isSleeping()) {
         if (++this.aboveGroundTickCount > 80) {
            LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.flying"));
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
               this.disconnect(new TranslatableComponent("multiplayer.disconnect.flying"));
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
            this.disconnect(new TranslatableComponent("disconnect.timeout"));
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

      if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMillis() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.idling"));
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

   public Connection getConnection() {
      return this.connection;
   }

   private boolean isSingleplayerOwner() {
      return this.server.isSingleplayerOwner(this.player.getGameProfile());
   }

   public void disconnect(Component var1) {
      this.connection.send(new ClientboundDisconnectPacket(var1), (var2) -> {
         this.connection.disconnect(var1);
      });
      this.connection.setReadOnly();
      MinecraftServer var10000 = this.server;
      Connection var10001 = this.connection;
      Objects.requireNonNull(var10001);
      var10000.executeBlocking(var10001::handleDisconnection);
   }

   private <T, R> void filterTextPacket(T var1, Consumer<R> var2, BiFunction<TextFilter, T, CompletableFuture<R>> var3) {
      MinecraftServer var4 = this.player.getLevel().getServer();
      Consumer var5 = (var2x) -> {
         if (this.getConnection().isConnected()) {
            var2.accept(var2x);
         } else {
            LOGGER.debug("Ignoring packet due to disconnection");
         }

      };
      ((CompletableFuture)var3.apply(this.player.getTextFilter(), var1)).thenAcceptAsync(var5, var4);
   }

   private void filterTextPacket(String var1, Consumer<TextFilter.FilteredText> var2) {
      this.filterTextPacket(var1, var2, TextFilter::processStreamMessage);
   }

   private void filterTextPacket(List<String> var1, Consumer<List<TextFilter.FilteredText>> var2) {
      this.filterTextPacket(var1, var2, TextFilter::processMessageBundle);
   }

   public void handlePlayerInput(ServerboundPlayerInputPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.setPlayerInput(var1.getXxa(), var1.getZza(), var1.isJumping(), var1.isShiftKeyDown());
   }

   private static boolean containsInvalidValues(double var0, double var2, double var4, float var6, float var7) {
      return Double.isNaN(var0) || Double.isNaN(var2) || Double.isNaN(var4) || !Floats.isFinite(var7) || !Floats.isFinite(var6);
   }

   private static double clampHorizontal(double var0) {
      return Mth.clamp(var0, -3.0E7D, 3.0E7D);
   }

   private static double clampVertical(double var0) {
      return Mth.clamp(var0, -2.0E7D, 2.0E7D);
   }

   public void handleMoveVehicle(ServerboundMoveVehiclePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (containsInvalidValues(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), var1.getXRot())) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_vehicle_movement"));
      } else {
         Entity var2 = this.player.getRootVehicle();
         if (var2 != this.player && var2.getControllingPassenger() == this.player && var2 == this.lastVehicle) {
            ServerLevel var3 = this.player.getLevel();
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
            if (var26 - var24 > 100.0D && !this.isSingleplayerOwner()) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", var2.getName().getString(), this.player.getName().getString(), var18, var20, var22);
               this.connection.send(new ClientboundMoveVehiclePacket(var2));
               return;
            }

            boolean var28 = var3.noCollision(var2, var2.getBoundingBox().deflate(0.0625D));
            var18 = var10 - this.vehicleLastGoodX;
            var20 = var12 - this.vehicleLastGoodY - 1.0E-6D;
            var22 = var14 - this.vehicleLastGoodZ;
            var2.move(MoverType.PLAYER, new Vec3(var18, var20, var22));
            double var29 = var20;
            var18 = var10 - var2.getX();
            var20 = var12 - var2.getY();
            if (var20 > -0.5D || var20 < 0.5D) {
               var20 = 0.0D;
            }

            var22 = var14 - var2.getZ();
            var26 = var18 * var18 + var20 * var20 + var22 * var22;
            boolean var31 = false;
            if (var26 > 0.0625D) {
               var31 = true;
               LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", var2.getName().getString(), this.player.getName().getString(), Math.sqrt(var26));
            }

            var2.absMoveTo(var10, var12, var14, var16, var17);
            boolean var32 = var3.noCollision(var2, var2.getBoundingBox().deflate(0.0625D));
            if (var28 && (var31 || !var32)) {
               var2.absMoveTo(var4, var6, var8, var16, var17);
               this.connection.send(new ClientboundMoveVehiclePacket(var2));
               return;
            }

            this.player.getLevel().getChunkSource().move(this.player);
            this.player.checkMovementStatistics(this.player.getX() - var4, this.player.getY() - var6, this.player.getZ() - var8);
            this.clientVehicleIsFloating = var29 >= -0.03125D && !this.server.isFlightAllowed() && this.noBlocksAround(var2);
            this.vehicleLastGoodX = var2.getX();
            this.vehicleLastGoodY = var2.getY();
            this.vehicleLastGoodZ = var2.getZ();
         }

      }
   }

   private boolean noBlocksAround(Entity var1) {
      return var1.level.getBlockStates(var1.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D)).allMatch(BlockBehaviour.BlockStateBase::isAir);
   }

   public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (var1.getId() == this.awaitingTeleport) {
         this.player.absMoveTo(this.awaitingPositionFromClient.field_414, this.awaitingPositionFromClient.field_415, this.awaitingPositionFromClient.field_416, this.player.getYRot(), this.player.getXRot());
         this.lastGoodX = this.awaitingPositionFromClient.field_414;
         this.lastGoodY = this.awaitingPositionFromClient.field_415;
         this.lastGoodZ = this.awaitingPositionFromClient.field_416;
         if (this.player.isChangingDimension()) {
            this.player.hasChangedDimension();
         }

         this.awaitingPositionFromClient = null;
      }

   }

   public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      Optional var10000 = this.server.getRecipeManager().byKey(var1.getRecipe());
      ServerRecipeBook var10001 = this.player.getRecipeBook();
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::removeHighlight);
   }

   public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.getRecipeBook().setBookSetting(var1.getBookType(), var1.isOpen(), var1.isFiltering());
   }

   public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (var1.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         ResourceLocation var2 = var1.getTab();
         Advancement var3 = this.server.getAdvancements().getAdvancement(var2);
         if (var3 != null) {
            this.player.getAdvancements().setSelectedTab(var3);
         }
      }

   }

   public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      StringReader var2 = new StringReader(var1.getCommand());
      if (var2.canRead() && var2.peek() == '/') {
         var2.skip();
      }

      ParseResults var3 = this.server.getCommands().getDispatcher().parse(var2, this.player.createCommandSourceStack());
      this.server.getCommands().getDispatcher().getCompletionSuggestions(var3).thenAccept((var2x) -> {
         this.connection.send(new ClientboundCommandSuggestionsPacket(var1.getId(), var2x));
      });
   }

   public void handleSetCommandBlock(ServerboundSetCommandBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notEnabled"), Util.NIL_UUID);
      } else if (!this.player.canUseGameMasterBlocks()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notAllowed"), Util.NIL_UUID);
      } else {
         BaseCommandBlock var2 = null;
         CommandBlockEntity var3 = null;
         BlockPos var4 = var1.getPos();
         BlockEntity var5 = this.player.level.getBlockEntity(var4);
         if (var5 instanceof CommandBlockEntity) {
            var3 = (CommandBlockEntity)var5;
            var2 = var3.getCommandBlock();
         }

         String var6 = var1.getCommand();
         boolean var7 = var1.isTrackOutput();
         if (var2 != null) {
            CommandBlockEntity.Mode var8 = var3.getMode();
            BlockState var9 = this.player.level.getBlockState(var4);
            Direction var10 = (Direction)var9.getValue(CommandBlock.FACING);
            BlockState var11;
            switch(var1.getMode()) {
            case SEQUENCE:
               var11 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
               break;
            case AUTO:
               var11 = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
               break;
            case REDSTONE:
            default:
               var11 = Blocks.COMMAND_BLOCK.defaultBlockState();
            }

            BlockState var12 = (BlockState)((BlockState)var11.setValue(CommandBlock.FACING, var10)).setValue(CommandBlock.CONDITIONAL, var1.isConditional());
            if (var12 != var9) {
               this.player.level.setBlock(var4, var12, 2);
               var5.setBlockState(var12);
               this.player.level.getChunkAt(var4).setBlockEntity(var5);
            }

            var2.setCommand(var6);
            var2.setTrackOutput(var7);
            if (!var7) {
               var2.setLastOutput((Component)null);
            }

            var3.setAutomatic(var1.isAutomatic());
            if (var8 != var1.getMode()) {
               var3.onModeSwitch();
            }

            var2.onUpdated();
            if (!StringUtil.isNullOrEmpty(var6)) {
               this.player.sendMessage(new TranslatableComponent("advMode.setCommand.success", new Object[]{var6}), Util.NIL_UUID);
            }
         }

      }
   }

   public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notEnabled"), Util.NIL_UUID);
      } else if (!this.player.canUseGameMasterBlocks()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notAllowed"), Util.NIL_UUID);
      } else {
         BaseCommandBlock var2 = var1.getCommandBlock(this.player.level);
         if (var2 != null) {
            var2.setCommand(var1.getCommand());
            var2.setTrackOutput(var1.isTrackOutput());
            if (!var1.isTrackOutput()) {
               var2.setLastOutput((Component)null);
            }

            var2.onUpdated();
            this.player.sendMessage(new TranslatableComponent("advMode.setCommand.success", new Object[]{var1.getCommand()}), Util.NIL_UUID);
         }

      }
   }

   public void handlePickItem(ServerboundPickItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.getInventory().pickSlot(var1.getSlot());
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, this.player.getInventory().selected, this.player.getInventory().getItem(this.player.getInventory().selected)));
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, var1.getSlot(), this.player.getInventory().getItem(var1.getSlot())));
      this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.getInventory().selected));
   }

   public void handleRenameItem(ServerboundRenameItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.containerMenu instanceof AnvilMenu) {
         AnvilMenu var2 = (AnvilMenu)this.player.containerMenu;
         String var3 = SharedConstants.filterText(var1.getName());
         if (var3.length() <= 50) {
            var2.setItemName(var3);
         }
      }

   }

   public void handleSetBeaconPacket(ServerboundSetBeaconPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.containerMenu instanceof BeaconMenu) {
         ((BeaconMenu)this.player.containerMenu).updateEffects(var1.getPrimary(), var1.getSecondary());
      }

   }

   public void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockState var3 = this.player.level.getBlockState(var2);
         BlockEntity var4 = this.player.level.getBlockEntity(var2);
         if (var4 instanceof StructureBlockEntity) {
            StructureBlockEntity var5 = (StructureBlockEntity)var4;
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
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.save_success", new Object[]{var6}), false);
                  } else {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.save_failure", new Object[]{var6}), false);
                  }
               } else if (var1.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                  if (!var5.isStructureLoadable()) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.load_not_found", new Object[]{var6}), false);
                  } else if (var5.loadStructure(this.player.getLevel())) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.load_success", new Object[]{var6}), false);
                  } else {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.load_prepare", new Object[]{var6}), false);
                  }
               } else if (var1.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                  if (var5.detectSize()) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.size_success", new Object[]{var6}), false);
                  } else {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.size_failure"), false);
                  }
               }
            } else {
               this.player.displayClientMessage(new TranslatableComponent("structure_block.invalid_structure_name", new Object[]{var1.getName()}), false);
            }

            var5.setChanged();
            this.player.level.sendBlockUpdated(var2, var3, var3, 3);
         }

      }
   }

   public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockState var3 = this.player.level.getBlockState(var2);
         BlockEntity var4 = this.player.level.getBlockEntity(var2);
         if (var4 instanceof JigsawBlockEntity) {
            JigsawBlockEntity var5 = (JigsawBlockEntity)var4;
            var5.setName(var1.getName());
            var5.setTarget(var1.getTarget());
            var5.setPool(var1.getPool());
            var5.setFinalState(var1.getFinalState());
            var5.setJoint(var1.getJoint());
            var5.setChanged();
            this.player.level.sendBlockUpdated(var2, var3, var3, 3);
         }

      }
   }

   public void handleJigsawGenerate(ServerboundJigsawGeneratePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = var1.getPos();
         BlockEntity var3 = this.player.level.getBlockEntity(var2);
         if (var3 instanceof JigsawBlockEntity) {
            JigsawBlockEntity var4 = (JigsawBlockEntity)var3;
            var4.generate(this.player.getLevel(), var1.levels(), var1.keepJigsaws());
         }

      }
   }

   public void handleSelectTrade(ServerboundSelectTradePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      int var2 = var1.getItem();
      AbstractContainerMenu var3 = this.player.containerMenu;
      if (var3 instanceof MerchantMenu) {
         MerchantMenu var4 = (MerchantMenu)var3;
         var4.setSelectionHint(var2);
         var4.tryMoveItems(var2);
      }

   }

   public void handleEditBook(ServerboundEditBookPacket var1) {
      int var2 = var1.getSlot();
      if (Inventory.isHotbarSlot(var2) || var2 == 40) {
         ArrayList var3 = Lists.newArrayList();
         Optional var4 = var1.getTitle();
         Objects.requireNonNull(var3);
         var4.ifPresent(var3::add);
         Stream var10000 = var1.getPages().stream().limit(100L);
         Objects.requireNonNull(var3);
         var10000.forEach(var3::add);
         this.filterTextPacket((List)var3, var4.isPresent() ? (var2x) -> {
            this.signBook((TextFilter.FilteredText)var2x.get(0), var2x.subList(1, var2x.size()), var2);
         } : (var2x) -> {
            this.updateBookContents(var2x, var2);
         });
      }
   }

   private void updateBookContents(List<TextFilter.FilteredText> var1, int var2) {
      ItemStack var3 = this.player.getInventory().getItem(var2);
      if (var3.method_87(Items.WRITABLE_BOOK)) {
         this.updateBookPages(var1, UnaryOperator.identity(), var3);
      }
   }

   private void signBook(TextFilter.FilteredText var1, List<TextFilter.FilteredText> var2, int var3) {
      ItemStack var4 = this.player.getInventory().getItem(var3);
      if (var4.method_87(Items.WRITABLE_BOOK)) {
         ItemStack var5 = new ItemStack(Items.WRITTEN_BOOK);
         CompoundTag var6 = var4.getTag();
         if (var6 != null) {
            var5.setTag(var6.copy());
         }

         var5.addTagElement("author", StringTag.valueOf(this.player.getName().getString()));
         if (this.player.isTextFilteringEnabled()) {
            var5.addTagElement("title", StringTag.valueOf(var1.getFiltered()));
         } else {
            var5.addTagElement("filtered_title", StringTag.valueOf(var1.getFiltered()));
            var5.addTagElement("title", StringTag.valueOf(var1.getRaw()));
         }

         this.updateBookPages(var2, (var0) -> {
            return Component.Serializer.toJson(new TextComponent(var0));
         }, var5);
         this.player.getInventory().setItem(var3, var5);
      }
   }

   private void updateBookPages(List<TextFilter.FilteredText> var1, UnaryOperator<String> var2, ItemStack var3) {
      ListTag var4 = new ListTag();
      if (this.player.isTextFilteringEnabled()) {
         Stream var10000 = var1.stream().map((var1x) -> {
            return StringTag.valueOf((String)var2.apply(var1x.getFiltered()));
         });
         Objects.requireNonNull(var4);
         var10000.forEach(var4::add);
      } else {
         CompoundTag var5 = new CompoundTag();
         int var6 = 0;

         for(int var7 = var1.size(); var6 < var7; ++var6) {
            TextFilter.FilteredText var8 = (TextFilter.FilteredText)var1.get(var6);
            String var9 = var8.getRaw();
            var4.add(StringTag.valueOf((String)var2.apply(var9)));
            String var10 = var8.getFiltered();
            if (!var9.equals(var10)) {
               var5.putString(String.valueOf(var6), (String)var2.apply(var10));
            }
         }

         if (!var5.isEmpty()) {
            var3.addTagElement("filtered_pages", var5);
         }
      }

      var3.addTagElement("pages", var4);
   }

   public void handleEntityTagQuery(ServerboundEntityTagQuery var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.hasPermissions(2)) {
         Entity var2 = this.player.getLevel().getEntity(var1.getEntityId());
         if (var2 != null) {
            CompoundTag var3 = var2.saveWithoutId(new CompoundTag());
            this.player.connection.send(new ClientboundTagQueryPacket(var1.getTransactionId(), var3));
         }

      }
   }

   public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.hasPermissions(2)) {
         BlockEntity var2 = this.player.getLevel().getBlockEntity(var1.getPos());
         CompoundTag var3 = var2 != null ? var2.saveWithoutMetadata() : null;
         this.player.connection.send(new ClientboundTagQueryPacket(var1.getTransactionId(), var3));
      }
   }

   public void handleMovePlayer(ServerboundMovePlayerPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (containsInvalidValues(var1.getX(0.0D), var1.getY(0.0D), var1.getZ(0.0D), var1.getYRot(0.0F), var1.getXRot(0.0F))) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_player_movement"));
      } else {
         ServerLevel var2 = this.player.getLevel();
         if (!this.player.wonGame) {
            if (this.tickCount == 0) {
               this.resetPosition();
            }

            if (this.awaitingPositionFromClient != null) {
               if (this.tickCount - this.awaitingTeleportTime > 20) {
                  this.awaitingTeleportTime = this.tickCount;
                  this.teleport(this.awaitingPositionFromClient.field_414, this.awaitingPositionFromClient.field_415, this.awaitingPositionFromClient.field_416, this.player.getYRot(), this.player.getXRot());
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
                  this.player.getLevel().getChunkSource().move(this.player);
               } else {
                  double var11 = this.player.getX();
                  double var13 = this.player.getY();
                  double var15 = this.player.getZ();
                  double var17 = this.player.getY();
                  double var19 = var3 - this.firstGoodX;
                  double var21 = var5 - this.firstGoodY;
                  double var23 = var7 - this.firstGoodZ;
                  double var25 = this.player.getDeltaMovement().lengthSqr();
                  double var27 = var19 * var19 + var21 * var21 + var23 * var23;
                  if (this.player.isSleeping()) {
                     if (var27 > 1.0D) {
                        this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), var9, var10);
                     }

                  } else {
                     ++this.receivedMovePacketCount;
                     int var29 = this.receivedMovePacketCount - this.knownMovePacketCount;
                     if (var29 > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), var29);
                        var29 = 1;
                     }

                     if (!this.player.isChangingDimension() && (!this.player.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                        float var30 = this.player.isFallFlying() ? 300.0F : 100.0F;
                        if (var27 - var25 > (double)(var30 * (float)var29) && !this.isSingleplayerOwner()) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), var19, var21, var23);
                           this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
                           return;
                        }
                     }

                     AABB var35 = this.player.getBoundingBox();
                     var19 = var3 - this.lastGoodX;
                     var21 = var5 - this.lastGoodY;
                     var23 = var7 - this.lastGoodZ;
                     boolean var31 = var21 > 0.0D;
                     if (this.player.isOnGround() && !var1.isOnGround() && var31) {
                        this.player.jumpFromGround();
                     }

                     this.player.move(MoverType.PLAYER, new Vec3(var19, var21, var23));
                     double var32 = var21;
                     var19 = var3 - this.player.getX();
                     var21 = var5 - this.player.getY();
                     if (var21 > -0.5D || var21 < 0.5D) {
                        var21 = 0.0D;
                     }

                     var23 = var7 - this.player.getZ();
                     var27 = var19 * var19 + var21 * var21 + var23 * var23;
                     boolean var34 = false;
                     if (!this.player.isChangingDimension() && var27 > 0.0625D && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                        var34 = true;
                        LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                     }

                     this.player.absMoveTo(var3, var5, var7, var9, var10);
                     if (this.player.noPhysics || this.player.isSleeping() || (!var34 || !var2.noCollision(this.player, var35)) && !this.isPlayerCollidingWithAnythingNew(var2, var35)) {
                        this.clientIsFloating = var32 >= -0.03125D && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.getAbilities().mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isFallFlying() && this.noBlocksAround(this.player);
                        this.player.getLevel().getChunkSource().move(this.player);
                        this.player.doCheckFallDamage(this.player.getY() - var17, var1.isOnGround());
                        this.player.setOnGround(var1.isOnGround());
                        if (var31) {
                           this.player.resetFallDistance();
                        }

                        this.player.checkMovementStatistics(this.player.getX() - var11, this.player.getY() - var13, this.player.getZ() - var15);
                        this.lastGoodX = this.player.getX();
                        this.lastGoodY = this.player.getY();
                        this.lastGoodZ = this.player.getZ();
                     } else {
                        this.teleport(var11, var13, var15, var9, var10);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPlayerCollidingWithAnythingNew(LevelReader var1, AABB var2) {
      Iterable var3 = var1.getCollisions(this.player, this.player.getBoundingBox().deflate(9.999999747378752E-6D));
      VoxelShape var4 = Shapes.create(var2.deflate(9.999999747378752E-6D));
      Iterator var5 = var3.iterator();

      VoxelShape var6;
      do {
         if (!var5.hasNext()) {
            return false;
         }

         var6 = (VoxelShape)var5.next();
      } while(Shapes.joinIsNotEmpty(var6, var4, BooleanOp.AND));

      return true;
   }

   public void dismount(double var1, double var3, double var5, float var7, float var8) {
      this.teleport(var1, var3, var5, var7, var8, Collections.emptySet(), true);
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8) {
      this.teleport(var1, var3, var5, var7, var8, Collections.emptySet(), false);
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9) {
      this.teleport(var1, var3, var5, var7, var8, var9, false);
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9, boolean var10) {
      double var11 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.field_228) ? this.player.getX() : 0.0D;
      double var13 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.field_229) ? this.player.getY() : 0.0D;
      double var15 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.field_230) ? this.player.getZ() : 0.0D;
      float var17 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT) ? this.player.getYRot() : 0.0F;
      float var18 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT) ? this.player.getXRot() : 0.0F;
      this.awaitingPositionFromClient = new Vec3(var1, var3, var5);
      if (++this.awaitingTeleport == 2147483647) {
         this.awaitingTeleport = 0;
      }

      this.awaitingTeleportTime = this.tickCount;
      this.player.absMoveTo(var1, var3, var5, var7, var8);
      this.player.connection.send(new ClientboundPlayerPositionPacket(var1 - var11, var3 - var13, var5 - var15, var7 - var17, var8 - var18, var9, this.awaitingTeleport, var10));
   }

   public void handlePlayerAction(ServerboundPlayerActionPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
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
         this.player.gameMode.handleBlockBreakAction(var2, var3, var1.getDirection(), this.player.level.getMaxBuildHeight());
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

   public void handleUseItemOn(ServerboundUseItemOnPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      ServerLevel var2 = this.player.getLevel();
      InteractionHand var3 = var1.getHand();
      ItemStack var4 = this.player.getItemInHand(var3);
      BlockHitResult var5 = var1.getHitResult();
      BlockPos var6 = var5.getBlockPos();
      Direction var7 = var5.getDirection();
      this.player.resetLastActionTime();
      int var8 = this.player.level.getMaxBuildHeight();
      if (var6.getY() < var8) {
         if (this.awaitingPositionFromClient == null && this.player.distanceToSqr((double)var6.getX() + 0.5D, (double)var6.getY() + 0.5D, (double)var6.getZ() + 0.5D) < 64.0D && var2.mayInteract(this.player, var6)) {
            InteractionResult var9 = this.player.gameMode.useItemOn(this.player, var2, var4, var3, var5);
            if (var7 == Direction.field_526 && !var9.consumesAction() && var6.getY() >= var8 - 1 && wasBlockPlacementAttempt(this.player, var4)) {
               MutableComponent var10 = (new TranslatableComponent("build.tooHigh", new Object[]{var8 - 1})).withStyle(ChatFormatting.RED);
               this.player.sendMessage(var10, ChatType.GAME_INFO, Util.NIL_UUID);
            } else if (var9.shouldSwing()) {
               this.player.swing(var3, true);
            }
         }
      } else {
         MutableComponent var11 = (new TranslatableComponent("build.tooHigh", new Object[]{var8 - 1})).withStyle(ChatFormatting.RED);
         this.player.sendMessage(var11, ChatType.GAME_INFO, Util.NIL_UUID);
      }

      this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var6));
      this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var6.relative(var7)));
   }

   public void handleUseItem(ServerboundUseItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      ServerLevel var2 = this.player.getLevel();
      InteractionHand var3 = var1.getHand();
      ItemStack var4 = this.player.getItemInHand(var3);
      this.player.resetLastActionTime();
      if (!var4.isEmpty()) {
         InteractionResult var5 = this.player.gameMode.useItem(this.player, var2, var4, var3);
         if (var5.shouldSwing()) {
            this.player.swing(var3, true);
         }

      }
   }

   public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.isSpectator()) {
         Iterator var2 = this.server.getAllLevels().iterator();

         while(var2.hasNext()) {
            ServerLevel var3 = (ServerLevel)var2.next();
            Entity var4 = var1.getEntity(var3);
            if (var4 != null) {
               this.player.teleportTo(var3, var4.getX(), var4.getY(), var4.getZ(), var4.getYRot(), var4.getXRot());
               return;
            }
         }
      }

   }

   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (var1.getAction() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) {
         LOGGER.info("Disconnecting {} due to resource pack rejection", this.player.getName());
         this.disconnect(new TranslatableComponent("multiplayer.requiredTexturePrompt.disconnect"));
      }

   }

   public void handlePaddleBoat(ServerboundPaddleBoatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      Entity var2 = this.player.getVehicle();
      if (var2 instanceof Boat) {
         ((Boat)var2).setPaddleState(var1.getLeft(), var1.getRight());
      }

   }

   public void handlePong(ServerboundPongPacket var1) {
   }

   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), var1.getString());
      this.server.invalidateStatus();
      this.server.getPlayerList().broadcastMessage((new TranslatableComponent("multiplayer.player.left", new Object[]{this.player.getDisplayName()})).withStyle(ChatFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
      this.player.disconnect();
      this.server.getPlayerList().remove(this.player);
      this.player.getTextFilter().leave();
      if (this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.halt(false);
      }

   }

   public void send(Packet<?> var1) {
      this.send(var1, (GenericFutureListener)null);
   }

   public void send(Packet<?> var1, @Nullable GenericFutureListener<? extends Future<? super Void>> var2) {
      try {
         this.connection.send(var1, var2);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Sending packet");
         CrashReportCategory var5 = var4.addCategory("Packet being sent");
         var5.setDetail("Packet class", () -> {
            return var1.getClass().getCanonicalName();
         });
         throw new ReportedException(var4);
      }
   }

   public void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
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

   public void handleChat(ServerboundChatPacket var1) {
      String var2 = StringUtils.normalizeSpace(var1.getMessage());

      for(int var3 = 0; var3 < var2.length(); ++var3) {
         if (!SharedConstants.isAllowedChatCharacter(var2.charAt(var3))) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.illegal_characters"));
            return;
         }
      }

      if (var2.startsWith("/")) {
         PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
         this.handleChat(TextFilter.FilteredText.passThrough(var2));
      } else {
         this.filterTextPacket(var2, this::handleChat);
      }

   }

   private void handleChat(TextFilter.FilteredText var1) {
      if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
         this.send(new ClientboundChatPacket((new TranslatableComponent("chat.disabled.options")).withStyle(ChatFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID));
      } else {
         this.player.resetLastActionTime();
         String var2 = var1.getRaw();
         if (var2.startsWith("/")) {
            this.handleCommand(var2);
         } else {
            String var3 = var1.getFiltered();
            TranslatableComponent var4 = var3.isEmpty() ? null : new TranslatableComponent("chat.type.text", new Object[]{this.player.getDisplayName(), var3});
            TranslatableComponent var5 = new TranslatableComponent("chat.type.text", new Object[]{this.player.getDisplayName(), var2});
            this.server.getPlayerList().broadcastMessage(var5, (var3x) -> {
               return this.player.shouldFilterMessageTo(var3x) ? var4 : var5;
            }, ChatType.CHAT, this.player.getUUID());
         }

         this.chatSpamTickCount += 20;
         if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
            this.disconnect(new TranslatableComponent("disconnect.spam"));
         }

      }
   }

   private void handleCommand(String var1) {
      this.server.getCommands().performCommand(this.player.createCommandSourceStack(), var1);
   }

   public void handleAnimate(ServerboundSwingPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      this.player.swing(var1.getHand());
   }

   public void handlePlayerCommand(ServerboundPlayerCommandPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      PlayerRideableJumping var2;
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
         if (this.player.getVehicle() instanceof PlayerRideableJumping) {
            var2 = (PlayerRideableJumping)this.player.getVehicle();
            int var3 = var1.getData();
            if (var2.canJump() && var3 > 0) {
               var2.handleStartJump(var3);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if (this.player.getVehicle() instanceof PlayerRideableJumping) {
            var2 = (PlayerRideableJumping)this.player.getVehicle();
            var2.handleStopJump();
         }
         break;
      case OPEN_INVENTORY:
         if (this.player.getVehicle() instanceof AbstractHorse) {
            ((AbstractHorse)this.player.getVehicle()).openInventory(this.player);
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

   public void handleInteract(ServerboundInteractPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      ServerLevel var2 = this.player.getLevel();
      final Entity var3 = var1.getTarget(var2);
      this.player.resetLastActionTime();
      this.player.setShiftKeyDown(var1.isUsingSecondaryAction());
      if (var3 != null) {
         if (!var2.getWorldBorder().isWithinBounds(var3.blockPosition())) {
            return;
         }

         double var4 = 36.0D;
         if (this.player.distanceToSqr(var3) < 36.0D) {
            var1.dispatch(new ServerboundInteractPacket.Handler() {
               private void performInteraction(InteractionHand var1, ServerGamePacketListenerImpl.EntityInteraction var2) {
                  ItemStack var3x = ServerGamePacketListenerImpl.this.player.getItemInHand(var1).copy();
                  InteractionResult var4 = var2.run(ServerGamePacketListenerImpl.this.player, var3, var1);
                  if (var4.consumesAction()) {
                     CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerGamePacketListenerImpl.this.player, var3x, var3);
                     if (var4.shouldSwing()) {
                        ServerGamePacketListenerImpl.this.player.swing(var1, true);
                     }
                  }

               }

               public void onInteraction(InteractionHand var1) {
                  this.performInteraction(var1, Player::interactOn);
               }

               public void onInteraction(InteractionHand var1, Vec3 var2) {
                  this.performInteraction(var1, (var1x, var2x, var3x) -> {
                     return var2x.interactAt(var1x, var2, var3x);
                  });
               }

               public void onAttack() {
                  if (!(var3 instanceof ItemEntity) && !(var3 instanceof ExperienceOrb) && !(var3 instanceof AbstractArrow) && var3 != ServerGamePacketListenerImpl.this.player) {
                     ServerGamePacketListenerImpl.this.player.attack(var3);
                  } else {
                     ServerGamePacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_entity_attacked"));
                     ServerGamePacketListenerImpl.LOGGER.warn("Player {} tried to attack an invalid entity", ServerGamePacketListenerImpl.this.player.getName().getString());
                  }
               }
            });
         }
      }

   }

   public void handleClientCommand(ServerboundClientCommandPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
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
               ((GameRules.BooleanValue)this.player.getLevel().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS)).set(false, this.server);
            }
         }
         break;
      case REQUEST_STATS:
         this.player.getStats().sendStats(this.player);
      }

   }

   public void handleContainerClose(ServerboundContainerClosePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.doCloseContainer();
   }

   public void handleContainerClick(ServerboundContainerClickPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == var1.getContainerId()) {
         if (this.player.isSpectator()) {
            this.player.containerMenu.sendAllDataToRemote();
         } else {
            boolean var2 = var1.getStateId() != this.player.containerMenu.getStateId();
            this.player.containerMenu.suppressRemoteUpdates();
            this.player.containerMenu.clicked(var1.getSlotNum(), var1.getButtonNum(), var1.getClickType(), this.player);
            ObjectIterator var3 = Int2ObjectMaps.fastIterable(var1.getChangedSlots()).iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               this.player.containerMenu.setRemoteSlotNoCopy(var4.getIntKey(), (ItemStack)var4.getValue());
            }

            this.player.containerMenu.setRemoteCarried(var1.getCarriedItem());
            this.player.containerMenu.resumeRemoteUpdates();
            if (var2) {
               this.player.containerMenu.broadcastFullState();
            } else {
               this.player.containerMenu.broadcastChanges();
            }
         }
      }

   }

   public void handlePlaceRecipe(ServerboundPlaceRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if (!this.player.isSpectator() && this.player.containerMenu.containerId == var1.getContainerId() && this.player.containerMenu instanceof RecipeBookMenu) {
         this.server.getRecipeManager().byKey(var1.getRecipe()).ifPresent((var2) -> {
            ((RecipeBookMenu)this.player.containerMenu).handlePlacement(var1.isShiftDown(), var2, this.player);
         });
      }
   }

   public void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == var1.getContainerId() && !this.player.isSpectator()) {
         this.player.containerMenu.clickMenuButton(this.player, var1.getButtonId());
         this.player.containerMenu.broadcastChanges();
      }

   }

   public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.gameMode.isCreative()) {
         boolean var2 = var1.getSlotNum() < 0;
         ItemStack var3 = var1.getItem();
         CompoundTag var4 = BlockItem.getBlockEntityData(var3);
         if (!var3.isEmpty() && var4 != null && var4.contains("x") && var4.contains("y") && var4.contains("z")) {
            BlockPos var5 = BlockEntity.getPosFromTag(var4);
            BlockEntity var6 = this.player.level.getBlockEntity(var5);
            if (var6 != null) {
               var6.saveToItem(var3);
            }
         }

         boolean var7 = var1.getSlotNum() >= 1 && var1.getSlotNum() <= 45;
         boolean var8 = var3.isEmpty() || var3.getDamageValue() >= 0 && var3.getCount() <= 64 && !var3.isEmpty();
         if (var7 && var8) {
            this.player.inventoryMenu.getSlot(var1.getSlotNum()).set(var3);
            this.player.inventoryMenu.broadcastChanges();
         } else if (var2 && var8 && this.dropSpamTickCount < 200) {
            this.dropSpamTickCount += 20;
            this.player.drop(var3, true);
         }
      }

   }

   public void handleSignUpdate(ServerboundSignUpdatePacket var1) {
      List var2 = (List)Stream.of(var1.getLines()).map(ChatFormatting::stripFormatting).collect(Collectors.toList());
      this.filterTextPacket(var2, (var2x) -> {
         this.updateSignText(var1, var2x);
      });
   }

   private void updateSignText(ServerboundSignUpdatePacket var1, List<TextFilter.FilteredText> var2) {
      this.player.resetLastActionTime();
      ServerLevel var3 = this.player.getLevel();
      BlockPos var4 = var1.getPos();
      if (var3.hasChunkAt(var4)) {
         BlockState var5 = var3.getBlockState(var4);
         BlockEntity var6 = var3.getBlockEntity(var4);
         if (!(var6 instanceof SignBlockEntity)) {
            return;
         }

         SignBlockEntity var7 = (SignBlockEntity)var6;
         if (!var7.isEditable() || !this.player.getUUID().equals(var7.getPlayerWhoMayEdit())) {
            LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getName().getString());
            return;
         }

         for(int var8 = 0; var8 < var2.size(); ++var8) {
            TextFilter.FilteredText var9 = (TextFilter.FilteredText)var2.get(var8);
            if (this.player.isTextFilteringEnabled()) {
               var7.setMessage(var8, new TextComponent(var9.getFiltered()));
            } else {
               var7.setMessage(var8, new TextComponent(var9.getRaw()), new TextComponent(var9.getFiltered()));
            }
         }

         var7.setChanged();
         var3.sendBlockUpdated(var4, var5, var5, 3);
      }

   }

   public void handleKeepAlive(ServerboundKeepAlivePacket var1) {
      if (this.keepAlivePending && var1.getId() == this.keepAliveChallenge) {
         int var2 = (int)(Util.getMillis() - this.keepAliveTime);
         this.player.latency = (this.player.latency * 3 + var2) / 4;
         this.keepAlivePending = false;
      } else if (!this.isSingleplayerOwner()) {
         this.disconnect(new TranslatableComponent("disconnect.timeout"));
      }

   }

   public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.getAbilities().flying = var1.isFlying() && this.player.getAbilities().mayfly;
   }

   public void handleClientInformation(ServerboundClientInformationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.updateOptions(var1);
   }

   public void handleCustomPayload(ServerboundCustomPayloadPacket var1) {
   }

   public void handleChangeDifficulty(ServerboundChangeDifficultyPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficulty(var1.getDifficulty(), false);
      }
   }

   public void handleLockDifficulty(ServerboundLockDifficultyPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficultyLocked(var1.isLocked());
      }
   }

   public ServerPlayer getPlayer() {
      return this.player;
   }

   @FunctionalInterface
   private interface EntityInteraction {
      InteractionResult run(ServerPlayer var1, Entity var2, InteractionHand var3);
   }
}
