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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public record SpawnParticlesEffect(
   ParticleOptions particle,
   SpawnParticlesEffect.PositionSource horizontalPosition,
   SpawnParticlesEffect.PositionSource verticalPosition,
   SpawnParticlesEffect.VelocitySource horizontalVelocity,
   SpawnParticlesEffect.VelocitySource verticalVelocity,
   FloatProvider speed
) implements EnchantmentEntityEffect {
   public static final MapCodec<SpawnParticlesEffect> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ParticleTypes.CODEC.fieldOf("particle").forGetter(SpawnParticlesEffect::particle),
               SpawnParticlesEffect.PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEffect::horizontalPosition),
               SpawnParticlesEffect.PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEffect::verticalPosition),
               SpawnParticlesEffect.VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEffect::horizontalVelocity),
               SpawnParticlesEffect.VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEffect::verticalVelocity),
               FloatProvider.CODEC.optionalFieldOf("speed", ConstantFloat.ZERO).forGetter(SpawnParticlesEffect::speed)
            )
            .apply(var0, SpawnParticlesEffect::new)
   );

   public SpawnParticlesEffect(
      ParticleOptions particle,
      SpawnParticlesEffect.PositionSource horizontalPosition,
      SpawnParticlesEffect.PositionSource verticalPosition,
      SpawnParticlesEffect.VelocitySource horizontalVelocity,
      SpawnParticlesEffect.VelocitySource verticalVelocity,
      FloatProvider speed
   ) {
      super();
      this.particle = particle;
      this.horizontalPosition = horizontalPosition;
      this.verticalPosition = verticalPosition;
      this.horizontalVelocity = horizontalVelocity;
      this.verticalVelocity = verticalVelocity;
      this.speed = speed;
   }

   public static SpawnParticlesEffect.PositionSource offsetFromEntityPosition(float var0) {
      return new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, var0, 1.0F);
   }

   public static SpawnParticlesEffect.PositionSource inBoundingBox() {
      return new SpawnParticlesEffect.PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F);
   }

   public static SpawnParticlesEffect.VelocitySource movementScaled(float var0) {
      return new SpawnParticlesEffect.VelocitySource(var0, ConstantFloat.ZERO);
   }

   public static SpawnParticlesEffect.VelocitySource fixedVelocity(FloatProvider var0) {
      return new SpawnParticlesEffect.VelocitySource(0.0F, var0);
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      RandomSource var6 = var4.getRandom();
      Vec3 var7 = var4.getDeltaMovement();
      float var8 = var4.getBbWidth();
      float var9 = var4.getBbHeight();
      var1.sendParticles(
         this.particle,
         this.horizontalPosition.getCoordinate(var5.x(), var8, var6),
         this.verticalPosition.getCoordinate(var5.y(), var9, var6),
         this.horizontalPosition.getCoordinate(var5.z(), var8, var6),
         0,
         this.horizontalVelocity.getVelocity(var7.x(), var6),
         this.verticalVelocity.getVelocity(var7.y(), var6),
         this.horizontalVelocity.getVelocity(var7.z(), var6),
         (double)this.speed.sample(var6)
      );
   }

   @Override
   public MapCodec<SpawnParticlesEffect> codec() {
      return CODEC;
   }

   public static record PositionSource(SpawnParticlesEffect.PositionSourceType type, float offset, float scale) {
      public static final MapCodec<SpawnParticlesEffect.PositionSource> CODEC = RecordCodecBuilder.mapCodec(
            var0 -> var0.group(
                     SpawnParticlesEffect.PositionSourceType.CODEC.fieldOf("type").forGetter(SpawnParticlesEffect.PositionSource::type),
                     Codec.FLOAT.optionalFieldOf("offset", 0.0F).forGetter(SpawnParticlesEffect.PositionSource::offset),
                     ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1.0F).forGetter(SpawnParticlesEffect.PositionSource::scale)
                  )
                  .apply(var0, SpawnParticlesEffect.PositionSource::new)
         )
         .validate(
            var0 -> var0.type() == SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION && var0.scale() != 1.0F
                  ? DataResult.error(() -> "Cannot scale an entity position coordinate source")
                  : DataResult.success(var0)
         );

      public PositionSource(SpawnParticlesEffect.PositionSourceType type, float offset, float scale) {
         super();
         this.type = type;
         this.offset = offset;
         this.scale = scale;
      }

      public double getCoordinate(double var1, float var3, RandomSource var4) {
         return this.type.getCoordinate(var1, var3 * this.scale, var4) + (double)this.offset;
      }
   }

   public static enum PositionSourceType implements StringRepresentable {
      ENTITY_POSITION("entity_position", (var0, var2, var3) -> var0),
      BOUNDING_BOX("in_bounding_box", (var0, var2, var3) -> var0 + (var3.nextDouble() - 0.5) * (double)var2);

      public static final Codec<SpawnParticlesEffect.PositionSourceType> CODEC = StringRepresentable.fromEnum(SpawnParticlesEffect.PositionSourceType::values);
      private final String id;
      private final SpawnParticlesEffect.PositionSourceType.CoordinateSource source;

      private PositionSourceType(final String nullxx, final SpawnParticlesEffect.PositionSourceType.CoordinateSource nullxxx) {
         this.id = nullxx;
         this.source = nullxxx;
      }

      public double getCoordinate(double var1, float var3, RandomSource var4) {
         return this.source.getCoordinate(var1, var3, var4);
      }

      @Override
      public String getSerializedName() {
         return this.id;
      }

      @FunctionalInterface
      interface CoordinateSource {
         double getCoordinate(double var1, float var3, RandomSource var4);
      }
   }

   public static record VelocitySource(float movementScale, FloatProvider base) {
      public static final MapCodec<SpawnParticlesEffect.VelocitySource> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.FLOAT.optionalFieldOf("movement_scale", 0.0F).forGetter(SpawnParticlesEffect.VelocitySource::movementScale),
                  FloatProvider.CODEC.optionalFieldOf("base", ConstantFloat.ZERO).forGetter(SpawnParticlesEffect.VelocitySource::base)
               )
               .apply(var0, SpawnParticlesEffect.VelocitySource::new)
      );

      public VelocitySource(float movementScale, FloatProvider base) {
         super();
         this.movementScale = movementScale;
         this.base = base;
      }

      public double getVelocity(double var1, RandomSource var3) {
         return var1 * (double)this.movementScale + (double)this.base.sample(var3);
      }
   }
}
