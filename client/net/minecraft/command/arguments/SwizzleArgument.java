package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.command.CommandSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;

public class SwizzleArgument implements ArgumentType<EnumSet<EnumFacing.Axis>> {
   private static final Collection<String> field_201335_a = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType field_197294_a = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.swizzle.invalid", new Object[0]));

   public SwizzleArgument() {
      super();
   }

   public static SwizzleArgument func_197293_a() {
      return new SwizzleArgument();
   }

   public static EnumSet<EnumFacing.Axis> func_197291_a(CommandContext<CommandSource> var0, String var1) {
      return (EnumSet)var0.getArgument(var1, EnumSet.class);
   }

   public EnumSet<EnumFacing.Axis> parse(StringReader var1) throws CommandSyntaxException {
      EnumSet var2 = EnumSet.noneOf(EnumFacing.Axis.class);

      while(var1.canRead() && var1.peek() != ' ') {
         char var3 = var1.read();
         EnumFacing.Axis var4;
         switch(var3) {
         case 'x':
            var4 = EnumFacing.Axis.X;
            break;
         case 'y':
            var4 = EnumFacing.Axis.Y;
            break;
         case 'z':
            var4 = EnumFacing.Axis.Z;
            break;
         default:
            throw field_197294_a.create();
         }

         if (var2.contains(var4)) {
            throw field_197294_a.create();
         }

         var2.add(var4);
      }

      return var2;
   }

   public Collection<String> getExamples() {
      return field_201335_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
