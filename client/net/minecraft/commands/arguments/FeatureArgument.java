package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FeatureArgument extends ResourceOrIdArgument.Simple<Feature> {
   private static final FeatureArgument INSTANCE = new FeatureArgument();
   public static final SerializableArgument.Type<FeatureArgument> TYPE;

   private FeatureArgument() {
      super(Registries.FEATURE, Feature.DIRECT_CODEC);
   }

   public SerializableArgument.Type<FeatureArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Holder<Feature>> feature() {
      return INSTANCE.toBrigadier();
   }

   public static Holder<Feature> getFeature(final CommandContext<?> context, final String name) {
      return getResource(context, name);
   }

   static {
      TYPE = SerializableArgument.<FeatureArgument>singletonType(INSTANCE);
   }
}
