package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MobEffect effect;
   private int duration;
   private int amplifier;
   private boolean splash;
   private boolean ambient;
   private boolean noCounter;
   private boolean visible;
   private boolean showIcon;
   @Nullable
   private MobEffectInstance hiddenEffect;

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
      this(var1, var2, var3, var4, var5, var6, (MobEffectInstance)null);
   }

   public MobEffectInstance(MobEffect var1, int var2, int var3, boolean var4, boolean var5, boolean var6, @Nullable MobEffectInstance var7) {
      super();
      this.effect = var1;
      this.duration = var2;
      this.amplifier = var3;
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

      boolean var2 = false;
      if (var1.amplifier > this.amplifier) {
         if (var1.duration < this.duration) {
            MobEffectInstance var3 = this.hiddenEffect;
            this.hiddenEffect = new MobEffectInstance(this);
            this.hiddenEffect.hiddenEffect = var3;
         }

         this.amplifier = var1.amplifier;
         this.duration = var1.duration;
         var2 = true;
      } else if (var1.duration > this.duration) {
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

   public String toString() {
      String var1;
      if (this.amplifier > 0) {
         var1 = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         var1 = this.getDescriptionId() + ", Duration: " + this.duration;
      }

      if (this.splash) {
         var1 = var1 + ", Splash: true";
      }

      if (!this.visible) {
         var1 = var1 + ", Particles: false";
      }

      if (!this.showIcon) {
         var1 = var1 + ", Show Icon: false";
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MobEffectInstance)) {
         return false;
      } else {
         MobEffectInstance var2 = (MobEffectInstance)var1;
         return this.duration == var2.duration && this.amplifier == var2.amplifier && this.splash == var2.splash && this.ambient == var2.ambient && this.effect.equals(var2.effect);
      }
   }

   public int hashCode() {
      int var1 = this.effect.hashCode();
      var1 = 31 * var1 + this.duration;
      var1 = 31 * var1 + this.amplifier;
      var1 = 31 * var1 + (this.splash ? 1 : 0);
      var1 = 31 * var1 + (this.ambient ? 1 : 0);
      return var1;
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putByte("Id", (byte)MobEffect.getId(this.getEffect()));
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

   }

   public static MobEffectInstance load(CompoundTag var0) {
      byte var1 = var0.getByte("Id");
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

      return new MobEffectInstance(var0, var3, var2 < 0 ? 0 : var2, var4, var5, var6, var7);
   }

   public void setNoCounter(boolean var1) {
      this.noCounter = var1;
   }

   public boolean isNoCounter() {
      return this.noCounter;
   }

   public int compareTo(MobEffectInstance var1) {
      boolean var2 = true;
      return (this.getDuration() <= 32147 || var1.getDuration() <= 32147) && (!this.isAmbient() || !var1.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), var1.isAmbient()).compare(this.getDuration(), var1.getDuration()).compare(this.getEffect().getColor(), var1.getEffect().getColor()).result() : ComparisonChain.start().compare(this.isAmbient(), var1.isAmbient()).compare(this.getEffect().getColor(), var1.getEffect().getColor()).result();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((MobEffectInstance)var1);
   }
}
