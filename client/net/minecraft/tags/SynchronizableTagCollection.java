package net.minecraft.tags;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SynchronizableTagCollection<T> extends TagCollection<T> {
   private final Registry<T> registry;

   public SynchronizableTagCollection(Registry<T> var1, String var2, String var3) {
      super(var1::getOptional, var2, false, var3);
      this.registry = var1;
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      Map var2 = this.getAllTags();
      var1.writeVarInt(var2.size());
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var1.writeResourceLocation((ResourceLocation)var4.getKey());
         var1.writeVarInt(((Tag)var4.getValue()).getValues().size());
         Iterator var5 = ((Tag)var4.getValue()).getValues().iterator();

         while(var5.hasNext()) {
            Object var6 = var5.next();
            var1.writeVarInt(this.registry.getId(var6));
         }
      }

   }

   public void loadFromNetwork(FriendlyByteBuf var1) {
      HashMap var2 = Maps.newHashMap();
      int var3 = var1.readVarInt();

      for(int var4 = 0; var4 < var3; ++var4) {
         ResourceLocation var5 = var1.readResourceLocation();
         int var6 = var1.readVarInt();
         Tag.Builder var7 = Tag.Builder.tag();

         for(int var8 = 0; var8 < var6; ++var8) {
            var7.add(this.registry.byId(var1.readVarInt()));
         }

         var2.put(var5, var7.build(var5));
      }

      this.replace(var2);
   }
}
