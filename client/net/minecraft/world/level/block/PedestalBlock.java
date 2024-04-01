package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PedestalBlock extends Block {
   private static final VoxelShape TOP = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
   private static final VoxelShape BASE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
   private static final VoxelShape SHAPE = Shapes.or(TOP, BASE, BOTTOM);
   public static final MapCodec<PedestalBlock> CODEC = simpleCodec(PedestalBlock::new);

   @Override
   public MapCodec<PedestalBlock> codec() {
      return CODEC;
   }

   public PedestalBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return SHAPE;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var1.is(Items.POISONOUS_POTATO)
         && var3.getBlockState(var4.above()).canBeReplaced()
         && var3 instanceof ServerLevel var8
         && var3.dimensionType().natural()) {
         var3.setBlock(var4.above(), Blocks.POTATO_PORTAL.defaultBlockState(), 3);
         var1.consume(1, var5);
         var8.sendParticles(
            ParticleTypes.ELECTRIC_SPARK, (double)var4.getX() + 0.5, (double)var4.getY() + 1.5, (double)var4.getZ() + 0.5, 100, 0.5, 0.5, 0.5, 0.2
         );
         var8.playSound(null, var4, SoundEvents.PLAGUEWHALE_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!var5.chapterIsPast("portal_opened")) {
            var5.setPotatoQuestChapter("portal_opened");
         }

         return ItemInteractionResult.SUCCESS;
      }

      return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
   }
}
