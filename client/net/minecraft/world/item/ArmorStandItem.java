package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStandItem extends Item {
   public ArmorStandItem(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Direction clickedFace = context.getClickedFace();
      if (clickedFace == Direction.DOWN) {
         return InteractionResult.FAIL;
      } else {
         Level level = context.getLevel();
         BlockPlaceContext placeContext = new BlockPlaceContext(context);
         BlockPos blockPos = placeContext.getClickedPos();
         ItemStack itemStack = context.getItemInHand();
         Vec3 pos = Vec3.atBottomCenterOf(blockPos);
         AABB box = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(pos.x(), pos.y(), pos.z());
         if (level.noCollision((Entity)null, box) && level.getEntities((Entity)null, box).isEmpty()) {
            if (level instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)level;
               Consumer<ArmorStand> entityConfig = EntityType.<ArmorStand>createDefaultStackConfig(serverLevel, itemStack, context.getPlayer());
               ArmorStand entity = EntityType.ARMOR_STAND.create(serverLevel, entityConfig, blockPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
               if (entity == null) {
                  return InteractionResult.FAIL;
               }

               float yRot = (float)Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
               entity.snapTo(entity.getX(), entity.getY(), entity.getZ(), yRot, 0.0F);
               serverLevel.addFreshEntityWithPassengers(entity);
               level.playSound((Entity)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               entity.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
            }

            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      }
   }
}
