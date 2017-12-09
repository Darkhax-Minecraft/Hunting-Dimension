package net.darkhax.huntingdim.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DimensionEffectHandler {

    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {

        // If entity is a player, and in the hunting dimension, and quick potion wear off is
        // enabled
        if (event.getEntityLiving() instanceof EntityPlayer && ConfigurationHandler.quickPotionWearOff && event.getEntityLiving().dimension == ConfigurationHandler.dimensionId) {

            for (final PotionEffect effect : event.getEntityLiving().getActivePotionEffects()) {

                // If the effect is positive
                if (effect.getPotion().isBeneficial()) {

                    // Lower the duration by a second tick.
                    effect.deincrementDuration();
                }
            }
        }
    }
}