package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class TrueFileFilter implements IOFileFilter, Serializable {
   private static final long serialVersionUID = 8782512160909720199L;
   public static final IOFileFilter TRUE = new TrueFileFilter();
   public static final IOFileFilter INSTANCE;

   protected TrueFileFilter() {
      super();
   }

   public boolean accept(File var1) {
      return true;
   }

   public boolean accept(File var1, String var2) {
      return true;
   }

   static {
      INSTANCE = TRUE;
   }
}
