package net.minecraft.client.gui.components.toasts;

import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("toast/advancement");
   public static final int DISPLAY_TIME = 5000;
   private final AdvancementHolder advancement;
   private boolean playedSound;

   public AdvancementToast(AdvancementHolder var1) {
      super();
      this.advancement = var1;
   }

   @Override
   public Toast.Visibility render(GuiGraphics var1, ToastComponent var2, long var3) {
      DisplayInfo var5 = this.advancement.value().display().orElse(null);
      var1.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      if (var5 != null) {
         List var6 = var2.getMinecraft().font.split(var5.getTitle(), 125);
         int var7 = var5.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (var6.size() == 1) {
            var1.drawString(var2.getMinecraft().font, var5.getFrame().getDisplayName(), 30, 7, var7 | 0xFF000000, false);
            var1.drawString(var2.getMinecraft().font, (FormattedCharSequence)var6.get(0), 30, 18, -1, false);
         } else {
            boolean var8 = true;
            float var9 = 300.0F;
            if (var3 < 1500L) {
               int var10 = Mth.floor(Mth.clamp((float)(1500L - var3) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               var1.drawString(var2.getMinecraft().font, var5.getFrame().getDisplayName(), 30, 11, var7 | var10, false);
            } else {
               int var14 = Mth.floor(Mth.clamp((float)(var3 - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int var11 = this.height() / 2 - var6.size() * 9 / 2;

               for(FormattedCharSequence var13 : var6) {
                  var1.drawString(var2.getMinecraft().font, var13, 30, var11, 16777215 | var14, false);
                  var11 += 9;
               }
            }
         }

         if (!this.playedSound && var3 > 0L) {
            this.playedSound = true;
            if (var5.getFrame() == FrameType.CHALLENGE) {
               var2.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         var1.renderFakeItem(var5.getIcon(), 8, 8);
         return (double)var3 >= 5000.0 * var2.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      } else {
         return Toast.Visibility.HIDE;
      }
   }
}
