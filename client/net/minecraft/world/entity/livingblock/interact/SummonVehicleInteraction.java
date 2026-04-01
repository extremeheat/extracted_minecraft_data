package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.phys.Vec3;

public class SummonVehicleInteraction implements OnInteract {
   public SummonVehicleInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      ItemStack itemStack = livingBlock.getItemStack().copy();
      Item item = itemStack.getItem();
      InteractionResult interactionResult;
      if (item instanceof BoatItem boat) {
         interactionResult = BoatItem.summonBoat(player.level(), player, itemStack, boat.getEntityType());
      } else if (item instanceof MinecartItem minecart) {
         interactionResult = MinecartItem.spawnMinecart(player.level(), livingBlock.blockPosition(), minecart.getType(), itemStack, player);
      } else {
         interactionResult = InteractionResult.FAIL;
      }

      if (interactionResult != InteractionResult.FAIL && interactionResult != InteractionResult.PASS) {
         livingBlock.discard();
      }

      return interactionResult;
   }
}
