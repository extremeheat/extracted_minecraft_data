package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.Registry;

public class TreeDecoratorType {
   public static final TreeDecoratorType TRUNK_VINE = register("trunk_vine", TrunkVineDecorator::new);
   public static final TreeDecoratorType LEAVE_VINE = register("leave_vine", LeaveVineDecorator::new);
   public static final TreeDecoratorType COCOA = register("cocoa", CocoaDecorator::new);
   public static final TreeDecoratorType BEEHIVE = register("beehive", BeehiveDecorator::new);
   public static final TreeDecoratorType ALTER_GROUND = register("alter_ground", AlterGroundDecorator::new);
   private final Function deserializer;

   private static TreeDecoratorType register(String var0, Function var1) {
      return (TreeDecoratorType)Registry.register(Registry.TREE_DECORATOR_TYPES, (String)var0, new TreeDecoratorType(var1));
   }

   private TreeDecoratorType(Function var1) {
      this.deserializer = var1;
   }

   public TreeDecorator deserialize(Dynamic var1) {
      return (TreeDecorator)this.deserializer.apply(var1);
   }
}
