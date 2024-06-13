package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int INFINITE_DURATION = -1;
   public static final int MIN_AMPLIFIER = 0;
   public static final int MAX_AMPLIFIER = 255;
   public static final Codec<MobEffectInstance> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               MobEffect.CODEC.fieldOf("id").forGetter(MobEffectInstance::getEffect),
               MobEffectInstance.Details.MAP_CODEC.forGetter(MobEffectInstance::asDetails)
            )
            .apply(var0, MobEffectInstance::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance> STREAM_CODEC = StreamCodec.composite(
      MobEffect.STREAM_CODEC, MobEffectInstance::getEffect, MobEffectInstance.Details.STREAM_CODEC, MobEffectInstance::asDetails, MobEffectInstance::new
   );
   private final Holder<MobEffect> effect;
   private int duration;
   private int amplifier;
   private boolean ambient;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private MobEffectInstance hiddenEffect;
   private final MobEffectInstance.BlendState blendState = new MobEffectInstance.BlendState();

   public MobEffectInstance(Holder<MobEffect> var1) {
      this(var1, 0, 0);
   }

   public MobEffectInstance(Holder<MobEffect> var1, int var2) {
      this(var1, var2, 0);
   }

   public MobEffectInstance(Holder<MobEffect> var1, int var2, int var3) {
      this(var1, var2, var3, false, true);
   }

   public MobEffectInstance(Holder<MobEffect> var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, var5);
   }

   public MobEffectInstance(Holder<MobEffect> var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
      this(var1, var2, var3, var4, var5, var6, null);
   }

   public MobEffectInstance(Holder<MobEffect> var1, int var2, int var3, boolean var4, boolean var5, boolean var6, @Nullable MobEffectInstance var7) {
      super();
      this.effect = var1;
      this.duration = var2;
      this.amplifier = Mth.clamp(var3, 0, 255);
      this.ambient = var4;
      this.visible = var5;
      this.showIcon = var6;
      this.hiddenEffect = var7;
   }

   public MobEffectInstance(MobEffectInstance var1) {
      super();
      this.effect = var1.effect;
      this.setDetailsFrom(var1);
   }

   private MobEffectInstance(Holder<MobEffect> var1, MobEffectInstance.Details var2) {
      this(
         var1,
         var2.duration(),
         var2.amplifier(),
         var2.ambient(),
         var2.showParticles(),
         var2.showIcon(),
         var2.hiddenEffect().map(var1x -> new MobEffectInstance(var1, var1x)).orElse(null)
      );
   }

   private MobEffectInstance.Details asDetails() {
      return new MobEffectInstance.Details(
         this.getAmplifier(),
         this.getDuration(),
         this.isAmbient(),
         this.isVisible(),
         this.showIcon(),
         Optional.ofNullable(this.hiddenEffect).map(MobEffectInstance::asDetails)
      );
   }

   public float getBlendFactor(LivingEntity var1, float var2) {
      return this.blendState.getFactor(var1, var2);
   }

   public ParticleOptions getParticleOptions() {
      return this.effect.value().createParticleOptions(this);
   }

   void setDetailsFrom(MobEffectInstance var1) {
      this.duration = var1.duration;
      this.amplifier = var1.amplifier;
      this.ambient = var1.ambient;
      this.visible = var1.visible;
      this.showIcon = var1.showIcon;
   }

   public boolean update(MobEffectInstance var1) {
      if (!this.effect.equals(var1.effect)) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean var2 = false;
      if (var1.amplifier > this.amplifier) {
         if (var1.isShorterDurationThan(this)) {
            MobEffectInstance var3 = this.hiddenEffect;
            this.hiddenEffect = new MobEffectInstance(this);
            this.hiddenEffect.hiddenEffect = var3;
         }

         this.amplifier = var1.amplifier;
         this.duration = var1.duration;
         var2 = true;
      } else if (this.isShorterDurationThan(var1)) {
         if (var1.amplifier == this.amplifier) {
            this.duration = var1.duration;
            var2 = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new MobEffectInstance(var1);
         } else {
            this.hiddenEffect.update(var1);
         }
      }

      if (!var1.ambient && this.ambient || var2) {
         this.ambient = var1.ambient;
         var2 = true;
      }

      if (var1.visible != this.visible) {
         this.visible = var1.visible;
         var2 = true;
      }

      if (var1.showIcon != this.showIcon) {
         this.showIcon = var1.showIcon;
         var2 = true;
      }

      return var2;
   }

   private boolean isShorterDurationThan(MobEffectInstance var1) {
      return !this.isInfiniteDuration() && (this.duration < var1.duration || var1.isInfiniteDuration());
   }

   public boolean isInfiniteDuration() {
      return this.duration == -1;
   }

   public boolean endsWithin(int var1) {
      return !this.isInfiniteDuration() && this.duration <= var1;
   }

   public int mapDuration(Int2IntFunction var1) {
      return !this.isInfiniteDuration() && this.duration != 0 ? var1.applyAsInt(this.duration) : this.duration;
   }

   public Holder<MobEffect> getEffect() {
      return this.effect;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean showIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity var1, Runnable var2) {
      if (this.hasRemainingDuration()) {
         int var3 = this.isInfiniteDuration() ? var1.tickCount : this.duration;
         if (this.effect.value().shouldApplyEffectTickThisTick(var3, this.amplifier) && !this.effect.value().applyEffectTick(var1, this.amplifier)) {
            var1.removeEffect(this.effect);
         }

         this.tickDownDuration();
         if (this.duration == 0 && this.hiddenEffect != null) {
            this.setDetailsFrom(this.hiddenEffect);
            this.hiddenEffect = this.hiddenEffect.hiddenEffect;
            var2.run();
         }
      }

      this.blendState.tick(this);
      return this.hasRemainingDuration();
   }

   private boolean hasRemainingDuration() {
      return this.isInfiniteDuration() || this.duration > 0;
   }

   private int tickDownDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.tickDownDuration();
      }

      return this.duration = this.mapDuration(var0 -> var0 - 1);
   }

   public void onEffectStarted(LivingEntity var1) {
      this.effect.value().onEffectStarted(var1, this.amplifier);
   }

   public void onMobRemoved(LivingEntity var1, Entity.RemovalReason var2) {
      this.effect.value().onMobRemoved(var1, this.amplifier, var2);
   }

   public void onMobHurt(LivingEntity var1, DamageSource var2, float var3) {
      this.effect.value().onMobHurt(var1, this.amplifier, var2, var3);
   }

   public String getDescriptionId() {
      return this.effect.value().getDescriptionId();
   }

   @Override
   public String toString() {
      String var1;
      if (this.amplifier > 0) {
         var1 = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.describeDuration();
      } else {
         var1 = this.getDescriptionId() + ", Duration: " + this.describeDuration();
      }

      if (!this.visible) {
         var1 = var1 + ", Particles: false";
      }

      if (!this.showIcon) {
         var1 = var1 + ", Show Icon: false";
      }

      return var1;
   }

   private String describeDuration() {
      return this.isInfiniteDuration() ? "infinite" : Integer.toString(this.duration);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof MobEffectInstance var2)
            ? false
            : this.duration == var2.duration
               && this.amplifier == var2.amplifier
               && this.ambient == var2.ambient
               && this.visible == var2.visible
               && this.showIcon == var2.showIcon
               && this.effect.equals(var2.effect);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.effect.hashCode();
      var1 = 31 * var1 + this.duration;
      var1 = 31 * var1 + this.amplifier;
      var1 = 31 * var1 + (this.ambient ? 1 : 0);
      var1 = 31 * var1 + (this.visible ? 1 : 0);
      return 31 * var1 + (this.showIcon ? 1 : 0);
   }

   public Tag save() {
      return (Tag)CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
   }

   @Nullable
   public static MobEffectInstance load(CompoundTag var0) {
      return (MobEffectInstance)CODEC.parse(NbtOps.INSTANCE, var0).resultOrPartial(LOGGER::error).orElse(null);
   }

   public int compareTo(MobEffectInstance var1) {
      short var2 = 32147;
      return (this.getDuration() <= 32147 || var1.getDuration() <= 32147) && (!this.isAmbient() || !var1.isAmbient())
         ? ComparisonChain.start()
            .compareFalseFirst(this.isAmbient(), var1.isAmbient())
            .compareFalseFirst(this.isInfiniteDuration(), var1.isInfiniteDuration())
            .compare(this.getDuration(), var1.getDuration())
            .compare(this.getEffect().value().getColor(), var1.getEffect().value().getColor())
            .result()
         : ComparisonChain.start()
            .compare(this.isAmbient(), var1.isAmbient())
            .compare(this.getEffect().value().getColor(), var1.getEffect().value().getColor())
            .result();
   }

   public void onEffectAdded(LivingEntity var1) {
      this.effect.value().onEffectAdded(var1, this.amplifier);
   }

   public boolean is(Holder<MobEffect> var1) {
      return this.effect.equals(var1);
   }

   public void copyBlendState(MobEffectInstance var1) {
      this.blendState.copyFrom(var1.blendState);
   }

   public void skipBlending() {
      this.blendState.setImmediate(this);
   }

   static class BlendState {
      private float factor;
      private float factorPreviousFrame;

      BlendState() {
         super();
      }

      public void setImmediate(MobEffectInstance var1) {
         this.factor = computeTarget(var1);
         this.factorPreviousFrame = this.factor;
      }

      public void copyFrom(MobEffectInstance.BlendState var1) {
         this.factor = var1.factor;
         this.factorPreviousFrame = var1.factorPreviousFrame;
      }

      public void tick(MobEffectInstance var1) {
         this.factorPreviousFrame = this.factor;
         int var2 = getBlendDuration(var1);
         if (var2 == 0) {
            this.factor = 1.0F;
         } else {
            float var3 = computeTarget(var1);
            if (this.factor != var3) {
               float var4 = 1.0F / (float)var2;
               this.factor = this.factor + Mth.clamp(var3 - this.factor, -var4, var4);
            }
         }
      }

      private static float computeTarget(MobEffectInstance var0) {
         boolean var1 = !var0.endsWithin(getBlendDuration(var0));
         return var1 ? 1.0F : 0.0F;
      }

      private static int getBlendDuration(MobEffectInstance var0) {
         return var0.getEffect().value().getBlendDurationTicks();
      }

      public float getFactor(LivingEntity var1, float var2) {
         if (var1.isRemoved()) {
            this.factorPreviousFrame = this.factor;
         }

         return Mth.lerp(var2, this.factorPreviousFrame, this.factor);
      }
   }

   static record Details(
      int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional<MobEffectInstance.Details> hiddenEffect
   ) {
      public static final MapCodec<MobEffectInstance.Details> MAP_CODEC = MapCodec.recursive(
         "MobEffectInstance.Details",
         var0 -> RecordCodecBuilder.mapCodec(
               var1 -> var1.group(
                        ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance.Details::amplifier),
                        Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstance.Details::duration),
                        Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance.Details::ambient),
                        Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectInstance.Details::showParticles),
                        Codec.BOOL.optionalFieldOf("show_icon").forGetter(var0xx -> Optional.of(var0xx.showIcon())),
                        var0.optionalFieldOf("hidden_effect").forGetter(MobEffectInstance.Details::hiddenEffect)
                     )
                     .apply(var1, MobEffectInstance.Details::create)
            )
      );
      public static final StreamCodec<ByteBuf, MobEffectInstance.Details> STREAM_CODEC = StreamCodec.recursive(
         var0 -> StreamCodec.composite(
               ByteBufCodecs.VAR_INT,
               MobEffectInstance.Details::amplifier,
               ByteBufCodecs.VAR_INT,
               MobEffectInstance.Details::duration,
               ByteBufCodecs.BOOL,
               MobEffectInstance.Details::ambient,
               ByteBufCodecs.BOOL,
               MobEffectInstance.Details::showParticles,
               ByteBufCodecs.BOOL,
               MobEffectInstance.Details::showIcon,
               var0.apply(ByteBufCodecs::optional),
               MobEffectInstance.Details::hiddenEffect,
               MobEffectInstance.Details::new
            )
      );

      Details(int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional<MobEffectInstance.Details> hiddenEffect) {
         super();
         this.amplifier = amplifier;
         this.duration = duration;
         this.ambient = ambient;
         this.showParticles = showParticles;
         this.showIcon = showIcon;
         this.hiddenEffect = hiddenEffect;
      }

      private static MobEffectInstance.Details create(
         int var0, int var1, boolean var2, boolean var3, Optional<Boolean> var4, Optional<MobEffectInstance.Details> var5
      ) {
         return new MobEffectInstance.Details(var0, var1, var2, var3, var4.orElse(var3), var5);
      }
   }
}
