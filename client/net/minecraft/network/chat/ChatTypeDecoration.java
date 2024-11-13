package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public record ChatTypeDecoration(String translationKey, List<Parameter> parameters, Style style) {
   public static final Codec<ChatTypeDecoration> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("translation_key").forGetter(ChatTypeDecoration::translationKey), ChatTypeDecoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(ChatTypeDecoration::parameters), Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(ChatTypeDecoration::style)).apply(var0, ChatTypeDecoration::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, ChatTypeDecoration> STREAM_CODEC;

   public ChatTypeDecoration(String var1, List<Parameter> var2, Style var3) {
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
      return new ChatTypeDecoration(var0, List.of(ChatTypeDecoration.Parameter.TARGET, ChatTypeDecoration.Parameter.SENDER, ChatTypeDecoration.Parameter.CONTENT), Style.EMPTY);
   }

   public Component decorate(Component var1, ChatType.Bound var2) {
      Component[] var3 = this.resolveParameters(var1, var2);
      return Component.translatable(this.translationKey, var3).withStyle(this.style);
   }

   private Component[] resolveParameters(Component var1, ChatType.Bound var2) {
      Component[] var3 = new Component[this.parameters.size()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Parameter var5 = (Parameter)this.parameters.get(var4);
         var3[var4] = var5.select(var1, var2);
      }

      return var3;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ChatTypeDecoration::translationKey, ChatTypeDecoration.Parameter.STREAM_CODEC.apply(ByteBufCodecs.list()), ChatTypeDecoration::parameters, Style.Serializer.TRUSTED_STREAM_CODEC, ChatTypeDecoration::style, ChatTypeDecoration::new);
   }

   public static enum Parameter implements StringRepresentable {
      SENDER(0, "sender", (var0, var1) -> var1.name()),
      TARGET(1, "target", (var0, var1) -> (Component)var1.targetName().orElse(CommonComponents.EMPTY)),
      CONTENT(2, "content", (var0, var1) -> var0);

      private static final IntFunction<Parameter> BY_ID = ByIdMap.<Parameter>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Parameter> CODEC = StringRepresentable.<Parameter>fromEnum(Parameter::values);
      public static final StreamCodec<ByteBuf, Parameter> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
      private final int id;
      private final String name;
      private final Selector selector;

      private Parameter(final int var3, final String var4, final Selector var5) {
         this.id = var3;
         this.name = var4;
         this.selector = var5;
      }

      public Component select(Component var1, ChatType.Bound var2) {
         return this.selector.select(var1, var2);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Parameter[] $values() {
         return new Parameter[]{SENDER, TARGET, CONTENT};
      }

      public interface Selector {
         Component select(Component var1, ChatType.Bound var2);
      }
   }
}
