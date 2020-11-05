package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningRodBlock extends RodBlock {
   public static final BooleanProperty POWERED;

   public LightningRodBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP)).setValue(POWERED, false));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getClickedFace());
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public void onLightningStrike(BlockState var1, Level var2, BlockPos var3) {
      var2.setBlock(var3, (BlockState)var1.setValue(POWERED, true), 3);
      var2.getBlockTicks().scheduleTick(var3, this, 8);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      var2.setBlock(var3, (BlockState)var1.setValue(POWERED, false), 3);
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (var1.isThundering() && var4 instanceof ThrownTrident && ((ThrownTrident)var4).isChanneling()) {
         BlockPos var5 = var3.getBlockPos();
         if (var1.canSeeSky(var5)) {
            LightningBolt var6 = (LightningBolt)EntityType.LIGHTNING_BOLT.create(var1);
            var6.moveTo(Vec3.atBottomCenterOf(var5));
            Entity var7 = var4.getOwner();
            var6.setCause(var7 instanceof ServerPlayer ? (ServerPlayer)var7 : null);
            var1.addFreshEntity(var6);
            var1.playSound((Player)null, (BlockPos)var5, SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 5.0F, 1.0F);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED);
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
