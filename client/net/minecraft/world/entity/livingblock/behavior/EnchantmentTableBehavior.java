package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EnchantmentTableBehavior implements LivingBlockBehavior {
   private static final int ENCHANTING_TIMEOUT_DURATION = 300;
   private int waitingTicks;

   public EnchantmentTableBehavior() {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.livingBlockBeingEnchanted != null;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      ++this.waitingTicks;
      if (this.waitingTicks > 300) {
         this.cleanupEnchantingAttempt(entity);
         return false;
      } else if (entity.livingBlockBeingEnchanted == null) {
         this.cleanupEnchantingAttempt(entity);
         return false;
      } else {
         Player commander = entity.getCommander();
         if (commander == null) {
            this.cleanupEnchantingAttempt(entity);
            return false;
         } else {
            Vec3 enchantablePos = entity.livingBlockBeingEnchanted.position();
            Vec3 above = entity.position().add(Vec3.Y_AXIS);
            boolean isItemToEnchantNearby = enchantablePos.closerThan(above, 0.699999988079071);
            List<LivingBlock> ownedBookshelves = commander.getCommandedBlocks().stream().filter((block) -> block.isBlock(Blocks.BOOKSHELF)).toList();
            boolean areBookshelvesNearby = ownedBookshelves.stream().allMatch((livingBLock) -> livingBLock.closerThan(entity, 5.0));
            if (isItemToEnchantNearby && areBookshelvesNearby) {
               ItemStack itemStack = entity.livingBlockBeingEnchanted.getItemStack();
               int cost = EnchantmentHelper.getEnchantmentCost(level.getRandom(), 2, ownedBookshelves.size(), itemStack);
               if (cost >= commander.experienceLevel) {
                  this.cleanupEnchantingAttempt(entity);
                  return false;
               } else {
                  commander.giveExperienceLevels(-cost);

                  for(EnchantmentInstance enchantment : this.getEnchantmentList(level.getRandom(), level.registryAccess(), itemStack, cost)) {
                     itemStack.enchant(enchantment.enchantment(), enchantment.level());
                  }

                  LivingBlock newItem = LivingBlock.createAt(level, entity.livingBlockBeingEnchanted.blockPosition().above(1), (ItemStack)itemStack);
                  entity.livingBlockBeingEnchanted.discard();
                  RandomSource random = level.getRandom();
                  level.sendParticles(ParticleTypes.EXPLOSION, true, true, newItem.getX() + random.nextDouble(), newItem.getY(), newItem.getZ() + random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                  this.cleanupEnchantingAttempt(entity);
                  return false;
               }
            } else {
               return true;
            }
         }
      }
   }

   private void cleanupEnchantingAttempt(final LivingBlock entity) {
      this.waitingTicks = 0;
      Player commander = entity.getCommander();
      if (commander != null) {
         commander.getCommandedBlocks().stream().filter((block) -> block.isBlock(Blocks.BOOKSHELF)).forEach((block) -> block.setCommander((Player)null));
      }

      if (entity.livingBlockBeingEnchanted != null) {
         entity.livingBlockBeingEnchanted.setCommander((Player)null);
      }

      entity.livingBlockBeingEnchanted = null;
      entity.setCommander((Player)null);
   }

   private List<EnchantmentInstance> getEnchantmentList(final RandomSource random, final RegistryAccess access, final ItemStack itemStack, final int enchantmentCost) {
      Optional<HolderSet.Named<Enchantment>> tag = access.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE);
      if (tag.isEmpty()) {
         return List.of();
      } else {
         List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(random, itemStack, enchantmentCost, ((HolderSet.Named)tag.get()).stream());
         if (itemStack.is(Items.BOOK) && list.size() > 1) {
            list.remove(random.nextInt(list.size()));
         }

         return list;
      }
   }

   public static LivingBlockBehaviorType enchantmentTable() {
      return LivingBlockBehaviorType.behaviorType(EnchantmentTableBehavior::new);
   }
}
