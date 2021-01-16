package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class FileFileFilter extends AbstractFileFilter implements Serializable {
   private static final long serialVersionUID = 5345244090827540862L;
   public static final IOFileFilter FILE = new FileFileFilter();

   protected FileFileFilter() {
      super();
   }

   public boolean accept(File var1) {
      return var1.isFile();
   }
}
