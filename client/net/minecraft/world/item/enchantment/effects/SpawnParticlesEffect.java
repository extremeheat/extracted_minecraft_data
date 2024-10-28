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

public record SpawnParticlesEffect(ParticleOptions particle, PositionSource horizontalPosition, PositionSource verticalPosition, VelocitySource horizontalVelocity, VelocitySource verticalVelocity, FloatProvider speed) implements EnchantmentEntityEffect {
   public static final MapCodec<SpawnParticlesEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ParticleTypes.CODEC.fieldOf("particle").forGetter(SpawnParticlesEffect::particle), SpawnParticlesEffect.PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEffect::horizontalPosition), SpawnParticlesEffect.PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEffect::verticalPosition), SpawnParticlesEffect.VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEffect::horizontalVelocity), SpawnParticlesEffect.VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEffect::verticalVelocity), FloatProvider.CODEC.optionalFieldOf("speed", ConstantFloat.ZERO).forGetter(SpawnParticlesEffect::speed)).apply(var0, SpawnParticlesEffect::new);
   });

   public SpawnParticlesEffect(ParticleOptions particle, PositionSource horizontalPosition, PositionSource verticalPosition, VelocitySource horizontalVelocity, VelocitySource verticalVelocity, FloatProvider speed) {
      super();
      this.particle = particle;
      this.horizontalPosition = horizontalPosition;
      this.verticalPosition = verticalPosition;
      this.horizontalVelocity = horizontalVelocity;
      this.verticalVelocity = verticalVelocity;
      this.speed = speed;
   }

   public static PositionSource offsetFromEntityPosition(float var0) {
      return new PositionSource(SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION, var0, 1.0F);
   }

   public static PositionSource inBoundingBox() {
      return new PositionSource(SpawnParticlesEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F);
   }

   public static VelocitySource movementScaled(float var0) {
      return new VelocitySource(var0, ConstantFloat.ZERO);
   }

   public static VelocitySource fixedVelocity(FloatProvider var0) {
      return new VelocitySource(0.0F, var0);
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      RandomSource var6 = var4.getRandom();
      Vec3 var7 = var4.getDeltaMovement();
      float var8 = var4.getBbWidth();
      float var9 = var4.getBbHeight();
      var1.sendParticles(this.particle, this.horizontalPosition.getCoordinate(var5.x(), var8, var6), this.verticalPosition.getCoordinate(var5.y(), var9, var6), this.horizontalPosition.getCoordinate(var5.z(), var8, var6), 0, this.horizontalVelocity.getVelocity(var7.x(), var6), this.verticalVelocity.getVelocity(var7.y(), var6), this.horizontalVelocity.getVelocity(var7.z(), var6), (double)this.speed.sample(var6));
   }

   public MapCodec<SpawnParticlesEffect> codec() {
      return CODEC;
   }

   public ParticleOptions particle() {
      return this.particle;
   }

   public PositionSource horizontalPosition() {
      return this.horizontalPosition;
   }

   public PositionSource verticalPosition() {
      return this.verticalPosition;
   }

   public VelocitySource horizontalVelocity() {
      return this.horizontalVelocity;
   }

   public VelocitySource verticalVelocity() {
      return this.verticalVelocity;
   }

   public FloatProvider speed() {
      return this.speed;
   }

   public static record PositionSource(PositionSourceType type, float offset, float scale) {
      public static final MapCodec<PositionSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(SpawnParticlesEffect.PositionSourceType.CODEC.fieldOf("type").forGetter(PositionSource::type), Codec.FLOAT.optionalFieldOf("offset", 0.0F).forGetter(PositionSource::offset), ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1.0F).forGetter(PositionSource::scale)).apply(var0, PositionSource::new);
      }).validate((var0) -> {
         return var0.type() == SpawnParticlesEffect.PositionSourceType.ENTITY_POSITION && var0.scale() != 1.0F ? DataResult.error(() -> {
            return "Cannot scale an entity position coordinate source";
         }) : DataResult.success(var0);
      });

      public PositionSource(PositionSourceType type, float offset, float scale) {
         super();
         this.type = type;
         this.offset = offset;
         this.scale = scale;
      }

      public double getCoordinate(double var1, float var3, RandomSource var4) {
         return this.type.getCoordinate(var1, var3 * this.scale, var4) + (double)this.offset;
      }

      public PositionSourceType type() {
         return this.type;
      }

      public float offset() {
         return this.offset;
      }

      public float scale() {
         return this.scale;
      }
   }

   public static record VelocitySource(float movementScale, FloatProvider base) {
      public static final MapCodec<VelocitySource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.FLOAT.optionalFieldOf("movement_scale", 0.0F).forGetter(VelocitySource::movementScale), FloatProvider.CODEC.optionalFieldOf("base", ConstantFloat.ZERO).forGetter(VelocitySource::base)).apply(var0, VelocitySource::new);
      });

      public VelocitySource(float movementScale, FloatProvider base) {
         super();
         this.movementScale = movementScale;
         this.base = base;
      }

      public double getVelocity(double var1, RandomSource var3) {
         return var1 * (double)this.movementScale + (double)this.base.sample(var3);
      }

      public float movementScale() {
         return this.movementScale;
      }

      public FloatProvider base() {
         return this.base;
      }
   }

   public static enum PositionSourceType implements StringRepresentable {
      ENTITY_POSITION("entity_position", (var0, var2, var3) -> {
         return var0;
      }),
      BOUNDING_BOX("in_bounding_box", (var0, var2, var3) -> {
         return var0 + (var3.nextDouble() - 0.5) * (double)var2;
      });

      public static final Codec<PositionSourceType> CODEC = StringRepresentable.fromEnum(PositionSourceType::values);
      private final String id;
      private final CoordinateSource source;

      private PositionSourceType(final String var3, final CoordinateSource var4) {
         this.id = var3;
         this.source = var4;
      }

      public double getCoordinate(double var1, float var3, RandomSource var4) {
         return this.source.getCoordinate(var1, var3, var4);
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static PositionSourceType[] $values() {
         return new PositionSourceType[]{ENTITY_POSITION, BOUNDING_BOX};
      }

      @FunctionalInterface
      interface CoordinateSource {
         double getCoordinate(double var1, float var3, RandomSource var4);
      }
   }
}
