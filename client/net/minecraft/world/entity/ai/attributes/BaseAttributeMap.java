package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.InsensitiveStringMap;

public abstract class BaseAttributeMap {
   protected final Map<Attribute, AttributeInstance> attributesByObject = Maps.newHashMap();
   protected final Map<String, AttributeInstance> attributesByName = new InsensitiveStringMap();
   protected final Multimap<Attribute, Attribute> descendantsByParent = HashMultimap.create();

   public BaseAttributeMap() {
      super();
   }

   @Nullable
   public AttributeInstance getInstance(Attribute var1) {
      return (AttributeInstance)this.attributesByObject.get(var1);
   }

   @Nullable
   public AttributeInstance getInstance(String var1) {
      return (AttributeInstance)this.attributesByName.get(var1);
   }

   public AttributeInstance registerAttribute(Attribute var1) {
      if (this.attributesByName.containsKey(var1.getName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         AttributeInstance var2 = this.createAttributeInstance(var1);
         this.attributesByName.put(var1.getName(), var2);
         this.attributesByObject.put(var1, var2);

         for(Attribute var3 = var1.getParentAttribute(); var3 != null; var3 = var3.getParentAttribute()) {
            this.descendantsByParent.put(var3, var1);
         }

         return var2;
      }
   }

   protected abstract AttributeInstance createAttributeInstance(Attribute var1);

   public Collection<AttributeInstance> getAttributes() {
      return this.attributesByName.values();
   }

   public void onAttributeModified(AttributeInstance var1) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> var1) {
      Iterator var2 = var1.entries().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         AttributeInstance var4 = this.getInstance((String)var3.getKey());
         if (var4 != null) {
            var4.removeModifier((AttributeModifier)var3.getValue());
         }
      }

   }

   public void addAttributeModifiers(Multimap<String, AttributeModifier> var1) {
      Iterator var2 = var1.entries().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         AttributeInstance var4 = this.getInstance((String)var3.getKey());
         if (var4 != null) {
            var4.removeModifier((AttributeModifier)var3.getValue());
            var4.addModifier((AttributeModifier)var3.getValue());
         }
      }

   }
}
