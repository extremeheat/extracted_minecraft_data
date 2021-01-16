package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

public class ExtensionFileComparator extends AbstractFileComparator implements Serializable {
   private static final long serialVersionUID = 1928235200184222815L;
   public static final Comparator<File> EXTENSION_COMPARATOR = new ExtensionFileComparator();
   public static final Comparator<File> EXTENSION_REVERSE;
   public static final Comparator<File> EXTENSION_INSENSITIVE_COMPARATOR;
   public static final Comparator<File> EXTENSION_INSENSITIVE_REVERSE;
   public static final Comparator<File> EXTENSION_SYSTEM_COMPARATOR;
   public static final Comparator<File> EXTENSION_SYSTEM_REVERSE;
   private final IOCase caseSensitivity;

   public ExtensionFileComparator() {
      super();
      this.caseSensitivity = IOCase.SENSITIVE;
   }

   public ExtensionFileComparator(IOCase var1) {
      super();
      this.caseSensitivity = var1 == null ? IOCase.SENSITIVE : var1;
   }

   public int compare(File var1, File var2) {
      String var3 = FilenameUtils.getExtension(var1.getName());
      String var4 = FilenameUtils.getExtension(var2.getName());
      return this.caseSensitivity.checkCompareTo(var3, var4);
   }

   public String toString() {
      return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
   }

   static {
      EXTENSION_REVERSE = new ReverseComparator(EXTENSION_COMPARATOR);
      EXTENSION_INSENSITIVE_COMPARATOR = new ExtensionFileComparator(IOCase.INSENSITIVE);
      EXTENSION_INSENSITIVE_REVERSE = new ReverseComparator(EXTENSION_INSENSITIVE_COMPARATOR);
      EXTENSION_SYSTEM_COMPARATOR = new ExtensionFileComparator(IOCase.SYSTEM);
      EXTENSION_SYSTEM_REVERSE = new ReverseComparator(EXTENSION_SYSTEM_COMPARATOR);
   }
}
