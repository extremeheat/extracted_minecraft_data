package net.minecraft.core.dispenser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public record ShootProjectileDispenseBehavior(Vec3 offset, double scale, float uncertainty, float power, Optional<DispenseSound> sound) implements DispenseBehavior {
   public static final ShootProjectileDispenseBehavior DEFAULT = builder().build();
   public static final float DEFAULT_UNCERTAINTY = 6.0F;
   public static final float DEFAULT_POWER = 1.1F;
   public static final MapCodec<ShootProjectileDispenseBehavior> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Vec3.CODEC.fieldOf("offset").forGetter(ShootProjectileDispenseBehavior::offset), Codec.DOUBLE.fieldOf("scale").forGetter(ShootProjectileDispenseBehavior::scale), ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("uncertainty").forGetter(ShootProjectileDispenseBehavior::uncertainty), ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("power").forGetter(ShootProjectileDispenseBehavior::power), ShootProjectileDispenseBehavior.DispenseSound.CODEC.optionalFieldOf("sound").forGetter(ShootProjectileDispenseBehavior::sound)).apply(i, ShootProjectileDispenseBehavior::new));
   private static final DispenseSound DEFAULT_SOUND;

   public ShootProjectileDispenseBehavior {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
      Position position = DispenserBlock.getDispensePosition(source, this.scale, this.offset);
      Item var7 = dispensed.getItem();
      if (var7 instanceof ProjectileItem projectileItem) {
         Projectile.spawnProjectileUsingShoot(projectileItem.asProjectile(level, position, dispensed, direction), level, dispensed, (double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ(), this.power, this.uncertainty);
      }

      dispensed.shrink(1);
      return DispenseBehavior.DispenseResult.success(dispensed);
   }

   public void playSuccessSound(final BlockSource source) {
      DispenseSound sound = (DispenseSound)this.sound.orElse(DEFAULT_SOUND);
      Direction direction = (Direction)source.state().getValueOrElse(DispenserBlock.FACING, Direction.UP);
      Vec3 pos = Vec3.atCenterOf(source.pos()).relative(direction, 0.5);
      RandomSource random = source.level().getRandom();
      source.level().playSound((Entity)null, pos.x(), pos.y(), pos.z(), sound.sound(), SoundSource.BLOCKS, sound.volume().sample(random), sound.pitch().sample(random));
   }

   public static Builder builder() {
      return new Builder();
   }

   public MapCodec<ShootProjectileDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      DEFAULT_SOUND = new DispenseSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.2F);
   }

   public static record DispenseSound(Holder<SoundEvent> sound, FloatProvider volume, FloatProvider pitch) {
      public static final Codec<DispenseSound> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("sound").forGetter(DispenseSound::sound), FloatProviders.CODEC.fieldOf("volume").forGetter(DispenseSound::volume), FloatProviders.CODEC.fieldOf("pitch").forGetter(DispenseSound::pitch)).apply(i, DispenseSound::new));

      public DispenseSound(final Holder<SoundEvent> sound, final float volume, final float pitch) {
         this(sound, ConstantFloat.of(volume), ConstantFloat.of(pitch));
      }

      public DispenseSound {
         super();
      }
   }

   public static class Builder {
      private Vec3 offset = new Vec3(0.0, 0.1, 0.0);
      private double scale = 0.699999988079071;
      private float uncertainty = 6.0F;
      private float power = 1.1F;
      private Optional<DispenseSound> sound = Optional.empty();

      public Builder() {
         super();
      }

      public Builder position(final Vec3 offset, final double scale) {
         this.offset = offset;
         this.scale = scale;
         return this;
      }

      public Builder positionJustOutside() {
         return this.position(Vec3.ZERO, 0.5000099999997474);
      }

      public Builder uncertainty(final float uncertainty) {
         this.uncertainty = uncertainty;
         return this;
      }

      public Builder power(final float power) {
         this.power = power;
         return this;
      }

      public Builder sound(final DispenseSound sound) {
         this.sound = Optional.of(sound);
         return this;
      }

      public ShootProjectileDispenseBehavior build() {
         return new ShootProjectileDispenseBehavior(this.offset, this.scale, this.uncertainty, this.power, this.sound);
      }
   }
}
