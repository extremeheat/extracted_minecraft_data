package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Rotation;

public class TemplateRotationArgument extends StringRepresentableArgument<Rotation> {
   private TemplateRotationArgument() {
      super(Rotation.CODEC, Rotation::values);
   }

   public static TemplateRotationArgument templateRotation() {
      return new TemplateRotationArgument();
   }

   public static Rotation getRotation(CommandContext<CommandSourceStack> var0, String var1) {
      return (Rotation)var0.getArgument(var1, Rotation.class);
   }
}
