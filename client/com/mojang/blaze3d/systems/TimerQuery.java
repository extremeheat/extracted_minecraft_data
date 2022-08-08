package com.mojang.blaze3d.systems;

import java.util.Optional;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;

public class TimerQuery {
   private int nextQueryName;

   public TimerQuery() {
      super();
   }

   public static Optional<TimerQuery> getInstance() {
      return TimerQuery.TimerQueryLazyLoader.INSTANCE;
   }

   public void beginProfile() {
      RenderSystem.assertOnRenderThread();
      if (this.nextQueryName != 0) {
         throw new IllegalStateException("Current profile not ended");
      } else {
         this.nextQueryName = GL32C.glGenQueries();
         GL32C.glBeginQuery(35007, this.nextQueryName);
      }
   }

   public FrameProfile endProfile() {
      RenderSystem.assertOnRenderThread();
      if (this.nextQueryName == 0) {
         throw new IllegalStateException("endProfile called before beginProfile");
      } else {
         GL32C.glEndQuery(35007);
         FrameProfile var1 = new FrameProfile(this.nextQueryName);
         this.nextQueryName = 0;
         return var1;
      }
   }

   static class TimerQueryLazyLoader {
      static final Optional<TimerQuery> INSTANCE = Optional.ofNullable(instantiate());

      private TimerQueryLazyLoader() {
         super();
      }

      @Nullable
      private static TimerQuery instantiate() {
         return !GL.getCapabilities().GL_ARB_timer_query ? null : new TimerQuery();
      }
   }

   public static class FrameProfile {
      private static final long NO_RESULT = 0L;
      private static final long CANCELLED_RESULT = -1L;
      private final int queryName;
      private long result;

      FrameProfile(int var1) {
         super();
         this.queryName = var1;
      }

      public void cancel() {
         RenderSystem.assertOnRenderThread();
         if (this.result == 0L) {
            this.result = -1L;
            GL32C.glDeleteQueries(this.queryName);
         }
      }

      public boolean isDone() {
         RenderSystem.assertOnRenderThread();
         if (this.result != 0L) {
            return true;
         } else if (1 == GL32C.glGetQueryObjecti(this.queryName, 34919)) {
            this.result = ARBTimerQuery.glGetQueryObjecti64(this.queryName, 34918);
            GL32C.glDeleteQueries(this.queryName);
            return true;
         } else {
            return false;
         }
      }

      public long get() {
         RenderSystem.assertOnRenderThread();
         if (this.result == 0L) {
            this.result = ARBTimerQuery.glGetQueryObjecti64(this.queryName, 34918);
            GL32C.glDeleteQueries(this.queryName);
         }

         return this.result;
      }
   }
}
