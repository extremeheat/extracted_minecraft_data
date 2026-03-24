package net.minecraft.server.dialog.action;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;

public interface Action {
   Codec<Action> CODEC = BuiltInRegistries.DIALOG_ACTION_TYPE.byNameCodec().dispatch(Action::codec, (c) -> c);

   MapCodec<? extends Action> codec();

   Optional<ClickEvent> createAction(Map<String, ValueGetter> parameters);

   public interface ValueGetter {
      String asTemplateSubstitution();

      Tag asTag();

      static Map<String, String> getAsTemplateSubstitutions(final Map<String, ValueGetter> parameters) {
         return Maps.transformValues(parameters, ValueGetter::asTemplateSubstitution);
      }

      static ValueGetter of(final String value) {
         return new ValueGetter() {
            public String asTemplateSubstitution() {
               return value;
            }

            public Tag asTag() {
               return StringTag.valueOf(value);
            }
         };
      }

      static ValueGetter of(final Supplier<String> value) {
         return new ValueGetter() {
            public String asTemplateSubstitution() {
               return (String)value.get();
            }

            public Tag asTag() {
               return StringTag.valueOf((String)value.get());
            }
         };
      }
   }
}
