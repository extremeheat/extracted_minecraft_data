package net.minecraft.world.entity.livingblock.interact;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EnchantRandomItemInteraction implements OnInteract {
   public EnchantRandomItemInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock enchantingTableBlock) {
      if (!(player.level() instanceof ServerLevel)) {
         return InteractionResult.FAIL;
      } else {
         ServerLevel level = (ServerLevel)player.level();
         List<LivingBlock> entitiesNearby = level.getEntities(EntityType.LIVING_BLOCK, enchantingTableBlock.getBoundingBox().inflate(20.0), Entity::isAlive);
         LivingBlock enchantable = (LivingBlock)entitiesNearby.stream().filter((block) -> {
            ItemStack item = block.getItemStack();
            return item.isEnchantable() && !item.isEnchanted();
         }).findAny().orElse((Object)null);
         if (enchantable == null) {
            return InteractionResult.FAIL;
         } else {
            List<LivingBlock> bookshelves = entitiesNearby.stream().filter((block) -> block.getBlockState().is(Blocks.BOOKSHELF)).toList();
            enchantingTableBlock.interrupt();
            enchantingTableBlock.setCommander(player);
            enchantable.interrupt();
            enchantable.hopesAndDreams.desire(Desires.APPROACH, Target.exactlyAt(enchantingTableBlock.position().add(Vec3.Y_AXIS)));
            enchantable.setCommander(player);
            bookshelves.forEach((shelf) -> {
               shelf.interrupt();
               shelf.hopesAndDreams.desire(Desires.APPROACH, Target.nearEntity(enchantingTableBlock, 1.0));
               shelf.setCommander(player);
            });
            enchantingTableBlock.livingBlockBeingEnchanted = enchantable;
            return InteractionResult.SUCCESS_SERVER;
         }
      }
   }
}
