package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.LivingBlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.interact.OnInteract;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class DispenseLivingBlockInteraction implements OnInteract {
   public DispenseLivingBlockInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      if (interactionHand == InteractionHand.MAIN_HAND) {
         Level var6 = player.level();
         if (var6 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var6;
            RandomSource random = level.getRandom();
            if (random.nextDouble() > 0.2) {
               List<LivingBlock> dispenseList = level.getEntities(EntityType.LIVING_BLOCK, livingBlock.getBoundingBox().inflate(4.0), (e) -> e.isAlive() && e != livingBlock);
               if (!dispenseList.isEmpty()) {
                  LivingBlock toDispense = (LivingBlock)dispenseList.get(random.nextInt(dispenseList.size()));
                  ItemStack itemStack = toDispense.getItemStack();
                  DispenseItemBehavior behavior = this.getDispenseItemBehavior(level, itemStack);
                  if (behavior != DispenseItemBehavior.NOOP) {
                     behavior.dispense(new LivingBlockSource(level, livingBlock), itemStack);
                     toDispense.discard();
                  }

                  return InteractionResult.SUCCESS_SERVER;
               }
            }

            return this.yoinkNearbyMob(livingBlock, level);
         }
      }

      return InteractionResult.FAIL;
   }

   private InteractionResult yoinkNearbyMob(final LivingBlock livingBlock, final ServerLevel level) {
      List<Mob> dispenseList = level.getEntitiesOfClass(Mob.class, livingBlock.getBoundingBox().inflate(10.0), (e) -> e.isAlive() && e instanceof Enemy);
      if (!dispenseList.isEmpty()) {
         Mob toDispense = (Mob)dispenseList.get(level.getRandom().nextInt(dispenseList.size()));
         Direction dispenseDirection = livingBlock.getNearestViewDirection().getOpposite();
         BlockPos dispensePos = livingBlock.blockPosition().relative(dispenseDirection);
         livingBlock.invulnerableTime = 20;
         toDispense.setPos((double)dispensePos.getX(), (double)dispensePos.getY(), (double)dispensePos.getZ());
         toDispense.setDeltaMovement(dispenseDirection.getUnitVec3().scale(1.5));
         toDispense.absSnapRotationTo(dispenseDirection.toYRot(), 0.0F);
         DefaultDispenseItemBehavior.playDefaultSound(level, dispensePos);
         DefaultDispenseItemBehavior.playDefaultAnimation(level, dispensePos, dispenseDirection);
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.FAIL;
      }
   }

   public DispenseItemBehavior getDispenseItemBehavior(final ServerLevel level, final ItemStack itemStack) {
      return DispenserBlock.getDispenseItemBehavior(level, itemStack);
   }
}
