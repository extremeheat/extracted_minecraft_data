package org.apache.logging.log4j.core.pattern;

public final class PlainTextRenderer implements TextRenderer {
   private static final PlainTextRenderer INSTANCE = new PlainTextRenderer();

   public PlainTextRenderer() {
      super();
   }

   public static PlainTextRenderer getInstance() {
      return INSTANCE;
   }

   public void render(String var1, StringBuilder var2, String var3) {
      var2.append(var1);
   }

   public void render(StringBuilder var1, StringBuilder var2) {
      var2.append(var1);
   }
}
