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
   Codec<Action> CODEC = BuiltInRegistries.DIALOG_ACTION_TYPE.byNameCodec().dispatch(Action::codec, (var0) -> var0);

   MapCodec<? extends Action> codec();

   Optional<ClickEvent> createAction(Map<String, ValueGetter> var1);

   public interface ValueGetter {
      String asTemplateSubstitution();

      Tag asTag();

      static Map<String, String> getAsTemplateSubstitutions(Map<String, ValueGetter> var0) {
         return Maps.transformValues(var0, ValueGetter::asTemplateSubstitution);
      }

      static ValueGetter of(final String var0) {
         return new ValueGetter() {
            public String asTemplateSubstitution() {
               return var0;
            }

            public Tag asTag() {
               return StringTag.valueOf(var0);
            }
         };
      }

      static ValueGetter of(final Supplier<String> var0) {
         return new ValueGetter() {
            public String asTemplateSubstitution() {
               return (String)var0.get();
            }

            public Tag asTag() {
               return StringTag.valueOf((String)var0.get());
            }
         };
      }
   }
}
