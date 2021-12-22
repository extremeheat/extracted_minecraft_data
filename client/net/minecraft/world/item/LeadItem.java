package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LeadItem extends Item {
   public LeadItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(BlockTags.FENCES)) {
         Player var5 = var1.getPlayer();
         if (!var2.isClientSide && var5 != null) {
            bindPlayerMobs(var5, var2, var3);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static InteractionResult bindPlayerMobs(Player var0, Level var1, BlockPos var2) {
      LeashFenceKnotEntity var3 = null;
      boolean var4 = false;
      double var5 = 7.0D;
      int var7 = var2.getX();
      int var8 = var2.getY();
      int var9 = var2.getZ();
      List var10 = var1.getEntitiesOfClass(Mob.class, new AABB((double)var7 - 7.0D, (double)var8 - 7.0D, (double)var9 - 7.0D, (double)var7 + 7.0D, (double)var8 + 7.0D, (double)var9 + 7.0D));
      Iterator var11 = var10.iterator();

      while(var11.hasNext()) {
         Mob var12 = (Mob)var11.next();
         if (var12.getLeashHolder() == var0) {
            if (var3 == null) {
               var3 = LeashFenceKnotEntity.getOrCreateKnot(var1, var2);
               var3.playPlacementSound();
            }

            var12.setLeashedTo(var3, true);
            var4 = true;
         }
      }

      return var4 ? InteractionResult.SUCCESS : InteractionResult.PASS;
   }
}
