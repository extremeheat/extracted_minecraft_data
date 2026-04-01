package net.minecraft.world.entity.livingblock.hurt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;

public class EmptyContentOnDeath implements OnHurt {
   public EmptyContentOnDeath() {
      super();
   }

   public void apply(final LivingBlock livingBlock, final ServerLevel level, final DamageSource source, final float damage, final boolean fatalDamage) {
      if (fatalDamage) {
         Item var7 = livingBlock.getItemStack().getItem();
         if (var7 instanceof DispensibleContainerItem) {
            DispensibleContainerItem bucketItem = (DispensibleContainerItem)var7;
            bucketItem.emptyContents((LivingEntity)null, livingBlock.level(), livingBlock.blockPosition(), (BlockHitResult)null);
            if (bucketItem.emptyContents((LivingEntity)null, livingBlock.level(), livingBlock.blockPosition(), (BlockHitResult)null)) {
               bucketItem.checkExtraContent((LivingEntity)null, livingBlock.level(), livingBlock.getItemStack(), livingBlock.blockPosition());
            }
         }
      }

   }
}
