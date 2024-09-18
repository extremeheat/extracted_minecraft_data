package net.minecraft.world.level.block.state.properties;

import java.util.List;
import java.util.Optional;

public final class BooleanProperty extends Property<Boolean> {
   private static final List<Boolean> VALUES = List.of(true, false);
   private static final int TRUE_INDEX = 0;
   private static final int FALSE_INDEX = 1;

   private BooleanProperty(String var1) {
      super(var1, Boolean.class);
   }

   @Override
   public List<Boolean> getPossibleValues() {
      return VALUES;
   }

   public static BooleanProperty create(String var0) {
      return new BooleanProperty(var0);
   }

   @Override
   public Optional<Boolean> getValue(String var1) {
      return switch (var1) {
         case "true" -> Optional.of(true);
         case "false" -> Optional.of(false);
         default -> Optional.empty();
      };
   }

   public String getName(Boolean var1) {
      return var1.toString();
   }

   public int getInternalIndex(Boolean var1) {
      return var1 ? 0 : 1;
   }
}
