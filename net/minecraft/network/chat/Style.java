package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.GsonHelper;

public class Style {
   private Style parent;
   private ChatFormatting color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private ClickEvent clickEvent;
   private HoverEvent hoverEvent;
   private String insertion;
   private static final Style ROOT = new Style() {
      @Nullable
      public ChatFormatting getColor() {
         return null;
      }

      public boolean isBold() {
         return false;
      }

      public boolean isItalic() {
         return false;
      }

      public boolean isStrikethrough() {
         return false;
      }

      public boolean isUnderlined() {
         return false;
      }

      public boolean isObfuscated() {
         return false;
      }

      @Nullable
      public ClickEvent getClickEvent() {
         return null;
      }

      @Nullable
      public HoverEvent getHoverEvent() {
         return null;
      }

      @Nullable
      public String getInsertion() {
         return null;
      }

      public Style setColor(ChatFormatting var1) {
         throw new UnsupportedOperationException();
      }

      public Style setBold(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Style setItalic(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Style setStrikethrough(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Style setUnderlined(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Style setObfuscated(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Style setClickEvent(ClickEvent var1) {
         throw new UnsupportedOperationException();
      }

      public Style setHoverEvent(HoverEvent var1) {
         throw new UnsupportedOperationException();
      }

      public Style inheritFrom(Style var1) {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return "Style.ROOT";
      }

      public Style copy() {
         return this;
      }

      public Style flatCopy() {
         return this;
      }

      public String getLegacyFormatCodes() {
         return "";
      }
   };

   @Nullable
   public ChatFormatting getColor() {
      return this.color == null ? this.getParent().getColor() : this.color;
   }

   public boolean isBold() {
      return this.bold == null ? this.getParent().isBold() : this.bold;
   }

   public boolean isItalic() {
      return this.italic == null ? this.getParent().isItalic() : this.italic;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == null ? this.getParent().isStrikethrough() : this.strikethrough;
   }

   public boolean isUnderlined() {
      return this.underlined == null ? this.getParent().isUnderlined() : this.underlined;
   }

   public boolean isObfuscated() {
      return this.obfuscated == null ? this.getParent().isObfuscated() : this.obfuscated;
   }

   public boolean isEmpty() {
      return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
   }

   public Style setColor(ChatFormatting var1) {
      this.color = var1;
      return this;
   }

   public Style setBold(Boolean var1) {
      this.bold = var1;
      return this;
   }

   public Style setItalic(Boolean var1) {
      this.italic = var1;
      return this;
   }

   public Style setStrikethrough(Boolean var1) {
      this.strikethrough = var1;
      return this;
   }

   public Style setUnderlined(Boolean var1) {
      this.underlined = var1;
      return this;
   }

   public Style setObfuscated(Boolean var1) {
      this.obfuscated = var1;
      return this;
   }

   public Style setClickEvent(ClickEvent var1) {
      this.clickEvent = var1;
      return this;
   }

   public Style setHoverEvent(HoverEvent var1) {
      this.hoverEvent = var1;
      return this;
   }

   public Style setInsertion(String var1) {
      this.insertion = var1;
      return this;
   }

   public Style inheritFrom(Style var1) {
      this.parent = var1;
      return this;
   }

   public String getLegacyFormatCodes() {
      if (this.isEmpty()) {
         return this.parent != null ? this.parent.getLegacyFormatCodes() : "";
      } else {
         StringBuilder var1 = new StringBuilder();
         if (this.getColor() != null) {
            var1.append(this.getColor());
         }

         if (this.isBold()) {
            var1.append(ChatFormatting.BOLD);
         }

         if (this.isItalic()) {
            var1.append(ChatFormatting.ITALIC);
         }

         if (this.isUnderlined()) {
            var1.append(ChatFormatting.UNDERLINE);
         }

         if (this.isObfuscated()) {
            var1.append(ChatFormatting.OBFUSCATED);
         }

         if (this.isStrikethrough()) {
            var1.append(ChatFormatting.STRIKETHROUGH);
         }

         return var1.toString();
      }
   }

   private Style getParent() {
      return this.parent == null ? ROOT : this.parent;
   }

   public String toString() {
      return "Style{hasParent=" + (this.parent != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Style)) {
         return false;
      } else {
         boolean var10000;
         label77: {
            Style var2 = (Style)var1;
            if (this.isBold() == var2.isBold() && this.getColor() == var2.getColor() && this.isItalic() == var2.isItalic() && this.isObfuscated() == var2.isObfuscated() && this.isStrikethrough() == var2.isStrikethrough() && this.isUnderlined() == var2.isUnderlined()) {
               label71: {
                  if (this.getClickEvent() != null) {
                     if (!this.getClickEvent().equals(var2.getClickEvent())) {
                        break label71;
                     }
                  } else if (var2.getClickEvent() != null) {
                     break label71;
                  }

                  if (this.getHoverEvent() != null) {
                     if (!this.getHoverEvent().equals(var2.getHoverEvent())) {
                        break label71;
                     }
                  } else if (var2.getHoverEvent() != null) {
                     break label71;
                  }

                  if (this.getInsertion() != null) {
                     if (this.getInsertion().equals(var2.getInsertion())) {
                        break label77;
                     }
                  } else if (var2.getInsertion() == null) {
                     break label77;
                  }
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
   }

   public Style copy() {
      Style var1 = new Style();
      var1.bold = this.bold;
      var1.italic = this.italic;
      var1.strikethrough = this.strikethrough;
      var1.underlined = this.underlined;
      var1.obfuscated = this.obfuscated;
      var1.color = this.color;
      var1.clickEvent = this.clickEvent;
      var1.hoverEvent = this.hoverEvent;
      var1.parent = this.parent;
      var1.insertion = this.insertion;
      return var1;
   }

   public Style flatCopy() {
      Style var1 = new Style();
      var1.setBold(this.isBold());
      var1.setItalic(this.isItalic());
      var1.setStrikethrough(this.isStrikethrough());
      var1.setUnderlined(this.isUnderlined());
      var1.setObfuscated(this.isObfuscated());
      var1.setColor(this.getColor());
      var1.setClickEvent(this.getClickEvent());
      var1.setHoverEvent(this.getHoverEvent());
      var1.setInsertion(this.getInsertion());
      return var1;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      @Nullable
      public Style deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            Style var4 = new Style();
            JsonObject var5 = var1.getAsJsonObject();
            if (var5 == null) {
               return null;
            } else {
               if (var5.has("bold")) {
                  var4.bold = var5.get("bold").getAsBoolean();
               }

               if (var5.has("italic")) {
                  var4.italic = var5.get("italic").getAsBoolean();
               }

               if (var5.has("underlined")) {
                  var4.underlined = var5.get("underlined").getAsBoolean();
               }

               if (var5.has("strikethrough")) {
                  var4.strikethrough = var5.get("strikethrough").getAsBoolean();
               }

               if (var5.has("obfuscated")) {
                  var4.obfuscated = var5.get("obfuscated").getAsBoolean();
               }

               if (var5.has("color")) {
                  var4.color = (ChatFormatting)var3.deserialize(var5.get("color"), ChatFormatting.class);
               }

               if (var5.has("insertion")) {
                  var4.insertion = var5.get("insertion").getAsString();
               }

               JsonObject var6;
               String var7;
               if (var5.has("clickEvent")) {
                  var6 = GsonHelper.getAsJsonObject(var5, "clickEvent");
                  var7 = GsonHelper.getAsString(var6, "action", (String)null);
                  ClickEvent.Action var8 = var7 == null ? null : ClickEvent.Action.getByName(var7);
                  String var9 = GsonHelper.getAsString(var6, "value", (String)null);
                  if (var8 != null && var9 != null && var8.isAllowedFromServer()) {
                     var4.clickEvent = new ClickEvent(var8, var9);
                  }
               }

               if (var5.has("hoverEvent")) {
                  var6 = GsonHelper.getAsJsonObject(var5, "hoverEvent");
                  var7 = GsonHelper.getAsString(var6, "action", (String)null);
                  HoverEvent.Action var10 = var7 == null ? null : HoverEvent.Action.getByName(var7);
                  Component var11 = (Component)var3.deserialize(var6.get("value"), Component.class);
                  if (var10 != null && var11 != null && var10.isAllowedFromServer()) {
                     var4.hoverEvent = new HoverEvent(var10, var11);
                  }
               }

               return var4;
            }
         } else {
            return null;
         }
      }

      @Nullable
      public JsonElement serialize(Style var1, Type var2, JsonSerializationContext var3) {
         if (var1.isEmpty()) {
            return null;
         } else {
            JsonObject var4 = new JsonObject();
            if (var1.bold != null) {
               var4.addProperty("bold", var1.bold);
            }

            if (var1.italic != null) {
               var4.addProperty("italic", var1.italic);
            }

            if (var1.underlined != null) {
               var4.addProperty("underlined", var1.underlined);
            }

            if (var1.strikethrough != null) {
               var4.addProperty("strikethrough", var1.strikethrough);
            }

            if (var1.obfuscated != null) {
               var4.addProperty("obfuscated", var1.obfuscated);
            }

            if (var1.color != null) {
               var4.add("color", var3.serialize(var1.color));
            }

            if (var1.insertion != null) {
               var4.add("insertion", var3.serialize(var1.insertion));
            }

            JsonObject var5;
            if (var1.clickEvent != null) {
               var5 = new JsonObject();
               var5.addProperty("action", var1.clickEvent.getAction().getName());
               var5.addProperty("value", var1.clickEvent.getValue());
               var4.add("clickEvent", var5);
            }

            if (var1.hoverEvent != null) {
               var5 = new JsonObject();
               var5.addProperty("action", var1.hoverEvent.getAction().getName());
               var5.add("value", var3.serialize(var1.hoverEvent.getValue()));
               var4.add("hoverEvent", var5);
            }

            return var4;
         }
      }

      // $FF: synthetic method
      @Nullable
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((Style)var1, var2, var3);
      }

      // $FF: synthetic method
      @Nullable
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
