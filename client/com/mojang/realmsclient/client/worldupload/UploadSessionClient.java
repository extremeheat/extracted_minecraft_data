package com.mojang.realmsclient.client.worldupload;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.UploadProcessingStatusResponseDto;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

final class UploadSessionClient {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final GuardedSerializer SERIALIZER = new GuardedSerializer();
   private static final Duration RENEWAL_WINDOW = Duration.ofMinutes(15L);
   private static final Duration CANCEL_TIMEOUT = Duration.ofSeconds(5L);
   private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(15L);
   private static final Duration PROCESSING_TIMEOUT = Duration.ofMinutes(15L);
   private static final Duration POLL_INTERVAL = Duration.ofSeconds(5L);
   private final RealmsClient realmsClient;
   private final UploadHttpClient client;
   private final boolean allowInsecureUrls;
   private final UploadRetry retry;

   public UploadSessionClient(final RealmsClient realmsClient, final UploadHttpClient client) {
      super();
      this.allowInsecureUrls = RealmsClient.ENVIRONMENT == RealmsClient.Environment.LOCAL;
      this.retry = new UploadRetry();
      this.realmsClient = realmsClient;
      this.client = client;
   }

   public UploadSession createSession(final long realmId, final int slotId, final String archiveFormat, final UploadCancellation cancellation) {
      int attempt = 0;

      while(true) {
         if (attempt < 5) {
            cancellation.throwIfCancelled();

            try {
               String response = (String)cancellation.join(this.realmsClient.createContentV2UploadSession(realmId, slotId, archiveFormat), REQUEST_TIMEOUT);

               try {
                  return UploadSession.parseCreateResponse(response, this.allowInsecureUrls);
               } catch (IllegalArgumentException var12) {
                  throw new ContentV2UploadException(ContentV2UploadException.Error.INVALID_SESSION_RESPONSE);
               }
            } catch (CompletionException e) {
               Throwable failure = e.getCause();
               if (isTransient(failure) && attempt + 1 != 5) {
                  Duration delay;
                  label33: {
                     if (failure instanceof RealmsServiceException) {
                        RealmsServiceException serviceException = (RealmsServiceException)failure;
                        if (serviceException.retryAfterHeader != null) {
                           delay = this.retry.delayFromHeader(serviceException.retryAfterHeader, attempt);
                           break label33;
                        }
                     }

                     if (failure instanceof RetryCallException) {
                        RetryCallException retryCallException = (RetryCallException)failure;
                        delay = Duration.ofSeconds((long)retryCallException.delaySeconds);
                     } else {
                        delay = this.retry.delay((HttpResponse)null, attempt);
                     }
                  }

                  this.retry.sleep(delay, cancellation);
                  ++attempt;
                  continue;
               }

               throw new ContentV2UploadException(ContentV2UploadException.Error.SESSION_CREATE_FAILED);
            }
         }

         throw new ContentV2UploadException(ContentV2UploadException.Error.SESSION_CREATE_FAILED);
      }
   }

   public UploadSession renewIfRequired(final UploadSession session, final UploadCancellation cancellation) {
      cancellation.throwIfCancelled();
      if (Instant.now().isBefore(session.expiresAt().minus(RENEWAL_WINDOW))) {
         return session;
      } else {
         String response = this.sendWithRetries(session.renewUrl(), session.token(), "POST", ContentV2UploadException.Error.RENEW_FAILED, cancellation);

         try {
            return session.applyRenewalResponse(response, this.allowInsecureUrls);
         } catch (IllegalArgumentException var5) {
            throw new ContentV2UploadException(ContentV2UploadException.Error.RENEW_FAILED);
         }
      }
   }

   public ProcessingStatus complete(final UploadSession session, final UploadCancellation cancellation) {
      try {
         HttpResponse<String> response = this.send(session.completeUrl(), session.token(), "POST", REQUEST_TIMEOUT, ContentV2UploadException.Error.COMPLETE_FAILED, cancellation);
         if (UploadRetry.isSuccessful(response.statusCode())) {
            return UploadSessionClient.ProcessingStatus.QUEUED;
         }

         if (!UploadRetry.isTransient(response.statusCode())) {
            throw new ContentV2UploadException(ContentV2UploadException.Error.COMPLETE_FAILED);
         }
      } catch (CompletionException e) {
         if (!isTransient(e.getCause())) {
            throw new ContentV2UploadException(ContentV2UploadException.Error.COMPLETE_FAILED);
         }
      }

      ProcessingStatus status = this.requestStatus(session, cancellation);
      if (status == UploadSessionClient.ProcessingStatus.FAILED) {
         throw new ContentV2UploadException(ContentV2UploadException.Error.PROCESSING_FAILED);
      } else {
         return status;
      }
   }

   public void pollUntilCompleted(final UploadSession session, final UploadCancellation cancellation) {
      Instant deadline = Instant.now().plus(PROCESSING_TIMEOUT);

      while(Instant.now().isBefore(deadline)) {
         ProcessingStatus status = this.requestStatus(session, cancellation);
         cancellation.throwIfCancelled();
         switch (status.ordinal()) {
            case 0:
            case 1:
               this.retry.sleep(POLL_INTERVAL, cancellation);
               break;
            case 2:
               return;
            case 3:
               throw new ContentV2UploadException(ContentV2UploadException.Error.PROCESSING_FAILED);
         }
      }

      throw new ContentV2UploadException(ContentV2UploadException.Error.PROCESSING_FAILED);
   }

   public void cancelBestEffort(final UploadSession session) {
      try {
         HttpRequest request = actionRequest(session.cancelUrl(), session.token(), "POST", CANCEL_TIMEOUT);
         CompletableFuture<HttpResponse<Void>> response = this.client.sendAsync(request, BodyHandlers.discarding());
         response.copy().orTimeout(CANCEL_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS).whenComplete((result, failure) -> {
            if (failure instanceof TimeoutException) {
               response.cancel(true);
            }

            if (failure != null) {
               LOGGER.warn("Failed to cancel Realms upload ({})", failure.getClass().getSimpleName());
            } else if (!UploadRetry.isSuccessful(result.statusCode())) {
               LOGGER.warn("Failed to cancel Realms upload: HTTP {}", result.statusCode());
            }

         });
      } catch (IllegalStateException | RejectedExecutionException | IllegalArgumentException e) {
         LOGGER.warn("Failed to send Realms upload cancellation ({})", e.getClass().getSimpleName());
      }

   }

   private ProcessingStatus requestStatus(final UploadSession session, final UploadCancellation cancellation) {
      return parseStatus(this.sendWithRetries(session.statusUrl(), session.token(), "GET", ContentV2UploadException.Error.PROCESSING_FAILED, cancellation));
   }

   private String sendWithRetries(final URI uri, final String token, final String method, final ContentV2UploadException.Error error, final UploadCancellation cancellation) {
      Instant deadline = Instant.now().plus(REQUEST_TIMEOUT);
      int attempt = 0;

      while(true) {
         if (attempt < 5) {
            cancellation.throwIfCancelled();
            Duration remaining = this.remaining(deadline);
            if (!remaining.isZero()) {
               HttpResponse<String> response = null;

               try {
                  response = this.send(uri, token, method, remaining, error, cancellation);
               } catch (CompletionException e) {
                  if (!isTransient(e.getCause()) || attempt + 1 == 5) {
                     throw new ContentV2UploadException(error);
                  }
               }

               if (response != null) {
                  if (UploadRetry.isSuccessful(response.statusCode())) {
                     return (String)response.body();
                  }

                  if (!UploadRetry.isTransient(response.statusCode()) || attempt + 1 == 5) {
                     throw new ContentV2UploadException(error);
                  }
               }

               Duration delay = this.retry.delay(response, attempt);
               Duration timeLeft = this.remaining(deadline);
               this.retry.sleep(delay.compareTo(timeLeft) < 0 ? delay : timeLeft, cancellation);
               ++attempt;
               continue;
            }
         }

         throw new ContentV2UploadException(error);
      }
   }

   private HttpResponse<String> send(final URI uri, final String token, final String method, final Duration timeout, final ContentV2UploadException.Error error, final UploadCancellation cancellation) {
      cancellation.throwIfCancelled();

      try {
         HttpRequest request = actionRequest(uri, token, method, timeout);
         return (HttpResponse)cancellation.join(this.client.sendAsync(request, BodyHandlers.ofString(StandardCharsets.UTF_8)), timeout);
      } catch (IllegalArgumentException var8) {
         throw new ContentV2UploadException(error);
      }
   }

   private static HttpRequest actionRequest(final URI uri, final String token, final String method, final Duration timeout) {
      return HttpRequest.newBuilder(uri).timeout(timeout).header("Authorization", "Bearer " + token).header("X-Realms-Platform", "Java").header("Content-Type", "application/json").method(method, BodyPublishers.noBody()).build();
   }

   private static ProcessingStatus parseStatus(final String response) {
      try {
         UploadProcessingStatusResponseDto responseDto = (UploadProcessingStatusResponseDto)SERIALIZER.fromJson(response, UploadProcessingStatusResponseDto.class);
         UploadProcessingStatusResponseDto.Result result = responseDto == null ? null : responseDto.result();
         String status = result == null ? null : result.status();
         if (status != null && !status.isBlank()) {
            ProcessingStatus var10000;
            switch (status) {
               case "Queued" -> var10000 = UploadSessionClient.ProcessingStatus.QUEUED;
               case "Processing" -> var10000 = UploadSessionClient.ProcessingStatus.PROCESSING;
               case "Completed" -> var10000 = UploadSessionClient.ProcessingStatus.COMPLETED;
               case "Failed" -> var10000 = UploadSessionClient.ProcessingStatus.FAILED;
               default -> throw new ContentV2UploadException(ContentV2UploadException.Error.UNEXPECTED_PROCESSING_STATUS);
            }

            return var10000;
         } else {
            throw new IllegalArgumentException("Missing or empty string field: status");
         }
      } catch (IllegalArgumentException | JsonParseException var6) {
         throw new ContentV2UploadException(ContentV2UploadException.Error.UNEXPECTED_PROCESSING_STATUS);
      }
   }

   private Duration remaining(final Instant deadline) {
      Duration remaining = Duration.between(Instant.now(), deadline);
      return remaining.isNegative() ? Duration.ZERO : remaining;
   }

   private static boolean isTransient(final @Nullable Throwable failure) {
      if (!(failure instanceof IOException) && !(failure instanceof RetryCallException)) {
         if (failure instanceof RealmsServiceException) {
            RealmsServiceException serviceException = (RealmsServiceException)failure;
            RealmsError error = serviceException.realmsError;
            int var10000;
            if (error instanceof RealmsError.ErrorWithJsonPayload) {
               RealmsError.ErrorWithJsonPayload jsonError = (RealmsError.ErrorWithJsonPayload)error;
               var10000 = jsonError.httpCode();
            } else {
               var10000 = error.errorCode();
            }

            return UploadRetry.isTransient(var10000);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static enum ProcessingStatus {
      QUEUED,
      PROCESSING,
      COMPLETED,
      FAILED;

      private ProcessingStatus() {
      }

      // $FF: synthetic method
      private static ProcessingStatus[] $values() {
         return new ProcessingStatus[]{QUEUED, PROCESSING, COMPLETED, FAILED};
      }
   }
}
