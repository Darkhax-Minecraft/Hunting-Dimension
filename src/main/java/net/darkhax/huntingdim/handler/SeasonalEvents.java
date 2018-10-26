package net.darkhax.huntingdim.handler;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.events.EventLoader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SeasonalEvents {
    
    @SubscribeEvent
    public void onLivingSpawn(LivingSpawnEvent.SpecialSpawn event) {
        
        if (isValidWorld(event.getWorld()) && this.isValidMob(event.getEntityLiving()) && MathsUtils.tryPercentage(0.10)) {
            
            if (Constants.RANDOM.nextBoolean()) {
                
                final ItemStack head = EventLoader.currentEvent.getRandomWorn().copy();
                event.getEntityLiving().setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
            }
            
            else {
                
                final ItemStack hand = EventLoader.currentEvent.getRandomHeld().copy();
                event.getEntityLiving().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, hand);
            }
        }
    }
    
    private boolean isValidWorld(World world) {
        
        return WorldUtils.isDimension(world, HuntingDimension.dimensionType) && !world.isRemote;
    }
    
    private boolean isValidMob(EntityLivingBase entity) {
        
        return entity instanceof EntityZombie || entity instanceof EntitySkeleton;
    }
}
