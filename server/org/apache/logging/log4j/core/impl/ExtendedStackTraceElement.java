package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;

public final class ExtendedStackTraceElement implements Serializable {
   private static final long serialVersionUID = -2171069569241280505L;
   private final ExtendedClassInfo extraClassInfo;
   private final StackTraceElement stackTraceElement;

   public ExtendedStackTraceElement(StackTraceElement var1, ExtendedClassInfo var2) {
      super();
      this.stackTraceElement = var1;
      this.extraClassInfo = var2;
   }

   public ExtendedStackTraceElement(String var1, String var2, String var3, int var4, boolean var5, String var6, String var7) {
      this(new StackTraceElement(var1, var2, var3, var4), new ExtendedClassInfo(var5, var6, var7));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ExtendedStackTraceElement)) {
         return false;
      } else {
         ExtendedStackTraceElement var2 = (ExtendedStackTraceElement)var1;
         if (this.extraClassInfo == null) {
            if (var2.extraClassInfo != null) {
               return false;
            }
         } else if (!this.extraClassInfo.equals(var2.extraClassInfo)) {
            return false;
         }

         if (this.stackTraceElement == null) {
            if (var2.stackTraceElement != null) {
               return false;
            }
         } else if (!this.stackTraceElement.equals(var2.stackTraceElement)) {
            return false;
         }

         return true;
      }
   }

   public String getClassName() {
      return this.stackTraceElement.getClassName();
   }

   public boolean getExact() {
      return this.extraClassInfo.getExact();
   }

   public ExtendedClassInfo getExtraClassInfo() {
      return this.extraClassInfo;
   }

   public String getFileName() {
      return this.stackTraceElement.getFileName();
   }

   public int getLineNumber() {
      return this.stackTraceElement.getLineNumber();
   }

   public String getLocation() {
      return this.extraClassInfo.getLocation();
   }

   public String getMethodName() {
      return this.stackTraceElement.getMethodName();
   }

   public StackTraceElement getStackTraceElement() {
      return this.stackTraceElement;
   }

   public String getVersion() {
      return this.extraClassInfo.getVersion();
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.extraClassInfo == null ? 0 : this.extraClassInfo.hashCode());
      var3 = 31 * var3 + (this.stackTraceElement == null ? 0 : this.stackTraceElement.hashCode());
      return var3;
   }

   public boolean isNativeMethod() {
      return this.stackTraceElement.isNativeMethod();
   }

   void renderOn(StringBuilder var1, TextRenderer var2) {
      this.render(this.stackTraceElement, var1, var2);
      var2.render(" ", var1, "Text");
      this.extraClassInfo.renderOn(var1, var2);
   }

   private void render(StackTraceElement var1, StringBuilder var2, TextRenderer var3) {
      String var4 = var1.getFileName();
      int var5 = var1.getLineNumber();
      var3.render(this.getClassName(), var2, "StackTraceElement.ClassName");
      var3.render(".", var2, "StackTraceElement.ClassMethodSeparator");
      var3.render(var1.getMethodName(), var2, "StackTraceElement.MethodName");
      if (var1.isNativeMethod()) {
         var3.render("(Native Method)", var2, "StackTraceElement.NativeMethod");
      } else if (var4 != null && var5 >= 0) {
         var3.render("(", var2, "StackTraceElement.Container");
         var3.render(var4, var2, "StackTraceElement.FileName");
         var3.render(":", var2, "StackTraceElement.ContainerSeparator");
         var3.render(Integer.toString(var5), var2, "StackTraceElement.LineNumber");
         var3.render(")", var2, "StackTraceElement.Container");
      } else if (var4 != null) {
         var3.render("(", var2, "StackTraceElement.Container");
         var3.render(var4, var2, "StackTraceElement.FileName");
         var3.render(")", var2, "StackTraceElement.Container");
      } else {
         var3.render("(", var2, "StackTraceElement.Container");
         var3.render("Unknown Source", var2, "StackTraceElement.UnknownSource");
         var3.render(")", var2, "StackTraceElement.Container");
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.renderOn(var1, PlainTextRenderer.getInstance());
      return var1.toString();
   }
}
