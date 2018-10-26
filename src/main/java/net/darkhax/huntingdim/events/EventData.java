package net.darkhax.huntingdim.events;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class EventData {

    private final String event;
    private final ItemStack[] heldItems;
    private final ItemStack[] wornItems;

    public EventData (NBTTagCompound tag) {

        this.event = tag.getString("Event");
        this.heldItems = readItems(tag, "HeldItems");
        this.wornItems = readItems(tag, "WornItems");
    }

    public String getName () {
        
        return event;
    }
    
    public ItemStack[] getHeldItems() {
        
        return this.heldItems;
    }
    
    public ItemStack[] getWornItems() {
        
        return this.wornItems;
    }
    
    public ItemStack getRandomHeld() {
        
        return this.heldItems[Constants.RANDOM.nextInt(this.heldItems.length)];
    }
    
    public ItemStack getRandomWorn() {
        
        return this.wornItems[Constants.RANDOM.nextInt(this.wornItems.length)];
    }
    
    private ItemStack[] readItems(NBTTagCompound tag, String name) {
        
        final NBTTagList list = tag.getTagList(name, NBT.TAG_COMPOUND);
        final ItemStack[] items = new ItemStack[list.tagCount()];
        
        for (int i = 0; i < items.length; i++) {
            
            items[i] = new ItemStack(list.getCompoundTagAt(i));
            StackUtils.appendLore(items[i], this.event + " " + EventLoader.year);
        }
        
        return items;
    }
}
