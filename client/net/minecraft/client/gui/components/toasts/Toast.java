package net.minecraft.client.gui.components.toasts;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public interface Toast {
   Object NO_TOKEN = new Object();
   int DEFAULT_WIDTH = 160;
   int SLOT_HEIGHT = 32;

   Visibility getWantedVisibility();

   void update(ToastManager var1, long var2);

   void render(GuiGraphics var1, Font var2, long var3);

   default Object getToken() {
      return NO_TOKEN;
   }

   default int width() {
      return 160;
   }

   default int height() {
      return 32;
   }

   default int occcupiedSlotCount() {
      return Mth.positiveCeilDiv(this.height(), 32);
   }

   public static enum Visibility {
      SHOW(SoundEvents.UI_TOAST_IN),
      HIDE(SoundEvents.UI_TOAST_OUT);

      private final SoundEvent soundEvent;

      private Visibility(final SoundEvent var3) {
         this.soundEvent = var3;
      }

      public void playSound(SoundManager var1) {
         var1.play(SimpleSoundInstance.forUI(this.soundEvent, 1.0F, 1.0F));
      }

      // $FF: synthetic method
      private static Visibility[] $values() {
         return new Visibility[]{SHOW, HIDE};
      }
   }
}
