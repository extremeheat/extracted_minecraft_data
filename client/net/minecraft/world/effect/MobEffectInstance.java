package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
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
         if (var1.level() instanceof ServerLevel var4
            && this.effect.value().shouldApplyEffectTickThisTick(var3, this.amplifier)
            && !this.effect.value().applyEffectTick(var4, var1, this.amplifier)) {
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

   public void onMobRemoved(ServerLevel var1, LivingEntity var2, Entity.RemovalReason var3) {
      this.effect.value().onMobRemoved(var1, var2, this.amplifier, var3);
   }

   public void onMobHurt(ServerLevel var1, LivingEntity var2, DamageSource var3, float var4) {
      this.effect.value().onMobHurt(var1, var2, this.amplifier, var3, var4);
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
