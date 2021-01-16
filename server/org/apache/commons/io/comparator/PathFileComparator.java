package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.IOCase;

public class PathFileComparator extends AbstractFileComparator implements Serializable {
   private static final long serialVersionUID = 6527501707585768673L;
   public static final Comparator<File> PATH_COMPARATOR = new PathFileComparator();
   public static final Comparator<File> PATH_REVERSE;
   public static final Comparator<File> PATH_INSENSITIVE_COMPARATOR;
   public static final Comparator<File> PATH_INSENSITIVE_REVERSE;
   public static final Comparator<File> PATH_SYSTEM_COMPARATOR;
   public static final Comparator<File> PATH_SYSTEM_REVERSE;
   private final IOCase caseSensitivity;

   public PathFileComparator() {
      super();
      this.caseSensitivity = IOCase.SENSITIVE;
   }

   public PathFileComparator(IOCase var1) {
      super();
      this.caseSensitivity = var1 == null ? IOCase.SENSITIVE : var1;
   }

   public int compare(File var1, File var2) {
      return this.caseSensitivity.checkCompareTo(var1.getPath(), var2.getPath());
   }

   public String toString() {
      return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
   }

   static {
      PATH_REVERSE = new ReverseComparator(PATH_COMPARATOR);
      PATH_INSENSITIVE_COMPARATOR = new PathFileComparator(IOCase.INSENSITIVE);
      PATH_INSENSITIVE_REVERSE = new ReverseComparator(PATH_INSENSITIVE_COMPARATOR);
      PATH_SYSTEM_COMPARATOR = new PathFileComparator(IOCase.SYSTEM);
      PATH_SYSTEM_REVERSE = new ReverseComparator(PATH_SYSTEM_COMPARATOR);
   }
}
