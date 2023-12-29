package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

   public BrushItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      if (var2 != null && this.calculateHitResult(var2).getType() == HitResult.Type.BLOCK) {
         var2.startUsingItem(var1.getHand());
      }

      return InteractionResult.CONSUME;
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.BRUSH;
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      return 200;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
      if (var4 >= 0 && var2 instanceof Player var5) {
         HitResult var6 = this.calculateHitResult((Player)var5);
         if (var6 instanceof BlockHitResult var7 && var6.getType() == HitResult.Type.BLOCK) {
            int var8 = this.getUseDuration(var3) - var4 + 1;
            boolean var9 = var8 % 10 == 5;
            if (var9) {
               BlockPos var10 = ((BlockHitResult)var7).getBlockPos();
               BlockState var11 = var1.getBlockState(var10);
               HumanoidArm var12 = var2.getUsedItemHand() == InteractionHand.MAIN_HAND
                  ? ((Player)var5).getMainArm()
                  : ((Player)var5).getMainArm().getOpposite();
               if (var11.shouldSpawnTerrainParticles() && var11.getRenderShape() != RenderShape.INVISIBLE) {
                  this.spawnDustParticles(var1, (BlockHitResult)var7, var11, var2.getViewVector(0.0F), var12);
               }

               Block var15 = var11.getBlock();
               SoundEvent var13;
               if (var15 instanceof BrushableBlock var14) {
                  var13 = var14.getBrushSound();
               } else {
                  var13 = SoundEvents.BRUSH_GENERIC;
               }

               var1.playSound((Player)var5, var10, var13, SoundSource.BLOCKS);
               if (!var1.isClientSide()) {
                  BlockEntity var18 = var1.getBlockEntity(var10);
                  if (var18 instanceof BrushableBlockEntity var17) {
                     boolean var19 = var17.brush(var1.getGameTime(), (Player)var5, ((BlockHitResult)var7).getDirection());
                     if (var19) {
                        EquipmentSlot var16 = var3.equals(((Player)var5).getItemBySlot(EquipmentSlot.OFFHAND))
                           ? EquipmentSlot.OFFHAND
                           : EquipmentSlot.MAINHAND;
                        var3.hurtAndBreak(1, var2, var1x -> var1x.broadcastBreakEvent(var16));
                     }
                  }
               }
            }

            return;
         }

         var2.releaseUsingItem();
      } else {
         var2.releaseUsingItem();
      }
   }

   private HitResult calculateHitResult(Player var1) {
      return ProjectileUtil.getHitResultOnViewVector(var1, var0 -> !var0.isSpectator() && var0.isPickable(), (double)Player.getPickRange(var1.isCreative()));
   }

   private void spawnDustParticles(Level var1, BlockHitResult var2, BlockState var3, Vec3 var4, HumanoidArm var5) {
      double var6 = 3.0;
      int var8 = var5 == HumanoidArm.RIGHT ? 1 : -1;
      int var9 = var1.getRandom().nextInt(7, 12);
      BlockParticleOption var10 = new BlockParticleOption(ParticleTypes.BLOCK, var3);
      Direction var11 = var2.getDirection();
      BrushItem.DustParticlesDelta var12 = BrushItem.DustParticlesDelta.fromDirection(var4, var11);
      Vec3 var13 = var2.getLocation();

      for(int var14 = 0; var14 < var9; ++var14) {
         var1.addParticle(
            var10,
            var13.x - (double)(var11 == Direction.WEST ? 1.0E-6F : 0.0F),
            var13.y,
            var13.z - (double)(var11 == Direction.NORTH ? 1.0E-6F : 0.0F),
            var12.xd() * (double)var8 * 3.0 * var1.getRandom().nextDouble(),
            0.0,
            var12.zd() * (double)var8 * 3.0 * var1.getRandom().nextDouble()
         );
      }
   }

   static record DustParticlesDelta(double a, double b, double c) {
      private final double xd;
      private final double yd;
      private final double zd;
      private static final double ALONG_SIDE_DELTA = 1.0;
      private static final double OUT_FROM_SIDE_DELTA = 0.1;

      private DustParticlesDelta(double var1, double var3, double var5) {
         super();
         this.xd = var1;
         this.yd = var3;
         this.zd = var5;
      }

      public static BrushItem.DustParticlesDelta fromDirection(Vec3 var0, Direction var1) {
         double var2 = 0.0;

         return switch(var1) {
            case DOWN, UP -> new BrushItem.DustParticlesDelta(var0.z(), 0.0, -var0.x());
            case NORTH -> new BrushItem.DustParticlesDelta(1.0, 0.0, -0.1);
            case SOUTH -> new BrushItem.DustParticlesDelta(-1.0, 0.0, 0.1);
            case WEST -> new BrushItem.DustParticlesDelta(-0.1, 0.0, -1.0);
            case EAST -> new BrushItem.DustParticlesDelta(0.1, 0.0, 1.0);
         };
      }
   }
}
