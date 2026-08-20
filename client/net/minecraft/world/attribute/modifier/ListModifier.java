package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface ListModifier<Element> extends AttributeModifier<List<Element>, List<Element>> {
   static <Element> ListModifier<Element> append() {
      return ListModifier.Append.INSTANCE;
   }

   default Codec<List<Element>> argumentCodec(final EnvironmentAttribute<List<Element>> attribute) {
      return attribute.valueCodec();
   }

   default LerpFunction<List<Element>> argumentKeyframeLerp(final EnvironmentAttribute<List<Element>> attribute) {
      return attribute.type().keyframeLerp();
   }

   public static record Append<Element>() implements ListModifier<Element> {
      private static final Append<?> INSTANCE = new Append();

      public Append() {
         super();
      }

      public List<Element> apply(final List<Element> subject, final List<Element> argument) {
         if (argument.isEmpty()) {
            return subject;
         } else {
            return subject.isEmpty() ? argument : Util.join(subject, argument);
         }
      }
   }
}
