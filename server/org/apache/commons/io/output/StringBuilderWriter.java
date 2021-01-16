package org.apache.commons.io.output;

import java.io.Serializable;
import java.io.Writer;

public class StringBuilderWriter extends Writer implements Serializable {
   private static final long serialVersionUID = -146927496096066153L;
   private final StringBuilder builder;

   public StringBuilderWriter() {
      super();
      this.builder = new StringBuilder();
   }

   public StringBuilderWriter(int var1) {
      super();
      this.builder = new StringBuilder(var1);
   }

   public StringBuilderWriter(StringBuilder var1) {
      super();
      this.builder = var1 != null ? var1 : new StringBuilder();
   }

   public Writer append(char var1) {
      this.builder.append(var1);
      return this;
   }

   public Writer append(CharSequence var1) {
      this.builder.append(var1);
      return this;
   }

   public Writer append(CharSequence var1, int var2, int var3) {
      this.builder.append(var1, var2, var3);
      return this;
   }

   public void close() {
   }

   public void flush() {
   }

   public void write(String var1) {
      if (var1 != null) {
         this.builder.append(var1);
      }

   }

   public void write(char[] var1, int var2, int var3) {
      if (var1 != null) {
         this.builder.append(var1, var2, var3);
      }

   }

   public StringBuilder getBuilder() {
      return this.builder;
   }

   public String toString() {
      return this.builder.toString();
   }
}
