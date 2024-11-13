package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;

public record Charge() implements SelectItemModelProperty<CrossbowItem.ChargeType> {
   public static final SelectItemModelProperty.Type<Charge, CrossbowItem.ChargeType> TYPE;

   public Charge() {
      super();
   }

   public CrossbowItem.ChargeType get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      ChargedProjectiles var6 = (ChargedProjectiles)var1.get(DataComponents.CHARGED_PROJECTILES);
      if (var6 != null && !var6.isEmpty()) {
         return var6.contains(Items.FIREWORK_ROCKET) ? CrossbowItem.ChargeType.ROCKET : CrossbowItem.ChargeType.ARROW;
      } else {
         return CrossbowItem.ChargeType.NONE;
      }
   }

   public SelectItemModelProperty.Type<Charge, CrossbowItem.ChargeType> type() {
      return TYPE;
   }

   // $FF: synthetic method
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.<Charge, CrossbowItem.ChargeType>create(MapCodec.unit(new Charge()), CrossbowItem.ChargeType.CODEC);
   }
}
