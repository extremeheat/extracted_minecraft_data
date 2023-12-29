package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class Style {
   public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
   @Nullable
   final TextColor color;
   @Nullable
   final Boolean bold;
   @Nullable
   final Boolean italic;
   @Nullable
   final Boolean underlined;
   @Nullable
   final Boolean strikethrough;
   @Nullable
   final Boolean obfuscated;
   @Nullable
   final ClickEvent clickEvent;
   @Nullable
   final HoverEvent hoverEvent;
   @Nullable
   final String insertion;
   @Nullable
   final ResourceLocation font;

   private static Style create(
      Optional<TextColor> var0,
      Optional<Boolean> var1,
      Optional<Boolean> var2,
      Optional<Boolean> var3,
      Optional<Boolean> var4,
      Optional<Boolean> var5,
      Optional<ClickEvent> var6,
      Optional<HoverEvent> var7,
      Optional<String> var8,
      Optional<ResourceLocation> var9
   ) {
      Style var10 = new Style(
         (TextColor)var0.orElse(null),
         (Boolean)var1.orElse(null),
         (Boolean)var2.orElse(null),
         (Boolean)var3.orElse(null),
         (Boolean)var4.orElse(null),
         (Boolean)var5.orElse(null),
         (ClickEvent)var6.orElse(null),
         (HoverEvent)var7.orElse(null),
         (String)var8.orElse(null),
         (ResourceLocation)var9.orElse(null)
      );
      return var10.equals(EMPTY) ? EMPTY : var10;
   }

   private Style(
      @Nullable TextColor var1,
      @Nullable Boolean var2,
      @Nullable Boolean var3,
      @Nullable Boolean var4,
      @Nullable Boolean var5,
      @Nullable Boolean var6,
      @Nullable ClickEvent var7,
      @Nullable HoverEvent var8,
      @Nullable String var9,
      @Nullable ResourceLocation var10
   ) {
      super();
      this.color = var1;
      this.bold = var2;
      this.italic = var3;
      this.underlined = var4;
      this.strikethrough = var5;
      this.obfuscated = var6;
      this.clickEvent = var7;
      this.hoverEvent = var8;
      this.insertion = var9;
      this.font = var10;
   }

   @Nullable
   public TextColor getColor() {
      return this.color;
   }

   public boolean isBold() {
      return this.bold == Boolean.TRUE;
   }

   public boolean isItalic() {
      return this.italic == Boolean.TRUE;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == Boolean.TRUE;
   }

   public boolean isUnderlined() {
      return this.underlined == Boolean.TRUE;
   }

   public boolean isObfuscated() {
      return this.obfuscated == Boolean.TRUE;
   }

   public boolean isEmpty() {
      return this == EMPTY;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion;
   }

   public ResourceLocation getFont() {
      return this.font != null ? this.font : DEFAULT_FONT;
   }

   private static <T> Style checkEmptyAfterChange(Style var0, @Nullable T var1, @Nullable T var2) {
      return var1 != null && var2 == null && var0.equals(EMPTY) ? EMPTY : var0;
   }

   public Style withColor(@Nullable TextColor var1) {
      return Objects.equals(this.color, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               var1, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
            ),
            this.color,
            var1
         );
   }

   public Style withColor(@Nullable ChatFormatting var1) {
      return this.withColor(var1 != null ? TextColor.fromLegacyFormat(var1) : null);
   }

   public Style withColor(int var1) {
      return this.withColor(TextColor.fromRgb(var1));
   }

   public Style withBold(@Nullable Boolean var1) {
      return Objects.equals(this.bold, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color,
               var1,
               this.italic,
               this.underlined,
               this.strikethrough,
               this.obfuscated,
               this.clickEvent,
               this.hoverEvent,
               this.insertion,
               this.font
            ),
            this.bold,
            var1
         );
   }

   public Style withItalic(@Nullable Boolean var1) {
      return Objects.equals(this.italic, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color, this.bold, var1, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
            ),
            this.italic,
            var1
         );
   }

   public Style withUnderlined(@Nullable Boolean var1) {
      return Objects.equals(this.underlined, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color, this.bold, this.italic, var1, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font
            ),
            this.underlined,
            var1
         );
   }

   public Style withStrikethrough(@Nullable Boolean var1) {
      return Objects.equals(this.strikethrough, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(this.color, this.bold, this.italic, this.underlined, var1, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font),
            this.strikethrough,
            var1
         );
   }

   public Style withObfuscated(@Nullable Boolean var1) {
      return Objects.equals(this.obfuscated, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color, this.bold, this.italic, this.underlined, this.strikethrough, var1, this.clickEvent, this.hoverEvent, this.insertion, this.font
            ),
            this.obfuscated,
            var1
         );
   }

   public Style withClickEvent(@Nullable ClickEvent var1) {
      return Objects.equals(this.clickEvent, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, var1, this.hoverEvent, this.insertion, this.font
            ),
            this.clickEvent,
            var1
         );
   }

   public Style withHoverEvent(@Nullable HoverEvent var1) {
      return Objects.equals(this.hoverEvent, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, var1, this.insertion, this.font
            ),
            this.hoverEvent,
            var1
         );
   }

   public Style withInsertion(@Nullable String var1) {
      return Objects.equals(this.insertion, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, var1, this.font
            ),
            this.insertion,
            var1
         );
   }

   public Style withFont(@Nullable ResourceLocation var1) {
      return Objects.equals(this.font, var1)
         ? this
         : checkEmptyAfterChange(
            new Style(
               this.color,
               this.bold,
               this.italic,
               this.underlined,
               this.strikethrough,
               this.obfuscated,
               this.clickEvent,
               this.hoverEvent,
               this.insertion,
               var1
            ),
            this.font,
            var1
         );
   }

   public Style applyFormat(ChatFormatting var1) {
      TextColor var2 = this.color;
      Boolean var3 = this.bold;
      Boolean var4 = this.italic;
      Boolean var5 = this.strikethrough;
      Boolean var6 = this.underlined;
      Boolean var7 = this.obfuscated;
      switch(var1) {
         case OBFUSCATED:
            var7 = true;
            break;
         case BOLD:
            var3 = true;
            break;
         case STRIKETHROUGH:
            var5 = true;
            break;
         case UNDERLINE:
            var6 = true;
            break;
         case ITALIC:
            var4 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            var2 = TextColor.fromLegacyFormat(var1);
      }

      return new Style(var2, var3, var4, var6, var5, var7, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyLegacyFormat(ChatFormatting var1) {
      TextColor var2 = this.color;
      Boolean var3 = this.bold;
      Boolean var4 = this.italic;
      Boolean var5 = this.strikethrough;
      Boolean var6 = this.underlined;
      Boolean var7 = this.obfuscated;
      switch(var1) {
         case OBFUSCATED:
            var7 = true;
            break;
         case BOLD:
            var3 = true;
            break;
         case STRIKETHROUGH:
            var5 = true;
            break;
         case UNDERLINE:
            var6 = true;
            break;
         case ITALIC:
            var4 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            var7 = false;
            var3 = false;
            var5 = false;
            var6 = false;
            var4 = false;
            var2 = TextColor.fromLegacyFormat(var1);
      }

      return new Style(var2, var3, var4, var6, var5, var7, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyFormats(ChatFormatting... var1) {
      TextColor var2 = this.color;
      Boolean var3 = this.bold;
      Boolean var4 = this.italic;
      Boolean var5 = this.strikethrough;
      Boolean var6 = this.underlined;
      Boolean var7 = this.obfuscated;

      for(ChatFormatting var11 : var1) {
         switch(var11) {
            case OBFUSCATED:
               var7 = true;
               break;
            case BOLD:
               var3 = true;
               break;
            case STRIKETHROUGH:
               var5 = true;
               break;
            case UNDERLINE:
               var6 = true;
               break;
            case ITALIC:
               var4 = true;
               break;
            case RESET:
               return EMPTY;
            default:
               var2 = TextColor.fromLegacyFormat(var11);
         }
      }

      return new Style(var2, var3, var4, var6, var5, var7, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyTo(Style var1) {
      if (this == EMPTY) {
         return var1;
      } else {
         return var1 == EMPTY
            ? this
            : new Style(
               this.color != null ? this.color : var1.color,
               this.bold != null ? this.bold : var1.bold,
               this.italic != null ? this.italic : var1.italic,
               this.underlined != null ? this.underlined : var1.underlined,
               this.strikethrough != null ? this.strikethrough : var1.strikethrough,
               this.obfuscated != null ? this.obfuscated : var1.obfuscated,
               this.clickEvent != null ? this.clickEvent : var1.clickEvent,
               this.hoverEvent != null ? this.hoverEvent : var1.hoverEvent,
               this.insertion != null ? this.insertion : var1.insertion,
               this.font != null ? this.font : var1.font
            );
      }
   }

   @Override
   public String toString() {
      final StringBuilder var1 = new StringBuilder("{");

      class 1Collector {
         private boolean isNotFirst;

         _Collector/* $VF was: 1Collector*/() {
            super();
         }

         private void prependSeparator() {
            if (this.isNotFirst) {
               var1.append(',');
            }

            this.isNotFirst = true;
         }

         void addFlagString(String var1x, @Nullable Boolean var2) {
            if (var2 != null) {
               this.prependSeparator();
               if (!var2) {
                  var1.append('!');
               }

               var1.append(var1x);
            }
         }

         void addValueString(String var1x, @Nullable Object var2) {
            if (var2 != null) {
               this.prependSeparator();
               var1.append(var1x);
               var1.append('=');
               var1.append(var2);
            }
         }
      }

      1Collector var2 = new 1Collector();
      var2.addValueString("color", this.color);
      var2.addFlagString("bold", this.bold);
      var2.addFlagString("italic", this.italic);
      var2.addFlagString("underlined", this.underlined);
      var2.addFlagString("strikethrough", this.strikethrough);
      var2.addFlagString("obfuscated", this.obfuscated);
      var2.addValueString("clickEvent", this.clickEvent);
      var2.addValueString("hoverEvent", this.hoverEvent);
      var2.addValueString("insertion", this.insertion);
      var2.addValueString("font", this.font);
      var1.append("}");
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Style)) {
         return false;
      } else {
         Style var2 = (Style)var1;
         return this.bold == var2.bold
            && Objects.equals(this.getColor(), var2.getColor())
            && this.italic == var2.italic
            && this.obfuscated == var2.obfuscated
            && this.strikethrough == var2.strikethrough
            && this.underlined == var2.underlined
            && Objects.equals(this.clickEvent, var2.clickEvent)
            && Objects.equals(this.hoverEvent, var2.hoverEvent)
            && Objects.equals(this.insertion, var2.insertion)
            && Objects.equals(this.font, var2.font);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion
      );
   }

   public static class Serializer {
      public static final MapCodec<Style> MAP_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(TextColor.CODEC, "color").forGetter(var0x -> Optional.ofNullable(var0x.color)),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "bold").forGetter(var0x -> Optional.ofNullable(var0x.bold)),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "italic").forGetter(var0x -> Optional.ofNullable(var0x.italic)),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "underlined").forGetter(var0x -> Optional.ofNullable(var0x.underlined)),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "strikethrough").forGetter(var0x -> Optional.ofNullable(var0x.strikethrough)),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "obfuscated").forGetter(var0x -> Optional.ofNullable(var0x.obfuscated)),
                  ExtraCodecs.strictOptionalField(ClickEvent.CODEC, "clickEvent").forGetter(var0x -> Optional.ofNullable(var0x.clickEvent)),
                  ExtraCodecs.strictOptionalField(HoverEvent.CODEC, "hoverEvent").forGetter(var0x -> Optional.ofNullable(var0x.hoverEvent)),
                  ExtraCodecs.strictOptionalField(Codec.STRING, "insertion").forGetter(var0x -> Optional.ofNullable(var0x.insertion)),
                  ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "font").forGetter(var0x -> Optional.ofNullable(var0x.font))
               )
               .apply(var0, Style::create)
      );
      public static final Codec<Style> CODEC = MAP_CODEC.codec();

      public Serializer() {
         super();
      }
   }
}
