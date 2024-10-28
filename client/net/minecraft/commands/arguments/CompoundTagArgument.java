package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class CompoundTagArgument implements ArgumentType<CompoundTag> {
   private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");

   private CompoundTagArgument() {
      super();
   }

   public static CompoundTagArgument compoundTag() {
      return new CompoundTagArgument();
   }

   public static <S> CompoundTag getCompoundTag(CommandContext<S> var0, String var1) {
      return (CompoundTag)var0.getArgument(var1, CompoundTag.class);
   }

   public CompoundTag parse(StringReader var1) throws CommandSyntaxException {
      return (new TagParser(var1)).readStruct();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
