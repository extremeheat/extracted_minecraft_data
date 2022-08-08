package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

public record ChatDecoration(String b, List<Parameter> c, Style d) {
   private final String translationKey;
   private final List<Parameter> parameters;
   private final Style style;
   public static final Codec<ChatDecoration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.STRING.fieldOf("translation_key").forGetter(ChatDecoration::translationKey), ChatDecoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(ChatDecoration::parameters), Style.FORMATTING_CODEC.fieldOf("style").forGetter(ChatDecoration::style)).apply(var0, ChatDecoration::new);
   });

   public ChatDecoration(String var1, List<Parameter> var2, Style var3) {
      super();
      this.translationKey = var1;
      this.parameters = var2;
      this.style = var3;
   }

   public static ChatDecoration withSender(String var0) {
      return new ChatDecoration(var0, List.of(ChatDecoration.Parameter.SENDER, ChatDecoration.Parameter.CONTENT), Style.EMPTY);
   }

   public static ChatDecoration directMessage(String var0) {
      Style var1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
      return new ChatDecoration(var0, List.of(ChatDecoration.Parameter.SENDER, ChatDecoration.Parameter.CONTENT), var1);
   }

   public static ChatDecoration teamMessage(String var0) {
      return new ChatDecoration(var0, List.of(ChatDecoration.Parameter.TEAM_NAME, ChatDecoration.Parameter.SENDER, ChatDecoration.Parameter.CONTENT), Style.EMPTY);
   }

   public Component decorate(Component var1, @Nullable ChatSender var2) {
      Component[] var3 = this.resolveParameters(var1, var2);
      return Component.translatable(this.translationKey, var3).withStyle(this.style);
   }

   private Component[] resolveParameters(Component var1, @Nullable ChatSender var2) {
      Component[] var3 = new Component[this.parameters.size()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Parameter var5 = (Parameter)this.parameters.get(var4);
         var3[var4] = var5.select(var1, var2);
      }

      return var3;
   }

   public String translationKey() {
      return this.translationKey;
   }

   public List<Parameter> parameters() {
      return this.parameters;
   }

   public Style style() {
      return this.style;
   }

   public static enum Parameter implements StringRepresentable {
      SENDER("sender", (var0, var1) -> {
         return var1 != null ? var1.name() : null;
      }),
      TEAM_NAME("team_name", (var0, var1) -> {
         return var1 != null ? var1.teamName() : null;
      }),
      CONTENT("content", (var0, var1) -> {
         return var0;
      });

      public static final Codec<Parameter> CODEC = StringRepresentable.fromEnum(Parameter::values);
      private final String name;
      private final Selector selector;

      private Parameter(String var3, Selector var4) {
         this.name = var3;
         this.selector = var4;
      }

      public Component select(Component var1, @Nullable ChatSender var2) {
         Component var3 = this.selector.select(var1, var2);
         return (Component)Objects.requireNonNullElse(var3, CommonComponents.EMPTY);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Parameter[] $values() {
         return new Parameter[]{SENDER, TEAM_NAME, CONTENT};
      }

      public interface Selector {
         @Nullable
         Component select(Component var1, @Nullable ChatSender var2);
      }
   }
}
