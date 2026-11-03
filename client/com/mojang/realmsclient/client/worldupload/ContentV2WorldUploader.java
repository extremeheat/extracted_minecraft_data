package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.RealmsClientConfig;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;

public final class ContentV2WorldUploader implements AutoCloseable {
   private final UploadHttpClient httpClient;
   private final UploadSessionClient sessionClient;
   private final AzureBlobUploader blobUploader;
   private final RealmsWorldUploadStatusTracker statusTracker;
   private final UploadCancellation cancellation;
   private final AtomicReference<State> state;
   private volatile @Nullable UploadSession session;
   private final AtomicBoolean serverCancellationSent;

   public ContentV2WorldUploader(final RealmsClient realmsClient, final RealmsWorldUploadStatusTracker statusTracker, final UploadCancellation cancellation) {
      super();
      this.state = new AtomicReference(ContentV2WorldUploader.State.RUNNING);
      this.serverCancellationSent = new AtomicBoolean();
      this.httpClient = UploadHttpClient.create(RealmsClientConfig.getProxy());
      this.sessionClient = new UploadSessionClient(realmsClient, this.httpClient);
      this.blobUploader = new AzureBlobUploader(this.httpClient);
      this.statusTracker = statusTracker;
      this.cancellation = cancellation;
   }

   public void upload(final Path archive, final long realmId, final int slotId, final String archiveFormat) {
      try {
         this.checkActive();
         UploadSession createdSession = this.sessionClient.createSession(realmId, slotId, archiveFormat, this.cancellation);
         this.session = createdSession;
         this.cancellation.throwIfCancelled();
         UploadPlan plan = UploadPlan.create(archive, archiveFormat, createdSession);
         this.checkActive();
         this.statusTracker.setUploading();
         this.blobUploader.upload(plan, this::renewSessionIfRequired, this.statusTracker.getUploadStatus(), this.cancellation, () -> {
            this.checkActive();
            this.statusTracker.setCommitting();
         });
         if (!plan.usesBlocks()) {
            this.statusTracker.setCommitting();
         }

         this.checkActive();
         UploadSession completionSession = this.renewSessionIfRequired();
         UploadSessionClient.ProcessingStatus completionStatus = this.sessionClient.complete(completionSession, this.cancellation);
         if (completionStatus != UploadSessionClient.ProcessingStatus.COMPLETED) {
            this.checkActive();
            this.statusTracker.setProcessing();
            this.sessionClient.pollUntilCompleted(completionSession, this.cancellation);
         }

         this.cancellation.throwIfCancelled();
         if (!this.setTerminal(ContentV2WorldUploader.State.SUCCEEDED)) {
            throw new RealmsUploadCanceledException();
         }
      } catch (RealmsUploadException e) {
         this.setTerminal(!(e instanceof RealmsUploadCanceledException) && !this.cancellation.isCancelled() ? ContentV2WorldUploader.State.FAILED : ContentV2WorldUploader.State.CANCELED);
         if (this.state.get() == ContentV2WorldUploader.State.CANCELED) {
            throw new RealmsUploadCanceledException();
         }

         throw e;
      } finally {
         if (this.state.get() != ContentV2WorldUploader.State.SUCCEEDED) {
            this.setTerminal(this.cancellation.isCancelled() ? ContentV2WorldUploader.State.CANCELED : ContentV2WorldUploader.State.FAILED);
            this.cancelServerBestEffort();
         }

      }

   }

   public void cancel() {
      if (this.setTerminal(ContentV2WorldUploader.State.CANCELED)) {
         this.cancellation.cancel();
         this.cancelServerBestEffort();
      }

   }

   private void checkActive() {
      this.cancellation.throwIfCancelled();
      State current = (State)this.state.get();
      if (current == ContentV2WorldUploader.State.CANCELED) {
         throw new RealmsUploadCanceledException();
      } else if (current != ContentV2WorldUploader.State.RUNNING) {
         throw new IllegalStateException("Upload already reached a terminal state");
      }
   }

   private boolean setTerminal(final State terminal) {
      return this.state.compareAndSet(ContentV2WorldUploader.State.RUNNING, terminal);
   }

   private UploadSession renewSessionIfRequired() {
      UploadSession current = this.session;
      if (current == null) {
         throw new IllegalStateException("Upload session has not been created");
      } else {
         UploadSession renewed = this.sessionClient.renewIfRequired(current, this.cancellation);
         this.session = renewed;
         return renewed;
      }
   }

   private void cancelServerBestEffort() {
      UploadSession current = this.session;
      if (current != null && this.serverCancellationSent.compareAndSet(false, true)) {
         this.sessionClient.cancelBestEffort(current);
      }

   }

   public void close() {
      this.httpClient.close();
   }

   private static enum State {
      RUNNING,
      SUCCEEDED,
      FAILED,
      CANCELED;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{RUNNING, SUCCEEDED, FAILED, CANCELED};
      }
   }
}
