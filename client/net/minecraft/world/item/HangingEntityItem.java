package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class HangingEntityItem extends Item {
   private final EntityType<? extends HangingEntity> type;

   public HangingEntityItem(EntityType<? extends HangingEntity> var1, Item.Properties var2) {
      super(var2);
      this.type = var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Direction var3 = var1.getClickedFace();
      BlockPos var4 = var2.relative(var3);
      Player var5 = var1.getPlayer();
      ItemStack var6 = var1.getItemInHand();
      if (var5 != null && !this.mayPlace(var5, var3, var6, var4)) {
         return InteractionResult.FAIL;
      } else {
         Level var7 = var1.getLevel();
         Object var8;
         if (this.type == EntityType.PAINTING) {
            var8 = new Painting(var7, var4, var3);
         } else if (this.type == EntityType.ITEM_FRAME) {
            var8 = new ItemFrame(var7, var4, var3);
         } else {
            if (this.type != EntityType.GLOW_ITEM_FRAME) {
               return InteractionResult.sidedSuccess(var7.isClientSide);
            }

            var8 = new GlowItemFrame(var7, var4, var3);
         }

         CompoundTag var9 = var6.getTag();
         if (var9 != null) {
            EntityType.updateCustomEntityTag(var7, var5, (Entity)var8, var9);
         }

         if (((HangingEntity)var8).survives()) {
            if (!var7.isClientSide) {
               ((HangingEntity)var8).playPlacementSound();
               var7.gameEvent(var5, GameEvent.ENTITY_PLACE, var2);
               var7.addFreshEntity((Entity)var8);
            }

            var6.shrink(1);
            return InteractionResult.sidedSuccess(var7.isClientSide);
         } else {
            return InteractionResult.CONSUME;
         }
      }
   }

   protected boolean mayPlace(Player var1, Direction var2, ItemStack var3, BlockPos var4) {
      return !var2.getAxis().isVertical() && var1.mayUseItemAt(var4, var2, var3);
   }
}
