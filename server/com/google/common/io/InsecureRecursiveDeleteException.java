package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.nio.file.FileSystemException;
import javax.annotation.Nullable;

@Beta
@AndroidIncompatible
@GwtIncompatible
public final class InsecureRecursiveDeleteException extends FileSystemException {
   public InsecureRecursiveDeleteException(@Nullable String var1) {
      super(var1, (String)null, "unable to guarantee security of recursive delete");
   }
}
