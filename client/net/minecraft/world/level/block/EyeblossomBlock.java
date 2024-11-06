package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EyeblossomBlock extends FlowerBlock {
   public static final MapCodec<EyeblossomBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.BOOL.fieldOf("open").forGetter((var0x) -> {
         return var0x.type.open;
      }), propertiesCodec()).apply(var0, EyeblossomBlock::new);
   });
   private static final int EYEBLOSSOM_XZ_RANGE = 3;
   private static final int EYEBLOSSOM_Y_RANGE = 2;
   private final Type type;

   public MapCodec<? extends EyeblossomBlock> codec() {
      return CODEC;
   }

   public EyeblossomBlock(Type var1, BlockBehaviour.Properties var2) {
      super(var1.effect, var1.effectDuration, var2);
      this.type = var1;
   }

   public EyeblossomBlock(boolean var1, BlockBehaviour.Properties var2) {
      super(EyeblossomBlock.Type.fromBoolean(var1).effect, EyeblossomBlock.Type.fromBoolean(var1).effectDuration, var2);
      this.type = EyeblossomBlock.Type.fromBoolean(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (this.type.emitSounds() && var4.nextInt(700) == 0) {
         BlockState var5 = var2.getBlockState(var3.below());
         if (var5.is(Blocks.PALE_MOSS_BLOCK)) {
            var2.playLocalSound((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), SoundEvents.EYEBLOSSOM_IDLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }
      }

   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.tryChangingState(var1, var2, var3, var4)) {
         var2.playSound((Player)null, var3, this.type.transform().longSwitchSound, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      super.randomTick(var1, var2, var3, var4);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.tryChangingState(var1, var2, var3, var4)) {
         var2.playSound((Player)null, var3, this.type.transform().shortSwitchSound, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      super.tick(var1, var2, var3, var4);
   }

   private boolean tryChangingState(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var2.dimensionType().natural()) {
         return false;
      } else if (var2.isDay() != this.type.open) {
         return false;
      } else {
         Type var5 = this.type.transform();
         var2.setBlock(var3, var5.state(), 3);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1));
         var5.spawnTransformParticle(var2, var3, var4);
         BlockPos.betweenClosed(var3.offset(-3, -2, -3), var3.offset(3, 2, 3)).forEach((var4x) -> {
            BlockState var5 = var2.getBlockState(var4x);
            if (var5 == var1) {
               double var6 = Math.sqrt(var3.distSqr(var4x));
               int var8 = var4.nextIntBetweenInclusive((int)(var6 * 5.0), (int)(var6 * 10.0));
               var2.scheduleTick(var4x, var1.getBlock(), var8);
            }

         });
         return true;
      }
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide() && var2.getDifficulty() != Difficulty.PEACEFUL && var4 instanceof Bee var5) {
         if (!var5.hasEffect(MobEffects.POISON)) {
            var5.addEffect(this.getBeeInteractionEffect());
         }
      }

   }

   public MobEffectInstance getBeeInteractionEffect() {
      return new MobEffectInstance(MobEffects.POISON, 25);
   }

   public static enum Type {
      OPEN(true, MobEffects.BLINDNESS, 7.0F, SoundEvents.EYEBLOSSOM_OPEN_LONG, SoundEvents.EYEBLOSSOM_OPEN, 16545810),
      CLOSED(false, MobEffects.CONFUSION, 7.0F, SoundEvents.EYEBLOSSOM_CLOSE_LONG, SoundEvents.EYEBLOSSOM_CLOSE, 6250335);

      final boolean open;
      final Holder<MobEffect> effect;
      final float effectDuration;
      final SoundEvent longSwitchSound;
      final SoundEvent shortSwitchSound;
      private final int particleColor;

      private Type(final boolean var3, final Holder var4, final float var5, final SoundEvent var6, final SoundEvent var7, final int var8) {
         this.open = var3;
         this.effect = var4;
         this.effectDuration = var5;
         this.longSwitchSound = var6;
         this.shortSwitchSound = var7;
         this.particleColor = var8;
      }

      public BlockState oppositeState() {
         return this == OPEN ? Blocks.CLOSED_EYEBLOSSOM.defaultBlockState() : Blocks.OPEN_EYEBLOSSOM.defaultBlockState();
      }

      public Block block() {
         return this.open ? Blocks.OPEN_EYEBLOSSOM : Blocks.CLOSED_EYEBLOSSOM;
      }

      public BlockState state() {
         return this.block().defaultBlockState();
      }

      public Type transform() {
         return fromBoolean(!this.open);
      }

      public boolean emitSounds() {
         return this.open;
      }

      public static Type fromBoolean(boolean var0) {
         return var0 ? OPEN : CLOSED;
      }

      public void spawnTransformParticle(ServerLevel var1, BlockPos var2, RandomSource var3) {
         Vec3 var4 = var2.getCenter();
         double var5 = 0.5 + var3.nextDouble();
         Vec3 var7 = new Vec3(var3.nextDouble() - 0.5, var3.nextDouble() + 1.0, var3.nextDouble() - 0.5);
         Vec3 var8 = var4.add(var7.scale(var5));
         TrailParticleOption var9 = new TrailParticleOption(var8, this.particleColor, (int)(20.0 * var5));
         var1.sendParticles(var9, var4.x, var4.y, var4.z, 1, 0.0, 0.0, 0.0, 0.0);
      }

      public SoundEvent longSwitchSound() {
         return this.longSwitchSound;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{OPEN, CLOSED};
      }
   }
}
