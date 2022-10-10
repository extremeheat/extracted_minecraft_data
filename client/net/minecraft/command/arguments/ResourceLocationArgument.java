package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> field_201322_e = Arrays.asList("foo", "foo:bar", "012");
   public static final DynamicCommandExceptionType field_197199_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.id.unknown", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType field_197200_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("advancement.advancementNotFound", new Object[]{var0});
   });
   public static final DynamicCommandExceptionType field_197202_d = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("recipe.notFound", new Object[]{var0});
   });

   public ResourceLocationArgument() {
      super();
   }

   public static ResourceLocationArgument func_197197_a() {
      return new ResourceLocationArgument();
   }

   public static Advancement func_197198_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = (ResourceLocation)var0.getArgument(var1, ResourceLocation.class);
      Advancement var3 = ((CommandSource)var0.getSource()).func_197028_i().func_191949_aK().func_192778_a(var2);
      if (var3 == null) {
         throw field_197200_b.create(var2);
      } else {
         return var3;
      }
   }

   public static IRecipe func_197194_b(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = (ResourceLocation)var0.getArgument(var1, ResourceLocation.class);
      IRecipe var3 = ((CommandSource)var0.getSource()).func_197028_i().func_199529_aN().func_199517_a(var2);
      if (var3 == null) {
         throw field_197202_d.create(var2);
      } else {
         return var3;
      }
   }

   public static ResourceLocation func_197195_e(CommandContext<CommandSource> var0, String var1) {
      return (ResourceLocation)var0.getArgument(var1, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader var1) throws CommandSyntaxException {
      return ResourceLocation.func_195826_a(var1);
   }

   public Collection<String> getExamples() {
      return field_201322_e;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
