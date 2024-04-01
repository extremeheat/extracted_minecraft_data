package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FloatatoItem extends BlockItem {
   private static final float FLOATING_PLACE_DISTANCE = 3.0F;

   public FloatatoItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      Vec3 var4 = var2.getEyePosition();
      Vec3 var5 = var2.calculateViewVector(var2.getXRot(), var2.getYRot());
      Vec3 var6 = var4.add(var5.scale(var2.blockInteractionRange()));
      BlockHitResult var7 = var1.clip(new ClipContext(var4, var6, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, var2));
      ItemStack var8 = var2.getItemInHand(var3);
      if (var7.getType() == HitResult.Type.MISS) {
         Vec3 var11 = var4.add(var5.scale(3.0));
         InteractionResult var10 = super.place(
            new BlockPlaceContext(var2, var3, var8, new BlockHitResult(var11, var2.getDirection(), BlockPos.containing(var11), false))
         );
         return new InteractionResultHolder<>(var10, var8);
      } else {
         InteractionResult var9 = super.useOn(new UseOnContext(var2, var3, var7));
         return new InteractionResultHolder<>(var9, var8);
      }
   }
}
