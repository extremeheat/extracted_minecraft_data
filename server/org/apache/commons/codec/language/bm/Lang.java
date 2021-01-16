package org.apache.commons.codec.language.bm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Lang {
   private static final Map<NameType, Lang> Langs = new EnumMap(NameType.class);
   private static final String LANGUAGE_RULES_RN = "org/apache/commons/codec/language/bm/%s_lang.txt";
   private final Languages languages;
   private final List<Lang.LangRule> rules;

   public static Lang instance(NameType var0) {
      return (Lang)Langs.get(var0);
   }

   public static Lang loadFromResource(String var0, Languages var1) {
      ArrayList var2 = new ArrayList();
      InputStream var3 = Lang.class.getClassLoader().getResourceAsStream(var0);
      if (var3 == null) {
         throw new IllegalStateException("Unable to resolve required resource:org/apache/commons/codec/language/bm/%s_lang.txt");
      } else {
         Scanner var4 = new Scanner(var3, "UTF-8");

         try {
            boolean var5 = false;

            while(var4.hasNextLine()) {
               String var6 = var4.nextLine();
               String var7 = var6;
               if (var5) {
                  if (var6.endsWith("*/")) {
                     var5 = false;
                  }
               } else if (var6.startsWith("/*")) {
                  var5 = true;
               } else {
                  int var8 = var6.indexOf("//");
                  if (var8 >= 0) {
                     var7 = var6.substring(0, var8);
                  }

                  var7 = var7.trim();
                  if (var7.length() != 0) {
                     String[] var9 = var7.split("\\s+");
                     if (var9.length != 3) {
                        throw new IllegalArgumentException("Malformed line '" + var6 + "' in language resource '" + var0 + "'");
                     }

                     Pattern var10 = Pattern.compile(var9[0]);
                     String[] var11 = var9[1].split("\\+");
                     boolean var12 = var9[2].equals("true");
                     var2.add(new Lang.LangRule(var10, new HashSet(Arrays.asList(var11)), var12));
                  }
               }
            }
         } finally {
            var4.close();
         }

         return new Lang(var2, var1);
      }
   }

   private Lang(List<Lang.LangRule> var1, Languages var2) {
      super();
      this.rules = Collections.unmodifiableList(var1);
      this.languages = var2;
   }

   public String guessLanguage(String var1) {
      Languages.LanguageSet var2 = this.guessLanguages(var1);
      return var2.isSingleton() ? var2.getAny() : "any";
   }

   public Languages.LanguageSet guessLanguages(String var1) {
      String var2 = var1.toLowerCase(Locale.ENGLISH);
      HashSet var3 = new HashSet(this.languages.getLanguages());
      Iterator var4 = this.rules.iterator();

      while(var4.hasNext()) {
         Lang.LangRule var5 = (Lang.LangRule)var4.next();
         if (var5.matches(var2)) {
            if (var5.acceptOnMatch) {
               var3.retainAll(var5.languages);
            } else {
               var3.removeAll(var5.languages);
            }
         }
      }

      Languages.LanguageSet var6 = Languages.LanguageSet.from(var3);
      return var6.equals(Languages.NO_LANGUAGES) ? Languages.ANY_LANGUAGE : var6;
   }

   static {
      NameType[] var0 = NameType.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         NameType var3 = var0[var2];
         Langs.put(var3, loadFromResource(String.format("org/apache/commons/codec/language/bm/%s_lang.txt", var3.getName()), Languages.getInstance(var3)));
      }

   }

   private static final class LangRule {
      private final boolean acceptOnMatch;
      private final Set<String> languages;
      private final Pattern pattern;

      private LangRule(Pattern var1, Set<String> var2, boolean var3) {
         super();
         this.pattern = var1;
         this.languages = var2;
         this.acceptOnMatch = var3;
      }

      public boolean matches(String var1) {
         return this.pattern.matcher(var1).find();
      }

      // $FF: synthetic method
      LangRule(Pattern var1, Set var2, boolean var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
