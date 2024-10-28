package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MinecartChest extends AbstractMinecartContainer {
   public MinecartChest(EntityType<? extends MinecartChest> var1, Level var2) {
      super(var1, var2);
   }

   protected Item getDropItem() {
      return Items.CHEST_MINECART;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.CHEST_MINECART);
   }

   public int getContainerSize() {
      return 27;
   }

   public BlockState getDefaultDisplayBlockState() {
      return (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
   }

   public int getDefaultDisplayOffset() {
      return 8;
   }

   public AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return ChestMenu.threeRows(var1, var2, this);
   }

   public void stopOpen(Player var1) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of((Entity)var1));
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      InteractionResult var3 = this.interactWithContainerVehicle(var1);
      if (var3.consumesAction()) {
         Level var5 = var1.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            this.gameEvent(GameEvent.CONTAINER_OPEN, var1);
            PiglinAi.angerNearbyPiglins(var4, var1, true);
         }
      }

      return var3;
   }
}
