package net.minecraft.client;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.Window;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;

public class FullscreenResolutionProgressOption extends ProgressOption {
   public FullscreenResolutionProgressOption(Window var1) {
      this(var1, var1.findBestMonitor());
   }

   private FullscreenResolutionProgressOption(Window var1, @Nullable Monitor var2) {
      super("options.fullscreen.resolution", -1.0D, var2 != null ? (double)(var2.getModeCount() - 1) : -1.0D, 1.0F, (var2x) -> {
         if (var2 == null) {
            return -1.0D;
         } else {
            Optional var3 = var1.getPreferredFullscreenVideoMode();
            return (Double)var3.map((var1x) -> {
               return (double)var2.getVideoModeIndex(var1x);
            }).orElse(-1.0D);
         }
      }, (var2x, var3) -> {
         if (var2 != null) {
            if (var3 == -1.0D) {
               var1.setPreferredFullscreenVideoMode(Optional.empty());
            } else {
               var1.setPreferredFullscreenVideoMode(Optional.of(var2.getMode(var3.intValue())));
            }

         }
      }, (var1x, var2x) -> {
         if (var2 == null) {
            return I18n.get("options.fullscreen.unavailable");
         } else {
            double var3 = var2x.get(var1x);
            String var5 = var2x.getCaption();
            return var3 == -1.0D ? var5 + I18n.get("options.fullscreen.current") : var2.getMode((int)var3).toString();
         }
      });
   }
}
