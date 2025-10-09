package net.minecraft.client.data.models;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.Item;

public interface ItemModelOutput {
   default void accept(Item var1, ItemModel.Unbaked var2) {
      this.accept(var1, var2, ClientItem.Properties.DEFAULT);
   }

   void accept(Item var1, ItemModel.Unbaked var2, ClientItem.Properties var3);

   void copy(Item var1, Item var2);
}
