package com.tomkeuper.bedwars.api.shop;

import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;

public interface IQuickBuyElement {
    boolean isLoaded();
    int getSlot();
    ICategoryContent getCategoryContent();

}
