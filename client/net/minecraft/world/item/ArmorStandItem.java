package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Direction var2 = var1.getClickedFace();
      if (var2 == Direction.DOWN) {
         return InteractionResult.FAIL;
      } else {
         Level var3 = var1.getLevel();
         BlockPlaceContext var4 = new BlockPlaceContext(var1);
         BlockPos var5 = var4.getClickedPos();
         ItemStack var6 = var1.getItemInHand();
         Vec3 var7 = Vec3.atBottomCenterOf(var5);
         AABB var8 = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(var7.x(), var7.y(), var7.z());
         if (var3.noCollision((Entity)null, var8) && var3.getEntities((Entity)null, var8).isEmpty()) {
            if (var3 instanceof ServerLevel) {
               ServerLevel var9 = (ServerLevel)var3;
               Consumer var10 = EntityType.createDefaultStackConfig(var9, var6, var1.getPlayer());
               ArmorStand var11 = (ArmorStand)EntityType.ARMOR_STAND.create(var9, var10, var5, MobSpawnType.SPAWN_EGG, true, true);
               if (var11 == null) {
                  return InteractionResult.FAIL;
               }

               float var12 = (float)Mth.floor((Mth.wrapDegrees(var1.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
               var11.moveTo(var11.getX(), var11.getY(), var11.getZ(), var12, 0.0F);
               var9.addFreshEntityWithPassengers(var11);
               var3.playSound((Player)null, var11.getX(), var11.getY(), var11.getZ(), (SoundEvent)SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               var11.gameEvent(GameEvent.ENTITY_PLACE, var1.getPlayer());
            }

            var6.shrink(1);
            return InteractionResult.sidedSuccess(var3.isClientSide);
         } else {
            return InteractionResult.FAIL;
         }
      }
   }
}
