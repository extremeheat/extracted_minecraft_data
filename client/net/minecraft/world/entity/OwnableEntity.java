package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public interface OwnableEntity {
   @Nullable
   EntityReference<LivingEntity> getOwnerReference();

   Level level();

   @Nullable
   default LivingEntity getOwner() {
      return EntityReference.getLivingEntity(this.getOwnerReference(), this.level());
   }

   @Nullable
   default LivingEntity getRootOwner() {
      ObjectArraySet var1 = new ObjectArraySet();
      LivingEntity var2 = this.getOwner();
      var1.add(this);

      while(var2 instanceof OwnableEntity) {
         OwnableEntity var3 = (OwnableEntity)var2;
         LivingEntity var4 = var3.getOwner();
         if (var1.contains(var4)) {
            return null;
         }

         var1.add(var2);
         var2 = var3.getOwner();
      }

      return var2;
   }
}
