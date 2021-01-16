package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;
import java.util.List;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class InternetDomainName {
   private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf(".\u3002\uff0e\uff61");
   private static final Splitter DOT_SPLITTER = Splitter.on('.');
   private static final Joiner DOT_JOINER = Joiner.on('.');
   private static final int NO_PUBLIC_SUFFIX_FOUND = -1;
   private static final String DOT_REGEX = "\\.";
   private static final int MAX_PARTS = 127;
   private static final int MAX_LENGTH = 253;
   private static final int MAX_DOMAIN_PART_LENGTH = 63;
   private final String name;
   private final ImmutableList<String> parts;
   private final int publicSuffixIndex;
   private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf("-_");
   private static final CharMatcher PART_CHAR_MATCHER;

   InternetDomainName(String var1) {
      super();
      var1 = Ascii.toLowerCase(DOTS_MATCHER.replaceFrom(var1, '.'));
      if (var1.endsWith(".")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      Preconditions.checkArgument(var1.length() <= 253, "Domain name too long: '%s':", (Object)var1);
      this.name = var1;
      this.parts = ImmutableList.copyOf(DOT_SPLITTER.split(var1));
      Preconditions.checkArgument(this.parts.size() <= 127, "Domain has too many parts: '%s'", (Object)var1);
      Preconditions.checkArgument(validateSyntax(this.parts), "Not a valid domain name: '%s'", (Object)var1);
      this.publicSuffixIndex = this.findPublicSuffix();
   }

   private int findPublicSuffix() {
      int var1 = this.parts.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         String var3 = DOT_JOINER.join((Iterable)this.parts.subList(var2, var1));
         if (PublicSuffixPatterns.EXACT.containsKey(var3)) {
            return var2;
         }

         if (PublicSuffixPatterns.EXCLUDED.containsKey(var3)) {
            return var2 + 1;
         }

         if (matchesWildcardPublicSuffix(var3)) {
            return var2;
         }
      }

      return -1;
   }

   public static InternetDomainName from(String var0) {
      return new InternetDomainName((String)Preconditions.checkNotNull(var0));
   }

   private static boolean validateSyntax(List<String> var0) {
      int var1 = var0.size() - 1;
      if (!validatePart((String)var0.get(var1), true)) {
         return false;
      } else {
         for(int var2 = 0; var2 < var1; ++var2) {
            String var3 = (String)var0.get(var2);
            if (!validatePart(var3, false)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean validatePart(String var0, boolean var1) {
      if (var0.length() >= 1 && var0.length() <= 63) {
         String var2 = CharMatcher.ascii().retainFrom(var0);
         if (!PART_CHAR_MATCHER.matchesAllOf(var2)) {
            return false;
         } else if (!DASH_MATCHER.matches(var0.charAt(0)) && !DASH_MATCHER.matches(var0.charAt(var0.length() - 1))) {
            return !var1 || !CharMatcher.digit().matches(var0.charAt(0));
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public ImmutableList<String> parts() {
      return this.parts;
   }

   public boolean isPublicSuffix() {
      return this.publicSuffixIndex == 0;
   }

   public boolean hasPublicSuffix() {
      return this.publicSuffixIndex != -1;
   }

   public InternetDomainName publicSuffix() {
      return this.hasPublicSuffix() ? this.ancestor(this.publicSuffixIndex) : null;
   }

   public boolean isUnderPublicSuffix() {
      return this.publicSuffixIndex > 0;
   }

   public boolean isTopPrivateDomain() {
      return this.publicSuffixIndex == 1;
   }

   public InternetDomainName topPrivateDomain() {
      if (this.isTopPrivateDomain()) {
         return this;
      } else {
         Preconditions.checkState(this.isUnderPublicSuffix(), "Not under a public suffix: %s", (Object)this.name);
         return this.ancestor(this.publicSuffixIndex - 1);
      }
   }

   public boolean hasParent() {
      return this.parts.size() > 1;
   }

   public InternetDomainName parent() {
      Preconditions.checkState(this.hasParent(), "Domain '%s' has no parent", (Object)this.name);
      return this.ancestor(1);
   }

   private InternetDomainName ancestor(int var1) {
      return from(DOT_JOINER.join((Iterable)this.parts.subList(var1, this.parts.size())));
   }

   public InternetDomainName child(String var1) {
      return from((String)Preconditions.checkNotNull(var1) + "." + this.name);
   }

   public static boolean isValid(String var0) {
      try {
         from(var0);
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   private static boolean matchesWildcardPublicSuffix(String var0) {
      String[] var1 = var0.split("\\.", 2);
      return var1.length == 2 && PublicSuffixPatterns.UNDER.containsKey(var1[1]);
   }

   public String toString() {
      return this.name;
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof InternetDomainName) {
         InternetDomainName var2 = (InternetDomainName)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   static {
      PART_CHAR_MATCHER = CharMatcher.javaLetterOrDigit().or(DASH_MATCHER);
   }
}
