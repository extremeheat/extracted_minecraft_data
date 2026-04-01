package net.minecraft.world.entity.livingblock.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmeltBehavior extends TransformWhenIntersectingBehavior.TransformIntoItemStack {
   public static final LivingBlockBehaviorType LAVA = LivingBlockBehaviorType.behaviorType((Function)((var0) -> new SmeltBehavior(Blocks.LAVA, ItemStack.EMPTY)));
   public static final LivingBlockBehaviorType FIRE = LivingBlockBehaviorType.behaviorType((Function)((var0) -> new SmeltBehavior(Blocks.FIRE, ItemStack.EMPTY) {
         protected boolean isColliding(final Entity entity, final ServerLevel level, final BlockPos pos, final BlockState state) {
            VoxelShape movedBlockShape = state.getShape(level, pos, CollisionContext.of(entity, false)).move((Vec3i)pos);
            return Shapes.joinIsNotEmpty(movedBlockShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
         }
      }));
   private Optional<ItemStack> result = Optional.empty();

   private SmeltBehavior(final Block intersectWith, final ItemStack transformInto) {
      super(intersectWith, transformInto);
   }

   public boolean checkInside(final LivingBlock entity, final ServerLevel level) {
      if (this.result.isEmpty()) {
         SingleRecipeInput input = new SingleRecipeInput(entity.getItemStack());
         Optional<RecipeHolder<SmeltingRecipe>> maybeRecipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level);
         this.result = maybeRecipe.map((holder) -> ((SmeltingRecipe)holder.value()).assemble(input)).or(() -> Optional.of(ItemStack.EMPTY));
      }

      return !this.getTransformInto().isEmpty() ? super.checkInside(entity, level) : false;
   }

   public ItemStack getTransformInto() {
      return (ItemStack)this.result.get();
   }
}
