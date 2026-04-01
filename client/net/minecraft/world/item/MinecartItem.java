package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class MinecartItem extends Item {
   private final EntityType<? extends AbstractMinecart> type;

   public MinecartItem(final EntityType<? extends AbstractMinecart> type, final Item.Properties properties) {
      super(properties);
      this.type = type;
   }

   public InteractionResult useOn(final UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      return spawnMinecart(level, pos, this.type, context.getItemInHand(), context.getPlayer());
   }

   public static InteractionResult spawnMinecart(final Level level, final BlockPos pos, final EntityType<? extends AbstractMinecart> type, final ItemStack itemStack, final @Nullable Player player) {
      BlockState blockState = level.getBlockState(pos);
      if (!blockState.is(BlockTags.RAILS)) {
         return InteractionResult.FAIL;
      } else {
         RailShape shape = blockState.getBlock() instanceof BaseRailBlock ? (RailShape)blockState.getValue(((BaseRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
         double offset = 0.0;
         if (shape.isSlope()) {
            offset = 0.5;
         }

         Vec3 spawnPos = new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.0625 + offset, (double)pos.getZ() + 0.5);
         AbstractMinecart cart = AbstractMinecart.createMinecart(level, spawnPos.x, spawnPos.y, spawnPos.z, type, EntitySpawnReason.DISPENSER, itemStack, player);
         if (cart == null) {
            return InteractionResult.FAIL;
         } else {
            if (AbstractMinecart.useExperimentalMovement(level)) {
               for(Entity entity : level.getEntities((Entity)null, cart.getBoundingBox())) {
                  if (entity instanceof AbstractMinecart) {
                     return InteractionResult.FAIL;
                  }
               }
            }

            if (level instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)level;
               serverLevel.addFreshEntity(cart);
               serverLevel.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(player, serverLevel.getBlockState(pos.below())));
            }

            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
         }
      }
   }

   public EntityType<? extends AbstractMinecart> getType() {
      return this.type;
   }
}
