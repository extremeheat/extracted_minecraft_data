package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class FalseFileFilter implements IOFileFilter, Serializable {
   private static final long serialVersionUID = 6210271677940926200L;
   public static final IOFileFilter FALSE = new FalseFileFilter();
   public static final IOFileFilter INSTANCE;

   protected FalseFileFilter() {
      super();
   }

   public boolean accept(File var1) {
      return false;
   }

   public boolean accept(File var1, String var2) {
      return false;
   }

   static {
      INSTANCE = FALSE;
   }
}
