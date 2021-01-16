package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.IOCase;

public class NameFileComparator extends AbstractFileComparator implements Serializable {
   private static final long serialVersionUID = 8397947749814525798L;
   public static final Comparator<File> NAME_COMPARATOR = new NameFileComparator();
   public static final Comparator<File> NAME_REVERSE;
   public static final Comparator<File> NAME_INSENSITIVE_COMPARATOR;
   public static final Comparator<File> NAME_INSENSITIVE_REVERSE;
   public static final Comparator<File> NAME_SYSTEM_COMPARATOR;
   public static final Comparator<File> NAME_SYSTEM_REVERSE;
   private final IOCase caseSensitivity;

   public NameFileComparator() {
      super();
      this.caseSensitivity = IOCase.SENSITIVE;
   }

   public NameFileComparator(IOCase var1) {
      super();
      this.caseSensitivity = var1 == null ? IOCase.SENSITIVE : var1;
   }

   public int compare(File var1, File var2) {
      return this.caseSensitivity.checkCompareTo(var1.getName(), var2.getName());
   }

   public String toString() {
      return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
   }

   static {
      NAME_REVERSE = new ReverseComparator(NAME_COMPARATOR);
      NAME_INSENSITIVE_COMPARATOR = new NameFileComparator(IOCase.INSENSITIVE);
      NAME_INSENSITIVE_REVERSE = new ReverseComparator(NAME_INSENSITIVE_COMPARATOR);
      NAME_SYSTEM_COMPARATOR = new NameFileComparator(IOCase.SYSTEM);
      NAME_SYSTEM_REVERSE = new ReverseComparator(NAME_SYSTEM_COMPARATOR);
   }
}
