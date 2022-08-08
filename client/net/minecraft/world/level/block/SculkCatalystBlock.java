package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;

public class SculkCatalystBlock extends BaseEntityBlock {
   public static final int PULSE_TICKS = 8;
   public static final BooleanProperty PULSE;
   private final IntProvider xpRange = ConstantInt.of(20);

   public SculkCatalystBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(PULSE, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(PULSE);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(PULSE)) {
         var2.setBlock(var3, (BlockState)var1.setValue(PULSE, false), 3);
      }

   }

   public static void bloom(ServerLevel var0, BlockPos var1, BlockState var2, RandomSource var3) {
      var0.setBlock(var1, (BlockState)var2.setValue(PULSE, true), 3);
      var0.scheduleTick(var1, var2.getBlock(), 8);
      var0.sendParticles(ParticleTypes.SCULK_SOUL, (double)var1.getX() + 0.5, (double)var1.getY() + 1.15, (double)var1.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
      var0.playSound((Player)null, var1, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + var3.nextFloat() * 0.4F);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SculkCatalystBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> GameEventListener getListener(ServerLevel var1, T var2) {
      if (var2 instanceof SculkCatalystBlockEntity var3) {
         return var3;
      } else {
         return null;
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.SCULK_CATALYST, SculkCatalystBlockEntity::serverTick);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         this.tryDropExperience(var2, var3, var4, this.xpRange);
      }

   }

   static {
      PULSE = BlockStateProperties.BLOOM;
   }
}
