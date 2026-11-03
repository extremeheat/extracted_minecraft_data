package com.mojang.realmsclient.client.worldupload;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;

public final class UploadCancellation {
   private final AtomicBoolean cancelled = new AtomicBoolean();
   private final AtomicReference<@Nullable CompletableFuture<?>> activeRequest = new AtomicReference();

   public UploadCancellation() {
      super();
   }

   public void cancel() {
      if (this.cancelled.compareAndSet(false, true)) {
         CompletableFuture<?> request = (CompletableFuture)this.activeRequest.get();
         if (request != null) {
            request.cancel(true);
         }

      }
   }

   public boolean isCancelled() {
      return this.cancelled.get();
   }

   public void throwIfCancelled() {
      if (this.isCancelled()) {
         throw new RealmsUploadCanceledException();
      }
   }

   public <T> T join(final CompletableFuture<T> request) {
      return (T)this.await(request, (Duration)null);
   }

   public <T> T join(final CompletableFuture<T> request, final Duration timeout) {
      if (!timeout.isZero() && !timeout.isNegative()) {
         return (T)this.await(request, timeout);
      } else {
         throw new IllegalArgumentException("Upload timeout must be positive");
      }
   }

   private <T> T await(final CompletableFuture<T> request, final @Nullable Duration timeout) {
      this.activeRequest.set(request);
      if (this.isCancelled()) {
         request.cancel(true);
      }

      Object var3;
      try {
         var3 = timeout == null ? request.join() : request.get(TimeUnit.NANOSECONDS.convert(timeout), TimeUnit.NANOSECONDS);
      } catch (CancellationException var11) {
         throw new RealmsUploadCanceledException();
      } catch (InterruptedException var12) {
         Thread.currentThread().interrupt();
         request.cancel(true);
         throw new RealmsUploadCanceledException();
      } catch (ExecutionException e) {
         throw new CompletionException(e.getCause());
      } catch (TimeoutException e) {
         request.cancel(true);
         this.throwIfCancelled();
         HttpTimeoutException timeoutException = new HttpTimeoutException("Realms upload request timed out");
         timeoutException.initCause(e);
         throw new CompletionException(timeoutException);
      } finally {
         this.activeRequest.compareAndSet(request, (Object)null);
      }

      return (T)var3;
   }
}
