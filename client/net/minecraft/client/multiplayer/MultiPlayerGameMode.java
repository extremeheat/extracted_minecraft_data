package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class MultiPlayerGameMode {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final ClientPacketListener connection;
   private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
   private ItemStack destroyingItem = ItemStack.EMPTY;
   private float destroyProgress;
   private float destroyTicks;
   private int destroyDelay;
   private boolean isDestroying;
   private GameType localPlayerMode = GameType.DEFAULT_MODE;
   @Nullable
   private GameType previousLocalPlayerMode;
   private int carriedIndex;

   public MultiPlayerGameMode(Minecraft var1, ClientPacketListener var2) {
      super();
      this.minecraft = var1;
      this.connection = var2;
   }

   public void adjustPlayer(Player var1) {
      this.localPlayerMode.updatePlayerAbilities(var1.getAbilities());
   }

   public void setLocalMode(GameType var1, @Nullable GameType var2) {
      this.localPlayerMode = var1;
      this.previousLocalPlayerMode = var2;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.getAbilities());
   }

   public void setLocalMode(GameType var1) {
      if (var1 != this.localPlayerMode) {
         this.previousLocalPlayerMode = this.localPlayerMode;
      }

      this.localPlayerMode = var1;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.getAbilities());
   }

   public boolean canHurtPlayer() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean destroyBlock(BlockPos var1) {
      if (this.minecraft.player.blockActionRestricted(this.minecraft.level, var1, this.localPlayerMode)) {
         return false;
      } else {
         ClientLevel var2 = this.minecraft.level;
         BlockState var3 = var2.getBlockState(var1);
         if (!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(var3, var2, var1, this.minecraft.player)) {
            return false;
         } else {
            Block var4 = var3.getBlock();
            if (var4 instanceof GameMasterBlock && !this.minecraft.player.canUseGameMasterBlocks()) {
               return false;
            } else if (var3.isAir()) {
               return false;
            } else {
               var4.playerWillDestroy(var2, var1, var3, this.minecraft.player);
               FluidState var5 = var2.getFluidState(var1);
               boolean var6 = var2.setBlock(var1, var5.createLegacyBlock(), 11);
               if (var6) {
                  var4.destroy(var2, var1, var3);
               }

               return var6;
            }
         }
      }
   }

   public boolean startDestroyBlock(BlockPos var1, Direction var2) {
      if (this.minecraft.player.blockActionRestricted(this.minecraft.level, var1, this.localPlayerMode)) {
         return false;
      } else if (!this.minecraft.level.getWorldBorder().isWithinBounds(var1)) {
         return false;
      } else {
         if (this.localPlayerMode.isCreative()) {
            BlockState var3 = this.minecraft.level.getBlockState(var1);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var3, 1.0F);
            this.startPrediction(this.minecraft.level, var3x -> {
               this.destroyBlock(var1);
               return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, var1, var2, var3x);
            });
            this.destroyDelay = 5;
         } else if (!this.isDestroying || !this.sameDestroyTarget(var1)) {
            if (this.isDestroying) {
               this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, var2));
            }

            BlockState var4 = this.minecraft.level.getBlockState(var1);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var4, 0.0F);
            this.startPrediction(this.minecraft.level, var4x -> {
               boolean var5 = !var4.isAir();
               if (var5 && this.destroyProgress == 0.0F) {
                  var4.attack(this.minecraft.level, var1, this.minecraft.player);
               }

               if (var5 && var4.getDestroyProgress(this.minecraft.player, this.minecraft.player.level(), var1) >= 1.0F) {
                  this.destroyBlock(var1);
               } else {
                  this.isDestroying = true;
                  this.destroyBlockPos = var1;
                  this.destroyingItem = this.minecraft.player.getMainHandItem();
                  this.destroyProgress = 0.0F;
                  this.destroyTicks = 0.0F;
                  this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
               }

               return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, var1, var2, var4x);
            });
         }

         return true;
      }
   }

   public void stopDestroyBlock() {
      if (this.isDestroying) {
         BlockState var1 = this.minecraft.level.getBlockState(this.destroyBlockPos);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, var1, -1.0F);
         this.connection
            .send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN));
         this.isDestroying = false;
         this.destroyProgress = 0.0F;
         this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
         this.minecraft.player.resetAttackStrengthTicker();
      }
   }

   public boolean continueDestroyBlock(BlockPos var1, Direction var2) {
      this.ensureHasSentCarriedItem();
      if (this.destroyDelay > 0) {
         this.destroyDelay--;
         return true;
      } else if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(var1)) {
         this.destroyDelay = 5;
         BlockState var5 = this.minecraft.level.getBlockState(var1);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var5, 1.0F);
         this.startPrediction(this.minecraft.level, var3x -> {
            this.destroyBlock(var1);
            return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, var1, var2, var3x);
         });
         return true;
      } else if (this.sameDestroyTarget(var1)) {
         BlockState var3 = this.minecraft.level.getBlockState(var1);
         if (var3.isAir()) {
            this.isDestroying = false;
            return false;
         } else {
            this.destroyProgress = this.destroyProgress + var3.getDestroyProgress(this.minecraft.player, this.minecraft.player.level(), var1);
            if (this.destroyTicks % 4.0F == 0.0F) {
               SoundType var4 = var3.getSoundType();
               this.minecraft
                  .getSoundManager()
                  .play(
                     new SimpleSoundInstance(
                        var4.getHitSound(),
                        SoundSource.BLOCKS,
                        (var4.getVolume() + 1.0F) / 8.0F,
                        var4.getPitch() * 0.5F,
                        SoundInstance.createUnseededRandom(),
                        var1
                     )
                  );
            }

            this.destroyTicks++;
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var3, Mth.clamp(this.destroyProgress, 0.0F, 1.0F));
            if (this.destroyProgress >= 1.0F) {
               this.isDestroying = false;
               this.startPrediction(this.minecraft.level, var3x -> {
                  this.destroyBlock(var1);
                  return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, var1, var2, var3x);
               });
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.destroyDelay = 5;
            }

            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
            return true;
         }
      } else {
         return this.startDestroyBlock(var1, var2);
      }
   }

   private void startPrediction(ClientLevel var1, PredictiveAction var2) {
      try (BlockStatePredictionHandler var3 = var1.getBlockStatePredictionHandler().startPredicting()) {
         int var4 = var3.currentSequence();
         Packet var5 = var2.predict(var4);
         this.connection.send(var5);
      }
   }

   public void tick() {
      this.ensureHasSentCarriedItem();
      if (this.connection.getConnection().isConnected()) {
         this.connection.getConnection().tick();
      } else {
         this.connection.getConnection().handleDisconnection();
      }
   }

   private boolean sameDestroyTarget(BlockPos var1) {
      ItemStack var2 = this.minecraft.player.getMainHandItem();
      return var1.equals(this.destroyBlockPos) && ItemStack.isSameItemSameComponents(var2, this.destroyingItem);
   }

   private void ensureHasSentCarriedItem() {
      int var1 = this.minecraft.player.getInventory().selected;
      if (var1 != this.carriedIndex) {
         this.carriedIndex = var1;
         this.connection.send(new ServerboundSetCarriedItemPacket(this.carriedIndex));
      }
   }

   public InteractionResult useItemOn(LocalPlayer var1, InteractionHand var2, BlockHitResult var3) {
      this.ensureHasSentCarriedItem();
      if (!this.minecraft.level.getWorldBorder().isWithinBounds(var3.getBlockPos())) {
         return InteractionResult.FAIL;
      } else {
         MutableObject var4 = new MutableObject();
         this.startPrediction(this.minecraft.level, var5 -> {
            var4.setValue(this.performUseItemOn(var1, var2, var3));
            return new ServerboundUseItemOnPacket(var2, var3, var5);
         });
         return (InteractionResult)var4.getValue();
      }
   }

   private InteractionResult performUseItemOn(LocalPlayer var1, InteractionHand var2, BlockHitResult var3) {
      BlockPos var4 = var3.getBlockPos();
      ItemStack var5 = var1.getItemInHand(var2);
      if (this.localPlayerMode == GameType.SPECTATOR) {
         return InteractionResult.CONSUME;
      } else {
         boolean var6 = !var1.getMainHandItem().isEmpty() || !var1.getOffhandItem().isEmpty();
         boolean var7 = var1.isSecondaryUseActive() && var6;
         if (!var7) {
            BlockState var8 = this.minecraft.level.getBlockState(var4);
            if (!this.connection.isFeatureEnabled(var8.getBlock().requiredFeatures())) {
               return InteractionResult.FAIL;
            }

            InteractionResult var9 = var8.useItemOn(var1.getItemInHand(var2), this.minecraft.level, var1, var2, var3);
            if (var9.consumesAction()) {
               return var9;
            }

            if (var9 instanceof InteractionResult.TryEmptyHandInteraction && var2 == InteractionHand.MAIN_HAND) {
               InteractionResult var10 = var8.useWithoutItem(this.minecraft.level, var1, var3);
               if (var10.consumesAction()) {
                  return var10;
               }
            }
         }

         if (!var5.isEmpty() && !var1.getCooldowns().isOnCooldown(var5)) {
            UseOnContext var12 = new UseOnContext(var1, var2, var3);
            InteractionResult var11;
            if (this.localPlayerMode.isCreative()) {
               int var13 = var5.getCount();
               var11 = var5.useOn(var12);
               var5.setCount(var13);
            } else {
               var11 = var5.useOn(var12);
            }

            return var11;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public InteractionResult useItem(Player var1, InteractionHand var2) {
      if (this.localPlayerMode == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else {
         this.ensureHasSentCarriedItem();
         MutableObject var3 = new MutableObject();
         this.startPrediction(this.minecraft.level, var4 -> {
            ServerboundUseItemPacket var5 = new ServerboundUseItemPacket(var2, var4, var1.getYRot(), var1.getXRot());
            ItemStack var6 = var1.getItemInHand(var2);
            if (var1.getCooldowns().isOnCooldown(var6)) {
               var3.setValue(InteractionResult.PASS);
               return var5;
            } else {
               InteractionResult var7 = var6.use(this.minecraft.level, var1, var2);
               ItemStack var8;
               if (var7 instanceof InteractionResult.Success var9) {
                  var8 = Objects.requireNonNullElseGet(var9.heldItemTransformedTo(), () -> var1.getItemInHand(var2));
               } else {
                  var8 = var1.getItemInHand(var2);
               }

               if (var8 != var6) {
                  var1.setItemInHand(var2, var8);
               }

               var3.setValue(var7);
               return var5;
            }
         });
         return (InteractionResult)var3.getValue();
      }
   }

   public LocalPlayer createPlayer(ClientLevel var1, StatsCounter var2, ClientRecipeBook var3) {
      return this.createPlayer(var1, var2, var3, false, false);
   }

   public LocalPlayer createPlayer(ClientLevel var1, StatsCounter var2, ClientRecipeBook var3, boolean var4, boolean var5) {
      return new LocalPlayer(this.minecraft, var1, this.connection, var2, var3, var4, var5);
   }

   public void attack(Player var1, Entity var2) {
      this.ensureHasSentCarriedItem();
      this.connection.send(ServerboundInteractPacket.createAttackPacket(var2, var1.isShiftKeyDown()));
      if (this.localPlayerMode != GameType.SPECTATOR) {
         var1.attack(var2);
         var1.resetAttackStrengthTicker();
      }
   }

   public InteractionResult interact(Player var1, Entity var2, InteractionHand var3) {
      this.ensureHasSentCarriedItem();
      this.connection.send(ServerboundInteractPacket.createInteractionPacket(var2, var1.isShiftKeyDown(), var3));
      return (InteractionResult)(this.localPlayerMode == GameType.SPECTATOR ? InteractionResult.PASS : var1.interactOn(var2, var3));
   }

   public InteractionResult interactAt(Player var1, Entity var2, EntityHitResult var3, InteractionHand var4) {
      this.ensureHasSentCarriedItem();
      Vec3 var5 = var3.getLocation().subtract(var2.getX(), var2.getY(), var2.getZ());
      this.connection.send(ServerboundInteractPacket.createInteractionPacket(var2, var1.isShiftKeyDown(), var4, var5));
      return (InteractionResult)(this.localPlayerMode == GameType.SPECTATOR ? InteractionResult.PASS : var2.interactAt(var1, var5, var4));
   }

   public void handleInventoryMouseClick(int var1, int var2, int var3, ClickType var4, Player var5) {
      AbstractContainerMenu var6 = var5.containerMenu;
      if (var1 != var6.containerId) {
         LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", var1, var6.containerId);
      } else {
         NonNullList var7 = var6.slots;
         int var8 = var7.size();
         ArrayList var9 = Lists.newArrayListWithCapacity(var8);

         for (Slot var11 : var7) {
            var9.add(var11.getItem().copy());
         }

         var6.clicked(var2, var3, var4, var5);
         Int2ObjectOpenHashMap var14 = new Int2ObjectOpenHashMap();

         for (int var15 = 0; var15 < var8; var15++) {
            ItemStack var12 = (ItemStack)var9.get(var15);
            ItemStack var13 = ((Slot)var7.get(var15)).getItem();
            if (!ItemStack.matches(var12, var13)) {
               var14.put(var15, var13.copy());
            }
         }

         this.connection.send(new ServerboundContainerClickPacket(var1, var6.getStateId(), var2, var3, var4, var6.getCarried().copy(), var14));
      }
   }

   public void handlePlaceRecipe(int var1, RecipeHolder<?> var2, boolean var3) {
      this.connection.send(new ServerboundPlaceRecipePacket(var1, var2, var3));
   }

   public void handleInventoryButtonClick(int var1, int var2) {
      this.connection.send(new ServerboundContainerButtonClickPacket(var1, var2));
   }

   public void handleCreativeModeItemAdd(ItemStack var1, int var2) {
      if (this.localPlayerMode.isCreative() && this.connection.isFeatureEnabled(var1.getItem().requiredFeatures())) {
         this.connection.send(new ServerboundSetCreativeModeSlotPacket(var2, var1));
      }
   }

   public void handleCreativeModeItemDrop(ItemStack var1) {
      boolean var2 = this.minecraft.screen instanceof AbstractContainerScreen && !(this.minecraft.screen instanceof CreativeModeInventoryScreen);
      if (this.localPlayerMode.isCreative() && !var2 && !var1.isEmpty() && this.connection.isFeatureEnabled(var1.getItem().requiredFeatures())) {
         this.connection.send(new ServerboundSetCreativeModeSlotPacket(-1, var1));
      }
   }

   public void releaseUsingItem(Player var1) {
      this.ensureHasSentCarriedItem();
      this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
      var1.releaseUsingItem();
   }

   public boolean hasExperience() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean hasMissTime() {
      return !this.localPlayerMode.isCreative();
   }

   public boolean hasInfiniteItems() {
      return this.localPlayerMode.isCreative();
   }

   public boolean isServerControlledInventory() {
      return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof HasCustomInventoryScreen;
   }

   public boolean isAlwaysFlying() {
      return this.localPlayerMode == GameType.SPECTATOR;
   }

   @Nullable
   public GameType getPreviousPlayerMode() {
      return this.previousLocalPlayerMode;
   }

   public GameType getPlayerMode() {
      return this.localPlayerMode;
   }

   public boolean isDestroying() {
      return this.isDestroying;
   }

   public int getDestroyStage() {
      return this.destroyProgress > 0.0F ? (int)(this.destroyProgress * 10.0F) : -1;
   }

   public void handlePickItem(int var1) {
      this.connection.send(new ServerboundPickItemPacket(var1));
   }

   public void handleSlotStateChanged(int var1, int var2, boolean var3) {
      this.connection.send(new ServerboundContainerSlotStateChangedPacket(var1, var2, var3));
   }
}
