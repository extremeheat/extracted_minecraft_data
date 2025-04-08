package net.minecraft.client.data.models;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.Item;

public interface ItemModelOutput {
   void accept(Item var1, ItemModel.Unbaked var2);

   void copy(Item var1, Item var2);
}
