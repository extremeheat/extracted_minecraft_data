package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

public class DefaultFileComparator extends AbstractFileComparator implements Serializable {
   private static final long serialVersionUID = 3260141861365313518L;
   public static final Comparator<File> DEFAULT_COMPARATOR = new DefaultFileComparator();
   public static final Comparator<File> DEFAULT_REVERSE;

   public DefaultFileComparator() {
      super();
   }

   public int compare(File var1, File var2) {
      return var1.compareTo(var2);
   }

   static {
      DEFAULT_REVERSE = new ReverseComparator(DEFAULT_COMPARATOR);
   }
}
