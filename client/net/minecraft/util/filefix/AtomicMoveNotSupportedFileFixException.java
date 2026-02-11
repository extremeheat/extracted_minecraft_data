package net.minecraft.util.filefix;

public class AtomicMoveNotSupportedFileFixException extends FileFixException {
   public AtomicMoveNotSupportedFileFixException(final FileSystemCapabilities fileSystemCapabilities) {
      super((Exception)null, fileSystemCapabilities);
   }
}
