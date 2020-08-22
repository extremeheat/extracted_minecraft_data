package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class AdvancementToast implements Toast {
   private final Advancement advancement;
   private boolean playedSound;

   public AdvancementToast(Advancement var1) {
      this.advancement = var1;
   }

   public Toast.Visibility render(ToastComponent var1, long var2) {
      var1.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      DisplayInfo var4 = this.advancement.getDisplay();
      var1.blit(0, 0, 0, 0, 160, 32);
      if (var4 != null) {
         List var5 = var1.getMinecraft().font.split(var4.getTitle().getColoredString(), 125);
         int var6 = var4.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
         if (var5.size() == 1) {
            var1.getMinecraft().font.draw(I18n.get("advancements.toast." + var4.getFrame().getName()), 30.0F, 7.0F, var6 | -16777216);
            var1.getMinecraft().font.draw(var4.getTitle().getColoredString(), 30.0F, 18.0F, -1);
         } else {
            boolean var7 = true;
            float var8 = 300.0F;
            int var9;
            if (var2 < 1500L) {
               var9 = Mth.floor(Mth.clamp((float)(1500L - var2) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               var1.getMinecraft().font.draw(I18n.get("advancements.toast." + var4.getFrame().getName()), 30.0F, 11.0F, var6 | var9);
            } else {
               var9 = Mth.floor(Mth.clamp((float)(var2 - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int var10001 = var5.size();
               var1.getMinecraft().font.getClass();
               int var10 = 16 - var10001 * 9 / 2;

               for(Iterator var11 = var5.iterator(); var11.hasNext(); var10 += 9) {
                  String var12 = (String)var11.next();
                  var1.getMinecraft().font.draw(var12, 30.0F, (float)var10, 16777215 | var9);
                  var1.getMinecraft().font.getClass();
               }
            }
         }

         if (!this.playedSound && var2 > 0L) {
            this.playedSound = true;
            if (var4.getFrame() == FrameType.CHALLENGE) {
               var1.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         var1.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, var4.getIcon(), 8, 8);
         return var2 >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      } else {
         return Toast.Visibility.HIDE;
      }
   }
}
