package com.mojang.realmsclient.client.worldupload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public record UploadPlan(Path archive, long fileSize, long blockSize) {
   public UploadPlan {
      super();
   }

   public static UploadPlan create(final Path archive, final String archiveFormat, final UploadSession session) {
      BasicFileAttributes attributes;
      try {
         attributes = Files.readAttributes(archive, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      } catch (IOException var12) {
         throw new ContentV2UploadException(ContentV2UploadException.Error.INVALID_ARCHIVE);
      }

      long fileSize = attributes.size();
      if (attributes.isRegularFile() && fileSize > 0L) {
         if (!session.acceptedArchiveFormats().contains("." + archiveFormat)) {
            throw new ContentV2UploadException(ContentV2UploadException.Error.UNSUPPORTED_ARCHIVE_FORMAT);
         } else if (fileSize > session.maxUploadSizeBytes()) {
            throw new ContentV2UploadException(ContentV2UploadException.Error.FILE_TOO_LARGE);
         } else if (fileSize <= session.maxSinglePutSizeBytes()) {
            return new UploadPlan(archive, fileSize, 0L);
         } else if (!session.supportsBlockUpload()) {
            throw new ContentV2UploadException(ContentV2UploadException.Error.BLOCK_UPLOAD_UNSUPPORTED);
         } else {
            long blockSize = session.recommendedBlockSizeBytes();
            long maxBlockCount = session.maxBlockCount();
            if (blockSize > 0L && maxBlockCount > 0L) {
               long blockCount = 1L + (fileSize - 1L) / blockSize;
               if (blockCount > maxBlockCount) {
                  throw new ContentV2UploadException(ContentV2UploadException.Error.FILE_TOO_LARGE);
               } else {
                  return new UploadPlan(archive, fileSize, blockSize);
               }
            } else {
               throw new ContentV2UploadException(ContentV2UploadException.Error.INVALID_SESSION_RESPONSE);
            }
         }
      } else {
         throw new ContentV2UploadException(ContentV2UploadException.Error.INVALID_ARCHIVE);
      }
   }

   public boolean usesBlocks() {
      return this.blockSize > 0L;
   }
}
