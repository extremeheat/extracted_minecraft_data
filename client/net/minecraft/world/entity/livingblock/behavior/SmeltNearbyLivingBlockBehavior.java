package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SmeltNearbyLivingBlockBehavior<R extends AbstractCookingRecipe> implements LivingBlockBehavior {
   private float SMELT_RADIUS = 3.0F;
   private final RecipeType<R> recipeType;
   private @Nullable LivingBlock smeltingTarget;
   private long smeltStartTick;

   private SmeltNearbyLivingBlockBehavior(final RecipeType<R> recipeType) {
      super();
      this.recipeType = recipeType;
   }

   public static <R extends AbstractCookingRecipe> LivingBlockBehaviorType smeltTarget(final RecipeType<R> recipeType) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new SmeltNearbyLivingBlockBehavior(recipeType)));
   }

   public boolean canStartUsing(final LivingBlock entity) {
      Level var3 = entity.level();
      if (!(var3 instanceof ServerLevel level)) {
         return false;
      } else {
         Vec3 var4 = entity.position();
         this.smeltingTarget = (LivingBlock)level.getNearestEntity(EntityType.LIVING_BLOCK, var4.x(), var4.y(), var4.z(), AABB.around(var4, (double)this.SMELT_RADIUS), (e) -> this.getRecipe(e).isPresent());
         return this.smeltingTarget != null && this.smeltingTarget.position().closerThan(entity.position(), (double)this.SMELT_RADIUS);
      }
   }

   public void onStart(final LivingBlock entity) {
      this.smeltStartTick = entity.level().getGameTime();
      entity.setBlockState((BlockState)entity.getBlockState().trySetValue(AbstractFurnaceBlock.LIT, true));
   }

   public void onStop(final LivingBlock entity) {
      entity.setBlockState((BlockState)entity.getBlockState().trySetValue(AbstractFurnaceBlock.LIT, false));
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (this.smeltingTarget.isAlive() && this.smeltingTarget.position().closerThan(entity.position(), (double)this.SMELT_RADIUS)) {
         Optional<RecipeHolder<R>> maybeRecipe = this.getRecipe(this.smeltingTarget);
         if (maybeRecipe.isEmpty()) {
            return false;
         } else {
            RecipeHolder<R> recipe = (RecipeHolder)maybeRecipe.get();
            int smeltingDuration = (int)(level.getGameTime() - this.smeltStartTick);
            Vec3 targetPos = this.smeltingTarget.getBoundingBox().getCenter();
            Vec3 furnacePos = entity.getBoundingBox().getCenter();
            Vec3 toTarget = targetPos.subtract(furnacePos);
            Vec3 pos = furnacePos.add(toTarget.scale(0.5));
            level.sendParticles(ParticleTypes.SMOKE, pos.x(), pos.y(), pos.z(), 1, toTarget.x() / 2.0, toTarget.y() / 2.0, toTarget.z() / 2.0, 0.0);
            if (smeltingDuration % 20 == 0) {
               entity.playSound(SoundEvents.FURNACE_FIRE_CRACKLE);
            }

            if (smeltingDuration >= ((AbstractCookingRecipe)recipe.value()).cookingTime()) {
               SingleRecipeInput input = new SingleRecipeInput(this.smeltingTarget.getItemStack());
               ItemStack result = ((AbstractCookingRecipe)recipe.value()).assemble(input);
               this.smeltingTarget.setItemStack(result);
               ServerPlayer player = entity.getAttributablePlayer();
               if (player != null) {
                  player.triggerRecipeCrafted(recipe, List.of(result));
                  CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), result);
               }

               return false;
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public Optional<RecipeHolder<R>> getRecipe(final LivingBlock livingBlock) {
      Level var3 = livingBlock.level();
      if (var3 instanceof ServerLevel level) {
         SingleRecipeInput input = new SingleRecipeInput(livingBlock.getItemStack());
         return level.recipeAccess().getRecipeFor(this.recipeType, input, level);
      } else {
         return Optional.empty();
      }
   }
}
