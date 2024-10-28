package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnifferEggBlock extends Block {
   public static final MapCodec<SnifferEggBlock> CODEC = simpleCodec(SnifferEggBlock::new);
   public static final int MAX_HATCH_LEVEL = 2;
   public static final IntegerProperty HATCH;
   private static final int REGULAR_HATCH_TIME_TICKS = 24000;
   private static final int BOOSTED_HATCH_TIME_TICKS = 12000;
   private static final int RANDOM_HATCH_OFFSET_TICKS = 300;
   private static final VoxelShape SHAPE;

   public MapCodec<SnifferEggBlock> codec() {
      return CODEC;
   }

   public SnifferEggBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HATCH);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public int getHatchLevel(BlockState var1) {
      return (Integer)var1.getValue(HATCH);
   }

   private boolean isReadyToHatch(BlockState var1) {
      return this.getHatchLevel(var1) == 2;
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!this.isReadyToHatch(var1)) {
         var2.playSound((Player)null, var3, SoundEvents.SNIFFER_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
         var2.setBlock(var3, (BlockState)var1.setValue(HATCH, this.getHatchLevel(var1) + 1), 2);
      } else {
         var2.playSound((Player)null, var3, SoundEvents.SNIFFER_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
         var2.destroyBlock(var3, false);
         Sniffer var5 = (Sniffer)EntityType.SNIFFER.create(var2);
         if (var5 != null) {
            Vec3 var6 = var3.getCenter();
            var5.setBaby(true);
            var5.moveTo(var6.x(), var6.y(), var6.z(), Mth.wrapDegrees(var2.random.nextFloat() * 360.0F), 0.0F);
            var2.addFreshEntity(var5);
         }

      }
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      boolean var6 = hatchBoost(var2, var3);
      if (!var2.isClientSide() && var6) {
         var2.levelEvent(3009, var3, 0);
      }

      int var7 = var6 ? 12000 : 24000;
      int var8 = var7 / 3;
      var2.gameEvent(GameEvent.BLOCK_PLACE, var3, GameEvent.Context.of(var1));
      var2.scheduleTick(var3, this, var8 + var2.random.nextInt(300));
   }

   public boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   public static boolean hatchBoost(BlockGetter var0, BlockPos var1) {
      return var0.getBlockState(var1.below()).is(BlockTags.SNIFFER_EGG_HATCH_BOOST);
   }

   static {
      HATCH = BlockStateProperties.HATCH;
      SHAPE = Block.box(1.0, 0.0, 2.0, 15.0, 16.0, 14.0);
   }
}
