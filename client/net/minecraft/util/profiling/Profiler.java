package net.minecraft.util.profiling;

import com.mojang.jtracy.TracyClient;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class Profiler {
   private static final ThreadLocal<TracyZoneFiller> TRACY_FILLER = ThreadLocal.withInitial(TracyZoneFiller::new);
   private static final ThreadLocal<ProfilerFiller> ACTIVE = new ThreadLocal();
   private static final AtomicInteger ACTIVE_COUNT = new AtomicInteger();

   private Profiler() {
      super();
   }

   public static Scope use(ProfilerFiller var0) {
      startUsing(var0);
      return Profiler::stopUsing;
   }

   private static void startUsing(ProfilerFiller var0) {
      if (ACTIVE.get() != null) {
         throw new IllegalStateException("Profiler is already active");
      } else {
         ProfilerFiller var1 = decorateFiller(var0);
         ACTIVE.set(var1);
         ACTIVE_COUNT.incrementAndGet();
         var1.startTick();
      }
   }

   private static void stopUsing() {
      ProfilerFiller var0 = (ProfilerFiller)ACTIVE.get();
      if (var0 == null) {
         throw new IllegalStateException("Profiler was not active");
      } else {
         ACTIVE.remove();
         ACTIVE_COUNT.decrementAndGet();
         var0.endTick();
      }
   }

   private static ProfilerFiller decorateFiller(ProfilerFiller var0) {
      return ProfilerFiller.combine(getDefaultFiller(), var0);
   }

   public static ProfilerFiller get() {
      return ACTIVE_COUNT.get() == 0 ? getDefaultFiller() : (ProfilerFiller)Objects.requireNonNullElseGet((ProfilerFiller)ACTIVE.get(), Profiler::getDefaultFiller);
   }

   private static ProfilerFiller getDefaultFiller() {
      return (ProfilerFiller)(TracyClient.isAvailable() ? (ProfilerFiller)TRACY_FILLER.get() : InactiveProfiler.INSTANCE);
   }

   public interface Scope extends AutoCloseable {
      void close();
   }
}
