package net.minecraft.world.item;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class AxeItem extends DiggerItem {
   private static final Set<Material> DIGGABLE_MATERIALS;
   private static final Set<Block> OTHER_DIGGABLE_BLOCKS;
   protected static final Map<Block, Block> STRIPABLES;

   protected AxeItem(Tier var1, float var2, float var3, Item.Properties var4) {
      super(var2, var3, var1, OTHER_DIGGABLE_BLOCKS, var4);
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      Material var3 = var2.getMaterial();
      return DIGGABLE_MATERIALS.contains(var3) ? this.speed : super.getDestroySpeed(var1, var2);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      Block var5 = (Block)STRIPABLES.get(var4.getBlock());
      if (var5 != null) {
         Player var6 = var1.getPlayer();
         var2.playSound(var6, var3, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!var2.isClientSide) {
            var2.setBlock(var3, (BlockState)var5.defaultBlockState().setValue(RotatedPillarBlock.AXIS, var4.getValue(RotatedPillarBlock.AXIS)), 11);
            if (var6 != null) {
               var1.getItemInHand().hurtAndBreak(1, var6, (var1x) -> {
                  var1x.broadcastBreakEvent(var1.getHand());
               });
            }
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   static {
      DIGGABLE_MATERIALS = Sets.newHashSet(new Material[]{Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT, Material.BAMBOO, Material.VEGETABLE});
      OTHER_DIGGABLE_BLOCKS = Sets.newHashSet(new Block[]{Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON});
      STRIPABLES = (new Builder()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).build();
   }
}
