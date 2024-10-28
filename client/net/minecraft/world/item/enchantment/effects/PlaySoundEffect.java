package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public record PlaySoundEffect(Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) implements EnchantmentEntityEffect {
   public static final MapCodec<PlaySoundEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter(PlaySoundEffect::soundEvent), FloatProvider.codec(1.0E-5F, 10.0F).fieldOf("volume").forGetter(PlaySoundEffect::volume), FloatProvider.codec(1.0E-5F, 2.0F).fieldOf("pitch").forGetter(PlaySoundEffect::pitch)).apply(var0, PlaySoundEffect::new);
   });

   public PlaySoundEffect(Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) {
      super();
      this.soundEvent = soundEvent;
      this.volume = volume;
      this.pitch = pitch;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      RandomSource var6 = var4.getRandom();
      var4.playSound((SoundEvent)this.soundEvent.value(), this.volume.sample(var6), this.pitch.sample(var6));
   }

   public MapCodec<PlaySoundEffect> codec() {
      return CODEC;
   }

   public Holder<SoundEvent> soundEvent() {
      return this.soundEvent;
   }

   public FloatProvider volume() {
      return this.volume;
   }

   public FloatProvider pitch() {
      return this.pitch;
   }
}
