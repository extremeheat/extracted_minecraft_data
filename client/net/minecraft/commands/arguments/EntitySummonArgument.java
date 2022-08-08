package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntitySummonArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("entity.notFound", var0);
   });

   public EntitySummonArgument() {
      super();
   }

   public static EntitySummonArgument id() {
      return new EntitySummonArgument();
   }

   public static ResourceLocation getSummonableEntity(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return verifyCanSummon((ResourceLocation)var0.getArgument(var1, ResourceLocation.class));
   }

   private static ResourceLocation verifyCanSummon(ResourceLocation var0) throws CommandSyntaxException {
      Registry.ENTITY_TYPE.getOptional(var0).filter(EntityType::canSummon).orElseThrow(() -> {
         return ERROR_UNKNOWN_ENTITY.create(var0);
      });
      return var0;
   }

   public ResourceLocation parse(StringReader var1) throws CommandSyntaxException {
      return verifyCanSummon(ResourceLocation.read(var1));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
