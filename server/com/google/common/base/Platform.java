package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class Platform {
   private static final Logger logger = Logger.getLogger(Platform.class.getName());
   private static final PatternCompiler patternCompiler = loadPatternCompiler();

   private Platform() {
      super();
   }

   static long systemNanoTime() {
      return System.nanoTime();
   }

   static CharMatcher precomputeCharMatcher(CharMatcher var0) {
      return var0.precomputedInternal();
   }

   static <T extends Enum<T>> Optional<T> getEnumIfPresent(Class<T> var0, String var1) {
      WeakReference var2 = (WeakReference)Enums.getEnumConstants(var0).get(var1);
      return var2 == null ? Optional.absent() : Optional.of(var0.cast(var2.get()));
   }

   static String formatCompact4Digits(double var0) {
      return String.format(Locale.ROOT, "%.4g", var0);
   }

   static boolean stringIsNullOrEmpty(@Nullable String var0) {
      return var0 == null || var0.isEmpty();
   }

   static CommonPattern compilePattern(String var0) {
      Preconditions.checkNotNull(var0);
      return patternCompiler.compile(var0);
   }

   static boolean usingJdkPatternCompiler() {
      return patternCompiler instanceof Platform.JdkPatternCompiler;
   }

   private static PatternCompiler loadPatternCompiler() {
      ServiceLoader var0 = ServiceLoader.load(PatternCompiler.class);

      try {
         Iterator var1 = var0.iterator();

         while(var1.hasNext()) {
            try {
               return (PatternCompiler)var1.next();
            } catch (ServiceConfigurationError var3) {
               logPatternCompilerError(var3);
            }
         }
      } catch (ServiceConfigurationError var4) {
         logPatternCompilerError(var4);
      }

      return new Platform.JdkPatternCompiler();
   }

   private static void logPatternCompilerError(ServiceConfigurationError var0) {
      logger.log(Level.WARNING, "Error loading regex compiler, falling back to next option", var0);
   }

   private static final class JdkPatternCompiler implements PatternCompiler {
      private JdkPatternCompiler() {
         super();
      }

      public CommonPattern compile(String var1) {
         return new JdkPattern(Pattern.compile(var1));
      }

      // $FF: synthetic method
      JdkPatternCompiler(Object var1) {
         this();
      }
   }
}
