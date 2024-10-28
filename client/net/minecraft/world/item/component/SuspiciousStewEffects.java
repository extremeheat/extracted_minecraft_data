package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record SuspiciousStewEffects(List<Entry> effects) {
   public static final SuspiciousStewEffects EMPTY = new SuspiciousStewEffects(List.of());
   public static final Codec<SuspiciousStewEffects> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, SuspiciousStewEffects> STREAM_CODEC;

   public SuspiciousStewEffects(List<Entry> var1) {
      super();
      this.effects = var1;
   }

   public SuspiciousStewEffects withEffectAdded(Entry var1) {
      return new SuspiciousStewEffects(Util.copyAndAdd((List)this.effects, (Object)var1));
   }

   public List<Entry> effects() {
      return this.effects;
   }

   static {
      CODEC = SuspiciousStewEffects.Entry.CODEC.listOf().xmap(SuspiciousStewEffects::new, SuspiciousStewEffects::effects);
      STREAM_CODEC = SuspiciousStewEffects.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SuspiciousStewEffects::new, SuspiciousStewEffects::effects);
   }

   public static record Entry(Holder<MobEffect> effect, int duration) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(MobEffect.CODEC.fieldOf("id").forGetter(Entry::effect), Codec.INT.lenientOptionalFieldOf("duration", 160).forGetter(Entry::duration)).apply(var0, Entry::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(Holder<MobEffect> var1, int var2) {
         super();
         this.effect = var1;
         this.duration = var2;
      }

      public MobEffectInstance createEffectInstance() {
         return new MobEffectInstance(this.effect, this.duration);
      }

      public Holder<MobEffect> effect() {
         return this.effect;
      }

      public int duration() {
         return this.duration;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(MobEffect.STREAM_CODEC, Entry::effect, ByteBufCodecs.VAR_INT, Entry::duration, Entry::new);
      }
   }
}
