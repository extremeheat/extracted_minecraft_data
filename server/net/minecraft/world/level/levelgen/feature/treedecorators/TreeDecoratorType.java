package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
   public static final TreeDecoratorType<TrunkVineDecorator> TRUNK_VINE;
   public static final TreeDecoratorType<LeaveVineDecorator> LEAVE_VINE;
   public static final TreeDecoratorType<CocoaDecorator> COCOA;
   public static final TreeDecoratorType<BeehiveDecorator> BEEHIVE;
   public static final TreeDecoratorType<AlterGroundDecorator> ALTER_GROUND;
   private final Codec<P> codec;

   private static <P extends TreeDecorator> TreeDecoratorType<P> register(String var0, Codec<P> var1) {
      return (TreeDecoratorType)Registry.register(Registry.TREE_DECORATOR_TYPES, (String)var0, new TreeDecoratorType(var1));
   }

   private TreeDecoratorType(Codec<P> var1) {
      super();
      this.codec = var1;
   }

   public Codec<P> codec() {
      return this.codec;
   }

   static {
      TRUNK_VINE = register("trunk_vine", TrunkVineDecorator.CODEC);
      LEAVE_VINE = register("leave_vine", LeaveVineDecorator.CODEC);
      COCOA = register("cocoa", CocoaDecorator.CODEC);
      BEEHIVE = register("beehive", BeehiveDecorator.CODEC);
      ALTER_GROUND = register("alter_ground", AlterGroundDecorator.CODEC);
   }
}
