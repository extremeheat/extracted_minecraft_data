package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BrushItem extends Item {
   public static final int ANIMATION_DURATION = 10;
   private static final int USE_DURATION = 200;

   public BrushItem(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Player player = context.getPlayer();
      if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
         player.startUsingItem(context.getHand());
      }

      return InteractionResult.CONSUME;
   }

   public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
      return ItemUseAnimation.BRUSH;
   }

   public int getUseDuration(final ItemStack itemStack, final LivingEntity user) {
      return 200;
   }

   public void onUseTick(final Level level, final LivingEntity livingEntity, final ItemStack itemStack, final int ticksRemaining) {
      if (ticksRemaining >= 0 && livingEntity instanceof Player player) {
         HitResult hitResult = this.calculateHitResult(player);
         if (hitResult instanceof BlockHitResult blockHitResult) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
               int timeElapsed = this.getUseDuration(itemStack, livingEntity) - ticksRemaining + 1;
               boolean isLastTickBeforeBackswing = timeElapsed % 10 == 5;
               if (isLastTickBeforeBackswing) {
                  BlockPos pos = blockHitResult.getBlockPos();
                  BlockState state = level.getBlockState(pos);
                  HumanoidArm brushingArm = livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                  if (state.shouldSpawnTerrainParticles() && state.getRenderShape() != RenderShape.INVISIBLE) {
                     this.spawnDustParticles(level, blockHitResult, state, livingEntity.getViewVector(0.0F), brushingArm);
                  }

                  Block var15 = state.getBlock();
                  SoundEvent brushSound;
                  if (var15 instanceof BrushableBlock) {
                     BrushableBlock brushableBlock = (BrushableBlock)var15;
                     brushSound = brushableBlock.getBrushSound();
                  } else {
                     brushSound = SoundEvents.BRUSH_GENERIC;
                  }

                  level.playSound(player, pos, brushSound, SoundSource.BLOCKS);
                  if (level instanceof ServerLevel) {
                     ServerLevel serverLevel = (ServerLevel)level;
                     BlockEntity var16 = level.getBlockEntity(pos);
                     if (var16 instanceof BrushableBlockEntity) {
                        BrushableBlockEntity brushableBlockEntity = (BrushableBlockEntity)var16;
                        boolean brushingUpdatedState = brushableBlockEntity.brush(level.getGameTime(), serverLevel, player, blockHitResult.getDirection(), itemStack);
                        if (brushingUpdatedState) {
                           EquipmentSlot equippedHand = itemStack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                           itemStack.hurtAndBreak(1, player, (EquipmentSlot)equippedHand);
                        }
                     }
                  }
               }

               return;
            }
         }

         livingEntity.releaseUsingItem();
      } else {
         livingEntity.releaseUsingItem();
      }
   }

   private HitResult calculateHitResult(final Player player) {
      return ProjectileUtil.getHitResultOnViewVector(player, EntitySelector.CAN_BE_PICKED, player.blockInteractionRange());
   }

   private void spawnDustParticles(final Level level, final BlockHitResult hitResult, final BlockState state, final Vec3 viewVector, final HumanoidArm brushingArm) {
      double deltaScale = 3.0;
      int flip = brushingArm == HumanoidArm.RIGHT ? 1 : -1;
      int particles = level.getRandom().nextInt(7, 12);
      BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
      Direction hitDirection = hitResult.getDirection();
      DustParticlesDelta dustParticlesDelta = BrushItem.DustParticlesDelta.fromDirection(viewVector, hitDirection);
      Vec3 hitLocation = hitResult.getLocation();

      for(int i = 0; i < particles; ++i) {
         level.addParticle(particle, hitLocation.x - (double)(hitDirection == Direction.WEST ? 1.0E-6F : 0.0F), hitLocation.y, hitLocation.z - (double)(hitDirection == Direction.NORTH ? 1.0E-6F : 0.0F), dustParticlesDelta.xd() * (double)flip * 3.0 * level.getRandom().nextDouble(), 0.0, dustParticlesDelta.zd() * (double)flip * 3.0 * level.getRandom().nextDouble());
      }

   }

   private static record DustParticlesDelta(double xd, double yd, double zd) {
      private static final double ALONG_SIDE_DELTA = 1.0;
      private static final double OUT_FROM_SIDE_DELTA = 0.1;

      private DustParticlesDelta {
         super();
      }

      public static DustParticlesDelta fromDirection(final Vec3 viewVector, final Direction hitDirection) {
         double yd = 0.0;
         DustParticlesDelta var10000;
         switch (hitDirection) {
            case DOWN:
            case UP:
               var10000 = new DustParticlesDelta(viewVector.z(), 0.0, -viewVector.x());
               break;
            case NORTH:
               var10000 = new DustParticlesDelta(1.0, 0.0, -0.1);
               break;
            case SOUTH:
               var10000 = new DustParticlesDelta(-1.0, 0.0, 0.1);
               break;
            case WEST:
               var10000 = new DustParticlesDelta(-0.1, 0.0, -1.0);
               break;
            case EAST:
               var10000 = new DustParticlesDelta(0.1, 0.0, 1.0);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}
