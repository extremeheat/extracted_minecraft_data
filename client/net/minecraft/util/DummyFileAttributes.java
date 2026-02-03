package net.minecraft.util;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import org.jspecify.annotations.Nullable;

public abstract class DummyFileAttributes implements BasicFileAttributes {
   public static final BasicFileAttributes DIRECTORY = new DummyFileAttributes() {
      public boolean isRegularFile() {
         return false;
      }

      public boolean isDirectory() {
         return true;
      }
   };
   public static final BasicFileAttributes FILE = new DummyFileAttributes() {
      public boolean isRegularFile() {
         return true;
      }

      public boolean isDirectory() {
         return false;
      }
   };
   private static final FileTime EPOCH = FileTime.fromMillis(0L);

   public DummyFileAttributes() {
      super();
   }

   public FileTime lastModifiedTime() {
      return EPOCH;
   }

   public FileTime lastAccessTime() {
      return EPOCH;
   }

   public FileTime creationTime() {
      return EPOCH;
   }

   public boolean isSymbolicLink() {
      return false;
   }

   public boolean isOther() {
      return false;
   }

   public long size() {
      return 0L;
   }

   public @Nullable Object fileKey() {
      return null;
   }
}
