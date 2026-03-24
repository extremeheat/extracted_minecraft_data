package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public record SpawnParticlesEffect(ParticleOptions particle, PositionSource horizontalPosition, PositionSource verticalPosition, VelocitySource horizontalVelocity, VelocitySource verticalVelocity, FloatProvider speed) implements EnchantmentEntityEffect {
   public static final MapCodec<SpawnParticlesEffect> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ParticleTypes.CODEC.fieldOf("particle").forGetter(SpawnParticlesEffect::particle), SpawnParticlesEffect.PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEffect::horizontalPosition), SpawnParticlesEffect.PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEffect::verticalPosition), SpawnParticlesEffect.VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEffect::horizontalVelocity), SpawnParticlesEffect.VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEffect::verticalVelocity), FloatProviders.CODEC.optionalFieldOf("speed", ConstantFloat.ZERO).forGetter(SpawnParticlesEffect::speed)).apply(i, SpawnParticlesEffect::new));

   public SpawnParticlesEffect {
      super();
   }

   public static PositionSource offsetFromEntityPosition(final float offset) {
      return new PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, offset, 1.0F);
   }

   public static PositionSource inBoundingBox() {
      return new PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F);
   }

   public static VelocitySource movementScaled(final float scale) {
      return new VelocitySource(scale, ConstantFloat.ZERO);
   }

   public static VelocitySource fixedVelocity(final FloatProvider provider) {
      return new VelocitySource(0.0F, provider);
   }

   public void apply(final ServerLevel serverLevel, final int enchantmentLevel, final EnchantedItemInUse item, final Entity entity, final Vec3 position) {
      RandomSource random = entity.getRandom();
      Vec3 movement = entity.getKnownMovement();
      float bbWidth = entity.getBbWidth();
      float bbHeight = entity.getBbHeight();
      serverLevel.sendParticles(this.particle, this.horizontalPosition.getCoordinate(position.x(), position.x(), bbWidth, random), this.verticalPosition.getCoordinate(position.y(), position.y() + (double)(bbHeight / 2.0F), bbHeight, random), this.horizontalPosition.getCoordinate(position.z(), position.z(), bbWidth, random), 0, this.horizontalVelocity.getVelocity(movement.x(), random), this.verticalVelocity.getVelocity(movement.y(), random), this.horizontalVelocity.getVelocity(movement.z(), random), (double)this.speed.sample(random));
   }

   public MapCodec<SpawnParticlesEffect> codec() {
      return CODEC;
   }

   public static enum PositionSourceType implements StringRepresentable {
      ENTITY_POSITION("entity_position", (pos, center, bbSpan, random) -> pos),
      BOUNDING_BOX("in_bounding_box", (pos, center, bbSpan, random) -> center + (random.nextDouble() - 0.5) * (double)bbSpan);

      public static final Codec<PositionSourceType> CODEC = StringRepresentable.<PositionSourceType>fromEnum(PositionSourceType::values);
      private final String id;
      private final CoordinateSource source;

      private PositionSourceType(final String id, final CoordinateSource source) {
         this.id = id;
         this.source = source;
      }

      public double getCoordinate(final double position, final double center, final float boundingBoxSpan, final RandomSource random) {
         return this.source.getCoordinate(position, center, boundingBoxSpan, random);
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static PositionSourceType[] $values() {
         return new PositionSourceType[]{ENTITY_POSITION, BOUNDING_BOX};
      }

      @FunctionalInterface
      private interface CoordinateSource {
         double getCoordinate(double pos, double center, float boundingBoxSpan, RandomSource random);
      }
   }

   public static record PositionSource(PositionSourceType type, float offset, float scale) {
      public static final MapCodec<PositionSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SpawnParticlesEffect.PositionSourceType.CODEC.fieldOf("type").forGetter(PositionSource::type), Codec.FLOAT.optionalFieldOf("offset", 0.0F).forGetter(PositionSource::offset), ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1.0F).forGetter(PositionSource::scale)).apply(i, PositionSource::new)).validate((positioning) -> positioning.type() == SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION && positioning.scale() != 1.0F ? DataResult.error(() -> "Cannot scale an entity position coordinate source") : DataResult.success(positioning));

      public PositionSource {
         super();
      }

      public double getCoordinate(final double position, final double center, final float boundingBoxSpan, final RandomSource random) {
         return this.type.getCoordinate(position, center, boundingBoxSpan * this.scale, random) + (double)this.offset;
      }
   }

   public static record VelocitySource(float movementScale, FloatProvider base) {
      public static final MapCodec<VelocitySource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.optionalFieldOf("movement_scale", 0.0F).forGetter(VelocitySource::movementScale), FloatProviders.CODEC.optionalFieldOf("base", ConstantFloat.ZERO).forGetter(VelocitySource::base)).apply(i, VelocitySource::new));

      public VelocitySource {
         super();
      }

      public double getVelocity(final double movement, final RandomSource random) {
         return movement * (double)this.movementScale + (double)this.base.sample(random);
      }
   }
}
