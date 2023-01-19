package net.minecraft.world.item;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public interface Instruments {
   int GOAT_HORN_RANGE_BLOCKS = 256;
   int GOAT_HORN_DURATION = 140;
   ResourceKey<Instrument> PONDER_GOAT_HORN = create("ponder_goat_horn");
   ResourceKey<Instrument> SING_GOAT_HORN = create("sing_goat_horn");
   ResourceKey<Instrument> SEEK_GOAT_HORN = create("seek_goat_horn");
   ResourceKey<Instrument> FEEL_GOAT_HORN = create("feel_goat_horn");
   ResourceKey<Instrument> ADMIRE_GOAT_HORN = create("admire_goat_horn");
   ResourceKey<Instrument> CALL_GOAT_HORN = create("call_goat_horn");
   ResourceKey<Instrument> YEARN_GOAT_HORN = create("yearn_goat_horn");
   ResourceKey<Instrument> DREAM_GOAT_HORN = create("dream_goat_horn");

   private static ResourceKey<Instrument> create(String var0) {
      return ResourceKey.create(Registry.INSTRUMENT_REGISTRY, new ResourceLocation(var0));
   }

   static Instrument bootstrap(Registry<Instrument> var0) {
      Registry.register(var0, PONDER_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0), 140, 256.0F));
      Registry.register(var0, SING_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(1), 140, 256.0F));
      Registry.register(var0, SEEK_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2), 140, 256.0F));
      Registry.register(var0, FEEL_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(3), 140, 256.0F));
      Registry.register(var0, ADMIRE_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(4), 140, 256.0F));
      Registry.register(var0, CALL_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5), 140, 256.0F));
      Registry.register(var0, YEARN_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6), 140, 256.0F));
      return Registry.register(var0, DREAM_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(7), 140, 256.0F));
   }
}
