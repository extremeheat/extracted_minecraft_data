package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EmptyBucketInteraction implements OnInteract {
   public EmptyBucketInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand hand, final Vec3 vec3, final LivingBlock livingBlock) {
      Item var6 = livingBlock.getItemStack().getItem();
      if (var6 instanceof DispensibleContainerItem bucketItem) {
         BlockPos currPos = livingBlock.blockPosition();
         livingBlock.setPos(currPos.getBottomCenter().add(0.0, 1.5, 0.0));
         livingBlock.addDeltaMovement(player.getLookAngle().multiply(1.0, 0.0, 1.0).add(0.0, 0.1, 0.0));
         boolean result = bucketItem.emptyContents((LivingEntity)null, livingBlock.level(), currPos, (BlockHitResult)null);
         if (result) {
            bucketItem.checkExtraContent((LivingEntity)null, livingBlock.level(), livingBlock.getItemStack(), livingBlock.blockPosition());
            livingBlock.setItemStack(new ItemStack(Items.BUCKET));
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.FAIL;
   }
}
