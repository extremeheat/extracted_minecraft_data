package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class PathWithAttributes {
   private final Path path;
   private final BasicFileAttributes attributes;

   public PathWithAttributes(Path var1, BasicFileAttributes var2) {
      super();
      this.path = (Path)Objects.requireNonNull(var1, "path");
      this.attributes = (BasicFileAttributes)Objects.requireNonNull(var2, "attributes");
   }

   public String toString() {
      return this.path + " (modified: " + this.attributes.lastModifiedTime() + ")";
   }

   public Path getPath() {
      return this.path;
   }

   public BasicFileAttributes getAttributes() {
      return this.attributes;
   }
}
