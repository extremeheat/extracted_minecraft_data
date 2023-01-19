package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
   public static final TreeDecoratorType<TrunkVineDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineDecorator.CODEC);
   public static final TreeDecoratorType<LeaveVineDecorator> LEAVE_VINE = register("leave_vine", LeaveVineDecorator.CODEC);
   public static final TreeDecoratorType<CocoaDecorator> COCOA = register("cocoa", CocoaDecorator.CODEC);
   public static final TreeDecoratorType<BeehiveDecorator> BEEHIVE = register("beehive", BeehiveDecorator.CODEC);
   public static final TreeDecoratorType<AlterGroundDecorator> ALTER_GROUND = register("alter_ground", AlterGroundDecorator.CODEC);
   public static final TreeDecoratorType<AttachedToLeavesDecorator> ATTACHED_TO_LEAVES = register("attached_to_leaves", AttachedToLeavesDecorator.CODEC);
   private final Codec<P> codec;

   private static <P extends TreeDecorator> TreeDecoratorType<P> register(String var0, Codec<P> var1) {
      return Registry.register(Registry.TREE_DECORATOR_TYPES, var0, new TreeDecoratorType<>(var1));
   }

   private TreeDecoratorType(Codec<P> var1) {
      super();
      this.codec = var1;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
