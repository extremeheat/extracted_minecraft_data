package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;

public class DirectionProperty extends EnumProperty<Direction> {
   protected DirectionProperty(String var1, List<Direction> var2) {
      super(var1, Direction.class, var2);
   }

   public static DirectionProperty create(String var0) {
      return create(var0, var0x -> true);
   }

   public static DirectionProperty create(String var0, Predicate<Direction> var1) {
      return create(var0, Arrays.stream(Direction.values()).filter(var1).collect(Collectors.toList()));
   }

   public static DirectionProperty create(String var0, Direction... var1) {
      return create(var0, Lists.newArrayList(var1));
   }

   public static DirectionProperty create(String var0, List<Direction> var1) {
      return new DirectionProperty(var0, var1);
   }
}
