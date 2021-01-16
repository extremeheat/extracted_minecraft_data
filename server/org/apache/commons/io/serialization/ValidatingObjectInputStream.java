package org.apache.commons.io.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ValidatingObjectInputStream extends ObjectInputStream {
   private final List<ClassNameMatcher> acceptMatchers = new ArrayList();
   private final List<ClassNameMatcher> rejectMatchers = new ArrayList();

   public ValidatingObjectInputStream(InputStream var1) throws IOException {
      super(var1);
   }

   private void validateClassName(String var1) throws InvalidClassException {
      Iterator var2 = this.rejectMatchers.iterator();

      while(var2.hasNext()) {
         ClassNameMatcher var3 = (ClassNameMatcher)var2.next();
         if (var3.matches(var1)) {
            this.invalidClassNameFound(var1);
         }
      }

      boolean var5 = false;
      Iterator var6 = this.acceptMatchers.iterator();

      while(var6.hasNext()) {
         ClassNameMatcher var4 = (ClassNameMatcher)var6.next();
         if (var4.matches(var1)) {
            var5 = true;
            break;
         }
      }

      if (!var5) {
         this.invalidClassNameFound(var1);
      }

   }

   protected void invalidClassNameFound(String var1) throws InvalidClassException {
      throw new InvalidClassException("Class name not accepted: " + var1);
   }

   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      this.validateClassName(var1.getName());
      return super.resolveClass(var1);
   }

   public ValidatingObjectInputStream accept(Class<?>... var1) {
      Class[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class var5 = var2[var4];
         this.acceptMatchers.add(new FullClassNameMatcher(new String[]{var5.getName()}));
      }

      return this;
   }

   public ValidatingObjectInputStream reject(Class<?>... var1) {
      Class[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class var5 = var2[var4];
         this.rejectMatchers.add(new FullClassNameMatcher(new String[]{var5.getName()}));
      }

      return this;
   }

   public ValidatingObjectInputStream accept(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.acceptMatchers.add(new WildcardClassNameMatcher(var5));
      }

      return this;
   }

   public ValidatingObjectInputStream reject(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.rejectMatchers.add(new WildcardClassNameMatcher(var5));
      }

      return this;
   }

   public ValidatingObjectInputStream accept(Pattern var1) {
      this.acceptMatchers.add(new RegexpClassNameMatcher(var1));
      return this;
   }

   public ValidatingObjectInputStream reject(Pattern var1) {
      this.rejectMatchers.add(new RegexpClassNameMatcher(var1));
      return this;
   }

   public ValidatingObjectInputStream accept(ClassNameMatcher var1) {
      this.acceptMatchers.add(var1);
      return this;
   }

   public ValidatingObjectInputStream reject(ClassNameMatcher var1) {
      this.rejectMatchers.add(var1);
      return this;
   }
}
