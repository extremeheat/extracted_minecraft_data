package net.minecraft.world.entity.livingblock.behavior;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class UseItemOnConditionBehavior implements LivingBlockBehavior {
   private static final int CHECK_INTERVAL = 10;
   private final double maxDistance;
   private final Predicate<Player> selector;
   private final @Nullable ItemStack overrideItemStack;
   private int lastTriggeredTick = 0;

   public static LivingBlockBehaviorType useItemIf(final double maxDistance, final Predicate<Player> selector) {
      return LivingBlockBehaviorType.behaviorType((Function)((var3) -> new UseItemOnConditionBehavior(maxDistance, selector, (ItemStack)null)));
   }

   public static LivingBlockBehaviorType useItemIf(final double maxDistance, final Predicate<Player> selector, final ItemStack overrideItemStack) {
      return LivingBlockBehaviorType.behaviorType((Function)((var4) -> new UseItemOnConditionBehavior(maxDistance, selector, overrideItemStack)));
   }

   public UseItemOnConditionBehavior(final double maxDistance, final Predicate<Player> selector, final @Nullable ItemStack overrideItemStack) {
      super();
      this.maxDistance = maxDistance;
      this.selector = selector;
      this.overrideItemStack = overrideItemStack;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.tickCount >= this.lastTriggeredTick + 10;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      List<Player> playersNearby = level.getEntities(EntityType.PLAYER, entity.getBoundingBox().inflate(this.maxDistance), this.selector);
      if (!playersNearby.isEmpty()) {
         Iterator var5 = playersNearby.iterator();
         if (var5.hasNext()) {
            Player player = (Player)var5.next();
            ItemStack itemStack = this.overrideItemStack != null ? this.overrideItemStack : entity.getItemStack();
            itemStack.finishUsingItem(level, player);
            entity.discard();
            return true;
         }
      }

      return false;
   }
}
