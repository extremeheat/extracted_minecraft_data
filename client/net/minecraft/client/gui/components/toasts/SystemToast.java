package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class SystemToast implements Toast {
   private final SystemToast.SystemToastIds id;
   private String title;
   private String message;
   private long lastChanged;
   private boolean changed;

   public SystemToast(SystemToast.SystemToastIds var1, Component var2, @Nullable Component var3) {
      super();
      this.id = var1;
      this.title = var2.getString();
      this.message = var3 == null ? null : var3.getString();
   }

   public Toast.Visibility render(ToastComponent var1, long var2) {
      if (this.changed) {
         this.lastChanged = var2;
         this.changed = false;
      }

      var1.getMinecraft().getTextureManager().bind(TEXTURE);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      var1.blit(0, 0, 0, 64, 160, 32);
      if (this.message == null) {
         var1.getMinecraft().font.draw(this.title, 18.0F, 12.0F, -256);
      } else {
         var1.getMinecraft().font.draw(this.title, 18.0F, 7.0F, -256);
         var1.getMinecraft().font.draw(this.message, 18.0F, 18.0F, -1);
      }

      return var2 - this.lastChanged < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
   }

   public void reset(Component var1, @Nullable Component var2) {
      this.title = var1.getString();
      this.message = var2 == null ? null : var2.getString();
      this.changed = true;
   }

   public SystemToast.SystemToastIds getToken() {
      return this.id;
   }

   public static void addOrUpdate(ToastComponent var0, SystemToast.SystemToastIds var1, Component var2, @Nullable Component var3) {
      SystemToast var4 = (SystemToast)var0.getToast(SystemToast.class, var1);
      if (var4 == null) {
         var0.addToast(new SystemToast(var1, var2, var3));
      } else {
         var4.reset(var2, var3);
      }

   }

   // $FF: synthetic method
   public Object getToken() {
      return this.getToken();
   }

   public static enum SystemToastIds {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP;

      private SystemToastIds() {
      }
   }
}
