package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

public class NBTArgument implements ArgumentType<NBTTagCompound> {
   private static final Collection<String> field_201315_b = Arrays.asList("{}", "{foo=bar}");
   public static final DynamicCommandExceptionType field_197132_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.nbt.invalid", new Object[]{var0});
   });

   private NBTArgument() {
      super();
   }

   public static NBTArgument func_197131_a() {
      return new NBTArgument();
   }

   public static <S> NBTTagCompound func_197130_a(CommandContext<S> var0, String var1) {
      return (NBTTagCompound)var0.getArgument(var1, NBTTagCompound.class);
   }

   public NBTTagCompound parse(StringReader var1) throws CommandSyntaxException {
      return (new JsonToNBT(var1)).func_193593_f();
   }

   public Collection<String> getExamples() {
      return field_201315_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
