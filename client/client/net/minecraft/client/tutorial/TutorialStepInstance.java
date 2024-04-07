package net.minecraft.client.tutorial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface TutorialStepInstance {
   default void clear() {
   }

   default void tick() {
   }

   default void onInput(Input var1) {
   }

   default void onMouse(double var1, double var3) {
   }

   default void onLookAt(ClientLevel var1, HitResult var2) {
   }

   default void onDestroyBlock(ClientLevel var1, BlockPos var2, BlockState var3, float var4) {
   }

   default void onOpenInventory() {
   }

   default void onGetItem(ItemStack var1) {
   }
}
