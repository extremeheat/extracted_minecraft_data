package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementToast implements Toast {
   private final Advancement advancement;
   private boolean playedSound;

   public AdvancementToast(Advancement var1) {
      super();
      this.advancement = var1;
   }

   public Toast.Visibility render(PoseStack var1, ToastComponent var2, long var3) {
      var2.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      DisplayInfo var5 = this.advancement.getDisplay();
      var2.blit(var1, 0, 0, 0, 0, this.width(), this.height());
      if (var5 != null) {
         List var6 = var2.getMinecraft().font.split(var5.getTitle(), 125);
         int var7 = var5.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (var6.size() == 1) {
            var2.getMinecraft().font.draw(var1, var5.getFrame().getDisplayName(), 30.0F, 7.0F, var7 | -16777216);
            var2.getMinecraft().font.draw(var1, (FormattedCharSequence)((FormattedCharSequence)var6.get(0)), 30.0F, 18.0F, -1);
         } else {
            boolean var8 = true;
            float var9 = 300.0F;
            int var10;
            if (var3 < 1500L) {
               var10 = Mth.floor(Mth.clamp((float)(1500L - var3) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               var2.getMinecraft().font.draw(var1, var5.getFrame().getDisplayName(), 30.0F, 11.0F, var7 | var10);
            } else {
               var10 = Mth.floor(Mth.clamp((float)(var3 - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int var10000 = this.height() / 2;
               int var10001 = var6.size();
               var2.getMinecraft().font.getClass();
               int var11 = var10000 - var10001 * 9 / 2;

               for(Iterator var12 = var6.iterator(); var12.hasNext(); var11 += 9) {
                  FormattedCharSequence var13 = (FormattedCharSequence)var12.next();
                  var2.getMinecraft().font.draw(var1, var13, 30.0F, (float)var11, 16777215 | var10);
                  var2.getMinecraft().font.getClass();
               }
            }
         }

         if (!this.playedSound && var3 > 0L) {
            this.playedSound = true;
            if (var5.getFrame() == FrameType.CHALLENGE) {
               var2.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         var2.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(var5.getIcon(), 8, 8);
         return var3 >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      } else {
         return Toast.Visibility.HIDE;
      }
   }
}
