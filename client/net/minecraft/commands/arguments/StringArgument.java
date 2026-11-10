package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public class StringArgument extends BrigadierWrapperArgument<String, StringArgumentType> {
   private static final MapCodec<StringArgument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(StringArgument.StringType.CODEC.fieldOf("type").forGetter((o) -> o.stringType)).apply(i, StringArgument::new));
   private static final StreamCodec<ByteBuf, StringArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<StringArgument> TYPE;
   private final StringType stringType;

   private StringArgument(final StringArgumentType original, final StringType stringType) {
      super(original);
      this.stringType = stringType;
   }

   public SerializableArgument.Type<StringArgument> type() {
      return TYPE;
   }

   private StringArgument(final StringType type) {
      this(createBrigadierType(type), type);
   }

   private static StringArgumentType createBrigadierType(final StringType type) {
      StringArgumentType var10000;
      switch (type.ordinal()) {
         case 0 -> var10000 = StringArgumentType.word();
         case 1 -> var10000 = StringArgumentType.string();
         case 2 -> var10000 = StringArgumentType.greedyString();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static ArgumentType<String> word() {
      return (new StringArgument(StringArgument.StringType.WORD)).toBrigadier();
   }

   public static ArgumentType<String> string() {
      return (new StringArgument(StringArgument.StringType.QUOTED)).toBrigadier();
   }

   public static ArgumentType<String> greedyString() {
      return (new StringArgument(StringArgument.StringType.GREEDY)).toBrigadier();
   }

   public static String getString(final CommandContext<?> context, final String name) {
      return (String)context.getArgument(name, String.class);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(StringArgument.StringType.STREAM_CODEC, (o) -> o.stringType, StringArgument::new);
      TYPE = new SerializableArgument.Type<StringArgument>(MAP_CODEC, STREAM_CODEC);
   }

   public static enum StringType implements StringRepresentable {
      WORD(0, "word"),
      QUOTED(1, "quoted"),
      GREEDY(2, "greedy");

      public static final Codec<StringType> CODEC = StringRepresentable.<StringType>fromEnum(StringType::values);
      public static final StreamCodec<ByteBuf, StringType> STREAM_CODEC = ByteBufCodecs.enumCodec(StringType.class, (v) -> v.id);
      private final int id;
      private final String name;

      private StringType(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static StringType[] $values() {
         return new StringType[]{WORD, QUOTED, GREEDY};
      }
   }
}
