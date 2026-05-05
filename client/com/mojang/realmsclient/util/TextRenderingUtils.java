package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class TextRenderingUtils {
   private TextRenderingUtils() {
      super();
   }

   @VisibleForTesting
   protected static List<String> lineBreak(final String text) {
      return Arrays.asList(text.split("\\n"));
   }

   public static List<Line> decompose(final String text, final LineSegment... links) {
      return decompose(text, Arrays.asList(links));
   }

   private static List<Line> decompose(final String text, final List<LineSegment> links) {
      List<String> brokenLines = lineBreak(text);
      return insertLinks(brokenLines, links);
   }

   private static List<Line> insertLinks(final List<String> lines, final List<LineSegment> links) {
      int linkCount = 0;
      List<Line> processedLines = Lists.newArrayList();

      for(String line : lines) {
         List<LineSegment> segments = Lists.newArrayList();

         for(String part : split(line, "%link")) {
            if ("%link".equals(part)) {
               segments.add((LineSegment)links.get(linkCount++));
            } else {
               segments.add(TextRenderingUtils.LineSegment.text(part));
            }
         }

         processedLines.add(new Line(segments));
      }

      return processedLines;
   }

   public static List<String> split(final String line, final String delimiter) {
      if (delimiter.isEmpty()) {
         throw new IllegalArgumentException("Delimiter cannot be the empty string");
      } else {
         List<String> parts = Lists.newArrayList();

         int searchStart;
         int matchIndex;
         for(searchStart = 0; (matchIndex = line.indexOf(delimiter, searchStart)) != -1; searchStart = matchIndex + delimiter.length()) {
            if (matchIndex > searchStart) {
               parts.add(line.substring(searchStart, matchIndex));
            }

            parts.add(delimiter);
         }

         if (searchStart < line.length()) {
            parts.add(line.substring(searchStart));
         }

         return parts;
      }
   }

   public static class Line {
      public final List<LineSegment> segments;

      public Line(final LineSegment... segments) {
         this(Arrays.asList(segments));
      }

      public Line(final List<LineSegment> segments) {
         super();
         this.segments = segments;
      }

      public String toString() {
         return "Line{segments=" + String.valueOf(this.segments) + "}";
      }

      public boolean equals(final Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Line line = (Line)o;
            return Objects.equals(this.segments, line.segments);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.segments});
      }
   }

   public static class LineSegment {
      private final String fullText;
      private final @Nullable String linkTitle;
      private final @Nullable String linkUrl;

      private LineSegment(final String fullText) {
         super();
         this.fullText = fullText;
         this.linkTitle = null;
         this.linkUrl = null;
      }

      private LineSegment(final String fullText, final @Nullable String linkTitle, final @Nullable String linkUrl) {
         super();
         this.fullText = fullText;
         this.linkTitle = linkTitle;
         this.linkUrl = linkUrl;
      }

      public boolean equals(final Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            LineSegment segment = (LineSegment)o;
            return Objects.equals(this.fullText, segment.fullText) && Objects.equals(this.linkTitle, segment.linkTitle) && Objects.equals(this.linkUrl, segment.linkUrl);
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
            throw new IllegalStateException("Not a link: " + String.valueOf(this));
         } else {
            return this.linkUrl;
         }
      }

      public static LineSegment link(final String linkTitle, final String linkUrl) {
         return new LineSegment((String)null, linkTitle, linkUrl);
      }

      @VisibleForTesting
      protected static LineSegment text(final String fullText) {
         return new LineSegment(fullText);
      }
   }
}
