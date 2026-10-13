package net.minecraft.client.sounds;

import java.util.Optional;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.cow.CowSoundVariant;
import net.minecraft.world.entity.animal.cow.CowSoundVariants;
import org.jspecify.annotations.Nullable;

public final class SoundPreviewHandler {
   private static @Nullable SoundInstance activePreview;
   private static @Nullable SoundSource previousCategory;

   public SoundPreviewHandler() {
      super();
   }

   public static void preview(final SoundManager soundManager, final SoundSource category, final float volume) {
      stopOtherCategoryPreview(soundManager, category);
      if (canPlaySound(soundManager)) {
         SimpleSoundInstance var10000;
         switch (category) {
            case RECORDS -> var10000 = SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_GUITAR, 1.0F, volume);
            case WEATHER -> var10000 = SimpleSoundInstance.forUI(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, volume);
            case BLOCKS -> var10000 = SimpleSoundInstance.forUI((Holder)((Optional)SoundEvents.GRASS.placeSound()).orElseThrow(), 1.0F, volume);
            case HOSTILE -> var10000 = SimpleSoundInstance.forUI(SoundEvents.ZOMBIE_AMBIENT, 1.0F, volume);
            case NEUTRAL -> var10000 = SimpleSoundInstance.forUI((Holder)((CowSoundVariant)SoundEvents.COW_SOUNDS.get(CowSoundVariants.SoundSet.CLASSIC)).ambientSound().orElseThrow(), 1.0F, volume);
            case PLAYERS -> var10000 = SimpleSoundInstance.forUI(SoundEvents.GENERIC_EAT, 1.0F, volume);
            case AMBIENT -> var10000 = SimpleSoundInstance.forUI(SoundEvents.AMBIENT_CAVE, 1.0F, volume);
            case UI -> var10000 = SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F, volume);
            default -> var10000 = null;
         }

         SoundInstance previewSound = var10000;
         if (previewSound != null) {
            activePreview = previewSound;
            soundManager.play(activePreview);
         }
      }

   }

   private static void stopOtherCategoryPreview(final SoundManager soundManager, final SoundSource category) {
      if (previousCategory != category) {
         previousCategory = category;
         if (activePreview != null) {
            soundManager.stop(activePreview);
         }
      }

   }

   private static boolean canPlaySound(final SoundManager soundManager) {
      return activePreview == null || !soundManager.isActive(activePreview);
   }
}
