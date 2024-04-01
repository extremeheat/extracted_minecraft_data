package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PotatoBatteryBlock extends Block {
   public static final MapCodec<PotatoBatteryBlock> CODEC = simpleCodec(PotatoBatteryBlock::new);
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

   @Override
   public MapCodec<PotatoBatteryBlock> codec() {
      return CODEC;
   }

   public PotatoBatteryBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(INVERTED, Boolean.valueOf(false)));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(INVERTED) ? 15 : 0;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var4.mayBuild()) {
         if (var2.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            BlockState var6 = var1.cycle(INVERTED);
            var2.setBlock(var3, var6, 3);
            var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var4, var6));
            return InteractionResult.CONSUME;
         }
      } else {
         return super.useWithoutItem(var1, var2, var3, var4, var5);
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4 instanceof LivingEntity var5 && var1.getValue(INVERTED) && var2 instanceof ServerLevel var6) {
         var5.hurt(var2.damageSources().potatoMagic(), 0.5F);

         for(float var7 = 0.2F; var7 < 0.8F; var7 += 0.1F) {
            var6.sendParticles(
               ParticleTypes.ELECTRIC_SPARK,
               (double)((float)var3.getX() + var7),
               (double)var3.getY() + 0.35,
               (double)((float)var3.getZ() + var7),
               1,
               0.05,
               0.05,
               0.05,
               0.1
            );
         }

         var6.playSound(null, var3, SoundEvents.BATTERY_ZAP, SoundSource.BLOCKS, 0.5F, 2.0F);
      }

      super.entityInside(var1, var2, var3, var4);
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(INVERTED);
   }
}
