package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class TextRenderingUtils {
   private TextRenderingUtils() {
      super();
   }

   @VisibleForTesting
   protected static List<String> lineBreak(String var0) {
      return Arrays.asList(var0.split("\\n"));
   }

   public static List<TextRenderingUtils.Line> decompose(String var0, TextRenderingUtils.LineSegment... var1) {
      return decompose(var0, Arrays.asList(var1));
   }

   private static List<TextRenderingUtils.Line> decompose(String var0, List<TextRenderingUtils.LineSegment> var1) {
      List var2 = lineBreak(var0);
      return insertLinks(var2, var1);
   }

   private static List<TextRenderingUtils.Line> insertLinks(List<String> var0, List<TextRenderingUtils.LineSegment> var1) {
      int var2 = 0;
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var0.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         ArrayList var6 = Lists.newArrayList();
         List var7 = split(var5, "%link");
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            String var9 = (String)var8.next();
            if ("%link".equals(var9)) {
               var6.add((TextRenderingUtils.LineSegment)var1.get(var2++));
            } else {
               var6.add(TextRenderingUtils.LineSegment.text(var9));
            }
         }

         var3.add(new TextRenderingUtils.Line(var6));
      }

      return var3;
   }

   public static List<String> split(String var0, String var1) {
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         ArrayList var2 = Lists.newArrayList();

         int var3;
         int var4;
         for(var3 = 0; (var4 = var0.indexOf(var1, var3)) != -1; var3 = var4 + var1.length()) {
            if (var4 > var3) {
               var2.add(var0.substring(var3, var4));
            }

            var2.add(var1);
         }

         if (var3 < var0.length()) {
            var2.add(var0.substring(var3));
         }

         return var2;
      }
   }

   public static class LineSegment {
      private final String fullText;
      @Nullable
      private final String linkTitle;
      @Nullable
      private final String linkUrl;

      private LineSegment(String var1) {
         super();
         this.fullText = var1;
         this.linkTitle = null;
         this.linkUrl = null;
      }

      private LineSegment(String var1, @Nullable String var2, @Nullable String var3) {
         super();
         this.fullText = var1;
         this.linkTitle = var2;
         this.linkUrl = var3;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            TextRenderingUtils.LineSegment var2 = (TextRenderingUtils.LineSegment)var1;
            return Objects.equals(this.fullText, var2.fullText) && Objects.equals(this.linkTitle, var2.linkTitle) && Objects.equals(this.linkUrl, var2.linkUrl);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.fullText, this.linkTitle, this.linkUrl});
      }

      public String toString() {
         return "Segment{fullText='" + this.fullText + "', linkTitle='" + this.linkTitle + "', linkUrl='" + this.linkUrl + "'}";
      }

      public String renderedText() {
         return this.isLink() ? this.linkTitle : this.fullText;
      }

      public boolean isLink() {
         return this.linkTitle != null;
      }

      public String getLinkUrl() {
         if (!this.isLink()) {
            throw new IllegalStateException("Not a link: " + this);
         } else {
            return this.linkUrl;
         }
      }

      public static TextRenderingUtils.LineSegment link(String var0, String var1) {
         return new TextRenderingUtils.LineSegment((String)null, var0, var1);
      }

      @VisibleForTesting
      protected static TextRenderingUtils.LineSegment text(String var0) {
         return new TextRenderingUtils.LineSegment(var0);
      }
   }

   public static class Line {
      public final List<TextRenderingUtils.LineSegment> segments;

      Line(TextRenderingUtils.LineSegment... var1) {
         this(Arrays.asList(var1));
      }

      Line(List<TextRenderingUtils.LineSegment> var1) {
         super();
         this.segments = var1;
      }

      public String toString() {
         return "Line{segments=" + this.segments + "}";
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            TextRenderingUtils.Line var2 = (TextRenderingUtils.Line)var1;
            return Objects.equals(this.segments, var2.segments);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.segments});
      }
   }
}
