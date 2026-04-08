package com.mojang.blaze3d.systems;

import java.util.OptionalLong;
import org.jspecify.annotations.Nullable;

public class TimerQuery {
   private @Nullable CommandEncoder activeEncoder;
   private @Nullable GpuQuery activeGpuQuery;

   public TimerQuery() {
      super();
   }

   public static TimerQuery getInstance() {
      return TimerQuery.TimerQueryLazyLoader.INSTANCE;
   }

   public boolean isRecording() {
      return this.activeGpuQuery != null;
   }

   public void beginProfile() {
      RenderSystem.assertOnRenderThread();
      if (this.activeGpuQuery != null) {
         throw new IllegalStateException("Current profile not ended");
      } else {
         this.activeEncoder = RenderSystem.getDevice().createCommandEncoder();
         this.activeGpuQuery = this.activeEncoder.timerQueryBegin();
      }
   }

   public FrameProfile endProfile() {
      RenderSystem.assertOnRenderThread();
      if (this.activeGpuQuery != null && this.activeEncoder != null) {
         this.activeEncoder.timerQueryEnd(this.activeGpuQuery);
         FrameProfile frameProfile = new FrameProfile(this.activeGpuQuery);
         this.activeGpuQuery = null;
         this.activeEncoder = null;
         return frameProfile;
      } else {
         throw new IllegalStateException("endProfile called before beginProfile");
      }
   }

   public static class FrameProfile {
      private static final long NO_RESULT = 0L;
      private static final long CANCELLED_RESULT = -1L;
      private final GpuQuery gpuQuery;
      private long timerResult = 0L;

      private FrameProfile(final GpuQuery gpuQuery) {
         super();
         this.gpuQuery = gpuQuery;
      }

      public void cancel() {
         RenderSystem.assertOnRenderThread();
         if (this.timerResult == 0L) {
            this.timerResult = -1L;
            this.gpuQuery.close();
         }
      }

      public boolean isDone() {
         RenderSystem.assertOnRenderThread();
         if (this.timerResult != 0L) {
            return true;
         } else {
            OptionalLong value = this.gpuQuery.getValue();
            if (value.isPresent()) {
               this.timerResult = value.getAsLong();
               this.gpuQuery.close();
               return true;
            } else {
               return false;
            }
         }
      }

      public long get() {
         RenderSystem.assertOnRenderThread();
         if (this.timerResult == 0L) {
            OptionalLong value = this.gpuQuery.getValue();
            if (value.isPresent()) {
               this.timerResult = value.getAsLong();
               this.gpuQuery.close();
            }
         }

         return this.timerResult;
      }
   }

   private static class TimerQueryLazyLoader {
      private static final TimerQuery INSTANCE = instantiate();

      private TimerQueryLazyLoader() {
         super();
      }

      private static TimerQuery instantiate() {
         return new TimerQuery();
      }
   }
}
