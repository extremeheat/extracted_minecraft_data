package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public record PlaySoundEffect(List<Holder<SoundEvent>> soundEvents, FloatProvider volume, FloatProvider pitch) implements EnchantmentEntityEffect {
   public static final MapCodec<PlaySoundEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.compactListCodec(SoundEvent.CODEC, SoundEvent.CODEC.sizeLimitedListOf(255)).fieldOf("sound").forGetter(PlaySoundEffect::soundEvents), FloatProvider.codec(1.0E-5F, 10.0F).fieldOf("volume").forGetter(PlaySoundEffect::volume), FloatProvider.codec(1.0E-5F, 2.0F).fieldOf("pitch").forGetter(PlaySoundEffect::pitch)).apply(var0, PlaySoundEffect::new));

   public PlaySoundEffect(List<Holder<SoundEvent>> var1, FloatProvider var2, FloatProvider var3) {
      super();
      this.soundEvents = var1;
      this.volume = var2;
      this.pitch = var3;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      if (!var4.isSilent()) {
         RandomSource var6 = var4.getRandom();
         int var7 = Mth.clamp(var2 - 1, 0, this.soundEvents.size() - 1);
         var1.playSound((Entity)null, var5.x(), var5.y(), var5.z(), (Holder)this.soundEvents.get(var7), var4.getSoundSource(), this.volume.sample(var6), this.pitch.sample(var6));
      }
   }

   public MapCodec<PlaySoundEffect> codec() {
      return CODEC;
   }
}
