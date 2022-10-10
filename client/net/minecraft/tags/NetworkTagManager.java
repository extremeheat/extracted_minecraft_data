package net.minecraft.tags;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.registry.IRegistry;

public class NetworkTagManager implements IResourceManagerReloadListener {
   private final NetworkTagCollection<Block> field_199719_a;
   private final NetworkTagCollection<Item> field_199720_b;
   private final NetworkTagCollection<Fluid> field_205705_c;

   public NetworkTagManager() {
      super();
      this.field_199719_a = new NetworkTagCollection(IRegistry.field_212618_g, "tags/blocks", "block");
      this.field_199720_b = new NetworkTagCollection(IRegistry.field_212630_s, "tags/items", "item");
      this.field_205705_c = new NetworkTagCollection(IRegistry.field_212619_h, "tags/fluids", "fluid");
   }

   public NetworkTagCollection<Block> func_199717_a() {
      return this.field_199719_a;
   }

   public NetworkTagCollection<Item> func_199715_b() {
      return this.field_199720_b;
   }

   public NetworkTagCollection<Fluid> func_205704_c() {
      return this.field_205705_c;
   }

   public void func_199718_c() {
      this.field_199719_a.func_199917_b();
      this.field_199720_b.func_199917_b();
      this.field_205705_c.func_199917_b();
   }

   public void func_195410_a(IResourceManager var1) {
      this.func_199718_c();
      this.field_199719_a.func_199909_a(var1);
      this.field_199720_b.func_199909_a(var1);
      this.field_205705_c.func_199909_a(var1);
      BlockTags.func_199895_a(this.field_199719_a);
      ItemTags.func_199902_a(this.field_199720_b);
      FluidTags.func_206953_a(this.field_205705_c);
   }

   public void func_199716_a(PacketBuffer var1) {
      this.field_199719_a.func_200042_a(var1);
      this.field_199720_b.func_200042_a(var1);
      this.field_205705_c.func_200042_a(var1);
   }

   public static NetworkTagManager func_199714_b(PacketBuffer var0) {
      NetworkTagManager var1 = new NetworkTagManager();
      var1.func_199717_a().func_200043_b(var0);
      var1.func_199715_b().func_200043_b(var0);
      var1.func_205704_c().func_200043_b(var0);
      return var1;
   }
}
