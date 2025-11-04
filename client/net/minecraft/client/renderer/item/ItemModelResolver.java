package net.minecraft.client.renderer.item;

import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ItemModelResolver {
   private final Function<Identifier, ItemModel> modelGetter;
   private final Function<Identifier, ClientItem.Properties> clientProperties;

   public ItemModelResolver(ModelManager var1) {
      super();
      Objects.requireNonNull(var1);
      this.modelGetter = var1::getItemModel;
      Objects.requireNonNull(var1);
      this.clientProperties = var1::getItemProperties;
   }

   public void updateForLiving(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, LivingEntity var4) {
      this.updateForTopItem(var1, var2, var3, var4.level(), var4, var4.getId() + var3.ordinal());
   }

   public void updateForNonLiving(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, Entity var4) {
      this.updateForTopItem(var1, var2, var3, var4.level(), (ItemOwner)null, var4.getId());
   }

   public void updateForTopItem(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, @Nullable Level var4, @Nullable ItemOwner var5, int var6) {
      var1.clear();
      if (!var2.isEmpty()) {
         var1.displayContext = var3;
         this.appendItemLayers(var1, var2, var3, var4, var5, var6);
      }

   }

   public void appendItemLayers(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, @Nullable Level var4, @Nullable ItemOwner var5, int var6) {
      Identifier var7 = (Identifier)var2.get(DataComponents.ITEM_MODEL);
      if (var7 != null) {
         var1.setOversizedInGui(((ClientItem.Properties)this.clientProperties.apply(var7)).oversizedInGui());
         ItemModel var10000 = (ItemModel)this.modelGetter.apply(var7);
         ClientLevel var10005;
         if (var4 instanceof ClientLevel) {
            ClientLevel var8 = (ClientLevel)var4;
            var10005 = var8;
         } else {
            var10005 = null;
         }

         var10000.update(var1, var2, this, var3, var10005, var5, var6);
      }
   }

   public boolean shouldPlaySwapAnimation(ItemStack var1) {
      Identifier var2 = (Identifier)var1.get(DataComponents.ITEM_MODEL);
      return var2 == null ? true : ((ClientItem.Properties)this.clientProperties.apply(var2)).handAnimationOnSwap();
   }

   public float swapAnimationScale(ItemStack var1) {
      Identifier var2 = (Identifier)var1.get(DataComponents.ITEM_MODEL);
      return var2 == null ? 1.0F : ((ClientItem.Properties)this.clientProperties.apply(var2)).swapAnimationScale();
   }
}
