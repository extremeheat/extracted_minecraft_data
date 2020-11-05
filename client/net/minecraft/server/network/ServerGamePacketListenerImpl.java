package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
import net.minecraft.core.NonNullList;
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
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
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
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
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

public class ServerGamePacketListenerImpl implements ServerGamePacketListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public final Connection connection;
   private final MinecraftServer server;
   public ServerPlayer player;
   private int tickCount;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveChallenge;
   private int chatSpamTickCount;
   private int dropSpamTickCount;
   private final Int2ShortMap expectedAcks = new Int2ShortOpenHashMap();
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   private Entity lastVehicle;
   private double vehicleFirstGoodX;
   private double vehicleFirstGoodY;
   private double vehicleFirstGoodZ;
   private double vehicleLastGoodX;
   private double vehicleLastGoodY;
   private double vehicleLastGoodZ;
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
      TextFilter var4 = var3.getTextFilter();
      if (var4 != null) {
         var4.join();
      }

   }

   public void tick() {
      this.resetPosition();
      this.player.xo = this.player.getX();
      this.player.yo = this.player.getY();
      this.player.zo = this.player.getZ();
      this.player.doTick();
      this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.yRot, this.player.xRot);
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
      Connection var10001 = this.connection;
      this.server.executeBlocking(var10001::handleDisconnection);
   }

   private <T> void filterTextPacket(T var1, Consumer<T> var2, BiFunction<TextFilter, T, CompletableFuture<Optional<T>>> var3) {
      MinecraftServer var4 = this.player.getLevel().getServer();
      Consumer var5 = (var2x) -> {
         if (this.getConnection().isConnected()) {
            var2.accept(var2x);
         } else {
            LOGGER.debug("Ignoring packet due to disconnection");
         }

      };
      TextFilter var6 = this.player.getTextFilter();
      if (var6 != null) {
         ((CompletableFuture)var3.apply(var6, var1)).thenAcceptAsync((var1x) -> {
            var1x.ifPresent(var5);
         }, var4);
      } else {
         var4.execute(() -> {
            var5.accept(var1);
         });
      }

   }

   private void filterTextPacket(String var1, Consumer<String> var2) {
      this.filterTextPacket(var1, var2, TextFilter::processStreamMessage);
   }

   private void filterTextPacket(List<String> var1, Consumer<List<String>> var2) {
      this.filterTextPacket(var1, var2, TextFilter::processMessageBundle);
   }

   public void handlePlayerInput(ServerboundPlayerInputPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.setPlayerInput(var1.getXxa(), var1.getZza(), var1.isJumping(), var1.isShiftKeyDown());
   }

   private static boolean containsInvalidValues(ServerboundMovePlayerPacket var0) {
      if (Doubles.isFinite(var0.getX(0.0D)) && Doubles.isFinite(var0.getY(0.0D)) && Doubles.isFinite(var0.getZ(0.0D)) && Floats.isFinite(var0.getXRot(0.0F)) && Floats.isFinite(var0.getYRot(0.0F))) {
         return Math.abs(var0.getX(0.0D)) > 3.0E7D || Math.abs(var0.getY(0.0D)) > 3.0E7D || Math.abs(var0.getZ(0.0D)) > 3.0E7D;
      } else {
         return true;
      }
   }

   private static boolean containsInvalidValues(ServerboundMoveVehiclePacket var0) {
      return !Doubles.isFinite(var0.getX()) || !Doubles.isFinite(var0.getY()) || !Doubles.isFinite(var0.getZ()) || !Floats.isFinite(var0.getXRot()) || !Floats.isFinite(var0.getYRot());
   }

   public void handleMoveVehicle(ServerboundMoveVehiclePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (containsInvalidValues(var1)) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_vehicle_movement"));
      } else {
         Entity var2 = this.player.getRootVehicle();
         if (var2 != this.player && var2.getControllingPassenger() == this.player && var2 == this.lastVehicle) {
            ServerLevel var3 = this.player.getLevel();
            double var4 = var2.getX();
            double var6 = var2.getY();
            double var8 = var2.getZ();
            double var10 = var1.getX();
            double var12 = var1.getY();
            double var14 = var1.getZ();
            float var16 = var1.getYRot();
            float var17 = var1.getXRot();
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
         this.player.absMoveTo(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
         this.lastGoodX = this.awaitingPositionFromClient.x;
         this.lastGoodY = this.awaitingPositionFromClient.y;
         this.lastGoodZ = this.awaitingPositionFromClient.z;
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
            Direction var9 = (Direction)this.player.level.getBlockState(var4).getValue(CommandBlock.FACING);
            BlockState var10;
            switch(var1.getMode()) {
            case SEQUENCE:
               var10 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(var4, (BlockState)((BlockState)var10.setValue(CommandBlock.FACING, var9)).setValue(CommandBlock.CONDITIONAL, var1.isConditional()), 2);
               break;
            case AUTO:
               var10 = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(var4, (BlockState)((BlockState)var10.setValue(CommandBlock.FACING, var9)).setValue(CommandBlock.CONDITIONAL, var1.isConditional()), 2);
               break;
            case REDSTONE:
            default:
               var10 = Blocks.COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(var4, (BlockState)((BlockState)var10.setValue(CommandBlock.FACING, var9)).setValue(CommandBlock.CONDITIONAL, var1.isConditional()), 2);
            }

            var5.clearRemoved();
            this.player.level.setBlockEntity(var4, var5);
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
      this.player.inventory.pickSlot(var1.getSlot());
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, this.player.inventory.selected, this.player.inventory.getItem(this.player.inventory.selected)));
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, var1.getSlot(), this.player.inventory.getItem(var1.getSlot())));
      this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.inventory.selected));
   }

   public void handleRenameItem(ServerboundRenameItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.containerMenu instanceof AnvilMenu) {
         AnvilMenu var2 = (AnvilMenu)this.player.containerMenu;
         String var3 = SharedConstants.filterText(var1.getName());
         if (var3.length() <= 35) {
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
      ItemStack var2 = var1.getBook();
      if (var2.getItem() == Items.WRITABLE_BOOK) {
         CompoundTag var3 = var2.getTag();
         if (WritableBookItem.makeSureTagIsValid(var3)) {
            ArrayList var4 = Lists.newArrayList();
            boolean var5 = var1.isSigning();
            if (var5) {
               var4.add(var3.getString("title"));
            }

            ListTag var6 = var3.getList("pages", 8);

            int var7;
            for(var7 = 0; var7 < var6.size(); ++var7) {
               var4.add(var6.getString(var7));
            }

            var7 = var1.getSlot();
            if (Inventory.isHotbarSlot(var7) || var7 == 40) {
               this.filterTextPacket((List)var4, var5 ? (var2x) -> {
                  this.signBook((String)var2x.get(0), var2x.subList(1, var2x.size()), var7);
               } : (var2x) -> {
                  this.updateBookContents(var2x, var7);
               });
            }
         }
      }
   }

   private void updateBookContents(List<String> var1, int var2) {
      ItemStack var3 = this.player.inventory.getItem(var2);
      if (var3.getItem() == Items.WRITABLE_BOOK) {
         ListTag var4 = new ListTag();
         var1.stream().map(StringTag::valueOf).forEach(var4::add);
         var3.addTagElement("pages", var4);
      }
   }

   private void signBook(String var1, List<String> var2, int var3) {
      ItemStack var4 = this.player.inventory.getItem(var3);
      if (var4.getItem() == Items.WRITABLE_BOOK) {
         ItemStack var5 = new ItemStack(Items.WRITTEN_BOOK);
         CompoundTag var6 = var4.getTag();
         if (var6 != null) {
            var5.setTag(var6.copy());
         }

         var5.addTagElement("author", StringTag.valueOf(this.player.getName().getString()));
         var5.addTagElement("title", StringTag.valueOf(var1));
         ListTag var7 = new ListTag();
         Iterator var8 = var2.iterator();

         while(var8.hasNext()) {
            String var9 = (String)var8.next();
            TextComponent var10 = new TextComponent(var9);
            String var11 = Component.Serializer.toJson(var10);
            var7.add(StringTag.valueOf(var11));
         }

         var5.addTagElement("pages", var7);
         this.player.inventory.setItem(var3, var5);
      }
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
         CompoundTag var3 = var2 != null ? var2.save(new CompoundTag()) : null;
         this.player.connection.send(new ClientboundTagQueryPacket(var1.getTransactionId(), var3));
      }
   }

   public void handleMovePlayer(ServerboundMovePlayerPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (containsInvalidValues(var1)) {
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
                  this.teleport(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
               }

            } else {
               this.awaitingTeleportTime = this.tickCount;
               if (this.player.isPassenger()) {
                  this.player.absMoveTo(this.player.getX(), this.player.getY(), this.player.getZ(), var1.getYRot(this.player.yRot), var1.getXRot(this.player.xRot));
                  this.player.getLevel().getChunkSource().move(this.player);
               } else {
                  double var3 = this.player.getX();
                  double var5 = this.player.getY();
                  double var7 = this.player.getZ();
                  double var9 = this.player.getY();
                  double var11 = var1.getX(this.player.getX());
                  double var13 = var1.getY(this.player.getY());
                  double var15 = var1.getZ(this.player.getZ());
                  float var17 = var1.getYRot(this.player.yRot);
                  float var18 = var1.getXRot(this.player.xRot);
                  double var19 = var11 - this.firstGoodX;
                  double var21 = var13 - this.firstGoodY;
                  double var23 = var15 - this.firstGoodZ;
                  double var25 = this.player.getDeltaMovement().lengthSqr();
                  double var27 = var19 * var19 + var21 * var21 + var23 * var23;
                  if (this.player.isSleeping()) {
                     if (var27 > 1.0D) {
                        this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), var1.getYRot(this.player.yRot), var1.getXRot(this.player.xRot));
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
                           this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.yRot, this.player.xRot);
                           return;
                        }
                     }

                     AABB var35 = this.player.getBoundingBox();
                     var19 = var11 - this.lastGoodX;
                     var21 = var13 - this.lastGoodY;
                     var23 = var15 - this.lastGoodZ;
                     boolean var31 = var21 > 0.0D;
                     if (this.player.isOnGround() && !var1.isOnGround() && var31) {
                        this.player.jumpFromGround();
                     }

                     this.player.move(MoverType.PLAYER, new Vec3(var19, var21, var23));
                     double var32 = var21;
                     var19 = var11 - this.player.getX();
                     var21 = var13 - this.player.getY();
                     if (var21 > -0.5D || var21 < 0.5D) {
                        var21 = 0.0D;
                     }

                     var23 = var15 - this.player.getZ();
                     var27 = var19 * var19 + var21 * var21 + var23 * var23;
                     boolean var34 = false;
                     if (!this.player.isChangingDimension() && var27 > 0.0625D && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                        var34 = true;
                        LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                     }

                     this.player.absMoveTo(var11, var13, var15, var17, var18);
                     if (this.player.noPhysics || this.player.isSleeping() || (!var34 || !var2.noCollision(this.player, var35)) && !this.isPlayerCollidingWithAnythingNew(var2, var35)) {
                        this.clientIsFloating = var32 >= -0.03125D && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.abilities.mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isFallFlying() && this.noBlocksAround(this.player);
                        this.player.getLevel().getChunkSource().move(this.player);
                        this.player.doCheckFallDamage(this.player.getY() - var9, var1.isOnGround());
                        this.player.setOnGround(var1.isOnGround());
                        if (var31) {
                           this.player.fallDistance = 0.0F;
                        }

                        this.player.checkMovementStatistics(this.player.getX() - var3, this.player.getY() - var5, this.player.getZ() - var7);
                        this.lastGoodX = this.player.getX();
                        this.lastGoodY = this.player.getY();
                        this.lastGoodZ = this.player.getZ();
                     } else {
                        this.teleport(var3, var5, var7, var17, var18);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPlayerCollidingWithAnythingNew(LevelReader var1, AABB var2) {
      Stream var3 = var1.getCollisions(this.player, this.player.getBoundingBox().deflate(9.999999747378752E-6D), (var0) -> {
         return true;
      });
      VoxelShape var4 = Shapes.create(var2.deflate(9.999999747378752E-6D));
      return var3.anyMatch((var1x) -> {
         return !Shapes.joinIsNotEmpty(var1x, var4, BooleanOp.AND);
      });
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8) {
      this.teleport(var1, var3, var5, var7, var8, Collections.emptySet());
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9) {
      double var10 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.X) ? this.player.getX() : 0.0D;
      double var12 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y) ? this.player.getY() : 0.0D;
      double var14 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.Z) ? this.player.getZ() : 0.0D;
      float var16 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT) ? this.player.yRot : 0.0F;
      float var17 = var9.contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT) ? this.player.xRot : 0.0F;
      this.awaitingPositionFromClient = new Vec3(var1, var3, var5);
      if (++this.awaitingTeleport == 2147483647) {
         this.awaitingTeleport = 0;
      }

      this.awaitingTeleportTime = this.tickCount;
      this.player.absMoveTo(var1, var3, var5, var7, var8);
      this.player.connection.send(new ClientboundPlayerPositionPacket(var1 - var10, var3 - var12, var5 - var14, var7 - var16, var8 - var17, var9, this.awaitingTeleport));
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
         this.player.gameMode.handleBlockBreakAction(var2, var3, var1.getDirection(), this.server.getMaxBuildHeight());
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
      if (var6.getY() < this.server.getMaxBuildHeight()) {
         if (this.awaitingPositionFromClient == null && this.player.distanceToSqr((double)var6.getX() + 0.5D, (double)var6.getY() + 0.5D, (double)var6.getZ() + 0.5D) < 64.0D && var2.mayInteract(this.player, var6)) {
            InteractionResult var8 = this.player.gameMode.useItemOn(this.player, var2, var4, var3, var5);
            if (var7 == Direction.UP && !var8.consumesAction() && var6.getY() >= this.server.getMaxBuildHeight() - 1 && wasBlockPlacementAttempt(this.player, var4)) {
               MutableComponent var9 = (new TranslatableComponent("build.tooHigh", new Object[]{this.server.getMaxBuildHeight()})).withStyle(ChatFormatting.RED);
               this.player.connection.send(new ClientboundChatPacket(var9, ChatType.GAME_INFO, Util.NIL_UUID));
            } else if (var8.shouldSwing()) {
               this.player.swing(var3, true);
            }
         }
      } else {
         MutableComponent var10 = (new TranslatableComponent("build.tooHigh", new Object[]{this.server.getMaxBuildHeight()})).withStyle(ChatFormatting.RED);
         this.player.connection.send(new ClientboundChatPacket(var10, ChatType.GAME_INFO, Util.NIL_UUID));
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
               this.player.teleportTo(var3, var4.getX(), var4.getY(), var4.getZ(), var4.yRot, var4.xRot);
               return;
            }
         }
      }

   }

   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
   }

   public void handlePaddleBoat(ServerboundPaddleBoatPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      Entity var2 = this.player.getVehicle();
      if (var2 instanceof Boat) {
         ((Boat)var2).setPaddleState(var1.getLeft(), var1.getRight());
      }

   }

   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), var1.getString());
      this.server.invalidateStatus();
      this.server.getPlayerList().broadcastMessage((new TranslatableComponent("multiplayer.player.left", new Object[]{this.player.getDisplayName()})).withStyle(ChatFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
      this.player.disconnect();
      this.server.getPlayerList().remove(this.player);
      TextFilter var2 = this.player.getTextFilter();
      if (var2 != null) {
         var2.leave();
      }

      if (this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.halt(false);
      }

   }

   public void send(Packet<?> var1) {
      this.send(var1, (GenericFutureListener)null);
   }

   public void send(Packet<?> var1, @Nullable GenericFutureListener<? extends Future<? super Void>> var2) {
      if (var1 instanceof ClientboundChatPacket) {
         ClientboundChatPacket var3 = (ClientboundChatPacket)var1;
         ChatVisiblity var4 = this.player.getChatVisibility();
         if (var4 == ChatVisiblity.HIDDEN && var3.getType() != ChatType.GAME_INFO) {
            return;
         }

         if (var4 == ChatVisiblity.SYSTEM && !var3.isSystem()) {
            return;
         }
      }

      try {
         this.connection.send(var1, var2);
      } catch (Throwable var6) {
         CrashReport var7 = CrashReport.forThrowable(var6, "Sending packet");
         CrashReportCategory var5 = var7.addCategory("Packet being sent");
         var5.setDetail("Packet class", () -> {
            return var1.getClass().getCanonicalName();
         });
         throw new ReportedException(var7);
      }
   }

   public void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (var1.getSlot() >= 0 && var1.getSlot() < Inventory.getSelectionSize()) {
         if (this.player.inventory.selected != var1.getSlot() && this.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            this.player.stopUsingItem();
         }

         this.player.inventory.selected = var1.getSlot();
         this.player.resetLastActionTime();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
      }
   }

   public void handleChat(ServerboundChatPacket var1) {
      String var2 = StringUtils.normalizeSpace(var1.getMessage());
      if (var2.startsWith("/")) {
         PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
         this.handleChat(var2);
      } else {
         this.filterTextPacket(var2, this::handleChat);
      }

   }

   private void handleChat(String var1) {
      if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
         this.send(new ClientboundChatPacket((new TranslatableComponent("chat.cannotSend")).withStyle(ChatFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID));
      } else {
         this.player.resetLastActionTime();

         for(int var2 = 0; var2 < var1.length(); ++var2) {
            if (!SharedConstants.isAllowedChatCharacter(var1.charAt(var2))) {
               this.disconnect(new TranslatableComponent("multiplayer.disconnect.illegal_characters"));
               return;
            }
         }

         if (var1.startsWith("/")) {
            this.handleCommand(var1);
         } else {
            TranslatableComponent var3 = new TranslatableComponent("chat.type.text", new Object[]{this.player.getDisplayName(), var1});
            this.server.getPlayerList().broadcastMessage(var3, ChatType.CHAT, this.player.getUUID());
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
      Entity var3 = var1.getTarget(var2);
      this.player.resetLastActionTime();
      this.player.setShiftKeyDown(var1.isUsingSecondaryAction());
      if (var3 != null) {
         double var4 = 36.0D;
         if (this.player.distanceToSqr(var3) < 36.0D) {
            InteractionHand var6 = var1.getHand();
            ItemStack var7 = var6 != null ? this.player.getItemInHand(var6).copy() : ItemStack.EMPTY;
            Optional var8 = Optional.empty();
            if (var1.getAction() == ServerboundInteractPacket.Action.INTERACT) {
               var8 = Optional.of(this.player.interactOn(var3, var6));
            } else if (var1.getAction() == ServerboundInteractPacket.Action.INTERACT_AT) {
               var8 = Optional.of(var3.interactAt(this.player, var1.getLocation(), var6));
            } else if (var1.getAction() == ServerboundInteractPacket.Action.ATTACK) {
               if (var3 instanceof ItemEntity || var3 instanceof ExperienceOrb || var3 instanceof AbstractArrow || var3 == this.player) {
                  this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_entity_attacked"));
                  LOGGER.warn("Player {} tried to attack an invalid entity", this.player.getName().getString());
                  return;
               }

               this.player.attack(var3);
            }

            if (var8.isPresent() && ((InteractionResult)var8.get()).consumesAction()) {
               CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(this.player, var7, var3);
               if (((InteractionResult)var8.get()).shouldSwing()) {
                  this.player.swing(var6, true);
               }
            }
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
      if (this.player.containerMenu.containerId == var1.getContainerId() && this.player.containerMenu.isSynched(this.player)) {
         if (this.player.isSpectator()) {
            NonNullList var2 = NonNullList.create();

            for(int var3 = 0; var3 < this.player.containerMenu.slots.size(); ++var3) {
               var2.add(((Slot)this.player.containerMenu.slots.get(var3)).getItem());
            }

            this.player.refreshContainer(this.player.containerMenu, var2);
         } else {
            ItemStack var6 = this.player.containerMenu.clicked(var1.getSlotNum(), var1.getButtonNum(), var1.getClickType(), this.player);
            if (ItemStack.matches(var1.getItem(), var6)) {
               this.player.connection.send(new ClientboundContainerAckPacket(var1.getContainerId(), var1.getUid(), true));
               this.player.ignoreSlotUpdateHack = true;
               this.player.containerMenu.broadcastChanges();
               this.player.broadcastCarriedItem();
               this.player.ignoreSlotUpdateHack = false;
            } else {
               this.expectedAcks.put(this.player.containerMenu.containerId, var1.getUid());
               this.player.connection.send(new ClientboundContainerAckPacket(var1.getContainerId(), var1.getUid(), false));
               this.player.containerMenu.setSynched(this.player, false);
               NonNullList var7 = NonNullList.create();

               for(int var4 = 0; var4 < this.player.containerMenu.slots.size(); ++var4) {
                  ItemStack var5 = ((Slot)this.player.containerMenu.slots.get(var4)).getItem();
                  var7.add(var5.isEmpty() ? ItemStack.EMPTY : var5);
               }

               this.player.refreshContainer(this.player.containerMenu, var7);
            }
         }
      }

   }

   public void handlePlaceRecipe(ServerboundPlaceRecipePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if (!this.player.isSpectator() && this.player.containerMenu.containerId == var1.getContainerId() && this.player.containerMenu.isSynched(this.player) && this.player.containerMenu instanceof RecipeBookMenu) {
         this.server.getRecipeManager().byKey(var1.getRecipe()).ifPresent((var2) -> {
            ((RecipeBookMenu)this.player.containerMenu).handlePlacement(var1.isShiftDown(), var2, this.player);
         });
      }
   }

   public void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if (this.player.containerMenu.containerId == var1.getContainerId() && this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
         this.player.containerMenu.clickMenuButton(this.player, var1.getButtonId());
         this.player.containerMenu.broadcastChanges();
      }

   }

   public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      if (this.player.gameMode.isCreative()) {
         boolean var2 = var1.getSlotNum() < 0;
         ItemStack var3 = var1.getItem();
         CompoundTag var4 = var3.getTagElement("BlockEntityTag");
         if (!var3.isEmpty() && var4 != null && var4.contains("x") && var4.contains("y") && var4.contains("z")) {
            BlockPos var5 = new BlockPos(var4.getInt("x"), var4.getInt("y"), var4.getInt("z"));
            BlockEntity var6 = this.player.level.getBlockEntity(var5);
            if (var6 != null) {
               CompoundTag var7 = var6.save(new CompoundTag());
               var7.remove("x");
               var7.remove("y");
               var7.remove("z");
               var3.addTagElement("BlockEntityTag", var7);
            }
         }

         boolean var8 = var1.getSlotNum() >= 1 && var1.getSlotNum() <= 45;
         boolean var9 = var3.isEmpty() || var3.getDamageValue() >= 0 && var3.getCount() <= 64 && !var3.isEmpty();
         if (var8 && var9) {
            if (var3.isEmpty()) {
               this.player.inventoryMenu.setItem(var1.getSlotNum(), ItemStack.EMPTY);
            } else {
               this.player.inventoryMenu.setItem(var1.getSlotNum(), var3);
            }

            this.player.inventoryMenu.setSynched(this.player, true);
            this.player.inventoryMenu.broadcastChanges();
         } else if (var2 && var9 && this.dropSpamTickCount < 200) {
            this.dropSpamTickCount += 20;
            this.player.drop(var3, true);
         }
      }

   }

   public void handleContainerAck(ServerboundContainerAckPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (ServerLevel)this.player.getLevel());
      int var2 = this.player.containerMenu.containerId;
      if (var2 == var1.getContainerId() && this.expectedAcks.getOrDefault(var2, (short)(var1.getUid() + 1)) == var1.getUid() && !this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
         this.player.containerMenu.setSynched(this.player, true);
      }

   }

   public void handleSignUpdate(ServerboundSignUpdatePacket var1) {
      List var2 = (List)Stream.of(var1.getLines()).map(ChatFormatting::stripFormatting).collect(Collectors.toList());
      this.filterTextPacket(var2, (var2x) -> {
         this.updateSignText(var1, var2x);
      });
   }

   private void updateSignText(ServerboundSignUpdatePacket var1, List<String> var2) {
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
         if (!var7.isEditable() || var7.getPlayerWhoMayEdit() != this.player) {
            LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getName().getString());
            return;
         }

         for(int var8 = 0; var8 < var2.size(); ++var8) {
            var7.setMessage(var8, new TextComponent((String)var2.get(var8)));
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
      this.player.abilities.flying = var1.isFlying() && this.player.abilities.mayfly;
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
}
