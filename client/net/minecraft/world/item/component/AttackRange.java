package net.minecraft.world.item.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record AttackRange(float minRange, float maxRange, float minCreativeRange, float maxCreativeRange, float hitboxMargin, float mobFactor) {
   public static final Codec<AttackRange> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("min_reach", 0.0F).forGetter(AttackRange::minRange), ExtraCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("max_reach", 3.0F).forGetter(AttackRange::maxRange), ExtraCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("min_creative_reach", 0.0F).forGetter(AttackRange::minCreativeRange), ExtraCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("max_creative_reach", 5.0F).forGetter(AttackRange::maxCreativeRange), ExtraCodecs.floatRange(0.0F, 1.0F).optionalFieldOf("hitbox_margin", 0.3F).forGetter(AttackRange::hitboxMargin), Codec.floatRange(0.0F, 2.0F).optionalFieldOf("mob_factor", 1.0F).forGetter(AttackRange::mobFactor)).apply(var0, AttackRange::new));
   public static final StreamCodec<ByteBuf, AttackRange> STREAM_CODEC;

   public AttackRange(float var1, float var2, float var3, float var4, float var5, float var6) {
      super();
      this.minRange = var1;
      this.maxRange = var2;
      this.minCreativeRange = var3;
      this.maxCreativeRange = var4;
      this.hitboxMargin = var5;
      this.mobFactor = var6;
   }

   public static AttackRange defaultFor(LivingEntity var0) {
      return new AttackRange(0.0F, (float)var0.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), 0.0F, (float)var0.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), 0.0F, 1.0F);
   }

   public HitResult getClosesetHit(Entity var1, float var2, Predicate<Entity> var3) {
      Either var4 = ProjectileUtil.getHitEntitiesAlong(var1, this, var3, ClipContext.Block.OUTLINE);
      if (var4.left().isPresent()) {
         return (HitResult)var4.left().get();
      } else {
         Collection var5 = (Collection)var4.right().get();
         EntityHitResult var6 = null;
         Vec3 var7 = var1.getEyePosition(var2);
         double var8 = 1.7976931348623157E308;

         for(EntityHitResult var11 : var5) {
            double var12 = var7.distanceToSqr(var11.getLocation());
            if (var12 < var8) {
               var8 = var12;
               var6 = var11;
            }
         }

         if (var6 != null) {
            return var6;
         } else {
            Vec3 var14 = var1.getHeadLookAngle();
            Vec3 var15 = var1.getEyePosition(var2).add(var14);
            return BlockHitResult.miss(var15, Direction.getApproximateNearest(var14), BlockPos.containing(var15));
         }
      }
   }

   public float effectiveMinRange(Entity var1) {
      if (var1 instanceof Player var2) {
         return var2.isCreative() ? this.minCreativeRange : this.minRange;
      } else {
         return this.minRange * this.mobFactor;
      }
   }

   public float effectiveMaxRange(Entity var1) {
      if (var1 instanceof Player var2) {
         return var2.isCreative() ? this.maxCreativeRange : this.maxRange;
      } else {
         return this.maxRange * this.mobFactor;
      }
   }

   public boolean isInRange(LivingEntity var1, Vec3 var2) {
      Objects.requireNonNull(var2);
      return this.isInRange(var1, var2::distanceToSqr, 0.0);
   }

   public boolean isInRange(LivingEntity var1, AABB var2, double var3) {
      Objects.requireNonNull(var2);
      return this.isInRange(var1, var2::distanceToSqr, var3);
   }

   private boolean isInRange(LivingEntity var1, ToDoubleFunction<Vec3> var2, double var3) {
      double var5 = Math.sqrt(var2.applyAsDouble(var1.getEyePosition()));
      double var7 = (double)(this.effectiveMinRange(var1) - this.hitboxMargin) - var3;
      double var9 = (double)(this.effectiveMaxRange(var1) + this.hitboxMargin) + var3;
      return var5 >= var7 && var5 <= var9;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, AttackRange::minRange, ByteBufCodecs.FLOAT, AttackRange::maxRange, ByteBufCodecs.FLOAT, AttackRange::minCreativeRange, ByteBufCodecs.FLOAT, AttackRange::maxCreativeRange, ByteBufCodecs.FLOAT, AttackRange::hitboxMargin, ByteBufCodecs.FLOAT, AttackRange::mobFactor, AttackRange::new);
   }
}
