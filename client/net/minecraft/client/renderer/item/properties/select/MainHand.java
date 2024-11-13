package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record MainHand() implements SelectItemModelProperty<HumanoidArm> {
   public static final SelectItemModelProperty.Type<MainHand, HumanoidArm> TYPE;

   public MainHand() {
      super();
   }

   @Nullable
   public HumanoidArm get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var3 == null ? null : var3.getMainArm();
   }

   public SelectItemModelProperty.Type<MainHand, HumanoidArm> type() {
      return TYPE;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.<MainHand, HumanoidArm>create(MapCodec.unit(new MainHand()), HumanoidArm.CODEC);
   }
}
