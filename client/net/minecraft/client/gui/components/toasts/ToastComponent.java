package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
   private final ToastComponent.ToastInstance<?>[] visible = new ToastComponent.ToastInstance[5];
   private final Deque<Toast> queued = Queues.newArrayDeque();

   public ToastComponent(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1) {
      if (!this.minecraft.options.hideGui) {
         for(int var2 = 0; var2 < this.visible.length; ++var2) {
            ToastComponent.ToastInstance var3 = this.visible[var2];
            if (var3 != null && var3.render(this.minecraft.getWindow().getGuiScaledWidth(), var2, var1)) {
               this.visible[var2] = null;
            }

            if (this.visible[var2] == null && !this.queued.isEmpty()) {
               this.visible[var2] = new ToastComponent.ToastInstance((Toast)this.queued.removeFirst());
            }
         }

      }
   }

   @Nullable
   public <T extends Toast> T getToast(Class<? extends T> var1, Object var2) {
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

   class ToastInstance<T extends Toast> {
      private final T toast;
      private long animationTime;
      private long visibleTime;
      private Toast.Visibility visibility;

      private ToastInstance(T var2) {
         super();
         this.animationTime = -1L;
         this.visibleTime = -1L;
         this.visibility = Toast.Visibility.SHOW;
         this.toast = var2;
      }

      public T getToast() {
         return this.toast;
      }

      private float getVisibility(long var1) {
         float var3 = Mth.clamp((float)(var1 - this.animationTime) / 600.0F, 0.0F, 1.0F);
         var3 *= var3;
         return this.visibility == Toast.Visibility.HIDE ? 1.0F - var3 : var3;
      }

      public boolean render(int var1, int var2, PoseStack var3) {
         long var4 = Util.getMillis();
         if (this.animationTime == -1L) {
            this.animationTime = var4;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         if (this.visibility == Toast.Visibility.SHOW && var4 - this.animationTime <= 600L) {
            this.visibleTime = var4;
         }

         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)var1 - (float)this.toast.width() * this.getVisibility(var4), (float)(var2 * this.toast.height()), (float)(800 + var2));
         Toast.Visibility var6 = this.toast.render(var3, ToastComponent.this, var4 - this.visibleTime);
         RenderSystem.popMatrix();
         if (var6 != this.visibility) {
            this.animationTime = var4 - (long)((int)((1.0F - this.getVisibility(var4)) * 600.0F));
            this.visibility = var6;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         return this.visibility == Toast.Visibility.HIDE && var4 - this.animationTime > 600L;
      }

      // $FF: synthetic method
      ToastInstance(Toast var2, Object var3) {
         this(var2);
      }
   }
}
