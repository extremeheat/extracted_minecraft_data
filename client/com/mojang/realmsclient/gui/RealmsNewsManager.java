package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.util.RealmsPersistence;

public class RealmsNewsManager {
   private final RealmsPersistence newsLocalStorage;
   private boolean hasUnreadNews;
   private String newsLink;

   public RealmsNewsManager(RealmsPersistence var1) {
      super();
      this.newsLocalStorage = var1;
      RealmsPersistence.RealmsPersistenceData var2 = var1.read();
      this.hasUnreadNews = var2.hasUnreadNews;
      this.newsLink = var2.newsLink;
   }

   public boolean hasUnreadNews() {
      return this.hasUnreadNews;
   }

   public String newsLink() {
      return this.newsLink;
   }

   public void updateUnreadNews(RealmsNews var1) {
      RealmsPersistence.RealmsPersistenceData var2 = this.updateNewsStorage(var1);
      this.hasUnreadNews = var2.hasUnreadNews;
      this.newsLink = var2.newsLink;
   }

   private RealmsPersistence.RealmsPersistenceData updateNewsStorage(RealmsNews var1) {
      RealmsPersistence.RealmsPersistenceData var2 = this.newsLocalStorage.read();
      if (var1.newsLink != null && !var1.newsLink.equals(var2.newsLink)) {
         RealmsPersistence.RealmsPersistenceData var3 = new RealmsPersistence.RealmsPersistenceData();
         var3.newsLink = var1.newsLink;
         var3.hasUnreadNews = true;
         this.newsLocalStorage.save(var3);
         return var3;
      } else {
         return var2;
      }
   }
}
