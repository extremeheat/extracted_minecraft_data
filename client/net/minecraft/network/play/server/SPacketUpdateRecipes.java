package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketUpdateRecipes implements Packet<INetHandlerPlayClient> {
   private List<IRecipe> field_199617_a;

   public SPacketUpdateRecipes() {
      super();
   }

   public SPacketUpdateRecipes(Collection<IRecipe> var1) {
      super();
      this.field_199617_a = Lists.newArrayList(var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_199525_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_199617_a = Lists.newArrayList();
      int var2 = var1.func_150792_a();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_199617_a.add(RecipeSerializers.func_199571_a(var1));
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_199617_a.size());
      Iterator var2 = this.field_199617_a.iterator();

      while(var2.hasNext()) {
         IRecipe var3 = (IRecipe)var2.next();
         RecipeSerializers.func_199574_a(var3, var1);
      }

   }

   public List<IRecipe> func_199616_a() {
      return this.field_199617_a;
   }
}
