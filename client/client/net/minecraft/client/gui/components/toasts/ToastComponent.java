package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class ToastComponent {
   private static final int SLOT_COUNT = 5;
   private static final int NO_SPACE = -1;
   final Minecraft minecraft;
   private final List<ToastComponent.ToastInstance<?>> visible = new ArrayList<>();
   private final BitSet occupiedSlots = new BitSet(5);
   private final Deque<Toast> queued = Queues.newArrayDeque();

   public ToastComponent(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(GuiGraphics var1) {
      if (!this.minecraft.options.hideGui) {
         int var2 = var1.guiWidth();
         this.visible.removeIf(var3 -> {
            if (var3 != null && var3.render(var2, var1)) {
               this.occupiedSlots.clear(var3.index, var3.index + var3.slotCount);
               return true;
            } else {
               return false;
            }
         });
         if (!this.queued.isEmpty() && this.freeSlots() > 0) {
            this.queued.removeIf(var1x -> {
               int var2x = var1x.slotCount();
               int var3 = this.findFreeIndex(var2x);
               if (var3 != -1) {
                  this.visible.add(new ToastComponent.ToastInstance<>(var1x, var3, var2x));
                  this.occupiedSlots.set(var3, var3 + var2x);
                  return true;
               } else {
                  return false;
               }
            });
         }
      }
   }

   private int findFreeIndex(int var1) {
      if (this.freeSlots() >= var1) {
         int var2 = 0;

         for (int var3 = 0; var3 < 5; var3++) {
            if (this.occupiedSlots.get(var3)) {
               var2 = 0;
            } else if (++var2 == var1) {
               return var3 + 1 - var2;
            }
         }
      }

      return -1;
   }

   private int freeSlots() {
      return 5 - this.occupiedSlots.cardinality();
   }

   @Nullable
   public <T extends Toast> T getToast(Class<? extends T> var1, Object var2) {
      for (ToastComponent.ToastInstance var4 : this.visible) {
         if (var4 != null && var1.isAssignableFrom(var4.getToast().getClass()) && var4.getToast().getToken().equals(var2)) {
            return (T)var4.getToast();
         }
      }

      for (Toast var6 : this.queued) {
         if (var1.isAssignableFrom(var6.getClass()) && var6.getToken().equals(var2)) {
            return (T)var6;
         }
      }

      return null;
   }

   public void clear() {
      this.occupiedSlots.clear();
      this.visible.clear();
      this.queued.clear();
   }

   public void addToast(Toast var1) {
      this.queued.add(var1);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public double getNotificationDisplayTimeMultiplier() {
      return this.minecraft.options.notificationDisplayTime().get();
   }

   class ToastInstance<T extends Toast> {
      private static final long ANIMATION_TIME = 600L;
      private final T toast;
      final int index;
      final int slotCount;
      private long animationTime = -1L;
      private long visibleTime = -1L;
      private Toast.Visibility visibility = Toast.Visibility.SHOW;

      ToastInstance(final T param2, final int param3, final int param4) {
         super();
         this.toast = (T)nullx;
         this.index = nullxx;
         this.slotCount = nullxxx;
      }

      public T getToast() {
         return this.toast;
      }

      private float getVisibility(long var1) {
         float var3 = Mth.clamp((float)(var1 - this.animationTime) / 600.0F, 0.0F, 1.0F);
         var3 *= var3;
         return this.visibility == Toast.Visibility.HIDE ? 1.0F - var3 : var3;
      }

      public boolean render(int var1, GuiGraphics var2) {
         long var3 = Util.getMillis();
         if (this.animationTime == -1L) {
            this.animationTime = var3;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         if (this.visibility == Toast.Visibility.SHOW && var3 - this.animationTime <= 600L) {
            this.visibleTime = var3;
         }

         var2.pose().pushPose();
         var2.pose().translate((float)var1 - (float)this.toast.width() * this.getVisibility(var3), (float)(this.index * 32), 800.0F);
         Toast.Visibility var5 = this.toast.render(var2, ToastComponent.this, var3 - this.visibleTime);
         var2.pose().popPose();
         if (var5 != this.visibility) {
            this.animationTime = var3 - (long)((int)((1.0F - this.getVisibility(var3)) * 600.0F));
            this.visibility = var5;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         return this.visibility == Toast.Visibility.HIDE && var3 - this.animationTime > 600L;
      }
   }
}
