package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BrushItem extends Item {
   public static final int TICKS_BETWEEN_SWEEPS = 10;
   private static final int USE_DURATION = 225;

   public BrushItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      if (var2 != null) {
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
      return 225;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
      if (var4 >= 0 && var2 instanceof Player var5) {
         BlockHitResult var6 = Item.getPlayerPOVHitResult(var1, (Player)var5, ClipContext.Fluid.NONE);
         BlockPos var7 = var6.getBlockPos();
         if (var6.getType() == HitResult.Type.MISS) {
            var2.releaseUsingItem();
         } else {
            int var8 = this.getUseDuration(var3) - var4 + 1;
            if (var8 == 1 || var8 % 10 == 0) {
               BlockState var9 = var1.getBlockState(var7);
               this.spawnDustParticles(var1, var6, var9, var2.getViewVector(0.0F));
               var1.playSound((Player)var5, var7, SoundEvents.BRUSH_BRUSHING, SoundSource.PLAYERS);
               if (!var1.isClientSide() && var9.is(Blocks.SUSPICIOUS_SAND)) {
                  BlockEntity var11 = var1.getBlockEntity(var7);
                  if (var11 instanceof SuspiciousSandBlockEntity var10) {
                     boolean var12 = var10.brush(var1.getGameTime(), (Player)var5, var6.getDirection());
                     if (var12) {
                        var3.hurtAndBreak(1, var2, var0 -> var0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                     }
                  }
               }
            }
         }
      } else {
         var2.releaseUsingItem();
      }
   }

   public void spawnDustParticles(Level var1, BlockHitResult var2, BlockState var3, Vec3 var4) {
      double var5 = 3.0;
      int var7 = var1.getRandom().nextInt(7, 12);
      BlockParticleOption var8 = new BlockParticleOption(ParticleTypes.BLOCK, var3);
      Direction var9 = var2.getDirection();
      BrushItem.DustParticlesDelta var10 = BrushItem.DustParticlesDelta.fromDirection(var4, var9);
      Vec3 var11 = var2.getLocation();

      for(int var12 = 0; var12 < var7; ++var12) {
         var1.addParticle(
            var8,
            var11.x - (double)(var9 == Direction.WEST ? 1.0E-6F : 0.0F),
            var11.y,
            var11.z - (double)(var9 == Direction.NORTH ? 1.0E-6F : 0.0F),
            var10.xd() * 3.0 * var1.getRandom().nextDouble(),
            0.0,
            var10.zd() * 3.0 * var1.getRandom().nextDouble()
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
            case DOWN -> new BrushItem.DustParticlesDelta(-var0.x(), 0.0, var0.z());
            case UP -> new BrushItem.DustParticlesDelta(var0.z(), 0.0, -var0.x());
            case NORTH -> new BrushItem.DustParticlesDelta(1.0, 0.0, -0.1);
            case SOUTH -> new BrushItem.DustParticlesDelta(-1.0, 0.0, 0.1);
            case WEST -> new BrushItem.DustParticlesDelta(-0.1, 0.0, -1.0);
            case EAST -> new BrushItem.DustParticlesDelta(0.1, 0.0, 1.0);
         };
      }
   }
}
