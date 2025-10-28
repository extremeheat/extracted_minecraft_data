package net.minecraft.commands.execution;

import net.minecraft.commands.ExecutionCommandSource;
import org.jspecify.annotations.Nullable;

public interface ExecutionControl<T> {
   void queueNext(EntryAction<T> var1);

   void tracer(@Nullable TraceCallbacks var1);

   @Nullable TraceCallbacks tracer();

   Frame currentFrame();

   static <T extends ExecutionCommandSource<T>> ExecutionControl<T> create(final ExecutionContext<T> var0, final Frame var1) {
      return new ExecutionControl<T>() {
         public void queueNext(EntryAction<T> var1x) {
            var0.queueNext(new CommandQueueEntry(var1, var1x));
         }

         public void tracer(@Nullable TraceCallbacks var1x) {
            var0.tracer(var1x);
         }

         public @Nullable TraceCallbacks tracer() {
            return var0.tracer();
         }

         public Frame currentFrame() {
            return var1;
         }
      };
   }
}
