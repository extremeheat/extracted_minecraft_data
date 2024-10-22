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

public class ToastManager {
   private static final int SLOT_COUNT = 5;
   private static final int ALL_SLOTS_OCCUPIED = -1;
   final Minecraft minecraft;
   private final List<ToastManager.ToastInstance<?>> visibleToasts = new ArrayList<>();
   private final BitSet occupiedSlots = new BitSet(5);
   private final Deque<Toast> queued = Queues.newArrayDeque();

   public ToastManager(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void update() {
      this.visibleToasts.removeIf(var1 -> {
         var1.update();
         if (var1.hasFinishedRendering()) {
            this.occupiedSlots.clear(var1.firstSlotIndex, var1.firstSlotIndex + var1.occupiedSlotCount);
            return true;
         } else {
            return false;
         }
      });
      if (!this.queued.isEmpty() && this.freeSlotCount() > 0) {
         this.queued.removeIf(var1 -> {
            int var2 = var1.occcupiedSlotCount();
            int var3 = this.findFreeSlotsIndex(var2);
            if (var3 == -1) {
               return false;
            } else {
               this.visibleToasts.add(new ToastManager.ToastInstance<>(var1, var3, var2));
               this.occupiedSlots.set(var3, var3 + var2);
               return true;
            }
         });
      }
   }

   public void render(GuiGraphics var1) {
      if (!this.minecraft.options.hideGui) {
         int var2 = var1.guiWidth();

         for (ToastManager.ToastInstance var4 : this.visibleToasts) {
            var4.render(var1, var2);
         }
      }
   }

   private int findFreeSlotsIndex(int var1) {
      if (this.freeSlotCount() >= var1) {
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

   private int freeSlotCount() {
      return 5 - this.occupiedSlots.cardinality();
   }

   @Nullable
   public <T extends Toast> T getToast(Class<? extends T> var1, Object var2) {
      for (ToastManager.ToastInstance var4 : this.visibleToasts) {
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
      this.visibleToasts.clear();
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
      private static final long SLIDE_ANIMATION_DURATION_MS = 600L;
      private final T toast;
      final int firstSlotIndex;
      final int occupiedSlotCount;
      private long animationStartTime = -1L;
      private long becameFullyVisibleAt = -1L;
      private Toast.Visibility visibility = Toast.Visibility.SHOW;
      private long fullyVisibleFor;
      private float visiblePortion;
      private boolean hasFinishedRendering;

      ToastInstance(final T nullx, final int nullxx, final int nullxxx) {
         super();
         this.toast = (T)nullx;
         this.firstSlotIndex = nullxx;
         this.occupiedSlotCount = nullxxx;
      }

      public T getToast() {
         return this.toast;
      }

      public boolean hasFinishedRendering() {
         return this.hasFinishedRendering;
      }

      private void calculateVisiblePortion(long var1) {
         float var3 = Mth.clamp((float)(var1 - this.animationStartTime) / 600.0F, 0.0F, 1.0F);
         var3 *= var3;
         if (this.visibility == Toast.Visibility.HIDE) {
            this.visiblePortion = 1.0F - var3;
         } else {
            this.visiblePortion = var3;
         }
      }

      public void update() {
         long var1 = Util.getMillis();
         if (this.animationStartTime == -1L) {
            this.animationStartTime = var1;
            this.visibility.playSound(ToastManager.this.minecraft.getSoundManager());
         }

         if (this.visibility == Toast.Visibility.SHOW && var1 - this.animationStartTime <= 600L) {
            this.becameFullyVisibleAt = var1;
         }

         this.fullyVisibleFor = var1 - this.becameFullyVisibleAt;
         this.calculateVisiblePortion(var1);
         this.toast.update(ToastManager.this, this.fullyVisibleFor);
         Toast.Visibility var3 = this.toast.getWantedVisibility();
         if (var3 != this.visibility) {
            this.animationStartTime = var1 - (long)((int)((1.0F - this.visiblePortion) * 600.0F));
            this.visibility = var3;
            this.visibility.playSound(ToastManager.this.minecraft.getSoundManager());
         }

         this.hasFinishedRendering = this.visibility == Toast.Visibility.HIDE && var1 - this.animationStartTime > 600L;
      }

      public void render(GuiGraphics var1, int var2) {
         var1.pose().pushPose();
         var1.pose().translate((float)var2 - (float)this.toast.width() * this.visiblePortion, (float)(this.firstSlotIndex * 32), 800.0F);
         this.toast.render(var1, ToastManager.this.minecraft.font, this.fullyVisibleFor);
         var1.pose().popPose();
      }
   }
}
