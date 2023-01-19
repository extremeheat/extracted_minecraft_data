package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

public record ChatTypeDecoration(String b, List<ChatTypeDecoration.Parameter> c, Style d) {
   private final String translationKey;
   private final List<ChatTypeDecoration.Parameter> parameters;
   private final Style style;
   public static final Codec<ChatTypeDecoration> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.STRING.fieldOf("translation_key").forGetter(ChatTypeDecoration::translationKey),
               ChatTypeDecoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(ChatTypeDecoration::parameters),
               Style.FORMATTING_CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(ChatTypeDecoration::style)
            )
            .apply(var0, ChatTypeDecoration::new)
   );

   public ChatTypeDecoration(String var1, List<ChatTypeDecoration.Parameter> var2, Style var3) {
      super();
      this.translationKey = var1;
      this.parameters = var2;
      this.style = var3;
   }

   public static ChatTypeDecoration withSender(String var0) {
      return new ChatTypeDecoration(var0, List.of(ChatTypeDecoration.Parameter.SENDER, ChatTypeDecoration.Parameter.CONTENT), Style.EMPTY);
   }

   public static ChatTypeDecoration incomingDirectMessage(String var0) {
      Style var1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
      return new ChatTypeDecoration(var0, List.of(ChatTypeDecoration.Parameter.SENDER, ChatTypeDecoration.Parameter.CONTENT), var1);
   }

   public static ChatTypeDecoration outgoingDirectMessage(String var0) {
      Style var1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
      return new ChatTypeDecoration(var0, List.of(ChatTypeDecoration.Parameter.TARGET, ChatTypeDecoration.Parameter.CONTENT), var1);
   }

   public static ChatTypeDecoration teamMessage(String var0) {
      return new ChatTypeDecoration(
         var0, List.of(ChatTypeDecoration.Parameter.TARGET, ChatTypeDecoration.Parameter.SENDER, ChatTypeDecoration.Parameter.CONTENT), Style.EMPTY
      );
   }

   public Component decorate(Component var1, ChatType.Bound var2) {
      Component[] var3 = this.resolveParameters(var1, var2);
      return Component.translatable(this.translationKey, var3).withStyle(this.style);
   }

   private Component[] resolveParameters(Component var1, ChatType.Bound var2) {
      Component[] var3 = new Component[this.parameters.size()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         ChatTypeDecoration.Parameter var5 = this.parameters.get(var4);
         var3[var4] = var5.select(var1, var2);
      }

      return var3;
   }

   public static enum Parameter implements StringRepresentable {
      SENDER("sender", (var0, var1) -> var1.name()),
      TARGET("target", (var0, var1) -> var1.targetName()),
      CONTENT("content", (var0, var1) -> var0);

      public static final Codec<ChatTypeDecoration.Parameter> CODEC = StringRepresentable.fromEnum(ChatTypeDecoration.Parameter::values);
      private final String name;
      private final ChatTypeDecoration.Parameter.Selector selector;

      private Parameter(String var3, ChatTypeDecoration.Parameter.Selector var4) {
         this.name = var3;
         this.selector = var4;
      }

      public Component select(Component var1, ChatType.Bound var2) {
         Component var3 = this.selector.select(var1, var2);
         return Objects.requireNonNullElse(var3, CommonComponents.EMPTY);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public interface Selector {
         @Nullable
         Component select(Component var1, ChatType.Bound var2);
      }
   }
}
