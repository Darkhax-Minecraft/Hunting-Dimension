package net.darkhax.huntingdim.handler;

import java.util.UUID;

import net.darkhax.bookshelf.data.AttributeOperation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DimensionEffectHandler {

    public static final AttributeModifier BUFF_ARMOR = new AttributeModifier(UUID.fromString("054ab076-0a2e-4ea4-b525-b201fcf4a2a7"), "buff_hunting_armor", ConfigurationHandler.buffArmor, AttributeOperation.ADDITIVE.ordinal());
    public static final AttributeModifier BUFF_HEALTH = new AttributeModifier(UUID.fromString("d52efdc2-53fc-42ab-80ed-9fe79d219ff7"), "buff_hunting_health", ConfigurationHandler.buffHealth, AttributeOperation.MULTIPLY.ordinal());
    public static final AttributeModifier BUFF_ATTACK = new AttributeModifier(UUID.fromString("d86f9cdc-8a12-492a-a0ad-e8cdded32ab7"), "buff_hunting_attack", ConfigurationHandler.buffAttack, AttributeOperation.MULTIPLY.ordinal());

    @SubscribeEvent
    public void onEntityJoinWorld (LivingUpdateEvent event) {

        if (event.getEntityLiving() instanceof IMob && event.getEntityLiving().dimension == ConfigurationHandler.dimensionId) {

            final EntityLivingBase entity = event.getEntityLiving();

            this.applyModifier(entity, BUFF_ARMOR, SharedMonsterAttributes.ARMOR);
            this.applyModifier(entity, BUFF_HEALTH, SharedMonsterAttributes.MAX_HEALTH);
            this.applyModifier(entity, BUFF_ATTACK, SharedMonsterAttributes.ATTACK_DAMAGE);
        }
    }

    private void applyModifier (EntityLivingBase entity, AttributeModifier modifier, IAttribute attribute) {

        if (modifier.getAmount() > 0) {

            final ModifiableAttributeInstance attrInst = (ModifiableAttributeInstance) entity.getEntityAttribute(attribute);

            if (attrInst != null && !attrInst.hasModifier(modifier)) {

                attrInst.applyModifier(modifier);

                if (attribute == SharedMonsterAttributes.MAX_HEALTH) {

                    entity.heal(entity.getMaxHealth());
                }
            }
        }
    }

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