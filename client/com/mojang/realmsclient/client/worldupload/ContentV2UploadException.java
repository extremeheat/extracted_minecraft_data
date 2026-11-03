package com.mojang.realmsclient.client.worldupload;

import net.minecraft.network.chat.Component;

public class ContentV2UploadException extends RealmsUploadException {
   private final Error error;

   public ContentV2UploadException(final Error error) {
      super();
      this.error = error;
   }

   public String getMessage() {
      return this.error.name();
   }

   public Component getStatusMessage() {
      return Component.translatable("mco.upload.failed", Component.translatable(this.error.translationKey));
   }

   public static enum Error {
      SESSION_CREATE_FAILED("mco.upload.error.session_create_failed"),
      INVALID_SESSION_RESPONSE("mco.upload.error.invalid_session_response"),
      INVALID_ARCHIVE("mco.upload.error.invalid_archive"),
      FILE_TOO_LARGE("mco.upload.error.file_too_large"),
      UNSUPPORTED_ARCHIVE_FORMAT("mco.upload.error.unsupported_archive_format"),
      BLOCK_UPLOAD_UNSUPPORTED("mco.upload.error.block_upload_unsupported"),
      SINGLE_UPLOAD_FAILED("mco.upload.error.single_upload_failed"),
      BLOCK_UPLOAD_FAILED("mco.upload.error.block_upload_failed"),
      BLOCK_LIST_FAILED("mco.upload.error.block_list_failed"),
      COMPLETE_FAILED("mco.upload.error.complete_failed"),
      RENEW_FAILED("mco.upload.error.renew_failed"),
      PROCESSING_FAILED("mco.upload.error.processing_failed"),
      UNEXPECTED_PROCESSING_STATUS("mco.upload.error.unexpected_processing_status");

      private final String translationKey;

      private Error(final String translationKey) {
         this.translationKey = translationKey;
      }

      // $FF: synthetic method
      private static Error[] $values() {
         return new Error[]{SESSION_CREATE_FAILED, INVALID_SESSION_RESPONSE, INVALID_ARCHIVE, FILE_TOO_LARGE, UNSUPPORTED_ARCHIVE_FORMAT, BLOCK_UPLOAD_UNSUPPORTED, SINGLE_UPLOAD_FAILED, BLOCK_UPLOAD_FAILED, BLOCK_LIST_FAILED, COMPLETE_FAILED, RENEW_FAILED, PROCESSING_FAILED, UNEXPECTED_PROCESSING_STATUS};
      }
   }
}
