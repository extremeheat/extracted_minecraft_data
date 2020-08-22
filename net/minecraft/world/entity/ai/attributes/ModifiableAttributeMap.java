package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.InsensitiveStringMap;

public class ModifiableAttributeMap extends BaseAttributeMap {
   private final Set dirtyAttributes = Sets.newHashSet();
   protected final Map attributesByLegacy = new InsensitiveStringMap();

   public ModifiableAttributeInstance getInstance(Attribute var1) {
      return (ModifiableAttributeInstance)super.getInstance(var1);
   }

   public ModifiableAttributeInstance getInstance(String var1) {
      AttributeInstance var2 = super.getInstance(var1);
      if (var2 == null) {
         var2 = (AttributeInstance)this.attributesByLegacy.get(var1);
      }

      return (ModifiableAttributeInstance)var2;
   }

   public AttributeInstance registerAttribute(Attribute var1) {
      AttributeInstance var2 = super.registerAttribute(var1);
      if (var1 instanceof RangedAttribute && ((RangedAttribute)var1).getImportLegacyName() != null) {
         this.attributesByLegacy.put(((RangedAttribute)var1).getImportLegacyName(), var2);
      }

      return var2;
   }

   protected AttributeInstance createAttributeInstance(Attribute var1) {
      return new ModifiableAttributeInstance(this, var1);
   }

   public void onAttributeModified(AttributeInstance var1) {
      if (var1.getAttribute().isClientSyncable()) {
         this.dirtyAttributes.add(var1);
      }

      Iterator var2 = this.descendantsByParent.get(var1.getAttribute()).iterator();

      while(var2.hasNext()) {
         Attribute var3 = (Attribute)var2.next();
         ModifiableAttributeInstance var4 = this.getInstance(var3);
         if (var4 != null) {
            var4.setDirty();
         }
      }

   }

   public Set getDirtyAttributes() {
      return this.dirtyAttributes;
   }

   public Collection getSyncableAttributes() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.getAttributes().iterator();

      while(var2.hasNext()) {
         AttributeInstance var3 = (AttributeInstance)var2.next();
         if (var3.getAttribute().isClientSyncable()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   // $FF: synthetic method
   public AttributeInstance getInstance(String var1) {
      return this.getInstance(var1);
   }

   // $FF: synthetic method
   public AttributeInstance getInstance(Attribute var1) {
      return this.getInstance(var1);
   }
}
