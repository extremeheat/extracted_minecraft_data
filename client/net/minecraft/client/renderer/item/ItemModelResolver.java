package net.minecraft.client.renderer.item;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;

public class ItemModelResolver {
   private final Function<ResourceLocation, ItemModel> modelGetter;

   public ItemModelResolver(ModelManager var1) {
      super();
      Objects.requireNonNull(var1);
      this.modelGetter = var1::getItemModel;
   }

   public void updateForLiving(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, boolean var4, LivingEntity var5) {
      this.updateForTopItem(var1, var2, var3, var4, var5.level(), var5, var5.getId() + var3.ordinal());
   }

   public void updateForNonLiving(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, Entity var4) {
      this.updateForTopItem(var1, var2, var3, false, var4.level(), (LivingEntity)null, var4.getId());
   }

   public void updateForTopItem(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, boolean var4, @Nullable Level var5, @Nullable LivingEntity var6, int var7) {
      var1.clear();
      if (!var2.isEmpty()) {
         var1.displayContext = var3;
         var1.isLeftHand = var4;
         this.appendItemLayers(var1, var2, var3, var5, var6, var7);
      }

   }

   private static void fixupSkullProfile(ItemStack var0) {
      Item var2 = var0.getItem();
      if (var2 instanceof BlockItem var1) {
         if (var1.getBlock() instanceof AbstractSkullBlock) {
            ResolvableProfile var3 = (ResolvableProfile)var0.get(DataComponents.PROFILE);
            if (var3 != null && !var3.isResolved()) {
               var0.remove(DataComponents.PROFILE);
               var3.resolve().thenAcceptAsync((var1x) -> var0.set(DataComponents.PROFILE, var1x), Minecraft.getInstance());
            }
         }
      }

   }

   public void appendItemLayers(ItemStackRenderState var1, ItemStack var2, ItemDisplayContext var3, @Nullable Level var4, @Nullable LivingEntity var5, int var6) {
      fixupSkullProfile(var2);
      ResourceLocation var7 = (ResourceLocation)var2.get(DataComponents.ITEM_MODEL);
      if (var7 != null) {
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
}
