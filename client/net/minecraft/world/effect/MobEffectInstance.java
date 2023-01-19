package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MobEffect effect;
   int duration;
   private int amplifier;
   private boolean ambient;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private MobEffectInstance hiddenEffect;
   private final Optional<MobEffectInstance.FactorData> factorData;

   public MobEffectInstance(MobEffect var1) {
      this(var1, 0, 0);
   }

   public MobEffectInstance(MobEffect var1, int var2) {
      this(var1, var2, 0);
   }

   public MobEffectInstance(MobEffect var1, int var2, int var3) {
      this(var1, var2, var3, false, true);
   }

   public MobEffectInstance(MobEffect var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, var5);
   }

   public MobEffectInstance(MobEffect var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
      this(var1, var2, var3, var4, var5, var6, null, var1.createFactorData());
   }

   public MobEffectInstance(
      MobEffect var1,
      int var2,
      int var3,
      boolean var4,
      boolean var5,
      boolean var6,
      @Nullable MobEffectInstance var7,
      Optional<MobEffectInstance.FactorData> var8
   ) {
      super();
      this.effect = var1;
      this.duration = var2;
      this.amplifier = var3;
      this.ambient = var4;
      this.visible = var5;
      this.showIcon = var6;
      this.hiddenEffect = var7;
      this.factorData = var8;
   }

   public MobEffectInstance(MobEffectInstance var1) {
      super();
      this.effect = var1.effect;
      this.factorData = this.effect.createFactorData();
      this.setDetailsFrom(var1);
   }

   public Optional<MobEffectInstance.FactorData> getFactorData() {
      return this.factorData;
   }

   void setDetailsFrom(MobEffectInstance var1) {
      this.duration = var1.duration;
      this.amplifier = var1.amplifier;
      this.ambient = var1.ambient;
      this.visible = var1.visible;
      this.showIcon = var1.showIcon;
   }

   public boolean update(MobEffectInstance var1) {
      if (this.effect != var1.effect) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      int var2 = this.duration;
      boolean var3 = false;
      if (var1.amplifier > this.amplifier) {
         if (var1.duration < this.duration) {
            MobEffectInstance var4 = this.hiddenEffect;
            this.hiddenEffect = new MobEffectInstance(this);
            this.hiddenEffect.hiddenEffect = var4;
         }

         this.amplifier = var1.amplifier;
         this.duration = var1.duration;
         var3 = true;
      } else if (var1.duration > this.duration) {
         if (var1.amplifier == this.amplifier) {
            this.duration = var1.duration;
            var3 = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new MobEffectInstance(var1);
         } else {
            this.hiddenEffect.update(var1);
         }
      }

      if (!var1.ambient && this.ambient || var3) {
         this.ambient = var1.ambient;
         var3 = true;
      }

      if (var1.visible != this.visible) {
         this.visible = var1.visible;
         var3 = true;
      }

      if (var1.showIcon != this.showIcon) {
         this.showIcon = var1.showIcon;
         var3 = true;
      }

      if (var2 != this.duration) {
         this.factorData.ifPresent(var2x -> var2x.effectChangedTimestamp += this.duration - var2);
         var3 = true;
      }

      return var3;
   }

   public MobEffect getEffect() {
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
      if (this.duration > 0) {
         if (this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
            this.applyEffect(var1);
         }

         this.tickDownDuration();
         if (this.duration == 0 && this.hiddenEffect != null) {
            this.setDetailsFrom(this.hiddenEffect);
            this.hiddenEffect = this.hiddenEffect.hiddenEffect;
            var2.run();
         }
      }

      this.factorData.ifPresent(var1x -> var1x.update(this));
      return this.duration > 0;
   }

   private int tickDownDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.tickDownDuration();
      }

      return --this.duration;
   }

   public void applyEffect(LivingEntity var1) {
      if (this.duration > 0) {
         this.effect.applyEffectTick(var1, this.amplifier);
      }
   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   @Override
   public String toString() {
      String var1;
      if (this.amplifier > 0) {
         var1 = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         var1 = this.getDescriptionId() + ", Duration: " + this.duration;
      }

      if (!this.visible) {
         var1 = var1 + ", Particles: false";
      }

      if (!this.showIcon) {
         var1 = var1 + ", Show Icon: false";
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MobEffectInstance)) {
         return false;
      } else {
         MobEffectInstance var2 = (MobEffectInstance)var1;
         return this.duration == var2.duration && this.amplifier == var2.amplifier && this.ambient == var2.ambient && this.effect.equals(var2.effect);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.effect.hashCode();
      var1 = 31 * var1 + this.duration;
      var1 = 31 * var1 + this.amplifier;
      return 31 * var1 + (this.ambient ? 1 : 0);
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putInt("Id", MobEffect.getId(this.getEffect()));
      this.writeDetailsTo(var1);
      return var1;
   }

   private void writeDetailsTo(CompoundTag var1) {
      var1.putByte("Amplifier", (byte)this.getAmplifier());
      var1.putInt("Duration", this.getDuration());
      var1.putBoolean("Ambient", this.isAmbient());
      var1.putBoolean("ShowParticles", this.isVisible());
      var1.putBoolean("ShowIcon", this.showIcon());
      if (this.hiddenEffect != null) {
         CompoundTag var2 = new CompoundTag();
         this.hiddenEffect.save(var2);
         var1.put("HiddenEffect", var2);
      }

      this.factorData
         .ifPresent(
            var1x -> MobEffectInstance.FactorData.CODEC
                  .encodeStart(NbtOps.INSTANCE, var1x)
                  .resultOrPartial(LOGGER::error)
                  .ifPresent(var1xx -> var1.put("FactorCalculationData", var1xx))
         );
   }

   @Nullable
   public static MobEffectInstance load(CompoundTag var0) {
      int var1 = var0.getInt("Id");
      MobEffect var2 = MobEffect.byId(var1);
      return var2 == null ? null : loadSpecifiedEffect(var2, var0);
   }

   private static MobEffectInstance loadSpecifiedEffect(MobEffect var0, CompoundTag var1) {
      byte var2 = var1.getByte("Amplifier");
      int var3 = var1.getInt("Duration");
      boolean var4 = var1.getBoolean("Ambient");
      boolean var5 = true;
      if (var1.contains("ShowParticles", 1)) {
         var5 = var1.getBoolean("ShowParticles");
      }

      boolean var6 = var5;
      if (var1.contains("ShowIcon", 1)) {
         var6 = var1.getBoolean("ShowIcon");
      }

      MobEffectInstance var7 = null;
      if (var1.contains("HiddenEffect", 10)) {
         var7 = loadSpecifiedEffect(var0, var1.getCompound("HiddenEffect"));
      }

      Optional var8;
      if (var1.contains("FactorCalculationData", 10)) {
         var8 = MobEffectInstance.FactorData.CODEC
            .parse(new Dynamic(NbtOps.INSTANCE, var1.getCompound("FactorCalculationData")))
            .resultOrPartial(LOGGER::error);
      } else {
         var8 = Optional.empty();
      }

      return new MobEffectInstance(var0, var3, Math.max(var2, 0), var4, var5, var6, var7, var8);
   }

   public int compareTo(MobEffectInstance var1) {
      boolean var2 = true;
      return (this.getDuration() <= 32147 || var1.getDuration() <= 32147) && (!this.isAmbient() || !var1.isAmbient())
         ? ComparisonChain.start()
            .compare(this.isAmbient(), var1.isAmbient())
            .compare(this.getDuration(), var1.getDuration())
            .compare(this.getEffect().getColor(), var1.getEffect().getColor())
            .result()
         : ComparisonChain.start().compare(this.isAmbient(), var1.isAmbient()).compare(this.getEffect().getColor(), var1.getEffect().getColor()).result();
   }

   public static class FactorData {
      public static final Codec<MobEffectInstance.FactorData> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("padding_duration").forGetter(var0x -> var0x.paddingDuration),
                  Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter(var0x -> var0x.factorStart),
                  Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter(var0x -> var0x.factorTarget),
                  Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter(var0x -> var0x.factorCurrent),
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("effect_changed_timestamp").orElse(0).forGetter(var0x -> var0x.effectChangedTimestamp),
                  Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter(var0x -> var0x.factorPreviousFrame),
                  Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter(var0x -> var0x.hadEffectLastTick)
               )
               .apply(var0, MobEffectInstance.FactorData::new)
      );
      private final int paddingDuration;
      private float factorStart;
      private float factorTarget;
      private float factorCurrent;
      int effectChangedTimestamp;
      private float factorPreviousFrame;
      private boolean hadEffectLastTick;

      public FactorData(int var1, float var2, float var3, float var4, int var5, float var6, boolean var7) {
         super();
         this.paddingDuration = var1;
         this.factorStart = var2;
         this.factorTarget = var3;
         this.factorCurrent = var4;
         this.effectChangedTimestamp = var5;
         this.factorPreviousFrame = var6;
         this.hadEffectLastTick = var7;
      }

      public FactorData(int var1) {
         this(var1, 0.0F, 1.0F, 0.0F, 0, 0.0F, false);
      }

      public void update(MobEffectInstance var1) {
         this.factorPreviousFrame = this.factorCurrent;
         boolean var2 = var1.duration > this.paddingDuration;
         if (this.hadEffectLastTick != var2) {
            this.hadEffectLastTick = var2;
            this.effectChangedTimestamp = var1.duration;
            this.factorStart = this.factorCurrent;
            this.factorTarget = var2 ? 1.0F : 0.0F;
         }

         float var3 = Mth.clamp(((float)this.effectChangedTimestamp - (float)var1.duration) / (float)this.paddingDuration, 0.0F, 1.0F);
         this.factorCurrent = Mth.lerp(var3, this.factorStart, this.factorTarget);
      }

      public float getFactor(LivingEntity var1, float var2) {
         if (var1.isRemoved()) {
            this.factorPreviousFrame = this.factorCurrent;
         }

         return Mth.lerp(var2, this.factorPreviousFrame, this.factorCurrent);
      }
   }
}
