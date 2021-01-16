package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

public class LastModifiedFileComparator extends AbstractFileComparator implements Serializable {
   private static final long serialVersionUID = 7372168004395734046L;
   public static final Comparator<File> LASTMODIFIED_COMPARATOR = new LastModifiedFileComparator();
   public static final Comparator<File> LASTMODIFIED_REVERSE;

   public LastModifiedFileComparator() {
      super();
   }

   public int compare(File var1, File var2) {
      long var3 = var1.lastModified() - var2.lastModified();
      if (var3 < 0L) {
         return -1;
      } else {
         return var3 > 0L ? 1 : 0;
      }
   }

   static {
      LASTMODIFIED_REVERSE = new ReverseComparator(LASTMODIFIED_COMPARATOR);
   }
}
