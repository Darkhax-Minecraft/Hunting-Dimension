package net.darkhax.huntingdim.creativetab;

import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.events.EventLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CreativeTabHunting extends CreativeTabs {

    public CreativeTabHunting () {

        super("huntingdim");
    }

    @Override
    public ItemStack createIcon () {

        return new ItemStack(HuntingDimension.frame);
    }
    
    @Override
    public void displayAllRelevantItems (NonNullList<ItemStack> itemList) {
        
        super.displayAllRelevantItems(itemList);
        
        if (EventLoader.currentEvent != null) {
            
            for (ItemStack stack : EventLoader.currentEvent.getHeldItems()) {
                
                itemList.add(stack);
            }
            
            for (ItemStack stack : EventLoader.currentEvent.getWornItems()) {
                
                itemList.add(stack);
            }
        }
    }
}
