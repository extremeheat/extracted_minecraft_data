package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

public class NbtTagArgument implements ArgumentType<Tag> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

   private NbtTagArgument() {
      super();
   }

   public static NbtTagArgument nbtTag() {
      return new NbtTagArgument();
   }

   public static <S> Tag getNbtTag(CommandContext<S> var0, String var1) {
      return (Tag)var0.getArgument(var1, Tag.class);
   }

   public Tag parse(StringReader var1) throws CommandSyntaxException {
      return (new TagParser(var1)).readValue();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
