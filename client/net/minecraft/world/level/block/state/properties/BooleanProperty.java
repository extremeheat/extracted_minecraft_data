package net.minecraft.world.level.block.state.properties;

import java.util.List;
import java.util.Optional;

public final class BooleanProperty extends Property<Boolean> {
   private static final List<Boolean> VALUES = List.of(true, false);
   private static final int TRUE_INDEX = 0;
   private static final int FALSE_INDEX = 1;

   private BooleanProperty(final String name) {
      super(name, Boolean.class);
   }

   public List<Boolean> getPossibleValues() {
      return VALUES;
   }

   public static BooleanProperty create(final String name) {
      return new BooleanProperty(name);
   }

   public Optional<Boolean> getValue(final String name) {
      Optional var10000;
      switch (name) {
         case "true" -> var10000 = Optional.of(true);
         case "false" -> var10000 = Optional.of(false);
         default -> var10000 = Optional.empty();
      }

      return var10000;
   }

   public String getName(final Boolean value) {
      return value.toString();
   }

   public int getInternalIndex(final Boolean value) {
      return value ? 0 : 1;
   }
}
