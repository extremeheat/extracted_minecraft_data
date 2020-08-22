package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;

public class ToastComponent extends GuiComponent {
   private final Minecraft minecraft;
   private final ToastComponent.ToastInstance[] visible = new ToastComponent.ToastInstance[5];
   private final Deque queued = Queues.newArrayDeque();

   public ToastComponent(Minecraft var1) {
      this.minecraft = var1;
   }

   public void render() {
      if (!this.minecraft.options.hideGui) {
         for(int var1 = 0; var1 < this.visible.length; ++var1) {
            ToastComponent.ToastInstance var2 = this.visible[var1];
            if (var2 != null && var2.render(this.minecraft.getWindow().getGuiScaledWidth(), var1)) {
               this.visible[var1] = null;
            }

            if (this.visible[var1] == null && !this.queued.isEmpty()) {
               this.visible[var1] = new ToastComponent.ToastInstance((Toast)this.queued.removeFirst());
            }
         }

      }
   }

   @Nullable
   public Toast getToast(Class var1, Object var2) {
      ToastComponent.ToastInstance[] var3 = this.visible;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ToastComponent.ToastInstance var6 = var3[var5];
         if (var6 != null && var1.isAssignableFrom(var6.getToast().getClass()) && var6.getToast().getToken().equals(var2)) {
            return var6.getToast();
         }
      }

      Iterator var7 = this.queued.iterator();

      Toast var8;
      do {
         if (!var7.hasNext()) {
            return null;
         }

         var8 = (Toast)var7.next();
      } while(!var1.isAssignableFrom(var8.getClass()) || !var8.getToken().equals(var2));

      return var8;
   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.queued.clear();
   }

   public void addToast(Toast var1) {
      this.queued.add(var1);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   class ToastInstance {
      private final Toast toast;
      private long animationTime;
      private long visibleTime;
      private Toast.Visibility visibility;

      private ToastInstance(Toast var2) {
         this.animationTime = -1L;
         this.visibleTime = -1L;
         this.visibility = Toast.Visibility.SHOW;
         this.toast = var2;
      }

      public Toast getToast() {
         return this.toast;
      }

      private float getVisibility(long var1) {
         float var3 = Mth.clamp((float)(var1 - this.animationTime) / 600.0F, 0.0F, 1.0F);
         var3 *= var3;
         return this.visibility == Toast.Visibility.HIDE ? 1.0F - var3 : var3;
      }

      public boolean render(int var1, int var2) {
         long var3 = Util.getMillis();
         if (this.animationTime == -1L) {
            this.animationTime = var3;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         if (this.visibility == Toast.Visibility.SHOW && var3 - this.animationTime <= 600L) {
            this.visibleTime = var3;
         }

         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)var1 - 160.0F * this.getVisibility(var3), (float)(var2 * 32), (float)(800 + var2));
         Toast.Visibility var5 = this.toast.render(ToastComponent.this, var3 - this.visibleTime);
         RenderSystem.popMatrix();
         if (var5 != this.visibility) {
            this.animationTime = var3 - (long)((int)((1.0F - this.getVisibility(var3)) * 600.0F));
            this.visibility = var5;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         return this.visibility == Toast.Visibility.HIDE && var3 - this.animationTime > 600L;
      }

      // $FF: synthetic method
      ToastInstance(Toast var2, Object var3) {
         this(var2);
      }
   }
}
