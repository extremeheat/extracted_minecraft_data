package net.minecraft.world.entity.livingblock.interact;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockBehavior;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockContainerBehavior;
import net.minecraft.world.entity.livingblock.behavior.LivingBlockMobContainerBehavior;
import net.minecraft.world.entity.livingblock.behavior.SimpleContainerBehavior;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class OpenContainerInteraction implements OnInteract {
   public OpenContainerInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand hand, final Vec3 location, final LivingBlock entity) {
      Optional<SimpleContainerBehavior> simpleContainer = entity.getBehaviorOfType(LivingBlockContainerBehavior.class).or(() -> entity.getBehaviorOfType(LivingBlockMobContainerBehavior.class)).flatMap((e) -> {
         LivingBlockBehavior instance = e.instance;
         if (instance instanceof SimpleContainerBehavior simpleContainerBehavior) {
            return Optional.of(simpleContainerBehavior);
         } else {
            return Optional.empty();
         }
      });
      if (player instanceof ServerPlayer && simpleContainer.isPresent()) {
         final SimpleContainerBehavior behavior = (SimpleContainerBehavior)simpleContainer.get();
         final boolean isShulker = entity.getItemStack().is(Items.SHULKER_BOX);
         player.openMenu(new MenuProvider() {
            {
               Objects.requireNonNull(OpenContainerInteraction.this);
            }

            public Component getDisplayName() {
               return behavior.displayName(entity);
            }

            public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
               return (AbstractContainerMenu)(isShulker ? new ShulkerBoxMenu(containerId, inventory, behavior.getContainer()) : ChestMenu.threeRows(containerId, inventory, behavior.getContainer()));
            }
         });
         player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
      }

      return InteractionResult.SUCCESS;
   }
}
