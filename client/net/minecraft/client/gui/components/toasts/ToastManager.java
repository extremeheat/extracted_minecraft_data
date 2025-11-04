package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

public class ToastManager {
   private static final int SLOT_COUNT = 5;
   private static final int ALL_SLOTS_OCCUPIED = -1;
   final Minecraft minecraft;
   private final List<ToastInstance<?>> visibleToasts = new ArrayList();
   private final BitSet occupiedSlots = new BitSet(5);
   private final Deque<Toast> queued = Queues.newArrayDeque();
   private final Set<SoundEvent> playedToastSounds = new HashSet();
   private @Nullable ToastInstance<NowPlayingToast> nowPlayingToast;

   public ToastManager(Minecraft var1, Options var2) {
      super();
      this.minecraft = var1;
      if ((Boolean)var2.showNowPlayingToast().get()) {
         this.createNowPlayingToast();
      }

   }

   public void update() {
      MutableBoolean var1 = new MutableBoolean(false);
      this.visibleToasts.removeIf((var2) -> {
         Toast.Visibility var3 = var2.visibility;
         var2.update();
         if (var2.visibility != var3 && var1.isFalse()) {
            var1.setTrue();
            var2.visibility.playSound(this.minecraft.getSoundManager());
         }

         if (var2.hasFinishedRendering()) {
            this.occupiedSlots.clear(var2.firstSlotIndex, var2.firstSlotIndex + var2.occupiedSlotCount);
            return true;
         } else {
            return false;
         }
      });
      if (!this.queued.isEmpty() && this.freeSlotCount() > 0) {
         this.queued.removeIf((var1x) -> {
            int var2 = var1x.occcupiedSlotCount();
            int var3 = this.findFreeSlotsIndex(var2);
            if (var3 == -1) {
               return false;
            } else {
               this.visibleToasts.add(new ToastInstance(var1x, var3, var2));
               this.occupiedSlots.set(var3, var3 + var2);
               SoundEvent var4 = var1x.getSoundEvent();
               if (var4 != null && this.playedToastSounds.add(var4)) {
                  this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(var4, 1.0F, 1.0F));
               }

               return true;
            }
         });
      }

      this.playedToastSounds.clear();
      if (this.nowPlayingToast != null) {
         this.nowPlayingToast.update();
      }

   }

   public void render(GuiGraphics var1) {
      if (!this.minecraft.options.hideGui) {
         int var2 = var1.guiWidth();
         if (!this.visibleToasts.isEmpty()) {
            var1.nextStratum();
         }

         for(ToastInstance var4 : this.visibleToasts) {
            var4.render(var1, var2);
         }

         if ((Boolean)this.minecraft.options.showNowPlayingToast().get() && this.nowPlayingToast != null && (this.minecraft.screen == null || !(this.minecraft.screen instanceof PauseScreen))) {
            this.nowPlayingToast.render(var1, var2);
         }

      }
   }

   private int findFreeSlotsIndex(int var1) {
      if (this.freeSlotCount() >= var1) {
         int var2 = 0;

         for(int var3 = 0; var3 < 5; ++var3) {
            if (this.occupiedSlots.get(var3)) {
               var2 = 0;
            } else {
               ++var2;
               if (var2 == var1) {
                  return var3 + 1 - var2;
               }
            }
         }
      }

      return -1;
   }

   private int freeSlotCount() {
      return 5 - this.occupiedSlots.cardinality();
   }

   public <T extends Toast> @Nullable T getToast(Class<? extends T> var1, Object var2) {
      for(ToastInstance var4 : this.visibleToasts) {
         if (var1.isAssignableFrom(var4.getToast().getClass()) && var4.getToast().getToken().equals(var2)) {
            return (T)var4.getToast();
         }
      }

      for(Toast var6 : this.queued) {
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

   public void showNowPlayingToast() {
      if (this.nowPlayingToast != null) {
         this.nowPlayingToast.resetToast();
         ((NowPlayingToast)this.nowPlayingToast.getToast()).showToast(this.minecraft.options);
      }

   }

   public void hideNowPlayingToast() {
      if (this.nowPlayingToast != null) {
         ((NowPlayingToast)this.nowPlayingToast.getToast()).setWantedVisibility(Toast.Visibility.HIDE);
      }

   }

   public void createNowPlayingToast() {
      this.nowPlayingToast = new ToastInstance<NowPlayingToast>(new NowPlayingToast(), 0, 0);
   }

   public void removeNowPlayingToast() {
      this.nowPlayingToast = null;
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public double getNotificationDisplayTimeMultiplier() {
      return (Double)this.minecraft.options.notificationDisplayTime().get();
   }

   class ToastInstance<T extends Toast> {
      private static final long SLIDE_ANIMATION_DURATION_MS = 600L;
      private final T toast;
      final int firstSlotIndex;
      final int occupiedSlotCount;
      private long animationStartTime;
      private long becameFullyVisibleAt;
      Toast.Visibility visibility;
      private long fullyVisibleFor;
      private float visiblePortion;
      protected boolean hasFinishedRendering;

      ToastInstance(final T var2, final int var3, final int var4) {
         super();
         this.toast = var2;
         this.firstSlotIndex = var3;
         this.occupiedSlotCount = var4;
         this.resetToast();
      }

      public T getToast() {
         return this.toast;
      }

      public void resetToast() {
         this.animationStartTime = -1L;
         this.becameFullyVisibleAt = -1L;
         this.visibility = Toast.Visibility.HIDE;
         this.fullyVisibleFor = 0L;
         this.visiblePortion = 0.0F;
         this.hasFinishedRendering = false;
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
            this.visibility = Toast.Visibility.SHOW;
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
         }

         boolean var4 = this.hasFinishedRendering;
         this.hasFinishedRendering = this.visibility == Toast.Visibility.HIDE && var1 - this.animationStartTime > 600L;
         if (this.hasFinishedRendering && !var4) {
            this.toast.onFinishedRendering();
         }

      }

      public void render(GuiGraphics var1, int var2) {
         if (!this.hasFinishedRendering) {
            var1.pose().pushMatrix();
            var1.pose().translate(this.toast.xPos(var2, this.visiblePortion), this.toast.yPos(this.firstSlotIndex));
            this.toast.render(var1, ToastManager.this.minecraft.font, this.fullyVisibleFor);
            var1.pose().popMatrix();
         }
      }
   }
}
