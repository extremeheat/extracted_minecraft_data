package net.minecraft.world.entity.animal.wolf;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;

public class WolfSoundVariants {
   public static final ResourceKey<WolfSoundVariant> CLASSIC;
   public static final ResourceKey<WolfSoundVariant> PUGLIN;
   public static final ResourceKey<WolfSoundVariant> SAD;
   public static final ResourceKey<WolfSoundVariant> ANGRY;
   public static final ResourceKey<WolfSoundVariant> GRUMPY;
   public static final ResourceKey<WolfSoundVariant> BIG;
   public static final ResourceKey<WolfSoundVariant> CUTE;

   public WolfSoundVariants() {
      super();
   }

   private static ResourceKey<WolfSoundVariant> createKey(final SoundSet wolfSoundVariant) {
      return ResourceKey.create(Registries.WOLF_SOUND_VARIANT, Identifier.withDefaultNamespace(wolfSoundVariant.getIdentifier()));
   }

   public static void bootstrap(final BootstrapContext<WolfSoundVariant> context) {
      register(context, CLASSIC, WolfSoundVariants.SoundSet.CLASSIC);
      register(context, PUGLIN, WolfSoundVariants.SoundSet.PUGLIN);
      register(context, SAD, WolfSoundVariants.SoundSet.SAD);
      register(context, ANGRY, WolfSoundVariants.SoundSet.ANGRY);
      register(context, GRUMPY, WolfSoundVariants.SoundSet.GRUMPY);
      register(context, BIG, WolfSoundVariants.SoundSet.BIG);
      register(context, CUTE, WolfSoundVariants.SoundSet.CUTE);
   }

   private static void register(final BootstrapContext<WolfSoundVariant> context, final ResourceKey<WolfSoundVariant> key, final SoundSet wolfSoundVariant) {
      context.register(key, (WolfSoundVariant)SoundEvents.WOLF_SOUNDS.get(wolfSoundVariant));
   }

   public static Holder<WolfSoundVariant> pickRandomSoundVariant(final RegistryAccess registryAccess, final RandomSource random) {
      return (Holder)registryAccess.lookupOrThrow(Registries.WOLF_SOUND_VARIANT).getRandom(random).orElseThrow();
   }

   static {
      CLASSIC = createKey(WolfSoundVariants.SoundSet.CLASSIC);
      PUGLIN = createKey(WolfSoundVariants.SoundSet.PUGLIN);
      SAD = createKey(WolfSoundVariants.SoundSet.SAD);
      ANGRY = createKey(WolfSoundVariants.SoundSet.ANGRY);
      GRUMPY = createKey(WolfSoundVariants.SoundSet.GRUMPY);
      BIG = createKey(WolfSoundVariants.SoundSet.BIG);
      CUTE = createKey(WolfSoundVariants.SoundSet.CUTE);
   }

   public static enum SoundSet {
      CLASSIC("classic", "wolf"),
      PUGLIN("puglin", "wolf_puglin"),
      SAD("sad", "wolf_sad"),
      ANGRY("angry", "wolf_angry"),
      GRUMPY("grumpy", "wolf_grumpy"),
      BIG("big", "wolf_big"),
      CUTE("cute", "wolf_cute");

      private final String identifier;
      private final String soundEventIdentifier;

      private SoundSet(final String identifier, final String soundEventIdentifier) {
         this.identifier = identifier;
         this.soundEventIdentifier = soundEventIdentifier;
      }

      public String getIdentifier() {
         return this.identifier;
      }

      public String getSoundEventIdentifier() {
         return this.soundEventIdentifier;
      }

      // $FF: synthetic method
      private static SoundSet[] $values() {
         return new SoundSet[]{CLASSIC, PUGLIN, SAD, ANGRY, GRUMPY, BIG, CUTE};
      }
   }
}
