package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightmapTypeArgument extends StringRepresentableArgument<Heightmap.Types> {
   private static final HeightmapTypeArgument INSTANCE = new HeightmapTypeArgument();
   public static final SerializableArgument.Type<HeightmapTypeArgument> TYPE;
   private static final Codec<Heightmap.Types> LOWER_CASE_CODEC;

   private static Heightmap.Types[] keptTypes() {
      return (Heightmap.Types[])Arrays.stream(Heightmap.Types.values()).filter(Heightmap.Types::keepAfterWorldgen).toArray((x$0) -> new Heightmap.Types[x$0]);
   }

   private HeightmapTypeArgument() {
      super(LOWER_CASE_CODEC, HeightmapTypeArgument::keptTypes);
   }

   public SerializableArgument.Type<HeightmapTypeArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Heightmap.Types> heightmap() {
      return INSTANCE.toBrigadier();
   }

   public static Heightmap.Types getHeightmap(final CommandContext<?> context, final String name) {
      return (Heightmap.Types)context.getArgument(name, Heightmap.Types.class);
   }

   protected String convertId(final String id) {
      return id.toLowerCase(Locale.ROOT);
   }

   static {
      TYPE = SerializableArgument.<HeightmapTypeArgument>singletonType(INSTANCE);
      LOWER_CASE_CODEC = StringRepresentable.<Heightmap.Types>fromEnumWithMapping(HeightmapTypeArgument::keptTypes, (s) -> s.toLowerCase(Locale.ROOT));
   }
}
