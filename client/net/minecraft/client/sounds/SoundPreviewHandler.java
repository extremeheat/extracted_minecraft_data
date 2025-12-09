package net.minecraft.client.sounds;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jspecify.annotations.Nullable;

public final class SoundPreviewHandler {
   private static @Nullable SoundInstance activePreview;
   private static @Nullable SoundSource previousCategory;

   public SoundPreviewHandler() {
      super();
   }

   public static void preview(SoundManager var0, SoundSource var1, float var2) {
      stopOtherCategoryPreview(var0, var1);
      if (canPlaySound(var0)) {
         SoundEvent var10000;
         switch (var1) {
            case RECORDS -> var10000 = SoundEvents.NOTE_BLOCK_GUITAR.value();
            case WEATHER -> var10000 = SoundEvents.LIGHTNING_BOLT_THUNDER;
            case BLOCKS -> var10000 = SoundEvents.GRASS_PLACE;
            case HOSTILE -> var10000 = SoundEvents.ZOMBIE_AMBIENT;
            case NEUTRAL -> var10000 = SoundEvents.COW_AMBIENT;
            case PLAYERS -> var10000 = SoundEvents.GENERIC_EAT.value();
            case AMBIENT -> var10000 = SoundEvents.AMBIENT_CAVE.value();
            case UI -> var10000 = SoundEvents.UI_BUTTON_CLICK.value();
            default -> var10000 = SoundEvents.EMPTY;
         }

         SoundEvent var3 = var10000;
         if (var3 != SoundEvents.EMPTY) {
            activePreview = SimpleSoundInstance.forUI(var3, 1.0F, var2);
            var0.play(activePreview);
         }
      }

   }

   private static void stopOtherCategoryPreview(SoundManager var0, SoundSource var1) {
      if (previousCategory != var1) {
         previousCategory = var1;
         if (activePreview != null) {
            var0.stop(activePreview);
         }
      }

   }

   private static boolean canPlaySound(SoundManager var0) {
      return activePreview == null || !var0.isActive(activePreview);
   }
}
