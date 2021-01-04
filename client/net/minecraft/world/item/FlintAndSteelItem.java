package net.minecraft.world.item;

import java.util.Iterator;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      BlockPos var5 = var4.relative(var1.getClickedFace());
      BlockState var6;
      if (canUse(var3.getBlockState(var5), var3, var5)) {
         var3.playSound(var2, var5, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         var6 = ((FireBlock)Blocks.FIRE).getStateForPlacement(var3, var5);
         var3.setBlock(var5, var6, 11);
         ItemStack var7 = var1.getItemInHand();
         if (var2 instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var5, var7);
            var7.hurtAndBreak(1, var2, (var1x) -> {
               var1x.broadcastBreakEvent(var1.getHand());
            });
         }

         return InteractionResult.SUCCESS;
      } else {
         var6 = var3.getBlockState(var4);
         if (canLightCampFire(var6)) {
            var3.playSound(var2, var4, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            var3.setBlock(var4, (BlockState)var6.setValue(BlockStateProperties.LIT, true), 11);
            if (var2 != null) {
               var1.getItemInHand().hurtAndBreak(1, var2, (var1x) -> {
                  var1x.broadcastBreakEvent(var1.getHand());
               });
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   public static boolean canLightCampFire(BlockState var0) {
      return var0.getBlock() == Blocks.CAMPFIRE && !(Boolean)var0.getValue(BlockStateProperties.WATERLOGGED) && !(Boolean)var0.getValue(BlockStateProperties.LIT);
   }

   public static boolean canUse(BlockState var0, LevelAccessor var1, BlockPos var2) {
      BlockState var3 = ((FireBlock)Blocks.FIRE).getStateForPlacement(var1, var2);
      boolean var4 = false;
      Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction var6 = (Direction)var5.next();
         if (var1.getBlockState(var2.relative(var6)).getBlock() == Blocks.OBSIDIAN && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(var1, var2) != null) {
            var4 = true;
         }
      }

      return var0.isAir() && (var3.canSurvive(var1, var2) || var4);
   }
}
