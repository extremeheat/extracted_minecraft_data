package org.apache.logging.log4j.core.lookup;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationAware;
import org.apache.logging.log4j.util.Strings;

public class StrSubstitutor implements ConfigurationAware {
   public static final char DEFAULT_ESCAPE = '$';
   public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
   public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
   public static final StrMatcher DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(":-");
   private static final int BUF_SIZE = 256;
   private char escapeChar;
   private StrMatcher prefixMatcher;
   private StrMatcher suffixMatcher;
   private StrMatcher valueDelimiterMatcher;
   private StrLookup variableResolver;
   private boolean enableSubstitutionInVariables;
   private Configuration configuration;

   public StrSubstitutor() {
      this((StrLookup)null, (StrMatcher)DEFAULT_PREFIX, (StrMatcher)DEFAULT_SUFFIX, '$');
   }

   public StrSubstitutor(Map<String, String> var1) {
      this((StrLookup)(new MapLookup(var1)), (StrMatcher)DEFAULT_PREFIX, (StrMatcher)DEFAULT_SUFFIX, '$');
   }

   public StrSubstitutor(Map<String, String> var1, String var2, String var3) {
      this((StrLookup)(new MapLookup(var1)), (String)var2, (String)var3, '$');
   }

   public StrSubstitutor(Map<String, String> var1, String var2, String var3, char var4) {
      this((StrLookup)(new MapLookup(var1)), (String)var2, (String)var3, var4);
   }

   public StrSubstitutor(Map<String, String> var1, String var2, String var3, char var4, String var5) {
      this((StrLookup)(new MapLookup(var1)), (String)var2, (String)var3, var4, (String)var5);
   }

   public StrSubstitutor(Properties var1) {
      this(toTypeSafeMap(var1));
   }

   public StrSubstitutor(StrLookup var1) {
      this(var1, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
   }

   public StrSubstitutor(StrLookup var1, String var2, String var3, char var4) {
      super();
      this.enableSubstitutionInVariables = true;
      this.setVariableResolver(var1);
      this.setVariablePrefix(var2);
      this.setVariableSuffix(var3);
      this.setEscapeChar(var4);
   }

   public StrSubstitutor(StrLookup var1, String var2, String var3, char var4, String var5) {
      super();
      this.enableSubstitutionInVariables = true;
      this.setVariableResolver(var1);
      this.setVariablePrefix(var2);
      this.setVariableSuffix(var3);
      this.setEscapeChar(var4);
      this.setValueDelimiter(var5);
   }

   public StrSubstitutor(StrLookup var1, StrMatcher var2, StrMatcher var3, char var4) {
      this(var1, var2, var3, var4, DEFAULT_VALUE_DELIMITER);
   }

   public StrSubstitutor(StrLookup var1, StrMatcher var2, StrMatcher var3, char var4, StrMatcher var5) {
      super();
      this.enableSubstitutionInVariables = true;
      this.setVariableResolver(var1);
      this.setVariablePrefixMatcher(var2);
      this.setVariableSuffixMatcher(var3);
      this.setEscapeChar(var4);
      this.setValueDelimiterMatcher(var5);
   }

   public static String replace(Object var0, Map<String, String> var1) {
      return (new StrSubstitutor(var1)).replace(var0);
   }

   public static String replace(Object var0, Map<String, String> var1, String var2, String var3) {
      return (new StrSubstitutor(var1, var2, var3)).replace(var0);
   }

   public static String replace(Object var0, Properties var1) {
      if (var1 == null) {
         return var0.toString();
      } else {
         HashMap var2 = new HashMap();
         Enumeration var3 = var1.propertyNames();

         while(var3.hasMoreElements()) {
            String var4 = (String)var3.nextElement();
            String var5 = var1.getProperty(var4);
            var2.put(var4, var5);
         }

         return replace((Object)var0, (Map)var2);
      }
   }

   private static Map<String, String> toTypeSafeMap(Properties var0) {
      HashMap var1 = new HashMap(var0.size());
      Iterator var2 = var0.stringPropertyNames().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.put(var3, var0.getProperty(var3));
      }

      return var1;
   }

   public String replace(String var1) {
      return this.replace((LogEvent)null, (String)var1);
   }

   public String replace(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder(var2);
         return !this.substitute(var1, var3, 0, var2.length()) ? var2 : var3.toString();
      }
   }

   public String replace(String var1, int var2, int var3) {
      return this.replace((LogEvent)null, (String)var1, var2, var3);
   }

   public String replace(LogEvent var1, String var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = (new StringBuilder(var4)).append(var2, var3, var4);
         return !this.substitute(var1, var5, 0, var4) ? var2.substring(var3, var3 + var4) : var5.toString();
      }
   }

   public String replace(char[] var1) {
      return this.replace((LogEvent)null, (char[])var1);
   }

   public String replace(LogEvent var1, char[] var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = (new StringBuilder(var2.length)).append(var2);
         this.substitute(var1, var3, 0, var2.length);
         return var3.toString();
      }
   }

   public String replace(char[] var1, int var2, int var3) {
      return this.replace((LogEvent)null, (char[])var1, var2, var3);
   }

   public String replace(LogEvent var1, char[] var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = (new StringBuilder(var4)).append(var2, var3, var4);
         this.substitute(var1, var5, 0, var4);
         return var5.toString();
      }
   }

   public String replace(StringBuffer var1) {
      return this.replace((LogEvent)null, (StringBuffer)var1);
   }

   public String replace(LogEvent var1, StringBuffer var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = (new StringBuilder(var2.length())).append(var2);
         this.substitute(var1, var3, 0, var3.length());
         return var3.toString();
      }
   }

   public String replace(StringBuffer var1, int var2, int var3) {
      return this.replace((LogEvent)null, (StringBuffer)var1, var2, var3);
   }

   public String replace(LogEvent var1, StringBuffer var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = (new StringBuilder(var4)).append(var2, var3, var4);
         this.substitute(var1, var5, 0, var4);
         return var5.toString();
      }
   }

   public String replace(StringBuilder var1) {
      return this.replace((LogEvent)null, (StringBuilder)var1);
   }

   public String replace(LogEvent var1, StringBuilder var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = (new StringBuilder(var2.length())).append(var2);
         this.substitute(var1, var3, 0, var3.length());
         return var3.toString();
      }
   }

   public String replace(StringBuilder var1, int var2, int var3) {
      return this.replace((LogEvent)null, (StringBuilder)var1, var2, var3);
   }

   public String replace(LogEvent var1, StringBuilder var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = (new StringBuilder(var4)).append(var2, var3, var4);
         this.substitute(var1, var5, 0, var4);
         return var5.toString();
      }
   }

   public String replace(Object var1) {
      return this.replace((LogEvent)null, (Object)var1);
   }

   public String replace(LogEvent var1, Object var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = (new StringBuilder()).append(var2);
         this.substitute(var1, var3, 0, var3.length());
         return var3.toString();
      }
   }

   public boolean replaceIn(StringBuffer var1) {
      return var1 == null ? false : this.replaceIn((StringBuffer)var1, 0, var1.length());
   }

   public boolean replaceIn(StringBuffer var1, int var2, int var3) {
      return this.replaceIn((LogEvent)null, (StringBuffer)var1, var2, var3);
   }

   public boolean replaceIn(LogEvent var1, StringBuffer var2, int var3, int var4) {
      if (var2 == null) {
         return false;
      } else {
         StringBuilder var5 = (new StringBuilder(var4)).append(var2, var3, var4);
         if (!this.substitute(var1, var5, 0, var4)) {
            return false;
         } else {
            var2.replace(var3, var3 + var4, var5.toString());
            return true;
         }
      }
   }

   public boolean replaceIn(StringBuilder var1) {
      return this.replaceIn((LogEvent)null, var1);
   }

   public boolean replaceIn(LogEvent var1, StringBuilder var2) {
      return var2 == null ? false : this.substitute(var1, var2, 0, var2.length());
   }

   public boolean replaceIn(StringBuilder var1, int var2, int var3) {
      return this.replaceIn((LogEvent)null, (StringBuilder)var1, var2, var3);
   }

   public boolean replaceIn(LogEvent var1, StringBuilder var2, int var3, int var4) {
      return var2 == null ? false : this.substitute(var1, var2, var3, var4);
   }

   protected boolean substitute(LogEvent var1, StringBuilder var2, int var3, int var4) {
      return this.substitute(var1, var2, var3, var4, (List)null) > 0;
   }

   private int substitute(LogEvent var1, StringBuilder var2, int var3, int var4, List<String> var5) {
      StrMatcher var6 = this.getVariablePrefixMatcher();
      StrMatcher var7 = this.getVariableSuffixMatcher();
      char var8 = this.getEscapeChar();
      StrMatcher var9 = this.getValueDelimiterMatcher();
      boolean var10 = this.isEnableSubstitutionInVariables();
      boolean var11 = var5 == null;
      boolean var12 = false;
      int var13 = 0;
      char[] var14 = this.getChars(var2);
      int var15 = var3 + var4;
      int var16 = var3;

      while(true) {
         label95:
         while(var16 < var15) {
            int var17 = var6.isMatch(var14, var16, var3, var15);
            if (var17 == 0) {
               ++var16;
            } else if (var16 > var3 && var14[var16 - 1] == var8) {
               var2.deleteCharAt(var16 - 1);
               var14 = this.getChars(var2);
               --var13;
               var12 = true;
               --var15;
            } else {
               int var18 = var16;
               var16 += var17;
               boolean var19 = false;
               int var20 = 0;

               while(true) {
                  while(true) {
                     if (var16 >= var15) {
                        continue label95;
                     }

                     int var28;
                     if (var10 && (var28 = var6.isMatch(var14, var16, var3, var15)) != 0) {
                        ++var20;
                        var16 += var28;
                     } else {
                        var28 = var7.isMatch(var14, var16, var3, var15);
                        if (var28 == 0) {
                           ++var16;
                        } else {
                           if (var20 == 0) {
                              String var21 = new String(var14, var18 + var17, var16 - var18 - var17);
                              if (var10) {
                                 StringBuilder var22 = new StringBuilder(var21);
                                 this.substitute(var1, var22, 0, var22.length());
                                 var21 = var22.toString();
                              }

                              var16 += var28;
                              String var23 = var21;
                              String var24 = null;
                              int var27;
                              int var30;
                              if (var9 != null) {
                                 char[] var25 = var21.toCharArray();
                                 boolean var26 = false;

                                 for(var27 = 0; var27 < var25.length && (var10 || var6.isMatch(var25, var27, var27, var25.length) == 0); ++var27) {
                                    if ((var30 = var9.isMatch(var25, var27)) != 0) {
                                       var23 = var21.substring(0, var27);
                                       var24 = var21.substring(var27 + var30);
                                       break;
                                    }
                                 }
                              }

                              if (var5 == null) {
                                 var5 = new ArrayList();
                                 ((List)var5).add(new String(var14, var3, var4 + var13));
                              }

                              this.checkCyclicSubstitution(var23, (List)var5);
                              ((List)var5).add(var23);
                              String var29 = this.resolveVariable(var1, var23, var2, var18, var16);
                              if (var29 == null) {
                                 var29 = var24;
                              }

                              if (var29 != null) {
                                 var30 = var29.length();
                                 var2.replace(var18, var16, var29);
                                 var12 = true;
                                 var27 = this.substitute(var1, var2, var18, var30, (List)var5);
                                 var27 += var30 - (var16 - var18);
                                 var16 += var27;
                                 var15 += var27;
                                 var13 += var27;
                                 var14 = this.getChars(var2);
                              }

                              ((List)var5).remove(((List)var5).size() - 1);
                              continue label95;
                           }

                           --var20;
                           var16 += var28;
                        }
                     }
                  }
               }
            }
         }

         if (var11) {
            return var12 ? 1 : 0;
         }

         return var13;
      }
   }

   private void checkCyclicSubstitution(String var1, List<String> var2) {
      if (var2.contains(var1)) {
         StringBuilder var3 = new StringBuilder(256);
         var3.append("Infinite loop in property interpolation of ");
         var3.append((String)var2.remove(0));
         var3.append(": ");
         this.appendWithSeparators(var3, var2, "->");
         throw new IllegalStateException(var3.toString());
      }
   }

   protected String resolveVariable(LogEvent var1, String var2, StringBuilder var3, int var4, int var5) {
      StrLookup var6 = this.getVariableResolver();
      return var6 == null ? null : var6.lookup(var1, var2);
   }

   public char getEscapeChar() {
      return this.escapeChar;
   }

   public void setEscapeChar(char var1) {
      this.escapeChar = var1;
   }

   public StrMatcher getVariablePrefixMatcher() {
      return this.prefixMatcher;
   }

   public StrSubstitutor setVariablePrefixMatcher(StrMatcher var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable prefix matcher must not be null!");
      } else {
         this.prefixMatcher = var1;
         return this;
      }
   }

   public StrSubstitutor setVariablePrefix(char var1) {
      return this.setVariablePrefixMatcher(StrMatcher.charMatcher(var1));
   }

   public StrSubstitutor setVariablePrefix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable prefix must not be null!");
      } else {
         return this.setVariablePrefixMatcher(StrMatcher.stringMatcher(var1));
      }
   }

   public StrMatcher getVariableSuffixMatcher() {
      return this.suffixMatcher;
   }

   public StrSubstitutor setVariableSuffixMatcher(StrMatcher var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable suffix matcher must not be null!");
      } else {
         this.suffixMatcher = var1;
         return this;
      }
   }

   public StrSubstitutor setVariableSuffix(char var1) {
      return this.setVariableSuffixMatcher(StrMatcher.charMatcher(var1));
   }

   public StrSubstitutor setVariableSuffix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable suffix must not be null!");
      } else {
         return this.setVariableSuffixMatcher(StrMatcher.stringMatcher(var1));
      }
   }

   public StrMatcher getValueDelimiterMatcher() {
      return this.valueDelimiterMatcher;
   }

   public StrSubstitutor setValueDelimiterMatcher(StrMatcher var1) {
      this.valueDelimiterMatcher = var1;
      return this;
   }

   public StrSubstitutor setValueDelimiter(char var1) {
      return this.setValueDelimiterMatcher(StrMatcher.charMatcher(var1));
   }

   public StrSubstitutor setValueDelimiter(String var1) {
      if (Strings.isEmpty(var1)) {
         this.setValueDelimiterMatcher((StrMatcher)null);
         return this;
      } else {
         return this.setValueDelimiterMatcher(StrMatcher.stringMatcher(var1));
      }
   }

   public StrLookup getVariableResolver() {
      return this.variableResolver;
   }

   public void setVariableResolver(StrLookup var1) {
      if (var1 instanceof ConfigurationAware && this.configuration != null) {
         ((ConfigurationAware)var1).setConfiguration(this.configuration);
      }

      this.variableResolver = var1;
   }

   public boolean isEnableSubstitutionInVariables() {
      return this.enableSubstitutionInVariables;
   }

   public void setEnableSubstitutionInVariables(boolean var1) {
      this.enableSubstitutionInVariables = var1;
   }

   private char[] getChars(StringBuilder var1) {
      char[] var2 = new char[var1.length()];
      var1.getChars(0, var1.length(), var2, 0);
      return var2;
   }

   public void appendWithSeparators(StringBuilder var1, Iterable<?> var2, String var3) {
      if (var2 != null) {
         var3 = var3 == null ? "" : var3;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            var1.append(var4.next());
            if (var4.hasNext()) {
               var1.append(var3);
            }
         }
      }

   }

   public String toString() {
      return "StrSubstitutor(" + this.variableResolver.toString() + ')';
   }

   public void setConfiguration(Configuration var1) {
      this.configuration = var1;
      if (this.variableResolver instanceof ConfigurationAware) {
         ((ConfigurationAware)this.variableResolver).setConfiguration(this.configuration);
      }

   }
}
