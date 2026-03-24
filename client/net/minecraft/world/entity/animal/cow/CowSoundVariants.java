package net.minecraft.world.entity.animal.cow;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;

public class CowSoundVariants {
   public static final ResourceKey<CowSoundVariant> CLASSIC;
   public static final ResourceKey<CowSoundVariant> MOODY;

   public CowSoundVariants() {
      super();
   }

   private static ResourceKey<CowSoundVariant> createKey(final SoundSet cowSoundVariant) {
      return ResourceKey.create(Registries.COW_SOUND_VARIANT, Identifier.withDefaultNamespace(cowSoundVariant.getIdentifier()));
   }

   public static void bootstrap(final BootstrapContext<CowSoundVariant> context) {
      register(context, CLASSIC, CowSoundVariants.SoundSet.CLASSIC);
      register(context, MOODY, CowSoundVariants.SoundSet.MOODY);
   }

   private static void register(final BootstrapContext<CowSoundVariant> context, final ResourceKey<CowSoundVariant> key, final SoundSet CowSoundVariant) {
      context.register(key, (CowSoundVariant)SoundEvents.COW_SOUNDS.get(CowSoundVariant));
   }

   public static Holder<CowSoundVariant> pickRandomSoundVariant(final RegistryAccess registryAccess, final RandomSource random) {
      return (Holder)registryAccess.lookupOrThrow(Registries.COW_SOUND_VARIANT).getRandom(random).orElseThrow();
   }

   static {
      CLASSIC = createKey(CowSoundVariants.SoundSet.CLASSIC);
      MOODY = createKey(CowSoundVariants.SoundSet.MOODY);
   }

   public static enum SoundSet {
      CLASSIC("classic", "cow"),
      MOODY("moody", "cow_moody");

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
         return new SoundSet[]{CLASSIC, MOODY};
      }
   }
}
