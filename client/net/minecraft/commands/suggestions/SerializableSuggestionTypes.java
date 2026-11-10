package net.minecraft.commands.suggestions;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class SerializableSuggestionTypes {
   public static final Codec<SerializableCustomSuggestion> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, SerializableCustomSuggestion> STREAM_CODEC;

   public SerializableSuggestionTypes() {
      super();
   }

   public static void bootstrap(final WritableRegistry<SerializableCustomSuggestion.Type<?>> registry) {
      Registry.register(registry, (String)"ask_server", AskServer.TYPE);
      Registry.register(registry, (String)"available_sounds", SuggestAvailableSounds.TYPE);
      Registry.register(registry, (String)"summonable_entities", SuggestSummonableEntities.TYPE);
      Registry.register(registry, (String)"post_effects", SuggestPostEffects.TYPE);
      Registry.register(registry, (String)"literal", SuggestLiteralString.TYPE);
   }

   static {
      CODEC = BuiltInRegistries.COMMAND_SUGGESTION_TYPE.byNameCodec().dispatch(SerializableCustomSuggestion::type, SerializableCustomSuggestion.Type::codec);
      STREAM_CODEC = ByteBufCodecs.registry(Registries.COMMAND_SUGGESTION_TYPE).dispatch(SerializableCustomSuggestion::type, SerializableCustomSuggestion.Type::streamCodec);
   }
}
