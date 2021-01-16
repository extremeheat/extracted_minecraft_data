package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class EmptyFileFilter extends AbstractFileFilter implements Serializable {
   private static final long serialVersionUID = 3631422087512832211L;
   public static final IOFileFilter EMPTY = new EmptyFileFilter();
   public static final IOFileFilter NOT_EMPTY;

   protected EmptyFileFilter() {
      super();
   }

   public boolean accept(File var1) {
      if (!var1.isDirectory()) {
         return var1.length() == 0L;
      } else {
         File[] var2 = var1.listFiles();
         return var2 == null || var2.length == 0;
      }
   }

   static {
      NOT_EMPTY = new NotFileFilter(EMPTY);
   }
}
