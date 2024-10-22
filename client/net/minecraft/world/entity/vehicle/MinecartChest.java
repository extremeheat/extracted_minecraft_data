package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

   @Override
   protected Item getDropItem() {
      return Items.CHEST_MINECART;
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(Items.CHEST_MINECART);
   }

   @Override
   public int getContainerSize() {
      return 27;
   }

   @Override
   public BlockState getDefaultDisplayBlockState() {
      return Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
   }

   @Override
   public int getDefaultDisplayOffset() {
      return 8;
   }

   @Override
   public AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return ChestMenu.threeRows(var1, var2, this);
   }

   @Override
   public void stopOpen(Player var1) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(var1));
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      InteractionResult var3 = this.interactWithContainerVehicle(var1);
      if (var3.consumesAction() && var1.level() instanceof ServerLevel var4) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, var1);
         PiglinAi.angerNearbyPiglins(var4, var1, true);
      }

      return var3;
   }
}
