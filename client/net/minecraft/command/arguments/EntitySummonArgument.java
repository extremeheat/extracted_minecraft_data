package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class EntitySummonArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> field_211370_b = Arrays.asList("minecraft:pig", "cow");
   public static final DynamicCommandExceptionType field_211369_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("entity.notFound", new Object[]{var0});
   });

   public EntitySummonArgument() {
      super();
   }

   public static EntitySummonArgument func_211366_a() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation func_211368_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return func_211365_a((ResourceLocation)var0.getArgument(var1, ResourceLocation.class));
   }

   private static final ResourceLocation func_211365_a(ResourceLocation var0) throws CommandSyntaxException {
      EntityType var1 = (EntityType)IRegistry.field_212629_r.func_212608_b(var0);
      if (var1 != null && var1.func_200720_b()) {
         return var0;
      } else {
         throw field_211369_a.create(var0);
      }
   }

   public ResourceLocation parse(StringReader var1) throws CommandSyntaxException {
      return func_211365_a(ResourceLocation.func_195826_a(var1));
   }

   public Collection<String> getExamples() {
      return field_211370_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
