package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class TntBlock extends Block {
   public static final MapCodec<TntBlock> CODEC = simpleCodec(TntBlock::new);
   public static final BooleanProperty UNSTABLE;

   public MapCodec<TntBlock> codec() {
      return CODEC;
   }

   public TntBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(UNSTABLE, false));
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!oldState.is(state.getBlock())) {
         if (level.hasNeighborSignal(pos) && prime(level, pos)) {
            level.removeBlock(pos, false);
         }

      }
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (level.hasNeighborSignal(pos) && prime(level, pos)) {
         level.removeBlock(pos, false);
      }

   }

   public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
      if (!level.isClientSide() && !player.getAbilities().instabuild && (Boolean)state.getValue(UNSTABLE)) {
         prime(level, pos);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public void wasExploded(final ServerLevel level, final BlockPos pos, final Explosion explosion) {
      if ((Boolean)level.getGameRules().get(GameRules.TNT_EXPLODES)) {
         PrimedTnt primed = new PrimedTnt(level, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, explosion.getIndirectSourceEntity());
         primed.setFuse(PrimedTnt.getRandomShortFuse(primed.getFuse(), level.getRandom()));
         level.addFreshEntity(primed);
      }
   }

   public static boolean prime(final Level level, final BlockPos pos) {
      return prime(level, pos, (LivingEntity)null);
   }

   private static boolean prime(final Level level, final BlockPos pos, final @Nullable LivingEntity source) {
      if (level instanceof ServerLevel serverLevel) {
         if ((Boolean)serverLevel.getGameRules().get(GameRules.TNT_EXPLODES)) {
            PrimedTnt tnt = new PrimedTnt(level, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, source);
            level.addFreshEntity(tnt);
            level.playSound((Entity)null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(source, GameEvent.PRIME_FUSE, pos);
            return true;
         }
      }

      return false;
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if (!itemStack.is(Items.FLINT_AND_STEEL) && !itemStack.is(Items.FIRE_CHARGE)) {
         return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
      } else {
         if (prime(level, pos, player)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            Item item = itemStack.getItem();
            if (itemStack.is(Items.FLINT_AND_STEEL)) {
               itemStack.hurtAndBreak(1, player, (EquipmentSlot)hand.asEquipmentSlot());
            } else {
               itemStack.consume(1, player);
            }

            player.awardStat(Stats.ITEM_USED.get(item));
         } else if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (!(Boolean)serverLevel.getGameRules().get(GameRules.TNT_EXPLODES)) {
               player.sendOverlayMessage(Component.translatable("block.minecraft.tnt.disabled"));
               return InteractionResult.PASS;
            }
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected void onProjectileHit(final Level level, final BlockState state, final BlockHitResult blockHit, final Projectile projectile) {
      if (level instanceof ServerLevel serverLevel) {
         BlockPos pos = blockHit.getBlockPos();
         Entity owner = projectile.getOwner();
         if (projectile.isOnFire() && projectile.mayInteract(serverLevel, pos)) {
            LivingEntity var10002;
            if (owner instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)owner;
               var10002 = livingEntity;
            } else {
               var10002 = null;
            }

            if (prime(level, pos, var10002)) {
               level.removeBlock(pos, false);
            }
         }
      }

   }

   public boolean dropFromExplosion(final Explosion explosion) {
      return false;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(UNSTABLE);
   }

   static {
      UNSTABLE = BlockStateProperties.UNSTABLE;
   }
}
