package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightmapTypeArgument extends StringRepresentableArgument<Heightmap.Types> {
   private static final Codec<Heightmap.Types> LOWER_CASE_CODEC = StringRepresentable.fromEnumWithMapping(HeightmapTypeArgument::keptTypes, (var0) -> {
      return var0.toLowerCase(Locale.ROOT);
   });

   private static Heightmap.Types[] keptTypes() {
      return (Heightmap.Types[])Arrays.stream(Heightmap.Types.values()).filter(Heightmap.Types::keepAfterWorldgen).toArray((var0) -> {
         return new Heightmap.Types[var0];
      });
   }

   private HeightmapTypeArgument() {
      super(LOWER_CASE_CODEC, HeightmapTypeArgument::keptTypes);
   }

   public static HeightmapTypeArgument heightmap() {
      return new HeightmapTypeArgument();
   }

   public static Heightmap.Types getHeightmap(CommandContext<CommandSourceStack> var0, String var1) {
      return (Heightmap.Types)var0.getArgument(var1, Heightmap.Types.class);
   }

   protected String convertId(String var1) {
      return var1.toLowerCase(Locale.ROOT);
   }
}
