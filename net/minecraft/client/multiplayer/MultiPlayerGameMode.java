package net.minecraft.client.multiplayer;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.PosAndRot;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiPlayerGameMode {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final ClientPacketListener connection;
   private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
   private ItemStack destroyingItem;
   private float destroyProgress;
   private float destroyTicks;
   private int destroyDelay;
   private boolean isDestroying;
   private GameType localPlayerMode;
   private final Object2ObjectLinkedOpenHashMap unAckedActions;
   private int carriedIndex;

   public MultiPlayerGameMode(Minecraft var1, ClientPacketListener var2) {
      this.destroyingItem = ItemStack.EMPTY;
      this.localPlayerMode = GameType.SURVIVAL;
      this.unAckedActions = new Object2ObjectLinkedOpenHashMap();
      this.minecraft = var1;
      this.connection = var2;
   }

   public static void creativeDestroyBlock(Minecraft var0, MultiPlayerGameMode var1, BlockPos var2, Direction var3) {
      if (!var0.level.extinguishFire(var0.player, var2, var3)) {
         var1.destroyBlock(var2);
      }

   }

   public void adjustPlayer(Player var1) {
      this.localPlayerMode.updatePlayerAbilities(var1.abilities);
   }

   public void setLocalMode(GameType var1) {
      this.localPlayerMode = var1;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.abilities);
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
            if ((var4 instanceof CommandBlock || var4 instanceof StructureBlock || var4 instanceof JigsawBlock) && !this.minecraft.player.canUseGameMasterBlocks()) {
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
         BlockState var3;
         if (this.localPlayerMode.isCreative()) {
            var3 = this.minecraft.level.getBlockState(var1);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var3, 1.0F);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, var1, var2);
            creativeDestroyBlock(this.minecraft, this, var1, var2);
            this.destroyDelay = 5;
         } else if (!this.isDestroying || !this.sameDestroyTarget(var1)) {
            if (this.isDestroying) {
               this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, var2);
            }

            var3 = this.minecraft.level.getBlockState(var1);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var3, 0.0F);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, var1, var2);
            boolean var4 = !var3.isAir();
            if (var4 && this.destroyProgress == 0.0F) {
               var3.attack(this.minecraft.level, var1, this.minecraft.player);
            }

            if (var4 && var3.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, var1) >= 1.0F) {
               this.destroyBlock(var1);
            } else {
               this.isDestroying = true;
               this.destroyBlockPos = var1;
               this.destroyingItem = this.minecraft.player.getMainHandItem();
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void stopDestroyBlock() {
      if (this.isDestroying) {
         BlockState var1 = this.minecraft.level.getBlockState(this.destroyBlockPos);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, var1, -1.0F);
         this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN);
         this.isDestroying = false;
         this.destroyProgress = 0.0F;
         this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
         this.minecraft.player.resetAttackStrengthTicker();
      }

   }

   public boolean continueDestroyBlock(BlockPos var1, Direction var2) {
      this.ensureHasSentCarriedItem();
      if (this.destroyDelay > 0) {
         --this.destroyDelay;
         return true;
      } else {
         BlockState var3;
         if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(var1)) {
            this.destroyDelay = 5;
            var3 = this.minecraft.level.getBlockState(var1);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var3, 1.0F);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, var1, var2);
            creativeDestroyBlock(this.minecraft, this, var1, var2);
            return true;
         } else if (this.sameDestroyTarget(var1)) {
            var3 = this.minecraft.level.getBlockState(var1);
            if (var3.isAir()) {
               this.isDestroying = false;
               return false;
            } else {
               this.destroyProgress += var3.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, var1);
               if (this.destroyTicks % 4.0F == 0.0F) {
                  SoundType var4 = var3.getSoundType();
                  this.minecraft.getSoundManager().play(new SimpleSoundInstance(var4.getHitSound(), SoundSource.BLOCKS, (var4.getVolume() + 1.0F) / 8.0F, var4.getPitch() * 0.5F, var1));
               }

               ++this.destroyTicks;
               this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, var1, var3, Mth.clamp(this.destroyProgress, 0.0F, 1.0F));
               if (this.destroyProgress >= 1.0F) {
                  this.isDestroying = false;
                  this.sendBlockAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, var1, var2);
                  this.destroyBlock(var1);
                  this.destroyProgress = 0.0F;
                  this.destroyTicks = 0.0F;
                  this.destroyDelay = 5;
               }

               this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0F) - 1);
               return true;
            }
         } else {
            return this.startDestroyBlock(var1, var2);
         }
      }
   }

   public float getPickRange() {
      return this.localPlayerMode.isCreative() ? 5.0F : 4.5F;
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
      boolean var3 = this.destroyingItem.isEmpty() && var2.isEmpty();
      if (!this.destroyingItem.isEmpty() && !var2.isEmpty()) {
         var3 = var2.getItem() == this.destroyingItem.getItem() && ItemStack.tagMatches(var2, this.destroyingItem) && (var2.isDamageableItem() || var2.getDamageValue() == this.destroyingItem.getDamageValue());
      }

      return var1.equals(this.destroyBlockPos) && var3;
   }

   private void ensureHasSentCarriedItem() {
      int var1 = this.minecraft.player.inventory.selected;
      if (var1 != this.carriedIndex) {
         this.carriedIndex = var1;
         this.connection.send((Packet)(new ServerboundSetCarriedItemPacket(this.carriedIndex)));
      }

   }

   public InteractionResult useItemOn(LocalPlayer var1, ClientLevel var2, InteractionHand var3, BlockHitResult var4) {
      this.ensureHasSentCarriedItem();
      BlockPos var5 = var4.getBlockPos();
      if (!this.minecraft.level.getWorldBorder().isWithinBounds(var5)) {
         return InteractionResult.FAIL;
      } else {
         ItemStack var6 = var1.getItemInHand(var3);
         if (this.localPlayerMode == GameType.SPECTATOR) {
            this.connection.send((Packet)(new ServerboundUseItemOnPacket(var3, var4)));
            return InteractionResult.SUCCESS;
         } else {
            boolean var7 = !var1.getMainHandItem().isEmpty() || !var1.getOffhandItem().isEmpty();
            boolean var8 = var1.isSecondaryUseActive() && var7;
            InteractionResult var9;
            if (!var8) {
               var9 = var2.getBlockState(var5).use(var2, var1, var3, var4);
               if (var9.consumesAction()) {
                  this.connection.send((Packet)(new ServerboundUseItemOnPacket(var3, var4)));
                  return var9;
               }
            }

            this.connection.send((Packet)(new ServerboundUseItemOnPacket(var3, var4)));
            if (!var6.isEmpty() && !var1.getCooldowns().isOnCooldown(var6.getItem())) {
               UseOnContext var10 = new UseOnContext(var1, var3, var4);
               if (this.localPlayerMode.isCreative()) {
                  int var11 = var6.getCount();
                  var9 = var6.useOn(var10);
                  var6.setCount(var11);
               } else {
                  var9 = var6.useOn(var10);
               }

               return var9;
            } else {
               return InteractionResult.PASS;
            }
         }
      }
   }

   public InteractionResult useItem(Player var1, Level var2, InteractionHand var3) {
      if (this.localPlayerMode == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else {
         this.ensureHasSentCarriedItem();
         this.connection.send((Packet)(new ServerboundUseItemPacket(var3)));
         ItemStack var4 = var1.getItemInHand(var3);
         if (var1.getCooldowns().isOnCooldown(var4.getItem())) {
            return InteractionResult.PASS;
         } else {
            int var5 = var4.getCount();
            InteractionResultHolder var6 = var4.use(var2, var1, var3);
            ItemStack var7 = (ItemStack)var6.getObject();
            if (var7 != var4 || var7.getCount() != var5) {
               var1.setItemInHand(var3, var7);
            }

            return var6.getResult();
         }
      }
   }

   public LocalPlayer createPlayer(ClientLevel var1, StatsCounter var2, ClientRecipeBook var3) {
      return new LocalPlayer(this.minecraft, var1, this.connection, var2, var3);
   }

   public void attack(Player var1, Entity var2) {
      this.ensureHasSentCarriedItem();
      this.connection.send((Packet)(new ServerboundInteractPacket(var2)));
      if (this.localPlayerMode != GameType.SPECTATOR) {
         var1.attack(var2);
         var1.resetAttackStrengthTicker();
      }

   }

   public InteractionResult interact(Player var1, Entity var2, InteractionHand var3) {
      this.ensureHasSentCarriedItem();
      this.connection.send((Packet)(new ServerboundInteractPacket(var2, var3)));
      return this.localPlayerMode == GameType.SPECTATOR ? InteractionResult.PASS : var1.interactOn(var2, var3);
   }

   public InteractionResult interactAt(Player var1, Entity var2, EntityHitResult var3, InteractionHand var4) {
      this.ensureHasSentCarriedItem();
      Vec3 var5 = var3.getLocation().subtract(var2.getX(), var2.getY(), var2.getZ());
      this.connection.send((Packet)(new ServerboundInteractPacket(var2, var4, var5)));
      return this.localPlayerMode == GameType.SPECTATOR ? InteractionResult.PASS : var2.interactAt(var1, var5, var4);
   }

   public ItemStack handleInventoryMouseClick(int var1, int var2, int var3, ClickType var4, Player var5) {
      short var6 = var5.containerMenu.backup(var5.inventory);
      ItemStack var7 = var5.containerMenu.clicked(var2, var3, var4, var5);
      this.connection.send((Packet)(new ServerboundContainerClickPacket(var1, var2, var3, var4, var7, var6)));
      return var7;
   }

   public void handlePlaceRecipe(int var1, Recipe var2, boolean var3) {
      this.connection.send((Packet)(new ServerboundPlaceRecipePacket(var1, var2, var3)));
   }

   public void handleInventoryButtonClick(int var1, int var2) {
      this.connection.send((Packet)(new ServerboundContainerButtonClickPacket(var1, var2)));
   }

   public void handleCreativeModeItemAdd(ItemStack var1, int var2) {
      if (this.localPlayerMode.isCreative()) {
         this.connection.send((Packet)(new ServerboundSetCreativeModeSlotPacket(var2, var1)));
      }

   }

   public void handleCreativeModeItemDrop(ItemStack var1) {
      if (this.localPlayerMode.isCreative() && !var1.isEmpty()) {
         this.connection.send((Packet)(new ServerboundSetCreativeModeSlotPacket(-1, var1)));
      }

   }

   public void releaseUsingItem(Player var1) {
      this.ensureHasSentCarriedItem();
      this.connection.send((Packet)(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN)));
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

   public boolean hasFarPickRange() {
      return this.localPlayerMode.isCreative();
   }

   public boolean isServerControlledInventory() {
      return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof AbstractHorse;
   }

   public boolean isAlwaysFlying() {
      return this.localPlayerMode == GameType.SPECTATOR;
   }

   public GameType getPlayerMode() {
      return this.localPlayerMode;
   }

   public boolean isDestroying() {
      return this.isDestroying;
   }

   public void handlePickItem(int var1) {
      this.connection.send((Packet)(new ServerboundPickItemPacket(var1)));
   }

   private void sendBlockAction(ServerboundPlayerActionPacket.Action var1, BlockPos var2, Direction var3) {
      LocalPlayer var4 = this.minecraft.player;
      this.unAckedActions.put(Pair.of(var2, var1), new PosAndRot(var4.position(), var4.xRot, var4.yRot));
      this.connection.send((Packet)(new ServerboundPlayerActionPacket(var1, var2, var3)));
   }

   public void handleBlockBreakAck(ClientLevel var1, BlockPos var2, BlockState var3, ServerboundPlayerActionPacket.Action var4, boolean var5) {
      PosAndRot var6 = (PosAndRot)this.unAckedActions.remove(Pair.of(var2, var4));
      if (var6 == null || !var5 || var4 != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && var1.getBlockState(var2) != var3) {
         var1.setKnownState(var2, var3);
         if (var6 != null) {
            Vec3 var7 = var6.pos();
            this.minecraft.player.absMoveTo(var7.x, var7.y, var7.z, var6.yRot(), var6.xRot());
         }
      }

      while(this.unAckedActions.size() >= 50) {
         Pair var8 = (Pair)this.unAckedActions.firstKey();
         this.unAckedActions.removeFirst();
         LOGGER.error("Too many unacked block actions, dropping " + var8);
      }

   }
}
