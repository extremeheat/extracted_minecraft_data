package net.minecraft.world.entity.animal.wolf;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

   private static ResourceKey<WolfSoundVariant> createKey(SoundSet var0) {
      return ResourceKey.create(Registries.WOLF_SOUND_VARIANT, ResourceLocation.withDefaultNamespace(var0.getIdentifier()));
   }

   public static void bootstrap(BootstrapContext<WolfSoundVariant> var0) {
      register(var0, CLASSIC, WolfSoundVariants.SoundSet.CLASSIC);
      register(var0, PUGLIN, WolfSoundVariants.SoundSet.PUGLIN);
      register(var0, SAD, WolfSoundVariants.SoundSet.SAD);
      register(var0, ANGRY, WolfSoundVariants.SoundSet.ANGRY);
      register(var0, GRUMPY, WolfSoundVariants.SoundSet.GRUMPY);
      register(var0, BIG, WolfSoundVariants.SoundSet.BIG);
      register(var0, CUTE, WolfSoundVariants.SoundSet.CUTE);
   }

   private static void register(BootstrapContext<WolfSoundVariant> var0, ResourceKey<WolfSoundVariant> var1, SoundSet var2) {
      var0.register(var1, (WolfSoundVariant)SoundEvents.WOLF_SOUNDS.get(var2));
   }

   public static Holder<WolfSoundVariant> pickRandomSoundVariant(RegistryAccess var0, RandomSource var1) {
      return (Holder)var0.lookupOrThrow(Registries.WOLF_SOUND_VARIANT).getRandom(var1).orElseThrow();
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
      CLASSIC("classic", ""),
      PUGLIN("puglin", "_puglin"),
      SAD("sad", "_sad"),
      ANGRY("angry", "_angry"),
      GRUMPY("grumpy", "_grumpy"),
      BIG("big", "_big"),
      CUTE("cute", "_cute");

      private final String identifier;
      private final String soundEventSuffix;

      private SoundSet(final String var3, final String var4) {
         this.identifier = var3;
         this.soundEventSuffix = var4;
      }

      public String getIdentifier() {
         return this.identifier;
      }

      public String getSoundEventSuffix() {
         return this.soundEventSuffix;
      }

      // $FF: synthetic method
      private static SoundSet[] $values() {
         return new SoundSet[]{CLASSIC, PUGLIN, SAD, ANGRY, GRUMPY, BIG, CUTE};
      }
   }
}
